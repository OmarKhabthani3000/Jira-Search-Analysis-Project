package org.hibernate.test.session;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.hibernate.ConnectionReleaseMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.service.ServiceRegistryBuilder;
import org.hibernate.service.internal.StandardServiceRegistryImpl;
import org.hibernate.test.annotations.Country;
import org.hibernate.testing.FailureExpected;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.env.ConnectionProviderBuilder;
import org.hibernate.testing.junit4.BaseUnitTestCase;

/**
 * @author Lukasz Antoniak (lukasz dot antoniak at gmail dot com)
 */
public class TemporarySessionTest extends BaseUnitTestCase {
	private StandardServiceRegistryImpl serviceRegistry = null;

	@Before
	public void setUp() {
		serviceRegistry = (StandardServiceRegistryImpl) new ServiceRegistryBuilder()
				.applySettings( ConnectionProviderBuilder.getConnectionProviderProperties() )
				.buildServiceRegistry();
	}

	@After
	public void tearDown() {
		serviceRegistry.destroy();
	}

	@Test
	@TestForIssue( jiraKey = "HHH-7090" )
	@FailureExpected( jiraKey = "HHH-7090" )
	public void testSharedTransactionContextSessionClosing() {
		SessionFactory sessionFactory = buildSessionFactory( Country.class );

		Session session = sessionFactory.openSession();
		session.getTransaction().begin();

		Session temporary = session.sessionWithOptions().transactionContext()
				.connectionReleaseMode( ConnectionReleaseMode.AFTER_TRANSACTION )
				.openSession();
		Country country = new Country();
		country.setName( "Poland" );
		temporary.save( country );
		Assert.assertTrue( temporary.isOpen() );
		Assert.assertTrue( session.isOpen() );
		temporary.close();

		session.getTransaction().commit();
		session.close();
		Assert.assertFalse( temporary.isOpen() );
		Assert.assertFalse( session.isOpen() );

		sessionFactory.close();
	}

	@Test
	@TestForIssue( jiraKey = "HHH-7090" )
	@FailureExpected( jiraKey = "HHH-7090" )
	public void testSharedTransactionContextAutoClosing() {
		SessionFactory sessionFactory = buildSessionFactory( Country.class );

		Session session = sessionFactory.openSession();
		session.getTransaction().begin();

		Session temporary = session.sessionWithOptions().transactionContext().autoClose(true)
				.connectionReleaseMode( ConnectionReleaseMode.AFTER_TRANSACTION )
				.openSession();
		Country country = new Country();
		country.setName( "Poland" );
		temporary.save( country );
		Assert.assertTrue( temporary.isOpen() );
		Assert.assertTrue( session.isOpen() );

		session.getTransaction().commit();
		Assert.assertFalse( temporary.isOpen() );
		session.close();
		Assert.assertFalse( temporary.isOpen() );
		Assert.assertFalse( session.isOpen() );

		sessionFactory.close();
	}

	private SessionFactory buildSessionFactory(Class ... entities) {
		Configuration config = new Configuration();
		config.setProperty( Environment.HBM2DDL_AUTO, "create-drop" );
		for (Class entity : entities) {
			config.addAnnotatedClass( entity );
		}
		return config.buildSessionFactory( serviceRegistry );
	}
}
