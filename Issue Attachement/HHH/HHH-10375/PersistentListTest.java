/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Persistence;
import org.junit.Assert;
import org.junit.Test;

/**
 * Regression in 'org.hibernate.collection.internal.PersistenList.add(int index, Object value)'
 * not working with detached Entities.
 * 
 * Solution:
 * change line 318 from:
 *      if ( !isInitialized() || isConnectedToSession() ) {
 * to:
 * 		if ( !isOperationQueueEnabled() ) {
 *
 * @author Jürgen Simon
 */
public class PersistentListTest {

    @Entity
    public static class BaseEntity implements Serializable {

        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        private Long id;

        @OneToMany(cascade = CascadeType.ALL, mappedBy = "baseEntity", orphanRemoval = true)
        @OrderColumn(name = "ordered")
        private List<SubEntity> subEntities = new ArrayList<>();

        public List<SubEntity> getSubEntities() {
            return subEntities;
        }

        public void setSubEntities(List<SubEntity> subEntities) {
            this.subEntities = subEntities;
        }
    }

    @Entity
    public static class SubEntity implements Serializable {

        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        private Long id;

        @ManyToOne
        private BaseEntity baseEntity;

    }
    
    @Test
    public void persistentListTest() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("PersistentListTestPU");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        // create new BaseEntity
        BaseEntity be = new BaseEntity();
        be = em.merge(be);

        // persist BaseEntity
        tx.commit();

        // detache BaseEntity
        em.close();
        SubEntity se = new SubEntity();

        // add new SubEntity
        be.getSubEntities().add(se);
        Assert.assertTrue(be.getSubEntities().size() == 1);
        
        se = new SubEntity();
        

        // add new SubEntity at index
        be.getSubEntities().add(1, se);
        Assert.assertTrue(be.getSubEntities().size() == 2);
    }
    
}
