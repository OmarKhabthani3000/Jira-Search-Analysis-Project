/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.dialect;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Query;

import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.junit.Test;

import static org.hibernate.testing.transaction.TransactionUtil.doInJPA;
import static org.junit.Assert.assertEquals;

/**
 * @author Chris Cranford
 */
public class SQLServerDynamicQueryTest extends BaseEntityManagerFunctionalTestCase {
	@Override
	protected Class<?>[] getAnnotatedClasses() {
		return new Class<?>[] { Document.class };
	}

	@Test
	public void testDynamicQuery() {
		initializeData();

		final Map<String, Object> properties = buildParameterProperties();
		final String queryString = buildDynamicQueryString( properties, false );

		// test no limits
		doInJPA( this::entityManagerFactory, entityManager -> {
			Query query = entityManager.createQuery( queryString );
			applyParameterValueBindings( query, properties );
			List results = query.getResultList();
			assertEquals( 3, results.size() );
		} );

		// test set first result value
		doInJPA( this::entityManagerFactory, entityManager -> {
			Query query = entityManager.createQuery( queryString );
			applyParameterValueBindings( query, properties );
			List results = query.setFirstResult( 2 ).getResultList();
			assertEquals( 1, results.size() );
		} );

		// test set max results value
		doInJPA( this::entityManagerFactory, entityManager -> {
			Query query = entityManager.createQuery( queryString );
			applyParameterValueBindings( query, properties );
			List results = query.setMaxResults( 2 ).getResultList();
			assertEquals( 2, results.size() );
		} );

		// test set max results and first  result value
		doInJPA( this::entityManagerFactory, entityManager -> {
			Query query = entityManager.createQuery( queryString );
			applyParameterValueBindings( query, properties );
			List results = query.setFirstResult( 2 ).setMaxResults( 2 ).getResultList();
			assertEquals( 1, results.size() );
		} );
	}

	private void initializeData() {
		doInJPA( this::entityManagerFactory, entityManager -> {
			for ( int i = 0; i < 3; ++i ) {
				Document document = new Document();
				document.setYear( 2017 );
				document.setMonth( 1 );
				document.setDay( i );
				entityManager.persist( document );
			}
		} );
	}

	private String buildDynamicQueryString(Map<String, Object> properties, boolean useOrdered) {
		StringBuilder sb = new StringBuilder();
		sb.append( "SELECT model FROM Document model WHERE 1=1" );

		int index = 1;
		for ( Map.Entry<String, Object> entry : properties.entrySet() ) {
			sb.append( " AND model." ).append( entry.getKey() ).append( " = ?" ).append( index++ );
		}

		if ( useOrdered ) {
			sb.append( " ORDER BY model.year desc, model.month desc, model.day desc" );
		}

		return sb.toString();
	}

	private void applyParameterValueBindings(Query query, Map<String, Object> properties) {
		int index = 1;
		for ( Object value : properties.values() ) {
			query.setParameter( index++, value );
		}
	}

	private Map<String, Object> buildParameterProperties() {
		Map<String, Object> properties = new HashMap<>();
		properties.put( "year", 2017 );
		return properties;
	}

	@Entity(name = "Document")
	public static class Document {
		@Id
		@GeneratedValue
		private Integer id;

		private Integer year;
		private Integer month;
		private Integer day;

		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}

		public Integer getYear() {
			return year;
		}

		public void setYear(Integer year) {
			this.year = year;
		}

		public Integer getMonth() {
			return month;
		}

		public void setMonth(Integer month) {
			this.month = month;
		}

		public Integer getDay() {
			return day;
		}

		public void setDay(Integer day) {
			this.day = day;
		}
	}
}
