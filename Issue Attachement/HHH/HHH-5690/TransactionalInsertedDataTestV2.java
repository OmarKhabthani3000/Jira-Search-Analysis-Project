/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * Copyright (c) 2010, Red Hat Inc. or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Inc.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.hibernate.test.cache;

import java.util.Map;

import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Nikita Koksharov
 */
public class TransactionalInsertedDataTestV2 extends BaseCoreFunctionalTestCase {
	@Override
	protected Class<?>[] getAnnotatedClasses() {
		return new Class[] { TransactionalCacheableItem.class };
	}

	@Override
	protected void configure(Configuration cfg) {
		super.configure( cfg );
		cfg.setProperty( Environment.CACHE_REGION_PREFIX, "" );
		cfg.setProperty( Environment.GENERATE_STATISTICS, "true" );
	}

	@Test
	public void testInsert() {
		sessionFactory().getCache().evictEntityRegions();
		sessionFactory().getStatistics().clear();

		Session s = openSession();
		s.beginTransaction();
		TransactionalCacheableItem item = new TransactionalCacheableItem( "data" );
		s.save( item );
		s.getTransaction().commit();
		s.close();

		Map cacheMap = sessionFactory().getStatistics().getSecondLevelCacheStatistics( "item" ).getEntries();
		Assert.assertEquals( 1, cacheMap.size() );

		s = openSession();
		s.beginTransaction();
		s.createQuery( "delete TransactionalCacheableItem" ).executeUpdate();
		s.getTransaction().commit();
		s.close();
	}

	@Test
	public void testInsertWithRollback() {
		sessionFactory().getCache().evictEntityRegions();
		sessionFactory().getStatistics().clear();

		Session s = openSession();
		s.beginTransaction();
		TransactionalCacheableItem item = new TransactionalCacheableItem( "data" );
		s.save( item );
		s.flush();
		s.getTransaction().rollback();
		s.close();

		Map cacheMap = sessionFactory().getStatistics().getSecondLevelCacheStatistics( "item" ).getEntries();
		Assert.assertEquals( 0, cacheMap.size() );
	}

	@Test
	public void testInsertThenUpdate() {
		sessionFactory().getCache().evictEntityRegions();
		sessionFactory().getStatistics().clear();

		Session s = openSession();
		s.beginTransaction();
		TransactionalCacheableItem item = new TransactionalCacheableItem( "data" );
		s.save( item );
		s.flush();
		item.setName( "new data" );
		s.getTransaction().commit();
		s.close();

		Map cacheMap = sessionFactory().getStatistics().getSecondLevelCacheStatistics( "item" ).getEntries();
		Assert.assertEquals( 1, cacheMap.size() );

		s = openSession();
		s.beginTransaction();
		s.createQuery( "delete TransactionalCacheableItem" ).executeUpdate();
		s.getTransaction().commit();
		s.close();
	}

	@Test
	public void testInsertThenUpdateThenRollback() {
		sessionFactory().getCache().evictEntityRegions();
		sessionFactory().getStatistics().clear();

		Session s = openSession();
		s.beginTransaction();
		TransactionalCacheableItem item = new TransactionalCacheableItem( "data" );
		s.save( item );
		s.flush();
		item.setName( "new data" );
		s.getTransaction().rollback();
		s.close();

		Map cacheMap = sessionFactory().getStatistics().getSecondLevelCacheStatistics( "item" ).getEntries();
		Assert.assertEquals( 0, cacheMap.size() );

		s = openSession();
		s.beginTransaction();
		s.createQuery( "delete CacheableItem" ).executeUpdate();
		s.getTransaction().commit();
		s.close();
	}

	@Test
	public void testInsertWithRefresh() {
		sessionFactory().getCache().evictEntityRegions();
		sessionFactory().getStatistics().clear();

		Session s = openSession();
		s.beginTransaction();
		TransactionalCacheableItem item = new TransactionalCacheableItem( "data" );
		s.save( item );
		s.flush();
		s.refresh( item );
		s.getTransaction().commit();
		s.close();

		Map cacheMap = sessionFactory().getStatistics().getSecondLevelCacheStatistics( "item" ).getEntries();
		Assert.assertEquals( 1, cacheMap.size() );

		s = openSession();
		s.beginTransaction();
		s.createQuery( "delete TransactionalCacheableItem" ).executeUpdate();
		s.getTransaction().commit();
		s.close();
	}

	@Test
	public void testInsertWithRefreshThenRollback() {
		sessionFactory().getCache().evictEntityRegions();
		sessionFactory().getStatistics().clear();

		Session s = openSession();
		s.beginTransaction();
		TransactionalCacheableItem item = new TransactionalCacheableItem( "data" );
		s.save( item );
		s.flush();
		s.refresh( item );
		s.getTransaction().rollback();
		s.close();

		Map cacheMap = sessionFactory().getStatistics().getSecondLevelCacheStatistics( "item" ).getEntries();
		Assert.assertEquals( 0, cacheMap.size() );

		s = openSession();
		s.beginTransaction();
		item = (TransactionalCacheableItem) s.get( TransactionalCacheableItem.class, item.getId() );
		s.getTransaction().commit();
		s.close();

		Assert.assertNull( "it should be null", item );
	}

	@Test
	public void testInsertWithClear() {
		sessionFactory().getCache().evictEntityRegions();
		sessionFactory().getStatistics().clear();

		Session s = openSession();
		s.beginTransaction();
		TransactionalCacheableItem item = new TransactionalCacheableItem( "data" );
		s.save( item );
		s.flush();
		s.clear();
		s.getTransaction().commit();
		s.close();

		Map cacheMap = sessionFactory().getStatistics().getSecondLevelCacheStatistics( "item" ).getEntries();
		Assert.assertEquals( 1, cacheMap.size() );

		s = openSession();
		s.beginTransaction();
		s.createQuery( "delete TransactionalCacheableItem" ).executeUpdate();
		s.getTransaction().commit();
		s.close();
	}

	@Test
	public void testInsertWithClearThenRollback() {
		sessionFactory().getCache().evictEntityRegions();
		sessionFactory().getStatistics().clear();

		Session s = openSession();
		s.beginTransaction();
		TransactionalCacheableItem item = new TransactionalCacheableItem( "data" );
		s.save( item );
		s.flush();
		s.clear();
		item = (TransactionalCacheableItem) s.get( TransactionalCacheableItem.class, item.getId() );
		s.getTransaction().rollback();
		s.close();

		Map cacheMap = sessionFactory().getStatistics().getSecondLevelCacheStatistics( "item" ).getEntries();
		Assert.assertEquals( 0, cacheMap.size() );

		s = openSession();
		s.beginTransaction();
		item = (TransactionalCacheableItem) s.get( TransactionalCacheableItem.class, item.getId() );
		s.getTransaction().commit();
		s.close();

		Assert.assertNull( "it should be null", item );
	}

}
