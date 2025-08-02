import org.hibernate.EmptyInterceptor;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.classic.Session;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

/**
 * Test class to show the illustrate the issue  HHH-2666 "subselect fetching ignores max results"
 *
 * @author donckels (created on 2010-06-24)
 */
public class TestHibernateSubselect {

    // ----- public methods -----

    @Test
    public void subselectFetchIgnoresLimit() {
        // Setup
        AnnotationConfiguration configuration = new AnnotationConfiguration();
        configuration.addAnnotatedClass(Parent.class);
        configuration.addAnnotatedClass(Child.class);
        configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.HSQLDialect");
        configuration.setProperty("hibernate.connection.driver_class", "org.hsqldb.jdbcDriver");
        configuration.setProperty("hibernate.connection.url", "jdbc:hsqldb:mem:test;shutdown=true");
        configuration.setProperty("hibernate.hbm2ddl.auto", "create-drop");

        SessionFactory sessionFactory = configuration.buildSessionFactory();

        Session dataCreationSession = sessionFactory.openSession();
        for (int i = 0; 10 > i; i++) {
            Parent parent = new Parent(0 == i % 2 ? "SELECT-ME-" + i : "DONT-SELECT-ME-" + i);
            parent.children = new ArrayList<Child>();

            for (int j = 0; 3 > j; j++) {
                parent.children.add(new Child("Child " + j + " of " + parent.data));
            }

            dataCreationSession.save(parent);
        }

        dataCreationSession.flush();
        dataCreationSession.close();

        // Operate & Verify
        Session reloadDataSession = sessionFactory.openSession(new VerifyLimitExistsInterceptor());
        Query query = reloadDataSession
                .createQuery("from TestHibernateSubselect$Parent where data like 'SELECT-ME-%' ");
        query.setMaxResults(2);
        List<Parent> list = query.list();
        Assert.assertEquals(list.size(), 2);

        Child child = list.get(0).children.get(0);
        System.out.println("child = " + child);

    }

    // ----- inner classes -----

    @Entity
    public static class Parent {

        // ----- instance fields -----

        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        private int id;

        @OneToMany
        @JoinColumn(name = "PARENT_ID", nullable = false)
        @Cascade(CascadeType.ALL)
        @Fetch(FetchMode.SUBSELECT)
        private List<Child> children;

        private String data;

        // ----- constructors -----

        public Parent() {
        }

        public Parent(String data) {
            this.data = data;
        }
    }

    @Entity
    public static class Child {

        // ----- instance fields -----

        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        private int id;

        private String data;

        // ----- constructors -----

        public Child() {
        }

        public Child(String data) {
            this.data = data;
        }
    }

    private static class VerifyLimitExistsInterceptor extends EmptyInterceptor {

        // ----- Interceptor -----

        @Override
        public String onPrepareStatement(String sql) {
            //            System.out.println("sql = " + sql);
            Assert.assertTrue(sql.contains(" top "), "SQL doesn't contain a limit: " + sql);
            return super.onPrepareStatement(sql);
        }
    }
}
