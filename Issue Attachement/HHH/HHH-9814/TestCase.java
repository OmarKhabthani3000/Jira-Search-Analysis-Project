package test.hh9814;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.junit.Before;
import org.junit.Test;

public class TestCase {

    private static EntityManagerFactory entityManagerFactory;
    
    private EntityManager em;
    
	@Before
	public void setUp() throws Exception {
		em = Persistence.createEntityManagerFactory("test").createEntityManager();
		Session session = em.unwrap(Session.class);
		Transaction trx = session.beginTransaction();
        TestEntity t1 = new TestEntity();
        t1.setUid(new UID("A", "B"));
        em.persist(t1);
        em.flush();
        em.clear();
        trx.commit();
	}
	
	@Test
	public void testJPQL() throws Exception {
		TypedQuery<Long> query = em.createQuery("select count(distinct t) from TestEntity t", Long.class);
		List<Long> list = query.getResultList();
		assertEquals(1, list.size());
		assertEquals(1, list.get(0).intValue());
	}
	
	@Test
	public void testCriteria() throws Exception {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<TestEntity> root = cq.from(TestEntity.class);
		cq.select(cb.countDistinct(root));
		TypedQuery<Long> query = em.createQuery(cq);
		List<Long> list = query.getResultList();
		assertEquals(1, list.size());
		assertEquals(1, list.get(0).intValue());
	}
	
}
