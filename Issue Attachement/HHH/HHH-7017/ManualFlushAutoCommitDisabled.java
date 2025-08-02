package org.hibernate.envers.test.integration.flush;

import org.hibernate.FlushMode;
import org.hibernate.ejb.Ejb3Configuration;
import org.hibernate.envers.RevisionType;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.test.entities.StrTestEntity;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.persistence.EntityManager;
import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.assertEquals;

/**
 * User: vvv
 * Date: 04.02.12
 * Time: 16:02
 */
public class ManualFlushAutoCommitDisabled extends AbstractFlushTest {

    private Integer id;

    public FlushMode getFlushMode() {
        return FlushMode.MANUAL;
    }

    //--------------------------------------------
    @Override
    public void configure(Ejb3Configuration cfg) {
        super.configure(cfg);
        cfg.setProperty("hibernate.connection.autocommit", "false");
    }
    //--------------------------------------------

    @BeforeClass(dependsOnMethods = "initFlush")
    public void initData() {
        // Revision 1
        EntityManager em = getEntityManager();
        em.getTransaction().begin();

        StrTestEntity fe = new StrTestEntity("x");
        em.persist(fe);

        em.getTransaction().commit();

        // No revision - we change the data, but do not flush the session
        em.getTransaction().begin();

        fe = em.find(StrTestEntity.class, fe.getId());
        fe.setStr("y");

        em.getTransaction().commit();

        // Revision 2 - only the first change should be saved
        em.getTransaction().begin();

        fe = em.find(StrTestEntity.class, fe.getId());
        fe.setStr("z");
        em.flush();

        fe = em.find(StrTestEntity.class, fe.getId());
        fe.setStr("z2");

        em.getTransaction().commit();

        //

        id = fe.getId();
    }

    @Test
    public void testRevisionsCounts() {
        assert Arrays.asList(1, 2).equals(getAuditReader().getRevisions(StrTestEntity.class, id));
    }

    @Test
    public void testHistoryOfId() {
        StrTestEntity ver1 = new StrTestEntity("x", id);
        StrTestEntity ver2 = new StrTestEntity("z", id);

        assert getAuditReader().find(StrTestEntity.class, id, 1).equals(ver1);
        assert getAuditReader().find(StrTestEntity.class, id, 2).equals(ver2);
    }

    @Test
    public void testCurrent() {
        assert getEntityManager().find(StrTestEntity.class, id).equals(new StrTestEntity("z", id));
    }

    @Test
    public void testRevisionTypes() {
        @SuppressWarnings({"unchecked"}) List<Object[]> results =
                getAuditReader().createQuery()
                        .forRevisionsOfEntity(StrTestEntity.class, false, true)
                        .add(AuditEntity.id().eq(id))
                        .getResultList();

        assertEquals(results.get(0)[2], RevisionType.ADD);
        assertEquals(results.get(1)[2], RevisionType.MOD);
    }
}
