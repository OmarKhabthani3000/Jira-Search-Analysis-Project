package org.hibernate.test;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.hibernate.CacheMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.Version;
import org.hibernate.cache.ehcache.EhCacheRegionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.hibernate.stat.Statistics;

public class CacheModeIgnoreTest extends TestCase {

	/**
	 * Test that there is no interaction with cache except for invalidation when using CacheMode.IGNORE
	 */
	public void testCacheModeIgnore() {
		System.out.println(Version.getVersionString());
		
		SessionFactory sessionFactory = createSessionFactory();
		Statistics statistics = sessionFactory.getStatistics();
		Session s;
		Transaction t;
		PurchaseOrder result;
		
		// ----------------------------------------------------------------------------------------------
		// insert
		statistics.clear();

		s = sessionFactory.openSession();
		s.setCacheMode(CacheMode.IGNORE);
		t = s.beginTransaction();
		
		PurchaseOrder purchaseOrder = new PurchaseOrder(1L, 2L, 1000L);
		s.persist(purchaseOrder);

		t.commit();
		s.close();

		assertEquals(0, statistics.getSecondLevelCacheHitCount());
		assertEquals(0, statistics.getSecondLevelCacheMissCount());
		assertEquals(0, statistics.getSecondLevelCachePutCount());

		// ----------------------------------------------------------------------------------------------
		// update
		statistics.clear();

		s = sessionFactory.openSession();
		s.setCacheMode(CacheMode.IGNORE);
		t = s.beginTransaction();
		
		result = (PurchaseOrder)s.get(PurchaseOrder.class, 1L);
		result.setTotalAmount(2000L);
		/*
		result = (PurchaseOrder)s.createCriteria(PurchaseOrder.class).add(Restrictions.eq("customerId", Long.valueOf(2))).uniqueResult();
		s.evict(result);
		s.update(result);
		*/
		/*
		purchaseOrder.setTotalAmount(2000L);
		s.update(purchaseOrder);
		*/
		
		t.commit();
		s.close();

		try {
			EhCacheRegionFactory regionFactory = ((EhCacheRegionFactory)((SessionFactoryImpl)sessionFactory).getSettings().getRegionFactory());
			Field field = EhCacheRegionFactory.class.getSuperclass().getDeclaredField("manager");
			field.setAccessible(true);
			CacheManager cacheManager = (CacheManager)field.get(regionFactory);
			Cache cache = cacheManager.getCache(PurchaseOrder.class.getName());
			for (Object key : cache.getKeys()) {
				Element element = cache.get(key);
				Object value = element.getValue();
				List<Object> data = new ArrayList<Object>();
				if (value.getClass().getName().equals("org.hibernate.cache.ehcache.internal.strategy.AbstractReadWriteEhcacheAccessStrategy$Item")) {
					Field field2 = value.getClass().getDeclaredField("value");
					field2.setAccessible(true);
					Object hbvalue = field2.get(value);
					if (hbvalue instanceof org.hibernate.cache.spi.entry.CacheEntry) { // for hibernate entity cache
						for (Serializable serializable : ((org.hibernate.cache.spi.entry.CacheEntry)hbvalue).getDisassembledState()) {
							if (serializable instanceof Object[]) {
								data.add(Arrays.toString((Object[])serializable));
							} else {
								data.add(serializable);
							}
						}
					}
				}
				System.out.println("cache: " + key + "->" + Arrays.toString(data.toArray()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		assertEquals(0, statistics.getSecondLevelCacheHitCount());
		assertEquals(0, statistics.getSecondLevelCacheMissCount());
		assertEquals(0, statistics.getSecondLevelCachePutCount());

		// ----------------------------------------------------------------------------------------------
		
		sessionFactory.close();
	}

	private SessionFactory createSessionFactory() {
		Configuration configuration = new Configuration();
		configuration.setProperty("hibernate.connection.driver_class", "org.hsqldb.jdbcDriver");
		configuration.setProperty("hibernate.connection.url", "jdbc:hsqldb:mem:test");
		configuration.setProperty("hibernate.connection.username", "sa");
		configuration.setProperty("hibernate.connection.password", "");
		configuration.setProperty(Environment.HBM2DDL_AUTO, "create-drop");
		configuration.setProperty(Environment.CACHE_REGION_FACTORY, EhCacheRegionFactory.class.getName());
		configuration.setProperty(Environment.SHOW_SQL, "true");
		configuration.setProperty(Environment.USE_SECOND_LEVEL_CACHE, "true");
		configuration.setProperty(Environment.USE_QUERY_CACHE, "true");
		configuration.setProperty(Environment.GENERATE_STATISTICS, "true");
		configuration.addAnnotatedClass(PurchaseOrder.class);
		
		ServiceRegistry serviceRegistry = new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry();
		return configuration.buildSessionFactory(serviceRegistry);
	}

}
