package org.hibernate.bugs;

import jakarta.persistence.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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
		entityManagerFactory.close(7);
	}

	@Test
	public void hhh123Test() throws Exception {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();

		entityManager.createQuery("select c from JPAUnitTestCase$Car c where c.field = 'Some'").getFirstResult();

		entityManager.getTransaction().commit();
		entityManager.close();
	}


	@MappedSuperclass
	public static abstract class Vehicle<T> {

		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		private Long id;

		private T field;


		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public T getField() {
			return field;
		}

		public void setField(T field) {
			this.field = field;
		}

	}

	@Entity
	public static class Car extends Vehicle<String> {
	}

}

