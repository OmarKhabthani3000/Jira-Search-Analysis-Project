package org.hibernate.bugs;

import de.roeperweise.compid.Company;
import de.roeperweise.compid.Department;
import de.roeperweise.compid.Employee;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test cascade = CascadeType.PERSIST in chained Entities.
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

        Company company = new Company("ABC Development");
        Department financeDep = new Department(company, "Finance");
        Employee employee = new Employee("Manager", financeDep);
        entityManager.persist(employee);
    
		entityManager.getTransaction().commit();
        
        Department dep = entityManager.find(Department.class, financeDep.getId());
        Assert.assertNotNull(dep.getCompany());
        
		entityManager.close();
	}
}