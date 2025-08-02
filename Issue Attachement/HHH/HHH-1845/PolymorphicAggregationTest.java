// $Id: WithClauseTest.java 9335 2006-02-24 22:10:26Z steveebersole $
package org.hibernate.test.hql;

import org.hibernate.test.TestCase;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.HibernateException;
import org.hibernate.QueryException;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Implementation of WithClauseTest.
 *
 * @author Steve Ebersole
 */
public class PolymorphicAggregationTest extends TestCase {

	public PolymorphicAggregationTest(String name) {
		super( name );
	}

	protected String[] getMappings() {
		return new String[] { "hql/DomesticAnimalForAggregateTest.hbm.xml" };
	}

	public static Test suite() {
		return new TestSuite( PolymorphicAggregationTest.class );
	}

	protected void configure(Configuration cfg) {
		super.configure( cfg );
	}

	public void testPolymorphicCount() {
		TestData data = new TestData();
		data.prepare();

		Session s = openSession();
		Transaction txn = s.beginTransaction();

	
		// count
		long count = ( Long ) ( s.createQuery( "Select count (distinct id) from org.hibernate.test.hql.Animal" )
			.list()
			.get( 0 ) );

		assertTrue( "aggregate inheritance count failed", count == 3 );

		txn.commit();
		s.close();

		data.cleanup();
	}

	private class TestData {
		public void prepare() {
			Session session = openSession();
			Transaction txn = session.beginTransaction();

			Cat garfield = new Cat();
			Cat miam = new Cat();
			
			Dog scooby = new Dog();
			
			Human me = new Human();

			session.save( garfield );
			session.save( miam );
			session.save( scooby );
			session.save( me );

			txn.commit();
			session.close();
		}

		public void cleanup() {
			Session session = openSession();
			Transaction txn = session.beginTransaction();

			session.createQuery( "delete Animal" ).executeUpdate();
			txn.commit();
			session.close();
		}
	}
}
