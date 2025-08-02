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
package org.hibernate.test.hhh10708_bytecodeissue;

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
public class TestCase extends BaseCoreFunctionalTestCase {

	private static final Logger LOGGER = Logger.getLogger(TestCase.class);

	// Add your entities here.
	@Override
	protected Class[] getAnnotatedClasses() {
		return new Class[] {
				Foo.class,
				Bar.class
		};
	}

	// Add in any settings that are specific to your test. See resources/hibernate.properties for the defaults.
	@Override
	protected void configure(Configuration configuration) {
		super.configure(configuration);

		configuration.setProperty(AvailableSettings.SHOW_SQL, Boolean.FALSE.toString());
		configuration.setProperty(AvailableSettings.USE_SQL_COMMENTS, Boolean.TRUE.toString());

	}

	// Add your tests, using standard JUnit.
	@Test
	public void hhh10708Test() throws Exception {
		// BaseCoreFunctionalTestCase automatically creates the SessionFactory and provides the Session.
		Session s = openSession();
		Transaction tx = s.beginTransaction();
		Bar bar1 = new Bar();
		Bar bar2 = new Bar();
		Foo foo = new Foo();
		s.save(bar1);
		s.save(bar2);
		s.save(foo);
		bar1.setFoo(foo);
		bar2.setFoo(foo);
		tx.commit();
		s.close();
		s = openSession();
		tx = s.beginTransaction();
		foo = s.get(Foo.class, foo.getId());
		LOGGER.error("bar = " + foo.getBar().getClass());
		s.flush();
		tx.commit();
		s.close();
	}
}
