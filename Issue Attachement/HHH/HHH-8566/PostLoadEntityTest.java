package org.hibernate.envers.test.integration.basic;

import static org.junit.Assert.*;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.PostLoad;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.envers.AuditReader;
import org.hibernate.envers.Audited;
import org.hibernate.envers.test.BaseEnversJPAFunctionalTestCase;
import org.hibernate.testing.TestForIssue;
import org.junit.Test;

@TestForIssue(jiraKey = "HHH-8566")
public class PostLoadEntityTest extends BaseEnversJPAFunctionalTestCase {
    
    @Entity
    @Table(name = "STR_TEST")
    static class StrTestEntity {
        @Id
        @GeneratedValue
        private Integer id;

        @Audited
        private String str;
        
        @Transient
        private String postLoadString;
        
        @PostLoad
        public void loadPostLoadString() {
            postLoadString = "I was loaded";
        }

        public StrTestEntity() {
            
        }

        public StrTestEntity(String str) {
            this.str = str;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getStr() {
            return str;
        }

        public void setStr(String str) {
            this.str = str;
        }

        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof StrTestEntity)) return false;

            StrTestEntity that = (StrTestEntity) o;

            if (id != null ? !id.equals(that.id) : that.id != null) return false;
            if (str != null ? !str.equals(that.str) : that.str != null) return false;

            return true;
        }

        public int hashCode() {
            int result;
            result = (id != null ? id.hashCode() : 0);
            result = 31 * result + (str != null ? str.hashCode() : 0);
            return result;
        }

        public String toString() {
            return "STE(id = " + id + ", str = " + str + ")";
        }

        public String getPostLoadString() {
            return postLoadString;
        }
    }
    
    @Override
    protected Class<?>[] getAnnotatedClasses() {
        return new Class<?>[]{StrTestEntity.class};
    }

    @Test
    public void testLoadRevisionCallsPostLoad() {
        EntityManager em = getEntityManager();

        // Revision 1
        em.getTransaction().begin();
        StrTestEntity testEntity = new StrTestEntity("Test");
        em.persist(testEntity);
        
        assertNull(testEntity.getPostLoadString());
        
        Integer testId = testEntity.getId();
        
        em.getTransaction().commit();
        em.close();
        
        em = getEntityManager();
        em.getTransaction().begin();
        StrTestEntity foundEntity = em.find(StrTestEntity.class, testEntity.getId());
        assertEquals("I was loaded", foundEntity.getPostLoadString());
        
        em.getTransaction().rollback();
        em.close();
        
        AuditReader reader = getAuditReader();
        List<Number> revisions = reader.getRevisions(StrTestEntity.class, testId);
        assertNotNull(revisions);
        assertEquals(1, revisions.size());
        Number revision = revisions.get(0);
        assertEquals(1, revision);
        
        StrTestEntity auditEntity = reader.find(StrTestEntity.class, testId, revision);
        
        assertEquals("I was loaded", auditEntity.getPostLoadString());
    }
}
