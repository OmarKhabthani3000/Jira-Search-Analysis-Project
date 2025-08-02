package hibernatebugs;

import static org.easymock.EasyMock.*;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.apache.log4j.Appender;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.easymock.EasyMock;
import org.easymock.IArgumentMatcher;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.ImprovedNamingStrategy;
import org.junit.Test;

public class TestSingleTableInheritanceWarnings {

	@Test
	public void testInheritanceSingleTableWarning() throws Exception {
		BasicConfigurator.configure();
		Logger  logger = Logger.getLogger("org.hibernate");
		Logger  annBLogger = Logger.getLogger("org.hibernate.cfg.AnnotationBinder");
		
		Appender mockAppender = createMock(Appender.class);
		logger.addAppender(mockAppender);
		logger.setLevel(Level.WARN);
		
		mockAppender.doAppend(notLoggingEvent(new LoggingEvent("org.hibernate.cfg.AnnotationBinder",annBLogger,Level.WARN,"Illegal use of @Table in a subclass of a SINGLE_TABLE hierarchy: hibernatebugs.TestSingleTableInheritanceWarnings$ChildEntity",null)));
		expectLastCall().anyTimes();

		replay(mockAppender);

		new AnnotationConfiguration()
			.addAnnotatedClass(ParentEntity.class)
			.addAnnotatedClass(ChildEntity.class)
			.addResource("hibernatebugs/inheritance-orm.xml")
			.setProperty("hibernate.dialect","org.hibernate.dialect.PostgreSQLDialect")
			.setNamingStrategy(new ImprovedNamingStrategy())
			.buildSessionFactory();
		
		verify(mockAppender);
		
	}
	
	@Entity
	public class ParentEntity implements Serializable {
		private static final long serialVersionUID = 1L;
		
		private Integer id;

		@Id
		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}
		
	}
	
	@Entity
	public class ChildEntity extends ParentEntity {
		private static final long serialVersionUID = 1L;

		private String text;

		public String getText() {
			return text;
		}

		public void setText(String text) {
			this.text = text;
		}
	}
	
	public static LoggingEvent notLoggingEvent(LoggingEvent in) {
	    EasyMock.reportMatcher(new NotLoggerEventEquals(in));
	    return null;
	}
	
	public static class NotLoggerEventEquals implements IArgumentMatcher {
	    private LoggingEvent expected;

	    public NotLoggerEventEquals(LoggingEvent expected) {
	        this.expected = expected;
	    }

	    public boolean matches(Object actual) {
	        if (!(actual instanceof LoggingEvent)) {
	            return false;
	        }
	        
	        LoggingEvent castActual = (LoggingEvent) actual;
	        return !(castActual.getLoggerName().equals(expected.getLoggerName())
	        	&& castActual.getLevel().equals(expected.getLevel())
	        	&& castActual.getMessage().equals(expected.getMessage()));
	    }

	    public void appendTo(StringBuffer buffer) {
	        buffer.append("notLoggingEvent(");
	        buffer.append(expected.getLoggerName() + ",");
	        buffer.append(expected.getLevel() + ",");
	        buffer.append(expected.getMessage() + ",");
	        buffer.append("\")");

	    }
	}
}
