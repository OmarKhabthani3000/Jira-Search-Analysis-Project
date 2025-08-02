/*
 * Copyright 2014 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package korhner.hibernatebug;

import org.hibernate.Transaction;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Test;

public class ReattachLazyListTests extends BaseCoreFunctionalTestCase {

	@Override
	protected Class[] getAnnotatedClasses() {
		return new Class[] { Customer.class, Order.class };
	}

	private void prepareData() {
		openSession();
		Transaction tx = session.beginTransaction();

		Customer c = new Customer();

		Order o1 = new Order();
		o1.setIndex(0);
		o1.setId(1L);
		o1.setCustomer(c);

		c.setId(1L);
		c.getOrders().add(o1);
		session.save(c);
		session.save(o1);

		tx.commit();
		session.close();
	}

	private Customer getDetached() {
		openSession();
		Transaction tx = session.beginTransaction();

		Customer customer = session.get(Customer.class, 1L);
		customer.getOrders().toArray(); // load lazy collection

		tx.commit();
		session.close();
		return customer;
	}

	@Test
	public void lazyListDetachedIndexTest() throws Exception {
		prepareData();
		Customer customer = getDetached();
		reattach(customer);
		check();

	}

	/**
	 * @return
	 */
	public void check() {
		openSession();
		Transaction tx = session.beginTransaction();
		Order order = session.get(Order.class, 1L);
		tx.commit();
		session.close();
		org.junit.Assert.assertEquals(order.getIndex(), (Integer) 0);
	}

	/**
	 * @param customer
	 */
	public void reattach(Customer customer) {
		openSession();
		Transaction tx = session.beginTransaction();
		session.merge(customer);
		tx.commit();
		session.close();
	}
}
