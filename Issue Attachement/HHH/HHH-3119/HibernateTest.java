import java.io.File;
import java.util.List;

import junit.framework.TestCase;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.AnnotationConfiguration;

public class HibernateTest extends TestCase {

    private Session session;
    private Transaction tx;

    protected void setUp() throws Exception {
        AnnotationConfiguration config = new AnnotationConfiguration();
        File configFile = new File("cfg/hibernate.cfg.xml");
        SessionFactory sessionFactory = config.configure(configFile).buildSessionFactory();
        session = sessionFactory.openSession();
        tx = session.beginTransaction();
    }

    protected void tearDown() throws Exception {
        tx.rollback();
    }

    public void testLazyLoading() {
        Level1 level1 = (Level1) session.createQuery("from Level1 l where l.level1id = 1").uniqueResult();
        session.clear();
        System.out.println(level1);
    }

}
