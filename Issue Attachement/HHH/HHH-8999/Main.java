package hibernatetest;

import java.util.List;
import javax.persistence.*;

public class Main {

  public static void main(String[] args) throws Exception {
    test();
  }

  private static void test() {
    
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpaDemo");

    EntityManager em = emf.createEntityManager();
    EntityTransaction tx = em.getTransaction();
    tx.begin();
    Query query = em.createQuery("select s from MyEntity s");
    try {
      List l =  query.getResultList();//returns 3 objects
      em.remove(l.get(0));
      em.remove(l.get(1));//throws exception
      System.out.println(l == null ? null : l.size());
    } catch (Exception e) {
      e.printStackTrace();
    }
    tx.rollback();
    em.close();
    emf.close();
  }
}
