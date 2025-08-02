package com.mycompany.tools.domain;

import static org.junit.Assert.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class JpaAttributeConverterExampleTest {
	private static final String PERSISTENCE_UNIT_NAME = "todos";
	private static EntityManagerFactory factory;
	private static EntityManager em;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
		em = factory.createEntityManager();

	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		em.close();
		factory.close();
	}

	@Test
	public void testNullAttributeConvertion() {
		em.getTransaction().begin();
		JpaAttributeConverterExample nullAttributeObj = new JpaAttributeConverterExample();
		nullAttributeObj.setSummary("This is null test");
		nullAttributeObj.setDescription("Null boolean value should be converted to F");
		em.persist(nullAttributeObj);
		em.getTransaction().commit();
		Long nullValueId = nullAttributeObj.getId();
		JpaAttributeConverterExample example = em.find(JpaAttributeConverterExample.class, nullValueId);
		assertNotNull("Attribute convertion failed. "
							+ "Boolean value is null.",example.getIsActive());

	}

	@Test
	public void testNotNullAttributeConvertion() {
		em.getTransaction().begin();
		JpaAttributeConverterExample trueAttributeObj = new JpaAttributeConverterExample();
		trueAttributeObj.setSummary("This is not null test");
		trueAttributeObj.setDescription("True boolean value should be converted to T");
		trueAttributeObj.setIsActive(true);
		em.persist(trueAttributeObj);
		em.getTransaction().commit();
		Long nullValueId = trueAttributeObj.getId();
		JpaAttributeConverterExample example = em.find(JpaAttributeConverterExample.class, nullValueId);
		assertTrue("Boolean convertion failed", example.getIsActive());
	}

}
