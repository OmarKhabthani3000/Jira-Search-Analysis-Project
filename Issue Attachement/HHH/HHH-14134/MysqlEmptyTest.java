package zzz;

import org.junit.jupiter.api.Test;

import javax.annotation.ManagedBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@ManagedBean
public class MysqlEmptyTest extends EJBTestSetup {

    @PersistenceContext
    private EntityManager em;

    @Test
    public void findStuff() {
        em.createQuery("FROM Stuff s WHERE s.things IS EMPTY", Stuff.class)
                .getResultList();
    }
}