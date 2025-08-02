package org.hibernate.bugs;

import org.hamcrest.core.Is;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;

/**
 * This template demonstrates how to develop a test case for Hibernate ORM, using the Java Persistence API.
 */
public class JPAUnitTestCase {

    private EntityManagerFactory entityManagerFactory;

    @Before
    public void init() {
        entityManagerFactory = Persistence.createEntityManagerFactory("templatePU");
    }

    @After
    public void destroy() {
        entityManagerFactory.close();
    }

    // Entities are auto-discovered, so just add them anywhere on class-path
    // Add your tests, using standard JUnit.
    @Test
    public void hhh123Test() throws Exception {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        AnyEntity child = new AnyEntity();
        entityManager.persist(child);
        AnyEntity parent = new AnyEntity();
        parent.childs.add(child);
        entityManager.persist(parent);

        entityManager.flush();
        entityManager.clear();

        CriteriaQuery<AnyEntity> query = entityManager.getCriteriaBuilder().createQuery(AnyEntity.class);
        Root<AnyEntity> from = query.from(AnyEntity.class);
        query.select(from);
        List<AnyEntity> resultList = entityManager.createQuery(query).getResultList();
        assertThat(resultList.size(), Is.is(2));
        for (AnyEntity entity : resultList) {
            assertThat(entityManager.getEntityManagerFactory().getPersistenceUnitUtil().isLoaded(entity.childs), Is.is(false));
            assertThat(entityManager.getEntityManagerFactory().getPersistenceUnitUtil().isLoaded(entity.parents), Is.is(false));
        }
        entityManager.getTransaction().commit();
        entityManager.close();
    }


    @Entity
    public static class AnyEntity {
        @Id
        @GeneratedValue
        private Integer id;

        @ManyToMany(fetch = FetchType.LAZY)
        @NotFound(action = NotFoundAction.IGNORE)
        private List<AnyEntity> childs = new ArrayList<>();

        @ManyToMany(mappedBy = "childs", fetch = FetchType.LAZY)
        private List<AnyEntity> parents = new ArrayList<>();
    }

}
