/*
 * Created on 12/07/2005
 */
package org.hibernate.test.selectjoin;

import java.util.*;

import junit.framework.*;

import org.hibernate.*;
import org.hibernate.test.TestCase;

public class SelectJoinTest extends TestCase {

    public void testSelectJoin() throws Exception {
        Session s;
        Transaction tx;
        s = openSession();
        tx = s.beginTransaction();

        Zoo zoo = new Zoo();
        s.persist(zoo);
        
        Animal a = new Animal();
        a.setZoo(zoo);        
        s.persist(a);
        
        tx.commit();
        s.close();

        s = openSession();
        tx = s.beginTransaction();
        
        Query q = s.createQuery("SELECT DISTINCT a.zoo FROM " + Animal.class.getName() + " AS a WHERE a.zoo IS NOT NULL ");
        List list = q.list();
        
        for (Iterator i = list.iterator(); i.hasNext();){
            assertTrue("Zoo instances not returned in query", i.next() instanceof Zoo);
        }

        tx.commit();
        s.close();
    }
    
    public void testSelectJoin2() throws Exception {
        Session s;
        Transaction tx;
        s = openSession();
        tx = s.beginTransaction();

        Zoo zoo = new Zoo();
        s.persist(zoo);
        
        Animal a = new Animal();
        a.setZoo(zoo);        
        s.persist(a);
        
        tx.commit();
        s.close();

        s = openSession();
        tx = s.beginTransaction();
        
        Query q = s.createQuery("SELECT DISTINCT a.zoo2 FROM " + Animal.class.getName() + " AS a WHERE a.zoo2 IS NOT NULL ");
        List list = q.list();
        
        for (Iterator i = list.iterator(); i.hasNext();){
            assertTrue("Zoo instances not returned in query", i.next() instanceof Zoo);
        }

        tx.commit();
        s.close();
    }
    
    public SelectJoinTest(String x) {
        super(x);
    }

    public static Test suite() {
        return new TestSuite(SelectJoinTest.class);
    }

    protected String[] getMappings() {
        return new String[] {
            "selectjoin/mapping.hbm.xml"
        };
    }
}
