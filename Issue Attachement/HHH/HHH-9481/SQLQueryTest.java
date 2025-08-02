import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.cfg.Configuration;
import org.junit.Before;
import org.junit.Test;

public class SQLQueryTest {

    private SessionFactory sessionFactory;
    
    @Before
    public void setup() {
        Configuration cfg = new Configuration()
            .addAnnotatedClass(Customer.class)
            .setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect")
            .setProperty("hibernate.connection.driver_clas", "com.mysql.jdbc.Driver")
            .setProperty("hibernate.connection.url", "jdbc:mysql://localhost/test")
            .setProperty("hibernate.connection.username", "root")
            .setProperty("hibernate.connection.password", "pass")
            .setProperty("hibernate.hbm2ddl.auto", "create")
            .setProperty("hibernate.cache.region.factory_class", "org.hibernate.testing.cache.CachingRegionFactory")
            .setProperty("hibernate.cache.use_second_level_cache", "true");
        sessionFactory = cfg.buildSessionFactory();
        
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Customer customer = new Customer(1, "Samuel");
        session.saveOrUpdate(customer);
        session.getTransaction().commit();
    }
    
    @Test
    public void test() {
        Session session1 = sessionFactory.openSession();
        session1.load(Customer.class, 1);
        session1.close();
        
        // There is a 2nd level cache of Customer object which is loaded before. 
        assertTrue(sessionFactory.getCache().containsEntity(Customer.class, 1));
        
        Session session2 = sessionFactory.openSession();
        SQLQuery query2 = session2.createSQLQuery("create table if not exists Account (id int)");
        query2.executeUpdate();
        session2.close();
        
        // Bug HHH-9481 fails here. An unrelated SQL query evicts the 2nd level cache of entity Customer.
        assertTrue(sessionFactory.getCache().containsEntity(Customer.class, 1));
        
        Session session3 = sessionFactory.openSession();
        SQLQuery query3 = session3.createSQLQuery("update Customer set name = 'John' where id = 1");
        query3.addSynchronizedEntityClass(Customer.class);
        query3.executeUpdate();
        session3.close();
        
        // After executing SQL with Customer as "synchronized entity", its 2nd level cache is evicted.
        assertFalse(sessionFactory.getCache().containsEntity(Customer.class, 1));
    }
    
    
    @Entity
    @Table(name="Customer")
    @Cacheable
    @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
    public static class Customer {
        @Id
        private int id;

        private String name;

        public Customer() {
        }

        public Customer(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }        
    }

}
