//$Id$
package org.hibernate.test.criteria;

import java.util.List;

import junit.framework.Test;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.Oracle8iDialect;
import org.hibernate.junit.functional.FunctionalTestCase;
import org.hibernate.junit.functional.FunctionalTestClassTestSuite;

/**
 * Test for escaping wildcards in Oracle when using Criteria API.
 * 
 * @author Samuel Fleischle
 */
public class OracleMatchModeTest extends FunctionalTestCase {
	
	public OracleMatchModeTest(String str) {
		super(str);
	}

	/**
	 * {@inheritDoc}
	 */
	public String[] getMappings() {
		return new String[] { "criteria/Enrolment.hbm.xml" };
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean appliesTo(Dialect dialect) {
		return ( dialect instanceof Oracle8iDialect );
	}

	/**
	 * {@inheritDoc}
	 */
	public void configure(Configuration cfg) {
		super.configure( cfg );
		cfg.setProperty( Environment.USE_QUERY_CACHE, "true" );
		cfg.setProperty( Environment.CACHE_REGION_PREFIX, "criteriaquerytest" );
		cfg.setProperty( Environment.USE_SECOND_LEVEL_CACHE, "true" );
		cfg.setProperty( Environment.GENERATE_STATISTICS, "true" );
	}

	public static Test suite() {
		return new FunctionalTestClassTestSuite( OracleMatchModeTest.class );
	}

	public void testEscapeWildcards() {
		Session session = openSession();
		Transaction t = session.beginTransaction();
		Course c1 = new Course();
		c1.setCourseCode( "course-1" );
		c1.setDescription( "_1%" );
		Course c2 = new Course();
		c2.setCourseCode( "course-2" );
		c2.setDescription( "_2%" );
		Course c3 = new Course();
		c3.setCourseCode( "course-3" );
		c3.setDescription( "control" );
		session.persist( c1 );
		session.persist( c2 );
		session.persist( c3 );
		session.flush();
		session.clear();

		// finds all courses which have a description starting with '_'
		Criteria crit = session.createCriteria( Course.class );
		crit.add( Restrictions.like( "description", "_", MatchMode.START ) );
		List result = crit.list();
		assertEquals( 2, result.size() );
		
		// finds all courses which have a description ending with '%'
		crit = session.createCriteria( Course.class );
		crit.add( Restrictions.like( "description", "%", MatchMode.END ) );
		result = crit.list();
		assertEquals( 2, result.size() );

		session.createQuery( "delete Course" ).executeUpdate();
		t.commit();
		session.close();
	}

}

