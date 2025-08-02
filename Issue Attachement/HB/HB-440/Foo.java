import net.sf.hibernate.cfg.Configuration;
import net.sf.hibernate.*;
import java.util.Collection;

public class Foo {
  private int id;
  private Collection bars;

/* UNCOMMENT TO AVOID EXCEPTION
  public Collection getBars() {
    return bars;
  }
*/

  public static void main(String[] args) throws HibernateException {
    Configuration cfg = new Configuration();
    cfg.addClass(Foo.class);
    SessionFactory sessionFactory = cfg.buildSessionFactory();
    Session sess = sessionFactory.openSession();
    Foo foo = (Foo) sess.load(Foo.class, new Integer(1));
    System.out.println(foo.bars);
    sess.close();
  }
}
