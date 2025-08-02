/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Inc.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.hibernate.jpa.test.cascade;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.LockMode;
import org.hibernate.Session;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * @author Steve Ebersole
 */
public class MergeWithTransientNonCascadedAssociationTest extends BaseEntityManagerFunctionalTestCase {
	@Override
	public Class[] getAnnotatedClasses() {
		return new Class[] { Person.class, Address.class };
	}


	@Test
	public void testMergeWithPreviouslyPersistedNonCascadedAssociation() {
		EntityManager em = getOrCreateEntityManager();
		em.getTransaction().begin();
		Person persistedPerson = new Person();

		Address persistedAddress = new Address();
		persistedAddress.street = "Grove Street";
		em.persist( persistedAddress );

		Address persistedMultipleAddressesAddress = new Address();
		persistedMultipleAddressesAddress.street = "Sesame Street";
		em.persist( persistedMultipleAddressesAddress );

		persistedPerson.address = persistedAddress;
		persistedPerson.multipleAddresses = Arrays.asList( persistedMultipleAddressesAddress );

		em.persist( persistedPerson );

		em.getTransaction().commit();
		em.close();

		// assemble a new Person and Addresses, but use IDs of the already persisted ones
		Person newPerson = new Person();
		newPerson.id=  persistedPerson.id;

		newPerson.address = new Address();
		newPerson.address.id = persistedAddress.id;
		// change street on address
		newPerson.address.street = "Grove Street (updated)";

		Address newMultipleAddressesAddress = new Address();
		newMultipleAddressesAddress.id = persistedAddress.id;
		newMultipleAddressesAddress.street = "Sesame Street (updated)";
		newPerson.multipleAddresses = Arrays.asList( newMultipleAddressesAddress );

		em = getOrCreateEntityManager();
		em.getTransaction().begin();
		Person managedPerson = em.merge( persistedPerson );

		// with Hibernate 3.6.5, the following was true: person.address == managedPerson.address
		assertNotNull( managedPerson.address );
		assertEquals( newPerson.address.street, managedPerson.address.street );

		// with Hibernate 3.6.5, the following was true: newPerson.multipleAddresses == managedPerson.multipleAddresses
		assertEquals( newPerson.multipleAddresses.size(), managedPerson.multipleAddresses.size() );

		for ( int i = 0; i < persistedPerson.multipleAddresses.size(); i++ ) {
			assertEquals( persistedPerson.multipleAddresses.get(i).street, newPerson.multipleAddresses.get(i).street );
		}

		try {
			em.flush();
			fail( "Expecting IllegalStateException" );
		}
		catch (IllegalStateException ise) {
			// expected...
			em.getTransaction().rollback();
		}
		em.close();

		em = getOrCreateEntityManager();
		em.getTransaction().begin();
		persistedPerson.address = null;
		persistedPerson.multipleAddresses.clear();
		delete(em, persistedPerson);
		delete(em, persistedAddress);
		delete(em, persistedMultipleAddressesAddress);
		em.getTransaction().commit();
		em.close();
	}


	private void delete(EntityManager em, Object persisted) {
		em.unwrap( Session.class ).lock( persisted, LockMode.NONE );
		em.unwrap( Session.class ).delete( persisted );
	}

	@Test
	public void testMergeWithTransientNonCascadedAssociation() {
		EntityManager em = getOrCreateEntityManager();
		em.getTransaction().begin();
		Person person = new Person();
		em.persist( person );
		em.getTransaction().commit();
		em.close();

		person.address = new Address();
		person.address.street = "Sesame Street";
		person.multipleAddresses = createAddresses( "Grove Street", "Jump Street" );

		em = getOrCreateEntityManager();
		em.getTransaction().begin();
		Person managedPerson = em.merge( person );

		// with Hibernate 3.6.5, the following was true: person.address == managedPerson.address
		assertNotNull( managedPerson.address );
		assertEquals( person.address.street, managedPerson.address.street );

		// with Hibernate 3.6.5, the following was true: person.multipleAddresses == managedPerson.multipleAddresses
		assertEquals( person.multipleAddresses.size(), managedPerson.multipleAddresses.size() );

		for ( int i = 0; i < person.multipleAddresses.size(); i++ ) {
			assertEquals( person.multipleAddresses.get(i).street, managedPerson.multipleAddresses.get(i).street );
		}

		try {
			em.flush();
			fail( "Expecting IllegalStateException" );
		}
		catch (IllegalStateException ise) {
			// expected...
			em.getTransaction().rollback();
		}
		em.close();

		em = getOrCreateEntityManager();
		em.getTransaction().begin();
		person.address = null;
		delete(em, person);
		em.getTransaction().commit();
		em.close();
	}

	private List<Address> createAddresses(String ... streets) {
		List<Address> result = new ArrayList<Address>();

		for(String street : streets) {
			Address a = new Address();
			a.street = street;
			result.add(a);
		}

		return result;
	}

	@Entity( name = "Person" )
	public static class Person {
		@Id
		@GeneratedValue( generator = "increment" )
		@GenericGenerator( name = "increment", strategy = "increment" )
		private Integer id;
		@ManyToOne
		private Address address;
		@OneToMany
		private List<Address> multipleAddresses;

		public Person() {
		}
	}

	@Entity( name = "Address" )
	public static class Address {
		@Id
		@GeneratedValue( generator = "increment" )
		@GenericGenerator( name = "increment", strategy = "increment" )
		private Integer id;

		private String street;
	}
}
