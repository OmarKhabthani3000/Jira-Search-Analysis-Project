package org.hibernate.test.criteria;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.testing.DialectChecks;
import org.hibernate.testing.RequiresDialectFeature;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Test;

/**
 * @author PHaroZ
 */
@RequiresDialectFeature(DialectChecks.SupportsSequences.class)
public class CriteriaAdvancedJoinTest extends BaseCoreFunctionalTestCase {
	@Override
	public String[] getMappings() {
		return new String[] { "criteria/Enrolment.hbm.xml" };
	}

	@Test
	public void testSES() {
		Session session = openSession();
		Transaction t = session.beginTransaction();

		Course course = new Course();
		course.setCourseCode( "HIBA" );
		course.setDescription( "Hibernate Advanced" );

		Student pharoz = new Student();
		pharoz.setName( "PHaroZ" );
		pharoz.setStudentNumber( 888L );
		pharoz.setPreferredCourse( course );

		Enrolment enrolment = new Enrolment();
		enrolment.setCourse( course );
		enrolment.setCourseCode( course.getCourseCode() );
		enrolment.setSemester( (short) 2 );
		enrolment.setYear( (short) 2012 );
		enrolment.setStudent( pharoz );
		enrolment.setStudentNumber( pharoz.getStudentNumber() );

		session.persist( course );
		session.persist( pharoz );
		session.persist( enrolment );

		session.flush();
		session.clear();

		// search on a component property
		List results = session.createCriteria( Enrolment.class )
				.createAlias( "course", "c", JoinType.INNER_JOIN, Restrictions.eq( "courseCode", "HIBA" ) )
				.createAlias( "student", "s", JoinType.INNER_JOIN, Restrictions.eq( "name", "PHaroZ" ) ).list();

		assertEquals( 1, results.size() );

		t.commit();
		session.close();

	}

}
