
import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import test.Employee;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * This template demonstrates how to develop a test case for Hibernate ORM, using the Java Persistence API.
 */
public class JPAUnitTestCase {

    private EntityManagerFactory emf;
    private EntityManager em;

    @BeforeEach
    public void setup() {
        emf = Persistence.createEntityManagerFactory( "testPU" );
        em = emf.createEntityManager();
    }

    @AfterEach
    public void tearDown() {
        em.close();
        emf.close();
    }

    // Entities are auto-discovered, so just add them anywhere on class-path
    // Add your tests, using standard JUnit.
    @Test
    public void queryTimeoutShouldThrowQueryTimeoutException() throws Exception {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Employee> cq = cb.createQuery(Employee.class);
        Root<Employee> root = cq.from(Employee.class);
        cq.select(root);
        TypedQuery<Employee> query = em.createQuery(cq);
        query.setHint("jakarta.persistence.query.timeout", 1); // Very low timeout to force the exception

        // Execute the query
        assertThrows(QueryTimeoutException.class, () -> {
            List<Employee> employees = query.getResultList();
        });

        em.close();
    }
}