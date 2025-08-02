package org.hibernate.hibernate5.constraintname;

import java.util.Objects;

import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Environment;
import org.hibernate.dialect.HSQLDialect;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.ForeignKey;
import org.hibernate.mapping.ManyToOne;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Table;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ecofin.util.junit.TestClassLoaderUtil;
import com.ecofin.util.junit.TestClassLoaderUtil.RuntimeClassPathEntry;

/**
 * Bug posted to https://hibernate.atlassian.net/browse/HHH-10207
 */
public class ConstraintNameTest {
  
  private static final String BUG = "Bug in Hibernate 5.02 Final";

  @BeforeClass
  public static void setUpOnce() {
    TestClassLoaderUtil.addToClassLoader(RuntimeClassPathEntry.HIBERNATE);
    TestClassLoaderUtil.addToClassLoader(RuntimeClassPathEntry.HSQLDB);
  }

  @Test
  public void testOneToMany() throws Exception {
    Metadata metadata = getMetadata("org/hibernate/hibernate5/constraintname/person_manytoone.hbm.xml");
    PersistentClass person = metadata.getEntityBinding("org.hibernate.hibernate5.constraintname.Person");
    Property personGroup = person.getProperty("persongroup");
    ManyToOne value = (ManyToOne) personGroup.getValue();
    String name = value.getForeignKeyName();
    String expectedConstraintName = "person_persongroup_fk";
    Assert.assertTrue("Constraint name should be " + expectedConstraintName + " but it is " + name,
        Objects.equals(name, expectedConstraintName));
  }
  
  @Test
  public void testSet() throws Exception {
    Metadata metadata = getMetadata("org/hibernate/hibernate5/constraintname/person_set.hbm.xml");
    Collection persons = metadata.getCollectionBinding("org.hibernate.hibernate5.constraintname.PersonGroup.persons");
    Table table = persons.getCollectionTable();
    ForeignKey foreignKey = (ForeignKey) table.getForeignKeyIterator().next();
    String name = foreignKey.getName();
    String expectedConstraintName = "person_persongroup_fk";
    Assert.assertTrue(BUG + " Constraint name should be " + expectedConstraintName + " but it is " + name,
        Objects.equals(name, expectedConstraintName));
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
