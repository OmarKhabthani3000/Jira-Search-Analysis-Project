package demo.hibernate.stateless.session;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.junit.jupiter.api.Test;

public class NativeQueryTest {

	// Test case https://hibernate.atlassian.net/browse/HHH-15914
	@Test
	public void test() {
		SessionFactory sessionFactory = MyHibernateUtil.createSessionFactory();
		StatelessSession session = sessionFactory.openStatelessSession();

		// Create table and insert record
		session.getTransaction().begin();
		session.createNativeMutationQuery("""
				CREATE TABLE persons (
					    id int,
					    name varchar(255)
				);""").executeUpdate();
		session.createNativeMutationQuery("INSERT INTO persons (id, name) VALUES (5, 'Erichsen');").executeUpdate();
		session.getTransaction().commit();
		
		
	    // Deprecated method call
		// Following the tutorial:
		// https://docs.jboss.org/hibernate/orm/6.1/userguide/html_single/Hibernate_User_Guide.html#sql
		// Example 556. Hibernate native query selecting all columns
		List<Object[]> persons = session.createNativeQuery("SELECT id, name FROM persons").list();
		// result = [{5, "Erichsen"}]
		Object[] firstResult = persons.get(0);
		assertEquals(5, firstResult[0]);
		assertEquals("Erichsen", firstResult[1]);
		
		// Now the non deprecated call 
		persons = session.createNativeQuery("SELECT id, name FROM persons", Object[].class).list();
		// Fails on java.lang.AssertionError when running as unit test.
		// at org.hibernate.query.results.dynamic.DynamicResultBuilderBasicStandard.<init>(DynamicResultBuilderBasicStandard.java:93)
		// at org.hibernate.query.results.Builders.scalar(Builders.java:134)
		firstResult = persons.get(0);
		assertEquals(5, firstResult[0]);
		assertEquals("Erichsen", firstResult[1]);
		
		session.close();
		sessionFactory.close();
	}
}
