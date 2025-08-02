package org.hibernate.bugs;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.h2.tools.Server;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * This template demonstrates how to develop a test case for Hibernate ORM, using the Java Persistence API.
 */
public class JPAUnitTestCase {

	private EntityManagerFactory entityManagerFactory;

	@Before
	public void init() throws Exception {
		Map<String, String> properties = new HashMap<>();
		properties.put("hibernate.format_sql", "false");
		properties.put("hibernate.generate_statistics", "true");

		properties.put("hibernate.order_inserts", "true");
		properties.put("hibernate.order_updates", "true");
		properties.put("hibernate.jdbc.batch_size", "10000");
		properties.put("hibernate.jdbc.batch_versioned_data", "true");

		// if  property hibernate.temp.use_jdbc_metadata_defaults is set to false then no batch inserts are executed
		// I saw it when debugging this test case with a breakpoint at AbstractEntityPersister#insert (line 3352)
		// where "if (useBatch)" evaluated to false ...
		// but should also be visible in log output if we could get log4j outputs for statistics working ...
    //
		// if property hibernate.temp.use_jdbc_metadata_defaults is omitted or set to true then batch inserts are executed
		properties.put("hibernate.temp.use_jdbc_metadata_defaults", "false");

		entityManagerFactory = Persistence.createEntityManagerFactory( "templatePU",  properties);
	}

	@After
	public void destroy() {
		entityManagerFactory.close();
	}

	// Entities are auto-discovered, so just add them anywhere on class-path
	// Add your tests, using standard JUnit.
	@Test
	public void hhh123Test() throws Exception {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();

		for (int i = 0; i < 10; i++) {
			MyEntity entity = new MyEntity();
			entity.setContent(Integer.toString(i));

			entityManager.persist(entity);
		}

		entityManager.getTransaction().commit();
		entityManager.close();
	}
}
