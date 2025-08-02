package org.hibernate.bugs;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManager;
/**
 * This template demonstrates how to develop a standalone test case for Hibernate ORM.  Although this is perfectly
 * acceptable as a reproducer, usage of ORMUnitTestCase is preferred!
 */
public class ORMStandaloneTestCase {

	private SessionFactory sf;

	@Before
	public void setup() {
		StandardServiceRegistryBuilder srb = new StandardServiceRegistryBuilder()
			// Add in any settings that are specific to your test. See resources/hibernate.properties for the defaults.
			.applySetting( "hibernate.show_sql", "true" )
			.applySetting( "hibernate.format_sql", "true" )
			.applySetting( "hibernate.hbm2ddl.auto", "update" );

		Metadata metadata = new MetadataSources( srb.build() )
		// Add your entities here.
			.addAnnotatedClass( SchedulingSubpart.class )
			.buildMetadata();

		sf = metadata.buildSessionFactory();
		setupFixture();
	}

	// Add your tests, using standard JUnit.

	@Test
	public void hhh123Test() throws Exception {
		final EntityManager entityManager = sf.createEntityManager();

		entityManager.getTransaction().begin();

		// Select both a subpart and its parent
		List<Object[]> both = entityManager.createQuery("select s, p from SchedulingSubpart s inner join s.parentSubpart p", Object[].class).getResultList();
		Assert.assertEquals(1, both.size()); // one record returned
		Assert.assertEquals(2, both.get(0).length); // two objects returned
		Assert.assertEquals(2L, (long)((SchedulingSubpart)both.get(0)[0]).getId()); // first one is the second subpart (id = 2)
		Assert.assertEquals(1L, (long)((SchedulingSubpart)both.get(0)[1]).getId()); // second one is the parent (id = 1)

		// Same query, but only returning IDs
		List<Object[]> ids = entityManager.createQuery("select s.id, p.id from SchedulingSubpart s inner join s.parentSubpart p", Object[].class).getResultList();
		Assert.assertEquals(1, ids.size()); // one record returned
		Assert.assertEquals(2, both.get(0).length); // two objects returned
		Assert.assertEquals(2L, ids.get(0)[0]); // first one is the second subpart (id = 2)
		Assert.assertEquals(1L, ids.get(0)[1]); // second one is the parent (id = 1)

		entityManager.getTransaction().commit();
		entityManager.close();
	}
	
	private void setupFixture() {
		final EntityManager entityManager = sf.createEntityManager();

		entityManager.getTransaction().begin();

		// saving fixture
		{
			// First subpart has no parent
			SchedulingSubpart s1 = new SchedulingSubpart();
			s1.setId(1L);
			s1.setParentSubpart(null);

			// Second subpart has the first one as parent
			SchedulingSubpart s2 = new SchedulingSubpart();
			s2.setId(2L);
			s2.setParentSubpart(s1);
			entityManager.persist(s1);
			entityManager.persist(s2);
		}

		entityManager.getTransaction().commit();
		entityManager.close();
	}
}
