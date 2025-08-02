import org.apache.log4j.BasicConfigurator;
import org.hibernate.Criteria;
import org.hibernate.EntityMode;
import org.hibernate.FetchMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

public class HbmXmlTest {

  /**
   * @param args
   * @throws Exception 
   */
  public static void main(String[] args) throws Exception {
    BasicConfigurator.configure();
    Configuration cfg = new Configuration().addResource("Test.hbm.xml");
    
    // Configure database here...
    
    SessionFactory factory = cfg.buildSessionFactory();
    
    Session session = null;
    Transaction transaction = null;
    try {
      session = factory.openSession();
      transaction = session.beginTransaction();
      
      Customer customer1 = new Customer();
      customer1.setName("John Doe");
      session.save(customer1);

      Customer customer2 = new Customer();
      customer2.setName("John Doe");
      session.save(customer2);

      Order order1 = new Order();
      order1.setClient(customer1);
      session.save(order1);
      
      Order order2 = new Order();
      order2.setClient(customer2);
      session.save(order2);

      OrderItem item1 = new OrderItem();
      item1.setCustomer(customer1);
      item1.setOrder(order1);
      session.save(item1);
      
      OrderItem item2 = new OrderItem();
      item2.setCustomer(customer1);
      item2.setOrder(order2);
      session.save(item2);

      
    } catch (Throwable t) {
      transaction.rollback();
      transaction = null;
      throw new Exception(t);
    } finally {
      if (transaction != null) transaction.commit();
      if (session != null) session.close();
    }
    

    try {
      session = factory.openSession();
      session = session.getSession(EntityMode.DOM4J);
      transaction = session.beginTransaction();
      
      Criteria criteria = session.createCriteria(OrderItem.class).setFetchMode("customer", FetchMode.JOIN);
      ScrollableResults scroll = criteria.scroll();
      while (scroll.next()) {
        scroll.get(0);
      }
      
    } catch (Throwable t) {
      transaction.rollback();
      transaction = null;
      throw new Exception(t);
    } finally {
      if (transaction != null) transaction.commit();
      if (session != null) session.close();
    }
  }
}
