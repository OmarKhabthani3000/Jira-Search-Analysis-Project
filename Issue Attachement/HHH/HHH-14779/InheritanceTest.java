import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Objects;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.Persistence;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.cache.internal.DisabledCaching;
import org.hibernate.cache.internal.EnabledCaching;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class InheritanceTest {
    private EntityManagerFactory entityManagerFactory;

    @Before
    public void setUp() throws Exception {
        entityManagerFactory = Persistence.createEntityManagerFactory("Test");
    }

    @After
    public void tearDown() throws Exception {
        entityManagerFactory.close();
    }

    @Test
    public void testFirstLevelCache() throws Exception {
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        try {
            entityManager.getTransaction().begin();
            Root root = entityManager.merge(new Root());
            Leaf1 leaf1 = entityManager.merge(new Leaf1());
            Leaf2 leaf2 = entityManager.merge(new Leaf2());
            entityManager.getTransaction().commit();

            assertEquals(root, entityManager.find(Root.class, root.getId()));
            assertEquals(leaf1, entityManager.find(Root.class, leaf1.getId()));
            assertEquals(leaf2, entityManager.find(Root.class, leaf2.getId()));

            assertNull(entityManager.find(Leaf1.class, root.getId()));
            assertEquals(leaf1, entityManager.find(Leaf1.class, leaf1.getId()));
            assertNull(entityManager.find(Leaf1.class, leaf2.getId()));

            assertNull(entityManager.find(Leaf2.class, root.getId()));
            assertNull(entityManager.find(Leaf2.class, leaf1.getId()));
            assertEquals(leaf2, entityManager.find(Leaf2.class, leaf2.getId()));
        } finally {
            entityManager.close();
        }
    }

    @Test
    public void testSecondLevelCache() throws Exception {
        assertThat(entityManagerFactory.getCache(), instanceOf(EnabledCaching.class));

        Root root = insert(new Root());
        Leaf1 leaf1 = insert(new Leaf1());
        Leaf2 leaf2 = insert(new Leaf2());

        assertEquals(root, find(Root.class, root.getId()));
        assertEquals(leaf1, find(Root.class, leaf1.getId()));
        assertEquals(leaf2, find(Root.class, leaf2.getId()));

        assertNull(find(Leaf1.class, root.getId()));
        assertEquals(leaf1, find(Leaf1.class, leaf1.getId()));
        assertNull(find(Leaf1.class, leaf2.getId()));

        assertNull(find(Leaf2.class, root.getId()));
        assertNull(find(Leaf2.class, leaf1.getId()));
        assertEquals(leaf2, find(Leaf2.class, leaf2.getId()));
    }

    @Test
    public void testNoCache() throws Exception {
        assertThat(entityManagerFactory.getCache(), instanceOf(DisabledCaching.class));

        Root root = insert(new Root());
        Leaf1 leaf1 = insert(new Leaf1());
        Leaf2 leaf2 = insert(new Leaf2());

        assertEquals(root, find(Root.class, root.getId()));
        assertEquals(leaf1, find(Root.class, leaf1.getId()));
        assertEquals(leaf2, find(Root.class, leaf2.getId()));

        assertNull(find(Leaf1.class, root.getId()));
        assertEquals(leaf1, find(Leaf1.class, leaf1.getId()));
        assertNull(find(Leaf1.class, leaf2.getId()));

        assertNull(find(Leaf2.class, root.getId()));
        assertNull(find(Leaf2.class, leaf1.getId()));
        assertEquals(leaf2, find(Leaf2.class, leaf2.getId()));
    }

    private <T> T insert(T entity) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        try {
            entityManager.getTransaction().begin();
            entity = entityManager.merge(entity);
            entityManager.getTransaction().commit();

            return entity;
        } finally {
            entityManager.close();
        }
    }

    private <T> T find(Class<T> entityClass, Object primaryKey) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        try {
            return entityManager.find(entityClass, primaryKey);
        } finally {
            entityManager.close();
        }
    }

    @Entity
    @Inheritance
    @Cacheable
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    public static class Root {
        @Id
        @GeneratedValue
        private long id;

        public long getId() {
            return id;
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null)
                return false;

            return getClass().equals(obj.getClass()) && id == ((Root) obj).id;
        }
    }

    @Entity
    public static class Leaf1 extends Root {
    }

    @Entity
    public static class Leaf2 extends Root {
    }
}
