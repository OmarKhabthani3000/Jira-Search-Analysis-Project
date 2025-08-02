
import jakarta.persistence.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class HibernateOracleUnitTestCase
{

    private EntityManagerFactory emf;
    private EntityManager em;

    @BeforeEach
    public void setup() {
        emf = Persistence.createEntityManagerFactory( "oracle" );
        em = emf.createEntityManager();
    }

    @AfterEach
    @Transactional
    public void tearDown() {
        em.close();
        emf.close();
    }

    // This method is used to test the query timeout exception for Oracle
    // by issuing a sleep command as follows:
    // BEGIN DBMS_LOCK.SLEEP(30); END;
    @Test
    public void nativeQuery1TimeoutShouldThrowQueryTimeoutException() throws Exception {
        Query query = em.createNativeQuery("BEGIN DBMS_LOCK.SLEEP(30); END;");
        query.setHint("jakarta.persistence.query.timeout", 1000); // Very low timeout to force the exception
        assertThrows(QueryTimeoutException.class, query::getResultList);
    }

    @Test
    public void nativeQuery2TimeoutShouldThrowQueryTimeoutException() throws Exception {
        Query query = em.createNativeQuery("select * from EMPLOYEE");
        query.setHint("jakarta.persistence.query.timeout", 1000); // Very low timeout to force the exception
        long start = System.currentTimeMillis();
        query.getResultList();
        long end = System.currentTimeMillis();
        System.out.println("Time taken: " + (end - start) + "ms");
    }
}