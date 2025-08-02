import de.shd.ecoro.server.system.persistence.MyEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.query.Query;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class HibernateBugTest
{
   private static SessionFactory sessionFactory;

   @BeforeAll
   public static void setUp() throws Exception
   {
      StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
            //.applySetting("hibernate.connection.url", "jdbc:Cache://localhost:1972/DB")
            //.applySetting("hibernate.dialect", "org.hibernate.community.dialect.CacheDialect")
            .applySetting("hibernate.connection.url", "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1")
            .applySetting("hibernate.dialect", "org.hibernate.dialect.H2Dialect")
            .applySetting("hibernate.hbm2ddl.auto", "create-drop")
            .build();

      MetadataSources metadataSources = new MetadataSources(registry)
            .addAnnotatedClass(MyEntity.class);

      sessionFactory = metadataSources.buildMetadata().buildSessionFactory();
   }

   private static void insertData(Session session)
   {
      MyEntity entity = new MyEntity();
      entity.setName("Name1");
      session.save(entity);
      MyEntity entity2 = new MyEntity();
      entity2.setName("Name2");
      session.save(entity2);
   }

   @AfterAll
   public static void tearDown() throws Exception
   {
      sessionFactory.close();
   }

   @Test
   public void testFirstResult()
   {
      Session session = sessionFactory.openSession();
      session.beginTransaction();

      try
      {
         insertData(session);

         Query<MyEntity> query = session.createQuery("from MyEntity", MyEntity.class);
         query.setMaxResults(10);
         Assertions.assertEquals(query.getResultList().size(), 2);

         query = session.createQuery("from MyEntity", MyEntity.class);
         query.setMaxResults(10);
         query.setFirstResult(1);
         Assertions.assertEquals(query.getResultList().size(), 1);

         query = session.createQuery("from MyEntity", MyEntity.class);
         query.setMaxResults(10);
         Assertions.assertEquals(query.getResultList().size(), 2);

         session.getTransaction().rollback();
      }
      finally
      {
         session.close();
      }
   }

}
