package hib;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

public class Testcase {

  public static void main(String[] args) {
    select();
  }

  static void select() {
    EntityManagerFactory factory = Persistence.createEntityManagerFactory("e2eSwift");

    try {
      EntityManager em = factory.createEntityManager();
      EntityTransaction transaction = null;
      try {
        // Get a transaction
        transaction = em.getTransaction();
        // Begin the transaction
        transaction.begin();
        var list = em.createQuery("select distinct a.entityB from EntityA a ", EntityA.class)
            .setMaxResults(2)
            .getResultList();


        /*
          TEST TEST TEST!!! LOOK INTO GENERATED SQL BY SETTING       <property name="hibernate.show_sql" value="true"/>
          Hibernate 6.2.1 produces correct sql:
          select * from (select distinct e2_0.tableBId c0 from TABLE_A e1_0 join TABLE_B e2_0 on e2_0.tableBId=e1_0.tableBId) where rownum<=?

          Hibernate 6.2.1 produces WRONG sql:
          select * from (select distinct e2_0.tableBId c0,rownum rn from TABLE_A e1_0 join TABLE_B e2_0 on e2_0.tableBId=e1_0.tableBId) r_0_ where r_0_.rn<=? order by r_0_.rn

          this is wrong because if you include rownum as column and distinct over it then all rows will become unique even the real duplicates

         */


        transaction.commit();
      } catch (RuntimeException e) {
        if (transaction != null) {
          transaction.rollback();
        }
        throw e;
      } finally {
        em.close();
      }
    } finally {
      factory.close();
    }

  }
}
