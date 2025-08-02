/*
 * Created on Apr 11, 2005
 */
package org.hibernate.test.criteria;

import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.hibernate.test.TestCase;

/**
 * @author Brett Prucha
 */
public class CriteriaRestrictionMappingTest extends TestCase {

	public CriteriaRestrictionMappingTest(String str) {
		super(str);
	}

	public void testRestrictionMapping() {
		Session s = openSession();
		Transaction t = s.beginTransaction();
		
		Course course = new Course();
		course.setCourseCode("HIB");
		course.setDescription("Hibernate Training");
		s.save(course);
		
		Student student = new Student();
		student.setStudentNumber(1);
		student.setName("Brett Prucha");
		s.save(student);

		Enrolment enrolment = new Enrolment();
		enrolment.setStudentNumber(student.getStudentNumber());
		enrolment.setStudent(student);
		enrolment.setCourseCode(course.getCourseCode());
		enrolment.setCourse(course);
		enrolment.setSemester((short) 1);
		enrolment.setYear((short) 2005);
		s.save(enrolment);

		List list = s.createCriteria(Course.class)
		    .add(Restrictions.sqlRestriction(
		        "exists(" +
		        "   select 1 as exists_p" +
		        "   from Enrolment e" +
		        "   where e.courseCode = {this.courseCode} " +
				"         and e.year = 2005" +
		        ")"
	    )).list();

		assertEquals( list.size(), 1 );
		
		s.delete(enrolment);
		s.delete(student);
		s.delete(course);
		t.commit();
		s.close();
	}

	protected String[] getMappings() {
		return new String[] { "criteria/Enrolment.hbm.xml" };
	}

	public static Test suite() {
		return new TestSuite(CriteriaRestrictionMappingTest.class);
	}
}
