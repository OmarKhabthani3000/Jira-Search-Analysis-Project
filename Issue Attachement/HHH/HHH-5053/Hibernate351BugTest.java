package com.mckesson.hibernate351bug;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.criterion.Projections;
import org.junit.Assert;
import org.junit.Test;

public class Hibernate351BugTest
{
    @Test
    public void shouldBeAbleToGenerateIdsWithHiLoIdGenerator()
    {
        final int insertsPerTransaction = 20;
        Set<Long> ids = new HashSet<Long>();
        SessionFactory sessionFactory = getSessionFactory("create");
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        Criteria crit = session.createCriteria(TestEntity.class).setProjection(Projections.rowCount());
        long nEntities = ((Long) crit.list().get(0));
        Assert.assertEquals(0L, nEntities);
        for (int x = 0; x < insertsPerTransaction; x++)
        {
            Long id = (Long) session.save(new TestEntity("payload_" + x));
            ids.add(id);
        }
        tx.commit();
        session.close();
        sessionFactory.close();

        sessionFactory = getSessionFactory("verify");
        session = sessionFactory.openSession();
        tx = session.beginTransaction();
        crit = session.createCriteria(TestEntity.class).setProjection(Projections.rowCount());
        nEntities = ((Long) crit.list().get(0));
        
        // make sure we have the results from the previous session persisted. 
        Assert.assertEquals(insertsPerTransaction, nEntities);
        for (int x = insertsPerTransaction; x < insertsPerTransaction * 2; x++)
        {
            Long id = (Long) session.save(new TestEntity("payload_" + x));
            ids.add(id);
        }
        // The following commit() call results in org.hibernate.exception.ConstraintViolationException:
        // 16:43:06,360 WARN  [main] JDBCExceptionReporter: SQL Error: 23001, SQLState: 23001
        //16:43:06,375 ERROR [main] JDBCExceptionReporter: Unique index or primary key violation: "TEST_ENTITY_DATA ON PUBLIC.TEST_ENTITY(ORDER_ID, DUMMYPAYLOAD)"; SQL statement:
        ///* insert com.mckesson.hibernate351bug.Hibernate351BugTest$TestEntity */ insert into TEST_ENTITY (dummyPayload, ORDER_ID) values (?, ?) [23001-132]
        //16:43:06,391 WARN  [main] JDBCExceptionReporter: SQL Error: 23001, SQLState: 23001
        tx.commit();
    }

    public final SessionFactory getSessionFactory(String autoDDL)
    {
        AnnotationConfiguration hbConfig = new AnnotationConfiguration();
        hbConfig.addAnnotatedClass(TestEntity.class);
        hbConfig.setProperty("hibernate.cache.use_query_cache", "false");
        hbConfig.setProperty("hibernate.cache.use_second_level_cache", "false");
        hbConfig.setProperty("hibernate.generate_statistics", "false");
        hbConfig.setProperty("hibernate.show_sql", "false");
        hbConfig.setProperty("hibernate.format_sql", "true");
        hbConfig.setProperty("hibernate.use_sql_comments", "true");
        hbConfig.setProperty("hibernate.hbm2ddl.auto", autoDDL);
        hbConfig.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        hbConfig.setProperty("hibernate.connection.driver_class", "org.h2.Driver");
        hbConfig.setProperty("hibernate.connection.url", "jdbc:h2:file:hbdump.log");
        hbConfig.setProperty("hibernate.connection.username", "sa");
        hbConfig.setProperty("hibernate.connection.password", "");
        return hbConfig.buildSessionFactory();
    }

    @Entity
    @Table(name = "TEST_ENTITY")
    private class TestEntity
    {
        @Basic
        String dummyPayload;

        @Id
        @Column(name = "ORDER_ID", nullable = false)
        @GeneratedValue(strategy = GenerationType.SEQUENCE)
        long entityId;

        public TestEntity(String payload)
        {
            dummyPayload = payload;
        }
    }
}
