package ro.objects.test;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

public class Test {

	private static SessionFactory sessionFactory;

	public static void badCode() {
		Session sess = sessionFactory.openSession();
		Transaction tx = null;
		try {
			tx = sess.beginTransaction();
			sess.createSQLQuery("update system_parameter set parameter_value = 2011 where parameter_name = 'next_reporting_year'").executeUpdate();
			if ("".equals("")) {
				throw new Error("I made a boo boo!"); // This may be thrown by some 3rd party code you have no control over, and you may not be even aware that it may be thrown
			}
			tx.commit(); // This line is never reached, so the update should never be committed
		} catch (RuntimeException e) { // This doesn't catch java.lang.Errors
			if (tx != null) {
				tx.rollback(); // This line is also never reached
			}
			e.printStackTrace();
		} finally {
			sess.close();
		}
	}

	public static void goodCode() {
		Session sess = sessionFactory.openSession(); // This session will have the same underlying connection as the session from badCode, as it is probably the only connection in the pool
		Transaction tx = null;
		try {
			tx = sess.beginTransaction();
			// Do something, or nothing, with the session. It doesn't matter as long as the commit is reached. Let's try to do nothing.
			tx.commit(); // The commit also commits the changes made by badCode before closing the session, even though the to methods are not related in any way. They may even run in different threads.
		} catch (RuntimeException e) {
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
		} finally {
			sess.close();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		sessionFactory = new Configuration().configure("database.cfg.xml").buildSessionFactory();
		try {
			badCode();
		} catch (Error e) {
			e.printStackTrace();
		}
		goodCode();
		sessionFactory.close(); // Even if goodCode doesn't do a commit, closing the factory closes all the connections in the pool, and the Oracle driver performs commits on close.
	}
}
