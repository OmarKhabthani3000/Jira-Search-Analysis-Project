package org.hibernate.bugs;

import javax.persistence.*;

import com.mongodb.MongoClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import dk.slashwin.objects.TestObj;

/**
 * This template demonstrates how to develop a test case for Hibernate ORM, using the Java Persistence API.
 */
public class JPAUnitTestCase {

	private EntityManagerFactory entityManagerFactory;

	@Before
	public void init() {
		entityManagerFactory = Persistence.createEntityManagerFactory( "templatePU" );
	}

	@After
	public void destroy() {
		entityManagerFactory.close();
	}

	// Entities are auto-discovered, so just add them anywhere on class-path
	// Add your tests, using standard JUnit.
	@Test
	public void noWorkie() throws Exception {
		//Clear the database
		MongoClient cli = new MongoClient("127.0.0.1", 27017);
		cli.getDatabase("test").drop();

		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();

		//Create object
		TestObj obj = new TestObj("Hello world");

		//Persist it
		entityManager.persist(obj);

        //Mutate it
        obj.setName("Jeff");

		//It all goes wrong
		entityManager.flush();

		//Commit
		entityManager.getTransaction().commit();
		entityManager.close();
	}

	@Test
	public void workie() throws Exception {
		//Clear the database
		MongoClient cli = new MongoClient("127.0.0.1", 27017);
		cli.getDatabase("test").drop();

		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();

		//Create object
		TestObj obj = new TestObj("Hello world");

		//Persist it
		entityManager.persist(obj);

		//Commit before the mutation
		entityManager.getTransaction().commit();
		entityManager.close();
		entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();

		//Get the object again
		obj = entityManager.merge(obj);

		//Mutate it
		obj.setName("Jeff");

        //It all goes pretty well
		entityManager.flush();

		//Commit
		entityManager.getTransaction().commit();
		entityManager.close();
	}
}
