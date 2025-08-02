package org.hibernate.ejb.test.cascade;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import javax.persistence.*;

import org.junit.Test;

import org.hibernate.ejb.test.BaseEntityManagerFunctionalTestCase;

import static javax.persistence.CascadeType.DETACH;
import static javax.persistence.CascadeType.REMOVE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class DetachAndContainsTest extends BaseEntityManagerFunctionalTestCase {

	@Test
	public void testDetachCompositeKeyCollection() {
		EntityManager em = getOrCreateEntityManager();
		em.getTransaction().begin();

		Child child = new Child();
		Parent parent = new Parent();
		Grandparent grandparent = new Grandparent();
		em.persist( grandparent );
		em.persist( parent );
		em.persist( child );
		child.grandparentId = grandparent.id;
		parent.parent = grandparent;
		parent.name = "Parent's name";
		parent.children = new ArrayList<Child>();
		child.parentName = parent.name;
		parent.children.add( child );
		grandparent.children = new ArrayList<Parent>();
		grandparent.children.add( parent );
		em.getTransaction().commit();
		em.close();

		em = getOrCreateEntityManager();
		em.getTransaction().begin();
		grandparent = em.find( Grandparent.class, grandparent.id );
		assertNotNull( grandparent );
		assertEquals( 1, grandparent.children.size() );
		parent = grandparent.children.iterator().next();
		em.detach( grandparent );
		assertFalse( em.contains( parent ) );
		assertFalse( em.contains( child ) );
		em.getTransaction().commit();
		em.close();

		em = getOrCreateEntityManager();
		em.getTransaction().begin();
		em.remove( em.find( Grandparent.class, grandparent.id ) );

		em.getTransaction().commit();
		em.close();
	}

	@Override
	public Class[] getAnnotatedClasses() {
		return new Class[] {
				Grandparent.class,
				Parent.class,
				Child.class
		};
	}

	@Entity
	public static class Grandparent {
		@Id
		@GeneratedValue
		public Integer id;
		@OneToMany(mappedBy = "parent", cascade = { DETACH, REMOVE} )
		public Collection<Parent> children;
	}

	@Entity
	public static class Parent implements Serializable {
		@Id
		@GeneratedValue
		public Integer id;
		public String name;
		@OneToMany(mappedBy = "parent", cascade = { DETACH, REMOVE} )
		public Collection<Child> children;
		@ManyToOne
		@JoinColumn(name = "grandparent_id")
		public Grandparent parent;
	}

	@Entity
	public static class Child {
		@Id
		@GeneratedValue
		public Integer id;
		@ManyToOne
		@JoinColumns({
				@JoinColumn(name = "grandparent_id", referencedColumnName = "grandparent_id", insertable = false, updatable = false),
				@JoinColumn(name = "parentName", referencedColumnName = "name", insertable = false, updatable = false)
		})
		public Parent parent;
		@Column(name = "grandparent_id")
		public Integer grandparentId;
		public String parentName;
	}
}
