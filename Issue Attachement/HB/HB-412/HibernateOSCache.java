package com.intrasoft.persistence.hibernate.cache;

import com.opensymphony.oscache.base.NeedsRefreshException;
import com.opensymphony.oscache.general.GeneralCacheAdministrator;
import net.sf.hibernate.cache.Cache;
import net.sf.hibernate.cache.CacheException;
import net.sf.hibernate.cache.Timestamper;

/**
 * @author <a href="mailto:m.bogaert@intrasoft.be">Mathias Bogaert</a>
 * @version $Revision$
 */
public class HibernateOSCache implements Cache {
    /** The OSCache 2.0 cache administrator. */
    public static GeneralCacheAdministrator CACHE = new GeneralCacheAdministrator();

    private int refreshPeriod;
    private String cron;

    public HibernateOSCache(int refreshPeriod, String cron) {
        this.refreshPeriod = refreshPeriod;
        this.cron = cron;
    }

	public Object get(Object key) throws CacheException {
        try {
            return CACHE.getFromCache(String.valueOf(key), refreshPeriod, cron);
        }
        catch (NeedsRefreshException e) {
            return null;
        }
    }

	public void put(Object key, Object value) throws CacheException {
		CACHE.putInCache(String.valueOf(key), value);
	}

	public void remove(Object key) throws CacheException {
		CACHE.flushEntry(String.valueOf(key));
	}

	public void clear() throws CacheException {
		CACHE.flushAll();
	}

	public void destroy() throws CacheException {
        CACHE.destroy();
	}

	public void lock(Object key) throws CacheException {
		// local cache, so we use synchronization
	}

	public void unlock(Object key) throws CacheException {
		// local cache, so we use synchronization
	}

	public long nextTimestamp() {
		return Timestamper.next();
	}

	public int getTimeout() {
		return Timestamper.ONE_MS * 60000; //ie. 60 seconds
	}
}