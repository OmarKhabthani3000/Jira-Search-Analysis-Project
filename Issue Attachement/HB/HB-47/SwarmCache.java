package net.sf.hibernate.cache;

import net.sf.swarmcache.*;
import java.io.Serializable;
import net.sf.hibernate.cache.Cache;
import net.sf.hibernate.cache.CacheException;

/**
 * Cache implementation for SwarmCache.
 *
 * @author John Watkinson (modified for Hibernate 2.1 by Fernando Martins)
 */
public class SwarmCache implements Cache {

    private ObjectCache cache = null;

    public SwarmCache(ObjectCache cache) {
        this.cache = cache;
    }

    public Object get(Object key) throws CacheException {
        return cache.get((Serializable)key);
    }

    public void put(Object oKey, Object value) throws CacheException {
        Serializable key = (Serializable)oKey;
        if (value == null) {
            // It's actually a flush
            cache.clear(key);
        } else {
            if (cache.get(key) != null) {
                // Indicate a re-cache
                cache.clear(key);
            }
            cache.put(key, value);
        }
    }

    public void setTimeout(int timeout) {
        // Not needed by SwarmCache
    }

	public void clear() throws CacheException {
		cache.clearAll();
	}

	public void destroy() throws CacheException {
		cache.clearAll();

	}

	public void remove(Object key) throws CacheException {
		cache.clear( (Serializable)key);
	}

    public void lock(Object o) throws CacheException {
    }

    public void unlock(Object o) throws CacheException {
    }

    public long nextTimestamp() {
        return 0;
    }

    public int getTimeout() {
        return 0;
    }
}