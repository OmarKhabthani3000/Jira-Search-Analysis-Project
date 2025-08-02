package hibernatebug;

import junit.framework.TestCase;
import org.hibernate.*;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Restrictions;

import java.util.*;

public class BugTest
    extends TestCase
{
    private Session session;
    private Transaction transaction;

    protected void setUp() throws Exception {
        Configuration configuration = new Configuration();
        configuration.configure("test-hibernate.cfg.xml");
        configuration.addResource("HibernateBug.hbm.xml");

        SessionFactory sessionFactory = configuration.buildSessionFactory();
        session = sessionFactory.openSession();
        transaction = session.beginTransaction();

        entities();
    }

    public void testCriteria() throws Exception {
        List<Map<String, Object>> keys = new ArrayList<Map<String, Object>>();
        keys.add(map("mandator", "M", "key", "holger"));
        keys.add(map("mandator", "M", "key", "leon"));
        List list = session.createCriteria(Entity.class)
            .add(Restrictions.in("key", keys))
            .list();
        assertEquals(2, list.size());
    }

    public void testHQL() throws Exception {
        List<Map<String, Object>> keys = new ArrayList<Map<String, Object>>();
        keys.add(map("mandator", "M", "key", "holger"));
        keys.add(map("mandator", "M", "key", "leon"));
        List list = session.createQuery("from " + Entity.class.getName() + " where key in (:keys)")
            .setParameterList("keys", keys)
            .list();
        assertEquals(2, list.size());
    }

    private void entities() {
        Entity entity;
        entity = new Entity();
        entity.setKey(map("mandator", "M", "key", "holger"));
        session.save(entity);
        entity = new Entity();
        entity.setKey(map("mandator", "M", "key", "leon"));
        session.save(entity);
        entity = new Entity();
        entity.setKey(map("mandator", "M", "key", "florian"));
        session.save(entity);
    }

    protected void tearDown() throws Exception {
        transaction.rollback();
        session.close();
        session.getSessionFactory().close();
    }

    public static Map map(Object... o) {
        Map map = new HashMap();
        for (int i = 0; i < o.length; i+=2)
            map.put(o[i], o[i+1]);
        return map;
    }
}
