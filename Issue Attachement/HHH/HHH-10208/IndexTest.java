package org.hibernate.hibernate5.index;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Environment;
import org.hibernate.dialect.HSQLDialect;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ecofin.util.junit.TestClassLoaderUtil;
import com.ecofin.util.junit.TestClassLoaderUtil.RuntimeClassPathEntry;

/**
 * Bug posted to https://hibernate.atlassian.net/browse/HHH-10208
 */
public class IndexTest {
  
  private static final String BUG = "Bug in Hibernate 5.02 Final";

  private PrintStream old;
  ByteArrayOutputStream byteStream;
  
  @BeforeClass
  public static void setUpOnce() {
    TestClassLoaderUtil.addToClassLoader(RuntimeClassPathEntry.HIBERNATE);
    TestClassLoaderUtil.addToClassLoader(RuntimeClassPathEntry.HSQLDB);
  }

  @Test
  public void testOneToMany() throws Exception {
    Metadata metadata = getMetadata("org/hibernate/hibernate5/index/person_manytoone.hbm.xml");
    changeSystemOut();
    metadata.getSessionFactoryBuilder().build();

    String expectedIndexName = "person_persongroup_index";
    String ddlText = byteStream.toString();
    changeSystemOutBack();
    
    Assert.assertTrue(BUG + " Index expected with name " + expectedIndexName + " in output: " + ddlText,
        ddlText.contains(expectedIndexName));
  }
  
  @Test
  public void testProperty() throws Exception {
    Metadata metadata = getMetadata("org/hibernate/hibernate5/index/person_property.hbm.xml");
    changeSystemOut();
    metadata.getSessionFactoryBuilder().build();
    
    String expectedIndexName = "person_name_index";
    String ddlText = byteStream.toString();
    changeSystemOutBack();
    
    Assert.assertTrue(BUG + " Index expected with name " + expectedIndexName + " in output: " + ddlText,
        ddlText.contains(expectedIndexName));
  }
  
  @Test
  public void testPropertyColumn() throws Exception {
    Metadata metadata = getMetadata("org/hibernate/hibernate5/index/person_propertycolumn.hbm.xml");
    changeSystemOut();
    metadata.getSessionFactoryBuilder().build();
    
    String expectedIndexName = "person_name_index";
    String ddlText = byteStream.toString();
    changeSystemOutBack();
    
    Assert.assertTrue("Index expected with name " + expectedIndexName + " in output: " + ddlText,
        ddlText.contains(expectedIndexName));
  }

  private void changeSystemOut() {
    byteStream = new ByteArrayOutputStream();
    PrintStream printStream = new PrintStream(byteStream);
    old = System.out;
    System.setOut(printStream);
  }
  
  private void changeSystemOutBack() {
    System.out.flush();
    System.setOut(old);
  }

  private Metadata getMetadata(String mappingFile) {
    StandardServiceRegistryBuilder standardRegistryBuilder = new StandardServiceRegistryBuilder();
    standardRegistryBuilder.applySetting("hibernate.connection.driver_class", "org.hsqldb.jdbcDriver");
    standardRegistryBuilder.applySetting("hibernate.connection.username", "sa");
    standardRegistryBuilder.applySetting("hibernate.connection.password", "");
    standardRegistryBuilder.applySetting("hibernate.connection.url", "jdbc:hsqldb:mem:testdb");
    standardRegistryBuilder.applySetting("hibernate.dialect", HSQLDialect.class.getName());
    standardRegistryBuilder.applySetting(Environment.HBM2DDL_AUTO, "create");
    standardRegistryBuilder.applySetting(Environment.SHOW_SQL, "true");
    standardRegistryBuilder.applySetting(Environment.FORMAT_SQL, "true");
    
    StandardServiceRegistry standardRegistry = standardRegistryBuilder.build();
    MetadataSources metaDataSources = new MetadataSources(standardRegistry);
    return metaDataSources.addResource(mappingFile).buildMetadata();
  }

}
