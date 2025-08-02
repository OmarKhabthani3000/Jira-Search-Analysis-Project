package org.hibernate.bugs;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Id;
import jakarta.persistence.LockModeType;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Persistence;
import jakarta.persistence.Version;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class JPAUnitTestCase {

    private static final int NUMBER_OF_THREADS = 5;

    private ExecutorService executorService;

    private EntityManagerFactory entityManagerFactory;

    @Before
    public void init() {
        executorService = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
        entityManagerFactory = Persistence.createEntityManagerFactory("templatePU");
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();
        SampleReferencedEntity sampleReferencedEntity = em.find(SampleReferencedEntity.class, "sampleId");
        if (sampleReferencedEntity == null) {
            sampleReferencedEntity = new SampleReferencedEntity("sampleId");
            em.persist(sampleReferencedEntity);
        }
        SampleEntity sampleEntity = em.find(SampleEntity.class, "sampleId");
        if (sampleEntity == null) {
            em.persist(new SampleEntity("sampleId", sampleReferencedEntity));
        }
        em.getTransaction().commit();
        em.close();
    }

    @After
    public void destroy() {
        executorService.shutdown();
        entityManagerFactory.close();
    }

    @Test
    public void testPessimisticLockForSimultaneousOperations() throws Exception {
        List<Callable<Boolean>> callables = IntStream.range(0, NUMBER_OF_THREADS).<Callable<Boolean>>mapToObj(i -> () -> {
            EntityManager em = entityManagerFactory.createEntityManager();
            try {
                em.getTransaction().begin();

                SampleEntity sampleEntity = em.find(SampleEntity.class, "sampleId", LockModeType.PESSIMISTIC_WRITE);
                sampleEntity.setAttribute(Instant.now().toString());

                em.getTransaction().commit();
                return true;
            } catch (Exception e) {
                em.getTransaction().rollback();
                return false;
            } finally {
                em.close();
            }
        }).collect(Collectors.toList());

        List<Future<Boolean>> futures = executorService.invokeAll(callables);

        List<Boolean> results = futures.stream().map(f -> {
            try {
                return f.get();
            } catch (InterruptedException | ExecutionException aE) {
                return false;
            }
        }).collect(Collectors.toList());

        Assert.assertEquals(NUMBER_OF_THREADS, results.stream().filter(r -> r).count());
    }

    @Entity
    public static class SampleEntity {

        @Id
        private String id;

        private String attribute;

        @OneToOne
        private SampleReferencedEntity referencedEntity;

        @Version
        private int version;

        protected SampleEntity() {

        }

        public SampleEntity(String id, SampleReferencedEntity referencedEntity) {
            this.id = id;
            this.referencedEntity = referencedEntity;
        }

        public String getId() {
            return id;
        }

        public String getAttribute() {
            return attribute;
        }

        public void setAttribute(String attribute) {
            this.attribute = attribute;
        }

        public int getVersion() {
            return version;
        }

        public SampleReferencedEntity getReferencedEntity() {
            return referencedEntity;
        }
    }

    @Entity
    public static class SampleReferencedEntity {

        @Id
        private String id;

        protected SampleReferencedEntity() {

        }

        public SampleReferencedEntity(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

    }

}
