package org.wfp.rita.test.hibernate;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.type.OneToOneType;
import org.wfp.rita.test.base.HibernateTestBase;

/**
 * A bidirectional one-to-one mapping, where the owning side's column
 * is also in the primary key, fails to configure, possibly because 
 * {@link OneToOneType#getColumnSpan(org.hibernate.engine.Mapping)}
 * returns zero instead of the actual number of columns used by the
 * join.
 * 
 * When the foreign key column used is not also part of the primary key,
 * or the composite primary key contains only a single column, then the
 * mapping doesn't fail.
 * 
<code>
org.hibernate.MappingException: broken column mapping for: journeyComment.id of: org.wfp.rita.test.hibernate.HibernateAnnotationMappingOneToOneTest$Journey
    at org.hibernate.persister.entity.AbstractPropertyMapping.initPropertyPaths(AbstractPropertyMapping.java:143)
    at org.hibernate.persister.entity.AbstractPropertyMapping.initIdentifierPropertyPaths(AbstractPropertyMapping.java:206)
    at org.hibernate.persister.entity.AbstractPropertyMapping.initPropertyPaths(AbstractPropertyMapping.java:181)
    at org.hibernate.persister.entity.AbstractEntityPersister.initOrdinaryPropertyPaths(AbstractEntityPersister.java:1725)
    at org.hibernate.persister.entity.AbstractEntityPersister.initPropertyPaths(AbstractEntityPersister.java:1755)
    at org.hibernate.persister.entity.AbstractEntityPersister.postConstruct(AbstractEntityPersister.java:2932)
    at org.hibernate.persister.entity.SingleTableEntityPersister.<init>(SingleTableEntityPersister.java:431)
    at org.hibernate.persister.PersisterFactory.createClassPersister(PersisterFactory.java:84)
    at org.hibernate.impl.SessionFactoryImpl.<init>(SessionFactoryImpl.java:267)
    at org.hibernate.cfg.Configuration.buildSessionFactory(Configuration.java:1341)
    at org.hibernate.cfg.AnnotationConfiguration.buildSessionFactory(AnnotationConfiguration.java:867)
    at org.wfp.rita.test.base.HibernateTestBase.buildSessionFactory(HibernateTestBase.java:93)
    at org.wfp.rita.test.base.HibernateTestBase.setUp(HibernateTestBase.java:106)
    at junit.framework.TestCase.runBare(TestCase.java:128)
    at junit.framework.TestResult$1.protect(TestResult.java:106)
    at junit.framework.TestResult.runProtected(TestResult.java:124)
    at junit.framework.TestResult.run(TestResult.java:109)
    at junit.framework.TestCase.run(TestCase.java:120)
    at junit.framework.TestSuite.runTest(TestSuite.java:230)
    at junit.framework.TestSuite.run(TestSuite.java:225)
    at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.runTests(RemoteTestRunner.java:478)
    at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.run(RemoteTestRunner.java:344)
    at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.main(RemoteTestRunner.java:196)
</code>
 * 
 * @see http://opensource.atlassian.com/projects/hibernate/browse/HHH-
 * 
 * Change HibernateTestBase to org.hibernate.test.annotations.TestCase to
 * run under Hibernate.
 *
 * @author Chris Wilson <chris+hibernate@aptivate.org>
 */
public class HibernateAnnotationMappingOneToOneTest
extends HibernateTestBase
{
    @Entity
    @Table(name="journey")
    private static class Journey
    {
        @Id
        Integer id;
        
        @OneToOne(mappedBy="journey")
        JourneyComment journeyComment;
    }

    @Entity
    @Table(name="journey_comment", uniqueConstraints={
        @UniqueConstraint(columnNames={"journey_id"})
        })
    private static class JourneyComment
    {
        private static class Id implements Serializable
        {
            @Column(name="journey_site_id")
            Integer journeySiteId;

            @Column(name="journey_id")
            Integer journeyId;
        }
        
        // Fields    
        @EmbeddedId
        Id id;
        
        @OneToOne
        /**
         * If you change the name of this column so that it's not also
         * included in the primary key, then the mapping doesn't fail
         */
        @JoinColumn(name="journey_id", insertable=false, updatable=false)
        Journey journey;
    }
    
    protected Class[] getMappings()
    {
        return new Class[]{Journey.class, JourneyComment.class};
    }

    public void testFailingMapping()
    {
        // do nothing, just initialize the mapping
    }
}
