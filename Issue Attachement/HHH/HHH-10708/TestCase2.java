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
package org.hibernate.test.hhh10708_bytecodeissue_wo_orphanremoval;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Test;

/**
 * This template demonstrates how to develop a test case for Hibernate ORM, using its built-in unit test framework. Although
 * ORMStandaloneTestCase is perfectly acceptable as a reproducer, usage of this class is much preferred. Since we nearly always include a
 * regression test with bug fixes, providing your reproducer using this method simplifies the process.
 *
 * What's even better? Fork hibernate-orm itself, add your test case directly to a module's unit tests, then submit it as a PR!
 */
public class TestCase2 extends BaseCoreFunctionalTestCase {

	private static final Logger LOGGER = Logger.getLogger(TestCase2.class);

	// Add your entities here.
	@Override
	protected Class[] getAnnotatedClasses() {
		return new Class[] {
				Foo2.class,
				Bar2.class
		};
	}

	// Add in any settings that are specific to your test. See resources/hibernate.properties for the defaults.
	@Override
	protected void configure(Configuration configuration) {
		super.configure(configuration);

		configuration.setProperty(AvailableSettings.FORMAT_SQL, Boolean.FALSE.toString());
		configuration.setProperty(AvailableSettings.SHOW_SQL, Boolean.TRUE.toString());
		configuration.setProperty(AvailableSettings.USE_SQL_COMMENTS, Boolean.TRUE.toString());

	}

	@Test
	/**
	 * https://hibernate.atlassian.net/browse/HHH-10708
	 * 
	 * @throws Exception
	 */
	public void hhh10708Test() throws Exception {
		Session s = openSession();
		Transaction tx = s.beginTransaction();
		Bar2 bar = new Bar2();

		Foo2 foo1 = new Foo2();
		Foo2 foo2 = new Foo2();
		s.save(bar);
		s.save(foo1);
		s.save(foo2);
		bar.getFoos().add(foo1);
		bar.getFoos().add(foo2);
		tx.commit();
		s.close();
		s = openSession();
		tx = s.beginTransaction();

		s.refresh(bar);
		LOGGER.error("collection = " + bar.getFoos());
		s.flush();
		LOGGER.error("DONE!");
		tx.commit();
		s.close();
	}
}
