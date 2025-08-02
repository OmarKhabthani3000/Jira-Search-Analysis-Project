package foo;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

public class Main {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("HBTestPU");
        EntityManager em = emf.createEntityManager();

        // Creating initial records
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        TestEntity te = new TestEntity("value11", "value12");
        em.persist(te);
        te = new TestEntity("value21", "value22");
        em.persist(te);
        te = new TestEntity("value31", "value32");
        em.persist(te);
        te = new TestEntity("value41", "value42");
        em.persist(te);
        te = new TestEntity("value51", "value52");
        em.persist(te);
        em.flush();
        tx.commit();

        // Showing initial records
        tx = em.getTransaction();
        tx.begin();
        List resultList = em.createQuery("select te from TestEntity te").getResultList();
        System.out.println("\n\nInitial records in table:\n" + resultList);
        tx.commit();

        // Normal query
        tx = em.getTransaction();
        tx.begin();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery();
        Root cr = cq.from(TestEntity.class);
        cq.select(cr);
        cq.where(
                cb.or(
                    cb.ge(cr.get("id"), 4L),
                    cb.like(cr.get("field1"), "%2%")
                    )
                );
        // Should generate SQL statement:
//        select
//            testentity0_.id as id0_,
//            testentity0_.field1 as field2_0_,
//            testentity0_.field2 as field3_0_
//        from
//            TestEntity testentity0_
//        where
//            testentity0_.id>=4
//            or testentity0_.field1 like '%2%'
        resultList = em.createQuery(cq).getResultList();
        System.out.println("\n\nQuery results with CompoundPredicate (disjunction)");
        System.out.println("Should return 3 records with ids 2, 4, 5");
        System.out.println(resultList);
        tx.commit();

        // Negated query
        tx = em.getTransaction();
        tx.begin();
        cb = em.getCriteriaBuilder();
        cq = cb.createQuery();
        cr = cq.from(TestEntity.class);
        cq.select(cr);
        cq.where(
                cb.or(
                    cb.ge(cr.get("id"), 4L),
                    cb.like(cr.get("field1"), "%2%")
                    ).not()
//                   ^^^^^^ Same disjunction restriction only negation added
                );
        // Should generate SQL statement:
//        select
//            testentity0_.id as id0_,
//            testentity0_.field1 as field2_0_,
//            testentity0_.field2 as field3_0_
//        from
//            TestEntity testentity0_
//        where
//            not (
//                testentity0_.id>=4
//                or testentity0_.field1 like '%2%'
//                )
        resultList = em.createQuery(cq).getResultList();
        System.out.println("\n\nQuery results with negated CompoundPredicate (disjunction)");
        System.out.println("Should return 2 records with ids 1, 3");
        System.out.println(resultList);
        tx.commit();

        em.close();
        emf.close();
    }

}
