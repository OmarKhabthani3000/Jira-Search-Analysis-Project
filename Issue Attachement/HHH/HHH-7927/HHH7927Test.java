import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.HSQLDialect;
import org.hibernate.tool.hbm2ddl.SchemaUpdate;
import org.hibernate.tool.hbm2ddl.SchemaValidator;
import org.hsqldb.Server;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Demonstrates Hibernate issue <a href="https://hibernate.onjira.com/browse/HHH-7927">HHH-7927</a>.
 *
 * @author Sergey Parhomenko
 */
public class HHH7927Test {
	@Entity
	private static class TestEntity {
		@Id
		@GeneratedValue(strategy = GenerationType.TABLE)
		private int id;
	}

	private Server server;

	@Before
	public void setUp() {
		server = new Server();
		server.setSilent(true);
		server.setDatabaseName(0, "");
		server.setDatabasePath(0, "mem:test");
		server.start();
	}

	@Test
	public void test() {
		Configuration configuration = new Configuration().addAnnotatedClass(TestEntity.class);
		configuration.setProperty(AvailableSettings.GLOBALLY_QUOTED_IDENTIFIERS, Boolean.TRUE.toString());
		configuration.setProperty(AvailableSettings.USE_NEW_ID_GENERATOR_MAPPINGS, Boolean.TRUE.toString());
		configuration.setProperty(AvailableSettings.DIALECT, HSQLDialect.class.getName());
		configuration.setProperty(AvailableSettings.URL, "jdbc:hsqldb:hsql://localhost");
		new SchemaUpdate(configuration).execute(false, true);

		new SchemaValidator(configuration).validate();
	}

	@After
	public void tearDown() {
		server.stop();
	}
}
