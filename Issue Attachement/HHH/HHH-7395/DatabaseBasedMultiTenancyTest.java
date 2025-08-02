import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.hibernate.MultiTenancyStrategy;
import org.hibernate.Session;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.service.ServiceRegistryBuilder;
import org.hibernate.service.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.hibernate.service.spi.ServiceRegistryImplementor;
import org.junit.Before;
import org.junit.Test;

import com.mypackage.Content;


public class DatabaseBasedMultiTenancyTest {

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
		config.getProperties().put(AvailableSettings.USE_SECOND_LEVEL_CACHE, "true");
		config.getProperties().put(AvailableSettings.CACHE_REGION_FACTORY, "org.hibernate.cache.ehcache.EhCacheRegionFactory");
		config.getProperties().put(AvailableSettings.ORDER_UPDATES, "true");
		config.getProperties().put(AvailableSettings.ORDER_INSERTS, "true");
		config.getProperties().put(AvailableSettings.MAX_FETCH_DEPTH, "1");

		
		Map<String, DataSource> dataSources = new HashMap<String, DataSource>();			
		DataSource dataSource1 = createDataSource("jdbc:oracle:thin:@host1:1521:xe", "user", "pass");
		DataSource dataSource2 = createDataSource("jdbc:oracle:thin:@host2:1521:xe", "user", "pass");

		dataSources.put("tenant1", dataSource1);
		dataSources.put("tenant2", dataSource2);
		
		TestMultiTenantConnectionProvider multiTenantConnectionProvider = new TestMultiTenantConnectionProvider(dataSources);
		TestCurrentTenantIdentifierResolver currentTenantIdentifierResolver = new TestCurrentTenantIdentifierResolver();
		config.getProperties().put(AvailableSettings.MULTI_TENANT_CONNECTION_PROVIDER, multiTenantConnectionProvider);
		config.getProperties().put(AvailableSettings.MULTI_TENANT, MultiTenancyStrategy.DATABASE);
		config.getProperties().put(AvailableSettings.MULTI_TENANT_IDENTIFIER_RESOLVER, currentTenantIdentifierResolver);
		//JPA annotated classes
		config.addPackage("com.mypackage");
		
		serviceRegistry = (ServiceRegistryImplementor) new ServiceRegistryBuilder().applySettings( config.getProperties() )
		.addService( MultiTenantConnectionProvider.class, multiTenantConnectionProvider )
		.buildServiceRegistry();

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
	
	private DataSource createDataSource(String url, String userName, String password) {
		final String driver = "oracle.jdbc.driver.OracleDriver";
		final String validationQuery = "SELECT 1 FROM DUAL";

		final int minIdle = 3;
		final int maxIdle = 3;
		final int maxActive = 10;
		final long maxWait = 6000;
		final boolean removeAbandoned = true;
		final boolean logAbandoned = true;
		final boolean testOnBorrow = true;
		final boolean testOnReturn = false;
		final boolean testWhileIdle = false;
		final long timeBetweenEvictionRunsMillis = 30000;
		final long minEvictableIdleTimeMillis = 30000;

		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setDriverClassName(driver);		
		dataSource.setUsername(userName);
		dataSource.setPassword(password);		
		dataSource.setValidationQuery(validationQuery);		
		dataSource.setUrl(url);
		dataSource.setMaxIdle(minIdle);
		dataSource.setMaxIdle(maxIdle);
		dataSource.setMaxActive(maxActive);
		dataSource.setMaxWait(maxWait);
		dataSource.setRemoveAbandoned(removeAbandoned);
		dataSource.setLogAbandoned(logAbandoned);
		dataSource.setTestOnBorrow(testOnBorrow);
		dataSource.setTestOnReturn(testOnReturn);
		dataSource.setTestWhileIdle(testWhileIdle);
		dataSource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
		dataSource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
		return dataSource;
	}
	
	protected Session getNewSession(String tenant) {
		return sessionFactory.withOptions().tenantIdentifier( tenant ).openSession();
	}
	
	@Test
	public void testTableBasedMultiTenancy() {
		//try getting a new session explicitly providing the tenant identifier
		Session session = getNewSession("tenant1");
		session.beginTransaction();
		Content content = (Content)session.load(Content.class, 1234);
		
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
