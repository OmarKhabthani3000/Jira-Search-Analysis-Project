package org.hibernate.cache;

import java.util.Properties;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.cfg.Environment;
import org.hibernate.util.StringHelper;
import org.jboss.mx.util.MBeanProxyExt;
import org.jboss.mx.util.MBeanServerLocator;

/**
 * Support for CacheProvider implementations which are backed by caches available as MBean.
 *
 * @author Steve Ebersole
 * @author Stephan Fudeus
 */
public abstract class AbstractMBeanBoundCacheProvider implements CacheProvider {

	private static final Log log = LogFactory.getLog( AbstractMBeanBoundCacheProvider.class );
	private Object cacheMBean;

	protected void prepare(Properties properties) {
		// Do nothing; subclasses may override.
	}

	protected void release() {
		// Do nothing; subclasses may override.
	}

	/**
	 * Callback to perform any necessary initialization of the underlying cache implementation during SessionFactory
	 * construction.
	 *
	 * @param properties current configuration settings.
	 */
	public final void start(Properties properties) throws CacheException {
		String mbeanNamespace = properties.getProperty( Environment.CACHE_NAMESPACE );
		if ( StringHelper.isEmpty( mbeanNamespace ) ) {
			throw new CacheException( "No MBean namespace specified for cache" );
		}
		cacheMBean = locateCache(mbeanNamespace);
		prepare( properties );
	}

	/**
	 * Callback to perform any necessary cleanup of the underlying cache
	 * implementation during SessionFactory.close().
	 */
	public final void stop() {
		release();
		cacheMBean = null;
	}

	private Object locateCache(String mbeanNamespace) {

            MBeanServer server=MBeanServerLocator.locate();
            try {
                return MBeanProxyExt.create(getMBeanClass(), mbeanNamespace, server);
            } catch (MalformedObjectNameException e) {
                String msg = "Unable to locate mbean [" + mbeanNamespace + "]";
                log.info( msg, e );
                throw new CacheException( msg );
            }
	}
	
	public Object getCacheMBean() {
		return cacheMBean;
	}
    
    protected abstract Class getMBeanClass();
}
