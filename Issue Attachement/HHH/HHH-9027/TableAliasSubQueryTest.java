package org.hibernate.test.hql;

import java.util.Collections;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.hibernate.Filter;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.engine.query.spi.HQLQueryPlan;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.hibernate.testing.junit4.BaseUnitTestCase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class TableAliasSubQueryTest extends BaseUnitTestCase {
	@Entity( name = "TheEntity" )
	public static class TheEntity {
		@Id
		private Long id;
	}

	private SessionFactory sessionFactory;

	@Before
	public void buildSessionFactory() {
		Configuration cfg = new Configuration()
				.addAnnotatedClass( TheEntity.class );
		cfg.getProperties().put( AvailableSettings.HBM2DDL_AUTO, "create-drop" );
		sessionFactory = cfg.buildSessionFactory();
	}

	@After
	public void releaseSessionFactory() {
		sessionFactory.close();
	}

	@Test
	public void testTableAliasInCorrelatedSubQuery() {
		final String hql = "insert into TheEntity (id) "
				+ "select e1.id+1 from TheEntity e1 "
				+ "where not exists (select 1 from TheEntity e2 where e2.id = e1.id+1)";
		HQLQueryPlan queryPlan = ( (SessionFactoryImplementor) sessionFactory ).getQueryPlanCache()
				.getHQLQueryPlan( hql, false, Collections.<String,Filter>emptyMap() );

		assertEquals( 1, queryPlan.getSqlStrings().length );

		System.out.println( " SQL : " + queryPlan.getSqlStrings()[0] );

		assertFalse( queryPlan.getSqlStrings()[0].contains( "TheEntity.id" ) );
	}

	@Test
	public void testTableAliasInCorrelatedSubQueryJoinInQuery() {
		final String hql = "insert into TheEntity (id) "
				+ "select e1.id+e2.id from TheEntity e1, TheEntity e2 "
				+ "where not exists (select 1 from TheEntity e3 where e3.id = e1.id+e2.id)";
		HQLQueryPlan queryPlan = ( (SessionFactoryImplementor) sessionFactory ).getQueryPlanCache()
				.getHQLQueryPlan( hql, false, Collections.<String,Filter>emptyMap() );

		assertEquals( 1, queryPlan.getSqlStrings().length );

		System.out.println( " SQL : " + queryPlan.getSqlStrings()[0] );

		assertFalse( queryPlan.getSqlStrings()[0].contains( "TheEntity.id" ) );
	}

	@Test
	public void testTableAliasInCorrelatedSubQueryJoinInSubQuery() {
		final String hql = "insert into TheEntity (id) "
				+ "select e1.id+1 from TheEntity e1 "
				+ "where not exists (select 1 from TheEntity e2, TheEntity e3 where e2.id = e1.id+1 and e3.id = e1.id+1)";
		HQLQueryPlan queryPlan = ( (SessionFactoryImplementor) sessionFactory ).getQueryPlanCache()
				.getHQLQueryPlan( hql, false, Collections.<String,Filter>emptyMap() );

		assertEquals( 1, queryPlan.getSqlStrings().length );

		System.out.println( " SQL : " + queryPlan.getSqlStrings()[0] );

		assertFalse( queryPlan.getSqlStrings()[0].contains( "TheEntity.id" ) );
	}
}
