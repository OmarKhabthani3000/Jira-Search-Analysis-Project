package org.hibernate.bugs;

import static org.hibernate.testing.transaction.TransactionUtil.doInJPA;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Persistence;
import jakarta.persistence.Table;
import org.hibernate.annotations.PartitionKey;
import org.hibernate.bugs.entities.SalesContact;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * This template demonstrates how to develop a test case for Hibernate ORM, using the Java Persistence API.
 */
public class PartitionKey1_1Test {

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
	public void HHH16849() throws Exception {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();
		// Do stuff...

		doInJPA( ()->entityManagerFactory, em -> {
			SalesContact salesContact = em.find(SalesContact.class, 3);
			System.out.println(salesContact.getAccountId());
			System.out.println(salesContact.getContactCustomFields().getId());
		});

		entityManager.getTransaction().commit();
		entityManager.close();
	}

	@Table(name = "contact_custom_fields")
	@Entity
	public static class ContactCustomField{
		@Id
		@Column(name = "id")
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		private Long id;

		public ContactCustomField(){

		}

		@OneToOne(fetch = FetchType.LAZY)
		@JoinColumn(name = "contact_id", referencedColumnName = "id", nullable = false)
		@JoinColumn(name = "account_id", referencedColumnName = "account_id", nullable = false)
		private SalesContact contact;

		public Long getId(){
			return id;
		}

	}


	@Table(name = "contacts")
	@Entity
	public static class SalesContact
		implements Serializable {

		public SalesContact(){}

		@Column(name = "first_name")
		private String firstName;

		@Column(name = "last_name")
		private String lastName;

		@PartitionKey
		@Column(name = "account_id", nullable = false)
		private Long accountId;

		@Id
		@Column(name = "id")
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		private Long id;

		@OneToOne(mappedBy = "contact", cascade = CascadeType.ALL)
		private ContactCustomField contactCustomFields = new ContactCustomField();

		public ContactCustomField getContactCustomFields(){
			return contactCustomFields;
		}

		public String getFirstName() {
			return firstName;
		}

		public void setFirstName(String firstName) {
			this.firstName = firstName;
		}

		public String getLastName() {
			return lastName;
		}

		public void setLastName(String lastName) {
			this.lastName = lastName;
		}

		public Long getAccountId() {
			return accountId;
		}

		public void setAccountId(Long accountId) {
			this.accountId = accountId;
		}

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

	}
}