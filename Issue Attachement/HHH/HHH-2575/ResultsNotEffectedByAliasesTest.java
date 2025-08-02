package org.hibernate.test.sql;

import java.util.List;

import junit.framework.Test;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.junit.functional.FunctionalTestCase;
import org.hibernate.junit.functional.FunctionalTestClassTestSuite;

public class ResultsNotEffectedByAliasesTest extends FunctionalTestCase {

	Organization ifa;
	Organization jboss;
	Person gavin;
	Employment emp;
	Employment emp2;
	
	public ResultsNotEffectedByAliasesTest(String x) {
		super( x );
	}

	public String[] getMappings() {
		return new String[] { "sql/General.hbm.xml" };
	}

	public void configure(Configuration cfg) {
		super.configure( cfg );
		cfg.setProperty( Environment.GENERATE_STATISTICS, "true" );
	}

	public static Test suite() {
		return new FunctionalTestClassTestSuite( ResultsNotEffectedByAliasesTest.class );
	}

	protected void prepareTest() throws Exception {

		Session s = openSession();
		Transaction t = s.beginTransaction();

		ifa = new Organization( "IFA" );
		jboss = new Organization( "JBoss" );
		gavin = new Person( "Gavin" );
		emp = new Employment( gavin, jboss, "AU" );
		emp2 = new Employment( gavin, ifa, "AU" );
		s.persist( ifa );
		s.persist( jboss );
		s.persist( gavin );
		s.persist( emp );
		s.persist( emp2 );
		
		t.commit();
		s.close();
	}

	protected void cleanupTest() throws Exception {

		Session s = openSession();
		Transaction t = s.beginTransaction();

		s.delete(emp2);
		s.delete(emp);
		s.delete(gavin);
		s.delete(jboss);
		s.delete(ifa);
		
		t.commit();
		s.close();
	}

	public void testResultsNotEffectedByUsingFullAliases() {

		Session s = openSession();
		Transaction t = s.beginTransaction();

		List list = s.createSQLQuery("SELECT e1.empid AS e1_employee, e2.empid AS e2_employee FROM EMPLOYMENT e1 JOIN EMPLOYMENT e2 ON e2.empid <> e1.empid").list();
		Object[] o = (Object[]) list.get(0);
		// they can't be equal because of the ON clause in the join
		assertFalse(
				"Employer IDs are the same when the SQL prevents that: " + o[0] + " == " + o[1],
				o[0] == o[1]);

		t.commit();
		s.close();
	}

	public void testResultsNotEffectedByUsingPartialAliases() {

		Session s = openSession();
		Transaction t = s.beginTransaction();

		List list = s.createSQLQuery("SELECT e1.empid AS e1_employee, e2.empid FROM EMPLOYMENT e1 JOIN EMPLOYMENT e2 ON e2.empid <> e1.empid").list();
		Object[] o = (Object[]) list.get(0);
		// they can't be equal because of the ON clause in the join
		assertFalse(
				"Employer IDs are the same when the SQL prevents that: " + o[0] + " == " + o[1],
				o[0] == o[1]);

		t.commit();
		s.close();
	}

	public void testResultsNotEffectedByUsingNoAliases() {

		Session s = openSession();
		Transaction t = s.beginTransaction();

		List list = s.createSQLQuery("SELECT e1.empid, e2.empid FROM EMPLOYMENT e1 JOIN EMPLOYMENT e2 ON e2.empid <> e1.empid").list();
		Object[] o = (Object[]) list.get(0);
		// they can't be equal because of the ON clause in the join
		assertFalse(
				"Employer IDs are the same when the SQL prevents that: " + o[0] + " == " + o[1],
				o[0] == o[1]);

		t.commit();
		s.close();
	}
}
