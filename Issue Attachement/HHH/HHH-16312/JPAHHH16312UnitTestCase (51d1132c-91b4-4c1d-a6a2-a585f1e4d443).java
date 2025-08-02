package org.hibernate.bugs;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Persistence;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.List;
import java.util.Set;
import org.junit.After;
import org.junit.Before;
//import org.junit.Test;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

/**
 * This template demonstrates how to develop a test case for Hibernate ORM, using the Java Persistence API.
 */
public class JPAHHH16312UnitTestCase {

	private EntityManagerFactory entityManagerFactory;

	@Before
	@BeforeEach
	public void init() {
		entityManagerFactory = Persistence.createEntityManagerFactory( "templatePU" );
	}

	@After
	@AfterEach
	public void destroy() {
		entityManagerFactory.close();
	}

	@Test
	public void hhh16312Test_WithArrayFunctionOnAttribute_ValidateFails() throws Exception {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();

		entityManager.persist(new Person(1L, "Dupont", Set.of(Accessory.HAT, Accessory.UMBRELLA)));
		entityManager.persist(new Person(2L, "Dupond", Set.of(Accessory.HAT, Accessory.UMBRELLA)));
		entityManager.persist(new Person(3L, "Professeur Tournesol", Set.of(Accessory.HAT)));

		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Person> query = criteriaBuilder.createQuery(Person.class);
		Root<Person> rootEntity = query.from(Person.class);
		Predicate criteria = criteriaBuilder.equal(
				criteriaBuilder.literal(Accessory.UMBRELLA.name()), 
				criteriaBuilder.function("ANY", String.class, rootEntity.<Set<Accessory>>get("accessories")));
		query.where(criteria);
		// execution works only on some databases (Postgres)
		//List<Person> results = entityManager.createQuery(query).getResultList();
		//assertThat(results).hasSize(2);

		entityManager.getTransaction().commit();
		entityManager.close();
	}

	@Test
	public void hhh16312Test_WithArrayFunctionOnLiteral_ValidateFails() throws Exception {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();

		entityManager.persist(new Person(4L, "Dupont", Set.of(Accessory.HAT, Accessory.UMBRELLA)));
		entityManager.persist(new Person(5L, "Dupond", Set.of(Accessory.HAT, Accessory.UMBRELLA)));
		entityManager.persist(new Person(6L, "Professeur Tournesol", Set.of(Accessory.HAT)));

		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Person> query = criteriaBuilder.createQuery(Person.class);
		Root<Person> rootEntity = query.from(Person.class);
		Predicate criteria = criteriaBuilder.equal(
				rootEntity.<String>get("name"), 
				criteriaBuilder.function("ANY", String.class, criteriaBuilder.literal(Set.of("Dupont", "Dupond"))));
		query.where(criteria);
		// execution works only on some databases (Postgres, H2)
		//List<Person> results = entityManager.createQuery(query).getResultList();
		//assertThat(results).hasSize(2);

		entityManager.getTransaction().commit();
		entityManager.close();
	}

	@Entity
	public static class Person {

		@Id
		private Long id;

		@Column(columnDefinition = "VARCHAR")
		private String name;

		@Column(columnDefinition = "VARCHAR ARRAY")
		@Enumerated(EnumType.STRING)
		private Set<Accessory> accessories;

		public Person() {
		}

		public Person(Long id, String name, Set<Accessory> accessories) {
			this.id = id;
			this.name = name;
			this.accessories = accessories;
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

		public Set<Accessory> getAccessories() {
			return accessories;
		}

		public void setAccessories(Set<Accessory> accessories) {
			this.accessories = accessories;
		}
	}

	public static enum Accessory {
		HAT, UMBRELLA;
	}
}
