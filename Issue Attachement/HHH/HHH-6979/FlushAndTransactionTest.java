//$Id: FlushAndTransactionTest.java 12915 2007-08-10 13:35:22Z epbernard $
package org.hibernate.ejb.test.transaction;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.RollbackException;
import javax.persistence.TransactionRequiredException;
import javax.persistence.PersistenceException;
import javax.persistence.OptimisticLockException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.criterion.Restrictions;
import org.hibernate.ejb.HibernateEntityManagerFactory;
import org.hibernate.ejb.test.TestCase;
import org.hibernate.stat.Statistics;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.junit.Test;

/**
 * @author Emmanuel Bernard
 */
public class FlushAndTransactionTest extends TestCase {
	public void testAlwaysTransactionalOperations() throws Exception {
		Book book = new Book();
		book.name = "Le petit prince";
		EntityManager em = factory.createEntityManager();
		em.getTransaction().begin();
		em.persist(book);
		em.getTransaction().commit();
		try {
			em.flush();
			fail("flush has to be inside a Tx");
		} catch (TransactionRequiredException e) {
			// success
		}
		try {
			em.lock(book, LockModeType.READ);
			fail("lock has to be inside a Tx");
		} catch (TransactionRequiredException e) {
			// success
		}
		em.getTransaction().begin();
		em.remove(em.find(Book.class, book.id));
		em.getTransaction().commit();
	}

	public void testSelect() throws Exception {
		EntityManager em = factory.createEntityManager();
		em.getTransaction().begin();
		CriteriaBuilder cb;
		CriteriaQuery<Book> query;

		Root<Book> root;

		cb = em.getCriteriaBuilder();
		query = cb.createQuery(Book.class);
		root = query.from(Book.class);
		// root.

	}

	// public void testTransactionalOperationsWhenTransactional() throws
	// Exception {
	// Book book = new Book();
	// book.name = "Le petit prince";
	// EntityManager em = factory.createEntityManager(
	// PersistenceContextType.TRANSACTION );
	// try {
	// em.persist( book );
	// fail("flush has to be inside a Tx");
	// }
	// catch (TransactionRequiredException e) {
	// //success
	// }
	// try {
	// em.refresh( book );
	// fail("refresh has to be inside a Tx");
	// }
	// catch (TransactionRequiredException e) {
	// //success
	// }
	// try {
	// em.remove( book );
	// fail("refresh has to be inside a Tx");
	// }
	// catch (TransactionRequiredException e) {
	// //success
	// }
	// em.close();
	// }

	public void testTransactionalOperationsWhenExtended() throws Exception {
		Book book = new Book();
		book.name = "Le petit prince";
		EntityManager em = factory.createEntityManager();
		Statistics stats = ((HibernateEntityManagerFactory) factory)
				.getSessionFactory().getStatistics();
		stats.clear();
		stats.setStatisticsEnabled(true);

		em.persist(book);
		assertEquals(0, stats.getEntityInsertCount());
		em.getTransaction().begin();
		em.flush();
		em.getTransaction().commit();
		assertEquals(1, stats.getEntityInsertCount());

		em.clear();
		book.name = "Le prince";
		book = em.merge(book);

		em.refresh(book);
		assertEquals(0, stats.getEntityUpdateCount());
		em.getTransaction().begin();
		em.flush();
		em.getTransaction().commit();
		assertEquals(0, stats.getEntityUpdateCount());

		book.name = "Le prince";
		em.getTransaction().begin();
		em.find(Book.class, book.id);
		em.getTransaction().commit();
		assertEquals(1, stats.getEntityUpdateCount());

		em.remove(book);
		assertEquals(0, stats.getEntityDeleteCount());
		em.getTransaction().begin();
		em.flush();
		em.getTransaction().commit();
		assertEquals(1, stats.getEntityDeleteCount());

		em.close();
		stats.setStatisticsEnabled(false);
	}

	public void testMergeWhenExtended() throws Exception {
		Book book = new Book();
		book.name = "Le petit prince";
		EntityManager em = factory.createEntityManager();
		Statistics stats = ((HibernateEntityManagerFactory) factory)
				.getSessionFactory().getStatistics();

		em.getTransaction().begin();
		em.persist(book);
		assertEquals(0, stats.getEntityInsertCount());
		em.getTransaction().commit();

		em.clear(); // persist and clear
		stats.clear();
		stats.setStatisticsEnabled(true);

		Book bookReloaded = em.find(Book.class, book.id);

		book.name = "Le prince";
		assertEquals("Merge should use the available entiies in the PC",
				em.merge(book), bookReloaded);
		assertEquals(book.name, bookReloaded.name);

		assertEquals(0, stats.getEntityDeleteCount());
		assertEquals(0, stats.getEntityInsertCount());
		assertEquals("Updates should have been queued", 0,
				stats.getEntityUpdateCount());

		em.getTransaction().begin();
		Book bookReReloaded = em.find(Book.class, bookReloaded.id);
		assertEquals("reload should return the object in PC", bookReReloaded,
				bookReloaded);
		assertEquals(bookReReloaded.name, bookReloaded.name);
		em.getTransaction().commit();

		assertEquals(0, stats.getEntityDeleteCount());
		assertEquals(0, stats.getEntityInsertCount());
		assertEquals("Work on Tx should flush", 1, stats.getEntityUpdateCount());

		em.getTransaction().begin();
		em.remove(bookReReloaded);
		em.getTransaction().commit();

		em.close();
		stats.setStatisticsEnabled(false);
	}

	public void testCloseAndTransaction() throws Exception {
		EntityManager em = factory.createEntityManager();
		em.getTransaction().begin();
		Book book = new Book();
		book.name = "Java for Dummies";
		em.persist(book);
		em.close();
		book.name = "C# for Dummies";
		assertFalse(em.isOpen());
		try {
			em.flush();
			fail("direct action on a closed em should fail");
		} catch (IllegalStateException e) {
			// success
		}
		em.getTransaction().commit();
		assertFalse(em.isOpen());
		em = factory.createEntityManager();
		em.getTransaction().begin();
		book = em.find(Book.class, book.id);
		assertEquals("C# for Dummies", book.name);
		em.remove(book);
		em.getTransaction().commit();
		em.close();
	}

	public void testTransactionCommitDoesNotFlush() throws Exception {
		EntityManager em = factory.createEntityManager();
		em.getTransaction().begin();
		Book book = new Book();
		book.name = "Java for Dummies";
		em.persist(book);
		em.getTransaction().commit();
		em.close();
		em = factory.createEntityManager();
		em.getTransaction().begin();
		List result = em
				.createQuery(
						"select book from Book book where book.name = :title")
				.setParameter("title", book.name).getResultList();
		assertEquals("EntityManager.commit() should trigger a flush()", 1,
				result.size());
		em.getTransaction().commit();
		em.close();
	}

	public void testTransactionAndContains() throws Exception {
		EntityManager em = factory.createEntityManager();
		em.getTransaction().begin();
		Book book = new Book();
		book.name = "Java for Dummies";
		em.persist(book);
		em.getTransaction().commit();
		em.close();
		em = factory.createEntityManager();
		em.getTransaction().begin();
		List result = em
				.createQuery(
						"select book from Book book where book.name = :title")
				.setParameter("title", book.name).getResultList();
		assertEquals("EntityManager.commit() should trigger a flush()", 1,
				result.size());
		assertTrue(em.contains(result.get(0)));
		em.getTransaction().commit();
		assertTrue(em.contains(result.get(0)));
		em.close();
	}

	public void testRollbackOnlyOnPersistenceException() throws Exception {
		Book book = new Book();
		book.name = "Stolen keys";
		book.id = null; // new Integer( 50 );
		EntityManager em = factory.createEntityManager();
		em.getTransaction().begin();
		try {
			em.persist(book);
			em.flush();
			em.clear();
			book.setName("kitty kid");
			em.merge(book);
			em.flush();
			em.clear();
			book.setName("kitty kid2"); // non updated version
			em.merge(book);
			em.flush();
			fail("optimistic locking exception");
		} catch (PersistenceException e) {
			// success
		}
		try {
			em.getTransaction().commit();
			fail("Commit should be rollbacked");
		} catch (RollbackException e) {
			// success
		} finally {
			em.close();
		}

	}

	public void testRollbackExceptionOnOptimisticLockException()
			throws Exception {
		Book book = new Book();
		book.name = "Stolen keys";
		book.id = null; // new Integer( 50 );
		EntityManager em = factory.createEntityManager();
		em.getTransaction().begin();
		em.persist(book);
		em.flush();
		em.clear();
		book.setName("kitty kid");
		em.merge(book);
		em.flush();
		em.clear();
		book.setName("kitty kid2"); // non updated version
		((Session) em.getDelegate()).update(book);
		try {
			em.getTransaction().commit();
			fail("Commit should be rollbacked");
		} catch (RollbackException e) {
			assertTrue(
					"During flush a StateStateException is wrapped into a OptimisticLockException",
					e.getCause() instanceof OptimisticLockException);
		} finally {
			em.close();
		}

	}

	public void testRollbackClearPC() throws Exception {
		Book book = new Book();
		book.name = "Stolen keys";
		EntityManager em = factory.createEntityManager();
		em.getTransaction().begin();
		em.persist(book);
		em.getTransaction().commit();
		em.getTransaction().begin();
		book.name = "Recovered keys";
		em.merge(book);
		em.getTransaction().rollback();
		assertEquals("Stolen keys", em.find(Book.class, book.id).name);
		em.close();
	}

	public Class[] getAnnotatedClasses() {
		return new Class[] { Book.class };
	}

	@Test
	public void testPoorSelect() throws Exception {
		EntityManager em = factory.createEntityManager();
		for (int i = 1; i <= 200; i++) {
			Book book = new Book();
			book.name = "Le petit prince"+i;
			em.getTransaction().begin();
			em.persist(book);
			em.getTransaction().commit();

			int cnt = 1500;
			List<String> nameList = new ArrayList<String>(cnt);
			nameList.add(book.name);
			for (int j = 1; j <= cnt; j++) {
				nameList.add(book.name + j);
			}

			long start = System.currentTimeMillis();
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Book> jpaQuery = cb.createQuery(Book.class);
			Root<Book> u = jpaQuery.from(Book.class);
			Predicate nameCondition = cb.equal(u.get("id"), book.id);
			Predicate inCondition = cb.in(u.get("name")).value(nameList);
			jpaQuery.where(cb.and(nameCondition, inCondition));
			TypedQuery<Book> query = em.createQuery(jpaQuery);

			System.out.println(query.getResultList());
			System.out.println("JPA: " + (System.currentTimeMillis() - start));

			start = System.currentTimeMillis();
			Criteria hibQuery;
			hibQuery = ((Session) em.getDelegate()).createCriteria(Book.class);
			hibQuery.add(Restrictions.in("name", nameList));
			hibQuery.add(Restrictions.eq("id", book.id));
			System.out.println(hibQuery.list());
			System.out.println("Hib: " + (System.currentTimeMillis() - start));
		}
	}

	@Test
	public void testNoCache() throws Exception {
		List<Book> bookList = new ArrayList<Book>();
		EntityManager em = factory.createEntityManager();
		em.getTransaction().begin();

		for (int i = 0; i < 5; i++) {
			Book book = new Book();
			book.name = "Le petit prince" + i;
			bookList.add(book);
			em.persist(book);
		}
		em.getTransaction().commit();

		for (Book book : bookList) {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Book> jpaQuery = cb.createQuery(Book.class);
			Root<Book> u = jpaQuery.from(Book.class);
			Predicate nameCondition = cb.equal(u.get("id"), book.id);
			jpaQuery.where(nameCondition);
			TypedQuery<Book> query = em.createQuery(jpaQuery);

			System.out.println(query.getResultList());
		}
	}
}
