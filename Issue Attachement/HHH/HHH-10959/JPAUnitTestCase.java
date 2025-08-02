package org.hibernate.bugs;


import static org.junit.Assert.assertEquals;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TemporalType;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


/**
 * This template demonstrates how to develop a test case for Hibernate ORM, using the Java Persistence API.
 */
public class JPAUnitTestCase {

    private EntityManagerFactory entityManagerFactory;


    @Before
    public void init () {
        entityManagerFactory = Persistence.createEntityManagerFactory("templatePU");
    }


    @After
    public void destroy () {
        entityManagerFactory.close();
    }


    // Entities are auto-discovered, so just add them anywhere on class-path
    // Add your tests, using standard JUnit.
    @Test
    public void hhh10959Test () throws Exception {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        ConvertedEntity e = new ConvertedEntity();
        e.id = 1;
        e.date = new ConvertedDate();
        e.date.timestamp = System.currentTimeMillis();
        entityManager.persist(e);
        entityManager.getTransaction().commit();

        entityManager.getTransaction().begin();
        Session sess = entityManager.unwrap(Session.class);
        Query<ConvertedEntity> query = sess.createQuery("SELECT e FROM ConvertedEntity e WHERE e.date <= :ts", ConvertedEntity.class); //$NON-NLS-1$

        // this is deprecated
        // ConvertedDate qd = new ConvertedDate();
        // qd.timestamp = System.currentTimeMillis();
        // query.setParameter("ts", qd, TemporalType.TIMESTAMP);

        // this does not perform the secondary conversion to java.sql.*
        // query.setParameter("ts", qd);

        // this throws ClassCastException
        query.setParameter("ts", new Date(), TemporalType.TIMESTAMP);

        assertEquals(1, query.getResultList().size());

        entityManager.getTransaction().commit();
        entityManager.close();
    }
}
