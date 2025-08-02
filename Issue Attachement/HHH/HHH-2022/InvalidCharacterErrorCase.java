import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * Class description.
 */
public class InvalidCharacterErrorCase {

    public static void main(String[] args) {
        InvalidCharacterErrorCase.runTest();
    }

    private static void runTest() {
        Configuration configuration = new Configuration();
        configuration.configure();
        SessionFactory sessionFactory = configuration.buildSessionFactory();
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            SimpleObject o = new SimpleObject(
                    "test" + System.currentTimeMillis());
            o.getTags().add("tag1");
            session.saveOrUpdate(o);
            session.getTransaction().commit();
            session.evict(o);
            session.beginTransaction();
            SimpleObject o2 = (SimpleObject) session.get(SimpleObject.class, o.getId());
            session.getTransaction().commit();
        } catch (RuntimeException e) {
            session.getTransaction().rollback();
            throw e;
        } finally {
            session.close();
            sessionFactory.close();
        }
    }

}
