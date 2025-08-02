package net.sf.hibernate.cache;

import java.util.Properties;

import net.sf.hibernate.cache.CacheProvider;
import net.sf.hibernate.cache.CacheException;
import net.sf.hibernate.cache.Cache;
import net.sf.swarmcache.CacheConfiguration;
import net.sf.swarmcache.ObjectCache;

/**
 * SwarmCache plugin for Hibernate.
 * Use <tt>hibernate.cache.provider_class=net.sf.hibernate.cache.SwarmCacheProvider</tt>
 * in Hibernate 2.1 beta 2 or later.
 *
 * @author John Watkinson (modified for Hibernate 2.1 by Fernando Martins)
 */
public class SwarmCacheProvider implements CacheProvider {

    private static net.sf.swarmcache.CacheFactory cacheFactory;

    /**
     * Sets up SwarmCache to use an LRU caching algorithm locally.
     */
    static {
        // Configure cache
        CacheConfiguration conf = new CacheConfiguration();
        conf.setCacheType(CacheConfiguration.TYPE_LRU);
        conf.setLRUCacheSize("100000");
        cacheFactory = new net.sf.swarmcache.CacheFactory(conf);
    }

    public Cache buildCache(String regionName, Properties properties)
            throws CacheException {
        ObjectCache cache = cacheFactory.createCache(regionName);

        return new SwarmCache(cache);
    }


    public long nextTimestamp() {
        return 0;
    }
}
