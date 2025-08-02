import java.util.List;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;
import net.sf.hibernate.SessionFactory;
import net.sf.hibernate.Transaction;
import net.sf.hibernate.cfg.Configuration;

public class HibBug {

    private static SessionFactory sessions;
    
    static {
        try {
        sessions = new Configuration()
            .addResource("HibBug.hbm.xml")
            .setProperty("hibernate.connection.driver_class", 
                         "com.mysql.jdbc.Driver")
            .setProperty("hibernate.connection.url", 
                         "jdbc:mysql://myServer/myDB")
            .setProperty("hibernate.connection.username", "")
            .setProperty("hibernate.connection.password", "")
            .setProperty("hibernate.show_sql", "true")
            .buildSessionFactory();
        }
        catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }
    
    public static void main(String[] args) throws HibernateException {
        
        Session session = sessions.openSession();
        Transaction tx = session.beginTransaction();
        List l = session.find(
            "from Client c where c.person.email.address = 'foo@exampe.com'");
        tx.commit();
        session.close();
    }
}
