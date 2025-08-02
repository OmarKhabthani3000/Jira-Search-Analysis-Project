import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.EJB3NamingStrategy;
import org.junit.Test;

/**
 * Demonstrates Hibernate issue <a href="https://hibernate.onjira.com/browse/HHH-7890">HHH-7890</a>.
 *
 * @author Sergey Parhomenko
 */
public class HHH7890Test {
	@Entity
	@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "name" }) })
	private static class TestEntity {
		@Id
		private int id;

		private String name;
	}

	private static Configuration getConfiguration(boolean quoteIdenfitiers) {
		Configuration configuration = new Configuration().addAnnotatedClass(TestEntity.class);
		configuration.setProperty(AvailableSettings.GLOBALLY_QUOTED_IDENTIFIERS, Boolean.toString(quoteIdenfitiers));
		return configuration;
	}

	@Test
	public void testWorks() {
		getConfiguration(false).buildMappings();
	}

	@Test
	public void testFails() {
		getConfiguration(true).buildMappings();
	}

	@Test
	public void testWorkaround() {
		Configuration configuration = getConfiguration(true);
		// customize default naming strategy
		configuration.setNamingStrategy(new EJB3NamingStrategy() {
			@Override
			public String logicalColumnName(String columnName, String propertyName) {
				// note that we don't have a way here to determine the value of hibernate.globally_quoted_identifiers
				// setting, so this strategy will only work if this setting is set to true.
				return "`" + super.logicalColumnName(columnName, propertyName) + "`";
			}
		});
		configuration.buildMappings();
	}
}
