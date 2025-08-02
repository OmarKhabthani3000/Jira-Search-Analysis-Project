package foo;

import static org.junit.Assert.assertEquals;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.junit.Test;

public class CriteriaApiTest {

	@Test
	public void noPredicatesInAnd() {
		EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("TestJPA");
		EntityManager em = entityManagerFactory.createEntityManager();

		EntityTransaction tx = em.getTransaction();

		tx.begin();
		try {
			Client client = new Client();
			client.id = 111;
			client.name = new Name();
			client.name.firstName = "foo";
			client.name.lastName = "bar";

			// No predicates
			em.persist(client);

			CriteriaBuilder cb = em.getCriteriaBuilder();

			CriteriaQuery<Client> cq = cb.createQuery(Client.class);

			Root<Client> root = cq.from(Client.class);

			cq.where(cb.and());

			assertEquals(1, em.createQuery(cq).getResultList().size());

			// Empty predicates
			cq = cb.createQuery(Client.class);

			root = cq.from(Client.class);

			cq.where(cb.and(new Predicate[0]));

			assertEquals(1, em.createQuery(cq).getResultList().size());


		} finally {
			tx.rollback();
		}
		em.close();
		entityManagerFactory.close();
	}
}
