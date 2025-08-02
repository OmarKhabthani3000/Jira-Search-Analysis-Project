package net.orless.hibernate.tests;

import java.sql.SQLException;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class ATest extends TestCase {

  public ATest(String x) {
    super(x);
  }

  public void testSaveOrUpdate() throws HibernateException, SQLException {

    final A a = new A();
    a.setId("a");
    a.getB().add(null);

    Session s = openSession();
    Transaction t = s.beginTransaction();
    s.saveOrUpdate(a);
    t.commit();
    s.close();

    a.getB().set(0, "b");

    s = openSession();
    t = s.beginTransaction();
    s.saveOrUpdate(a);
    t.commit();
    s.close();
  }

  protected String[] getMappings() {
    return new String[]{ "A.hbm.xml" };
  }
}
