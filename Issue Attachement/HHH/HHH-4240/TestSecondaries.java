import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.MappedSuperclass;
import javax.persistence.SecondaryTable;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.junit.Test;

/**
 * <p>
 * The <code>TestSecondaries</code> is used to verify that the
 * AnnotationConfiguration doesn't work like expected.
 * </p>
 * 
 * <p>
 * &copy 2006 by paybox solutions AG
 * </p>
 * 
 * @author <a href='mailto:Sebastian.Kirsch@paybox.net'>Sebastian Kirsch</a>
 * @version $Revision: 1.1 $ $Name: $
 */
public class TestSecondaries {

    @Test
    public final void testSetup() {
	AnnotationConfiguration cfg = new AnnotationConfiguration();
	cfg.configure("mobiliser.cfg.xml");
	cfg.addAnnotatedClass(Super.class);
	cfg.addAnnotatedClass(Customer.class);
	cfg.addAnnotatedClass(EndConsumer.class);
	SessionFactory sF = cfg.buildSessionFactory();
    }

    @MappedSuperclass
    public class Super {

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DAT_CREATION")
	public Date creation;

    }

    @Entity
    @Inheritance(strategy = InheritanceType.JOINED)
    @Table(name = "Customers")
    @SecondaryTable(name = "Addresses")
    public class Customer extends Super {

	@Id
	@Column(name = "ID_CUSTOMER")
	public Long id;

	@Column(name = "STR_FIRSTNAME")
	public String firstName;

	@Column(table = "Addresses", name = "STR_STREET")
	public String street;

    }

    @Entity
    @Table(name = "EndConsumers")
    public class EndConsumer extends Customer {

	@Column(name = "ID_STATUS")
	public int status;

    }

}