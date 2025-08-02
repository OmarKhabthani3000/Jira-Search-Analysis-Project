package org.hibernate.test.event.hhh_3317;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import org.hibernate.event.PreUpdateEvent;
import org.hibernate.event.PreUpdateEventListener;
import org.hibernate.junit.functional.FunctionalTestCase;

/**
 * @author Martin Backhaus
 * @since 2008.06.03 08:40
 */
public class HHH_3317_Test extends FunctionalTestCase 
{

    public HHH_3317_Test(final String string)
    {
        super(string);
    }
    
    public void configure(final Configuration cfg) 
    {
        final PreUpdateEventListener[] stack = { new HibernateListener()};
        cfg.getEventListeners().setPreUpdateEventListeners(stack);
    }
    

    public String[] getMappings()
    {
        return new String[] { "event/hhh_3317/HHH_3317.hbm.xml" };
    }

//    public boolean appliesTo(final Dialect dialect) 
//    {
//    Environment.USE_GET_GENERATED_KEYS

//        dialect.get
//        return !dialect.supportsIdentityColumns();
//    }

  


    public void testIdentityColumnGeneratedIds() 
    {
        Session s = openSession();
        HibernateListener.currentSession = s;
        
        // initial insert
        s.beginTransaction();
        AEntity myEntity = new AEntity();
        myEntity.setNotice("initial");
        s.save(myEntity);
        s.getTransaction().commit();

        s.close();
        s = openSession();
        HibernateListener.currentSession = s;

        // reload, change, update -> triggers listener -> insert history
        s.beginTransaction();
        myEntity = (AEntity) s.get(AEntity.class, myEntity.getId());
        myEntity.setNotice("changed");
        s.saveOrUpdate(myEntity);
        s.getTransaction().commit();
        
        s.close();
        s = openSession();
        HibernateListener.currentSession = s;

        // assert history was inserted
        s.beginTransaction();
        final List myHistory = s.createQuery("FROM HHH_3317_Test$History").list();
        assertEquals(1, myHistory.size());
        s.getTransaction().commit();
        
        s.close();
    }
    
    /**
     * @author Martin Backhaus
     * @since 2008.06.03 08:18
     */
//    @Entity
//    @Table(name = "HHH3317_AEntity")
    public static class AEntity
    {
        /**
         * Unique object identifier
         */
//        @Id
//        @GeneratedValue(strategy = GenerationType.AUTO)
        protected Long id = null;

//        @Column(length = 32, name = "notice")
        protected String notice;

        protected String getNotice()
        {
            return notice;
        }

        protected void setNotice(final String notice)
        {
            this.notice = notice;
        }

        protected Long getId()
        {
            return id;
        }
    }
    
    /**
     * @author Martin Backhaus
     * @since 2008.06.03 08:23
     */
//    @Entity
//    @Table(name = "HHH3317_History")
    public static class History
    {
        /**
         * Unique object identifier
         */
//        @Id
//        @GeneratedValue(strategy = GenerationType.AUTO)
        protected Long id = null;

//        @Column(length = 32, name = "story")
        protected String story;

        protected Long getId()
        {
            return id;
        }

        protected String getStory()
        {
            return story;
        }

        protected void setStory(final String story)
        {
            this.story = story;
        }
    }
    
    /**
     * @author Martin Backhaus
     * @since 2008.06.03 08:34
     */
    public static class HibernateListener implements PreUpdateEventListener 
    {
        private static final long serialVersionUID = 1L;
        
        public static Session currentSession; 
        
        public boolean onPreUpdate(final PreUpdateEvent arg0)
        {
            final Object o = arg0.getEntity();

            // Normally testing of an Interface and then if an special annotation is at a changed 
            // property defined. Would be nice if 
            // arg0.getPersister().getClassMetadata().getPropertyAnnotations() : Annotation[]
            // exists ;o)
            if (o instanceof AEntity)
            {  
                
                final History h = new History();
                h.setStory("the history for AEntity #" + ((AEntity)o).getId());
                
//               final Session s = arg0.getSource().getFactory().getCurrentSession();
                currentSession.save(h);           
            }
            
            return false;
        }
    }    
}
