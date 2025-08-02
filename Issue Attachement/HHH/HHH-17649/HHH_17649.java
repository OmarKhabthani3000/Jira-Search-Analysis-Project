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

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.orm.junit.DomainModel;
import org.hibernate.testing.orm.junit.JiraKey;
import org.hibernate.testing.orm.junit.SessionFactory;
import org.hibernate.testing.orm.junit.SessionFactoryScope;
import org.junit.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import static org.hibernate.cfg.AvailableSettings.*;
import static org.junit.Assert.assertEquals;

/**
 * @author Christoph Aigensberger
 */
@SessionFactory
@DomainModel(annotatedClasses = {
		HHH_17649.TestEntity.class
})
@JiraKey("HHH-17649")
public class HHH_17649 {
	@BeforeAll
	public void setUp(SessionFactoryScope scope) {
		scope.inTransaction( session -> {
			session.persist(createEntity(1, 10, "A", "TEST1234"));
			session.persist(createEntity(2, 10, "A", "TEST1234"));
			session.persist(createEntity(3, 10, "B", "TEST1234"));
			session.persist(createEntity(4, 11, "C", "TEST1234"));
		} );
	}

	@AfterAll
	public void tearDown(SessionFactoryScope scope) {
		scope.inTransaction( session -> {
			session.createMutationQuery( "delete from TestEntity " ).executeUpdate();
		} );
	}


	@Test
	public void testSingleColumnDistinctSelect(SessionFactoryScope scope) {
		scope.inTransaction((session) -> {
			final var resultList = session.createQuery("select distinct t.attributeA, t.attributeB, t.attributeC from TestEntity t where t.attributeA = 10", Object[].class).setMaxResults(4)
					.setFirstResult(0).getResultList();

			assertEquals(2, resultList.size()); //breaks when using oracle Dialect, returns 3
		});
	}
	@Test
	public void testSingleColumnGroupBySelect(SessionFactoryScope scope) {
		scope.inTransaction((session) -> {
			final var resultList = session.createQuery("select distinct t.attributeA, t.attributeB, t.attributeC from TestEntity t where t.attributeA = 10 group by t.attributeA, t.attributeB, t.attributeC", Object[].class).setMaxResults(4)
					.setFirstResult(0).getResultList();

			assertEquals(2, resultList.size()); //breaks when using oracle Dialect, leads to broken SQL
		});
	}


	private static TestEntity createEntity(long id, long attributeA, String attributeB, String attributeC) {
		final var childA = new TestEntity();
		childA.setId(id);
		childA.setAttributeA(attributeA);
		childA.setAttributeB(attributeB);
		childA.setAttributeC(attributeC);
		return childA;
	}

	@Entity(name = "TestEntity")
	public static class TestEntity {

		@Id
		private Long id;
		private Long attributeA;
		private String attributeB;
		private String attributeC;

		public Long getAttributeA() {
			return attributeA;
		}

		public void setAttributeA(Long attributeA) {
			this.attributeA = attributeA;
		}

		public String getAttributeB() {
			return attributeB;
		}

		public void setAttributeB(String attributeB) {
			this.attributeB = attributeB;
		}

		public String getAttributeC() {
			return attributeC;
		}

		public void setAttributeC(String attributeC) {
			this.attributeC = attributeC;
		}

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}
	}
}

