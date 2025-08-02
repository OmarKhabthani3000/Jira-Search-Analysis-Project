package org.hibernate.userguide.collections;

import jakarta.persistence.*;
import org.hibernate.Hibernate;
import org.hibernate.orm.test.jpa.BaseEntityManagerFunctionalTestCase;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hibernate.testing.transaction.TransactionUtil.doInJPA;

public class BidirectionalListKeepOrphansWithNewParentTest extends BaseEntityManagerFunctionalTestCase {

	@Override
	protected Class<?>[] getAnnotatedClasses() {
		return new Class<?>[]{
			Root.class,
			Level1.class,
			Level2.class
		};
	}

	@Before
	public void setUp() throws Exception {
		doInJPA(this::entityManagerFactory, em -> {
			Root root = new Root();

			Level1 level1 = new Level1("old");
			root.addChild(level1);

			Level2 level2 = new Level2("old");
			level1.addChild(level2);

			em.persist(root);
		});
	}

	@After
	public void tearDown() throws Exception {
		doInJPA(this::entityManagerFactory, em -> {
			em.createQuery("delete from Level2").executeUpdate();
			em.createQuery("delete from Level1").executeUpdate();
			em.createQuery("delete from Root").executeUpdate();
		});
	}

	@Test
	public void createNewLevel1MoveLevel2ToItAndRemoveOldLevel1InOneStep() {
		// given setup

		// when
		doInJPA(this::entityManagerFactory, em -> {
			Root root = em.createQuery("select r from Root r", Root.class).getSingleResult();

			Level1 level1 = root.getChildren().get(0);
			root.removeChild(level1);

			Level2 level2 = level1.getChildren().get(0);

			Level1 newLevel1 = new Level1("new");
			level1.removeChild(level2);
			newLevel1.addChild(level2);

			root.addChild(newLevel1);
		});

		// then
		assertThatOldLevel2ExistsInNewLevel1();
	}

	@Test
	public void createNewLevel1MoveLevel2ToItAndRemoveOldLevel1InTwoSteps() {
		// given setup

		// when
		doInJPA(this::entityManagerFactory, em -> {
			Root root = em.createQuery("select r from Root r", Root.class).getSingleResult();

			Level1 newLevel1 = new Level1("new");
			root.addChild(newLevel1);
		});

		doInJPA(this::entityManagerFactory, em -> {
			Root root = em.createQuery("select r from Root r", Root.class).getSingleResult();

			Level1 oldLevel1 = root.getChildren().get(0);
			Level2 level2 = oldLevel1.getChildren().get(0);
			oldLevel1.removeChild(level2);

			Level1 newLevel1 = root.getChildren().get(1);
			newLevel1.addChild(level2);

			root.removeChild(oldLevel1);
		});

		// then
		assertThatOldLevel2ExistsInNewLevel1();
	}

	private void assertThatOldLevel2ExistsInNewLevel1() {
		doInJPA(this::entityManagerFactory, em -> {
			Root root = em.createQuery("select r from Root r", Root.class).getSingleResult();

			Level1 newLevel1 = root.getChildren().get(0);
			Assert.assertEquals("new", newLevel1.getName());

			List<Level2> level1Children = newLevel1.getChildren();
			Assert.assertEquals(1, level1Children.size());
			Assert.assertEquals("old", level1Children.get(0).getName());
		});
	}

	@Entity(name = "Root")
	public static class Root extends Base {

		@OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
		@OrderColumn
		private List<Level1> children = new ArrayList<>();

		public List<Level1> getChildren() {
			return children;
		}

		public void setChildren(List<Level1> children) {
			this.children = children;
		}

		public void addChild(Level1 child) {
			this.children.add(child);
			child.setParent(this);
		}

		public void removeChild(Level1 child) {
			this.children.remove(child);
			child.setParent(null);
		}

	}

	@Entity(name = "Level1")
	public static class Level1 extends Base {

		@ManyToOne(optional = false)
		private Root parent;

		@OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
		@OrderColumn
		private List<Level2> children = new ArrayList<>();

		public Level1() {
		}

		public Level1(String name) {
			super(name);
		}

		public Root getParent() {
			return parent;
		}

		public void setParent(Root parent) {
			this.parent = parent;
		}

		public List<Level2> getChildren() {
			return children;
		}

		public void setChildren(List<Level2> children) {
			this.children = children;
		}

		public void addChild(Level2 child) {
			this.children.add(child);
			child.setParent(this);
		}

		public void removeChild(Level2 child) {
			this.children.remove(child);
			child.setParent(null);
		}

	}

	@Entity(name = "Level2")
	public static class Level2 extends Base {

		@ManyToOne(optional = false)
		private Level1 parent;

		public Level2() {
		}

		public Level2(String name) {
			super(name);
		}

		public Level1 getParent() {
			return parent;
		}

		public void setParent(Level1 parent) {
			this.parent = parent;
		}

	}

	@MappedSuperclass
	public static class Base {

		@Id
		@GeneratedValue
		private Long id;

		private String name = "";

		public Base() {
		}

		public Base(String name) {
			this.name = name;
		}

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}


		@Override
		public boolean equals(Object obj) {
			if (null == obj) {
				return false;
			}

			if (this == obj) {
				return true;
			}

			if (Hibernate.getClass(this) != Hibernate.getClass(obj)) {
				return false;
			}

			Base that = (Base) obj;

			return null != this.getId() && this.getId().equals(that.getId());
		}

		@Override
		public int hashCode() {
			return 31;
		}

	}

}
