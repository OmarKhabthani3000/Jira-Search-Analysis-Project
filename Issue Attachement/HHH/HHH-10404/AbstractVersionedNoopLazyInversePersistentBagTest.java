package org.hibernate.test.collection.bag.version;

import org.hibernate.Session;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author NickK
 */
public abstract class AbstractVersionedNoopLazyInversePersistentBagTest extends BaseCoreFunctionalTestCase {

	protected void createParentAndChild() {
		BagOwner parent = new BagOwner("root");
		BagOwner child = new BagOwner("c1");
		child.setParent(parent);
		BagOwner otherChild = new BagOwner("c2");

		Session session = openSession();
		session.beginTransaction();
		session.save(parent);
		session.save(child);
		session.getTransaction().commit();
		session.close();
	}


	@Test
	public void testLoadAndFlushUpdatesParentRv() {
		// create entities
		createParentAndChild();

		Session session = openSession();
		session.beginTransaction();

		// load entities and get parent rv
		BagOwner child = getBagOwnerById(session, 2);
		BagOwner parent = getBagOwnerById(session, child.getParent().getId());
		Integer originalParentRv = parent.getRv();

		// session is dirty with no changes, then flush to update the parent rv.
		Assert.assertTrue(session.isDirty());
		session.flush();
		parent = getBagOwnerById(session, parent.getId());
		Integer updateParentRv = parent.getRv();
		Assert.assertEquals(originalParentRv, updateParentRv);

		session.getTransaction().commit();
		session.close();
	}


	protected BagOwner getBagOwnerById(Session session, Integer id) {
		return unwrapEntityIfNecessary(session.load(BagOwner.class.getName(), id));
	}


	private BagOwner unwrapEntityIfNecessary(Object entity) {
		if (entity instanceof HibernateProxy) {
			return (BagOwner) ((HibernateProxy) entity).getHibernateLazyInitializer().getImplementation();
		}
		return (BagOwner) entity;
	}
}
