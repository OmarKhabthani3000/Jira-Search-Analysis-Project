/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.lob;

import java.sql.Clob;
import java.sql.SQLException;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.hibernate.dialect.InformixDialect;
import org.hibernate.query.Query;

import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.hibernate.testing.transaction.TransactionUtil.doInHibernate;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * @author Andrea Boriero
 * @author VladoKuruc
 */
@TestForIssue(jiraKey = "HHH-8511")
@RequiresDialect(InformixDialect.class)
public class InformixLobStringTest extends BaseCoreFunctionalTestCase {

	private final String value1 = "xxxxxxxxxx";
	private final String value2 = "yyyyyyyyyy";

	@Override
	protected Class<?>[] getAnnotatedClasses() {
		return new Class[] {TestEntity.class};
	}

	@Override
	protected void prepareTest() throws Exception {
		TestEntity entity = new TestEntity();
		doInHibernate( this::sessionFactory, session -> {

						   entity.setFirstLobField( value1 );
						   entity.setSecondLobField( value2 );
						   entity.setClobField( session.getLobHelper().createClob( value2 ) );
						   session.save( entity );
					   } );

		doInHibernate( this::sessionFactory, session -> {
						   final TestEntity testEntity = session.find( TestEntity.class, entity.getId() );
						   assertThat( testEntity.getFirstLobField(), is( value1 ) );
					   } );
	}

	@Test
	@TestForIssue(jiraKey = "HHH-8511")
	public void testHqlQuery() {
		doInHibernate( this::sessionFactory, session -> {
						   final Query query = session.createQuery( "from TestEntity" );

						   final List<TestEntity> results = query.list();

						   assertThat( results.size(), is( 1 ) );

						   final TestEntity testEntity = results.get( 0 );
						   assertThat( testEntity.getFirstLobField(), is( value1 ) );
						   assertThat( testEntity.getSecondLobField(), is( value2 ) );
						   final Clob clobField = testEntity.getClobField();
						   try {

							   assertThat( clobField.getSubString( 1, (int) clobField.length() ), is( value2 ) );
						   }
						   catch (SQLException e) {
							   fail( e.getMessage() );
						   }
					   } );
	}

	@Entity(name = "TestEntity")
	@Table(name = "TEST_ENTITY")
	public static class TestEntity {
		@Id
		@GeneratedValue
		private long id;

		@Lob
		String firstLobField;

		@Lob
		String secondLobField;

		@Lob
		Clob clobField;

		public long getId() {
			return id;
		}

		public String getFirstLobField() {
			return firstLobField;
		}

		public void setFirstLobField(String firstLobField) {
			this.firstLobField = firstLobField;
		}

		public String getSecondLobField() {
			return secondLobField;
		}

		public void setSecondLobField(String secondLobField) {
			this.secondLobField = secondLobField;
		}

		public Clob getClobField() {
			return clobField;
		}

		public void setClobField(Clob clobField) {
			this.clobField = clobField;
		}
	}

	@Override
	protected boolean isCleanupTestDataRequired() {
		return true;
	}
}