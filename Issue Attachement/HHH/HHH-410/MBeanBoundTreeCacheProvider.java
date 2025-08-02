package org.hibernate.cache;

import java.util.Properties;

import javax.transaction.TransactionManager;

import org.hibernate.transaction.TransactionManagerLookup;
import org.hibernate.transaction.TransactionManagerLookupFactory;

/**
 * Support for JBossCache (TreeCache), where a cache instance is available
 * via MBean-lookup.
 *
 * @author Steve Ebersole
 * @author Stephan Fudeus
 */
public class MBeanBoundTreeCacheProvider extends AbstractMBeanBoundCacheProvider {

	private TransactionManager transactionManager;
    private org.jboss.cache.TreeCache treeCacheInstance; 
    
    private final Class mbeanClass = org.jboss.cache.TreeCacheMBean.class;

	/**
	 * Construct a Cache representing the "region" within in the underlying cache
	 * provider.
	 *
	 * @param regionName the name of the cache region
	 * @param properties configuration settings
	 *
	 * @throws CacheException
	 */
	public Cache buildCache(String regionName, Properties properties) throws CacheException {
		return new TreeCache( getTreeCacheInstance(), regionName, transactionManager );
	}

	public void prepare(Properties properties) throws CacheException {
		TransactionManagerLookup transactionManagerLookup = TransactionManagerLookupFactory.getTransactionManagerLookup(properties);
		if (transactionManagerLookup!=null) {
			transactionManager = transactionManagerLookup.getTransactionManager(properties);
		}
	}
	/**
	 * Generate a timestamp
	 */
	public long nextTimestamp() {
		return System.currentTimeMillis() / 100;
	}

	/**
	 * By default, should minimal-puts mode be enabled when using this cache.
	 * <p/>
	 * Since TreeCache is a clusterable cache and we are only getting a
	 * reference to the instance deployed as mbean, safest to assume a clustered
	 * setup and return true here.
	 *
	 * @return True.
	 */
	public boolean isMinimalPutsEnabledByDefault() {
		return true;
	}

	public org.jboss.cache.TreeCache getTreeCacheInstance() {
		return (( org.jboss.cache.TreeCacheMBean ) super.getCacheMBean()).getInstance();
	}
    
    protected Class getMBeanClass() {
        return mbeanClass;
    }
}
