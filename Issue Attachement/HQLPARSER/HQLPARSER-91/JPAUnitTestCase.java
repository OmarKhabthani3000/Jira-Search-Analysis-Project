package org.hibernate.bugs;

import jakarta.persistence.*;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.NaturalId;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
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
	public void hhh123Test() throws Exception {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();
		for (long i = 0; i < 2; i++) {
			Department department = new Department();
			department.id = i + 1;
			department.name = String.format("Department %d", department.id);
			entityManager.persist(department);

			for (long j = 0; j < 3; j++) {
				Employee employee1 = new Employee();
				employee1.username = String.format("user %d_%d", i, j);
				employee1.department = department;
				entityManager.persist(employee1);
			}
		}
		entityManager.getTransaction().commit();
		entityManager.close();
		EntityManager entityManager2 = entityManagerFactory.createEntityManager();
		entityManager2.getTransaction().begin();
			List<Wrapper> departments = entityManager2.createQuery(
							"select new org.hibernate.bugs.Wrapper(d, d.id as offending) " +
									"from Department d " +
									"where d.name like :token order by offending", Wrapper.class)
					.setParameter("token", "Department%")
					.getResultList();

			for (Wrapper wrapper : departments) {
				assertEquals(3, wrapper.department.getEmployees().size());
			}
		entityManager2.getTransaction().commit();
		entityManager2.close();
	}

	@Entity(name = "Department")
	public static class Department {

		@Id
		private Long id;

		private String name;

		//tag::fetching-strategies-fetch-mode-subselect-mapping-example[]
		@OneToMany(mappedBy = "department", fetch = FetchType.LAZY)
		@Fetch(FetchMode.SUBSELECT)
		private List<Employee> employees = new ArrayList<>();
		//end::fetching-strategies-fetch-mode-subselect-mapping-example[]

		//Getters and setters omitted for brevity

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

		public List<Employee> getEmployees() {
			return employees;
		}

		public void setEmployees(List<Employee> employees) {
			this.employees = employees;
		}
	}

	@Entity(name = "Employee")
	public static class Employee {

		@Id
		@GeneratedValue
		private Long id;

		@NaturalId
		private String username;

		@ManyToOne(fetch = FetchType.LAZY)
		private Department department;

		//Getters and setters omitted for brevity

	}
}
