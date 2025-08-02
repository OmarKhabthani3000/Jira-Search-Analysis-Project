package org.hibernate.hhh9745;

import javax.sql.DataSource;

import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.cfg.Environment;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.hbm2ddl.SchemaUpdate;
import org.hibernate.tool.hbm2ddl.SchemaValidator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

/**
 * @author Steve Ebersole
 */
public class SimpleTest {
	private StandardServiceRegistry serviceRegistry;
	private MetadataImplementor metadata;

	@Before
	public void setUp() {
		serviceRegistry = new StandardServiceRegistryBuilder()
				.applySetting( Environment.SHOW_SQL, true )
				.applySetting( Environment.FORMAT_SQL, true )
				.applySetting( Environment.AUTOCOMMIT, false )
				.applySetting( Environment.DATASOURCE, dataSource() )
				.build();

		metadata = (MetadataImplementor) new MetadataSources( serviceRegistry )
				.addAnnotatedClass( TestEntity.class )
				.buildMetadata();

		System.out.println( "********* Starting SchemaExport for START-UP *************************" );
		SchemaExport schemaExport = new SchemaExport( serviceRegistry, metadata );
		schemaExport.create( true, true );
		System.out.println( "********* Completed SchemaExport for START-UP *************************" );
	}

	private DataSource dataSource() {
		return new EmbeddedDatabaseBuilder()
				.setType( EmbeddedDatabaseType.HSQL )
				.ignoreFailedDrops( true )
				.addDefaultScripts()
				.setScriptEncoding( "UTF-8" )
				.build();
	}

	@After
	public void tearDown() {
		System.out.println( "********* Starting SchemaExport (drop) for TEAR-DOWN *************************" );
		SchemaExport schemaExport = new SchemaExport( serviceRegistry, metadata );
		schemaExport.drop( true, true );
		System.out.println( "********* Completed SchemaExport (drop) for TEAR-DOWN *************************" );

		if ( serviceRegistry != null ) {
			StandardServiceRegistryBuilder.destroy( serviceRegistry );
		}
	}

	@Test
	public void testSchemaUpdateAndValidate() {
		System.out.println( "********* Starting SchemaUpdate for TEST *************************" );
		SchemaUpdate schemaUpdate = new SchemaUpdate( serviceRegistry, metadata );
		schemaUpdate.execute( true, true );
		System.out.println( "********* Completed SchemaUpdate for TEST *************************" );

		System.out.println( "********* Starting SchemaValidate for TEST *************************" );
		SchemaValidator schemaValidator = new SchemaValidator( serviceRegistry, metadata );
		schemaValidator.validate();
		System.out.println( "********* Completed SchemaValidate for TEST *************************" );
	}
}
