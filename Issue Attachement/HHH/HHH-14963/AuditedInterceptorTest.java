import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.EmptyInterceptor;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.envers.Audited;
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.type.Type;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AuditedInterceptorTest {
	private SessionFactory sessionFactory;

	@Before
	public void setUp() throws Exception {
		StandardServiceRegistry registry = new StandardServiceRegistryBuilder().build();

		Metadata metadata = new MetadataSources(registry).addAnnotatedClass(A.class).buildMetadata();

		sessionFactory = metadata.buildSessionFactory();
	}

	@After
	public void tearDown() throws Exception {
		sessionFactory.close();
	}

	@Test
	public void test() throws Exception {
		for (FlushMode flushMode : FlushMode.values()) {
			TestInterceptor interceptor = new TestInterceptor();

			Session session = sessionFactory.withOptions().interceptor(interceptor).flushMode(flushMode).openSession();

			try {
				session.getTransaction().begin();

				Object a = session.merge(new A());

				if (FlushMode.MANUAL.equals(session.getHibernateFlushMode()))
					session.flush();

				session.getTransaction().commit();

				assertEquals(3, interceptor.saved.size());
				assertEquals(a, interceptor.saved.get(0));
				assertTrue(interceptor.saved.get(1) instanceof DefaultRevisionEntity);
				assertTrue(interceptor.saved.get(2) instanceof HashMap);
			} finally {
				session.close();
			}
		}
	}

	public static class TestInterceptor extends EmptyInterceptor {
		private List<Object> saved = new ArrayList<>();

		@Override
		public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
			saved.add(entity);

			return false;
		}
	}

	@Entity
	@Audited
	public static class A {
		@Id
		@GeneratedValue
		private long id;

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
}
