package org.hibernate.hibernate5.uniquedelegate;

import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.cfg.Environment;
import org.hibernate.dialect.HSQLDialect;
import org.hibernate.dialect.unique.UniqueDelegate;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.UniqueKey;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ecofin.util.junit.TestClassLoaderUtil;
import com.ecofin.util.junit.TestClassLoaderUtil.RuntimeClassPathEntry;

public class UniqueDelegateTest {
  
  private static final String BUG = "Bug in Hibernate 5.02 Final";

  public static int columnDefinitionUniquenessFragment = 0;
  public static int tableCreationUniqueConstraintsFragment = 0;
  
  @BeforeClass
  public static void setUpOnce() {
    TestClassLoaderUtil.addToClassLoader(RuntimeClassPathEntry.HIBERNATE);
    TestClassLoaderUtil.addToClassLoader(RuntimeClassPathEntry.HSQLDB);
  }

  @Test
  public void testUniqueDelegate() {
    boolean script = true;
    boolean export = false;
    boolean dropStatementsOnly = false;
    boolean createStatementsOnly = true;
    
    MetadataImplementor metadata = getMetadata("org/hibernate/hibernate5/uniquedelegate/person_unique.hbm.xml");
    SchemaExport se = new SchemaExport(metadata);
    se.execute(script, export, dropStatementsOnly, createStatementsOnly);
    
    Assert.assertTrue("At least one call to getColumnDefinitionUniquenessFragment() expected",
        columnDefinitionUniquenessFragment > 0);
    Assert.assertTrue(BUG + " At least one call to getTableCreationUniqueConstraintsFragment() expected",
        tableCreationUniqueConstraintsFragment > 0);
  }
  
  public static class MyHSQLDialect extends HSQLDialect {
    private final UniqueDelegate uniqueDelegate = new MyUniqueDelegate();
    
    @Override
    public UniqueDelegate getUniqueDelegate() {
      return uniqueDelegate;
    }
  }
  
  public static class MyUniqueDelegate implements UniqueDelegate {
    
    @Override
    public String getColumnDefinitionUniquenessFragment(Column column) {
      UniqueDelegateTest.columnDefinitionUniquenessFragment++;
      return "";
    }
    
    @Override
    public String getTableCreationUniqueConstraintsFragment(Table table) {
      UniqueDelegateTest.tableCreationUniqueConstraintsFragment++;
      return "";
    }
    
    @Override
    public String getAlterTableToAddUniqueKeyCommand(UniqueKey uniqueKey, Metadata metadata) {
      return "";
    }
    
    @Override
    public String getAlterTableToDropUniqueKeyCommand(UniqueKey uniqueKey, Metadata metadata) {
      return "";
    }
    
  }
  
  private MetadataImplementor getMetadata(String mappingFile) {
    StandardServiceRegistryBuilder standardRegistryBuilder = new StandardServiceRegistryBuilder();
    standardRegistryBuilder.applySetting("hibernate.connection.driver_class", "org.hsqldb.jdbcDriver");
    standardRegistryBuilder.applySetting("hibernate.connection.username", "sa");
    standardRegistryBuilder.applySetting("hibernate.connection.password", "");
    standardRegistryBuilder.applySetting("hibernate.connection.url", "jdbc:hsqldb:mem:testdb");
    standardRegistryBuilder.applySetting(Environment.HBM2DDL_AUTO, "create");
    standardRegistryBuilder.applySetting(Environment.SHOW_SQL, "true");
    standardRegistryBuilder.applySetting(Environment.FORMAT_SQL, "true");
    
    standardRegistryBuilder.applySetting(Environment.DIALECT, MyHSQLDialect.class.getName());

    StandardServiceRegistry standardRegistry = standardRegistryBuilder.build();
    MetadataSources metaDataSources = new MetadataSources(standardRegistry);
    return (MetadataImplementor) metaDataSources.addResource(mappingFile).buildMetadata();
  }

}
