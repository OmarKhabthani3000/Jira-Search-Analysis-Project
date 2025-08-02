/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * Copyright (c) {DATE}, Red Hat Inc. or third-party contributors as
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

package org.hibernate.test.schemaupdate;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.Session;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.dialect.MySQLDialect;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.hbm2ddl.SchemaUpdate;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.CustomRunner;

/**
 * @author Trong Dinh
 */

@Entity
@Table(name = CapitalCaseTableNameTest.tableName)
class MyEntity {
	@Id
	public int getId() {
		return 0;
	}
	
	public void setId(final int _id) {
	}
}

@TestForIssue(jiraKey = "HHH-10011")
@RunWith(CustomRunner.class)
// uncomment this if running on Linux
//@RequiresDialect(MySQLDialect.class)
public class CapitalCaseTableNameTest {
	// set this to true if running on Linux
	protected boolean isRunningOnLinux = false; 
	protected static final String tableName = "MyEntity";
	protected String capitalDifferedTableName = tableName.toLowerCase();
	protected ServiceRegistry serviceRegistry;
	protected MetadataImplementor metadata;
	
	@Test
	public void testUpdateSchema() throws Exception {
		new SchemaUpdate(
				metadata
		).execute( true, true );
	}

	@Before
	public void setUp() {		
		StandardServiceRegistryBuilder standardServiceRegistryBuilder = new StandardServiceRegistryBuilder();
		if (!isRunningOnLinux) {
			MysqlDataSource ds = new MysqlDataSource();
			// change this if running on Windows and database is on a Linux machine
			ds.setUrl("jdbc:mysql://192.168.1.6/trongtest?user=root");
			standardServiceRegistryBuilder.applySetting(Environment.DIALECT, MySQLDialect.class.getName())
				.applySetting(Environment.DATASOURCE, ds);
		}
		serviceRegistry = standardServiceRegistryBuilder.build();
		metadata = (MetadataImplementor) new MetadataSources( serviceRegistry )
				.addAnnotatedClass( MyEntity.class )
				.buildMetadata();
		
		//this creates a table with a differing name in capitalization with the table name of our entity  
		Session session = new Configuration().buildSessionFactory( serviceRegistry ).openSession();
		session.createSQLQuery("drop table if exists " + capitalDifferedTableName).executeUpdate();
		session.createSQLQuery("create table " + capitalDifferedTableName + " (id integer not null, primary key (id))").executeUpdate();
		session.close();

		System.out.println( "********* Starting SchemaExport for START-UP *************************" );
		SchemaExport schemaExport = new SchemaExport( serviceRegistry, metadata );
		schemaExport.create( true, true );
		System.out.println( "********* Completed SchemaExport for START-UP *************************" );
	}

	@After
	public void tearDown() {
		System.out.println( "********* Starting SchemaExport (drop) for TEAR-DOWN *************************" );
		SchemaExport schemaExport = new SchemaExport( serviceRegistry, metadata );
		schemaExport.drop( true, true );
		System.out.println( "********* Completed SchemaExport (drop) for TEAR-DOWN *************************" );

		StandardServiceRegistryBuilder.destroy( serviceRegistry );
		serviceRegistry = null;
	}
}

