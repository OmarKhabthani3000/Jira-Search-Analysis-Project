
import oracle.jdbc.pool.OracleDataSource;
import org.apache.tomcat.jdbc.pool.PoolConfiguration;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.hibernate.MultiTenancyStrategy;
import org.hibernate.Session;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.service.ServiceRegistryBuilder;
import org.hibernate.service.spi.ServiceRegistryImplementor;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ShahAmit on 22-04-2015.
 */
public class SchemaBasedMultiTenancyTest {

    private ServiceRegistryImplementor serviceRegistry;
    private SessionFactoryImplementor sessionFactory;
    private TestCurrentTenantIdentifierResolver currentTenantIdentifierResolver;

    @Before
    public void setUp() {
        Configuration config = new Configuration();
        config.getProperties().put(AvailableSettings.DIALECT, "org.hibernate.dialect.Oracle10gDialect");
        config.getProperties().put(AvailableSettings.SHOW_SQL, "false");
        config.getProperties().put(AvailableSettings.FORMAT_SQL, "true");
        config.getProperties().put(AvailableSettings.HBM2DDL_AUTO, "validate");
        config.getProperties().put(AvailableSettings.DEFAULT_SCHEMA, "FEED");
        config.getProperties().put(AvailableSettings.STATEMENT_BATCH_SIZE, "3000");
        config.getProperties().put(AvailableSettings.USE_SECOND_LEVEL_CACHE, "false");
        config.getProperties().put(AvailableSettings.ORDER_UPDATES, "true");
        config.getProperties().put(AvailableSettings.ORDER_INSERTS, "true");
        config.getProperties().put(AvailableSettings.MAX_FETCH_DEPTH, "1");


        Map<String, DataSource> dataSources = new HashMap<String, DataSource>();
        DataSource dataSource1 = createDataSource("user1");
        DataSource dataSource2 = createDataSource("user2");

        dataSources.put("tenant1", dataSource1);
        dataSources.put("tenant2", dataSource2);

        TestMultiTenantConnectionProvider multiTenantConnectionProvider = new TestMultiTenantConnectionProvider(dataSources);
        TestCurrentTenantIdentifierResolver currentTenantIdentifierResolver = new TestCurrentTenantIdentifierResolver();
        //JPA annotated classes
        config.addPackage("com.mypackage");

        Map settings = new HashMap();
        settings.put(Environment.MULTI_TENANT, MultiTenancyStrategy.SCHEMA );
        settings.put(Environment.MULTI_TENANT_IDENTIFIER_RESOLVER, currentTenantIdentifierResolver);

        serviceRegistry = (ServiceRegistryImplementor) new StandardServiceRegistryBuilder()
                .applySettings(settings)
                .addService( MultiTenantConnectionProvider.class, multiTenantConnectionProvider )
                .build();

        sessionFactory = (SessionFactoryImplementor) config.buildSessionFactory( serviceRegistry );

        /**
         * Sequence of events
         * 1.
         * buildJdbcConnectionAccess - JdbcServicesImpl
         *
         if ( MultiTenancyStrategy.NONE == multiTenancyStrategy ) {
         connectionProvider = serviceRegistry.getService( ConnectionProvider.class );
         return new ConnectionProviderJdbcConnectionAccess( connectionProvider );
         }
         else {

         //HITS THIS BLOCK IN JdbcServicesImpl intializing service, connectionProvder is set to null
         connectionProvider = null;
         final MultiTenantConnectionProvider multiTenantConnectionProvider = serviceRegistry.getService( MultiTenantConnectionProvider.class );
         return new MultiTenantConnectionProviderJdbcConnectionAccess( multiTenantConnectionProvider );
         }


         2. SessionFactory Impl validates schema
         if ( settings.isAutoValidateSchema() ) {
         new SchemaValidator( serviceRegistry, cfg ).validate();
         }

         3. SuppliedConnectionProviderConnectionHelper constructor passed connection provider from JdbcServicesImpl which is null

         4. SuppliedConnectionProviderConnectionHelper prepare calls
         public void prepare(boolean needsAutoCommit) throws SQLException {
         connection = provider.getConnection(); THROWS NPE

         */

    }

    private DataSource createDataSource(String schemaName) {
        return buildDataSource(schemaName);
    }

    private DataSource buildDataSource(String schemaName) {
        org.apache.tomcat.jdbc.pool.DataSource tomcatDataSource =
                new org.apache.tomcat.jdbc.pool.DataSource(getPoolConfiguration(schemaName));
        try {
            tomcatDataSource.createPool();
        } catch (SQLException e) {
            throw new RuntimeException("Error while creating pool for schema named : " + schemaName, e);
        }
        return tomcatDataSource;
    }

    private PoolConfiguration getPoolConfiguration(String schemaName) {
        PoolConfiguration poolConfig = new PoolProperties();
        poolConfig.setName("POOL_" + schemaName);

        poolConfig.setDataSource(getOracleDataSource());
        poolConfig.setUsername(schemaName);
        poolConfig.setPassword("adept");

        poolConfig.setMaxIdle(10);
        poolConfig.setMaxActive(1);
        poolConfig.setMinIdle(1);
        poolConfig.setInitialSize(0);
        poolConfig.setMinEvictableIdleTimeMillis(180 * 1000);

        poolConfig.setTestWhileIdle(false);
        poolConfig.setTestOnBorrow(true);
        poolConfig.setFairQueue(false);//don't use fair queue since it's buggy http://tomcat.10.x6.nabble.com/Tomcat-connection-pool-quot-bleeding-quot-under-heavy-load-td5008052.html
        poolConfig.setValidationQuery("select 1 from dual");
        poolConfig.setJdbcInterceptors("ConnectionState;StatementFinalizer");
        return poolConfig;

    }

    private DataSource getOracleDataSource() {
        OracleDataSource oracleDataSource;
        try {
            oracleDataSource = new OracleDataSource();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        oracleDataSource.setDriverType("thin");
        oracleDataSource.setServerName("server-name");
        oracleDataSource.setPortNumber(1521);
        oracleDataSource.setServiceName("optymyze");
        return oracleDataSource;
    }


    protected Session getNewSession(String tenant) {
        return sessionFactory.withOptions().tenantIdentifier( tenant ).openSession();
    }

    @Test
    public void testTableBasedMultiTenancy() {
        //try getting a new session explicitly providing the tenant identifier
        Session session = getNewSession("tenant1");
        session.beginTransaction();
//        Content content = (Content)session.load(Content.class, 1234);
        System.out.println(session.createQuery("from Field").getFirstResult());

        session.getTransaction().commit();
        session.close();
    }

    public class TestMultiTenantConnectionProvider implements MultiTenantConnectionProvider {

        private static final long serialVersionUID = -403359210555925728L;

        private Map<String, DataSource> tenantDataSources;

        public TestMultiTenantConnectionProvider(Map<String, DataSource> tenantDataSources) {
            this.tenantDataSources = tenantDataSources;
        }

        @Override
        public boolean isUnwrappableAs(Class unwrapType) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public <T> T unwrap(Class<T> unwrapType) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Connection getAnyConnection() throws SQLException {
            return tenantDataSources.get("tenant1").getConnection();
        }

        @Override
        public void releaseAnyConnection(Connection connection) throws SQLException {
            connection.close();
        }

        @Override
        public Connection getConnection(String tenantIdentifier) throws SQLException {
            return tenantDataSources.get(tenantIdentifier).getConnection();
        }

        @Override
        public void releaseConnection(String tenantIdentifier, Connection connection) throws SQLException {
            connection.close();
        }

        @Override
        public boolean supportsAggressiveRelease() {
            return false;
        }
    }

    public class TestCurrentTenantIdentifierResolver implements CurrentTenantIdentifierResolver {

        private String tenant;

        public void setCurrentTenant(String tenant) {
            this.tenant = tenant;
        }

        @Override
        public String resolveCurrentTenantIdentifier() {
            return tenant;
        }

        @Override
        public boolean validateExistingCurrentSessions() {
            return true;
        }

    }
}
