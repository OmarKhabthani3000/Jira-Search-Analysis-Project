package com.arvato.hibernate.entities;
// Generated 06.10.2006 20:21:36 by Hibernate Tools 3.2.0.beta6a


import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Home object for domain model class Tras001.
 * @see com.arvato.hibernate.entities.Tras001
 * @author Hibernate Tools
 */
@Stateless
public class Tras001Home extends com.arvato.hibernate.entities.ABaseDAO  {

    private static final Log log = LogFactory.getLog(Tras001Home.class);

    @PersistenceContext private EntityManager entityManager = com.arvato.hibernate.EntityManagerFactoryConfiguration.getCurrentEntityManager();
    
    public void persist(Tras001 transientInstance) {
        log.debug("persisting Tras001 instance");
        try {
            entityManager.persist(transientInstance);
            log.debug("persist successful");
        }
        catch (RuntimeException re) {
            log.error("persist failed", re);
            throw re;
        }
    }
    
    public void remove(Tras001 persistentInstance) {
        log.debug("removing Tras001 instance");
        try {
            entityManager.remove(persistentInstance);
            log.debug("remove successful");
        }
        catch (RuntimeException re) {
            log.error("remove failed", re);
            throw re;
        }
    }
    
    public Tras001 merge(Tras001 detachedInstance) {
        log.debug("merging Tras001 instance");
        try {
            Tras001 result = entityManager.merge(detachedInstance);
            log.debug("merge successful");
            return result;
        }
        catch (RuntimeException re) {
            log.error("merge failed", re);
            throw re;
        }
    }
    
    public Tras001 findById( Tras001Id id) {
        log.debug("getting Tras001 instance with id: " + id);
        try {
            Tras001 instance = entityManager.find(Tras001.class, id);
            log.debug("get successful");
            return instance;
        }
        catch (RuntimeException re) {
            log.error("get failed", re);
            throw re;
        }
    }
}

