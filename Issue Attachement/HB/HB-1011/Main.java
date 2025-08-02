/*
 * Created on 05.06.2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package de.jdufner;

import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Set;

import net.sf.hibernate.Session;
import net.sf.hibernate.SessionFactory;
import net.sf.hibernate.Transaction;
import net.sf.hibernate.cfg.Configuration;

/**
 * @author dufnjue
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class Main {
    
    public static void main(String[] args) throws Exception {
        SessionFactory sessionFactory = new Configuration()
            .addClass(Filiale.class)
            .addClass(BestellKopf.class)
            .addClass(BestellDetail.class)
            .buildSessionFactory();
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        
        
        Filiale primaryKeyFiliale = new Filiale();
        primaryKeyFiliale.setFilialnummer(1);
        
        BestellKopf primaryKey = new BestellKopf();
        primaryKey.setFiliale(primaryKeyFiliale);
        primaryKey.setBestelldatum(new GregorianCalendar(2004, 5, 5));
        
        BestellKopf bestellKopf = (BestellKopf) session.get(BestellKopf.class, primaryKey);
        System.out.println(bestellKopf);
        
        Set bestellDetails = bestellKopf.getBestellDetail();
        //System.out.println(bestellDetails);
        Iterator it = bestellDetails.iterator();
        while (it.hasNext()) {
            BestellDetail bestellDetail = (BestellDetail) it.next();
        }
        
        tx.commit();
        session.close();
    }

}
