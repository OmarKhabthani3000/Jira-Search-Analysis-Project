//$Id: QueryCacheTest.java 10977 2006-12-12 23:28:04Z steve.ebersole@jboss.com $
package org.hibernate.test.querycache;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;

import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.junit.functional.FunctionalTestCase;
import org.hibernate.junit.functional.FunctionalTestClassTestSuite;

/**
 * @author Gavin King
 */
public class QueryCacheTest extends FunctionalTestCase {

	public QueryCacheTest(String str) {
		super(str);
	}

	public String[] getMappings() {
		return new String[] { "querycache/Item.hbm.xml" };
	}

	public void configure(Configuration cfg) {
		super.configure(cfg);
		cfg.setProperty(Environment.USE_QUERY_CACHE, "true");
		cfg.setProperty(Environment.CACHE_REGION_PREFIX, "foo");
		cfg.setProperty(Environment.USE_SECOND_LEVEL_CACHE, "true");
		cfg.setProperty(Environment.GENERATE_STATISTICS, "true");
	}

	public static Test suite() {
		return new FunctionalTestClassTestSuite(QueryCacheTest.class);
	}

	public void testHQLUpdates() {
		getSessions().evictQueries();
		getSessions().getStatistics().clear();
		Session s = openSession();
		List list = new ArrayList();
		s.beginTransaction();
		for (int i = 0; i < 3; i++) {
			Item a = new Item();
			a.setName("a" + i);
			a.setDescription("a" + i);
			list.add(a);
			s.persist(a);
		}
		s.getTransaction().commit();

		s.beginTransaction();
		String queryString = "select count(*) from Item";
		// this query will hit the database and create the cache
		Number result = (Number) s.createQuery(queryString).setCacheable(true).uniqueResult();
		assertEquals(3, result.intValue());
		s.getTransaction().commit();

		s.beginTransaction();
		String updateString = "delete from Item";
		s.createQuery(updateString).executeUpdate();
		s.getTransaction().commit();

		s.beginTransaction();
		// and this one SHOULD not be served by the cache
		Number result2 = (Number) s.createQuery(queryString).setCacheable(true).uniqueResult();
		assertEquals(0, result2.intValue());
		s.getTransaction().commit();
	}
}
