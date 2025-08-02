package org.hibernate.test.hqlfetchscroll;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Transaction;
import org.hibernate.classic.Session;
import org.hibernate.test.TestCase;
import org.hibernate.transform.ResultTransformer;

public class CriteriaScrollFetchTest extends TestCase {

	public CriteriaScrollFetchTest(String name) {
		super(name);
	}

	protected String[] getMappings() {
		return new String[]{"hqlfetchscroll/ParentChild.hbm.xml"};
	}

	private void assertResultFromAllUsers(List list) {
		assertEquals("list is not correct size: ",2,list.size());
		for (Iterator i = list.iterator(); i.hasNext(); ) {
			Parent parent = (Parent) i.next();
			assertEquals("parent "+parent+" has incorrect collection(" + parent.getChildren() + ").",3,parent.getChildren().size());
		}
	}

	private void deleteAll() {
		Session s = openSession();
		Transaction t = s.beginTransaction();
		List list = s.createQuery("from Parent").list();
		for (Iterator i = list.iterator(); i.hasNext();) {
			s.delete((Parent) i.next());
		}
		t.commit();
		s.close();
	}

	private void insertTestData() {
		Session s = openSession();
		Transaction t = s.beginTransaction();
		s.save(makeParent("parent1", "child1-1", "child1-2", "child1-3"));
		s.save(makeParent("parent2", "child2-1", "child2-2", "child2-3"));
		t.commit();
		s.close();
	}

	protected Object makeParent(String name, String child1, String child2, String child3) {
		Parent parent = new Parent(name);
		parent.addChild(new Child(child1));
		parent.addChild(new Child(child2));
		parent.addChild(new Child(child3));
		return parent;
	}

	protected void setUp() throws Exception {
		super.setUp();
		insertTestData();
	}
	
	public final void testScrollWithoutFetch () {
		try {
			Session s = openSession();
			ScrollableResults results = s.createCriteria(Parent.class).scroll();
			assertResultFromAllUsers(makeList(results));
			s.close();
		} finally {
			deleteAll();
		}
	}

	public final void testScrollWithFetch () {
		try {
			Session s = openSession();
			ScrollableResults results = s.createCriteria(Parent.class).setFetchMode("children",FetchMode.JOIN).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).scroll();
			assertResultFromAllUsers(makeList(results));
			s.close();
		} finally {
			deleteAll();
		}
	}

	public final void testListWithoutFetch () {
		try {
			Session s = openSession();
			List list = s.createCriteria(Parent.class).list();
			assertResultFromAllUsers(list);
			s.close();
		} finally {
			deleteAll();
		}
	}

	public final void testListWithFetch () {
		try {
			Session s = openSession();
			List list = s.createCriteria(Parent.class).setFetchMode("children",FetchMode.JOIN).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
			assertResultFromAllUsers(list);
			s.close();
		} finally {
			deleteAll();
		}
	}

	private List makeList(ScrollableResults results) {
		List list = new ArrayList();
		while (results.next()) {
			list.add(results.get(0));
		}
		return list;
	}
}
