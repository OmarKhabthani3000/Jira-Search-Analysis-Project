//$Id: C3P0ConnectionProvider.java,v 1.6 2003/02/22 06:42:07 oneovthafew Exp $
package net.sf.hibernate.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Properties;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.cfg.Environment;
import net.sf.hibernate.util.JDBCExceptionReporter;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.logicalcobwebs.proxool.ProxoolException;
import org.logicalcobwebs.proxool.ProxoolFacade;
import org.logicalcobwebs.proxool.configuration.JAXPConfigurator;
import org.logicalcobwebs.proxool.configuration.PropertyConfigurator;

/**
 * A connection provider that uses a  connection pool. Hibernate will use this by
 * default if the <tt>hibernate.proxool.*</tt> properties are set.
 * @see ConnectionProvider
 */
public class ProxoolConnectionProvider implements ConnectionProvider {

  private static final String PROXOOL_JDBC_STEM = "proxool.";
  
	private static final String HIBERNATE_ALIAS_STEM = PROXOOL_JDBC_STEM + "hibernate-";

	private static final Log log = LogFactory.getLog(ProxoolConnectionProvider.class);

	private static int aliasCount;

	private String proxoolAlias;
  
  private boolean existingPool;

	public Connection getConnection() throws SQLException {
		try {
			return DriverManager.getConnection(proxoolAlias);
		}
		catch (SQLException sqle) {
			JDBCExceptionReporter.logExceptions(sqle);
			throw sqle;
		}
	}

	public boolean isStatementCache() {
		// TODO: Proxool 0.x does not provide statement cache functionality, on the roadmap for 1.1
		return false;
	}

	public void closeConnection(Connection conn) throws SQLException {
		try {
			conn.close();
		}
		catch (SQLException sqle) {
			JDBCExceptionReporter.logExceptions(sqle);
			throw sqle;
		}
	}

	public void configure(Properties props) throws HibernateException {
    
    // Get the configurator files (if available)    
    String jaxpFile = props.getProperty(Environment.PROXOOL_JAXP_CONFIGURATOR);
    String propFile = props.getProperty(Environment.PROXOOL_PROPERTIES_CONFIGURATOR);
    String externalConfig = props.getProperty(Environment.PROXOOL_EXISTING_POOL);
     
    // Default the Proxool alias setting 
    proxoolAlias = props.getProperty(Environment.PROXOOL_POOL_ALIAS);
    
    // Configured outside of Hibernate (i.e. Servlet container, or Java Bean Container
    // already has Proxool pools running, and this provider is to just borrow one of these
    if (StringUtils.equalsIgnoreCase(externalConfig, "true")) {
      
      // Validate that an alias name was provided to determine which pool to use
      if (StringUtils.isEmpty(proxoolAlias)) {
        String msg = "Cannot configure Proxool Provider to use an existing in memory pool without the " + Environment.PROXOOL_POOL_ALIAS + " property set.";
        log.fatal(msg);
        throw new HibernateException(msg);
      }
      // Append the stem to the proxool pool alias
      proxoolAlias = PROXOOL_JDBC_STEM + proxoolAlias;      
      
      // Set the existing pool flag to true
      existingPool = true;
      
      log.info("Configuring Proxool Provider using existing pool in memory: " + proxoolAlias);
            
    // Configured using the JAXP Configurator 
    } else if (StringUtils.isNotEmpty(jaxpFile)) {
      
      log.info("Configuring Proxool Provider using JAXPConfigurator: " + jaxpFile);
      
      // Validate that an alias name was provided to determine which pool to use
      if (StringUtils.isEmpty(proxoolAlias)) {
        String msg = "Cannot configure Proxool Provider to use JAXP without the " + Environment.PROXOOL_POOL_ALIAS + " property set.";
        log.fatal(msg);
        throw new HibernateException(msg);
      }      
      
      try {        
				JAXPConfigurator.configure(jaxpFile, false);        
			} catch (ProxoolException e) {
        String msg = "Proxool Provider unable to load JAXP configurator file: " + jaxpFile;
        log.fatal(msg);
        throw new HibernateException(msg);
			}

      // Append the stem to the proxool pool alias
      proxoolAlias = PROXOOL_JDBC_STEM + proxoolAlias;      
      log.info("Configuring Proxool Provider to use pool alias: " + proxoolAlias);      
      
    // Configured using the Properties File Configurator      
    } else if (StringUtils.isNotEmpty(propFile)) {
      
      log.info("Configuring Proxool Provider using Properties File: " + propFile);
      
      // Validate that an alias name was provided to determine which pool to use
      if (StringUtils.isEmpty(proxoolAlias)) {
        String msg = "Cannot configure Proxool Provider to use Properties File without the " + Environment.PROXOOL_POOL_ALIAS + " property set.";
        log.fatal(msg);
        throw new HibernateException(msg);
      }      
            
			try {
				PropertyConfigurator.configure(props.getProperty(propFile));
			} catch (ProxoolException e) {
        String msg = "Proxool Provider unable to load load Property configurator file: " + propFile;
        log.fatal(msg);
        throw new HibernateException(msg);
			}

      // Append the stem to the proxool pool alias
      proxoolAlias = PROXOOL_JDBC_STEM + proxoolAlias;      
      log.info("Configuring Proxool Provider to use pool alias: " + proxoolAlias);       

    // "Manual" Hibernate property configuration      
    } else {
      
      log.info("Configuring Proxool Provider using Hibernate properties");
      
      // Get the JDBC specific propeties
      String jdbcDriverClass = props.getProperty(Environment.DRIVER);
      String jdbcUrl = props.getProperty(Environment.URL);      
        
      // Get the Proxool specific properites
      Properties proxoolProps = extractProxoolProperties(props);
      log.info("Proxool Provider properties: " + proxoolProps);      

      // Get the delgate JDBC Driver specific properties 
      Properties connectionProps = ConnectionProviderFactory.getConnectionProperties(props);
      log.info("Delegate JDBC Driver Connection properties: " + connectionProps);
        
      // Merge the JDBC conneciton properties with the Proxool properties
      Properties finalProps = mergeProperties(proxoolProps, connectionProps);
  
  		log.info("Proxool Provider using delegate JDBC Driver: " + jdbcDriverClass + " at URL: " + jdbcUrl);
  		
  
      // Test that the JDBC driver exists
  		if (jdbcDriverClass==null) {
  			log.warn("No JDBC Driver class was specified by property " + Environment.DRIVER);
  		}
  		else {
  			try {
  				// Attempt to find and register the delegate JDBC Driver
  				Class.forName(jdbcDriverClass);
  			}
  			catch (ClassNotFoundException cnfe) {
  				String msg = "Delegate JDBC Driver class not found: " + jdbcDriverClass;
  				log.fatal(msg);
  				throw new HibernateException(msg);
  			}
  		}
  
  		try {
  
        // Get a new pool alias for Proxool
        proxoolAlias = newAlias();
  
        // Create the magic URL!
        String connectionUrl = proxoolAlias + ":" + jdbcDriverClass + ":" + jdbcUrl;
        log.info("Proxool Provider Connection URL: " + connectionUrl);
  
        // Register using the ProxoolFacade object, could also use a
        // standard connection, but this seems more straight forward at this
        // point
        ProxoolFacade.registerConnectionPool(connectionUrl, finalProps);
  
  		}
  		catch (Exception e) {
  			log.fatal("Could not instantiate Proxool Provider connection pool", e);
  			throw new HibernateException( "Could not instantiate Proxool connection pool", e );
   		}
    }
	}

	public void close() throws HibernateException {
    
    // If the provider was leeching off an existing pool don't close it
    if (existingPool) {
      return; 
    }
    
		try {

      // Kill all the connections (defaults gracefully) using
      // the ProxoolFacade
			ProxoolFacade.killAllConnections(proxoolAlias);

		} catch (Exception e) {
      // If you're closing down the ConnectionProvider chances are an
      // is not a real big deal, just warn
			log.warn("Exception occured when closing the Proxool pool", e);
			throw new HibernateException("Exception occured when closing the Proxool pool", e);
		}
	}

	// Creates a new alais and is thread safe.
	private synchronized static String newAlias() {

    // Increment the alias counter
		aliasCount++;

    // Build the alias using a stringbuffer, just easier to read and faster to execute
    StringBuffer buff = new StringBuffer(HIBERNATE_ALIAS_STEM);
		buff.append(aliasCount);
		return(buff.toString());
	}

  // Extract all properties that are "hibernate.proxool.XXX" and make a new
  // Properties object with them names "proxool.XXX" instead
	private static Properties extractProxoolProperties(Properties properties) {
    Iterator iter = properties.keySet().iterator();
    Properties result = new Properties();
    while ( iter.hasNext() ) {
      String prop = (String) iter.next();
      if ( prop.indexOf(Environment.PROXOOL_PREFIX) > -1) {
        result.setProperty(
          prop.substring( "hibernate".length()+1 ),
          properties.getProperty(prop)
        );
      }
    }
    return result;
	}

  // Merge two Properties objects, props2 will override props1
  // TODO: Perhaps move into PropertiesHelper, not my call
  private static Properties mergeProperties(Properties props1, Properties props2) {
    Iterator iter = props2.keySet().iterator();
    while ( iter.hasNext() ) {
      String prop = (String) iter.next();
      props1.setProperty(prop, props2.getProperty(prop));
    }

    return props1;
  }


}







