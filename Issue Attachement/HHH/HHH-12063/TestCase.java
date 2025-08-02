package org.hibernate.envers.bugs;

import static org.junit.Assert.assertTrue;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestCase {
    private EntityManagerFactory entityManagerFactory;

    @Before
    public void init() {
        entityManagerFactory = Persistence.createEntityManagerFactory("templatePU");
    }

    @After
    public void destroy() {
        entityManagerFactory.close();
    }

    @Test
    public void attributeAccessorTest() throws Exception {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        entityManager.persist(new Foo(1L));
        entityManager.getTransaction().commit();

        MyAttributeAccessor.invoked = false;

        AuditReader auditReader = AuditReaderFactory.get(entityManager);
        auditReader.createQuery().forRevisionsOfEntity(Foo.class, true, true).getResultList();

        entityManager.close();

        assertTrue(MyAttributeAccessor.invoked);
    }
}
