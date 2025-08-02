/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * Copyright (c) 2011, Red Hat Inc. or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Inc.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.hibernate.test.annotations;

import static org.junit.Assert.assertTrue;

import java.util.Properties;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;

import org.hibernate.Session;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.context.internal.JTASessionContext;
import org.hibernate.engine.transaction.internal.jta.JtaTransactionFactory;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.test.annotations.Company;
import org.hibernate.test.annotations.Flight;
import org.hibernate.test.annotations.Sky;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.jta.TestingJtaBootstrap;
import org.hibernate.testing.jta.TestingJtaBootstrap.JtaAwareDataSourceConnectionProvider;
import org.hibernate.testing.jta.TestingJtaBootstrap.TestingJtaPlatformImpl;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Emmanuel Bernard
 */
public class UnqiueConstraintVoilationJtaTest extends BaseCoreFunctionalTestCase {
	
	
	@Test
	@TestForIssue(jiraKey="HHH-7215")
	public void testUniqueConstraintException() throws NotSupportedException, SystemException, SecurityException, IllegalStateException, RollbackException, HeuristicMixedException, HeuristicRollbackException{
		int id = 5;
		
		Sky sky = new Sky();
		sky.id = new Long( id++ );
		sky.color = "green";
		sky.day = "monday";
		sky.month = "March";

		Sky otherSky = new Sky();
		otherSky.id = new Long( id++ );
		otherSky.color = "red";
		otherSky.day = "monday";
		otherSky.month = "March";
		
		TestingJtaBootstrap.INSTANCE.getTransactionManager().begin();
		Session s = sessionFactory().getCurrentSession();
		s.save(sky);
		s.save(otherSky);
		try{
		   TestingJtaBootstrap.INSTANCE.getTransactionManager().commit();
		}catch(RollbackException e){
			Throwable cause=e.getCause();
			assertTrue("Wrong cause for transaction roll back. Cause is "+cause.getClass().getName(),cause instanceof ConstraintViolationException);
		}
		
	}
	
		
	@Override
	protected void configure(Configuration configuration) {
		Properties properties=configuration.getProperties();
		//Additional properties for JTA
		properties.put( AvailableSettings.JTA_PLATFORM, TestingJtaPlatformImpl.INSTANCE);
		properties.put( Environment.CONNECTION_PROVIDER, JtaAwareDataSourceConnectionProvider.class.getName() );
		properties.put( Environment.DATASOURCE, TestingJtaBootstrap.INSTANCE.getDataSource());
		properties.put(Environment.CURRENT_SESSION_CONTEXT_CLASS,JTASessionContext.class.getName());
		properties.put( "javax.persistence.transactionType", "JTA" );
		properties.put(Environment.TRANSACTION_STRATEGY, JtaTransactionFactory.class.getName());
	}

	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected Class[] getAnnotatedClasses() {
		return new Class[]{
				Flight.class,
				Company.class,
				Sky.class
		};
	}

	// tests are leaving data around, so drop/recreate schema for now.  this is wha the old tests did

	@Override
	protected boolean createSchema() {
		return false;
	}

	@Before
	public void runCreateSchema() throws NotSupportedException, SystemException, SecurityException, IllegalStateException, RollbackException, HeuristicMixedException, HeuristicRollbackException {
		TestingJtaBootstrap.INSTANCE.getTransactionManager().begin();
		schemaExport().create( false, true );
		TestingJtaBootstrap.INSTANCE.getTransactionManager().commit();
	}

	private SchemaExport schemaExport() {
		return new SchemaExport( serviceRegistry(), configuration() );
	}

	@After
	public void runDropSchema() throws SecurityException, IllegalStateException, RollbackException, HeuristicMixedException, HeuristicRollbackException, SystemException, NotSupportedException {
		TestingJtaBootstrap.INSTANCE.getTransactionManager().begin();
		schemaExport().drop( false, true );
		TestingJtaBootstrap.INSTANCE.getTransactionManager().commit();
	}

}

