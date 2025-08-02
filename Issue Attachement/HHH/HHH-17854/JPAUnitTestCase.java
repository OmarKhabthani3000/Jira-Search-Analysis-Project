package org.hibernate.bugs;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Persistence;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.hibernate.annotations.SQLRestriction;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * This template demonstrates how to develop a test case for Hibernate ORM, using the Java Persistence API.
 */
public class JPAUnitTestCase {

    private EntityManagerFactory entityManagerFactory;

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

        Child e1 = new Child();
        e1.setDeletedAt(LocalDateTime.now());
        Child e2 = new Child();
        Child e3 = new Child();
        Parent entity = new Parent();
        entity.addChild(e1);
        entity.addChild(e2);
        entity.addChild(e3);
        entityManager.persist(entity);

        var eg = entityManager.createEntityGraph(Parent.class);
        eg.addAttributeNodes("childSet");
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Parent> criteriaQuery = criteriaBuilder.createQuery(Parent.class);
        Root<Parent> root = criteriaQuery.from(Parent.class);
        Predicate predicate = criteriaBuilder.equal(root.get("id"), 1);
        criteriaQuery.where(predicate);
        entityManager
                .createQuery(criteriaQuery)
                .setHint("javax.persistence.fetchgraph", eg)
                .getSingleResult();

        entityManager.getTransaction().commit();
        entityManager.close();
    }

    @Before
    public void init() {
        entityManagerFactory = Persistence.createEntityManagerFactory("templatePU");
    }

    @Entity
    static class Parent {
        @Id
        @GeneratedValue
        private Long id;

        @SQLRestriction("deleted_at IS NULL")
        @OneToMany(mappedBy = "parent")
        private Set<Child> childSet = new HashSet<>();

        public Parent(Long id, Set<Child> childSet) {
            this.id = id;
            this.childSet = childSet;
        }

        public Parent() {
        }

        public void addChild(Child c) {
            this.childSet.add(c);
            c.setParent(this);
        }

        public Set<Child> getChildSet() {
            return childSet;
        }

        public void setChildSet(Set<Child> childSet) {
            this.childSet = childSet;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }
    }

    @Entity
    static class Child {
        @Id
        @GeneratedValue
        private Long id;

        @ManyToOne
        private Parent parent;

        private LocalDateTime deletedAt;

        public Child(Long id, Parent parent, LocalDateTime deletedAt) {
            this.id = id;
            this.parent = parent;
            this.deletedAt = deletedAt;
        }

        public Child() {

        }

        public LocalDateTime getDeletedAt() {
            return deletedAt;
        }

        public void setDeletedAt(LocalDateTime deletedAt) {
            this.deletedAt = deletedAt;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Parent getParent() {
            return parent;
        }

        public void setParent(Parent parent) {
            this.parent = parent;
        }
    }
}
