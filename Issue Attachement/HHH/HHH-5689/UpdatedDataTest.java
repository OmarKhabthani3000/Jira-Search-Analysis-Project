package org.hibernate.test.cache;

import java.util.Map;

import junit.framework.Assert;

import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.test.annotations.TestCase;

public class UpdatedDataTest extends TestCase {

	@Override
	protected Class<?>[] getAnnotatedClasses() {
		return new Class[] { UpdatedItem.class };
	}
	
	@Override
	protected void configure(Configuration cfg) {
		super.configure( cfg );
		cfg.setProperty( Environment.CACHE_REGION_PREFIX, "" );
		cfg.setProperty( Environment.GENERATE_STATISTICS, "true" );
	}
	
	public void testUpdateWithRefreshThenRollback() {
		getSessions().getCache().evictEntityRegions();
		getSessions().getStatistics().clear();

		Long id = null;
		Session s = openSession();
		s.beginTransaction();
		UpdatedItem item = new UpdatedItem( "data" );
		id = (Long) s.save( item );
		s.flush();
		s.getTransaction().commit();

		Map cacheMap = getSessions().getStatistics().getSecondLevelCacheStatistics( "item" ).getEntries();
		assertEquals( 1, cacheMap.size() );

		s = openSession();
		s.beginTransaction();
		item = (UpdatedItem) s.get(UpdatedItem.class, id);
		item.setName("newdata");
		s.update(item);
		s.flush();
		s.refresh(item);
		s.getTransaction().rollback();
		s.clear();
		s.close();

		s = openSession();
		s.beginTransaction();
		item = (UpdatedItem) s.get(UpdatedItem.class, id);
		Assert.assertEquals("data", item.getName());
		s.getTransaction().commit();
		s.close();
	}


}
