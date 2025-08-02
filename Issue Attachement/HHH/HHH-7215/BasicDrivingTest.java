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
package org.hibernate.test.transaction.jta;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.cfg.Environment;
import org.hibernate.engine.transaction.internal.TransactionCoordinatorImpl;
import org.hibernate.engine.transaction.internal.jta.JtaTransactionFactory;
import org.hibernate.engine.transaction.spi.TransactionContext;
import org.hibernate.service.ServiceRegistryBuilder;
import org.hibernate.service.internal.StandardServiceRegistryImpl;
import org.hibernate.test.common.TransactionContextImpl;
import org.hibernate.test.common.TransactionEnvironmentImpl;
import org.hibernate.testing.env.ConnectionProviderBuilder;
import org.hibernate.testing.jta.TestingJtaBootstrap;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Testing transaction handling when the JTA transaction facade is the driver.
 *
 * @author Steve Ebersole
 */
public class BasicDrivingTest extends BaseUnitTestCase {
	private StandardServiceRegistryImpl serviceRegistry;

	@Before
	@SuppressWarnings( {"unchecked"})
	public void setUp() throws Exception {
		Map configValues = new HashMap();
		configValues.putAll( ConnectionProviderBuilder.getConnectionProviderProperties() );
		configValues.put( Environment.TRANSACTION_STRATEGY, JtaTransactionFactory.class.getName() );
		TestingJtaBootstrap.prepare( configValues );
		serviceRegistry = (StandardServiceRegistryImpl) new ServiceRegistryBuilder()
				.applySettings( configValues )
				.buildServiceRegistry();
	}

	@After
	public void tearDown() throws Exception {
		serviceRegistry.destroy();
	}

	@Test
	public void testBasicUsage() throws Throwable {
		final TransactionContext transactionContext = new TransactionContextImpl( new TransactionEnvironmentImpl( serviceRegistry ) );

		TransactionCoordinatorImpl transactionCoordinator = new TransactionCoordinatorImpl( null, transactionContext );
		transactionCoordinator.getTransactionContext().getTransactionEnvironment().getJtaPlatform().retrieveTransactionManager().begin();
		//This will throw null pointer
		transactionCoordinator.setRollbackOnly();
		
	}

}
