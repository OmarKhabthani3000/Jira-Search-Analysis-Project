package org.hibernate.search.test.query.criteria;

import junit.framework.Assert;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.Query;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.hibernate.search.test.SearchTestCase;
import org.junit.Test;

import java.util.List;


public class ResultSizeOnCriteria extends SearchTestCase {

	@Test
	public void testResultSize() throws ParseException {
		indexTestData();

		// Search
		Session session = openSession();
		Transaction tx = session.beginTransaction();
		FullTextSession fullTextSession = Search.getFullTextSession(session);

		//Write query
		QueryBuilder qb = fullTextSession.getSearchFactory().buildQueryBuilder().forEntity(Tractor.class).get();
		Query query = qb.keyword().wildcard().onField("owner").matching("p*").createQuery();


		//set criteria
		Criteria criteria = session.createCriteria(Tractor.class);
		criteria.add(Restrictions.eq("hasColor", Boolean.FALSE));

		FullTextQuery hibQuery = fullTextSession.createFullTextQuery(query, Tractor.class).setCriteriaQuery(criteria);
		List<Tractor> result = hibQuery.list();
		//Result size is ok
		assertEquals(1, result.size());

		for (Tractor tractor : result) {
			Assert.assertTrue(!tractor.isHasColor());
			Assert.assertTrue(tractor.getOwner().startsWith("P"));
		}

		//Compare with resultSize
		assertEquals(result.size(), hibQuery.getResultSize());
		//getResultSize get only count of tractors matching keyword on field "owner" beginning with "p*"
		tx.commit();
		session.close();

	}


	private void indexTestData() {
		Session s = openSession();
		Transaction tx = s.beginTransaction();


		Tractor tractor = new Tractor();
		tractor.setKurztext("tractor");
		tractor.setOwner("Paul");
		tractor.removeColor();
		s.persist(tractor);

		Tractor tractor2 = new Tractor();
		tractor2.setKurztext("tractor");
		tractor2.setOwner("Pierre");
		s.persist(tractor2);

		Tractor tractor3 = new Tractor();
		tractor3.setKurztext("tractor");
		tractor3.setOwner("Jacques");
		s.persist(tractor3);

		tx.commit();
		s.close();
	}

	@Override
	protected Class<?>[] getAnnotatedClasses() {
		return new Class[] {
				Tractor.class
		};
	}
}
