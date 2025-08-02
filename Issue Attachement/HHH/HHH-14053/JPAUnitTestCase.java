package by;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class JPAUnitTestCase {

    private EntityManagerFactory entityManagerFactory;

    @Before
    public void init() {
        entityManagerFactory = Persistence.createEntityManagerFactory("JPAUnit");
    }

    @After
    public void destroy() {
        entityManagerFactory.close();
    }

    @Test
    public void workingWithOneEntity() {
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();

        em.createQuery("select a from by.entity.Author a");

        // Assert not fail

        em.getTransaction().commit();
        em.close();
    }

    @Test(expected = RuntimeException.class)
    public void failsWithTwoEntities() {
        EntityManager em = entityManagerFactory.createEntityManager();

        try {
            em.getTransaction().begin();
            em.createQuery("select a, b from by.entity.Author a, by.entity.Book b");
        } finally {
            em.getTransaction().commit();
            em.close();
        }

    }

}