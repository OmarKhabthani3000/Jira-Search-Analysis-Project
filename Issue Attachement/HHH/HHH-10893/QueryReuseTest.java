package org.hibernate.hibernate5.queryreuse;

import java.util.ArrayList;
import java.util.Collection;
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
 * Bug posted to https://hibernate.atlassian.net/browse/HHH-10893
 */
public class QueryReuseTest {
  
  private static final String DRIVER = "org.hsqldb.jdbcDriver";
  private static final String URL = "jdbc:hsqldb:mem:testdb";
  
  private static final String USER = "sa";
  private static final String PASSWORD = "";
  
  @BeforeClass
  public static void setUpOnce() {
    TestClassLoaderUtil.addToClassLoader(RuntimeClassPathEntry.HIBERNATE);
    TestClassLoaderUtil.addToClassLoader(RuntimeClassPathEntry.HSQLDB);
    TestClassLoaderUtil.addToClassLoader(RuntimeClassPathEntry.LOGBACK);
  }
  
  @Test
  public void test() throws Exception {
    Metadata metadata = getMetadata("org/hibernate/hibernate5/queryreuse/person.hbm.xml");
    try (SessionFactory sessionFactory = metadata.getSessionFactoryBuilder().build()) {
      
      try (Session session = sessionFactory.openSession()) {
        
        Transaction tx = session.beginTransaction();
        for (int i = 0; i < 20; i++) {
          Person p1 = new Person(i, "p" + i);
          session.save(p1);
        }
        tx.commit();
        
        Collection<Long> ids = new ArrayList<>();
        Query<Long> q = session.createQuery("select id from Person where id in (:ids) order by id");
        for (int i = 0; i < 10; i++) {
          ids.add(Long.valueOf(i));
        }
        q.setParameter("ids", ids);
        q.getResultList();
        
        ids.clear();
        for (int i = 10; i < 20; i++) {
          ids.add(Long.valueOf(i));
        }
        // reuse the same query, but set new collection parameter
        q.setParameter("ids", ids);
        List<Long> foundIds = q.getResultList();
        
        Assert.assertTrue("expects 10 rows, got " + foundIds.size(), foundIds.size() == ids.size());
        Assert.assertTrue("All person ids between 10 and 20 expected, got " + foundIds, ids.equals(foundIds));
      }
      sessionFactory.close();
    }
  }
  
  private Metadata getMetadata(String mappingFile) {
    StandardServiceRegistryBuilder standardRegistryBuilder = new StandardServiceRegistryBuilder();
    standardRegistryBuilder.applySetting("hibernate.connection.driver_class", DRIVER);
    standardRegistryBuilder.applySetting("hibernate.connection.username", USER);
    standardRegistryBuilder.applySetting("hibernate.connection.password", PASSWORD);
    standardRegistryBuilder.applySetting("hibernate.connection.url", URL);
    standardRegistryBuilder.applySetting("hibernate.dialect", HSQLDialect.class.getName());
    standardRegistryBuilder.applySetting(Environment.HBM2DDL_AUTO, "create");
    standardRegistryBuilder.applySetting(Environment.SHOW_SQL, "true");
    standardRegistryBuilder.applySetting(Environment.FORMAT_SQL, "true");
    
    StandardServiceRegistry standardRegistry = standardRegistryBuilder.build();
    MetadataSources metaDataSources = new MetadataSources(standardRegistry);
    return metaDataSources.addResource(mappingFile).buildMetadata();
  }
  
}
