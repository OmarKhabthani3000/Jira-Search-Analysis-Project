package net.sf.hibernate.ant;

import java.util.Map;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.SessionFactory;
import net.sf.hibernate.cfg.Configuration;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 * Ant Task to check a configuration and namedqueries for errors
 * 
 * @author Joris Verschoor
 * 
 */
public class HibernateTask extends Task {
	private String configuration = null;

	public String getConfiguration() {
		return configuration;
	}

	public void setConfiguration(String configuration) {
		this.configuration = configuration;
	}
	
	public void execute() throws BuildException {
		SessionFactory sf = null;
		try {
			Configuration cfg = new Configuration();
			if (configuration == null) {
				cfg.configure();
			}
			else {
				cfg.configure(configuration);
			}

			Map queries = cfg.getNamedQueries();

			// No need to build the sessionFactory if there are no named queries
			if (queries.isEmpty()) {
				log("No named queries found");
			}
			else {
				sf = cfg.buildSessionFactory();
			}
		}
		catch (HibernateException e) {
			throw new BuildException(e);
		}
		finally {
			if (sf != null) {
				try {
					sf.close();
				}
				catch (HibernateException e) {
				}
			}
		}
	}
}