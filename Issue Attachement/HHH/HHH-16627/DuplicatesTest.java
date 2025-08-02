package org.hibernate.bugs.duplicates;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DuplicatesTest {
	
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
			.addAnnotatedClass( Course.class )
			.addAnnotatedClass( Student.class )
			.addAnnotatedClass( Enrollment.class )
			.buildMetadata();

		sf = metadata.buildSessionFactory();
		setupFixture();
	}
	
	@Test
	public void duplicatesTest() throws Exception {
		final Session hibSession1 = sf.openSession();
		Transaction t1 = hibSession1.beginTransaction();
		
		// Lookup the student and the two courses
		Student s1 = hibSession1.createQuery("from Student where name = :name", Student.class).setParameter("name", "John").uniqueResult();
		System.out.println("Student: " + s1.getId() + ", " + s1.getName());
		Course c1 = hibSession1.createQuery("from Course where name = :name", Course.class).setParameter("name", "ENGL 101").uniqueResult();
		System.out.println("Course: " + c1.getId() + ", " + c1.getName());
		Course c2 = hibSession1.createQuery("from Course where name = :name", Course.class).setParameter("name", "BIOL 101").uniqueResult();
		System.out.println("Course: " + c2.getId() + ", " + c2.getName());
		
		// Enroll student to the two courses
		Enrollment e1 = new Enrollment();
		e1.setScore(10);
		e1.setStudent(s1); s1.addEnrollment(e1);
		e1.setCourse(c1); c1.addEnrollment(e1);
		
		Enrollment e2 = new Enrollment();
		e2.setScore(20);
		e2.setStudent(s1); s1.addEnrollment(e2);
		e2.setCourse(c2); c2.addEnrollment(e2);
		
		// update student
		hibSession1.merge(s1);
		
		t1.commit();
		hibSession1.close();
		
		// Check what have been stored in the database
		final Session hibSession2 = sf.openSession();
		Transaction t2 = hibSession2.beginTransaction();
		
		List<Enrollment> enrls = hibSession2.createQuery("from Enrollment e where e.student.name = :name", Enrollment.class)
				.setParameter("name", "John").list();
		System.out.println("Id,Student,Course,Score");
		for (Enrollment e: enrls)
			System.out.println(e.getId() + ", " + e.getStudent().getName() + "," + e.getCourse().getName() + "," + e.getScore());
		Assert.assertEquals(2, enrls.size());
		
		t2.commit();
		hibSession2.close();
	}
	
	private void setupFixture() {
		final Session hibSession = sf.openSession();

		Transaction tx = hibSession.beginTransaction();

		// saving fixture
		{
			// First create two courses
			Course c1 = new Course();
			c1.setName("ENGL 101");
			hibSession.persist(c1);
			Course c2 = new Course();
			c2.setName("BIOL 101");
			hibSession.persist(c2);

			// Second create a student
			Student s1 = new Student();
			s1.setName("John");
			hibSession.persist(s1);
		}

		tx.commit();
		hibSession.close();
	}	

}
