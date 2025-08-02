package org.hibernate.cache;

import java.util.Properties;

import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ibm.websphere.cache.DistributedObjectCache;

/**
 * CacheProvider for use with IBM WebSphere's Dynamic Cache.
 * Since instances of the DistributedObjectCache are located using JNDI lookups,
 * a mapping is needed from Hibernate's cache regions to a JNDI name. This is
 * done through a NameResolver, for which a default implementation is provided.
 * For custom strategies, a different implementation can be registered.
 * <p/>
 * If the name obtained from the NameResolver cannot be found in the JNDI registry,
 * a default cache instance is searched. The default name of the default instance
 * is <code>services/cache/distributedmap</code>, since it's available by default 
 * in WebSphere. This mechanism allows you to configure specific caches per region 
 * and a generic catch-all instance for all regions for which no specific cache is 
 * provided.
 * <p/>
 * The following properties can be used to configure the provider:
 * <ul>
 * <li><code>cache.websphere.provider.defaultCacheName</code><br>
 * JNDI name of the cache instance used when no region-specific instance was found.
 * Defaults to <code>services/cache/distributedmap</code>
 * <li><code>cache.websphere.provider.resolver.className</code><br>
 * Class name of the <code>NameResolver</code> implementation to use instead
 * of the default
 * <li><code>cache.websphere.provider.defaultResolver.useLocalRefs</code><br>
 * Set to <code>true</code> to make the default NameResolver use local references
 * (i.e., prefixed by <code>java:comp/env</code>)
 * <li><code>cache.websphere.provider.defaultResolver.jndiPrefix</code><br>
 * Prefix that's prepended before each region name to form a JNDI name. Defaults to
 * <code>services/cache/</code>
 * </ul>
 * 
 * For more info on WebSphere's Dynamic Cache, see
 * <a href="http://publib.boulder.ibm.com/infocenter/wasinfo/v6r1/index.jsp?topic=/com.ibm.websphere.base.doc/info/aes/ae/tdyn_distmap.html">
 * this InfoCenter page</a>.
 * 
 * @author Joris Kuipers
 *
 */
public class WebSphereCacheProvider implements CacheProvider {
	
	public static final String DEFAULT_CACHE_JNDI_NAME_PROPNAME  = "cache.websphere.provider.defaultCacheName";
	public static final String NAME_RESOLVER_CLASS_NAME_PROPNAME = "cache.websphere.provider.resolver.className";
	public static final String USE_LOCAL_REFS_PROPNAME           = "cache.websphere.provider.defaultResolver.useLocalRefs";
	public static final String JNDI_PREFIX_PROPNAME              = "cache.websphere.provider.defaultResolver.jndiPrefix";
	
    private static final Log LOG = LogFactory.getLog(WebSphereCacheProvider.class);
    
    private InitialContext ic;
    private NameResolver nameResolver;
    private String defaultCacheJndiName = "services/cache/distributedmap";
    
	public Cache buildCache(String regionName, Properties properties) throws CacheException {
		String jndiName = nameResolver.getJndiNameForRegion(regionName);
		DistributedObjectCache objectCache;
		try {
			try {
				objectCache = (DistributedObjectCache) ic.lookup(jndiName);
			} catch (NameNotFoundException e) {
				LOG.warn("Could not find DistributedObjectCache with jndi name '" 
						+ jndiName + "' for region name '" + regionName
						+ "', falling back to default jndi name '"
						+ defaultCacheJndiName + "' instead");
				try {
					objectCache = (DistributedObjectCache) ic.lookup(defaultCacheJndiName);
				} catch (NameNotFoundException e2) {
					throw new CacheException("Could not find the default DistributedObjectCache: "
							+ "make sure WebSphere's dynamic cache service is enabled!");
				}
			}
			return new WebSphereCache(objectCache, regionName);
		} catch (NamingException e) {
			throw new CacheException(e);
		}
	}

	public boolean isMinimalPutsEnabledByDefault() {
		return false;
	}

	public long nextTimestamp() {
		return Timestamper.next();
	}

	public void start(Properties properties) throws CacheException {
		try {
			this.ic = new InitialContext();
		} catch (NamingException e) {
			throw new CacheException(e); 
		}
		String resolverClassName = properties.getProperty(NAME_RESOLVER_CLASS_NAME_PROPNAME);
		if (resolverClassName != null) {
			try {
				this.nameResolver = (NameResolver) Class.forName(resolverClassName, true, Thread.currentThread().getContextClassLoader()).newInstance();
			} catch (Exception e) {
				LOG.error("Exception creating custom nameResolver with classname '" + resolverClassName
						+ "', falling back to the default implementation", e);
			}
		}
		if (this.nameResolver == null) {
			DefaultNameResolver defaultNameResolver = new DefaultNameResolver();
			String prefix = properties.getProperty(JNDI_PREFIX_PROPNAME);
			if (prefix != null) {
				LOG.info("Üsing '" + prefix + "' as prefix for cache's JNDI names");
				defaultNameResolver.setDefaultJndiPrefix(prefix);
			}
			String localRefs = properties.getProperty(USE_LOCAL_REFS_PROPNAME);
			if (localRefs != null) {
				boolean useLocalRefs = Boolean.valueOf(localRefs).booleanValue();
				LOG.info((useLocalRefs ? "Using" : "Not using") + " local refs for JNDI lookups of caches");
				defaultNameResolver.setUseLocalRefs(useLocalRefs);
			}
			this.nameResolver = defaultNameResolver;
		}
		String jndiName = properties.getProperty(DEFAULT_CACHE_JNDI_NAME_PROPNAME);
		if (jndiName != null) {
			LOG.info("Using '" + jndiName + "' as JNDI name for the default cache");
			defaultCacheJndiName = jndiName;
		}
	}

	public void stop() {
		try {
			ic.close();
		} catch (NamingException e) {
			LOG.error("Error closing InitialContext on stop()", e);
		}
	}
	
	public static interface NameResolver {
		String getJndiNameForRegion(String regionName);
	}
	
	static class DefaultNameResolver implements NameResolver {
		private boolean useLocalRefs = false;
		private String defaultJndiPrefix = "services/cache/";
		
		public String getJndiNameForRegion(String regionName) {
			StringBuffer buff = new StringBuffer();
			if (useLocalRefs) {
				buff.append("java:comp/env");
				if (!defaultJndiPrefix.startsWith("/")) {
					buff.append("/");
				}
			}
			buff.append(defaultJndiPrefix);
			if (!defaultJndiPrefix.endsWith("/")) {
				buff.append('/');
			}
			buff.append(regionName);
			return buff.toString();
		}
		
		public void setUseLocalRefs(boolean useLocalRefs) {
			this.useLocalRefs = useLocalRefs;
		}
		
		public void setDefaultJndiPrefix(String prefix) {
			if (prefix != null) {
				this.defaultJndiPrefix = prefix;
			} else {
				this.defaultJndiPrefix = "";
			}
		}
	}

}