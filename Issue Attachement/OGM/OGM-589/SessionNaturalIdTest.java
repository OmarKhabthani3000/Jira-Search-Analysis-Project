/*
 * Hibernate OGM, Domain model persistence for NoSQL datastores
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.ogm.backendtck.id.natural;

import static org.fest.assertions.Assertions.assertThat;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.hibernate.Transaction;
import org.hibernate.annotations.NaturalId;
import org.hibernate.ogm.OgmSession;
import org.hibernate.ogm.utils.OgmTestCase;
import org.junit.Test;

/**
 * @author Davide D'Alto
 */
public class SessionNaturalIdTest extends OgmTestCase {

	@Test
	public void getByNaturalId() {
		try ( OgmSession session = openSession() ) {
			Transaction tx = session.beginTransaction();
			session.persist( new Company( 5, "Red Hat" ) );
			tx.commit();
		}

		try ( OgmSession session = openSession() ) {
			Transaction tx = session.beginTransaction();
			Company company = session.byNaturalId( Company.class ).using( "name", "Red Hat" ).load();
			tx.commit();

			assertThat( company.getId() ).equals( 5 );
			assertThat( company.getName() ).equals( "Red Hat" );
		}
	}

	@Override
	protected Class<?>[] getAnnotatedClasses() {
		return new Class[]{ Company.class };
	}

	@Entity
	public static class Company {

		@Id
		private Integer id;

		@NaturalId
		private String name;

		public Company() {
		}

		public Company(Integer id, String name) {
			this.id = id;
			this.name = name;
		}

		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}

}
