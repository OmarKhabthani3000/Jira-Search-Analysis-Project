package com.lawson.lbp.core.hibernate;

import java.util.Properties;

import org.hibernate.cache.Cache;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.CacheProvider;
import org.hibernate.cache.EhCacheProvider;

/**
 * This provider fixes a problem with overlapping initialized
 * {@link com.lawson.lbp.core.application.LBPApplication LBPApplications}.
 * The original {@link org.hibernate.cache.EhCacheProvider} calls 
 * {@link net.sf.ehcache.CacheManager#shutdown() shutdown} the first time 
 * {@link #stop()} it is called.
 * <br>
 * This implementation will count {@link #start(Properties) start} and only
 * call {@link net.sf.ehcache.CacheManager#shutdown() shutdown} when
 * the last {@link #stop()} is called.
 * 
 * 
 *
 */
public class LBPEHCacheProvider implements CacheProvider {

    private EhCacheProvider _provider;
    private static int      _count;
    
    public LBPEHCacheProvider() {
        _provider = new EhCacheProvider();
    }

    public Cache buildCache(String regionName, Properties properties)
        throws CacheException
    {
        return _provider.buildCache(regionName, properties);
    }
    public boolean equals(Object obj) {
        return _provider.equals(obj);
    }
    public int hashCode() {
        return _provider.hashCode();
    }
    public boolean isMinimalPutsEnabledByDefault() {
        return _provider.isMinimalPutsEnabledByDefault();
    }
    public long nextTimestamp() {
        return _provider.nextTimestamp();
    }
    public void start(Properties properties) throws CacheException {
        _provider.start(properties);
        synchronized (LBPEHCacheProvider.class) {
            _count++;
        }
    }
    public void stop() {
        synchronized (LBPEHCacheProvider.class) {
            _count--;
            if (_count == 0)
                _provider.stop();
        }
    }
    public String toString() {
        return _provider.toString();
    }
}
