package org.hibernate.test.annotations.cache;

import java.util.Map;

import org.hibernate.LockOptions;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.test.annotations.TestCase;

public class ItemTest extends TestCase {

	@Override
	protected Class<?>[] getAnnotatedClasses() {
		return new Class[] { Item.class };
	}

	@Override
	protected void configure( Configuration cfg ) {
		super.configure(cfg);
		cfg.setProperty(Environment.CACHE_REGION_PREFIX, "");
		cfg.setProperty(Environment.GENERATE_STATISTICS, "true");
	}

	public void testInsert() {
		getSessions().getCache().evictEntityRegions();
		getSessions().getStatistics().clear();
		Session s = openSession();
		s.beginTransaction();
		Item item = new Item();
		item.setName("stliu");
		s.save(item);
		s.getTransaction().commit();
		s.close();
		Map cacheMap = getSessions().getStatistics()
				.getSecondLevelCacheStatistics("item").getEntries();
		assertEquals(1, cacheMap.size());
	}

	public void testInsertWithRefresh() {
		getSessions().getCache().evictEntityRegions();
		getSessions().getStatistics().clear();
		
		Session s = openSession();
		s.beginTransaction();
		Item item = new Item();
		item.setName("stliu");
		s.save(item);
		s.flush();
		s.refresh(item);
		s.getTransaction().rollback();
		s.close();
		
		Map cacheMap = getSessions().getStatistics()
				.getSecondLevelCacheStatistics("item").getEntries();
		assertEquals(0, cacheMap.size());
		
		s = openSession();
		s.beginTransaction();
		item = (Item)s.get(Item.class, item.getId());
		s.getTransaction().commit();
		s.close();
		
		assertNull("it should be null", item);
	}

	public void testInsertWithRollback() {
		getSessions().getCache().evictEntityRegions();
		getSessions().getStatistics().clear();

		Session s = openSession();
		s.beginTransaction();
		Item item = new Item();
		item.setName("stliu");
		s.save(item);
		s.flush();
		s.getTransaction().rollback();
		s.close();
		Map cacheMap = getSessions().getStatistics()
				.getSecondLevelCacheStatistics("item").getEntries();
		assertEquals(0, cacheMap.size());
	}

	public void testQueryWithRollback() {
		Session s = openSession();
		s.beginTransaction();
		Item item = new Item();
		item.setName("stliu");
		s.save(item);
		s.flush();
		s.getTransaction().commit();
		s.close();
		getSessions().getCache().evictEntityRegions();
		getSessions().getStatistics().clear();
		s = openSession();
		s.beginTransaction();
		s.get(Item.class, item.getId());
		s.getTransaction().rollback();
		Map cacheMap = getSessions().getStatistics()
				.getSecondLevelCacheStatistics("item").getEntries();
		assertEquals(0, cacheMap.size());
	}

}
