package org.hibernate.bugs.hash;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.Before;
import org.junit.Test;

public class HashTest {
	
	private SessionFactory sf;
	
	@Before
	public void setup() {
		StandardServiceRegistryBuilder srb = new StandardServiceRegistryBuilder()
			// Add in any settings that are specific to your test. See resources/hibernate.properties for the defaults.
			.applySetting( "hibernate.show_sql", "true" )
			.applySetting( "hibernate.format_sql", "true" )
			.applySetting( "hibernate.hbm2ddl.auto", "update" );

		Metadata metadata = new MetadataSources( srb.build() )
		// Add your entities here.
			.addAnnotatedClass( Top.class )
			.addAnnotatedClass( Middle.class )
			.addAnnotatedClass( Bottom.class )
			.buildMetadata();

		sf = metadata.buildSessionFactory();
		setupFixture();
	}
	
	@Test
	public void testMerge() throws Exception {
		final Session hibSession1 = sf.openSession();
		Transaction t1 = hibSession1.beginTransaction();

		// Lookup a top entity
		Top top = hibSession1.createQuery("from Top where name = :name", Top.class).setParameter("name", "Top 1").uniqueResult();
		System.out.println("Top: " + top.getId() + ", " + top.getName());

		// Add one middle entity with a single bottom entity to the top entity
		Middle m1 = new Middle();
		m1.setTop(top); top.addMiddle(m1);
		Bottom b1 = new Bottom();
		b1.setMiddle(m1); m1.addBottom(b1); b1.setType(0); b1.setNote("Bottom 1");
		
		// update the top entity
		hibSession1.merge(top);
		
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
		hibSession1.persist(top);
		
		t1.commit();
		hibSession1.close();
	}

	private void setupFixture() {
		final Session hibSession = sf.openSession();

		Transaction tx = hibSession.beginTransaction();

		// Create a top
		Top t1 = new Top();
		t1.setName("Top 1");
		hibSession.persist(t1);

		tx.commit();
		hibSession.close();
	}	

}
