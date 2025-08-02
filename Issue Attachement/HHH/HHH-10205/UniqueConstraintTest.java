package org.hibernate.hibernate5.uniqueconstraint;

import static org.junit.Assert.fail;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Environment;
import org.hibernate.dialect.HSQLDialect;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ecofin.util.junit.TestClassLoaderUtil;
import com.ecofin.util.junit.TestClassLoaderUtil.RuntimeClassPathEntry;

/**
 * Bug posted to https://hibernate.atlassian.net/browse/HHH-10205
 */
public class UniqueConstraintTest {
  
  private static final String BUG = "Bug in Hibernate 5.02 Final";

  @BeforeClass
  public static void setUpOnce() {
    TestClassLoaderUtil.addToClassLoader(RuntimeClassPathEntry.HIBERNATE);
    TestClassLoaderUtil.addToClassLoader(RuntimeClassPathEntry.HSQLDB);
  }

  @Test
  public void testUnique() throws Exception {
    try (SessionFactory sessionFactory = getSessionFactory("org/hibernate/hibernate5/uniqueconstraint/person_unique.hbm.xml")) {
      runSingle(sessionFactory, "Unique", "", "Two persons with same name cannot be inserted");
    }
  }
  
  /**
   * No unique key is created here
   * 
   * @throws Exception
   */
  @Test
  public void testUniqueKey() throws Exception {
    try (SessionFactory sessionFactory = getSessionFactory("org/hibernate/hibernate5/uniqueconstraint/person_uniquekey.hbm.xml")) {
      runSingle(sessionFactory, "UniqueKey", BUG, "Two persons with same name cannot be inserted");
    }
  }

  /**
   * No unique key is created here
   * 
   * @throws Exception
   */
  @Test
  public void testCompositUnique() throws Exception {
    try (SessionFactory sessionFactory =
        getSessionFactory("org/hibernate/hibernate5/uniqueconstraint/person_composituniquekey.hbm.xml")) {
      runComposit(sessionFactory, "CompositUnique", BUG, "Two persons with same firstname&name cannot be inserted");
    }
  }
  
  /**
   * A wrong unique key is created here: only name (because on name "column"-property is given, but not on firstname)
   * 
   * @throws Exception
   */
  @Test
  public void testCompositUniqueWithOneColumnTag() throws Exception {
    try (SessionFactory sessionFactory =
        getSessionFactory("org/hibernate/hibernate5/uniqueconstraint/person_composituniquekeywithonecolumntag.hbm.xml")) {
      runComposit(sessionFactory, "CompositUniqueWithOneColumnTag", BUG, "Two persons with same firstname&name cannot be inserted");
    }
  }

  private void runSingle(SessionFactory sessionFactory, String testCase, String bug, String text) {
    try (Session session = sessionFactory.openSession()) {
      Transaction t = session.beginTransaction();
      Person person = new Person("Miller", "Joe");
      session.save(person);
      t.commit();
    }
    
    try (Session session = sessionFactory.openSession()) {
      Transaction t = session.beginTransaction();
      Person person = new Person("Miller", "Jeff");
      session.save(person);
      t.commit();
      fail(bug + " A constraintViolationException is expected: " + text);
    } catch (ConstraintViolationException e) {
      System.out.println(testCase + " works correctly");
    }
  }
  
  private void runComposit(SessionFactory sessionFactory, String testCase, String bug, String text) {
    try (Session session = sessionFactory.openSession()) {
      Transaction t = session.beginTransaction();
      Person person = new Person("Miller", "Joe");
      session.save(person);
      t.commit();
    }
    
    try (Session session = sessionFactory.openSession()) {
      Transaction t = session.beginTransaction();
      Person person = new Person("Miller", "Jeff");
      session.save(person);
      t.commit();
    } catch (ConstraintViolationException e) {
      fail(bug + "No constraintViolationException is expected. " + e);
    }
    
    try (Session session = sessionFactory.openSession()) {
      Transaction t = session.beginTransaction();
      Person person = new Person("Miller", "Joe");
      session.save(person);
      t.commit();
      fail(bug + " A constraintViolationException is expected: " + text);
    } catch (ConstraintViolationException e) {
      System.out.println(testCase + " works correctly");
    }
  }
  
  private SessionFactory getSessionFactory(String mappingFile) {
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
    return metaDataSources.addResource(mappingFile).buildMetadata().getSessionFactoryBuilder().build();
  }

}
