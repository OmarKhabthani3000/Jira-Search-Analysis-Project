package org.hibernate.test;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.Statement;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.hibernate.annotations.Immutable;
import org.hibernate.cfg.Configuration;
import org.hibernate.Session;
import org.hsqldb.jdbc.jdbcDataSource;
import org.junit.BeforeClass;
import org.junit.Test;

public class DirtyCheckTest {

  @Entity
  @Immutable
  @Table(name="immutable_person")
  public static class ImmutablePerson {
    private int id;
    private String name;
    private int accessorCount = 0;

    @Id
    public int getId() { return id; }
    public void setId(int id) { this.id = id;}

    @Column
    public String getName() {
      accessorCount++;
      return name;
    }
    public void setName(String name) { this.name = name; }

    @Transient
    public int getAccessorCount() { return accessorCount; }

    @Override
    public int hashCode() {
      return id;
    }

    @Override
    public boolean equals(Object obj) {
      return this == obj; // good enough for government work
    }
  }

  private static SessionFactory sessionFactory;

  @BeforeClass
  public static void createDb() throws Exception {
    jdbcDataSource dataSource = new jdbcDataSource();
    dataSource.setDatabase("jdbc:hsqldb:mem:MiscHibernateTests");
    dataSource.setUser("sa");
    runSql("CREATE TABLE immutable_person (id Numeric, name VARCHAR)", dataSource);
    runSql("INSERT INTO immutable_person VALUES(1, 'John Doe')", dataSource);

    Configuration cfg = new Configuration()
      .addAnnotatedClass(ImmutablePerson.class)

      .setProperty("hibernate.dialect", "org.hibernate.dialect.HSQLDialect")
      .setProperty("hibernate.connection.url", "jdbc:hsqldb:mem:MiscHibernateTests")
      .setProperty("hibernate.connection.user", "sa")
      .setProperty("hibernate.cache.use_second_level_cache", "false");

    sessionFactory = cfg.buildSessionFactory();
  }

  @Test
  public void testFlush() {
    Session session = sessionFactory.openSession();
    ImmutablePerson immutablePerson = (ImmutablePerson) session.load(ImmutablePerson.class, 1);
    assertEquals("John Doe", immutablePerson.getName());
    assertEquals(1, immutablePerson.getAccessorCount());
    immutablePerson.setName("New Name");
    session.flush();

    // Because ImmutablePerson is immutable, there should have been no need to perform dirty checks
    // on it to determine if it has changed. In fact, the accessor count will be 2, since the
    // call to flush will have triggered an additional invocation of the accessor.
    assertEquals(1, immutablePerson.getAccessorCount());
  }


  private static void runSql(String sql, DataSource dataSource) throws Exception {
    Connection connection = dataSource.getConnection();
    Statement statement = connection.createStatement();
    statement.executeUpdate(sql);
    statement.close();
    connection.close();
  }

}
