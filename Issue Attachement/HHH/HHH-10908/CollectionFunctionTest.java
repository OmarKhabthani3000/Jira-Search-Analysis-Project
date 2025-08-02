package org.hibernate.hibernate5.collectionfunction;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Environment;
import org.hibernate.dialect.HSQLDialect;
import org.hibernate.query.Query;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ecofin.util.junit.TestClassLoaderUtil;
import com.ecofin.util.junit.TestClassLoaderUtil.RuntimeClassPathEntry;

/**
 * Bug posted to https://hibernate.atlassian.net/browse/HHH-10908
 */
public class CollectionFunctionTest {
  
  private PrintStream old;
  private ByteArrayOutputStream byteStream;
  
  @BeforeClass
  public static void setUpOnce() {
    TestClassLoaderUtil.addToClassLoader(RuntimeClassPathEntry.HIBERNATE);
    TestClassLoaderUtil.addToClassLoader(RuntimeClassPathEntry.HSQLDB);
  }
  
  @Test
  public void testSize() throws Exception {
    Metadata metadata = getMetadata("org/hibernate/hibernate5/collectionfunction/person.hbm.xml");
    
    try (SessionFactory sessionFactory = metadata.getSessionFactoryBuilder().build()) {
      try (Session session = sessionFactory.openSession()) {
        Transaction tx = session.beginTransaction();
        PersonGroup group = new PersonGroup("group");
        group.addPerson(new Person("person1"));
        session.save(group);
        tx.commit();
        
        changeSystemOut();
        Query<PersonGroup> q = session.createQuery("from PersonGroup where size(persons) = 1");
        List<PersonGroup> groups = q.getResultList();
        
        Assert.assertTrue("1 Group contains exactly one person, got " + groups.size(), groups.size() == 1);
        
        String notExpectedWarning = "HHH90000016";
        String output = byteStream.toString();
        changeSystemOutBack();
        
        Assert.assertTrue("BUG: No deprectated warning expected in output", output.contains(notExpectedWarning));
      }
    }
  }
  
  @Test
  public void testElements() throws Exception {
    Metadata metadata = getMetadata("org/hibernate/hibernate5/collectionfunction/person.hbm.xml");
    
    try (SessionFactory sessionFactory = metadata.getSessionFactoryBuilder().build()) {
      try (Session session = sessionFactory.openSession()) {
        Transaction tx = session.beginTransaction();
        PersonGroup group = new PersonGroup("group");
        Person person = new Person("person1");
        group.addPerson(person);
        session.save(group);
        tx.commit();
        
        changeSystemOut();
        Query<PersonGroup> q = session.createQuery("from PersonGroup where :person in elements(persons)");
        q.setParameter("person", person);
        List<PersonGroup> groups = q.getResultList();
        
        Assert.assertTrue("1 Group contains person, got " + groups.size(), groups.size() == 1);
        
        String notExpectedWarning = "HHH90000016";
        String output = byteStream.toString();
        changeSystemOutBack();
        
        Assert.assertTrue("BUG: No deprectated warning expected in output", output.contains(notExpectedWarning));
      }
    }
  }
  
  private Metadata getMetadata(String mappingFile) {
    StandardServiceRegistryBuilder standardRegistryBuilder = new StandardServiceRegistryBuilder();
    standardRegistryBuilder.applySetting("hibernate.connection.driver_class", "org.hsqldb.jdbcDriver");
    standardRegistryBuilder.applySetting("hibernate.connection.username", "sa");
    standardRegistryBuilder.applySetting("hibernate.connection.password", "");
    standardRegistryBuilder.applySetting("hibernate.connection.url", "jdbc:hsqldb:mem:testdb");
    standardRegistryBuilder.applySetting("hibernate.dialect", HSQLDialect.class.getName());
    standardRegistryBuilder.applySetting(Environment.HBM2DDL_AUTO, "create");
    
    StandardServiceRegistry standardRegistry = standardRegistryBuilder.build();
    MetadataSources metaDataSources = new MetadataSources(standardRegistry);
    return metaDataSources.addResource(mappingFile).buildMetadata();
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
  
}
