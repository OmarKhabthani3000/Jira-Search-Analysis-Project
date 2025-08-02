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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Test;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

/**
 * @author Peter Gazdik
 */
public class HHH_PolymorphicPaginationTestCase extends BaseCoreFunctionalTestCase {

	@Override
	protected Class<?>[] getAnnotatedClasses() {
		return new Class[] { //
				Left.class, //
				Right.class //
		};
	}

	@Test
	public void splitQueryWithPagination() throws Exception {
		Session s = openSession();

		// PREPARE DATA
		{
			Transaction tx = s.beginTransaction();
			persist(new Left(), "left1", s);
			persist(new Left(), "left2", s);

			persist(new Right(), "right1", s);
			persist(new Right(), "right2", s);
			tx.commit();
		}

		// TEST QURYING

		String slectAllEntitiesHql = "select e from " + Superclass.class.getName() + " e";

		{
			// THIS WORKS
			// No pagination - > All 4 entities are returned

			Transaction tx = s.beginTransaction();
			List<Superclass> list = s.createSelectionQuery(slectAllEntitiesHql, Superclass.class) //
					.list();

			assertEquals(4, list.size(), "Wrong number of entities returned!");
			System.out.println(list);
			tx.commit();
		}

		{
			// THIS FAILS
			// With pagination -> 1 should be skipped, 3 should be returned

			Transaction tx = s.beginTransaction();
			List<Superclass> list = s.createSelectionQuery(slectAllEntitiesHql, Superclass.class) //
					.setMaxResults(10) //
					.setFirstResult(1) //
					.list();

			assertEquals(3, list.size(), "Wrong number of entities returned!");
			tx.commit();
		}

		s.close();
	}

	private void persist(Superclass entity, String name, Session s) {
		entity.name = name;
		s.persist(entity);
	}

	@MappedSuperclass
	public static abstract class Superclass {
		@Id
		@GeneratedValue
		public Long id;

		public String name;
	}

	@Entity
	public static class Left extends Superclass {
		// nothing to add
	}

	@Entity
	public static class Right extends Superclass {
		// nothing to add
	}
}
