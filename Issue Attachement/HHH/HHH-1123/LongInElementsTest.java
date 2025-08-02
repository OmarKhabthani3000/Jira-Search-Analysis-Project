package org.hibernate.test.criteria;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.hibernate.junit.functional.FunctionalTestCase;
import org.hibernate.test.hql.StateProvince;

public class LongInElementsTest extends FunctionalTestCase {
	
	private static final int ELEMENTS_SIZE = 2000;
	
	public LongInElementsTest( String string ) {
		super(string);
	}

	public String[] getMappings() {
		return new String[] { "criteria/Animal.hbm.xml" };
	}
	
	//HHH-1123
	public void testLongInElementsByHQL(){
		Session session = openSession();
		Transaction t = session.beginTransaction();

		StateProvince beijing = new StateProvince();
		beijing.setIsoCode("100089");
		beijing.setName("beijing");
		session.persist(beijing);
		session.flush();
		session.clear();
		
		Query query = session.createQuery("from org.hibernate.test.hql.StateProvince sp where sp.id in ( :idList )");
		query.setParameterList( "idList" , createLotsOfElements() );
		List list = query.list();
		session.flush();
		session.clear();
		assertEquals( 1, list.size() );
		session.delete(beijing);
		t.commit();
		session.close();
		
	}
	//HHH-1123
	public void testLongInElementsByCriteria(){
		Session session = openSession();
		Transaction t = session.beginTransaction();

		StateProvince beijing = new StateProvince();
		beijing.setIsoCode("100089");
		beijing.setName("beijing");
		session.persist(beijing);
		session.flush();
		session.clear();
		
		Criteria criteria = session.createCriteria(StateProvince.class);
		criteria.add(Restrictions.in("id", createLotsOfElements()));
		List list = criteria.list();
		session.flush();
		session.clear();
		assertEquals( 1, list.size() );
		session.delete(beijing);
		t.commit();
		session.close();
		
	}
	
	private List createLotsOfElements(){
		List list = new ArrayList();
		for ( int i = 0; i < ELEMENTS_SIZE; i++ ){
			list.add(Long.valueOf(i));
		}
		return list;
	}
}
