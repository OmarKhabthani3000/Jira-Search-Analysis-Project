//$Id: InterceptorTest.java 9912 2006-05-08 22:41:00Z max.andersen@jboss.com $
package org.hibernate.test.interceptor;

import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.test.TestCase;


/**
 * @author Gavin King
 */
public class InterceptorTest extends TestCase {
	
	public InterceptorTest(String str) {
		super(str);
	}

	public void testCollectionIntercept() {
		Session s = openSession( new CollectionInterceptor() );
		Transaction t = s.beginTransaction();
		User u = new User("Gavin", "nivag");
		s.persist(u);
		u.setPassword("vagni");
		t.commit();
		s.close();
		
		s = openSession();
		t = s.beginTransaction();
		u = (User) s.get(User.class, "Gavin");
		assertEquals( 2, u.getActions().size() );
		s.delete(u);
		t.commit();
		s.close();
	}	
	
	public void testPropertyIntercept() {
		Session s = openSession( new PropertyInterceptor() );
		Transaction t = s.beginTransaction();
		User u = new User("Gavin", "nivag");
		s.persist(u);
		u.setPassword("vagni");
		t.commit();
		s.close();
		
		s = openSession();
		t = s.beginTransaction();
		u = (User) s.get(User.class, "Gavin");
		assertNotNull( u.getCreated() );
		assertNotNull( u.getLastUpdated() );
		s.delete(u);
		t.commit();
		s.close();
	}	
	
	public void testStatefulIntercept() {
		final StatefulInterceptor statefulInterceptor = new StatefulInterceptor();
		Session s = openSession( statefulInterceptor );
		statefulInterceptor.setSession(s);
		
		Transaction t = s.beginTransaction();
		User u = new User("Gavin", "nivag");
		s.persist(u);
		u.setPassword("vagni");
		t.commit();
		s.close();
		
		s = openSession();
		t = s.beginTransaction();
		List logs = s.createCriteria(Log.class).list();
		assertEquals( 2, logs.size() );
		s.delete(u);
		s.createQuery( "delete from Log" ).executeUpdate();
		t.commit();
		s.close();
	}
	
	public void testInterceptorResets() {
		Session s = openSession( new ResetInterceptor() );
		Transaction t = s.beginTransaction();
		User u = new User("Josh", "test");
		s.persist(u);
		u.setPassword("nottest");
		t.commit();
		s.close();
		
		s = openSession();
		t = s.beginTransaction();
		u = (User) s.get(User.class, "Josh");
		assertEquals("test", u.getPassword());
		s.delete(u);
		t.commit();
		s.close();
	}	
	
	protected String[] getMappings() {
		return new String[] { "interceptor/User.hbm.xml" };
	}

	public static Test suite() {
		return new TestSuite(InterceptorTest.class);
	}

}

