package org.hibernate.bugs;

import javax.persistence.*;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
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
		Parent parent = new Parent();
		parent.parentId = 1L;
		parent.children.add(new Child());
		entityManager.persist(parent);
		entityManager.flush();
		entityManager.clear();
		Parent foundParent = entityManager.find(Parent.class, 1L);
		Assert.assertEquals(1, foundParent.children.size());
		entityManager.getTransaction().commit();
		entityManager.close();
	}

	@Entity
	@Table(name = "Parent")
	public static class Parent {
		private long parentId;

		@Id
		public long getParentId() {
			return parentId;
		}

		public void setParentId(long parentId) {
			this.parentId = parentId;
		}

		private List<Child> children = new ArrayList<>();

		@ElementCollection(fetch = FetchType.EAGER)
		@OrderColumn
		public List<Child> getChildren() {
			return children;
		}

		public void setChildren(List<Child> children) {
			this.children = new ArrayList<>(children);
		}

		// Uncommenting this version of the property setter
		// will result in a org.hibernate.LazyInitializationException
//		public void setChildren(List<Child> children) {
//			this.children = children;
//			System.out.println("received " + children.size() + " child items");
//		}
	}

	@Embeddable
	public static class Child {
		private String name;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}
}
