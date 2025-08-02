package org.hibernate.bugs;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Id;
import javax.persistence.LockModeType;
import javax.persistence.Persistence;
import javax.persistence.Version;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test case for HHH-11810
 */
public class OptimisticForceIncrementNotIncrementedOnFlush {

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
    public void hhh123Test() throws Exception {
        //given
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();
        
        MyEntity toPersist = new MyEntity(0);
        toPersist.setData("sample v1");
        em.persist(toPersist);
        
        em.getTransaction().commit();
        em.close();
        
        //when
        em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();
        
        MyEntity toUpdate = em.find(MyEntity.class, 0l);
        em.lock(toUpdate, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
        toUpdate.setData("sample v2");
        em.flush();
        long versionAfterFlush = toUpdate.getVersion();

        em.getTransaction().commit();
        em.close();
        
        //then
        em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();
        
        MyEntity afterUpdate = em.find(MyEntity.class, 0l);
        
        Assert.assertEquals(versionAfterFlush,afterUpdate.getVersion());

        em.getTransaction().commit();
        em.close();
    }

    @Entity
    public static class MyEntity {

        @Id
        private long id;
        
        private String data;

        @Version
        private long version;

        public MyEntity(long id) {
            this.id = id;
            version = 0;
        }

        public MyEntity() {
        }

        public long getId() {
            return id;
        }

        public long getVersion() {
            return version;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }

    }
}
