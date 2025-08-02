package org.hibernate.bugs;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Persistence;

import jakarta.persistence.PostLoad;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class JPAUnitTestCase640 {

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
    public void h640ImplicitEntitiesLoadInPostLoad() throws Exception {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        Child child1 = new Child();
        entityManager.persist(child1);
        Child child2 = new Child();
        entityManager.persist(child2);
        Parent parent1 = new Parent(List.of(child1));
        entityManager.persist(parent1);
        Parent parent2 = new Parent(List.of(child2));
        entityManager.persist(parent2);

        entityManager.getTransaction().commit();

        entityManager.clear();

        entityManager.getTransaction().begin();

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Parent> criteriaQuery = builder.createQuery(Parent.class);

        Root<Parent> root = criteriaQuery.from(Parent.class);
        criteriaQuery.select(root);

        TypedQuery<Parent> typedQuery = entityManager.createQuery(criteriaQuery);
        List<Parent> parents = typedQuery.getResultList();

        entityManager.getTransaction().commit();
        entityManager.close();

        Assert.assertNotNull(parents.get(0).getChildren().get(0));
        Assert.assertNotNull(parents.get(1).getChildren().get(0));
    }

    @Entity
    public static class Parent implements Serializable {

        private Long id;
        private List<Child> children = new ArrayList<>();

        public Parent() {
        }

        public Parent(List<Child> children) {
            this.children = children;
        }

        @PostLoad
        protected void jpaPostLoad() {
            //
            // At time of this call, with Hibernate 6.4.0, there's no children entities (@ManyToMany) in persistence
            // context (even with FetchType.EAGER), so ConcurrentModificationException is thrown on next iteration
            // in StatefulPersistenceContext.postLoad() as persistence context (entitiesByKey) is extended by children.
            //
            // It looks that, in order to be more compatible with previous versions, it's better to collect events
            // while processing holder consumers in StatefulPersistenceContext.postLoad(), and fire these events
            // in a separate loop after that processing (also be aware about _iterator.remove();_ - currently it's
            // theoretically possible to get ConcurrentModificationException if a nested iterator from a fired event
            // will remove an entity).
            //
            // It will be also better if new Hibernate will allow FetchType.LAZY for this case.
            //
            if (getChildren() != null) {
                System.out.println("parent (id = " + getId() + ") has " + getChildren().size() + " children");
            }
        }

        @Id
        @GeneratedValue
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        @ManyToMany(fetch = FetchType.EAGER)
        public List<Child> getChildren() {
            return children;
        }

        public void setChildren(List<Child> children) {
            this.children = children;
        }
    }

    @Entity
    public static class Child implements Serializable {

        private Long id;

        public Child() {
        }

        @Id
        @GeneratedValue
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }
    }

}
