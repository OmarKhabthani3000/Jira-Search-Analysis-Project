package com.autodesk.lbs.cs;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNotNull;

import java.util.EnumSet;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.hibernate.Criteria;
import org.hibernate.criterion.NaturalIdentifier;
import org.hibernate.criterion.Restrictions;
import org.hibernate.ejb.HibernateEntityManager;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.autodesk.lbs.common.entity.EntityManagerContext;

public class DummyTest {

    private static EntityManagerFactory emf;
    protected EntityManager em;

    @BeforeClass
    public static void beforeAllTests() {
        emf = Persistence.createEntityManagerFactory("clientservices");
    }

    @Before
    public void beforeTest() {
        em = emf.createEntityManager();
        em.getTransaction().begin();
    }

    @Test
    public void testQuery() {
        String n = "joe";
        Dummy d = new Dummy(n);
        em.persist(d);
        
        Dummy d2 = naturalKeyCachedQuery(em, Dummy.class, "name", n);
        assertEquals(n, d2.getName());
        em.remove(d);
        
        naturalKeyCachedQuery(em, Dummy.class, "name", n);
        
        /*
        Dummy d3 = new Dummy(n);
        em.persist(d3);
        
        Dummy d4 = naturalKeyCachedQuery(em, Dummy.class, "name", n);
        assertEquals(d3.getId(), d4.getId());
        */
    }
    
    public static <T> T naturalKeyCachedQuery(EntityManager em, Class<T> c, String key, Object value) {
        return naturalKeyCachedQuery(em, c, Restrictions.naturalId().set(key, value));
    }

    public static <T> T naturalKeyCachedQuery(EntityManager em, Class<T> c, NaturalIdentifier id) {
        HibernateEntityManager hem = (HibernateEntityManager) em;
        Criteria criteria = hem.getSession().createCriteria(c);
        criteria.add(id);
        criteria.setCacheable(true);
        return c.cast(criteria.uniqueResult());
    }

    @After
    public void afterTest() {
        if (em != null && em.getTransaction() != null && em.getTransaction().isActive())
            em.getTransaction().rollback();
        EntityManagerContext.close();
    }

}