package org.hibernate.ejb.test.query;

import org.hibernate.ejb.test.TestCase;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaBuilder.Case;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import junit.framework.Assert;

public class MSSQLPagingTest extends TestCase {
	@Entity
	public static class Aircraft {
		private Integer id;
		private String name;
		private Integer order;

		@Id @GeneratedValue
		public Integer getId() {
			return id;
		}
		public void setId(Integer id) {
			this.id = id;
		}

		public String getName() {
			return this.name;
		}
		public void setName(final String arg) {
			this.name = arg;
		}

		@Column(name="idx")
		public Integer getOrder() {
			return order;
		}
		public void setOrder(Integer arg) {
			this.order = arg;
		}
	}

	public void test_HHH_5915() {
		insert();

		query();
	}

	private void query() {
		EntityManager em = getOrCreateEntityManager();
		em.getTransaction().begin();

		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<Aircraft> cq = cb.createQuery(Aircraft.class);
		final Root<Aircraft> from = cq.from(Aircraft.class);

		/* commenting out this block for order by will cause the test to succeed */
		final Case<Object> orderSwitch = cb.selectCase();
		orderSwitch.when(cb.isNull(from.get("order")), Integer.valueOf(1));
		orderSwitch.otherwise(from.get("name"));
		cq.orderBy(cb.desc(orderSwitch));

		cq.where(cb.like(from.get("name").as(String.class), "bug%"));

		final TypedQuery<Aircraft> query = em.createQuery(cq);

		query.setFirstResult(0);
		query.setMaxResults(30);

		final List<Aircraft> resultList = query.getResultList();
		Assert.assertEquals("wrong # of results", 1, resultList.size());

		em.getTransaction().commit();
		em.close();

	}

	private void insert() {
		EntityManager em = getOrCreateEntityManager();
		em.getTransaction().begin();

		Aircraft a = new Aircraft();
		a.setName( "bug_test" );
		a.setOrder(1);

		em.persist( a );

		em.getTransaction().commit();
		em.close();
	}

	public Class[] getAnnotatedClasses() {
		return new Class[] {
			Aircraft.class
		};
	}
}