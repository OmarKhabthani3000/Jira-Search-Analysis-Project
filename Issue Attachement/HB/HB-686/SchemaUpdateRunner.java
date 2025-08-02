package net.sf.hibernate.jmx;

import javax.management.JMException;
import javax.management.MBeanServerFactory;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.cfg.Configuration;
import net.sf.hibernate.cfg.Environment;
import net.sf.hibernate.tool.hbm2ddl.SchemaUpdate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Runs schema update at deployment time.
 * Here's a sample JMX configuration: <pre>
 *
 * &lt;?xml version="1.0" encoding="UTF-8"?&gt;
 * &lt;server&gt;
 *   &lt;mbean code="net.sf.hibernate.jmx.SchemaUpdateRunner"
 *       name="harvester:service=SchemaUpdateRunner"&gt;
 *     &lt;depends optional-attribute-name="HibernateService"&gt;hibernate:service=HibernateFactory&lt;/depends&gt;
 *   &lt;/mbean&gt;
 * &lt;/server&gt;  </pre>
 */
public class SchemaUpdateRunner implements SchemaUpdateRunnerMBean {
  
  private static final Log LOG = LogFactory.getLog(SchemaUpdateRunner.class);
  
  private ObjectName _hibernateService;
  
  /**
   * @jmx.managed-operation impact="ACTION"
   */
  public void start() {
    try {
      Configuration cfg = new Configuration();
      MBeanServer server = (MBeanServer) MBeanServerFactory.findMBeanServer(
				  null).iterator().next();
      cfg.setProperty(Environment.DATASOURCE, (String) server.getAttribute(
          _hibernateService, "Datasource"));
      cfg.setProperty(Environment.DIALECT, (String) server.getAttribute(
          _hibernateService, "Dialect"));
      String[] mappingFiles = ((String) server.getAttribute(_hibernateService,
          "MapResources")).split("(\\s|,)+");
      for (int i = 0; i < mappingFiles.length; i++) {
        cfg.addResource(mappingFiles[i], Thread.currentThread().getContextClassLoader());
      }
      new SchemaUpdate(cfg).execute(false, true);
    } catch (JMException e) {
			LOG.error("Couldn't start", e);
		} catch (HibernateException e) {
			LOG.error("Couldn't start", e);
		}
  }
  
  /**
   * @jmx.managed-attribute access="read-write"
   */
  public void setHibernateService(ObjectName service) {
    _hibernateService = service;
  }
  
}
