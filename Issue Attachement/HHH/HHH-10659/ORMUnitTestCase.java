/*
 * Copyright 2014 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hibernate.test;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.NaturalIdCache;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Test;

import javax.persistence.*;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;


/**
 * This template demonstrates how to develop a test case for Hibernate ORM, using its built-in unit test framework.
 * Although ORMStandaloneTestCase is perfectly acceptable as a reproducer, usage of this class is much preferred.
 * Since we nearly always include a regression test with bug fixes, providing your reproducer using this method
 * simplifies the process.
 *
 * What's even better?  Fork hibernate-orm itself, add your test case directly to a module's unit tests, then
 * submit it as a PR!
 */
public class ORMUnitTestCase extends BaseCoreFunctionalTestCase {

	@Override
	protected Class<?>[] getAnnotatedClasses() {
		return new Class[] {MyEntity.class};
	}

	// Add in any settings that are specific to your test.  See resources/hibernate.properties for the defaults.
	@Override
	protected void configure(Configuration configuration) {

		configuration.setProperty( AvailableSettings.GENERATE_STATISTICS, "true" );
	}

	@Test
	public void naturalIdNotCached() throws Exception {

        // First session - create new entity
		Session session = openSession();
		Transaction transaction = session.beginTransaction();

		MyEntity myEntity = new MyEntity();
		myEntity.setNaturalId("natural id 1");
        myEntity.setPayload("payload 1");
		session.save(myEntity);

		transaction.commit();
		session.close();

        assertThat( sessionFactory().getStatistics().getNaturalIdQueryExecutionCount(), is(0L) );
        assertThat( sessionFactory().getStatistics().getNaturalIdCacheHitCount(), is(0L) );
        assertThat( sessionFactory().getStatistics().getNaturalIdCacheMissCount(), is(0L) );
        assertThat( sessionFactory().getStatistics().getNaturalIdCachePutCount(), is(1L) ); // Invalid entry (with null identity) was put into cache

        // Second session - load entity by natural id
        session = openSession();
        transaction = session.beginTransaction();
        MyEntity loadedEntity = session.bySimpleNaturalId(MyEntity.class).load("natural id 1");
        assertThat(loadedEntity, is(notNullValue()));
        transaction.commit();
        session.close();

        assertThat( sessionFactory().getStatistics().getNaturalIdQueryExecutionCount(), is(1L) );
        assertThat( sessionFactory().getStatistics().getNaturalIdCacheHitCount(), is(1L) ); // Fails - Natural ID cache lookup failed
        assertThat( sessionFactory().getStatistics().getNaturalIdCacheMissCount(), is(0L) );
        assertThat( sessionFactory().getStatistics().getNaturalIdCachePutCount(), is(1L) );

	}

    @Entity
    @NaturalIdCache
    @SuppressWarnings({"unused", "WeakerAccess"})
    public static class MyEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id = null;

        @NaturalId
        private String naturalId;

        @Column
        private String payload;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getNaturalId() {
            return naturalId;
        }

        public void setNaturalId(String naturalId) {
            this.naturalId = naturalId;
        }

        public String getPayload() {
            return payload;
        }

        public void setPayload(String payload) {
            this.payload = payload;
        }
    }
}