package org.hibernate.test;

import java.io.Serializable;
import java.util.Set;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.LazyInitializationException;
import net.sf.hibernate.Session;

/**
 * TestCase to show bug with outer join and proxy. 
 * 
 * @author Maarten Winkels
 */
public class OuterJoinProxyTest extends TestCase {

    public OuterJoinProxyTest(String x) {
        super(x);
    }

    protected String[] getMappings() {
        return new String[]{"SelfReferent.hbm.xml"};
    }
    
    /**
     * Test shows that the <code>SelfReferent</code> class is proxied.
     * 
     * @throws HibernateException if something goes wrong.
     */
    public void testProxy () throws HibernateException {
        Session s;
        s = openSession();
        SelfReferent inst = new SelfReferent();
        inst.setName("first instance");
        Serializable id = s.save(inst);
        s.flush();
        s.close();
        
        s = openSession();
        SelfReferent proxy = (SelfReferent) s.load(SelfReferent.class,id);
        s.flush();
        s.close();
        
        try {
            proxy.getName();
            fail("proxy: should throw LazyInitializationException");
        } catch (LazyInitializationException expected) {
        }
        
        s = openSession();
        s.delete(proxy);
        s.flush();
        s.close();
    }
    
    /**
     * Test shows that the <code>SelfReferent</code> class is loaded
     * on "get".
     * 
     * @throws HibernateException if something goes wrong.
     */
    public void testGet () throws HibernateException {
        Session s;
        s = openSession();
        SelfReferent inst = new SelfReferent();
        String name = "first instance";
        inst.setName(name);
        Serializable id = s.save(inst);
        s.flush();
        s.close();
        
        s = openSession();
        SelfReferent obj = (SelfReferent) s.get(SelfReferent.class,id);
        s.flush();
        s.close();
        
        assertEquals("name",name,obj.getName());
        
        s = openSession();
        s.delete(obj);
        s.flush();
        s.close();
    }
    
    /**
     * Test shows that the eager association is loaded correctly.
     * 
     * @throws HibernateException if something goes wrong.
     */
    public void testOuterJoinAssociation () throws HibernateException {
        Session s;
        s = openSession();
        SelfReferent inst = new SelfReferent();
        String name = "first instance";
        inst.setName(name);
        SelfReferent referee = new SelfReferent();
        String refereeName = "referee";
        referee.setName(refereeName);
        inst.setEager(referee);
        Serializable id = s.save(inst);
        s.flush();
        s.close();
        
        s = openSession();
        SelfReferent obj = (SelfReferent) s.get(SelfReferent.class,id);
        s.flush();
        s.close();
        
        assertEquals("name",name,obj.getName());
        assertEquals("eager.name",refereeName,obj.getEager().getName());
        
        s = openSession();
        s.delete(obj);
        s.flush();
        s.close();
    }
    
    /**
     * Test shows that the eager association is <b>not</b> loaded correctly if the
     * object to fetch was already loaded in the session as a proxy.
     * 
     * @throws HibernateException if something goes wrong.
     */
    public void testOuterJoinAssociationAfterGet () throws HibernateException {
        Session s;
        s = openSession();
        SelfReferent inst = new SelfReferent();
        String name = "first instance";
        inst.setName(name);
        SelfReferent referee = new SelfReferent();
        String refereeName = "referee";
        referee.setName(refereeName);
        inst.setEager(referee);
        Serializable id = s.save(inst);
        Long idOther = inst.getEager().getId();
        s.flush();
        s.close();
        
        s = openSession();
        // Get a proxy to the referee object in the session
        SelfReferent proxy = (SelfReferent) s.load(SelfReferent.class,idOther);
        SelfReferent obj = (SelfReferent) s.get(SelfReferent.class,id);
        s.flush();
        s.close();
        
        assertEquals("name",name,obj.getName());
        // This test should succeed but does not, due to bug.
        //assertEquals("eager.name",refereeName,obj.getEager().getName());
        
        // Now the proxy will be set instead of the eagerly fetched object...
        assertSame("proxy == eager",proxy,obj.getEager());
        // ... and thus it's properties are not readable.
        try {
            obj.getEager().getName();
            fail("Expecing LazyInitializationException due to bug.");
        } catch (LazyInitializationException expected) {
        }
        
        s = openSession();
        s.delete(obj);
        s.flush();
        s.close();
    }
    
    /**
     * Test shows that the association is lazily loaded.
     * 
     * @throws HibernateException if something goes wrong.
     */
    public void testLazyAssociation () throws HibernateException {
        Session s;
        s = openSession();
        SelfReferent inst = new SelfReferent();
        String name = "first instance";
        inst.setName(name);
        SelfReferent referee = new SelfReferent();
        String refereeName = "referee";
        referee.setName(refereeName);
        inst.setLazy(referee);
        Serializable id = s.save(inst);
        s.flush();
        s.close();
        
        s = openSession();
        SelfReferent obj = (SelfReferent) s.get(SelfReferent.class,id);
        s.flush();
        s.close();
        
        assertEquals("name",name,obj.getName());
        // The lazy property should refer a proxy.
        try {
            obj.getLazy().getName();
            fail("Expecing LazyInitializationException");
        } catch (LazyInitializationException expected) {
        }
        
        s = openSession();
        s.delete(obj);
        s.flush();
        s.close();
    }
    
    /**
     * Test to show that set is eagerly loaded.
     * 
     * @throws HibernateException if something goes wrong.
     */
    public void testEagerSet () throws HibernateException {
        Session s;
        s = openSession();
        SelfReferent inst = new SelfReferent();
        String name = "first instance";
        inst.setName(name);
        SelfReferent referee = new SelfReferent();
        String refereeName = "referee";
        referee.setName(refereeName);
        inst.add(referee);
        Serializable id = s.save(inst);
        s.flush();
        s.close();
        
        s = openSession();
        SelfReferent obj = (SelfReferent) s.get(SelfReferent.class,id);
        s.flush();
        s.close();
        
        assertEquals("name",name,obj.getName());
        Set group = obj.getGroup();
        assertEquals("#items",1,group.size());
        SelfReferent[] items = (SelfReferent[]) group.toArray(new SelfReferent[group.size()]);
        assertEquals("item.name",refereeName,items[0].getName());
        
        s = openSession();
        s.delete(obj);
        s.flush();
        s.close();
    }

    /**
     * Test both lazy an eager fetching on the same object.
     * 
     * @throws HibernateException if something goes wring.
     */
    public void testLazyAndEagerAssociation () throws HibernateException {
        Session s;
        s = openSession();
        SelfReferent inst = new SelfReferent();
        String name = "first instance";
        inst.setName(name);
        SelfReferent referee = new SelfReferent();
        String refereeName = "referee";
        referee.setName(refereeName);
        inst.setLazy(referee);
        inst.setEager(referee);
        Serializable id = s.save(inst);
        s.flush();
        s.close();
        
        s = openSession();
        SelfReferent obj = (SelfReferent) s.get(SelfReferent.class,id);
        s.flush();
        s.close();
        
        assertEquals("name",name,obj.getName());
        //The object does not have a proxy, since already fetched.
        assertEquals("lazy.name",refereeName,obj.getEager().getName());
        
        // This test should succeed but does not, due to bug.
        assertEquals("eager.name",refereeName,obj.getEager().getName());
                
        s = openSession();
        s.delete(obj);
        s.flush();
        s.close();
    }
    
    /**
     * Test to show that if an object is refered to twice, first through
     * a lazy association and then through a eager assocoation, the object
     * is not eagerly fetched. More precise, 
     * 
     * @throws HibernateException if something goes wrong.
     */
    public void testLazyAndEagerAssociationThroughSet () throws HibernateException {
        Session s;
        s = openSession();
        SelfReferent inst = new SelfReferent();
        String name = "first instance";
        inst.setName(name);
        SelfReferent referee = new SelfReferent();
        String refereeName = "referee";
        referee.setName(refereeName);
        inst.setLazy(referee);
        SelfReferent link = new SelfReferent();
        String linkName = "link";
        link.setName(linkName);
        link.setEager(referee);
        link.setLazy(referee);
        inst.add(link);
        Serializable id = s.save(inst);
        s.flush();
        s.close();
        
        s = openSession();
        SelfReferent obj = (SelfReferent) s.get(SelfReferent.class,id);
        s.flush();
        s.close();
        
        assertEquals("name",name,obj.getName());
        //The object should have a proxy.
        SelfReferent proxy = obj.getLazy();
        try {
            proxy.getName();
            fail("Expecting LazyInitializationException");
        } catch (LazyInitializationException expected) {
        }
        
        // The item in the set should be reachable.
        Set group = obj.getGroup();
        assertEquals("#items",1,group.size());
        SelfReferent item = (SelfReferent) group.toArray()[0];
        assertEquals("item.name",linkName,item.getName());
        try {
            item.getLazy().getName();
            fail("Expecing LazyInitializationException due to bug.");
        } catch (LazyInitializationException expected) {
        }
        
        // This test should succeed but does not, due to bug.
        //assertEquals("item.eager.name",refereeName,item.getEager().getName());
        
        // The lazy and the other object are the same.
        assertSame("proxy == eager",proxy,item.getEager());
        // ... and thus it's properties are not readable.
        try {
            item.getEager().getName();
            fail("Expecing LazyInitializationException due to bug.");
        } catch (LazyInitializationException expected) {
        }
        
        s = openSession();
        s.delete(obj);
        s.flush();
        s.close();
    }
}