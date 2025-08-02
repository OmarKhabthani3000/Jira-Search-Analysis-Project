package org.hibernate.bugs.test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.junit.Test;

public class TestHibernate
{
	@Test
	public void testOracle()
	{
		//EntityManagerFactory emf = Persistence.createEntityManagerFactory("h2bugs");
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("orabugs");
		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		
		em.createQuery( "SELECT e1 FROM Entity1 e1 WHERE e1 IN ("
				+ "SELECT e1 FROM Entity1 e1 WHERE "
				+ "e1.data='qqq')" ).getResultList();
		
		tx.commit();
		em.close();
		emf.close();
	}
}
