package test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.classic.Session;

public class ByteTypeDescriptorTest {

	public static void main(String[] args) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, SQLException {
		Properties props = new Properties();
		InputStream input = new FileInputStream(args[0]);
		props.load(input);
		input.close();

	    Configuration cfg = new Configuration();
		Properties hprops = new Properties();
		hprops.put("hibernate.connection.driver_class", props.getProperty("dataSource.driver"));
		hprops.put("hibernate.connection.url", props.getProperty("dataSource.url"));
		hprops.put("hibernate.connection.username", props.getProperty("dataSource.username"));
		hprops.put("hibernate.connection.password", props.getProperty("dataSource.password"));
		hprops.put("hibernate.dialect", props.get("hibernate.dialect"));
		hprops.put("hibernate.current_session_context_class", "thread");
		hprops.put("hibernate.cache.provider_class", "org.hibernate.cache.NoCacheProvider");
		hprops.put("hibernate.hbm2ddl.auto", "update");
		hprops.put("hibernate.connection.autocommit", "true");

		cfg.setProperties(hprops);

		cfg.addInputStream(ClassLoader.getSystemResourceAsStream("test/ByteTypeDescriptor.hbm.xml"));

		SessionFactory sessionFactory = cfg.buildSessionFactory();

		emptyTable(sessionFactory);
		initTable(sessionFactory);
		testNamedQuery(sessionFactory);
		emptyTable(sessionFactory);
	}

	private static void emptyTable(SessionFactory sessionFactory) {
		Session session = sessionFactory.openSession();
		try {
			session.createQuery("delete from Test").executeUpdate();
		} finally {
			session.close();
		}
	}

	private static void initTable(SessionFactory sessionFactory) {
		Session session = sessionFactory.openSession();
		try {
			session.createQuery("delete from Test").executeUpdate();
			for (int n = Byte.MIN_VALUE; n <= Byte.MAX_VALUE; ++n) {
				session.save(new Test((byte)n));
			}
			session.flush();
		} finally {
			session.close();
		}
	}

	private static void testNamedQuery(SessionFactory sessionFactory) {
		Session session = sessionFactory.openSession();
		try {
			List<?> list = session.getNamedQuery("test_query").list();
			System.out.println("List: " + list);
		} finally {
			session.close();
		}
	}

}
