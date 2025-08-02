/*
 * Copyright 2014 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hibernate.bugs;

import java.util.Properties;

import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.bugs.entities.SomeEntity;
import org.hibernate.bugs.transaction.MyJtaTransactionManager;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.engine.transaction.jta.platform.internal.AbstractJtaPlatform;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Test;

/**
 * This template demonstrates how to develop a test case for Hibernate ORM, using its built-in unit test framework.
 * Although ORMStandaloneTestCase is perfectly acceptable as a reproducer, usage of this class is much preferred.
 * Since we nearly always include a regression test with bug fixes, providing your reproducer using this method
 * simplifies the process.
 *
 * What's even better?  Fork hibernate-orm itself, add your test case directly to a module's unit tests, then
 * submit it as a PR!
 */
public class ORMUnitTestCase extends BaseCoreFunctionalTestCase {

	private MyJtaTransactionManager transactionManager;

    // Add your entities here.
	@Override
	protected Class[] getAnnotatedClasses() {
		return new Class[] {
				SomeEntity.class,
		};
	}

	// If you use *.hbm.xml mappings, instead of annotations, add the mappings here.
	@Override
	protected String[] getMappings() {
		return new String[] {
//				"Foo.hbm.xml",
//				"Bar.hbm.xml"
		};
	}
	// If those mappings reside somewhere other than resources/org/hibernate/test, change this.
	@Override
	protected String getBaseForMappings() {
		return "org/hibernate/test/";
	}

	// Add in any settings that are specific to your test.  See resources/hibernate.properties for the defaults.
	@Override
	protected void configure(Configuration configuration) {
		super.configure( configuration );

		configuration.setProperty( AvailableSettings.SHOW_SQL, Boolean.TRUE.toString() );
		configuration.setProperty( AvailableSettings.FORMAT_SQL, Boolean.TRUE.toString() );

		transactionManager = new MyJtaTransactionManager();
		Properties properties = new Properties();
		properties.put(AvailableSettings.JTA_PLATFORM, new MyJtaPlatform(transactionManager));
		properties.put(AvailableSettings.TRANSACTION_COORDINATOR_STRATEGY, "jta");
		properties.put(AvailableSettings.FLUSH_BEFORE_COMPLETION, Boolean.FALSE);
		configuration.addProperties(properties);
	}

	// Add your tests, using standard JUnit.
	@Test
	public void hhh123Test() throws Exception {
	    transactionManager.begin();

		// BaseCoreFunctionalTestCase automatically creates the SessionFactory and provides the Session.
		Session s = openSession();
		Transaction tx = s.beginTransaction();
		session.saveOrUpdate(new SomeEntity("ABC"));
		tx.commit();
		s.close();

		transactionManager.commit();
	}

	public static class MyJtaPlatform extends AbstractJtaPlatform {

        TransactionManager transactionManager;

        public MyJtaPlatform(TransactionManager transactionManager) {
            this.transactionManager = transactionManager;
        }

        @Override
        protected TransactionManager locateTransactionManager() {
            return transactionManager;
        }

        @Override
        protected UserTransaction locateUserTransaction() {
            if (transactionManager instanceof UserTransaction) {
                return (UserTransaction) transactionManager;
            }
            throw new UnsupportedOperationException("User transactions are not supported");
        }
    }
}
