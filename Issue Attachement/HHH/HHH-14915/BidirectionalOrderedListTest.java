import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class BidirectionalOrderedListTest {
	private EntityManagerFactory entityManagerFactory;

	@Before
	public void setUp() throws Exception {
		entityManagerFactory = Persistence.createEntityManagerFactory("Test");
	}

	@After
	public void tearDown() throws Exception {
		entityManagerFactory.close();
	}

	@Test
	public void test() throws Exception {
		A a = new A();
		a.list.add(new B(a));

		EntityManager entityManager = entityManagerFactory.createEntityManager();

		try {
			entityManager.getTransaction().begin();
			a = entityManager.merge(a);
			entityManager.getTransaction().commit();

			assertEquals(a, entityManager.find(A.class, a.id));
		} finally {
			entityManager.close();
		}
	}

	@Entity
	public static class A {
		@Id
		@GeneratedValue
		private long id;

		@OneToMany(cascade = CascadeType.ALL, mappedBy = "a", orphanRemoval = true)
		@OrderColumn(nullable = false)
		private List<B> list = new ArrayList<>();

		@Override
		public int hashCode() {
			return Objects.hash(id);
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof A)
				return id == ((A) obj).id;

			return false;
		}
	}

	@Entity
	public static class B {
		@Id
		@GeneratedValue
		private long id;

		@ManyToOne(optional = false)
		private A a;

		private B(A a) {
			this.a = a;
		}

		private B() {
		}

		@Override
		public int hashCode() {
			return Objects.hash(id);
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof B)
				return id == ((B) obj).id;

			return false;
		}
	}
}
