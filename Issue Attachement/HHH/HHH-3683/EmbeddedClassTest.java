package test;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import junit.framework.TestCase;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.annotations.Cascade;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Environment;
import org.hibernate.dialect.HSQLDialect;
import org.hibernate.tool.hbm2ddl.SchemaExport;

public class EmbeddedClassTest extends TestCase
{

	@Entity
	public static class Car
	{

		@Id
		int id;

		@Embedded
		Registration registration = new Registration();
	}

	@Embeddable
	public static class Registration
	{

		@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
		@Cascade( { org.hibernate.annotations.CascadeType.SAVE_UPDATE })
		@JoinColumn(name = "primaryDriverId", referencedColumnName = "id", nullable = false)
		Driver primaryDriver; // many-to-one, cascade

		String rego;
	}

	@Entity
	public static class Driver
	{

		@Id
		int id;

		String name;
	}

	private SessionFactory sessionFactory;

	@Override
	protected void setUp()
	{

		AnnotationConfiguration configuration = new AnnotationConfiguration();
		configuration.setProperty(Environment.DRIVER, "org.hsqldb.jdbcDriver");
		configuration.setProperty(Environment.URL, "jdbc:hsqldb:mem:mydb");
		configuration.setProperty(Environment.USER, "sa");
		configuration.setProperty(Environment.PASS, "");
		configuration.setProperty(Environment.DIALECT, HSQLDialect.class.getName());
		configuration.setProperty(Environment.SHOW_SQL, "false");
		configuration.setProperty(Environment.USE_SECOND_LEVEL_CACHE, "false");
		configuration.setProperty("jdbc.batch_size", "100");
		configuration.setProperty(Environment.ORDER_INSERTS, "true");

		configuration.addAnnotatedClass(Car.class);
		configuration.addAnnotatedClass(Driver.class);

		// creates - and does not drop! - all the tables
		SchemaExport schemaExport = new SchemaExport(configuration);
		schemaExport.execute(false, true, false, true);

		sessionFactory = configuration.buildSessionFactory();

	}

	public void testBatching()
	{

		Session session = sessionFactory.openSession();

		Driver simon = new Driver();
		simon.id = 1;
		Driver chris = new Driver();
		chris.id = 2;

		System.out.println("First batch");

		// Create a car with Simon as the driver
		Car twocv = new Car();
		twocv.id = 1;
		twocv.registration.primaryDriver = simon;
		session.save(twocv);
		session.flush(); // this works

		System.out.println("Second batch");

		// Create another car with simon as the driver
		// In the same batch create a car with chris as a driver.
		Car punto = new Car();
		punto.id = 2;
		punto.registration.primaryDriver = simon;
		session.save(punto);

		Car ferrari = new Car();
		ferrari.id = 3;
		ferrari.registration.primaryDriver = chris;
		session.save(ferrari);
		session.flush();

	}

}