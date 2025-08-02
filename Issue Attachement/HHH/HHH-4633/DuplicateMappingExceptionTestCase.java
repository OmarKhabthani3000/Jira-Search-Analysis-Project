package duplicatemappingbug;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;

public class DuplicateMappingExceptionTestCase {
	static {
		// Setup log4j for the console (system.out)
		ConsoleAppender consoleAppender = new ConsoleAppender();
		consoleAppender.setLayout(new PatternLayout("%d [%t] %5p (%F:%L) %m%n"));
		consoleAppender.setName("consoleAppender");
		consoleAppender.setTarget(ConsoleAppender.SYSTEM_OUT);
		consoleAppender.activateOptions();
		Logger.getRootLogger().addAppender(consoleAppender);
		Logger.getRootLogger().setLevel(Level.DEBUG);
	}
	private static final Logger logger = Logger.getLogger(DuplicateMappingExceptionTestCase.class);
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		logger.error("Starting Test by creating configuration.");
		try{
			SessionFactory sessionFactory = new AnnotationConfiguration().configure().buildSessionFactory();
		}catch (Exception e) {
			logger.error("Unable to create proper configuration.",e);
		}
	}
}
