package org.hibernate.tool.issues.hbx1517;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Properties;

import org.hibernate.cfg.reveng.DefaultReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.OverrideRepository;
import org.hibernate.cfg.reveng.ReverseEngineeringStrategy;
import org.hibernate.tool.api.metadata.MetadataDescriptor;
import org.hibernate.tool.api.metadata.MetadataDescriptorFactory;
import org.hibernate.tool.hbm2x.POJOExporter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestCase {
	
	private Connection connection = null;
	private Statement statement = null;
	private static File OUTPUT_DIR = new File("target/generated-output");
	
	@Before
	public void before() throws Exception {
		removeDir(OUTPUT_DIR);
		createDatabase();
	}
	
	@Test
	public void testIt() throws Exception {
		ReverseEngineeringStrategy strategy = getOverrideRepository()
				.getReverseEngineeringStrategy(new DefaultReverseEngineeringStrategy());
		MetadataDescriptor mdd = MetadataDescriptorFactory.createJdbcDescriptor(
				strategy,
				getHibernateProperties(), 
				true);
		POJOExporter exporter = new POJOExporter();
		exporter.setMetadataDescriptor(mdd);
		exporter.getProperties().setProperty("ejb3", "true");
		exporter.getProperties().setProperty("jdk5", "true");
		File outputDir = new File("target/generated-output");
		exporter.setOutputDirectory(outputDir);
		assertFalse(outputDir.exists());
		exporter.start();
		assertTrue(outputDir.exists());
	}
	
	@After
	public void after() throws Exception {
		dropDatabase();
	}
	
	
	private void createDatabase() throws Exception {
		String url = "jdbc:sqlserver://localhost:1433";
		connection = DriverManager
				.getDriver(url)
				.connect(url, getConnectionProperties());
		statement = connection.createStatement();
		statement.execute("USE [master]");
		statement.execute("CREATE DATABASE [mydb]");
		statement.execute("USE [mydb]");
		statement.execute("SET ANSI_NULLS ON");
		statement.execute("SET QUOTED_IDENTIFIER ON");
		statement.execute(""
				+ "CREATE TABLE [dbo].[hibernate_test]("
				+ "	[TableID] [uniqueidentifier] NOT NULL,"
				+ "	[TableValue] [varchar](50) NOT NULL,"
				+ " CONSTRAINT [PK_hibernate_test] PRIMARY KEY CLUSTERED "
				+ "("
				+ "	[TableID] ASC"
				+ ")WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]"
				+ ") ON [PRIMARY]");
	}
	
	private void dropDatabase() throws Exception {
		statement.execute("DROP TABLE [dbo].[hibernate_test]");
		statement.execute("USE [master]");
		statement.execute("DROP DATABASE [mydb]");
		statement.close();
		connection.close();
		connection = null;
	}
	
	private static Properties getConnectionProperties() {
		Properties connectionProperties = new Properties();
		connectionProperties.put("user", "sa");
		connectionProperties.put("password", "P@55w0rd");
		return connectionProperties;
	}
	
	private OverrideRepository getOverrideRepository() {
		String revengString = ""
				+ "<?xml version='1.0' encoding='UTF-8'?>                                               \n"
				+ "<!DOCTYPE hibernate-reverse-engineering                                              \n"
				+ "  SYSTEM 'http://hibernate.sourceforge.net/hibernate-reverse-engineering-3.0.dtd'>   \n"
				+ "                                                                                     \n"
				+ "<hibernate-reverse-engineering>                                                      \n"
				+ "  <type-mapping>                                                                     \n"
				+ "    <sql-type jdbc-type='NVARCHAR' hibernate-type='string'/>                         \n"
				+ "  </type-mapping>                                                                    \n"
				+ "  <table-filter                                                                      \n"
				+ "     match-catalog='mydb'                                                            \n"
				+ "     match-schema='dbo'                                                              \n"
				+ "		match-name='hibernate_.*' />                                                    \n"
				+ "</hibernate-reverse-engineering>                                                       ";
		OverrideRepository result = new OverrideRepository();
		result.addInputStream(new ByteArrayInputStream(revengString.getBytes()));
		return result;
	}
	
	private static Properties getHibernateProperties() {
		Properties properties = new Properties();
		properties.put("hibernate.dialect", "org.hibernate.dialect.SQLServer2012Dialect");
		properties.put("hibernate.connection.driver", "class com.microsoft.sqlserver.jdbc.SQLServerDriver");
		properties.put("hibernate.connection.username", "sa");
		properties.put("hibernate.connection.password", "P@55w0rd");
		properties.put("hibernate.connection.url", "jdbc:sqlserver://localhost:1433");
		properties.put("hibernate.default_schema", "dbo");
		properties.put("hibernate.default_catalog", "mydb");
		return properties;
	}
	
	private void removeDir(File dir) {
		if (!dir.exists()) return;
		for (String str : dir.list()) {
			File f = new File(dir, str);
			if (f.exists()) {
				if (f.isDirectory()) {
					removeDir(f);
				} else {
					f.delete();
				}
			}
		}
		dir.delete();
	}
	


}
