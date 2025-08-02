package org.hibernate.jpa.test.query;

import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.TestForIssue;
import org.junit.Test;

import javax.persistence.*;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class QueryTypeDetermineTest extends BaseEntityManagerFunctionalTestCase {

    @Override
    public Class[] getAnnotatedClasses() {
        return new Class[]{
                Movie.class
        };
    }

    @Test
    @TestForIssue(jiraKey = "HHH-13131")
    public void testDetermineIndexedType() {
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();

        try {
            Query query = em.createQuery("select m from Movie m where (?1 is null or ?1=m.category)");
            query.setParameter(1, null);
            List list = query.getResultList();
            assertEquals(0, list.size());
        } finally {
            if (em.getTransaction() != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }


    enum MovieCategory {
        ACTION, DRAMA, SCIFI
    }

    @Entity(name = "Movie")
    public static class Movie {
        @Id
        @GeneratedValue
        Long id;

        MovieCategory category;
    }
}
