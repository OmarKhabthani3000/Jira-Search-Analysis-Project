package org.hibernate.test.hhh1401;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.test.TestCase;

public class HHH1401Test extends TestCase
{

    protected String[] getMappings()
    {
        return new String[]{
          "hhh1401/Experimenter.hbm.xml",
          "hhh1401/GroupExperimenterMap.hbm.xml"
        };
    }
    
    public HHH1401Test(String str) {
        super(str);
    }

    public static Test suite() {
        return new TestSuite(HHH1401Test.class);
    }
    
    public void testNoExtraUpdatesOnMerge() throws Exception {
        
        // Create our data
        Session s = openSession();
        Transaction t = s.beginTransaction();
        Experimenter e = new Experimenter();
        e = (Experimenter) s.merge( e );
        s.flush();
        t.commit();
        s.close();

        // Do our problematic merge.
        s = openSession();
        t = s.beginTransaction();
        Experimenter e2 = (Experimenter) s.merge( e );
        s.flush();
        t.commit();
        s.close();
        
        assertTrue( "Version was incremented although we did nothing",
                e.getVersion().equals( e2.getVersion() )
                );

        s = openSession();
        t = s.beginTransaction();
        s.createQuery( "delete Experimenter" ).executeUpdate();
        t.commit();
        s.close();
        
    }
    
    public void testNoExtraUpdatesOnMergeWithCollection() throws Exception {
        
        // Create our data
        Session s = openSession();
        Transaction t = s.beginTransaction();
        Experimenter e = new Experimenter();
        GroupExperimenterMap map = new GroupExperimenterMap();
        e.getGroupExperimenterMap().add( map );
        map.setChild( e );
        s.save( e );
        s.flush();
        t.commit();
        s.close();

        // Do our problematic merge.
        s = openSession();
        t = s.beginTransaction();
        Experimenter e2 = (Experimenter) s.merge( e );
        s.flush();
        t.commit();
        s.close();
        
        assertTrue( "Version was incremented although we did nothing",
                e.getVersion().equals( e2.getVersion() )
                );

        s = openSession();
        t = s.beginTransaction();
        s.createQuery( "delete Experimenter" ).executeUpdate();
        t.commit();
        s.close();
        
    }
}
