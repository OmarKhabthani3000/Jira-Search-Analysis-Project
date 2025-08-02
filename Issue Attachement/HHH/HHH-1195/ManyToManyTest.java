//$Id: ManyToManyTest.java 10976 2006-12-12 23:22:26Z steve.ebersole@jboss.com $
package org.hibernate.test.manytomany;

import junit.framework.Test;

import org.hibernate.*;
import org.hibernate.criterion.Restrictions;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.junit.functional.FunctionalTestCase;
import org.hibernate.junit.functional.FunctionalTestClassTestSuite;

import java.util.List;

/**
 * @author Gavin King
 */
public class ManyToManyTest extends FunctionalTestCase {
	User gavin;
	Group seam;
	Group hb;

	public ManyToManyTest(String str) {
		super(str);
	}

	public String[] getMappings() {
		return new String[] { "manytomany/UserGroup.hbm.xml" };
	}

	public void configure(Configuration cfg) {
		cfg.setProperty(Environment.USE_SECOND_LEVEL_CACHE, "false");
	}

	public static Test suite() {
		return new FunctionalTestClassTestSuite( ManyToManyTest.class );
	}

	public void prepareTest() {
		Session s = openSession();
		Transaction t = s.beginTransaction();
		gavin = new User("gavin", "jboss");
		seam = new Group("seam", "jboss");
		hb = new Group("hibernate", "jboss");
		gavin.getGroups().add(seam);
		gavin.getGroups().add(hb);
		seam.getUsers().add(gavin);
		hb.getUsers().add(gavin);
		s.persist(gavin);
		s.persist(seam);
		s.persist(hb);
		t.commit();
		s.close();
	}

	public void cleanupTest() {
		Session s = openSession();
		Transaction t = s.beginTransaction();
		s.delete(gavin);
		s.flush();
		s.createQuery("delete from Group").executeUpdate();
		t.commit();
		s.close();
		gavin = null;
		seam = null;
		hb = null;
	}

	public void testManyToManyWithFormula() {
		Session s = openSession();
		Transaction t = s.beginTransaction();
		gavin = (User) s.get(User.class, gavin);
		assertFalse( Hibernate.isInitialized( gavin.getGroups() ) );
		assertEquals( 2, gavin.getGroups().size() );
		hb = (Group) s.get(Group.class, hb);
		assertFalse( Hibernate.isInitialized( hb.getUsers() ) );
		assertEquals( 1, hb.getUsers().size() );
		t.commit();
		s.close();

		s = openSession();
		t = s.beginTransaction();
		gavin = (User) s.createCriteria(User.class)
			.setFetchMode("groups", FetchMode.JOIN)
			.uniqueResult();
		assertTrue( Hibernate.isInitialized( gavin.getGroups() ) );
		assertEquals( 2, gavin.getGroups().size() );
		Group group = (Group) gavin.getGroups().iterator().next();
		assertFalse( Hibernate.isInitialized( group.getUsers() ) );
		assertEquals( 1, group.getUsers().size() );
		t.commit();
		s.close();
		
		s = openSession();
		t = s.beginTransaction();
		gavin = (User) s.createCriteria(User.class)
			.setFetchMode("groups", FetchMode.JOIN)
			.setFetchMode("groups.users", FetchMode.JOIN)
			.uniqueResult();
		assertTrue( Hibernate.isInitialized( gavin.getGroups() ) );
		assertEquals( 2, gavin.getGroups().size() );
		group = (Group) gavin.getGroups().iterator().next();
		assertTrue( Hibernate.isInitialized( group.getUsers() ) );
		assertEquals( 1, group.getUsers().size() );
		t.commit();
		s.close();
		
		s = openSession();
		t = s.beginTransaction();
		gavin = (User) s.createQuery("from User u join fetch u.groups g join fetch g.users").uniqueResult();
		assertTrue( Hibernate.isInitialized( gavin.getGroups() ) );
		assertEquals( 2, gavin.getGroups().size() );
		group = (Group) gavin.getGroups().iterator().next();
		assertTrue( Hibernate.isInitialized( group.getUsers() ) );
		assertEquals( 1, group.getUsers().size() );
		t.commit();
		s.close();

		s = openSession();
		t = s.beginTransaction();
		gavin = (User) s.get(User.class, gavin);
		hb = (Group) s.get(Group.class, hb);
		gavin.getGroups().remove(hb);
		t.commit();
		s.close();

		s = openSession();
		t = s.beginTransaction();
		gavin = (User) s.get(User.class, gavin);
		assertEquals( gavin.getGroups().size(), 1 );
		hb = (Group) s.get(Group.class, hb);
		assertEquals( hb.getUsers().size(), 0 );
		t.commit();
		s.close();
	}

	public void testManyToManyWithUserRemovedFromGroupByCriteriaWithoutFlush() {
		testManyToManyWithUserRemovedFromGroupByCriteria(false);
	}

	public void testManyToManyWithUserRemovedFromGroupByCriteriaWithFlush() {
		testManyToManyWithUserRemovedFromGroupByCriteria(true);
	}

	private void testManyToManyWithUserRemovedFromGroupByCriteria(boolean isFlushedBeforeCriteria) {
		Session s = openSession();
		Transaction t = s.beginTransaction();
		gavin = (User) s.get(User.class, gavin);
		hb = (Group) s.get(Group.class, hb);
		seam = (Group) s.get(Group.class, seam);
		assertEquals(1, hb.getUsers().size());
		assertTrue(hb.getUsers().contains(gavin));
		assertEquals(2, gavin.getGroups().size());
		assertTrue(gavin.getGroups().contains(hb));
		assertTrue(gavin.getGroups().contains(seam));
		gavin.getGroups().remove(hb);
		hb.getUsers().remove(gavin);
		if (isFlushedBeforeCriteria) {
			s.flush();
		}
		List groupsWithGavin = s.createCriteria(Group.class)
			.createCriteria("users")
			.add(Restrictions.eq("name", gavin.getName()))
			.list();
		assertEquals(1, groupsWithGavin.size());
		assertTrue(groupsWithGavin.contains(seam));
		List usersWithHibernate = s.createCriteria(User.class)
			.createCriteria("groups")
			.add(Restrictions.eq("name", hb.getName()))
			.list();
		assertEquals(0, usersWithHibernate.size());
		t.commit();
		s.close();
	}

	public void testManyToManyWithUserRemovedFromGroupByQuery() {
		Session s = openSession();
		Transaction t = s.beginTransaction();
		gavin = (User) s.get(User.class, gavin);
		hb = (Group) s.get(Group.class, hb);
		seam = (Group) s.get(Group.class, seam);
		assertEquals(1, hb.getUsers().size());
		assertTrue(hb.getUsers().contains(gavin));
		assertEquals(2, gavin.getGroups().size());
		assertTrue(gavin.getGroups().contains(hb));
		assertTrue(gavin.getGroups().contains(seam));
		gavin.getGroups().remove(hb);
		hb.getUsers().remove(gavin);
		List groupsWithGavin = s.createQuery("from Group g join fetch g.users u where u.name = :name").setString("name", gavin.getName()).list();
		assertEquals(1, groupsWithGavin.size());
		assertTrue(groupsWithGavin.contains(seam));
		List usersWithHibernate = s.createQuery("from User u join fetch u.groups g where g.name = :name").setString("name", hb.getName()).list();
		assertEquals(0, usersWithHibernate.size());
		t.commit();
		s.close();
	}


}

