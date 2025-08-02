package test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import junit.framework.TestCase;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.classic.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.dialect.H2Dialect;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * Reproduces issue as mentioned in HHH-5169.
 * 
 * @author Daniël van 't Ooster
 */
public class HHH5169 extends TestCase {

	public void testWrongSQL() throws Exception {
		SessionFactory sessionFactory = new AnnotationConfiguration()
			.addAnnotatedClass(Foo.class)
			.addAnnotatedClass(Bar.class)
			.addAnnotatedClass(SomethingElse.class)
			.setProperty("hibernate.dialect", H2Dialect.class.getName())
			.buildSessionFactory();

		final PreparedStatement preparedStatement = Mockito.mock(PreparedStatement.class);
		final ResultSet resultSet = Mockito.mock(ResultSet.class);

		final Connection connection = Mockito.mock(Connection.class);
		Mockito.when(connection.prepareStatement(Mockito.anyString())).thenAnswer(new Answer<PreparedStatement>() {
			public PreparedStatement answer(InvocationOnMock invocation) throws Throwable {
				String sql = (String) invocation.getArguments()[0];
				if (sql.contains("NewBarId")) {
					throw new RuntimeException("Wrong column in select clause");
				} else {
					return preparedStatement;
				}
			}
		});
		Mockito.when(preparedStatement.executeQuery()).thenReturn(resultSet);

		Session session = sessionFactory.openSession(connection);

		session.createCriteria(Bar.class).setProjection(Projections.rowCount()).uniqueResult();
	}
	
	public void testWorkaround() throws Exception {
		SessionFactory sessionFactory = new AnnotationConfiguration()
			.addAnnotatedClass(Foo.class)
			.addAnnotatedClass(BarWithWorkaround.class)
			.addAnnotatedClass(SomethingElse.class)
			.setProperty("hibernate.dialect", H2Dialect.class.getName())
			.buildSessionFactory();

		final PreparedStatement preparedStatement = Mockito.mock(PreparedStatement.class);
		final ResultSet resultSet = Mockito.mock(ResultSet.class);

		final Connection connection = Mockito.mock(Connection.class);
		Mockito.when(connection.prepareStatement(Mockito.anyString())).thenAnswer(new Answer<PreparedStatement>() {
			public PreparedStatement answer(InvocationOnMock invocation) throws Throwable {
				String sql = (String) invocation.getArguments()[0];
				if (sql.contains("NewBarId")) {
					throw new RuntimeException("Wrong column in select clause");
				} else {
					return preparedStatement;
				}
			}
		});
		Mockito.when(preparedStatement.executeQuery()).thenReturn(resultSet);

		Session session = sessionFactory.openSession(connection);

		session.createCriteria(BarWithWorkaround.class).setProjection(Projections.rowCount()).uniqueResult();
	}
}


@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
class Bar {
	@Column(name = "BarId")
	@Id
	protected long id;
}

@Entity
@Table(name = "Foo")
class Foo extends Bar {

}

@Entity(name = "NewBar")
@Table(name = "Bar")
class SomethingElse {
	@Id
	@Column(name = "NewBarId")
	@GeneratedValue
	private Long id;
}

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(name="EnablingThisAnnotationIsAWorkaround")
class BarWithWorkaround {
	@Column(name = "BarId")
	@Id
	protected long id;
}

