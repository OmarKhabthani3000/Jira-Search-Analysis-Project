import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import junit.framework.TestCase;

import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.classic.Session;

/**
 * Test for hibernate bug report HHH-2083.
 * 
 */
public class HHH2083Test extends TestCase {

    /**
     * @throws Exception
     */
    public void testBug() throws Exception {
        AnnotationConfiguration configuration = new AnnotationConfiguration().addAnnotatedClass(Child.class);
        configuration.configure();
        SessionFactory factory = configuration.buildSessionFactory();
        Session session = factory.openSession();

        Child child = new Child();
        session.save(child);
        session.flush();
        checkLoad(factory);
        
        Child child2 = new Child();
        child2.parent=child;
        session.save(child2);
        checkLoad(factory);
        
        Child child3 = new Child();
        child3.parent=child2;
        session.save(child3);
        checkLoad(factory);

    }

    private void checkLoad(SessionFactory factory) {
        System.out.println("---------------------");
        StatelessSession session = factory.openStatelessSession();
        ScrollableResults results = session.createCriteria(Child.class).scroll(ScrollMode.FORWARD_ONLY);
        while (results.next()) {
            System.out.println("load " + ((Child) results.get(0)).fId);
        }
        session.close();
    }
}

@MappedSuperclass
abstract class Base<SubType> {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long fId;

    @ManyToOne
    public SubType parent;

}

@Entity
class Child extends Base<Child> {

}
