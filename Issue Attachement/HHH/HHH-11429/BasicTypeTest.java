/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.envers.test.integration.components;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;
import org.hibernate.envers.test.BaseEnversJPAFunctionalTestCase;
import org.hibernate.envers.test.Priority;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Chris Cranford
 */
public class BasicTypeTest extends BaseEnversJPAFunctionalTestCase {
	@Override
	protected Class<?>[] getAnnotatedClasses() {
		return new Class<?>[] { CitaAgenda.class, Reconocimiento.class };
	}

	@Test
	@Priority(10)
	public void initData() {
		EntityManager entityManager = getEntityManager();
		try {
			// Revision 1
			entityManager.getTransaction().begin();
			final Reconocimiento reconocimiento = new Reconocimiento( 1, new Enfermeria( true ) );
			entityManager.persist( reconocimiento );
			CitaAgenda citaAgenda = new CitaAgenda( 1, reconocimiento );
			entityManager.persist( citaAgenda );
			entityManager.getTransaction().commit();
		}
		catch ( Throwable t ) {
			if ( entityManager.getTransaction().isActive() ) {
				entityManager.getTransaction().rollback();
			}
			throw t;
		}
		finally {
			entityManager.close();
		}
	}

	@Test
	public void testBasicTypes() {
		final AuditQuery query = getAuditReader() .createQuery()
				.forRevisionsOfEntity( CitaAgenda.class, false, false )
				.add( AuditEntity.id().eq( 1 ) );

		for ( Object object : query.getResultList() ) {
			Object[] array = (Object[]) object;
			for ( int i = 0; i < array.length; ++i ) {
				CitaAgenda citaAgenda = (CitaAgenda) array[0];
				assertNotNull( citaAgenda.getReconocimiento() );
				assertNotNull( citaAgenda.getReconocimiento().getEnfermeria() );
				assertTrue( citaAgenda.getReconocimiento().getEnfermeria().isDiabetes() );
			}
		}
	}

	@Entity(name = "CitaAgenda")
	@Audited
	public static class CitaAgenda {
		@Id
		private Integer id;

		@ManyToOne(fetch = FetchType.LAZY)
		@JoinColumn(name = "reconocimiento_id")
		@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
		private Reconocimiento reconocimiento;

		CitaAgenda() {

		}

		CitaAgenda(Integer id, Reconocimiento reconocimiento) {
			this.id = id;
			this.reconocimiento = reconocimiento;
		}

		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}

		public Reconocimiento getReconocimiento() {
			return reconocimiento;
		}

		public void setReconocimiento(Reconocimiento reconocimiento) {
			this.reconocimiento = reconocimiento;
		}

		@Override
		public boolean equals(Object o) {
			if ( this == o ) {
				return true;
			}
			if ( o == null || getClass() != o.getClass() ) {
				return false;
			}

			CitaAgenda that = (CitaAgenda) o;

			if ( getId() != null ? !getId().equals( that.getId() ) : that.getId() != null ) {
				return false;
			}
			return getReconocimiento() != null ? getReconocimiento().equals( that.getReconocimiento() ) : that.getReconocimiento() == null;
		}

		@Override
		public int hashCode() {
			int result = getId() != null ? getId().hashCode() : 0;
			result = 31 * result + ( getReconocimiento() != null ? getReconocimiento().hashCode() : 0 );
			return result;
		}
	}

	@Entity
	public static class Reconocimiento {
		@Id
		private Integer id;
		@Embedded
		private Enfermeria enfermeria;

		Reconocimiento() {

		}

		Reconocimiento(Integer id, Enfermeria enfermeria) {
			this.id = id;
			this.enfermeria = enfermeria;
		}

		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}

		public Enfermeria getEnfermeria() {
			return enfermeria;
		}

		public void setEnfermeria(Enfermeria enfermeria) {
			this.enfermeria = enfermeria;
		}

		@Override
		public boolean equals(Object o) {
			if ( this == o ) {
				return true;
			}
			if ( o == null || getClass() != o.getClass() ) {
				return false;
			}

			Reconocimiento that = (Reconocimiento) o;

			if ( getId() != null ? !getId().equals( that.getId() ) : that.getId() != null ) {
				return false;
			}
			return getEnfermeria() != null ? getEnfermeria().equals( that.getEnfermeria() ) : that.getEnfermeria() == null;
		}

		@Override
		public int hashCode() {
			int result = getId() != null ? getId().hashCode() : 0;
			result = 31 * result + ( getEnfermeria() != null ? getEnfermeria().hashCode() : 0 );
			return result;
		}
	}

	@Embeddable
	public static class Enfermeria {
		private boolean diabetes;

		Enfermeria() {
			this( false );
		}

		Enfermeria(boolean diabetes) {
			this.diabetes = diabetes;
		}

		public boolean isDiabetes() {
			return diabetes;
		}

		public void setDiabetes(boolean diabetes) {
			this.diabetes = diabetes;
		}

		@Override
		public boolean equals(Object o) {
			if ( this == o ) {
				return true;
			}
			if ( o == null || getClass() != o.getClass() ) {
				return false;
			}

			Enfermeria that = (Enfermeria) o;

			return isDiabetes() == that.isDiabetes();
		}

		@Override
		public int hashCode() {
			return ( isDiabetes() ? 1 : 0 );
		}
	}
}
