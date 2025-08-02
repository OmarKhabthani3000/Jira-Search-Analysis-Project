package org.hibernate.ejb.test.hhh6958;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;

import junit.framework.Assert;

import org.junit.Test;

import org.hibernate.ejb.test.BaseEntityManagerFunctionalTestCase;

public class PrePersistBugNewTest extends BaseEntityManagerFunctionalTestCase {

	public static int prePersistCounter = 0;

	@Test
	public void testCascade() {
		EntityManager em = getOrCreateEntityManager();

		A anA = new A();
		em.persist(anA);

		B aB = new B();
		C aC = new C();

		aB.setC(aC);

		anA.setB(aB);
		try {
			em.merge(anA);
		} catch (Throwable e) {
			// should throw an exception about the transient instance
			// object c will not be written do database (which is correct)
			// expected behaviour: exception about the reference to a transient instance from B to C
			// no call on prePersist of C whatsoever...
		}
		Assert.assertEquals(0, prePersistCounter );
	}

	@Entity
	public static class A {

		@Id
		@GeneratedValue(strategy = GenerationType.AUTO)
		private long id;

		@OneToOne(cascade = CascadeType.ALL, optional = true, orphanRemoval = true)
		private B b;

		public B getB() {
			return b;
		}

		public void setB(B b) {
			this.b = b;
		}
	}

	@Entity
	public static class B {

		@Id
		@GeneratedValue(strategy = GenerationType.AUTO)
		private long id;

		@ManyToOne(optional = false, cascade = { CascadeType.PERSIST })
		private C c;

		public C getC() {
			return c;
		}

		public void setC(C c) {
			this.c = c;
		}
	}

	@Entity
	public static class C {

		@Id
		@GeneratedValue(strategy = GenerationType.AUTO)
		private long id;

		private String anAttribute;

		@PrePersist
		public void prePersist() {
			prePersistCounter++; //count each call (should not be called since no instance is ever persisted)
			System.out.println("Called @prePersist on object of class C");
		}

	}
	
	@Override
	public Class[] getAnnotatedClasses() {
		return new Class[] { 
				A.class,
				B.class,
				C.class
		};
	}

}
