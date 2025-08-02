package org.hibernate.orm.test.query;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.orm.junit.DomainModel;
import org.hibernate.testing.orm.junit.SessionFactory;
import org.hibernate.testing.orm.junit.SessionFactoryScope;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;

@DomainModel(
		annotatedClasses = { FunTest.Person.class }
)
@SessionFactory
public class FunTest {

	@BeforeEach
	public void setUp(SessionFactoryScope scope) {
		scope.inTransaction(
				session -> {
					Person person = new Person( 77l, "fab" );
					session.persist( person );
				}
		);
	}

	@AfterEach
	public void tearDown(SessionFactoryScope scope) {
		scope.inTransaction(
				session -> {
					session.createQuery( "delete from Person" ).executeUpdate();
				}
		);
	}

	@Test
	public void funnySelect_ok(SessionFactoryScope scope) {
		scope.inTransaction(
				session -> {
					var result = session.createQuery("select distinct dp from Person as dp" +
							" where (dp in (" +
							"select distinct dataPointK from Person as dataPointK ))", Person.class).uniqueResult();
					Assertions.assertEquals("fab", result.name);
				}
		);
	}

	@Test
	public void funnySelect_0(SessionFactoryScope scope) {
		scope.inTransaction(
				session -> {
					var result = session.createQuery("select distinct dp from Person as dp" +
							" where (dp.id in (" +
							"select distinct dataPointK from Person as dataPointK ))", Person.class).uniqueResult();
					Assertions.assertEquals("fab", result.name);
				}
		);
	}



	@Test
	public void funnySelect_1(SessionFactoryScope scope) {
		scope.inTransaction(
				session -> {
					var result = session.createQuery( "select distinct dp from Person as dp" +
							" where (dp.id in (" +
							"select distinct dataPointK from Person as dataPointK where(" +
							" dataPointK IN(from Person) or dataPointK IN (from Person))))",Person.class).uniqueResult();
					Assertions.assertEquals("fab", result.name);
				}
		);
	}

	@Test
	public void funnySelect_2(SessionFactoryScope scope) {
		scope.inTransaction(
				session -> {
					var result = session.createQuery("select distinct dp from Person as dp" +
							" where (dp.id in (" +
							"(select distinct dataPointK from Person as dataPointK where(" +
							" dataPointK IN(from Person) or dataPointK IN (from Person)))))", Person.class).uniqueResult();
					Assertions.assertEquals("fab", result.name);
				}
		);
	}


	@Entity(name = "Person")
	@Table(name = "PERSON_TABLE")
	public static class Person {
		@Id
		private Long ident;

		private String name;

		public Person() {
		}

		public Person(Long ident, String name) {
			this.ident = ident;
			this.name = name;
		}

		public Long getIdent() {
			return ident;
		}

		public void setIdent(Long id) {
			this.ident = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}
}
