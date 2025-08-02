package org.acme;

import java.util.List;
import java.util.Objects;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.dialect.PostgreSQLDialect;
import org.hibernate.type.SqlTypes;

import org.hibernate.testing.orm.junit.DomainModel;
import org.hibernate.testing.orm.junit.JiraKey;
import org.hibernate.testing.orm.junit.RequiresDialect;
import org.hibernate.testing.orm.junit.ServiceRegistry;
import org.hibernate.testing.orm.junit.SessionFactory;
import org.hibernate.testing.orm.junit.SessionFactoryScope;
import org.hibernate.testing.orm.junit.Setting;
import org.junit.jupiter.api.Test;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DomainModel(annotatedClasses = {
		MyEntityTest.MyEntity.class,
		MyEntityTest.MyJson.class
})
@SessionFactory
@ServiceRegistry(
		settings = {
				@Setting(name = AvailableSettings.JAKARTA_HBM2DDL_CREATE_SCHEMAS, value = "true")
		}
)
@RequiresDialect(PostgreSQLDialect.class)
@JiraKey("HHH-16682")
public class MyEntityTest {

	static private final String CHANGED = "CHANGED";

	@Test
	@JiraKey("HHH-16682")
	public void shouldCreateUpdateAndSelectMyEntity(SessionFactoryScope sessionFactoryScope) {
		final Long pk = sessionFactoryScope.fromTransaction(
				session -> {
					MyEntity myEntity = new MyEntity();
					MyJson myJson = new MyJson();
					myJson.setLongProp( 100L );
					myJson.setStringProp( "Hello" );
					myEntity.setJsonProperty( myJson );
					session.persist( myEntity );
					return myEntity.id;
				}
		);

		sessionFactoryScope.inTransaction(
				session -> {
					MyEntity found = session.find( MyEntity.class, pk );
					found.getJsonProperty().setStringProp( CHANGED );
					// found.setInfo(CHANGED); // by changing any other property of the entity, it will be marked as dirty and EVERY change will be written to the DB
				}
		);

		sessionFactoryScope.inSession(
				session -> {
					List<MyEntity> result =
							session.createQuery(
											"SELECT e FROM MyEntity e WHERE e.jsonProperty.longProp = :x",
											MyEntity.class
									)
									.setParameter( "x", 100L )
									.getResultList();
					assertEquals( 1, result.size() );
					assertEquals(
							CHANGED,
							result.get( 0 ).getJsonProperty().getStringProp(),
							"json property not changed"
					);
					//assertEquals( CHANGED, result.get( 0 ).getInfo(), "plain property not changed" );
				}
		);
	}

	@Entity(name = "MyEntity")
	public static class MyEntity {

		@Id
		@GeneratedValue
		private Long id;

		@JdbcTypeCode(SqlTypes.JSON)
		private MyJson jsonProperty;

		private String info;

		//<editor-fold defaultstate="collapsed" desc="getter & setter">
		public Long getId() {
			return id;
		}

		public void setId(Long aId) {
			this.id = aId;
		}

		public MyJson getJsonProperty() {
			return jsonProperty;
		}

		public void setJsonProperty(MyJson aJsonProperty) {
			this.jsonProperty = aJsonProperty;
		}

		public String getInfo() {
			return info;
		}

		public void setInfo(String aInfo) {
			this.info = aInfo;
		}
		//</editor-fold>

		//<editor-fold defaultstate="collapsed" desc="hashCode & equals">
		@Override
		public int hashCode() {
			int hash = 7;
			hash = 89 * hash + Objects.hashCode( this.id );
			hash = 89 * hash + Objects.hashCode( this.jsonProperty );
			hash = 89 * hash + Objects.hashCode( this.info );
			return hash;
		}

		@Override
		public boolean equals(Object obj) {
			if ( this == obj ) {
				return true;
			}
			if ( obj == null ) {
				return false;
			}
			if ( getClass() != obj.getClass() ) {
				return false;
			}
			final MyEntity other = (MyEntity) obj;
			if ( !Objects.equals( this.info, other.info ) ) {
				return false;
			}
			if ( !Objects.equals( this.id, other.id ) ) {
				return false;
			}
			return Objects.equals( this.jsonProperty, other.jsonProperty );
		}
		//</editor-fold>
	}

	@Embeddable
	public static class MyJson {

		private String stringProp;
		private Long longProp;

		public String getStringProp() {
			return stringProp;
		}

		public void setStringProp(String aStringProp) {
			this.stringProp = aStringProp;
		}

		public Long getLongProp() {
			return longProp;
		}

		public void setLongProp(Long aLongProp) {
			this.longProp = aLongProp;
		}

		@Override
		public int hashCode() {
			int hash = 3;
			hash = 97 * hash + Objects.hashCode( this.stringProp );
			hash = 97 * hash + Objects.hashCode( this.longProp );
			return hash;
		}

		@Override
		public boolean equals(Object obj) {
			if ( this == obj ) {
				return true;
			}
			if ( obj == null ) {
				return false;
			}
			if ( getClass() != obj.getClass() ) {
				return false;
			}
			final MyJson other = (MyJson) obj;
			if ( !Objects.equals( this.stringProp, other.stringProp ) ) {
				return false;
			}
			return Objects.equals( this.longProp, other.longProp );
		}


	}
}
