package org.hibernate.orm.test.annotations;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import org.hibernate.orm.test.jpa.BaseEntityManagerFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author Yanming Zhou
 */
public class HHH18006Test extends BaseEntityManagerFunctionalTestCase {
	@Override
	protected Class<?>[] getAnnotatedClasses() {
		return new Class[]{MyClassConverter.class, MyEntity.class};
	}

	@Test
	public void test() {
		EntityManager entityManager = getOrCreateEntityManager();
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();
		MyEntity entity = new MyEntity();
		entity.myEmbeddable = new MyEmbeddable<>();
		entity.myEmbeddable.myField = new MyClass("test");
		entityManager.persist(entity);
		transaction.commit();
		MyEntity persisted = entityManager.find(MyEntity.class, entity.id);
		Assert.assertEquals("test", persisted.myEmbeddable.myField.value);
	}

	static class MyClass implements Serializable {
		String value;

		MyClass(String value) {
			this.value = value;
		}

		public boolean equals(Object other) {
			if (!(other instanceof MyClass)) {
				return false;
			}
			return Objects.equals(this.value, ((MyClass) other).value);
		}
	}

	@Converter(autoApply = true)
	static class MyClassConverter implements AttributeConverter<MyClass, String> {

		@Override
		public String convertToDatabaseColumn(MyClass myClass) {
			return myClass.value;
		}

		@Override
		public MyClass convertToEntityAttribute(String string) {
			return new MyClass(string);
		}
	}

	@Embeddable
	static class MyEmbeddable<C extends Serializable> {
		C myField;
	}

	@Entity
	static class MyEntity {
		@Id
		@GeneratedValue
		Long id;

		@Embedded
		MyEmbeddable<MyClass> myEmbeddable;
	}
}
