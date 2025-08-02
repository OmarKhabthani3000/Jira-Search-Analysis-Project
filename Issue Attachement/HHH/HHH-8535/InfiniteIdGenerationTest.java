import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.EntityManagerFactory;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.dialect.HSQLDialect;
import org.hibernate.ejb.AvailableSettings;
import org.hibernate.ejb.HibernatePersistence;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test case to reproduce Hibernate bug which hangs the application with infinite ID generation.
 *
 * @author Sergey Parhomenko
 */
public class InfiniteIdGenerationTest {
	private static final String URL = "jdbc:hsqldb:mem:test";
	private static final int WAIT_SECONDS = 10;

	@Entity
	private static class TestEntity {
		@Id
		@GeneratedValue(strategy = GenerationType.TABLE)
		private int id;
	}

	@Test
	public void test() throws SQLException, InterruptedException {
		// create EntityManagerFactory to generate the tables
		Map<String, Object> properties = new HashMap<>();
		properties.put(AvailableSettings.CLASS_NAMES, Collections.singleton(TestEntity.class.getName()));
		properties.put(org.hibernate.cfg.AvailableSettings.DIALECT, HSQLDialect.class.getName());
		properties.put(org.hibernate.cfg.AvailableSettings.URL, URL);
		properties.put(org.hibernate.cfg.AvailableSettings.HBM2DDL_AUTO, "update");
		final EntityManagerFactory emf = new HibernatePersistence().createEntityManagerFactory(properties);

		// insert a row with NULL value in TableGenerator table
		try (Connection connection = DriverManager.getConnection(URL);
				Statement statement = connection.createStatement()) {
			statement.execute(String.format("INSERT INTO HIBERNATE_SEQUENCES VALUES ('%s$%s', NULL)",
					InfiniteIdGenerationTest.class.getSimpleName(), TestEntity.class.getSimpleName()));
		}

		// try to save new entity with a generated ID in a separate thread
		Thread thread = new Thread() {
			@Override
			public void run() {
				emf.createEntityManager().persist(new TestEntity());
			}
		};
		thread.start();

		// wait for some time for that thread to finish
		for (int i = 0; i < WAIT_SECONDS && thread.isAlive(); i++) {
			Thread.sleep(1000);
		}

		// if the thread is still alive the ID generation is hung and bug is reproducible
		if (thread.isAlive()) {
			thread.stop(); // no way to gracefully stop the thread
			Assert.fail("Entity not persisted in " + WAIT_SECONDS + " seconds, likely an infinite ID generation loop");
		}
	}
}
