package org.hibernate.test;

import net.sf.hibernate.Session;
import net.sf.hibernate.Hibernate;

import java.io.Serializable;

/**
 * Copyright 1998-2005 BenefitPoint, Inc.
 * <p/>
 * <code>ManyToManySet</code> is very useful.
 * <p/>
 * TODO:add a javadoc description for this class!!!
 *
 * @author Tyson Norris; Last modified by: $Author$
 * @version $Revision$ $Date$
 *          <p/>
 *          svn url: $URL$
 */
public class ManyToManySet extends TestCase{
    public ManyToManySet(String arg) {
        super(arg);
    }

    public String[] getMappings() {
        return new String[] {
            "MasterDetail.hbm.xml"
        };
    }


    public void testIncomingOutgoing() throws Exception {

//		if (getDialect() instanceof HSQLDialect) return;

        Session s = openSession();
        Master master1 = new Master();
        Master master2 = new Master();
        Master master3 = new Master();
        s.save(master1);
        s.save(master2);
        s.save(master3);
        master1.addIncoming(master2);
        master2.addOutgoing(master1);
        master1.addIncoming(master3);
        master3.addOutgoing(master1);
        Serializable m1id = s.getIdentifier(master1);

        s.flush();
        s.connection().commit();
        s.close();

        s = openSession();

        Master temp = (Master) s.load(Master.class, m1id);

        //get and update the first outgoing object
        Master child=(Master)master1.getIncoming().iterator().next();
        child.setX(123);

        s.saveOrUpdateCopy(master1);

        s.flush();
        s.connection().commit();
        s.close();
    }
}
