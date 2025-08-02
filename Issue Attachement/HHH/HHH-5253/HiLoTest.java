import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;

public class HiLoTest {
   private static Configuration configuration = null;
   private static SessionFactory sessionFactory = null;

   public static void main(String[] args) {
      configuration = new AnnotationConfiguration();
      configuration.configure("hibernate.cfg.xml");
      sessionFactory = configuration.buildSessionFactory();
      Session session = sessionFactory.getCurrentSession();
      session.beginTransaction();
      
      for (int i = 0; i < 7; i++) {
         A a = new A();
         session.save(a);
      } 
      session.getTransaction().commit();
      if (sessionFactory != null) {
         sessionFactory.close();
      }
   }
}
