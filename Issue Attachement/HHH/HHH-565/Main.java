/*
 * Main.java
 *
 * Created on May 3, 2005, 9:42 AM
 */

package edu.ucsd.netDB;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import javax.transaction.SystemException;
import java.util.*;


/**
 *
 * @author Kevin
 */
public class Main {
    
    
    /** Creates a new instance of Main */
    public Main() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        Configuration cfg = new Configuration();
        cfg.addClass(User.class);
        cfg.addClass(Mail.class);
        SessionFactory sf = cfg.buildSessionFactory();
        Session sess = sf.openSession();
        Transaction tx = sess.beginTransaction();

        User u = (User) sess.load(User.class, new Integer(405305));
        
        sess.delete(u);
        
        tx.commit();

        sess.close();
        
        
    }
    
}
