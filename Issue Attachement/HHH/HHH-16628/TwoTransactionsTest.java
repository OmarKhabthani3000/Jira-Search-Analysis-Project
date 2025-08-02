package org.hibernate.bugs.twotrans;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.Before;
import org.junit.Test;

public class TwoTransactionsTest {
	
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
	public void twoTransactionsTestMerge() throws Exception {
		// Shared session
		final Session hibSession = sf.openSession();

		// FIRST TRANSACTION
		Transaction t1 = hibSession.beginTransaction();
		// Lookup the student and the two courses
		Student s1 = hibSession.createQuery("from Student where name = :name", Student.class).setParameter("name", "John").uniqueResult();
		Course c1 = hibSession.createQuery("from Course where name = :name", Course.class).setParameter("name", "ENGL 101").uniqueResult();
		Course c2 = hibSession.createQuery("from Course where name = :name", Course.class).setParameter("name", "BIOL 101").uniqueResult();
		
		// Enroll student to the two courses
		Enrollment e1 = new Enrollment();
		e1.setScore(10);
		e1.setStudent(s1); s1.addEnrollment(e1);
		e1.setCourse(c1); c1.addEnrollment(e1);
		
		Enrollment e2 = new Enrollment();
		e2.setScore(20);
		e2.setStudent(s1); s1.addEnrollment(e2);
		e2.setCourse(c2); c2.addEnrollment(e2);
		
		// merge student & commit
		hibSession.merge(s1);
		t1.commit();

		// SECOND TRANSACTION
		Transaction t2 = hibSession.beginTransaction();
		// Lookup a course and update credit information
		Course course = hibSession.createQuery("from Course where name = :name", Course.class).setParameter("name", "ENGL 101").uniqueResult();
		course.setCredit(3);
		// merge course & commit
		hibSession.merge(course);
		t2.commit();
		
		// Shared session is closed at the end
		hibSession.close();
	}
	
	@Test
	public void twoTransactionsTestPersistEach() throws Exception {
		// Shared session
		final Session hibSession = sf.openSession();

		// FIRST TRANSACTION
		Transaction t1 = hibSession.beginTransaction();
		// Lookup the student and the two courses
		Student s1 = hibSession.createQuery("from Student where name = :name", Student.class).setParameter("name", "John").uniqueResult();
		Course c1 = hibSession.createQuery("from Course where name = :name", Course.class).setParameter("name", "ENGL 101").uniqueResult();
		Course c2 = hibSession.createQuery("from Course where name = :name", Course.class).setParameter("name", "BIOL 101").uniqueResult();
		
		// Enroll student to the two courses, but now persist each enrollment individually
		Enrollment e1 = new Enrollment();
		e1.setScore(10);
		e1.setStudent(s1); s1.addEnrollment(e1);
		e1.setCourse(c1); c1.addEnrollment(e1);
		hibSession.persist(e1);
		
		Enrollment e2 = new Enrollment();
		e2.setScore(20);
		e2.setStudent(s1); s1.addEnrollment(e2);
		e2.setCourse(c2); c2.addEnrollment(e2);
		hibSession.persist(e2);
		
		// merge student & commit
		hibSession.merge(s1);
		t1.commit();

		// SECOND TRANSACTION
		Transaction t2 = hibSession.beginTransaction();
		// Lookup a course and update credit information
		Course course = hibSession.createQuery("from Course where name = :name", Course.class).setParameter("name", "ENGL 101").uniqueResult();
		course.setCredit(3);
		// merge course & commit
		hibSession.merge(course);
		t2.commit();
		
		// Shared session is closed at the end
		hibSession.close();
	}
	
	@Test
	public void twoTransactionsTestCreate() throws Exception {
		// Shared session
		final Session hibSession = sf.openSession();

		// FIRST TRANSACTION
		Transaction t1 = hibSession.beginTransaction();
		// Create a new student and lookup the two courses
		Student s1 = new Student();
		s1.setName("Betty");
		Course c1 = hibSession.createQuery("from Course where name = :name", Course.class).setParameter("name", "ENGL 101").uniqueResult();
		Course c2 = hibSession.createQuery("from Course where name = :name", Course.class).setParameter("name", "BIOL 101").uniqueResult();
		
		// Enroll student to the two courses
		Enrollment e1 = new Enrollment();
		e1.setScore(10);
		e1.setStudent(s1); s1.addEnrollment(e1);
		e1.setCourse(c1); c1.addEnrollment(e1);
		
		Enrollment e2 = new Enrollment();
		e2.setScore(20);
		e2.setStudent(s1); s1.addEnrollment(e2);
		e2.setCourse(c2); c2.addEnrollment(e2);
		
		// persist course & commit
		hibSession.persist(s1);
		t1.commit();

		// SECOND TRANSACTION
		Transaction t2 = hibSession.beginTransaction();
		// Lookup a course and update credit information
		Course course = hibSession.createQuery("from Course where name = :name", Course.class).setParameter("name", "ENGL 101").uniqueResult();
		course.setCredit(3);
		// merge course & commit
		hibSession.merge(course);
		t2.commit();
		
		// Shared session is closed at the end
		hibSession.close();
	}
	
	@Test
	public void twoTransactionsTestNewSession() throws Exception {
		// Session only for the first transaction
		final Session hibSession1 = sf.openSession();

		// FIRST TRANSACTION
		Transaction t1 = hibSession1.beginTransaction();
		// Lookup the student and the two courses
		Student s1 = hibSession1.createQuery("from Student where name = :name", Student.class).setParameter("name", "John").uniqueResult();
		Course c1 = hibSession1.createQuery("from Course where name = :name", Course.class).setParameter("name", "ENGL 101").uniqueResult();
		Course c2 = hibSession1.createQuery("from Course where name = :name", Course.class).setParameter("name", "BIOL 101").uniqueResult();
		
		// Enroll student to the two courses
		Enrollment e1 = new Enrollment();
		e1.setScore(10);
		e1.setStudent(s1); s1.addEnrollment(e1);
		e1.setCourse(c1); c1.addEnrollment(e1);
		
		Enrollment e2 = new Enrollment();
		e2.setScore(20);
		e2.setStudent(s1); s1.addEnrollment(e2);
		e2.setCourse(c2); c2.addEnrollment(e2);
		
		// merge student & commit, close the first session
		hibSession1.merge(s1);
		t1.commit();
		hibSession1.close();
		
		// Open a new session for the second transaction
		final Session hibSession2 = sf.openSession();
		// SECOND TRANSACTION
		Transaction t2 = hibSession2.beginTransaction();
		// Lookup a course and update credit information
		Course course = hibSession2.createQuery("from Course where name = :name", Course.class).setParameter("name", "ENGL 101").uniqueResult();
		course.setCredit(3);
		// merge course & commit, close the second session
		hibSession2.merge(course);
		t2.commit();
		hibSession2.close();
	}
	
	private void setupFixture() {
		final Session hibSession = sf.openSession();

		Transaction tx = hibSession.beginTransaction();

		// saving fixture
		{
			// Clear up everything
			hibSession.createMutationQuery("delete Enrollment").executeUpdate();
			hibSession.createMutationQuery("delete Course").executeUpdate();
			hibSession.createMutationQuery("delete Student").executeUpdate();
			
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
