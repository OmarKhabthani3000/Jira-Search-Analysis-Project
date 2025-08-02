package org.hibernate.ejb.test.ejb3configuration;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.hibernate.ejb.Ejb3Configuration;
import org.hibernate.ejb.test.Distributor;


import junit.framework.TestCase;

public class EJB3ConfigurationHibernateCfgXmlInitTest extends TestCase 
{
	private static final String URL = "jdbc:hsqldb:.";
	private static final String JDBC_DRIVER = "org.hsqldb.jdbcDriver";
	private static File hibernateCfgXml;
	private static final String HIBERNATE_CFG_XML_DIR="bin";//should be on classpath
	private static final String HIBERNATE_CFG_XML_FILENAME="hibernate_cfg_xml";
	Ejb3Configuration cfg;
	
	public void initWitthoutHibernateCfg()
	{
		Properties props = new Properties();
		props.setProperty("hibernate.dialect", "org.hibernate.dialect.HSQLDialect");
		props.setProperty("hibernate.connection.driver_class", JDBC_DRIVER);
		props.setProperty("hibernate.connection.username","sa");
		props.setProperty("hibernate.connection.password","");
		props.setProperty("hibernate.connection.url",URL);
		props.setProperty("hibernate.max_fetch_depth","3");
		props.setProperty("javax.persistence.transactionType","RESOURCE_LOCAL" );//not necessary default is RESOURCE_LOCAL

		cfg = new Ejb3Configuration();
		cfg.addProperties(props);
		cfg.addAnnotatedClass(Distributor.class);
	}
	
	@Override
	public void setUp()
	{
		initWitthoutHibernateCfg();
	}
	
	/*
	 * Should fail
	 */
	
	public void testPersistWithoutHibernateCfg()
	{
		EntityManagerFactory emf = cfg.buildEntityManagerFactory();
		EntityManager em = emf.createEntityManager();
		try
		{
			Distributor dist = new Distributor();
			dist.setName("Amazon");
			em.persist(dist);
		}
		catch(NullPointerException npe)
		{
			fail("Shouldn't occur if Hibernate listeners are initialized");
		}
	}
	
	/*
	 * Added as otherwise javax.persistence.PersistenceException: org.hibernate.exception.SQLGrammarException: could not load an entity: [org.hibernate.ejb.test.Distributor#0]
	 * is thrown
	 */
	
	private void createDistributorTable()
	{
		try 
		{
			Class.forName(JDBC_DRIVER);
			Connection conn = DriverManager.getConnection(URL, "sa", "");
			Statement stmt = conn.createStatement();
			stmt.execute("drop table DISTRIBUTOR if exists;"+
					"create table DISTRIBUTOR (ID INTEGER IDENTITY PRIMARY KEY, NAME varchar(64));" +
					"insert into DISTRIBUTOR (NAME) VALUES('EBAY');");
		} 
		catch (SQLException e) 
		{
			fail("DISTRIBUTOR Table creation failed");
		}
		catch (ClassNotFoundException e) 
		{
			fail("HSQLDB driver not found!");
		}
	}
	
	/*
	 * Should fail
	 */
	public void testFindWithoutHibernateCfg()
	{
		
		EntityManagerFactory emf = cfg.buildEntityManagerFactory();
		EntityManager em = emf.createEntityManager();
		try
		{
			createDistributorTable();
			Distributor dist = em.find(Distributor.class, 0);
		}
		catch(IllegalArgumentException iae)
		{
			fail("\nShouldn't occur if Hibernate listeners are initialized");
		} 
		
	}
	
	private  void initWithHibernateCfgXML() {
		if(hibernateCfgXml==null)
		{
			try
			{
				hibernateCfgXml = new File(HIBERNATE_CFG_XML_DIR+"/"+HIBERNATE_CFG_XML_FILENAME);
				hibernateCfgXml.deleteOnExit();
				BufferedWriter writer = new BufferedWriter(new FileWriter(hibernateCfgXml));
				writer.write("<!DOCTYPE hibernate-configuration PUBLIC\n\"-//Hibernate/Hibernate Configuration DTD 3.0//EN\"\n\"http://hibernate.sourceforge.net/hibernate-configuration-3.0.dtd\">"+
						"<hibernate-configuration><session-factory> </session-factory></hibernate-configuration>");
				writer.close();
			}
			catch(IOException ioe)
			{
				fail("Couldn't create config file '"+HIBERNATE_CFG_XML_FILENAME+"' on Dir '"+HIBERNATE_CFG_XML_DIR+"'");
			}
		}
		cfg.configure(HIBERNATE_CFG_XML_FILENAME);
	}
	
	/*
	 * Should pass
	 */
	public void testFindWithHibernateCfg()
	{
		initWithHibernateCfgXML();
		testFindWithoutHibernateCfg();
	}
	
	/*
	 * Should pass
	 */
	public void testPersistWithHibernateCfg()
	{
		initWithHibernateCfgXML();
		testPersistWithoutHibernateCfg();
	}

		
	

}
