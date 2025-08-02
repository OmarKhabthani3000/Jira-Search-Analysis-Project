package test;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.Id;

import junit.framework.TestCase;

import org.hibernate.annotations.NaturalId;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Environment;
import org.hibernate.dialect.HSQLDialect;

public class BugTestCase extends TestCase {
	
	public void test() throws Exception {
		AnnotationConfiguration config = new AnnotationConfiguration();
		config.addAnnotatedClass(MyEntity.class);
		config.getProperties().setProperty(Environment.DIALECT, HSQLDialect.class.getName());
		config.buildSessionFactory();
	}

	@Entity 
	public static class MyEntity {
		
		@Id Long id = 0L;
		
		@NaturalId
		Component component;
	}
	
	@Embeddable
	public static class Component {
		String value = "test";
	}
}
