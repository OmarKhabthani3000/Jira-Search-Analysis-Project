/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * Copyright (c) 2010, Red Hat, Inc. and/or its affiliates or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat, Inc.
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
package org.hibernate.search.test.query.dsl;

import org.apache.solr.analysis.LowerCaseFilterFactory;
import org.apache.solr.analysis.NGramFilterFactory;
import org.apache.solr.analysis.SnowballPorterFilterFactory;
import org.apache.solr.analysis.StandardFilterFactory;
import org.apache.solr.analysis.StandardTokenizerFactory;
import org.apache.solr.analysis.StopFilterFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.search.Environment;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.annotations.Factory;
import org.hibernate.search.cfg.SearchMapping;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.hibernate.search.test.SearchTestCase;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

/**
 * @author Emmanuel Bernard
 * @author Hardy Ferentschik
 * @author Mathieu Perez
 */
public class HSearch744BeforeFixTest extends SearchTestCase {

	FullTextSession fullTextSession;

	public void test() {

		Transaction transaction = fullTextSession.beginTransaction();

		final QueryBuilder monthQb = fullTextSession.getSearchFactory()
		.buildQueryBuilder().forEntity( Month.class ).get();

		try {

			monthQb.range().onField( "raindropInMm" ).below( 0.24d ).createQuery();

			fail( "createQuery() should throw NullPointerException " +
			"according to this reported bug " +
			"https://hibernate.onjira.com/browse/HSEARCH-744" );

		} catch ( NullPointerException e ) {
			transaction.rollback();
		}
	}

	public void setUp() throws Exception {
		super.setUp();
		Session session = openSession();
		fullTextSession = Search.getFullTextSession( session );
		indexTestData();
	}

	public void tearDown() throws Exception {
		cleanUpTestData();
		super.tearDown();
	}

	//FIXME add boolean tests

	private void indexTestData() {
		Transaction tx = fullTextSession.beginTransaction();
		final Calendar calendar = Calendar.getInstance();
		calendar.setTimeZone( TimeZone.getTimeZone( "UTC" ) );
		calendar.set( 0 + 1900, 2, 12, 0, 0, 0 );
		fullTextSession.persist(
				new Month(
						"January",
						1,
						"Month of colder and whitening",
						"Historically colder than any other month in the northern hemisphere",
						calendar.getTime(),
						0.231d
				)
		);
		calendar.set( 100 + 1900, 2, 12, 0, 0, 0 );
		fullTextSession.persist(
				new Month(
						"February",
						2,
						"Month of snowboarding",
						"Historically, the month where we make babies while watching the whitening landscape",
						calendar.getTime(),
						0.435d
				)
		);
		tx.commit();
		fullTextSession.clear();
	}

	private void cleanUpTestData() {
		if ( !fullTextSession.isOpen() ) {
			return;
		}
		Transaction tx = fullTextSession.beginTransaction();
		final List<Month> results = fullTextSession.createQuery( "from " + Month.class.getName() ).list();
		assertEquals( 2, results.size() );

		for ( Month entity : results ) {
			fullTextSession.delete( entity );
		}

		tx.commit();
		fullTextSession.close();
	}

	@Override
	protected Class<?>[] getAnnotatedClasses() {
		return new Class<?>[] {
				Month.class
		};
	}

	@Override
	protected void configure(Configuration cfg) {
		super.configure( cfg );
		cfg.getProperties().put( Environment.MODEL_MAPPING, MappingFactory.class.getName() );
	}

	public static class MappingFactory {
		@Factory
		public SearchMapping build() {
			SearchMapping mapping = new SearchMapping();
			mapping
					.analyzerDef( "stemmer", StandardTokenizerFactory.class )
					.filter( StandardFilterFactory.class )
					.filter( LowerCaseFilterFactory.class )
					.filter( StopFilterFactory.class )
					.filter( SnowballPorterFilterFactory.class )
					.param( "language", "English" )
					.analyzerDef( "ngram", StandardTokenizerFactory.class )
					.filter( StandardFilterFactory.class )
					.filter( LowerCaseFilterFactory.class )
					.filter( StopFilterFactory.class )
					.filter( NGramFilterFactory.class )
					.param( "minGramSize", "3" )
					.param( "maxGramSize", "3" );
			return mapping;
		}
	}
}
