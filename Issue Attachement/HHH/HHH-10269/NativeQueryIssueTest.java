package org.hibernate.issues;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

public class NativeQueryIssueTest {
    protected final Log LOG = LogFactory.getLog(NativeQueryIssueTest.class);
    private int counter;

    @Test
    public void testNativeQuery() {
        final EntityManagerFactory entityManagerFactory = Persistence
                .createEntityManagerFactory("org.hibernate.issues.native-query-issue");
        final EntityManager entityManager = entityManagerFactory.createEntityManager();
        // on iteration 300 exception appeared:
        // WARN org.hibernate.engine.jdbc.spi.SqlExceptionHelper: SQL Error: 1000, SQLState: 72000
        // ERROR org.hibernate.engine.jdbc.spi.SqlExceptionHelper: ORA-01000: maximum open cursors exceeded
        for (int i = 0; i < 1000; i++) {
            selectNonExistedTable(entityManager);
        }

    }


    private void selectNonExistedTable(final EntityManager entityManager) {
        LOG.info("Iteration: " + counter++);
        try {
            // "Select 1 from Dual" - worked 
            entityManager.createNativeQuery("Select 1 from NotExistedTable").getResultList();
        } catch (Exception e) {
            LOG.error("Expected exception " + e.getMessage());
        }

    }

}
