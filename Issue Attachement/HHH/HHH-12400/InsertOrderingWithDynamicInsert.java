/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.insertordering;

import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.FetchProfile;
import org.hibernate.annotations.Proxy;
import org.hibernate.annotations.SortComparator;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.engine.jdbc.batch.internal.BatchBuilderImpl;
import org.hibernate.engine.jdbc.batch.internal.BatchBuilderInitiator;
import org.hibernate.engine.jdbc.batch.internal.BatchingBatch;
import org.hibernate.engine.jdbc.batch.spi.Batch;
import org.hibernate.engine.jdbc.batch.spi.BatchKey;
import org.hibernate.engine.jdbc.spi.JdbcCoordinator;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Test;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.CascadeType.REFRESH;
import static javax.persistence.FetchType.LAZY;

import static org.hibernate.annotations.CascadeType.SAVE_UPDATE;
import static org.hibernate.annotations.FetchMode.JOIN;

/**
 * @author Roger Dearnaley
 */
public class InsertOrderingWithDynamicInsert extends BaseCoreFunctionalTestCase {

	@Override
	public void configure(Configuration cfg) {
		super.configure(cfg);
		cfg.setProperty(Environment.ORDER_INSERTS, "true");
		cfg.setProperty(Environment.STATEMENT_BATCH_SIZE, "10");
	}

	@Test
	public void testBatchOrdering() {
		Session s = openSession();
		s.beginTransaction();

		// Create four Acl entities
		Acl acl1 = new Acl();
		Acl acl2 = new Acl();
		Acl acl3 = new Acl();
		Acl acl4 = new Acl();

		// Connect them parent-to-child in sequence
		acl2.setParent(acl1);
		acl3.setParent(acl2);
		acl4.setParent(acl3);

		// Give some Acl objects more non-null columns than others, so @DynamicInsert has something to do. Note that
		// long and short objects are alternated, so either possible query reordering will cause a constraint violation.
		acl2.setNonce(2L);
		acl2.setVersion(2L);
		acl2.setTypeName("foo");
		acl4.setNonce(2L);
		acl4.setVersion(2L);
		acl4.setTypeName("foo");

		// Save parent before child, for four generations
		s.save(acl1);
		s.save(acl2);
		s.save(acl3);
		s.save(acl4);

		s.getTransaction().commit();
		s.close();
	}

	@Override protected Class<?>[] getAnnotatedClasses() {
		return new Class<?>[] {
				Acl.class
		};
	}

	@DynamicInsert  // If this annotation is commented out, the test will pass
//	@DynamicUpdate
	@Entity(name = "Acl")
	@Proxy(proxyClass = Acl.class)
	@Table(name = "acl")
	public class Acl {

		//~ Instance fields --------------------------------------------------------

		private UUID   m_id;
		private Long   m_nonce;
		private Acl    m_parent;
		private String m_typeName;
		private Long   m_version;

		//~ Methods ----------------------------------------------------------------

		@Column(
				length = 16,
				updatable = false,
				nullable = false
		)
		@GeneratedValue()
		@Id
		@javax.validation.constraints.NotNull
		public UUID getId() {
			return m_id;
		}

		public void setId(UUID id) {
			m_id = id;
		}

		@Column(nullable = false)
		@javax.validation.constraints.NotNull
		@Version
		public Long getVersion() {
			return m_version;
		}

		public void setVersion(Long version) {
			m_version = version;
		}

		public Long getNonce() {
			return m_nonce;
		}

		public String getTypeName() {
			return m_typeName;
		}

		@Cascade(SAVE_UPDATE)
		@JoinColumn(
				name = "parent_id",
				foreignKey = @ForeignKey(name = "fk_acl_parent_acl")
		)
		@ManyToOne(
				fetch = LAZY,
				cascade = {PERSIST, MERGE, REFRESH},
				targetEntity = org.hibernate.test.insertordering.InsertOrderingWithDynamicInsert.Acl.class
		)
		public Acl getParent() {
			return m_parent;
		}

		void setNonce(Long nonce) {
			m_nonce = nonce;
		}

		public void setTypeName(String typeName) {
			m_typeName = typeName;
		}

		public void setParent(Acl parent) {
			m_parent = parent;
		}
	}
}
