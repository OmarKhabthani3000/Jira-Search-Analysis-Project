package com.eightd.ftk.db.hibernate.criteria;

import com.eightd.ftk.db.hibernate.criteria.model.EntityA;
import com.eightd.ftk.db.hibernate.criteria.model.EntityB;
import com.eightd.ftk.db.hibernate.criteria.model.EntityC;
import com.mysql.jdbc.Driver;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.criterion.Restrictions;
import org.hibernate.dialect.MySQL5InnoDBDialect;
import org.hibernate.sql.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;

public class CollectionCriteriaTest {

	private static final Logger logger = LoggerFactory.getLogger( CollectionCriteriaTest.class );

	public static void main( String[] args ) throws Exception {
		CollectionCriteriaTest testObject = new CollectionCriteriaTest();
		testObject.runTest();
	}

	protected void runTest() throws Exception {
		((ch.qos.logback.classic.Logger) LoggerFactory.getLogger( "ROOT" )).setLevel( Level.INFO );
		Configuration hConf = initializeSchemaAndGetConfiguration();

		SessionFactory sessionFactory = hConf.buildSessionFactory();
		Session session = sessionFactory.openSession();
		session.beginTransaction();

		List<EntityC> entityCList = new ArrayList<>();
		List<EntityB> entityBList = new ArrayList<>();
		List<EntityA> entityAList = new ArrayList<>();
		for ( int i = 1; i <= 3; i++ ) {
			entityCList.add( new EntityC( String.format( "c%d", i ) ) );
			entityBList.add( new EntityB( String.format( "b%d", i ) ) );
			entityAList.add( new EntityA( String.format( "a%d", i ) ) );

			if ( initializeEntities() ) {
				session.save( entityCList.get( i - 1 ) );
				session.save( entityBList.get( i - 1 ) );
				session.save( entityAList.get( i - 1 ) );
			}
		}

		session.getTransaction().commit();

		if ( initializeEntities() ) {
			session.beginTransaction();
		}

		/*
		Types and instances:
		 EntityA: a1, a2, a3
		 EntityB: b1, b2, b3
		 EntityC: c1, c2, c3
		Instance Relationship:
		 a1 contains b1, b2, b3
		 a2 contains 2x b2, b3
		 a3 does not contain any b
		 b1 contains c1, c2
		 b2 contains c1, c3
		 b3 does not contain any c
		 */
		entityAList.get( 0 ).getEntityBlist().addAll( entityBList );

		entityAList.get( 1 ).getEntityBlist().add( entityBList.get( 1 ) );
		entityAList.get( 1 ).getEntityBlist().add( entityBList.get( 1 ) );
		entityAList.get( 1 ).getEntityBlist().add( entityBList.get( 2 ) );

		entityBList.get( 0 ).getEntityClist().add( entityCList.get( 0 ) );
		entityBList.get( 0 ).getEntityClist().add( entityCList.get( 1 ) );

		entityBList.get( 1 ).getEntityClist().add( entityCList.get( 0 ) );
		entityBList.get( 1 ).getEntityClist().add( entityCList.get( 2 ) );

		Stream.concat( Stream.concat( entityAList.stream(), entityBList.stream() ), entityCList.stream() ).forEach( session::saveOrUpdate );

		if ( initializeEntities() ) {
			session.getTransaction().commit();
		}

		session.close();

		List<EntityA> expectedAList = entityAList.stream().filter( e -> e.getName().equals( "a1" ) || e.getName().equals( "a2" ) ).collect( Collectors.toList() );

		// Get all 'a' connected to 'c1': no condition on 'b'
		// Expected result: a1, a2
		// a1 should contain b1, b2 and b3
		// a2 should contain 2x b2 and b3
		// Actual result: a1, a2
		// a1 contains b1 and b2 **but not b3**
		// a2 contains 2x b2 **but not b3**
		session = sessionFactory.openSession();
		session.beginTransaction();

		List<EntityA> actualANoCondOnBList = session//
				.createCriteria( EntityA.class )//
				.createCriteria( EntityA.ATTRIBUTE_ENTITYB_LIST, JoinType.LEFT_OUTER_JOIN )//
				.createCriteria( EntityB.ATTRIBUTE_ENTITYC_LIST, JoinType.LEFT_OUTER_JOIN )//
				.add( Restrictions.eq( EntityC.ATTRIBUTE_NAME, "c1" ) )//
				.setResultTransformer( Criteria.DISTINCT_ROOT_ENTITY )//
				.list();

		checkList( expectedAList, actualANoCondOnBList, "no condition on B" );

		session.getTransaction().commit();
		session.close();


		// Get all 'a' connected to 'c1' with dummy condition on 'b'
		// Expected result: a1, a2
		// a1 should contain b1, b2 and b3
		// a2 should contain 2x b2 and b3
		// Actual result matches
		session = sessionFactory.openSession();
		session.beginTransaction();

		List<EntityA> actualAWithCondOnBList = session//
				.createCriteria( EntityA.class )//
				.createCriteria( EntityA.ATTRIBUTE_ENTITYB_LIST, JoinType.LEFT_OUTER_JOIN )//
				.add( Restrictions.isNotEmpty( EntityB.ATTRIBUTE_ENTITYC_LIST ) ) // DUMMY CONDITION ON B
				.createCriteria( EntityB.ATTRIBUTE_ENTITYC_LIST, JoinType.LEFT_OUTER_JOIN )//
				.add( Restrictions.eq( EntityC.ATTRIBUTE_NAME, "c1" ) )//
				.setResultTransformer( Criteria.DISTINCT_ROOT_ENTITY )//
				.list();

		checkList( expectedAList, actualAWithCondOnBList, "with condition on B" );

		session.getTransaction().commit();
		session.close();
	}

	private static void checkList( List<EntityA> expectedList, List<EntityA> actualList, String name ) {
		boolean ok = true;
		println( String.format( "Checking results for '%s'", name ) );
		println( "...Check that A results are expected..." );
		ok &= checkEntityAList( expectedList, actualList, actualList );

		println( "...Check that expected A results are in my list..." );
		ok &= checkEntityAList( expectedList, actualList, expectedList );

		for ( EntityA entityA : actualList ) {
			println( String.format( "...%s: Check that elements from my B list are expected...", entityA.getName() ) );
			EntityA expectedA = expectedList.stream().filter( e -> e.getName().equals( entityA.getName() ) ).findFirst().get();
			ok &= checkEntityBList( entityA, expectedA, entityA.getEntityBlist() );
			println( String.format( "...%s: Check that expected B elements are in my B list...", entityA.getName() ) );
			ok &= checkEntityBList( entityA, expectedA, expectedA.getEntityBlist() );
		}

		println( String.format( "%s: '%s'", ok ? "OK" : "**NOK**", name ), ok );
	}

	private static boolean checkEntityAList( List<EntityA> expectedList, List<EntityA> actualList, List<EntityA> checkList ) {
		boolean ok = true;
		for ( EntityA entityA : checkList ) {
			println( String.format( "......Checking '%s'...", entityA.getName() ) );
			long myCount = actualList.stream().filter( e -> e.getName().equals( entityA.getName() ) ).count();
			long expectedCount = expectedList.stream().filter( e -> e.getName().equals( entityA.getName() ) ).count();
			boolean oneEntityOk = myCount == expectedCount;
			println( String.format( "......%s: expected %d of %s, got %d", oneEntityOk ? "OK" : "**NOK**", expectedCount, entityA.getName(), myCount ), oneEntityOk );
			ok &= oneEntityOk;
		}
		println( String.format( "...%s", ok ? "OK" : "**NOK**" ), ok );
		return ok;
	}

	private static boolean checkEntityBList( EntityA entityA, EntityA expectedA, List<EntityB> entityBlist ) {
		boolean ok = true;
		for ( EntityB entityB : entityBlist ) {
			long myCount = entityA.getEntityBlist().stream().filter( e -> e.getName().equals( entityB.getName() ) ).count();
			long expectedCount = expectedA.getEntityBlist().stream().filter( e -> e.getName().equals( entityB.getName() ) ).count();
			boolean oneEntityOk = myCount == expectedCount;
			println( String.format( "......%s: expected %d of %s, got %d", oneEntityOk ? "OK" : "**NOK**", expectedCount, entityB.getName(), myCount ), oneEntityOk );
			ok &= oneEntityOk;
		}
		println( String.format( "...%s", ok ? "OK" : "**NOK**" ), ok );
		return ok;
	}

	protected String getUser() {
		return "user";
	}

	protected String getPassword() {
		return "password";
	}

	protected String getAddress() {
		return "localhost";
	}

	protected String getPort() {
		return "3306";
	}

	protected String getDb() {
		return "criteria_test";
	}

	protected String getDriver() {
		return Driver.class.getCanonicalName();
	}

	protected String getDialect() {
		return MySQL5InnoDBDialect.class.getCanonicalName();
	}

	protected String getDriverDBType() {
		return "mysql";
	}

	protected String getParameters() {
		return String.format( "user=%s&password=%s", getUser(), getPassword() );
	}

	protected String getMandatoryParameters() {
		return "autoCommit=false&characterEncoding=UTF-8&rewriteBatchedStatements=true&allowMultiQueries=true&useCursorFetch=true&useServerPrepStmts=true&useLocalSessionState=true&useLocalTransactionState=true&alwaysSendSetIsolation=false&elideSetAutoCommits=true&cacheServerConfiguration=true";
	}

	protected boolean initializeEntities() {
		return false;
	}

	protected String getUrl( boolean noSchema ) {
		StringBuilder sURL = new StringBuilder();
		sURL.append( "jdbc:" );
		sURL.append( this.getDriverDBType() );
		sURL.append( "://" );
		sURL.append( this.getAddress() );
		sURL.append( ":" );
		sURL.append( this.getPort() );
		if ( !noSchema ) {
			sURL.append( "/" );
			sURL.append( this.getDb() );
		}

		String sParams = this.getParameters();
		String sMandatoryParams = this.getMandatoryParameters();
		if ( sParams != null || sMandatoryParams != null ) {
			sURL.append( "?" );
		}

		if ( sParams != null ) {
			sURL.append( sParams );
		}

		if ( sMandatoryParams != null ) {
			if ( sParams != null ) {
				sURL.append( "&" );
			}

			sURL.append( sMandatoryParams );
		}

		return sURL.toString();
	}

	protected Configuration initializeSchemaAndGetConfiguration() throws Exception {
		// Schema initialization done in subclass (non-agnostic)

		// Create configuration
		Configuration configuration = new Configuration();
		configuration.setProperty( Environment.DRIVER, getDriver() );
		configuration.setProperty( Environment.URL, getUrl( false ) );
		configuration.setProperty( Environment.USER, getUser() );
		configuration.setProperty( Environment.PASS, getPassword() );
		configuration.setProperty( Environment.DIALECT, getDialect() );

		configuration.addAnnotatedClass( EntityA.class );
		configuration.addAnnotatedClass( EntityB.class );
		configuration.addAnnotatedClass( EntityC.class );

		return configuration;
	}

	private static void println( String message ) {
		println( message, true );
	}

	private static void println( String message, boolean ok ) {
		if ( !ok ) {
			logger.error( message );
		} else {
			logger.info( message );
		}
	}

}
