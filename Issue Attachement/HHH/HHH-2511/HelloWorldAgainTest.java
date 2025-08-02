import java.util.List;

import junit.framework.TestCase;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.AnnotationConfiguration;

public class HelloWorldAgainTest extends TestCase {

	// ###################################################################
	// Firing up Hibernate
	// ###################################################################
	private static final SessionFactory sessionFactory;

	static {
		try {
			sessionFactory = new AnnotationConfiguration().configure()
					.buildSessionFactory();
		} catch (Throwable ex) {
			System.err.println("Initial SessionFactory creation failed: " + ex);
			throw new ExceptionInInitializerError(ex);
		}
	}

	// ###################################################################
	// Test cases
	// ###################################################################

	public void testGenerationOfModifiedProperty() throws Exception {
		// First unit
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		assertNotNull(session.save(new HelloWorldAgain()));
		assertNotNull(session.save(new HelloWorldAgain()));
		tx.commit();
		session.close();

		// Second unit
		Session newSession = sessionFactory.openSession();
		Transaction newTransaction = newSession.beginTransaction();

		List<HelloWorldAgain> list = newSession.createCriteria(
				HelloWorldAgain.class).list();

		assertTrue(list.size() >= 2);
		for (HelloWorldAgain hello : list) {
			assertTrue(hello.getId() > 0);
			assertNotNull("modified should NOT be null", hello.getModified());
		}

		newTransaction.commit();
		newSession.close();
	}

}
