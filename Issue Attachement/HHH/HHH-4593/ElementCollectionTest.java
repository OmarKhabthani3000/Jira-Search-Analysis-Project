package foo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.junit.Test;

public class ElementCollectionTest {

	@Test
	public void foreignKey() {
		EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("TestJPA");
		EntityManager entityManager = entityManagerFactory.createEntityManager();

		EntityTransaction tx = entityManager.getTransaction();

		tx.begin();

		Set<String> barSet = new HashSet<String>(Arrays.asList("bar1", "bar2"));

		Foo foo = new Foo();
		foo.bar = barSet;

		entityManager.persist(foo);
		int id = foo.id;

		tx.commit();
		entityManager.close();


		entityManager = entityManagerFactory.createEntityManager();

		tx = entityManager.getTransaction();
		tx.begin();

		foo = entityManager.find(Foo.class, id);

		assertNotNull(foo);
		assertEquals(barSet, foo.bar);

		tx.rollback();
		entityManager.close();
	}
}
