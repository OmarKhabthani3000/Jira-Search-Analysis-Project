import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Locale;
import java.util.Properties;

public class MissingColumnWithTurkishTest {

    private Connection conn;
    private SessionFactory sessionFactory;
    private Properties props = new Properties();

    @Before
    public void setUp() throws Exception {
        setDefaultLocale("tur");
        initDriver();
        initDb();

        props.put("hibernate.connection.driver_class", "org.hsqldb.jdbc.JDBCDriver");
        props.put("hibernate.connection.url", "jdbc:hsqldb:mem:testdb");
        props.put("hibernate.connection.username", "sa");
        props.put("hibernate.connection.password", "");
        props.put("hibernate.connection.pool_size", "1");
        props.put("hibernate.dialect", "org.hibernate.dialect.HSQLDialect");
        props.put("hibernate.hbm2ddl.auto", "validate");
    }

    @Test
    public void testBuildSessionFactory() {
        sessionFactory = new Configuration()
                .addProperties(props)
                .addAnnotatedClass(Element.class)
                .buildSessionFactory();
    }

    @After
    public void tearDown() throws Exception {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
        if (conn != null) {
            conn.close();
        }
    }

    private void setDefaultLocale(String isoCode) {
        if (!Locale.getDefault().getISO3Language().equals(isoCode)) {
            for (Locale l : Locale.getAvailableLocales()) {
                if (l.getISO3Language().equals(isoCode)) {
                    Locale.setDefault(l);
                    return;
                }
            }
            throw new IllegalStateException("could not set locale to " + isoCode);
        }
    }

    private void initDriver() throws Exception {
        Class.forName("org.hsqldb.jdbc.JDBCDriver");
    }

    private void initDb() throws Exception {
        conn = DriverManager.getConnection("jdbc:hsqldb:mem:testdb", "sa", "");
        Statement statement = conn.createStatement();
        statement.executeUpdate("CREATE TABLE element (id INTEGER)");
        statement.close();
    }

    @Entity
    @Table(name = "element")
    public static class Element {
        @Id
        @Column(name = "id")
        private Long id;

        public Element() {
            this.id = null;
        }

        public Element(Long id) {
            this.id = id;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }
    }
}
