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
package org.hibernate.test;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
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
public class HHH1250ORMUnitTest extends BaseCoreFunctionalTestCase {

	// Add your entities here.
	@Override
	protected Class[] getAnnotatedClasses() {
		return new Class[] {
				HHH1250Parent.class,
				HHH1250Child.class
		};
	}

	// If you use *.hbm.xml mappings, instead of annotations, add the mappings here.
	@Override
	protected String[] getMappings() {
		return new String[] {};
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
		//configuration.setProperty( AvailableSettings.GENERATE_STATISTICS, "true" );
	}

	// Add your tests, using standard JUnit.
	@Test
	public void hhh1250Test() throws Exception {
		// BaseCoreFunctionalTestCase automatically creates the SessionFactory and provides the Session.
		Session s = openSession();
		Transaction tx = s.beginTransaction();

		HHH1250Child child1 = new HHH1250Child();
		HHH1250Child child2 = new HHH1250Child();
		
		HHH1250Parent parent = new HHH1250Parent();

		child1.setParent(parent);
		parent.getChildren().put(1, child1);
		parent.getChildren().put(2, child1);
		
		Serializable parentId = s.save(parent);
		s.save(child1);
		s.save(child2);
		
		s.flush();
		s.clear();

		HHH1250Parent reloaded = s.get(HHH1250Parent.class, parentId);
		
		//This will generate SetProxy-Instances as getChildren() will 
		//return an AbstractPersistentCollection
		Set<Integer> set1 = reloaded.getChildren().keySet();
		Set<Integer> set2 = reloaded.getChildren().keySet();
		
		Assert.assertEquals(set1, set2);
		Assert.assertEquals(set2, set1);
		
		//Following the contract of java.util.Set, these should be just as true:
		Set<Integer> set3 = new HashSet<>();
		set3.add(1);
		set3.add(2);
		Assert.assertEquals(set1, set3);
		Assert.assertEquals(set3, set1);

		tx.rollback();
		//tx.commit();
		s.close();
	}
}
