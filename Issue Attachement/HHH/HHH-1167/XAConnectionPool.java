package ru.arptek.arpsite.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.enhydra.jdbc.pool.StandardXAPoolDataSource;
import org.objectweb.jotm.TransactionImpl;

import ru.arptek.arpsite.Configuration;
import ru.arptek.arpsite.data.JOTMTransactionFactory;
import ru.arptek.arpsite.utils.timers.Timers;
import ru.arptek.common.Cache;
import ru.arptek.common.DateUtils;
import ru.arptek.common.profile.Profile;

@Profile
public class XAConnectionPool extends AbstractConnectionPool implements Cache {

    private static final class DestroyCacheTask extends TimerTask {
        private final static Log log = LogFactory
                .getLog(DestroyCacheTask.class);

        private final ThreadConnectionCache cache;

        private final long marker;

        private DestroyCacheTask(ThreadConnectionCache cache, long marker) {
            this.cache = cache;
            this.marker = marker;
        }

        public void run() {
            try {
                cache.destroy(marker, true);
            } catch (SQLException exc) {
                log.warn(exc.getMessage(), exc);
            }
        }
    }

    private class ThreadConnectionCache {
        private volatile XADelegatedConnection connection;

        private final Log log = LogFactory.getLog(ThreadConnectionCache.class);

        public final AtomicLong marker = new AtomicLong(0);

        private volatile String storedXid;

        private final String threadName = Thread.currentThread().getName();

        public volatile TimerTask timerTask;

        public synchronized Connection borrow() throws SQLException {
            if (this.connection == null)
                return null;

            try {
                boolean allow = false;
                TransactionImpl transaction = ((TransactionImpl) JOTMTransactionFactory
                        .getTransactionManager().getTransaction());
                if (storedXid == null ? (transaction == null || transaction
                        .getXid() == null) : (transaction != null && storedXid
                        .equals(String.valueOf(transaction.getXid())))) {
                    allow = true;
                }

                Connection connection = allow ? this.connection : null;
                destroy(marker.get(), !allow);
                return connection;
            } catch (Exception exc) {
                log.warn(exc.toString(), exc);
                destroy(marker.get(), true);
                return null;
            }
        }

        public synchronized void destroy(long marker, boolean close)
                throws SQLException {
            if (this.marker.get() != marker) {
                // too late - already destroyed

                if (TRACE_CACHE && log.isTraceEnabled()) {
                    log.trace("Connection with marker " + marker + " of "
                            + threadName + "was destroyed before");
                }

                return;
            }

            if (TRACE_CACHE && log.isTraceEnabled()) {
                log.trace("Destroing connection with marker " + marker
                        + " in cache of " + threadName);
            }

            if (connection == null)
                throw new IllegalStateException();

            if (close) {
                connection.closeDelegate();
            }
            connection = null;
            storedXid = null;
            timerTask.cancel();

            // purge every 100 tasks
            long canselledTasks = XAConnectionPool.this.canselledTasks
                    .incrementAndGet();
            if (canselledTasks % 100 == 0) {
                timers.get(ROLE).purge();
            }
        }

        public synchronized void store(XADelegatedConnection connection)
                throws SQLException {

            if (this.connection != null) {
                destroy(marker.get(), true);

                if (TRACE_CACHE && log.isTraceEnabled()) {
                    log.trace("Old connection in cache of " + this.storedXid
                            + " with marker " + marker
                            + " will be destroyed in cache of " + threadName
                            + " before storing " + "another connection");
                }
            }

            long newMarker = this.marker.incrementAndGet();

            try {
                this.connection = connection;
                TransactionImpl transaction = ((TransactionImpl) JOTMTransactionFactory
                        .getTransactionManager().getTransaction());
                storedXid = transaction == null ? null : transaction.getXid()
                        .toString();

                if (TRACE_CACHE && log.isTraceEnabled()) {
                    log.trace("Connection of " + this.storedXid
                            + " stored in cache of " + threadName
                            + "with marker " + newMarker);
                }

                timerTask = new DestroyCacheTask(this, newMarker);
            } catch (Exception exc) {
                destroy(newMarker, true);
                log.warn(exc.getMessage(), exc);
            }

            try {
                timers.get(ROLE).schedule(timerTask,
                        STORE_CONNECTION_IN_THREAD_CACHE_MS);
            } catch (IllegalStateException exc) {
                // Timer already cancelled.
                destroy(newMarker, true);
            }
        }
    }

    private final class ThreadConnectionCacheContainer extends
            ThreadLocal<ThreadConnectionCache> {
        @Override
        protected ThreadConnectionCache initialValue() {
            return new ThreadConnectionCache();
        }
    }

    public static final String ENVPROPERTY_LOG_STACK_TRACE = "ru.arptek.arpsite.db.XAConnectionPool.logStackTrace";

    private final static long STORE_CONNECTION_IN_THREAD_CACHE_MS = DateUtils.MILLIS_IN_SECOND;

    private final static boolean TRACE_CACHE = false;

    private final AtomicLong canselledTasks = new AtomicLong(0);

    /**
     * Map from connection name (or alias) to XADataSource
     */
    Map<String, StandardXAPoolDataSource> dataSources = new HashMap<String, StandardXAPoolDataSource>();

    private final Log log = LogFactory.getLog(this.getClass());

    private final AtomicLong threadCacheHits = new AtomicLong(0);

    private final AtomicLong threadCacheMisses = new AtomicLong(0);

    private ThreadConnectionCacheContainer threadConnectionCacheContainer = new ThreadConnectionCacheContainer();

    @Resource
    private Timers timers;

    /**
     * @see ru.arptek.arpsite.db.ConnectionPool#borrowConnection(java.lang.String)
     */
    public Connection borrowConnection(String poolName) throws SQLException {
        return getConnectionI(poolName, this.dataSources);
    }

    // private KeyedObjectPool xaConnectionsPool;

    public void clear() {
        // NOOP
    }

    protected void closeDelegate(XADelegatedConnection delegatedSystemConnection)
            throws SQLException {
        threadConnectionCacheContainer.get().store(delegatedSystemConnection);
    }

    /**
     * @see org.apache.avalon.framework.activity.Disposable#dispose()
     */
    public void dispose() {
        for (StandardXAPoolDataSource dataSource : (new HashSet<StandardXAPoolDataSource>(
                dataSources.values()))) {
            try {
                dataSource.shutdown(false);
                while (dataSources.values().remove(dataSource)) {
                    // remove closed pool from collection.
                }
            } catch (Exception e) {
                log.warn("Can't properly disconnect: " + e, e);
            }
        }
    }

    public long getBuildCalls() {
        return threadCacheMisses.get();
    }

    public int getCacheSize() {
        return -1;
    }

    private Connection getConnectionI(String poolName,
            Map<String, StandardXAPoolDataSource> dataSources)
            throws SQLException {
        poolName = poolName == null ? DEFAULT_POOL_NAME : poolName;

        if (Configuration.DATABASE_SYSTEM.equals(poolName)) {
            Connection connection = threadConnectionCacheContainer.get()
                    .borrow();
            if (connection != null) {
                threadCacheHits.incrementAndGet();
                return connection;
            } else {
                threadCacheMisses.incrementAndGet();
            }
        }

        reconfigureLock.readLock().lock();
        try {
            try {
                DataSource dataSource = dataSources.get(poolName);
                if (dataSource == null)
                    throw new SQLException("Pool not defined: " + poolName);
                synchronized (dataSource) {
                    Connection connection = dataSource.getConnection();
                    if (Configuration.DATABASE_SYSTEM.equals(poolName)) {
                        return new XADelegatedConnection(this, connection);
                    } else {
                        return connection;
                    }
                }
            } catch (SQLException exc) {
                throw exc;
            } catch (Throwable exc) {
                SQLException sqlExc = new SQLException(exc.toString());
                sqlExc.initCause(exc);
                throw sqlExc;
            }
        } finally {
            reconfigureLock.readLock().unlock();
        }
    }

    public long getGetCalls() {
        return threadCacheHits.get() + threadCacheMisses.get();
    }

    public long getHits() {
        return threadCacheHits.get();
    }

    public int getHitsProcent() {
        long missings = threadCacheMisses.get();
        long total = threadCacheHits.get() + threadCacheMisses.get();
        if (total == 0)
            return 0;
        return (int) ((total - missings) * 100 / total);
    }

    @Override
    protected void initPoolsMap() throws ConfigurationException, SQLException {
        final XADataSourceFactory dataSourceFactory = new XADataSourceFactory();

        Set<String> connectionNames = new HashSet<String>();
        final Map<String, StandardXAPoolDataSource> dataSources = new HashMap<String, StandardXAPoolDataSource>();

        // final KeyedObjectPool xaConnectionsPool = new
        // GenericKeyedObjectPool();
        // final KeyedPoolableObjectFactory xaConnectionFactory = new
        // XAConnectionFactory(
        // xaConnectionsPool, dataSources);
        // xaConnectionsPool.setFactory(xaConnectionFactory);

        for (ConnectionConfiguration cc : connectionsConfiguratiuons.values()) {
            if (cc instanceof RealConnectionConfiguration) {
                RealConnectionConfiguration rcc = (RealConnectionConfiguration) cc;
                StandardXAPoolDataSource dbcp = dataSourceFactory
                        .buildDataSource(rcc);

                try {
                    dataSources.put(rcc.name, dbcp);

                    // connection check
                    if (!rcc.skipTest) {
                        getConnectionI(rcc.name, dataSources).close();
                        connectionNames.add(rcc.name);
                    }
                } catch (SQLException exc) {
                    if (rcc.required) {
                        throw exc;
                    } else {
                        log.warn("Can't check connection to " + rcc.url
                                + " due to " + exc.getMessage(), exc);
                    }
                } catch (RuntimeException exc) {
                    if (rcc.required) {
                        throw exc;
                    } else {
                        log.warn("Can't check connection to " + rcc.url
                                + " due to " + exc.getMessage(), exc);
                    }
                }
                log.info("Initialized connection to " + rcc.url + " as "
                        + cc.name);
            }
        }

        // Now start init aliases
        for (ConnectionConfiguration cc : connectionsConfiguratiuons.values()) {
            if (cc instanceof AliasConnectionConfiguration) {
                AliasConnectionConfiguration acc = (AliasConnectionConfiguration) cc;
                StandardXAPoolDataSource dbcp = dataSources.get(acc.aliasOf);
                if (dbcp != null) {
                    dataSources.put(acc.name, dbcp);

                    if (connectionNames.contains(acc.aliasOf)) {
                        connectionNames.add(acc.name);
                    }

                    log.info("Initialized connection '" + acc.name
                            + "' as alias of " + acc.aliasOf);
                } else {
                    log.error("Database connection '" + acc.aliasOf
                            + "' for alias connection '" + acc.name
                            + "' not found.");
                }
            }
        }

        this.connectionNames = connectionNames;
        this.dataSources = dataSources;
        // this.xaConnectionsPool = xaConnectionsPool;
    }

}