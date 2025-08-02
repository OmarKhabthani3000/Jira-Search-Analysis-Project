package org.hibernate.bugs.hash;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;

public class HashTest {
	
	private SessionFactory sf;
	
	@Before
	public void setup() {
		Configuration cfg = new Configuration();
		cfg.configure("hibernate.xml");
		StandardServiceRegistry registry = new StandardServiceRegistryBuilder().applySettings(cfg.getProperties()).build();
		sf = cfg.buildSessionFactory(registry);
		setupFixture();
	}
	
	@Test
	public void testMerge() throws Exception {
		final Session hibSession1 = sf.openSession();
		Transaction t1 = hibSession1.beginTransaction();

		// Lookup a top entity
		Top top = (Top)hibSession1.createQuery("from Top where name = :name").setString("name", "Top 1").uniqueResult();
		System.out.println("Top: " + top.getId() + ", " + top.getName());

		// Add one middle entity with a single bottom entity to the top entity
		Middle m1 = new Middle();
		m1.setTop(top); top.addMiddle(m1);
		Bottom b1 = new Bottom();
		b1.setMiddle(m1); m1.addBottom(b1); b1.setType(0); b1.setNote("Bottom 1");
		
		// update the top entity
		hibSession1.update(top);
		
		t1.commit();
		hibSession1.close();
	}
	
	@Test
	public void testPersist() throws Exception {
		final Session hibSession1 = sf.openSession();
		Transaction t1 = hibSession1.beginTransaction();

		// Create a top entity
		Top top = new Top();
		top.setName("Top 2");
		
		// Add one middle entity with a single bottom entity to the top entity
		Middle m1 = new Middle();
		m1.setTop(top); top.addMiddle(m1);
		Bottom b1 = new Bottom();
		b1.setMiddle(m1); m1.addBottom(b1); b1.setType(0); b1.setNote("Bottom 1");
		
		// persist the top entity
		hibSession1.save(top);
		
		t1.commit();
		hibSession1.close();
	}

	private void setupFixture() {
		final Session hibSession = sf.openSession();

		Transaction tx = hibSession.beginTransaction();

		// Create a top
		Top t1 = new Top();
		t1.setName("Top 1");
		hibSession.save(t1);

		tx.commit();
		hibSession.close();
	}	

}
