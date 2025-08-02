package org.hibernate.bugs;

import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * This template demonstrates how to develop a test case for Hibernate ORM,
 * using the Java Persistence API.
 */
public class JPAUnitTestCase {

	private EntityManagerFactory entityManagerFactory;

	@Before
	public void init() {
		entityManagerFactory = Persistence.createEntityManagerFactory("templatePU");
	}

	@After
	public void destroy() {
		entityManagerFactory.close();
	}

	@Test
	public void testDisjunction() throws Exception {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();

		createEntities(entityManager);

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();

		CriteriaQuery<BaseType> criteria = cb.createQuery(BaseType.class);
		Root<BaseType> root = criteria.from(BaseType.class);
		Root<SubType1> sub1Root = cb.treat(root, SubType1.class);
		Predicate sub1pred = cb.and(cb.equal(sub1Root.<Integer>get("basevalue"), cb.literal(100)),
				cb.isNull(sub1Root.<Integer>get("subvalue1")));
		Root<SubType2> sub2Root = cb.treat(root, SubType2.class);
		Predicate sub2pred = cb.and(cb.equal(sub2Root.<Integer>get("basevalue"), cb.literal(200)),
				cb.isNull(sub2Root.<Integer>get("subvalue2")));
		criteria.where(cb.or(sub1pred, sub2pred));
		List<BaseType> resultList = entityManager.createQuery(criteria.select(root)).getResultList();

		entityManager.getTransaction().commit();
		entityManager.close();

		Assert.assertEquals(Collections.emptyList(), resultList);
	}

	@Test
	public void testConjunction() throws Exception {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();

		createEntities(entityManager);

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();

		CriteriaQuery<BaseType> criteria = cb.createQuery(BaseType.class);
		Root<BaseType> root = criteria.from(BaseType.class);
		Root<SubType1> sub1Root = cb.treat(root, SubType1.class);
		Predicate sub1pred = cb.and(cb.equal(sub1Root.<Integer>get("basevalue"), cb.literal(100)),
				cb.isNull(sub1Root.<Integer>get("subvalue1")));
		Root<SubType2> sub2Root = cb.treat(root, SubType2.class);
		Predicate sub2pred = cb.and(cb.equal(sub2Root.<Integer>get("basevalue"), cb.literal(100)),
				cb.isNull(sub2Root.<Integer>get("subvalue2")));
		criteria.where(cb.and(sub1pred, sub2pred));
		List<BaseType> resultList = entityManager.createQuery(criteria.select(root)).getResultList();

		entityManager.getTransaction().commit();
		entityManager.close();

		Assert.assertEquals(Collections.emptyList(), resultList);
	}

	@Test
	public void testTreatInNot() throws Exception {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();

		entityManager.persist(new SubType1(100, 100));
		entityManager.persist(new SubType1(200, 200));
		entityManager.persist(new SubType2(100, 100));
		entityManager.persist(new SubType2(200, 200));

		List<BaseType> resultList = entityManager.createQuery(
				"select e from BaseType as e where not(treat(e as SubType1).subvalue1 = 100)",
				BaseType.class).getResultList();

		entityManager.getTransaction().commit();
		entityManager.close();

		Assert.assertEquals(3, resultList.size());
	}

	@Test
	public void testMixedWhere() throws Exception {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();

		entityManager.persist(new SubType1(100, 100));
		entityManager.persist(new SubType1(200, 200));
		entityManager.persist(new SubType2(100, 100));
		entityManager.persist(new SubType2(200, 200));

		List<BaseType> resultList = entityManager.createQuery(
				"select e from BaseType as e where e.basevalue = 100 or treat(e as SubType1).subvalue1 = 200",
				BaseType.class).getResultList();

		entityManager.getTransaction().commit();
		entityManager.close();

		Assert.assertEquals(3, resultList.size());
	}

	@Test
	public void testTreatWithSupertypeProperty() throws Exception {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();

		entityManager.persist(new SubType1(100, 100));
		entityManager.persist(new SubType1(200, 200));
		entityManager.persist(new SubType2(100, 100));
		entityManager.persist(new SubType2(200, 200));

		List<BaseType> resultList = entityManager.createQuery(
				"select e from BaseType as e where treat(e as SubType1).basevalue = 100 or treat(e as SubType2).subvalue2 = 200",
				BaseType.class).getResultList();

		entityManager.getTransaction().commit();
		entityManager.close();

		Assert.assertEquals(2, resultList.size());
	}

	protected void createEntities(EntityManager entityManager) {
		entityManager.persist(new SubType1(200, null));
		entityManager.persist(new SubType2(100, null));
	}
}
