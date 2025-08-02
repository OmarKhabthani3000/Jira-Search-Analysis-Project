import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.temenos.core.util.HibernateUtil;

import junit.framework.TestCase;

public class SessionCloseTest extends TestCase {
    
    public void testSessionClose() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.createQuery("SELECT * FROM DUAL");
            tx.commit();
        }
        catch (HibernateException he) {
            if (tx!=null) {
                tx.rollback();
            }
            throw he;
        }
        finally {
            session.close();
        }
    }

    public void testSessionCloseWithIsOpenCheck() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.createSQLQuery("SELECT * FROM DUAL");
            tx.commit();
        }
        catch (HibernateException he) {
            if (tx!=null) {
                tx.rollback();
            }
            throw he;
        }
        finally {
            if (session.isOpen()) {
                session.close();
            }
        }
    }
}
