/*
 * Created on Dec 1, 2003
 *  
 */
package gatt.example;

import gatt.util.hibernate.HibernateFilter;
import junit.framework.TestCase;
import net.sf.hibernate.Query;
import net.sf.hibernate.Session;
import net.sf.hibernate.SessionFactory;

/**
 * @author Gatt
 *  
 */
public class EntityATest extends TestCase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(EntityATest.class);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Constructor for EntityATest.
	 * 
	 * @param arg0
	 */
	public EntityATest(String arg0) {
		super(arg0);
	}

	public void testQuery() {

		try {
			SessionFactory sf = HibernateFilter.getSessionFactory();
			Session session = sf.openSession();

			// get the number of associations
			Query query = session.createQuery("select count(assoc) from EntityA entityA "
					+ "join entityA.association assoc");
			int count = ((Integer) query.iterate().next()).intValue();
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}