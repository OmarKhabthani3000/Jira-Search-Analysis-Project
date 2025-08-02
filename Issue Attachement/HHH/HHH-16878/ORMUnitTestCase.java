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
package org.hibernate.bugs;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * This template demonstrates how to develop a test case for Hibernate ORM, using its built-in unit test framework.
 * Although ORMStandaloneTestCase is perfectly acceptable as a reproducer, usage of this class is much preferred.
 * Since we nearly always include a regression test with bug fixes, providing your reproducer using this method
 * simplifies the process.
 * <p>
 * What's even better?  Fork hibernate-orm itself, add your test case directly to a module's unit tests, then
 * submit it as a PR!
 */
public class ORMUnitTestCase extends BaseCoreFunctionalTestCase {

    // Add your entities here.
    @Override
    protected Class[] getAnnotatedClasses() {
        return new Class[]{User.class, Comment.class};
    }

    // Add in any settings that are specific to your test.  See resources/hibernate.properties for the defaults.
    @Override
    protected void configure(Configuration configuration) {
        super.configure(configuration);

        configuration.setProperty(AvailableSettings.SHOW_SQL, Boolean.TRUE.toString());
        configuration.setProperty(AvailableSettings.FORMAT_SQL, Boolean.TRUE.toString());
        //configuration.setProperty( AvailableSettings.GENERATE_STATISTICS, "true" );
    }

    // Add your tests, using standard JUnit.
    @Test
    public void hhh123CanSelectByAssociation() {
        Session s = openSession();
        Transaction tx = s.beginTransaction();

        // given
        User user1 = new User("test 1");
        User user2 = new User("test 2");
        s.persist(user1);
        s.persist(user2);
        s.persist(new Comment("example 1", user1));
        s.persist(new Comment("example 2", user2));
        s.persist(new Comment("example 3", user1));

        // when
        List<Comment> comments = s.createQuery("SELECT c FROM Comment c WHERE c.user = :user", Comment.class)
                .setParameter("user", user1)
                .getResultList();

        // then
        assertEquals(2, comments.size());

        tx.commit();
        s.close();
    }

    @Test
    public void hhh123CanUpdateByAssociation() {
        Session s = openSession();
        Transaction tx = s.beginTransaction();

        // given
        User user1 = new User("test 1");
        User user2 = new User("test 2");
        s.persist(user1);
        s.persist(user2);
        s.persist(new Comment("example 1", user1));
        s.persist(new Comment("example 2", user2));
        s.persist(new Comment("example 3", user1));

        // when
        int affectedComments = s.createMutationQuery("UPDATE Comment c SET c.text = :text WHERE c.user = :user")
                .setParameter("text", "updated")
                .setParameter("user", user1)
                .executeUpdate();

        // then
        assertEquals(2, affectedComments);

        tx.commit();
        s.close();
    }

    @Test
    public void hhh123CanDeleteByAssociation() {
        Session s = openSession();
        Transaction tx = s.beginTransaction();

        // given
        User user1 = new User("test 1");
        User user2 = new User("test 2");
        s.persist(user1);
        s.persist(user2);
        s.persist(new Comment("example 1", user1));
        s.persist(new Comment("example 2", user2));
        s.persist(new Comment("example 3", user1));

        // when
        int affectedComments = s.createMutationQuery("delete from Comment c where c.user = :user")
                .setParameter("user", user1)
                .executeUpdate();

        // then
        assertEquals(2, affectedComments);

        tx.commit();
        s.close();
    }
}
