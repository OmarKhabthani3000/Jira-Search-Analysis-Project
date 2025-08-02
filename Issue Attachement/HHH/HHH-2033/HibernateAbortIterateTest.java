import java.util.Iterator;

import junit.framework.TestCase;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.exception.GenericJDBCException;

public class HibernateAbortIterateTest extends TestCase {

    private Session session;

    @Override
    protected void setUp() throws Exception {
        session = HibernateTools.openSession();
    }

    @Override
    protected void tearDown() throws Exception {
        session.close();
    }

    public void testAbortIterateSimple() throws Exception {
        Integer count = (Integer)session.createQuery("select count(*) from Account").uniqueResult();
        assertTrue("count > 1", count.intValue() > 1); // to be sure, that there are more then one row

        int iterations = 0;
        try {
            Query query = session.createQuery("from Account");
            for (int i = 0; i < 10000; i++) {
                iterations++;
                Iterator iterator = query.iterate();
                iterator.next();
            }
        } finally {
            System.out.println("Iterations: " + iterations);
        }
    }

    public void testAbortIterate() throws Exception {
        Integer count = (Integer)session.createQuery("select count(*) from Account").uniqueResult();
        assertTrue("count > 1", count.intValue() > 1); // to be sure, that there are more then one row

        int iterations = 0;
        try {
            Query query = session.createQuery("from Account");
            for (int i = 0; i < 10000; i++) { // processing data from an import-file
                iterations++;
                try {
                    Iterator iterator = query.iterate();
                    while (iterator.hasNext()) {
                        Account user = (Account)iterator.next();
                        user.getName(); // do something ..
                        if (true) { // check something here that fails ...
                            throw new Exception();
                        }
                    }
                } catch (GenericJDBCException e) {
                    throw e;
                } catch (Exception e) {
                    // log exception ...
                }
            }
        } finally {
            System.out.println("Iterations: " + iterations);
        }
    }

}