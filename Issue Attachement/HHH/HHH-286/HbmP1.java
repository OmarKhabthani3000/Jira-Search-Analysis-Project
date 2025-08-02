package com.ops.Premium;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.annotations.Proxy;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.dialect.HSQLDialect;
import org.hibernate.tool.hbm2ddl.SchemaUpdate;

import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratorType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.OneToMany;
import javax.persistence.Version;
import java.io.Serializable;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Set;

public class HbmP1
{
   @Entity(access = AccessType.FIELD)
   @Proxy(lazy=true)
   public static class TableA implements Serializable
   {
      @Id(generate=GeneratorType.NONE)
      private TableAPk key;

      private String value;

      @Version()
      private long version;

      @OneToMany(fetch=FetchType.LAZY, cascade={CascadeType.REFRESH, CascadeType.ALL})
      @JoinColumns({
         @JoinColumn(name="companyId", insertable = false, updatable = false),
         @JoinColumn(name="value", insertable = false, updatable = false)
      })
      private Set<TableB> children;

        public TableA()
      {
      }

      public TableAPk getKey()
      {
         return key;
      }

      public void setKey(TableAPk key)
      {
         this.key = key;
      }

      public String getValue()
      {
         return value;
      }

      public long getVersion()
      {
         return version;
      }

      public void setValue(String value)
      {
         this.value = value;
      }

      public Set<TableB> getChildren()
      {
         return children;
      }

      public boolean equals(Object o)
      {
         if (this == o)
         {
            return true;
         }
         if (!(o instanceof TableA))
         {
            return false;
         }

         final TableA tableA = (TableA) o;

         if (key != null ? !key.equals(tableA.key) : tableA.key != null)
         {
            return false;
         }

         return true;
      }

      public int hashCode()
      {
         return (key != null ? key.hashCode() : 0);
      }
   }

   @Embeddable(access=AccessType.FIELD)
   public static class TableAPk implements Serializable
   {
      private String companyId;
      private String customerId;

        public TableAPk()
      {
      }

      public String getCompanyId()
      {
         return companyId;
      }

      public void setCompanyId(String companyId)
      {
         this.companyId = companyId;
      }

      public String getCustomerId()
      {
         return customerId;
      }

      public void setCustomerId(String customerId)
      {
         this.customerId = customerId;
      }

      public boolean equals(Object o)
      {
         if (this == o)
         {
            return true;
         }
         if (!(o instanceof TableAPk))
         {
            return false;
         }

         final TableAPk tableAPk = (TableAPk) o;

         if (companyId != null ? !companyId.equals(tableAPk.companyId) : tableAPk.companyId != null)
         {
            return false;
         }
         if (customerId != null ? !customerId.equals(tableAPk.customerId) : tableAPk.customerId != null)
         {
            return false;
         }

         return true;
      }

      public int hashCode()
      {
         int result;
         result = (companyId != null ? companyId.hashCode() : 0);
         result = 29 * result + (customerId != null ? customerId.hashCode() : 0);
         return result;
      }
   }

   @Entity(access = AccessType.FIELD)
   @Proxy(lazy=true)
   public static class TableB implements Serializable
   {
      @Id(generate=GeneratorType.NONE)
      private TableBPk key;

      private String value;

      private double value2;

      @Version()
      private long version;

      public TableB()
      {
      }

      public TableBPk getKey()
      {
         return key;
      }

      public void setKey(TableBPk key)
      {
         this.key = key;
      }

      public String getValue()
      {
         return value;
      }

      public void setValue(String value)
      {
         this.value = value;
      }

      public double getValue2()
      {
         return value2;
      }

      public void setValue2(double value2)
      {
         this.value2 = value2;
      }

      public long getVersion()
      {
         return version;
      }

      public boolean equals(Object o)
      {
         if (this == o)
         {
            return true;
         }
         if (!(o instanceof TableB))
         {
            return false;
         }

         final TableB tableB = (TableB) o;

         if (key != null ? !key.equals(tableB.key) : tableB.key != null)
         {
            return false;
         }

         return true;
      }

      public int hashCode()
      {
         return (key != null ? key.hashCode() : 0);
      }
   }

   @Embeddable(access=AccessType.FIELD)
   public static class TableBPk implements Serializable
   {
      private String companyId;

      private String customerId;

      public TableBPk()
      {
      }

      public String getCompanyId()
      {
         return companyId;
      }

      public void setCompanyId(String companyId)
      {
         this.companyId = companyId;
      }

      public String getCustomerId()
      {
         return customerId;
      }

      public void setCustomerId(String customerId)
      {
         this.customerId = customerId;
      }

      public boolean equals(Object o)
      {
         if (this == o)
         {
            return true;
         }
         if (!(o instanceof TableBPk))
         {
            return false;
         }

         final TableBPk tableBPk = (TableBPk) o;

         if (companyId != null ? !companyId.equals(tableBPk.companyId) : tableBPk.companyId != null)
         {
            return false;
         }
         if (customerId != null ? !customerId.equals(tableBPk.customerId) : tableBPk.customerId != null)
         {
            return false;
         }

         return true;
      }

      public int hashCode()
      {
         int result;
         result = (companyId != null ? companyId.hashCode() : 0);
         result = 29 * result + (customerId != null ? customerId.hashCode() : 0);
         return result;
      }
   }

   public static void main(String[] args) throws SQLException
   {
      AnnotationConfiguration cfg = new AnnotationConfiguration();
      cfg.addAnnotatedClass(TableA.class);
      cfg.addAnnotatedClass(TableB.class);

      cfg.setProperty("hibernate.connection.driver_class", "org.hsqldb.jdbcDriver");
      cfg.setProperty("hibernate.connection.url", "jdbc:hsqldb:mem:hbmtest");
      cfg.setProperty("hibernate.dialect", HSQLDialect.class.getName());

      cfg.setProperty("hibernate.show_sql", "true");
      SessionFactory factory = cfg.buildSessionFactory();

      SchemaUpdate update = new SchemaUpdate(cfg);
      update.execute(false, true);

      // pupulate table
      {
         Session sess = factory.openSession();

         TableA row1 = new TableA();
         row1.setKey(new TableAPk());
         row1.getKey().setCompanyId("A");
         row1.getKey().setCustomerId("B");
         row1.setValue("B");

         TableB row2 = new TableB();
         row2.setKey(new TableBPk());
         row2.getKey().setCompanyId("A");
         row2.getKey().setCustomerId("B");
         row2.setValue("B");
            row2.setValue2(1.0);

         sess.save(row1);
         sess.save(row2);
         sess.flush();
         sess.connection().commit();
         sess.close();
      }

      // lets start
      Session sess = factory.openSession();
      TableA header = (TableA) sess.createCriteria(TableA.class).list().get(0);
      System.err.println("header: " + header);

      TableB child = (TableB) sess.createCriteria(TableB.class).list().get(0);

      System.err.println("init");
      printChild("read", child);
      printChild("assoc", (TableB) header.getChildren().iterator().next());

      System.err.println("simple update");
      child.setValue2(2);

      sess.flush();

      printChild("read", child);
      printChild("assoc", (TableB) header.getChildren().iterator().next());

      System.err.println("simulate stored procedure");

      simulateStoredProcedure();

      System.err.println("refresh header (should cascade)");
      sess.refresh(header);

      printChild("read", child);
      printChild("assoc", (TableB) header.getChildren().iterator().next());

      System.err.println("refresh child");
      sess.refresh(child);

      printChild("read", child);
      printChild("assoc", (TableB) header.getChildren().iterator().next());

      System.err.println("simple update");

      child.setValue2(3);

      sess.flush();
      sess.refresh(header);

      printChild("read", child);
      printChild("assoc", (TableB) header.getChildren().iterator().next());
   }

   private static void printChild(String prefix, TableB child)
   {
      System.err.println("child " + prefix + ": " + child + " " + child.getVersion());
   }

   private static void simulateStoredProcedure() throws SQLException
   {
      Connection con = DriverManager.getConnection("jdbc:hsqldb:mem:hbmtest");
      Statement stm = con.createStatement();
      stm.executeUpdate("update HbmP1$tableB set version = version + 1");
      stm.close();
      con.commit();
      con.close();
   }
}