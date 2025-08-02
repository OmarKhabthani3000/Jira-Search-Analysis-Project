import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import junit.framework.TestCase;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.annotations.AccessType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.id.PersistentIdentifierGenerator;

public class HibernateKeyPoolTest extends TestCase {
    private SessionFactory sessionFactory;

    public void testKeyPoolAssignment() {
        Session session = sessionFactory.openSession();
        Mouth m = new Mouth();

        Tooth canine = new Tooth("canine");
        m.addTooth(canine);

        System.out.println("*** After creation");
        System.out.println(m);

        System.out.println("*** Save");
        session.saveOrUpdate(m);
        System.out.println(m);

        System.out.println("*** Flush");
        session.flush();
        System.out.println(m);

        System.out.println("*** Add new");
        m.addTooth(new Tooth("molar"));
        System.out.println(m);

        System.out.println("*** Save again");
        session.saveOrUpdate(m);
        System.out.println(m);

        System.out.println("*** Flush again");
        session.flush();
        System.out.println(m);
        
        assertEquals(3, SharedIDGenerator.counter.get());
    }

    @Entity @Table(name = "Mouth") @AccessType("field")
    public static class Mouth {
        @OneToMany(cascade = CascadeType.ALL, mappedBy = "mouth")
        @JoinColumn(name = "mouthId", nullable = false)
        private List<Tooth> teeth = new ArrayList<Tooth>();

        @Column(name = "id") @Id @GeneratedValue(generator = "idgen")
        @GenericGenerator(name = "idgen", strategy = "HibernateKeyPoolTest$SharedIDGenerator")
        private Integer id;

        public void addTooth(Tooth tooth) {
            teeth.add(tooth);
            tooth.mouth = this;
        }

        @Override
        public String toString() {
            return "Mouth (id=" + id + ") has teeth " + teeth;
        }
    }

    @Entity @Table(name = "Tooth") @AccessType("field")
    public static class Tooth {
        @ManyToOne @JoinColumn(name = "mouthId", nullable = false)
        @SuppressWarnings("unused")
        private Mouth mouth = null;

        private String name;

        @Column(name = "id") @Id @GeneratedValue(generator = "idgen")
        @GenericGenerator(name = "idgen", strategy = "HibernateKeyPoolTest$SharedIDGenerator")
        private Integer id;

        public Tooth(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name + "(id=" + id + ")";
        }
    }

    public static class SharedIDGenerator implements PersistentIdentifierGenerator {
        private static AtomicInteger counter = new AtomicInteger();

        public Serializable generate(SessionImplementor session, Object object) {
            return counter.incrementAndGet();
        }

        public String[] sqlCreateStrings(Dialect dialect) throws HibernateException {
            return new String[] {};
        }

        public Object generatorKey() {
            return "X";
        }

        public String[] sqlDropStrings(Dialect dialect) throws HibernateException {
            return new String[] {};
        }
    }

    @Override
    protected void setUp() throws Exception {
        AnnotationConfiguration config = new AnnotationConfiguration();
        config.addAnnotatedClass(Mouth.class).addAnnotatedClass(Tooth.class);
        config.setProperty("hibernate.connection.username", "sa");
        config.setProperty("hibernate.connection.password", "");
        config.setProperty("hibernate.show_sql", "true");
        config.setProperty("hibernate.dialect", "org.hibernate.dialect.HSQLDialect");
        config.setProperty("hibernate.connection.pool_size", "1");
        config.setProperty("hibernate.connection.autocommit", "true");
        config.setProperty("hibernate.cache.provider_class",
                "org.hibernate.cache.HashtableCacheProvider");
        config.setProperty("hibernate.hbm2ddl.auto", "create-drop");
        config.setProperty("hibernate.connection.driver_class", "org.hsqldb.jdbcDriver");
        config.setProperty("hibernate.connection.url", "jdbc:hsqldb:mem:HibernateKeyTest");
        sessionFactory = config.buildSessionFactory();
    }

    @Override
    protected void tearDown() throws Exception {
        sessionFactory.close();
    }

}
