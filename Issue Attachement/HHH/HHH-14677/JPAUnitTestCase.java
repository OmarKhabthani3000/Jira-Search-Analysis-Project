package org.hibernate.bugs;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;

import org.hibernate.criterion.Order;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

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
	public void treatMultiRootJoinedOrderedBy() throws Exception {
		EntityManager em = entityManagerFactory.createEntityManager();
		em.getTransaction().begin();
		// Do stuff...

		Greyhound greyhound = new Greyhound();
		greyhound.setName("wuff");
		Dachshund dachshund = new Dachshund();
		dachshund.setName("smallwuff");
		dachshund.setRoyalName("Sir Smallwuff");
		em.persist( greyhound );
		em.persist( dachshund );

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Dog> criteriaQuery = cb.createQuery( Dog.class );

		Root<Dog> dogRoot = criteriaQuery.from( Dog.class );
		Root<Greyhound> greyHoundRoot = cb.treat( dogRoot, Greyhound.class );
		Root<Dachshund> dachsHundRoot = cb.treat( dogRoot, Dachshund.class );

		List<Dog> resultsWithoutOrder = em.createQuery( criteriaQuery ).getResultList();

		assertEquals(2, resultsWithoutOrder.size());

		System.out.println(" saved Dogs: " + resultsWithoutOrder.toString());

		Expression<Object> caseNameExpression = cb.selectCase()
				.when(cb.isNotNull(greyHoundRoot.get("name")), greyHoundRoot.get("name"))
				.when(cb.isNotNull(dachsHundRoot.get("royalName")), dachsHundRoot.get("royalName"))
				.when(cb.and(cb.isNull(dachsHundRoot.get("royalName")), cb.isNotNull(dachsHundRoot.get("name")) ), dachsHundRoot.get("name"))
				.otherwise("");
		criteriaQuery.orderBy(cb.asc(caseNameExpression));

		List<Dog> results = em.createQuery( criteriaQuery ).getResultList();

		System.out.println(" ordered Dogs: " + results.toString());
		// we should two dogs ordered by the name / royalname
		assertEquals(2, results.size());

		em.getTransaction().commit();
		em.close();
	}

	@Entity(name = "Dog")
	@Inheritance(strategy = InheritanceType.JOINED)
	@Table(name = "dog")
	public static abstract class Dog {

		@Id
		@GeneratedValue
		private Long id;

		private boolean fast;

		private String name;

		protected Dog(boolean fast) {
			this.fast = fast;
		}

		public Dog() {

		}

		public final boolean isFast() {
			return fast;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}

	@Entity(name = "Dachshund")
	@Table(name = "dachshund")
	public static class Dachshund extends Dog {
		public Dachshund() {
			super( false );
		}

		private String royalName;

		public String getRoyalName() {
			return royalName;
		}

		public void setRoyalName(String royalName) {
			this.royalName = royalName;
		}
	}

	@Entity(name = "Greyhound")
	@Table(name = "greyhound")
	public static class Greyhound extends Dog {
		public Greyhound() {
			super( true );
		}
	}
}
