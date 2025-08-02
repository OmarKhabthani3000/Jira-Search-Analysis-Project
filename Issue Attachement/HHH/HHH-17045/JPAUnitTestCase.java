package org.hibernate.bugs;

import jakarta.persistence.*;

import org.hibernate.annotations.RowId;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static jakarta.persistence.CascadeType.*;
import static jakarta.persistence.CascadeType.DETACH;

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
	public void errorDuringUpdatingSameEntityFieldInSameTransaction() throws Exception {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();

		var testEntity = new SimpleTestEntity(UUID.randomUUID(), "TestStatus");

		entityManager.persist(testEntity);
		entityManager.flush();

		testEntity.status = "NewStatus";

		entityManager.flush();

		entityManager.getTransaction().commit();
		entityManager.close();
	}

	@Test
	public void errorDuringUpdatingRelatedEntityFieldInDifferentTransactions() throws Exception {
		EntityManager entityManager = entityManagerFactory.createEntityManager();

		var testId = UUID.randomUUID();
		var testEntity = new RelatedMainEntity(testId, new RelatedSecondaryEntity(testId, "OldStatus"));

		//First transaction
		entityManager.getTransaction().begin();

		entityManager.persist(testEntity);
		entityManager.flush();

		entityManager.getTransaction().commit();


		//Second transaction
		entityManager.getTransaction().begin();

		var savedEntity = entityManager.find(RelatedMainEntity.class, testId);
		savedEntity.setStatus("NewStatus");

		entityManager.flush();

		entityManager.getTransaction().commit();
		entityManager.close();
	}
}

@Entity
@RowId
class SimpleTestEntity {

	@Id
	@Column(name = "id")
	public UUID id;

	@Column(name = "status")
	public String status;

	public SimpleTestEntity() {
	}

	public SimpleTestEntity(UUID id, String status) {
		this.id = id;
		this.status = status;
	}

}

@Entity
@RowId
class RelatedMainEntity {

	@Id
	@Column(name = "id")
	public UUID id;

	@OneToOne(cascade = {PERSIST, MERGE, REFRESH, DETACH})
	@MapsId
	public RelatedSecondaryEntity secondary;

	public RelatedMainEntity() {
	}

	public RelatedMainEntity(UUID id, RelatedSecondaryEntity secondary) {
		this.id = id;
		this.secondary = secondary;
	}

	public String getStatus() {
		return secondary.status;
	}

	public void setStatus(String newStatus) {
		secondary.status = newStatus;
	}

}

@Entity
@RowId
class RelatedSecondaryEntity {

	@Id
	@Column(name = "id")
	public UUID id;

	@Column(name = "status")
	public String status;

	public RelatedSecondaryEntity() {
	}

	public RelatedSecondaryEntity(UUID id, String status) {
		this.id = id;
		this.status = status;
	}

}

