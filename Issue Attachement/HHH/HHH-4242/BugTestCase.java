package test;

import java.util.List;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.Id;

import junit.framework.TestCase;

import org.hibernate.annotations.CollectionOfElements;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Environment;
import org.hibernate.dialect.HSQLDialect;

public class BugTestCase extends TestCase {
	
	public void testEntityWithSingleElement() throws Exception {
		test(EntityWithSingleElement.class);
	}
	
	public void testEntityWithCollectionOfElements() throws Exception {
		test(EntityWithCollectionOfElements.class);
	}
	
	private void test(Class<?> entityClass) throws Exception {
		AnnotationConfiguration config = new AnnotationConfiguration();
		config.addAnnotatedClass(entityClass);
		config.getProperties().setProperty(Environment.DIALECT, HSQLDialect.class.getName());
		config.buildSessionFactory();
	}

	@Entity
	public static class EntityWithSingleElement {
		
		@Id Long id = 0L;
		
		ComponentA component;
	}
	
	@Entity
	public static class EntityWithCollectionOfElements {
		
		@Id Long id = 0L;
		
		@CollectionOfElements
		List<ComponentA> components;
	}

	@Embeddable
	public static class ComponentA {
		ComponentB componentB;
	}
	
	@Embeddable
	public static class ComponentB {
		String value = "test";
	}
}
