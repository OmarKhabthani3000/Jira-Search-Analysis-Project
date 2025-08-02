package com.isolutions.inexus.service.performance.metric;

import org.hibernate.cache.CacheProvider;
import org.hibernate.cache.Cache;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.Timestamper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Properties;
import java.util.Map;
import java.util.HashMap;

/**
 * @author chris.brown
 */
public class TestCacheProvider implements CacheProvider {
    private static final Log log = LogFactory.getLog(TestCacheProvider.class);

    public Cache buildCache(final String regionName, Properties properties) throws CacheException {
        return new Cache() {
            public Object read(Object key) throws CacheException {
                return null;
            }

            public Object get(Object key) throws CacheException {
                return null;
            }

            public void put(Object key, Object value) throws CacheException {
                if (value != null) log.warn("put - should be disabled");
            }

            public void update(Object key, Object value) throws CacheException {
                if (value != null) log.warn("update - should be disabled");
            }

            public void remove(Object key) throws CacheException {
                log.debug("remove - ok");
            }

            public void clear() throws CacheException {
                log.debug("clear - ok");
            }

            public void destroy() throws CacheException {
            }

            public void lock(Object key) throws CacheException {
            }

            public void unlock(Object key) throws CacheException {
            }

            public long nextTimestamp() {
                return Timestamper.next();
            }

            public int getTimeout() {
                return Timestamper.ONE_MS * 60000;
            }

            public String getRegionName() {
                return regionName;
            }

            public long getSizeInMemory() {
                return 0;
            }

            public long getElementCountInMemory() {
                return 0;
            }

            public long getElementCountOnDisk() {
                return 0;
            }

            public Map toMap() {
                return new HashMap();
            }
        };
    }

    public long nextTimestamp() {
        return Timestamper.next();
    }

    public void start(Properties properties) throws CacheException {
    }

    public void stop() {
    }

    public boolean isMinimalPutsEnabledByDefault() {
        return false;
    }
}
