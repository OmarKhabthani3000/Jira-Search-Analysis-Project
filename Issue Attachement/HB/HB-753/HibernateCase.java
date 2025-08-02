/*
 * Created on Feb 26, 2004 at 6:54:42 PM
 * 
 */
package hu.pmmedia.test.hibernate;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Query;
import net.sf.hibernate.Session;
import net.sf.hibernate.SessionFactory;
import net.sf.hibernate.Transaction;
import net.sf.hibernate.cfg.Configuration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 * @author f00060
 *  
 */
public class HibernateCase extends TestCase {
	private static SessionFactory sf = null;
	private static Log log = LogFactory.getLog(HibernateCase.class);
	static {
		try {
			sf = new Configuration().addClass(Parent.class).addClass(
					Child.class).buildSessionFactory();
		} catch (HibernateException e) {
			log.error("Error when init", e);
		}
	}
	public void testHibernate() {
		
		Transaction tx = null;
		Session session = null;
		try {
			session = sf.openSession();
			
			tx = session.beginTransaction();
			Query q = session
					.createQuery(" from Parent as parent "
							+    " where parent.instanz = :instaz and parent.vorname = :vorname");
			q.setString("instaz", "OIPC20");
			q.setString("vorname", "Christine");			
			
			List result = q.list();
			log.debug("Result list size: " + result.size());
			Parent actual = null;
			Child childactual = null;
			Query childQuery = null;
			for (Iterator i = result.iterator(); i.hasNext();) {
				actual = (Parent) i.next();
				log.debug("### Parent: " + actual);
				log.debug("### Children: " + actual.getChildren());
			}
			tx.commit();
		} catch (Exception ex) {
			log.error("Error when accessing db", ex);
		} finally {
			try {
				session.close();
				session = null;
			} catch (Exception ex) {
				log.error("Can not close the session", ex);
			}
		}
	}
}
