import java.io.ByteArrayOutputStream;

import javax.persistence.Entity;
import javax.persistence.Id;

import junit.framework.TestCase;

import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.util.SerializationHelper;

public class SerializationTest extends TestCase
{
	@Entity
	public static class TestEntity
	{
		@Id
		private Long id;
	}

	public void testAnnotationConfigurationSerialization()
	{
		AnnotationConfiguration configuration = new AnnotationConfiguration();
		
		configuration.addAnnotatedClass(TestEntity.class);
		
		SerializationHelper.serialize(configuration, new ByteArrayOutputStream());
	}
}
