package cascade.test;

import java.util.Collections;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import junit.framework.TestCase;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.cfg.AnnotationConfiguration;

/**
 * Test case to illustrate that when a delete-orphan cascade is used on a
 * one-to-many collection and the many-to-one side is also cascaded a
 * TransientObjectException is thrown.
 */
public class TestBidirectionalCascade extends TestCase {
	private SessionFactory sf;
	private Session session;
	private Transaction tx;

	private Parent parent;
	private DeleteOrphanChild deleteOrphanChild;
	private Child child;

	public TestBidirectionalCascade() {
		super();
	}

	public TestBidirectionalCascade(String testName) {
		super(testName);
	}

	/**
	 * Create the Hibernate session factory and the three objects used by the
	 * tests.
	 * 
	 * The parent reference of child and deleteOrphanChild is set since only one
	 * of these will ever be used.
	 * 
	 * The sets of Parent are not set as this would create additional cascades.
	 */
	@Override
	protected void setUp() {
		sf = createSessionFactory();
		session = sf.openSession();
		tx = session.beginTransaction();

		parent = new Parent();

		deleteOrphanChild = new DeleteOrphanChild();
		deleteOrphanChild.parent = parent;

		child = new Child();
		child.parent = parent;

	}

	@Override
	protected void tearDown() {
		session.close();
		sf.close();
	}

	/**
	 * Test that we can save the parent object cascading to a child via a delete
	 * orphan collection.
	 */
	public void testSaveParentDeleteOrphan() {
		parent.deleteOrphanChildren = Collections.singleton(deleteOrphanChild);

		session.save(parent);
		tx.commit();
	}

	/**
	 * Test that we can save the child object cascading to the parent on a
	 * collection that is delete orphan. This test fails with a
	 * TransientObjectException
	 */
	public void testSaveChildDeleteOrphan() {
		parent.deleteOrphanChildren = Collections.singleton(deleteOrphanChild);

		session.save(deleteOrphanChild);
		tx.commit();
	}

	/**
	 * Test that we can save the parent object cascading to a child via a
	 * collection that isn't marked delete orphan.
	 */
	public void testSaveParent() {
		parent.children = Collections.singleton(child);

		session.save(parent);
		tx.commit();
	}

	/**
	 * Test that we can save the child object cascading to the parent on a
	 * collection that is not marked delete orphan.
	 */
	public void testSaveChild() {
		parent.children = Collections.singleton(child);

		session.save(deleteOrphanChild);
		tx.commit();
	}

	private SessionFactory createSessionFactory() {
		final AnnotationConfiguration cfg = new AnnotationConfiguration();
		cfg.setProperty("hibernate.connection.driver_class", "org.hsqldb.jdbcDriver");
		cfg.setProperty("hibernate.connection.url", "jdbc:hsqldb:mem:cascadeTest");
		cfg.setProperty("hibernate.connection.username", "sa");
		cfg.setProperty("hibernate.connection.password", "");
		cfg.setProperty("hibernate.connection.pool_size", "20");
		cfg.setProperty("hibernate.hbm2ddl.auto", "create");
		cfg.setProperty("hibernate.cache.provider_class", "org.hibernate.cache.EhCacheProvider");
		cfg.setProperty("hibernate.show_sql", "true");
		cfg.addAnnotatedClass(Parent.class);
		cfg.addAnnotatedClass(DeleteOrphanChild.class);
		cfg.addAnnotatedClass(Child.class);
		return cfg.buildSessionFactory();
	}

	@Entity
	public static class Parent {
		@Id
		@GeneratedValue(strategy = GenerationType.AUTO)
		Long id;

		@OneToMany(mappedBy = "parent")
		@Cascade(value = {CascadeType.ALL, CascadeType.DELETE_ORPHAN})
		Set<DeleteOrphanChild> deleteOrphanChildren;

		@OneToMany(mappedBy = "parent")
		@Cascade(value = {CascadeType.ALL})
		Set<Child> children;
	}

	@Entity
	public static class DeleteOrphanChild {
		@Id
		@GeneratedValue(strategy = GenerationType.AUTO)
		Long id;

		@ManyToOne
		@Cascade(value = CascadeType.ALL)
		Parent parent;
	}

	@Entity
	public static class Child {
		@Id
		@GeneratedValue(strategy = GenerationType.AUTO)
		Long id;

		@ManyToOne
		@Cascade(value = CascadeType.ALL)
		Parent parent;

	}
}
