package test.lazyloading;

import java.io.Serializable;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.jdbc.ConnectionManager;

public class Test {

    private final static Logger log = Logger.getLogger(Test.class);

    public void run() {
        initLog4j();

        // Initialize Hibernate.
        Configuration cfg = new Configuration().configure("/hibernate.cfg.xml");
        SessionFactory sf = cfg.buildSessionFactory();

        // Create test data.
        Serializable pk = createTestData(sf);

        // Load data for test.
        Session session = sf.openSession();
        Person person = (Person) session.load(Person.class, pk);

        // Now the test.
        // We access a lazily loaded collection without having an active transaction. We
        adjustLog4jForTest();
        log.info("Accessing lazily loaded collection...");
        person.getPets().size();
        // Hibernate.initialize(person.getPets());
        log.info("...done with collection. Should have seen \"aggressively releasing JDBC connection\".");

        log.info("");

        log.info("Executing a query...");
        Query q = session.createQuery("FROM " + Person.class.getName());
        q.list();
        log.info("...done with query. Should have seen \"aggressively releasing JDBC connection\".");

        // Clean up and finish.
        log.info("");
        session.close();
    }

    private Serializable createTestData(SessionFactory sf) {
        Pet pet1 = new Pet();
        pet1.setName("Pet1");
        Person person1 = new Person();
        person1.setName("Person1");
        person1.getPets().add(pet1);

        Session session = sf.openSession();
        Transaction tx = session.beginTransaction();
        session.save(person1);
        tx.commit();
        session.close();

        return person1.getId();
    }

    private void initLog4j() {
        Logger.getRootLogger().setLevel(Level.WARN);
        Logger.getLogger("test").setLevel(Level.INFO);
        Logger.getRootLogger().addAppender(new ConsoleAppender(new PatternLayout("%-5p [%-20.20c{1}] %m%n")));
    }

    private void adjustLog4jForTest() {
        Logger.getLogger(ConnectionManager.class).setLevel(Level.DEBUG);
        Logger.getLogger("org.hibernate.SQL").setLevel(Level.DEBUG);
    }

    public static void main(String[] args) {
        Test t = new Test();
        t.run();
    }
}
