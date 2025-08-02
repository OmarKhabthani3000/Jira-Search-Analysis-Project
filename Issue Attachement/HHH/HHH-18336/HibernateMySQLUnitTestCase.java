
import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;
import test.Employee;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class HibernateMySQLUnitTestCase
{

    private EntityManagerFactory emf;
    private EntityManager em;

    @BeforeEach
    public void setup() {
        emf = Persistence.createEntityManagerFactory( "mysql" );
        em = emf.createEntityManager();
    }

    @AfterEach
    @Transactional
    public void tearDown() {
        em.close();
        emf.close();
    }

    // This method is used to test the query timeout exception for MySQL.
    // Before running this test, please first issue a lock table command as follows:
    // mysql> LOCK TABLES employee WRITE;
    @Test
    public void criteriaQuery1TimeoutShouldThrowQueryTimeoutException() throws Exception {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Employee> cq = cb.createQuery(Employee.class);
        Root<Employee> root = cq.from(Employee.class);
        cq.select(root);
        TypedQuery<Employee> query = em.createQuery(cq);
        query.setHint("jakarta.persistence.query.timeout", 1000); // Very low timeout to force the exception
        assertThrows(QueryTimeoutException.class, query::getResultList);
    }


    // This method is used to test the query timeout exception for MySQL.
    // Before running this test, please insert 100k records into the employee table.
    // If query timeout works, a QueryTimeoutException will be thrown.
    @Test
    public void criteriaQuery2TimeoutShouldThrowQueryTimeoutException() throws Exception {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Employee> cq = cb.createQuery(Employee.class);
        Root<Employee> root = cq.from(Employee.class);
        cq.select(root);
        TypedQuery<Employee> query = em.createQuery(cq);
        query.setHint("jakarta.persistence.query.timeout", 1000); // Very low timeout to force the exception
        long start = System.currentTimeMillis();
        query.getResultList();
        long end = System.currentTimeMillis();
        System.out.println("Time taken: " + (end - start) + "ms");
    }

}