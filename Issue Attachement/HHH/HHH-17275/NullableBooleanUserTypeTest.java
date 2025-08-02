package org.hibernate.orm.test.mapping.converted.converter;

import java.io.Serializable;

import org.hibernate.annotations.Type;

import org.hibernate.testing.orm.junit.DomainModel;
import org.hibernate.testing.orm.junit.JiraKey;
import org.hibernate.testing.orm.junit.SessionFactory;
import org.hibernate.testing.orm.junit.SessionFactoryScope;
import org.junit.jupiter.api.Test;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@JiraKey("HHH-10371")
@SessionFactory
@DomainModel(annotatedClasses = { NullableBooleanUserTypeTest.Car.class })
public class NullableBooleanUserTypeTest {

	@Test
	public void testFalse(SessionFactoryScope scope) {
		scope.inTransaction(
				session -> {
					Car car = new Car();

					car.setId( 1L );
					car.setName( "TestCar" );
					car.setMainCar( Boolean.FALSE );
					car.setMainCars( Boolean.FALSE );

					session.persist( car );
				}
		);
		scope.inSession(
				session -> {
					final Car entity = session.find( Car.class, 1L );
					assertEquals( "TestCar", entity.name );
					assertFalse( entity.mainCar );
					assertFalse( entity.mainCars );
				}
		);
	}

	@Test
	public void testTrue(SessionFactoryScope scope) {
		scope.inTransaction(
				session -> {
					Car car = new Car();

					car.setId( 2L );
					car.setName( "MainCar" );
					car.setMainCar( Boolean.TRUE );
					car.setMainCars( Boolean.TRUE );

					session.persist( car );
				}
		);
		scope.inSession(
				session -> {
					final Car entity = session.find( Car.class, 2L );
					assertEquals( "MainCar", entity.name );
					assertTrue( entity.mainCar );
					assertTrue( entity.mainCars );
				}
		);
	}

	@Entity
	@Table(name = "car")
	public static class Car implements Serializable {

		@Id
		private Long id;

		private String name;

		@Type(BooleanLongUserType.class)
		@Column(columnDefinition = "NUMERIC(38,0)")
		private Boolean mainCar;

		@Type(BooleanStringUserType.class)
		@Column(columnDefinition = "VARCHAR(38)")
		private Boolean mainCars;

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Boolean getMainCar() {
			return mainCar;
		}

		public void setMainCar(Boolean mainCar) {
			this.mainCar = mainCar;
		}

		public Boolean getMainCars() {
			return mainCars;
		}

		public void setMainCars(Boolean mainCars) {
			this.mainCars = mainCars;
		}
	}
}
