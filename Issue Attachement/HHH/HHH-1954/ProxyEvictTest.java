package org.hibernate.test.proxyevict;

import java.io.Serializable;

import org.hibernate.Hibernate;
import org.hibernate.LazyInitializationException;
import org.hibernate.LockMode;
import org.hibernate.Transaction;
import org.hibernate.classic.Session;
import org.hibernate.test.TestCase;

public class ProxyEvictTest extends TestCase {

	public ProxyEvictTest(String name) {
		super(name);
	}

	protected String[] getMappings() {
		return new String[]{"proxyevict/Person.hbm.xml"};
	}
	
	public void testGetAndEvict () {
		Serializable id = insertData();
		
		Session s = openSession();
		Person loaded = (Person) s.get(Person.class,id);
		s.evict(loaded);
		assertFalse(s.contains(loaded));
		s.close();
	}
	
	public void testLoadAndEvict () {
		Serializable id = insertData();
		
		Session s = openSession();
		Person loaded = (Person) s.load(Person.class,id);
		s.evict(loaded);
		assertFalse("Loaded entity still in session.",s.contains(loaded));
		s.close();
	}
	
	public void testCascading_Close () {
		Serializable id = insertData();
		
		Session s = openSession();
		Person loaded = (Person) s.get(Person.class,id);
		s.close();
		
		assertNotNull(loaded);
		assertFalse(Hibernate.isInitialized(loaded.getFriend()));
		assertFalse(Hibernate.isInitialized(loaded.getChildren()));
		
		try {
			loaded.getChildren().iterator().next();
			fail("Collection is still fetchable");
		} catch (LazyInitializationException expected) {
		}
		
		try {
			loaded.getFriend().getName();
			fail("Proxy is still fetchable.");
		} catch (LazyInitializationException expected) {
		}
	}

	public void testCascading_Evict () {
		Serializable id = insertData();
		
		Session s = openSession();
		Person loaded = (Person) s.get(Person.class,id);
		s.evict(loaded);
		
		assertNotNull(loaded);
		assertFalse(Hibernate.isInitialized(loaded.getFriend()));
		assertFalse(Hibernate.isInitialized(loaded.getChildren()));
		
		try {
			loaded.getChildren().iterator().next();
			fail("Collection is still fetchable");
		} catch (LazyInitializationException expected) {
		}
		
		try {
			loaded.getFriend().getName();
			fail("Proxy is still fetchable.");
		} catch (LazyInitializationException expected) {
		}
		
		
		s.close();
	}

	private Serializable insertData() {
		Session s = openSession();
		Transaction t = s.beginTransaction();
		
		Person parent = new Person("p1");
		parent.addChild(new Person("c1"));
		parent.addChild(new Person("c2"));
		parent.addChild(new Person("c3"));
		
		parent.setFriend(new Person("f1"));
		
		Serializable id = s.save(parent);
		
		t.commit();
		s.close();
		
		return id;
	}

}
