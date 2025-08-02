package com.intrasoft.persistence.hibernate.cache;

import net.sf.hibernate.cache.Cache;
import net.sf.hibernate.cache.CacheException;
import net.sf.hibernate.cache.CacheProvider;
import net.sf.hibernate.cache.Timestamper;

import java.util.Properties;

/**
 * @author <a href="mailto:m.bogaert@intrasoft.be">Mathias Bogaert</a>
 * @version $Revision$
 */
public class HibernateOSCacheProvider implements CacheProvider {
    /** Default cache refresh period, defaults to 4800. */
    public static final int DEFAULT_REFRESH_PERIOD = 4800;

    /** Default CRON expression, put cache to stale on Sundays. */
    public static final String DEFAULT_CRON = "* * * * Wednesday";

    public Cache buildCache(String string, Properties properties) throws CacheException {
        return new HibernateOSCache(DEFAULT_REFRESH_PERIOD, DEFAULT_CRON);
    }

    public long nextTimestamp() {
        return Timestamper.next();
    }
}