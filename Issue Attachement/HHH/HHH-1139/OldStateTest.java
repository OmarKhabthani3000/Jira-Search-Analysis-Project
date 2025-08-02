package org.hibernate.test.interceptor;

import java.io.FileInputStream;
import java.util.Properties;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.hibernate.LockMode;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.event.PreUpdateEvent;
import org.hibernate.event.PreUpdateEventListener;
import org.hibernate.test.TestCase;

/**
 * @author Gabriel Handford
 */
public class OldStateTest extends TestCase implements PreUpdateEventListener {
    
    public OldStateTest(String str) {
        super(str);        
    }
    
    /**
    // This is to get it to work for me in standalone.
    public Properties getExtraProperties() {
        try {
            Properties props = new Properties();
            props.load(new FileInputStream("etc/hibernate.properties"));            
            return props;
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }*/
    
    protected void configure(Configuration cfg) {        
        cfg.getEventListeners().setPreUpdateEventListeners(new PreUpdateEventListener[] { this });
    }
    
    public void testOldState() {
        User u = new User("Gavin", "nivag");   
                
        Session s = openSession();
        Transaction t = s.beginTransaction();             
        s.save(u);        
        t.commit();
        s.close();
        
        s = openSession();
        t = s.beginTransaction();
        
        // Reloading the user, or calling lock will
        // cause the old state to be set correctly
        //u = (User) s.get(User.class, "Gavin");
        //s.lock(u, LockMode.NONE);
        
        u.setPassword("blah");        
        s.update(u);
        t.commit();
        s.close();         
    }          
    
    protected String[] getMappings() {
        return new String[] { "interceptor/User.hbm.xml" };
    }

    public static Test suite() {
        return new TestSuite(OldStateTest.class);
    }
    
    public boolean onPreUpdate(PreUpdateEvent event) {
        assertNotNull(event.getOldState());        
        return false;
    }

}

