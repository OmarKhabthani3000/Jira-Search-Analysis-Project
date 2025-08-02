/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.orm.test.jpa.inheritance;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;
import org.hibernate.testing.orm.junit.EntityManagerFactoryScope;
import org.hibernate.testing.orm.junit.Jpa;
import org.junit.jupiter.api.Test;

/**
 * @author
 */
@Jpa(annotatedClasses = {
		PrimaryKeyJoinColumnTest.VersionedFruit.class,
		PrimaryKeyJoinColumnTest.Raspberry.class,
		PrimaryKeyJoinColumnTest.Drupelet.class
})
public class PrimaryKeyJoinColumnTest {

	@Test
	//@FailureExpected
	public void testImplicitForcedVersionIncrementWithPrimaryKeyJoinColumn(EntityManagerFactoryScope scope) {
		scope.inEntityManager(
				entityManager -> {
					Raspberry raspberry = new Raspberry();
					Drupelet drupelet = new Drupelet();
					try {
						entityManager.getTransaction().begin();
						entityManager.persist(drupelet);
						raspberry.setDrupelets(new HashSet<>());
						raspberry.getDrupelets().add(drupelet);
						entityManager.persist(raspberry);
						entityManager.flush();

						raspberry.getDrupelets().clear();
						entityManager.flush();

						entityManager.getTransaction().commit();
					}
					catch (Exception e) {
						if ( entityManager.getTransaction().isActive() ) {
							entityManager.getTransaction().rollback();
						}
						throw e;
					}
				}
		);
	}

	@Entity
	@Inheritance(strategy = InheritanceType.JOINED)
	public abstract class VersionedFruit {
		Long id;
		Long version = 0L;

		@Id
		@GeneratedValue
		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		@Version
		public Long getVersion() {
			return version;
		}

		public void setVersion(Long version) {
			this.version = version;
		}
	}

	@Entity
	@PrimaryKeyJoinColumn(name = "RASPBERRY_ID")
	public class Raspberry extends VersionedFruit {
		private Set<Drupelet> drupelets;

		@OneToMany(fetch = FetchType.LAZY)
		@JoinTable(
				name = "DRUPELETS",
				joinColumns = {@JoinColumn(name = "RASPBERRY_ID")},
				inverseJoinColumns = {@JoinColumn(name = "DRUPELET_ID")}
		)
		public Set<Drupelet> getDrupelets() {
			return drupelets;
		}

		public void setDrupelets(Set<Drupelet> drupelets) {
			this.drupelets = drupelets;
		}
	}

	@Entity
	public class Drupelet {
		Long id;

		@Id
		@GeneratedValue
		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		};
	}
}
