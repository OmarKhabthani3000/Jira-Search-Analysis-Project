package org.hibernate.bugs;

import jakarta.persistence.*;

import java.util.LinkedList;
import java.util.List;
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
		entityManagerFactory.close();
	}

	@Test
	public void hhh123Test() throws Exception {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();

		entityManager.createQuery("select c from JPAUnitTestCase$ChildA c where c.parent.id = 1").getFirstResult();

		entityManager.getTransaction().commit();
		entityManager.close();
	}


	@MappedSuperclass
	public static abstract class Child<P extends Parent<?>> {

		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		private Long id;

		@ManyToOne(optional = false)
		private P parent;


		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public P getParent() {
			return parent;
		}

		public void setParent(P parent) {
			this.parent = parent;
		}

	}

	@MappedSuperclass
	public static abstract class Parent<C extends Child<?>> {

		@OneToMany(mappedBy = "parent")
		private List<C> children = new LinkedList<>();


		public abstract Long getId();

		public abstract void setId(Long id);

		public List<C> getChildren() {
			return children;
		}

		public void setChildren(List<C> children) {
			this.children = children;
		}

	}

	@Entity
	public class ChildA extends Child<ParentA> {
	}

	@Entity
	public static class ChildB extends Child<ParentB> {
	}

	@Entity
	public static class ParentA extends Parent<ChildA> {

		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		private Long id;


		@Override
		public Long getId() {
			return id;
		}

		@Override
		public void setId(Long id) {
			this.id = id;
		}
	}

	@Entity
	public static class ParentB extends Parent<ChildB> {

		@Id
		private Long id;


		@Override
		public Long getId() {
			return id;
		}

		@Override
		public void setId(Long id) {
			this.id = id;
		}
	}

}

