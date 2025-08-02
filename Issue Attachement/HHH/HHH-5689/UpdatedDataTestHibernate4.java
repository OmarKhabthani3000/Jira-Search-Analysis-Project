package org.hibernate.test;

import java.util.Map;

import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;

public class UpdatedDataTestHibernate4 extends BaseCoreFunctionalTestCase {

	@Override
	protected Class<?>[] getAnnotatedClasses() {
		return new Class[] { UpdatedItemHibernate4.class };
	}

	@Override
	protected void configure(Configuration cfg) {
		super.configure( cfg );
		cfg.setProperty( Environment.CACHE_REGION_PREFIX, "" );
		cfg.setProperty( Environment.GENERATE_STATISTICS, "true" );
	}

	@Test
	public void testUpdateWithRefreshThenRollback() {
		sessionFactory().getCache().evictEntityRegions();
		sessionFactory().getStatistics().clear();

		Long id = null;
		Session s = openSession();
		s.beginTransaction();
		UpdatedItemHibernate4 item = new UpdatedItemHibernate4( "data" );
		id = (Long) s.save( item );
		s.flush();
		s.getTransaction().commit();

		Map cacheMap = sessionFactory().getStatistics().getSecondLevelCacheStatistics( "item" ).getEntries();
		Assert.assertEquals( 1, cacheMap.size() );

		s = openSession();
		s.beginTransaction();
		item = (UpdatedItemHibernate4) s.get(UpdatedItemHibernate4.class, id);
		item.setName("newdata");
		s.update(item);
		s.flush();
		s.refresh(item);
		s.getTransaction().rollback();
		s.clear();
		s.close();

		s = openSession();
		s.beginTransaction();
		item = (UpdatedItemHibernate4) s.get(UpdatedItemHibernate4.class, id);
		Assert.assertEquals("data", item.getName());
		s.getTransaction().commit();
		s.close();
	}



}
