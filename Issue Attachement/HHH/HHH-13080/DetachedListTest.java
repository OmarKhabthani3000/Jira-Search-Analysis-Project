

import org.hibernate.envers.Audited;
import org.hibernate.envers.test.BaseEnversJPAFunctionalTestCase;
import org.hibernate.envers.test.Priority;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class DetachedListTest extends BaseEnversJPAFunctionalTestCase {
    private Long alertId;
    private Long ruleName1Id;
    private Long ruleName2Id;

    @Entity
    @Audited
    public static class Alert {

        @Id
        @GeneratedValue
        private Long id;

        @ManyToMany
        private List<RuleName> ruleNames = new ArrayList<>();


        public List<RuleName> getRuleNames() {
            return ruleNames;
        }


    }

    @Entity
    @Audited
    public static class RuleName {

        @Id
        @GeneratedValue
        private Long id;

    }


    @Override
    protected Class<?>[] getAnnotatedClasses() {
        return new Class[]{Alert.class, RuleName.class};
    }

    @Test
    @Priority(10)
    public void initData() {
        EntityManager em = getEntityManager();

        RuleName ruleName1 = new RuleName();
        RuleName ruleName2 = new RuleName();

        Alert alert = new Alert();
        alert.getRuleNames().add(ruleName1);
        alert.getRuleNames().add(ruleName2);

        // Revision 1
        em.getTransaction().begin();
        em.persist(ruleName1);
        em.persist(ruleName2);
        em.persist(alert);
        em.getTransaction().commit();

        alertId = alert.id;
        ruleName1Id = ruleName1.id;
        ruleName2Id = ruleName2.id;
    }

    @Test
    @Priority(9)
    public void testRevisionsCounts() {
        Assert.assertEquals(Arrays.asList(1), getAuditReader().getRevisions(Alert.class, alertId));
        Assert.assertEquals(Arrays.asList(1), getAuditReader().getRevisions(RuleName.class, ruleName1Id));
        Assert.assertEquals(Arrays.asList(1), getAuditReader().getRevisions(RuleName.class, ruleName2Id));
    }

    @Test
    @Priority(8)
    public void testClearAndAddWithinTransactionDoesNotChangeAnything() {
        EntityManager em = getEntityManager();
        em.getTransaction().begin();
        Alert alert = em.find(Alert.class, alertId);
        List<RuleName> clone = new ArrayList<>(alert.getRuleNames());
        alert.getRuleNames().clear();
        alert.getRuleNames().addAll(clone);
        em.persist(alert);
        em.getTransaction().commit();

        Assert.assertEquals(Arrays.asList(1), getAuditReader().getRevisions(Alert.class, alertId));
        Assert.assertEquals(Arrays.asList(1), getAuditReader().getRevisions(RuleName.class, ruleName1Id));
        Assert.assertEquals(Arrays.asList(1), getAuditReader().getRevisions(RuleName.class, ruleName2Id));
    }

    @Test
    @Priority(7)
    @Ignore
    public void testClearAddOneWithinTransaction() {
        EntityManager em = getEntityManager();
        em.getTransaction().begin();
        Alert alert = em.find(Alert.class, alertId);
        List<RuleName> clone = new ArrayList<>(alert.getRuleNames());
        alert.getRuleNames().clear();
        alert.getRuleNames().add(clone.get(0));
        em.persist(alert);
        em.getTransaction().commit();

        Assert.assertEquals(Arrays.asList(1, 2), getAuditReader().getRevisions(Alert.class, alertId));
        Assert.assertEquals(Arrays.asList(1), getAuditReader().getRevisions(RuleName.class, ruleName1Id));
        Assert.assertEquals(Arrays.asList(1), getAuditReader().getRevisions(RuleName.class, ruleName2Id));
    }

    @Test
    @Priority(7)
    public void testClearAddDetachedOutsideTransaction() {
        EntityManager em = getEntityManager();
        em.getTransaction().begin();
        List<RuleName> clone = Arrays.asList(em.find(RuleName.class, ruleName1Id), em.find(RuleName.class, ruleName2Id));
        em.getTransaction().rollback();

        em.getTransaction().begin();
        Alert alert = em.find(Alert.class, alertId);
        alert.getRuleNames().clear();
        alert.getRuleNames().addAll(clone);
        em.persist(alert);
        em.getTransaction().commit();

        Assert.assertEquals(Arrays.asList(1), getAuditReader().getRevisions(Alert.class, alertId));
        Assert.assertEquals(Arrays.asList(1), getAuditReader().getRevisions(RuleName.class, ruleName1Id));
        Assert.assertEquals(Arrays.asList(1), getAuditReader().getRevisions(RuleName.class, ruleName2Id));
    }
}