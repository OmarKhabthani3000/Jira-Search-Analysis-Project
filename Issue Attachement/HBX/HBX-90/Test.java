import javax.ejb.Entity;
import javax.ejb.Id;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;

public class Test
{
   public static class TestId
   {
      private Long id;

      public Long getId()
      {
         return id;
      }
      
      public void setId(Long id)
      {
         this.id = id;
      }
   }

   @Entity
   public static class TestEntity extends TestId
   {
      @Id
      public Long getId()
      {
         return super.getId();
      }
   }

   public static void main(String[] args)
   {
      AnnotationConfiguration config = new AnnotationConfiguration();
      config.addAnnotatedClass(TestEntity.class);
      SessionFactory factory = config.buildSessionFactory();
   }
}