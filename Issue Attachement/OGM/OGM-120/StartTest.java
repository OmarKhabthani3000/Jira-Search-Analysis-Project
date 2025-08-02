package org.hibernate.ogm.test.utils;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.Test;

public class StartTest {
	@Test
	public void launchTest() {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("mongoPU");
		EntityManager em = emf.createEntityManager();
		assertNotNull(em);
	}
}
