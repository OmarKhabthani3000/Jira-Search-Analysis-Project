package org.hibernate.test.joinfetch;

import org.hibernate.ScrollableResults;
import org.hibernate.Transaction;
import org.hibernate.classic.Session;
import org.hibernate.test.TestCase;

public class EmptyFetchJoinScrollTest extends TestCase {

	public EmptyFetchJoinScrollTest(String name) {
		super(name);
	}

	protected String[] getMappings() {
		return new String[] { "joinfetch/UserGroup.hbm.xml" };
	}
	
	public void testFetchJoinEmptyScroll () {
		Session s = openSession();
		ScrollableResults results = s.createQuery("from User u join fetch u.groups").scroll();
		assertFalse(results.next());
		s.close();
	}

	public void testFetchJoinNotEmptyScroll () {
		Session s;
		s = openSession();
		Transaction t = s.beginTransaction();
		Group group = new Group("user");
		User user = new User("maarten");
		user.getGroups().put(group.getName(),group);
		s.save(group);
		s.save(user);
		t.commit();
		s.close();

		s = openSession();
		ScrollableResults results = s.createQuery("from User u join fetch u.groups").scroll();
		assertTrue(results.next());
		assertEquals(1,results.get().length);
		User found = (User) results.get(0);
		assertFalse(results.next());
		s.close();

		assertEquals("maarten",found.getName());
		assertEquals(1,found.getGroups().size());
	}
}
