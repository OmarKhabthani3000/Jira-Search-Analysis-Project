/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.embeddables;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.H2Dialect;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.junit.Test;

@RequiresDialect(H2Dialect.class)
public class NullableOverrideTest extends BaseUnitTestCase {

	@Test
	public void testUnsetNullable() {
		SessionFactory sf = new Configuration().addAnnotatedClass( TestEntity.class )
				.setProperty( "hibernate.hbm2ddl.auto", "create-drop" )
				.buildSessionFactory();
		try {
			Session sess = sf.openSession();
			TestEntity t = new TestEntity("test-1");

			sess.save( t ); // <== THIS FAILS

			sess.flush();
			sess.clear();

			sess.close();
		}
		finally {
			sf.close();
		}
	}

	@Embeddable
	public static class Code implements java.io.Serializable {
		@Basic(optional = false)
		@Column(nullable = false)
		public String theCode;
	}

	@Entity
	public static class TestEntity {
		@Id
		public String id;

		@Embedded
		@AttributeOverrides({
			@AttributeOverride(name = "theCode", column = @Column(nullable = true))
		})
		public Code aCode;

		public TestEntity(String id) {
			this();
			setId(id);
		}

		public void setCode(Code aCode) {
			this.aCode = aCode;
		}

		protected TestEntity() {
		}

		protected void setId(String id) {
			this.id = id;
		}
	}
}
