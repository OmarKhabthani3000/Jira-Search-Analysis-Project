
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.AccessType;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddableSuperclass;
import javax.persistence.Entity;
import javax.persistence.GeneratorType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.criterion.Restrictions;
import org.hibernate.tool.hbm2ddl.SchemaExport;

/**
 * FetchModeTest
 */
public class FetchModeTest extends TestCase
{
   private final static Log log = LogFactory.getLog(FetchModeTest.class);
   
   
   SessionFactory factory;

   /* (non-Javadoc)
    * @see junit.framework.TestCase#setUp()
    */
   @Override
   protected void setUp() throws Exception
   {
      AnnotationConfiguration cfg = new AnnotationConfiguration();
      cfg.setProperty("hibernate.dialect", "org.hibernate.dialect.HSQLDialect");
      cfg.setProperty("hibernate.connection.driver_class", "org.hsqldb.jdbcDriver");
      cfg.setProperty("hibernate.connection.url", "jdbc:hsqldb:data/fetchmodetest");
      cfg.setProperty("hibernate.connection.username", "sa");
      cfg.setProperty("hibernate.connection.password", "");
      cfg.setProperty("hibernate.cache.use_second_level_cache", "false");
      cfg.setProperty("hibernate.show_sql", "true");

      cfg.addAnnotatedClass(AbstractEntity.class);
      cfg.addAnnotatedClass(Parent.class);
      cfg.addAnnotatedClass(Child.class);
      factory = cfg.buildSessionFactory();

      SchemaExport export = new SchemaExport(cfg);
      export.create(true, true);
   }

   /* (non-Javadoc)
    * @see junit.framework.TestCase#tearDown()
    */
   @Override
   protected void tearDown() throws Exception
   {
      factory.close();
      factory = null;
   }

   /**
    * here we go 
    */
   public void testFetchMode()
   {
      Parent parent1 = createParent();
      log.info("Size: " + parent1.children.size());

      Parent parent2 = findParent();
      log.info("Size: " + parent2.children.size()); // Bumm
   }

   /**
    * creates a parent instance with a child and saves it  
    * @return the created parent instance
    */
   Parent createParent()
   {
      Parent parent = new Parent();
      parent.id = Integer.valueOf(1);
      parent.name = "Homer";
      Child child = new Child();
      child.id = Integer.valueOf(1);
      child.name = "Bart";
      child.parent = parent;
      parent.children.add(child);

      Session s = factory.openSession();
      Transaction tx = s.beginTransaction();
      try
      {
         s.save(parent);
         tx.commit();
      }
      catch (HibernateException e)
      {
         tx.rollback();
      }
      finally
      {
         s.close();
      }
      return parent;
   }

   /**
    * selects the previously created parent
    * @return the selected parent instance
    */
   Parent findParent()
   {
      Parent parent = null;
      Session s = factory.openSession();
      Transaction tx = s.beginTransaction();
      try
      {
         Criteria c = s.createCriteria(Parent.class);
         c.setFetchMode(Parent.CHILDREN, FetchMode.SELECT);
         c.add(Restrictions.idEq(Integer.valueOf(1)));
         parent = (Parent) c.uniqueResult();
         tx.commit();
      }
      catch (HibernateException e)
      {
         tx.rollback();
      }
      finally
      {
         s.close();
      }
      return parent;
   }

   /**
    * AbstractEntity
    */
   @EmbeddableSuperclass(access = AccessType.FIELD)
   static abstract class AbstractEntity implements Serializable
   {
      @Id(generate = GeneratorType.NONE)
      Integer id;
      @Version
      Integer version;
      @Basic
      @Column(nullable = false)
      String name;
   }

   /**
    * Parent
    */
   @Entity(access = AccessType.FIELD)
   @Table(name="PARENT")
   static class Parent extends AbstractEntity
   {
      static final String CHILDREN = "children";
      @OneToMany(cascade = CascadeType.ALL)
      @JoinColumn(name = Child.FK_PARENT)
      Set<Child> children = new HashSet<Child>();
   }

   /**
    * Child
    */
   @Entity(access = AccessType.FIELD)
   @Table(name="CHILD")
   static class Child extends AbstractEntity
   {
      static final String FK_PARENT = "parent_id";
      @ManyToOne()
      @JoinColumn(name = FK_PARENT, nullable = false)
      Parent parent;
   }


}
