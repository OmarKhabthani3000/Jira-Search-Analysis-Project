import static org.junit.Assert.*;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.InformixDialect;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.junit.Before;
import org.junit.Test;

public class MainTest {

	public static volatile String DRIVER = "com.informix.jdbc.IfxDriver";
	public static volatile String URL = "*********";
	public static volatile String USER = "******";
	public static volatile String PASS = "******";

	public static final Dialect DIALECT = new InformixDialect();

	private static SessionFactory sessionFactory;
	private static ServiceRegistry serviceRegistry;
	private Session session;

	public static Configuration buildBaseConfiguration() {
		return new Configuration().setProperty(Environment.DRIVER, DRIVER)
				.setProperty(Environment.URL, URL)
				.setProperty(Environment.USER, USER)
				.setProperty(Environment.PASS, PASS)
				.setProperty(Environment.DIALECT, DIALECT.getClass().getName())
				.setProperty(Environment.SHOW_SQL, "TRUE");
	}

	@Before
	public void setUp() throws Exception {
		Configuration configuration = buildBaseConfiguration();
		// configuration.configure();
		configuration.addResource("TestTbl.hbm.xml");
		serviceRegistry = new ServiceRegistryBuilder().applySettings(
				configuration.getProperties()).buildServiceRegistry();

		sessionFactory = configuration.buildSessionFactory(serviceRegistry);

		session = sessionFactory.openSession();
	}

	@Test
	public void testInsert() {
		Transaction tr =null;
		try {
			TestTbl item1=new TestTbl();
			item1.setCcol("test string1");
//			session.createQuery("from TestTbl").list().size();
			tr = session.beginTransaction();
			session.save(item1);
			tr.commit();
		} catch (HibernateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			tr.rollback();
			fail();
		}finally{
			session.close();
		}
	}

}
