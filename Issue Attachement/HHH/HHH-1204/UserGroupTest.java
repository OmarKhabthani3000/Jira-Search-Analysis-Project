/*
 * Copyright 2001-2005 Fizteh-Center Lab., MIPT, Russia
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Created on 25.11.2005
 */
package ru.arptek.arpsite.data.usergroup;

import java.io.File;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import junit.framework.TestCase;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Environment;
import org.hibernate.dialect.DerbyDialect;
import org.hibernate.transaction.JOTMTransactionManagerLookup;

import ru.arptek.arpsite.data.JOTMTransactionFactory;
import ru.arptek.arpsite.data.XAPoolConnectionFactory;
import ru.arptek.common.IOTools;

public class UserGroupTest extends TestCase {

    private static final String DIALECT = DerbyDialect.class.getName();

    private static final String DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";

    static {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    static {
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.INFO);
        Logger.getLogger("org.hibernate").setLevel(Level.INFO);
        Logger.getLogger("org.hibernate.hql.PARSER").setLevel(Level.ERROR);
        Logger.getLogger("org.hibernate.util.JDBCExceptionReporter").setLevel(
                Level.ERROR);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(UserGroupTest.class);
    }

    protected AnnotationConfiguration cfg;

    private SessionFactory sessionFactory;

    protected void configure() {
        cfg = new AnnotationConfiguration();
        cfg.addAnnotatedClass(User.class);
        cfg.addAnnotatedClass(Group.class);
        // cfg.setCacheConcurrencyStrategy(TestTwoClassA.class.getName(),
        // CacheFactory.TRANSACTIONAL);
        // cfg.setCacheConcurrencyStrategy(TestTwoClassB.class.getName(),
        // CacheFactory.TRANSACTIONAL);

        cfg.setProperty(Environment.DIALECT, DIALECT);

        cfg.setProperty(Environment.CONNECTION_PROVIDER,
                XAPoolConnectionFactory.class.getName());
        {
            cfg.setProperty(XAPoolConnectionFactory.URL, getURL());
            cfg.setProperty(XAPoolConnectionFactory.MAX_CONNECTIONS, "5");
            cfg.setProperty(XAPoolConnectionFactory.DRIVER, DRIVER);
        }

        cfg.setProperty(Environment.SHOW_SQL, "true");
        cfg.setProperty(Environment.HBM2DDL_AUTO, "create-drop");
        cfg.setProperty(Environment.GENERATE_STATISTICS, "true");
        // cfg.setProperty(Environment.CACHE_PROVIDER,
        // SoftReferenceCacheProvider.class.getName());
        // cfg.setProperty(Environment.USE_SECOND_LEVEL_CACHE, "true");
        cfg.setProperty(Environment.USE_REFLECTION_OPTIMIZER, "true");

        cfg.setProperty(Environment.TRANSACTION_MANAGER_STRATEGY,
                JOTMTransactionManagerLookup.class.getName());
        cfg.setProperty(Environment.TRANSACTION_STRATEGY,
                JOTMTransactionFactory.class.getName());

        // cfg.setProperty(Environment.USE_QUERY_CACHE, "true");
    }

    public String getDIR() {
        return "derbydb";
    }

    protected String getURL() {
        return "jdbc:derby:" + getDIR() + ";create=true";
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        JOTMTransactionFactory.start();

        configure();
        sessionFactory = cfg.buildSessionFactory();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        sessionFactory.close();
        sessionFactory = null;

        try {
            DriverManager.getConnection(getURL() + ";shutdown=true");
        } catch (SQLException exc) {
            if (exc.getErrorCode() != 45000)
                throw exc;
        }
        assertEquals(true, IOTools.delete(new File(getDIR())));

        JOTMTransactionFactory.stop();
    }

    public void testTwoClass() {
        int userId;
        int groupId;
        {
            Session session = sessionFactory.openSession();
            Transaction transaction = session.beginTransaction();
            boolean complete = false;
            try {

                {
                    User user = new User();
                    user.setName("A");
                    userId = (Integer) session.save(user);
                    session.update(user);
                    session.flush();

                    Group group = new Group();
                    groupId = (Integer) session.save(group);
                    session.flush();

                    user = (User) session.load(User.class, userId);
                    group = (Group) session.load(Group.class, groupId);
                    user.getGroups().add(group);
                    session.save(user);
                    session.flush();
                    // assertEquals(classA, classB.getClassA());
                    // assertEquals(aId, classB.getClassA().getId());
                }

                session.flush();
                complete = true;
            } finally {
                if (complete)
                    transaction.commit();
                else
                    transaction.rollback();
            }
            session.close();
        }
        sessionFactory.evict(User.class);
        sessionFactory.evict(Group.class);
        {
            Session session = sessionFactory.openSession();
            Transaction transaction = session.beginTransaction();
            boolean complete = false;
            try {

                User user = (User) session.load(User.class, userId);
                assertEquals(userId, user.getId());
                assertEquals("A", user.getName());
                assertEquals(1, user.getGroups().size());
                assertEquals(groupId, user.getGroups().iterator().next()
                        .getId());

                complete = true;
            } finally {
                if (complete)
                    transaction.commit();
                else
                    transaction.rollback();
            }
            session.close();
        }
    }

    public void testAddGroupOnUserCreate() {
        int userId;
        int groupId;
        {
            Session session = sessionFactory.openSession();
            Transaction transaction = session.beginTransaction();
            boolean complete = false;
            try {

                {
                    Group group = new Group();
                    groupId = (Integer) session.save(group);
                    session.flush();

                    User user = new User();
                    user.setName("A");
                    user.getGroups().add(
                            (Group) session.load(Group.class, groupId));
                    userId = (Integer) session.save(user);
                    session.save(user);
                    session.flush();
                }

                session.flush();
                complete = true;
            } finally {
                if (complete)
                    transaction.commit();
                else
                    transaction.rollback();
            }
            session.close();
        }
        sessionFactory.evict(User.class);
        sessionFactory.evict(Group.class);
        {
            Session session = sessionFactory.openSession();
            Transaction transaction = session.beginTransaction();
            boolean complete = false;
            try {

                User user = (User) session.load(User.class, userId);
                assertEquals(userId, user.getId());
                assertEquals("A", user.getName());
                assertEquals(1, user.getGroups().size());
                assertEquals(groupId, user.getGroups().iterator().next()
                        .getId());

                Group group = (Group) session.load(Group.class, groupId);
                assertFalse(group.getUsers().isEmpty());
                assertEquals(1, group.getUsers().size());
                assertEquals(userId, group.getUsers().iterator().next().getId());

                complete = true;
            } finally {
                if (complete)
                    transaction.commit();
                else
                    transaction.rollback();
            }
            session.close();
        }
    }

    public void testFindByGroupRealm() {
        int user1Id;
        int user2Id;
        int groupId;
        {
            Session session = sessionFactory.openSession();
            Transaction transaction = session.beginTransaction();
            boolean complete = false;
            try {

                {
                    {
                        Group group = new Group();
                        groupId = (Integer) session.save(group);
                        session.flush();
                    }
                    {
                        User user1 = new User();
                        user1.setName("A");
                        user1.setRealm(1);
                        user1.getGroups().add(
                                (Group) session.load(Group.class, groupId));
                        user1Id = (Integer) session.save(user1);
                        session.save(user1);
                    }
                    {
                        User user2 = new User();
                        user2.setName("B");
                        user2.setRealm(2);
                        user2.getGroups().add(
                                (Group) session.load(Group.class, groupId));
                        user2Id = (Integer) session.save(user2);
                        session.save(user2);
                    }
                    session.flush();
                }

                session.flush();
                complete = true;
            } finally {
                if (complete)
                    transaction.commit();
                else
                    transaction.rollback();
            }
            session.close();
        }
        sessionFactory.evict(User.class);
        sessionFactory.evict(Group.class);
        {
            Session session = sessionFactory.openSession();
            Transaction transaction = session.beginTransaction();
            boolean complete = false;
            try {

                {
                    User user1 = (User) session.load(User.class, user1Id);
                    assertEquals(user1Id, user1.getId());
                    assertEquals("A", user1.getName());
                    assertEquals(1, user1.getGroups().size());
                    assertEquals(groupId, user1.getGroups().iterator().next()
                            .getId());
                }
                {
                    User user2 = (User) session.load(User.class, user2Id);
                    assertEquals(user2Id, user2.getId());
                    assertEquals("B", user2.getName());
                    assertEquals(1, user2.getGroups().size());
                    assertEquals(groupId, user2.getGroups().iterator().next()
                            .getId());
                }

                Group group = (Group) session.load(Group.class, groupId);
                assertFalse(group.getUsers().isEmpty());
                assertEquals(2, group.getUsers().size());

                {
                    Query query = session
                            .getNamedQuery("User.findByGroupRealm");
                    query.setEntity(0, group);
                    query.setInteger(1, 1);
                    List<User> list = query.list();
                    assertFalse(list.isEmpty());
                    assertEquals(1, list.size());
                    assertEquals(user1Id, list.iterator().next().getId());
                    assertEquals(1, list.iterator().next().getRealm());
                    assertEquals("A", list.iterator().next().getName());
                }

                {
                    Query query = session
                            .getNamedQuery("User.findByGroupRealm");
                    query.setEntity(0, group);
                    query.setInteger(1, 2);
                    List<User> list = query.list();
                    assertFalse(list.isEmpty());
                    assertEquals(1, list.size());
                    assertEquals(user2Id, list.iterator().next().getId());
                    assertEquals(2, list.iterator().next().getRealm());
                    assertEquals("B", list.iterator().next().getName());
                }

                complete = true;
            } finally {
                if (complete)
                    transaction.commit();
                else
                    transaction.rollback();
            }
            session.close();
        }
    }
}
