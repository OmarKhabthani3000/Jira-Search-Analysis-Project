/*
 * $Id$
 */
package org.hibernate.test.fetchProxy;

import java.io.Serializable;

import net.sf.hibernate.FetchMode;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.LazyInitializationException;
import net.sf.hibernate.Session;
import net.sf.hibernate.expression.Expression;

import org.hibernate.test.TestCase;

/**
 * Test the proxies.
 * 
 * @author Maarten Winkels
 * @version $Revision$
 */
public class EagerTest extends TestCase {

    public EagerTest(String x) {
        super(x);
    }

    protected String[] getMappings() {
        return new String[]{"fetchProxy/Eager.hbm.xml"};
    }

    public final void testBothSet () throws HibernateException {
        Session s;
        Main main = new Main();
        main.setOne(new Proxied("first"));
        main.setTwo(new Proxied("second"));
        s = openSession();
        Serializable id = s.save(main);
        s.flush();
        s.close();
        
        s = openSession();
        Main persisted = (Main) s.get(Main.class,id);
        s.close();
        
        assertEquals("first",persisted.getOne().getName());
        assertEquals("second",persisted.getTwo().getName());
    }

    public final void testTwoNull () throws HibernateException {
        Session s;
        Main main = new Main();
        main.setOne(new Proxied("first"));
        s = openSession();
        Serializable id = s.save(main);
        s.flush();
        s.close();
        
        s = openSession();
        Main persisted = (Main) s.get(Main.class,id);
        s.close();
        
        assertNull(persisted.getTwo());
        assertNotNull(persisted.getOne());
        assertEquals("first",persisted.getOne().getName());
    }

    public final void testOneNull () throws HibernateException {
        Session s;
        Main main = new Main();
        main.setTwo(new Proxied("second"));
        s = openSession();
        Serializable id = s.save(main);
        s.flush();
        s.close();
        
        s = openSession();
        Main persisted = (Main) s.get(Main.class,id);
        s.close();
        
        assertNotNull(persisted.getTwo());
        assertNull(persisted.getOne());
        assertEquals("second",persisted.getTwo().getName());
    }
    
    public final void testCriteria () throws HibernateException {
        Session s;
        Main main = new Main();
        main.setOne(new Proxied("first"));
        main.setTwo(new Proxied("second"));
        s = openSession();
        Serializable id = s.save(main);
        s.flush();
        s.close();
        
        s = openSession();
        Main persisted = (Main) s.createCriteria(Main.class)
            .add(Expression.eq("id",id))
            .setFetchMode("second",FetchMode.EAGER)
            .uniqueResult();
        s.close();
        
        assertEquals("first",persisted.getOne().getName());
        assertEquals("second",persisted.getTwo().getName());
    }

    public final void testHQL () throws HibernateException {
        Session s;
        Main main = new Main();
        main.setOne(new Proxied("first"));
        main.setTwo(new Proxied("second"));
        s = openSession();
        Serializable id = s.save(main);
        s.flush();
        s.close();
        
        s = openSession();
        Main persisted = (Main) s.createQuery("from Main m where m.id = :id").setParameter("id",id).uniqueResult();
        s.close();
        
        try {
            persisted.getOne().getName();
            fail();
        } catch (LazyInitializationException expected) {
        }
        try {
            persisted.getTwo().getName();
            fail();
        } catch (LazyInitializationException expected) {
        }
        
        s = openSession();
        persisted = (Main) s.createQuery("from Main m join fetch m.one join fetch m.two where m.id = :id").setParameter("id",id).uniqueResult();
        s.close();
        
        assertEquals("first",persisted.getOne().getName());
        assertEquals("second",persisted.getTwo().getName());
    }
}
