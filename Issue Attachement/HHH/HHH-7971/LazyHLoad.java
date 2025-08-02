package test;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;


public class LazyHLoad {

   /**
    * @param args
    */
   public static void main(String[] args) {
      Parent p = null;
      
      try {
         SessionFactory sf = new Configuration().configure().buildSessionFactory();

         Session session = sf.getCurrentSession();
         session.beginTransaction();

         p = (Parent)session.load(Parent.class, 1L);

         session.getTransaction().commit();
      } 
      catch (HibernateException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }

      p.getChildren().size();
      
      
   }
   
   @Entity
   public static class Parent {
      private Long id;
      private List<Child> children = new ArrayList<Child>();
      
      @Id
      @GeneratedValue(strategy = GenerationType.AUTO)
      public Long getId() {
         return id;
      }
      public void setId(Long id) {
         this.id = id;
      }
      
      @OneToMany(cascade=CascadeType.ALL)
      @JoinTable(name="PARTY_ROLE_CONTACT", joinColumns={@JoinColumn(name="RC_ROLE_ID")}, inverseJoinColumns={@JoinColumn(name="RC_CONTACT_ID")})
      public List<Child> getChildren() {
         return children;
      }
      public void setChildren(List<Child> children) {
         this.children = children;
      }
   }
   
   @Entity
   public static class Child {
      private Long id;

      @Id
      @GeneratedValue(strategy = GenerationType.AUTO)
      public Long getId() {
         return id;
      }

      public void setId(Long id) {
         this.id = id;
      }
   }
}