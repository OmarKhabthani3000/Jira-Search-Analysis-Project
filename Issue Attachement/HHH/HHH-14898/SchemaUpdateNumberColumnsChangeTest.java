package org.hibernate.test.schemaupdate;

import java.sql.ResultSet;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.BootstrapServiceRegistryBuilder;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SchemaUpdateNumberColumnsChangeTest  extends BaseUnitTestCase {

	@Before
	public void buildInitialSchema() throws Exception {
		// Builds the initial table in the schema.
		StandardServiceRegistry ssr = null;
		try {
			final Configuration cfg = buildConfiguration( BaseFourColumnEntity.class );
			ssr = new StandardServiceRegistryBuilder(
					new BootstrapServiceRegistryBuilder().build(),
					cfg.getStandardServiceRegistryBuilder().getAggregatedCfgXml() )
					.applySettings( cfg.getProperties() )
					.build();
			cfg.buildSessionFactory( ssr ).close();
		}
		finally {
			StandardServiceRegistryBuilder.destroy( ssr );
		}
	}

	@After
	public void cleanup() {
		// Drops the table after the sql alter test.
		StandardServiceRegistry ssr = null;
		try {
			// build simple configuration
			final Configuration cfg = buildConfiguration( BaseFourColumnEntity.class );

			// Build Standard Service Registry
			ssr = new StandardServiceRegistryBuilder(
					new BootstrapServiceRegistryBuilder().build(),
					cfg.getStandardServiceRegistryBuilder().getAggregatedCfgXml()
			)
					.applySettings( cfg.getProperties() )
					.build();

			try ( SessionFactory sf = cfg.buildSessionFactory();) {
				Session session = sf.openSession();
				try {
					session.getTransaction().begin();
					session.createNativeQuery( "DROP TABLE ColumnsUpdateTestEntity" ).executeUpdate();
					session.getTransaction().commit();
				}
				catch ( Throwable t ) {
					if ( session.getTransaction().isActive() ) {
						session.getTransaction().rollback();
					}
					throw t;
				}
				finally {
					session.close();
				}
			}
		}
		finally {
			StandardServiceRegistryBuilder.destroy( ssr );
		}
	}

	@Test
	public void testSqlAlterRemoveColumn() throws Exception {
		StandardServiceRegistry ssr = null;
		try {
			final Configuration cfg = buildConfiguration( ThreeColumnEntity.class );
			ssr = new StandardServiceRegistryBuilder(
					new BootstrapServiceRegistryBuilder().build(),
					cfg.getStandardServiceRegistryBuilder().getAggregatedCfgXml() )
					.applySettings( cfg.getProperties() )
					.build();
			try (SessionFactory sf = cfg.buildSessionFactory( ssr )) {
				Session session = sf.openSession();
				try {
					session.getTransaction().begin();
					List result = session.createSQLQuery( "select column_name from information_schema.columns where table_name = 'columnsupdatetestentity'").getResultList();
					Assert.assertEquals(3, result.size());
					session.getTransaction().commit();
				}
				catch ( Throwable t ) {
					if ( session.getTransaction().isActive() ) {
						session.getTransaction().rollback();
					}
					throw t;
				}
				finally {
					session.close();
				}
			}
		}
		finally {
			StandardServiceRegistryBuilder.destroy( ssr );
		}
	}

	private static Configuration buildConfiguration(Class<?> clazz) {
		Configuration cfg = new Configuration();
		cfg.setProperty( AvailableSettings.HBM2DDL_AUTO, "update" );
		cfg.setProperty( AvailableSettings.SHOW_SQL, "true" );
		cfg.setProperty( AvailableSettings.FORMAT_SQL, "true" );
		cfg.addAnnotatedClass( clazz );
		return cfg;
	}

	@Entity(name = "ColumnsUpdateTestEntity")
	public static class BaseFourColumnEntity {
		@Id
		@GeneratedValue
		Integer id;

		String columnOne;
		String columnTwo;
		String columnThree;

		public String getEntityName() {
			return "ColumnsUpdateTestEntity";
		}
	}

	@Entity(name = "ColumnsUpdateTestEntity")
	public static class ThreeColumnEntity {
		@Id
		@GeneratedValue
		Integer id;

		String columnOne;
		String columnTwo;

		public String getEntityName() {
			return "ColumnsUpdateTestEntity";
		}
	}
}