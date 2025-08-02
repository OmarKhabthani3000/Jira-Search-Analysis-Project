/**
 * 
 */
package org.hibernate.util;

import java.util.Iterator;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;

/**
 * @author mtedone
 *
 */
public class ConfigurationFactory {
	
	
	private ConfigurationFactory() {}
	
	
	
	public static final Configuration getStandardConfiguration(ExternalSessionFactoryConfig config) {
		
		Configuration cfg = new Configuration().setProperties( config.buildProperties() );

		String[] mappingFiles = PropertiesHelper.toStringArray( config.getMapResources(), " ,\n\t\r\f" );
		for ( int i = 0; i < mappingFiles.length; i++ ) {
			cfg.addResource( mappingFiles[i] );
		}

		setCommonConfigs(config, cfg);

		return cfg;
		
	}
	
	
	public static final Configuration getAnnotationConfiguration(ExternalSessionFactoryConfig config) {
		
		
		AnnotationConfiguration cfg = new AnnotationConfiguration();
		
		cfg.addProperties(config.buildProperties());

		String[] mappedClasses = PropertiesHelper.toStringArray( config.getMappedClasses(), " ,\n\t\r\f" );
		for ( int i = 0; i < mappedClasses.length; i++ ) {
			try {
				cfg.addAnnotatedClass(Class.forName(mappedClasses[i]));
			} catch (MappingException e) {
				throw new HibernateException(e);
			} catch (ClassNotFoundException e) {
				throw new HibernateException(e);
			}
		}

		setCommonConfigs(config, cfg);

		return cfg;
		
	}



	private static void setCommonConfigs(ExternalSessionFactoryConfig config,
			Configuration cfg) {
		if ( config.getCustomListeners() != null && !config.getCustomListeners().isEmpty() ) {
			Iterator entries = config.getCustomListeners().entrySet().iterator();
			while ( entries.hasNext() ) {
				final Map.Entry entry = ( Map.Entry ) entries.next();
				final String type = ( String ) entry.getKey();
				final Object value = entry.getValue();
				if ( value != null ) {
					if ( String.class.isAssignableFrom( value.getClass() ) ) {
						// Its the listener class name
						cfg.setListener( type, ( ( String ) value ) );
					}
					else {
						// Its the listener instance (or better be)
						cfg.setListener( type, value );
					}
				}
			}
		}
	}

}
