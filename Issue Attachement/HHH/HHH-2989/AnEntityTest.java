package test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import junit.framework.Assert;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class AnEntityTest {

    private EntityManagerFactory factory;
    private EntityManager entityManager;
    private String pk;

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        factory = Persistence.createEntityManagerFactory("LoaderTestPU");
        entityManager = factory.createEntityManager();
        entityManager.getTransaction().begin();
        AnEntity e = new AnEntity();
        e.setTest("Test String 1");
        entityManager.persist(e);
        AnEntity e2 = new AnEntity();
        e2.setTest("Test String 2");
        entityManager.persist(e2);
        pk = e2.getId();
        entityManager.flush();
        Query query = entityManager.createNamedQuery("AnEntity.count");
        Long l = (Long) query.getSingleResult();
        entityManager.getTransaction().commit();
        Assert.assertTrue("Entity inserted", l == 2);
    }

    @Test
    public void testLoader() {
        entityManager = factory.createEntityManager();
        entityManager.getTransaction().begin();
        System.out.println("Looking for entity with PK [" + pk + "]");
        AnEntity e = entityManager.find(AnEntity.class, pk);
        entityManager.getTransaction().commit();
        Assert.assertNotNull("Loader query found an entity", e);
    }

}