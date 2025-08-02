package org.hibernate.bugs;

import java.util.stream.Stream;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Persistence;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.hibernate.testing.TestForIssue;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class JPAUnitTestCase {
    
    @Entity
    public static class SampleEntity {
        
        @Id
        @GeneratedValue
        private long id;
        
        @Basic
        private double value;
        
    }

    private EntityManagerFactory entityManagerFactory;

    @Before
    public void init() {
        this.entityManagerFactory = Persistence.createEntityManagerFactory("h2");
    }

    @After
    public void destroy() {
        this.entityManagerFactory.close();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-11743")
    public void testTupleStream() {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> criteriaQuery = criteriaBuilder.createQuery(Tuple.class);
        Root<SampleEntity> from = criteriaQuery.from(SampleEntity.class);
        Expression<Long> count = criteriaBuilder.count(from);
        criteriaQuery.multiselect(count, count);

        Stream<Tuple> stream = entityManager.unwrap(Session.class).createQuery(criteriaQuery).stream();

        Tuple tuple = stream.findAny().get();

        Assert.assertTrue(tuple instanceof Tuple);
    }

}
