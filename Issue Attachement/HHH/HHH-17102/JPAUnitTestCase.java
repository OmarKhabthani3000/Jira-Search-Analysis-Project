package org.hibernate.bugs;

import jakarta.persistence.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * This template demonstrates how to develop a test case for Hibernate ORM, using the Java Persistence API.
 */
public class JPAUnitTestCase {

	private EntityManagerFactory entityManagerFactory;

	@Before
	public void init() {
		entityManagerFactory = Persistence.createEntityManagerFactory( "templatePU" );
	}

	@After
	public void destroy() {
		entityManagerFactory.close();
	}

	// Entities are auto-discovered, so just add them anywhere on class-path
	// Add your tests, using standard JUnit.
	@Test
	public void hhh123Test() throws Exception {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();

		final List<Child> children =
				entityManager.createNamedQuery("Child", Child.class).getResultList();

		entityManager.getTransaction().commit();
		entityManager.close();
	}


	/**
	 * Parent entity.
	 */
	@Entity
	@Table(name = "parent")
	@Inheritance(strategy = InheritanceType.JOINED)
	public class Parent {

		@Id
		@Column(name = "id")
		private Long id;

	}


	/**
	 * Child entity.
	 */
	@SqlResultSetMapping(
			name = "ChildResult",
			entities = {
					@EntityResult(
							entityClass = Child.class,
							fields = {
									@FieldResult(name = "id", column = "id"),
									@FieldResult(name = "test", column = "test")
							}
					)
			}
	)
	@NamedNativeQuery(
			name = "Child",
			query = "SELECT id, test FROM child",
			resultSetMapping = "ChildResult"
	)
	@Entity
	@Table(name = "child")
	public class Child extends Parent {


		@Column(name = "test")
		private int test;

	}
}
