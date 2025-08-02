package org.hibernate.bugs;

import jakarta.persistence.*;

import jakarta.persistence.criteria.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

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

		EntityGraph entityGraph = entityManager.getEntityGraph("test-graph");
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		ArrayList<Predicate> predicates = new ArrayList<>();


		CriteriaQuery<Person> criteriaQuery = criteriaBuilder.createQuery(Person.class);
		Root<Person> root = criteriaQuery.from(Person.class);

		Join<Object, Object> join = root.join("address", JoinType.LEFT);
		predicates.add(criteriaBuilder.equal(join.get("description"), "test"));

		criteriaQuery.distinct(true)
				.where(predicates.toArray(new Predicate[0]))
				.orderBy(criteriaBuilder.asc(join.get("id")));
		TypedQuery<Person> typedQuery = entityManager.createQuery(criteriaQuery);
		typedQuery.setHint("jakarta.persistence.fetchgraph", entityGraph);

		typedQuery.getResultList();

		entityManager.getTransaction().commit();
		entityManager.close();
	}

	@Entity
	public static class Address{
		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		@Column(name = "id", nullable = false)
		private Integer id;

		@Column(name = "description", nullable = false)
		private String description;

		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String value) {
			this.description = value;
		}
	}

	@Entity
	@NamedEntityGraph(
			name = "test-graph",
			attributeNodes = {
					@NamedAttributeNode("address"),
			}
	)
	public static class Person {
		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		@Column(name = "id", nullable = false)
		private Integer id;

		@Column(name = "name", nullable = false)
		private String name;

		@ManyToOne(fetch = FetchType.LAZY)
		@JoinColumn(name = "address_id")
		private Address address;

		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Address getAddress() {
			return address;
		}

		public void setAddress(Address number) {
			this.address = number;
		}
	}
}
