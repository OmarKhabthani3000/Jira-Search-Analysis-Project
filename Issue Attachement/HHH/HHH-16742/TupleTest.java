package org.hibernate.bugs.tuples;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.Before;
import org.junit.Test;

import jakarta.persistence.Tuple;
import jakarta.persistence.TupleElement;

public class TupleTest {
	
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
			.addAnnotatedClass( TestEntity.class )
			.buildMetadata();

		sf = metadata.buildSessionFactory();
		setupFixture();
	}
	
	@Test
	public void testNormal() throws Exception {
		final Session hibSession = sf.openSession();
		Transaction t = hibSession.beginTransaction();

		// Get a list of tuples
		List<Tuple> list = hibSession.createQuery("select name as Name, value as Value from TestEntity", Tuple.class).list();
		
		// Print the returned tuples
		for (Tuple tuple: list) {
			for (TupleElement<?> e: tuple.getElements()) {
				System.out.println(e.getAlias() + ": " + tuple.get(e));
			}
		}
		
		t.commit();
		hibSession.close();
	}
	
	@Test
	public void testDuplicates() throws Exception {
		final Session hibSession = sf.openSession();
		Transaction t = hibSession.beginTransaction();

		// Get a list of tuples
		List<Tuple> list = hibSession.createQuery("select name as Reference, name as Name, value as Value from TestEntity", Tuple.class).list();
		
		// Print the returned tuples
		for (Tuple tuple: list) {
			for (TupleElement<?> e: tuple.getElements()) {
				System.out.println(e.getAlias() + ": " + tuple.get(e));
			}
		}
		
		t.commit();
		hibSession.close();
	}
	
	private void setupFixture() {
		final Session hibSession = sf.openSession();

		Transaction tx = hibSession.beginTransaction();

		// saving fixture
		{
			// Clear up everything
			hibSession.createMutationQuery("delete TestEntity").executeUpdate();
						
			// Create two entities
			TestEntity t1 = new TestEntity();
			t1.setName("Name 1");
			t1.setValue("Value 1");
			hibSession.persist(t1);
			
			TestEntity t2 = new TestEntity();
			t2.setName("Name 2");
			t2.setValue("Value 2");
			hibSession.persist(t2);
		}

		tx.commit();
		hibSession.close();
	}	

}
