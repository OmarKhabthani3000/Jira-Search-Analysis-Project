/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package com.example.demo;

import java.io.Serializable;
import java.util.Optional;

import jakarta.persistence.*;

import org.hibernate.testing.orm.junit.DomainModel;
import org.hibernate.testing.orm.junit.JiraKey;
import org.hibernate.testing.orm.junit.SessionFactory;
import org.hibernate.testing.orm.junit.SessionFactoryScope;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Christoph Aigensberger
 */
@SessionFactory
@DomainModel(annotatedClasses = {
		CharArrayConverterTest.Vehicle.class
})
@JiraKey("HHH-16211")
public class CharArrayConverterTest {
	@BeforeAll
	public void setUp(SessionFactoryScope scope) {
		scope.inTransaction( session -> {
			Vehicle vehicle = new Vehicle();
			vehicle.setId( 1L );
			vehicle.setStringProp1( "TEST123456".toCharArray() );
			vehicle.setStringProp2( "TEST123456" );
			session.persist( vehicle );
		} );
	}

	@AfterAll
	public void tearDown(SessionFactoryScope scope) {
		scope.inTransaction( session -> {
			session.createMutationQuery( "delete from Vehicle" ).executeUpdate();
		} );
	}

	@Test
	public void testAssociationStringProp1(SessionFactoryScope scope) {
		scope.inTransaction( session -> {
			final Vehicle vehicleInvoice = session.createQuery(
					"from Vehicle where stringProp1 like :param escape '!' ",
					Vehicle.class
			).setParameter("param", "TEST%").getSingleResult();
			assertEquals( 1L, vehicleInvoice.getId() );
			assertArrayEquals( "TEST123456".toCharArray(), vehicleInvoice.getStringProp1() );
			assertEquals( "TEST123456", vehicleInvoice.getStringProp2() );
		} );
	}

	@Test
	public void testAssociationStringProp2(SessionFactoryScope scope) {
		scope.inTransaction( session -> {
			final Vehicle vehicleInvoice = session.createQuery(
					"from Vehicle where stringProp2 like :param escape '!' ",
					Vehicle.class
			).setParameter("param", "TEST%").getSingleResult();
			assertEquals( 1L, vehicleInvoice.getId() );
			assertArrayEquals( "TEST123456".toCharArray(), vehicleInvoice.getStringProp1() );
			assertEquals( "TEST123456", vehicleInvoice.getStringProp2() );
		} );
	}

	@Entity(name = "Vehicle")
	public static class Vehicle implements Serializable {
		@Id
		private Long id;

		@Column(name = "string_col_1", nullable = false)
		private char[] stringProp1;

		@Column(name = "string_col_2", nullable = false)
		@Convert(converter = StringToCharConverter.class)
		private String stringProp2;

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public char[] getStringProp1() {
			return stringProp1;
		}

		public void setStringProp1(char[] stringProp1) {
			this.stringProp1 = stringProp1;
		}

		public String getStringProp2() {
			return stringProp2;
		}

		public void setStringProp2(String stringProp2) {
			this.stringProp2 = stringProp2;
		}
	}

	@Converter
	public static class StringToCharConverter implements AttributeConverter<String, char[]> {

		@Override
		public char[] convertToDatabaseColumn(String attribute) {
			return Optional.ofNullable(attribute).map(String::toCharArray).orElse(null);
		}

		@Override
		public String convertToEntityAttribute(char[] dbData) {
			return Optional.ofNullable(dbData).map(String::new).orElse(null);
		}
	}
}