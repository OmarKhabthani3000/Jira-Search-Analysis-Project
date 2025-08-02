package foo;

import static org.junit.Assert.assertEquals;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.junit.Test;

public class CriteriaApiTest {

	@Test
	public void embeddableInPath() {
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

			em.persist(client);

			CriteriaBuilder cb = em.getCriteriaBuilder();

			CriteriaQuery<Client> cq = cb.createQuery(Client.class);

			Root<Client> root = cq.from(Client.class);

			cq.where(cb.equal(root.get("name").get("firstName"), "foo"));

			List<Client> list = em.createQuery(cq).getResultList();

			assertEquals(1, list.size());

		} finally {
			tx.rollback();
		}
		em.close();
		entityManagerFactory.close();
	}
}
