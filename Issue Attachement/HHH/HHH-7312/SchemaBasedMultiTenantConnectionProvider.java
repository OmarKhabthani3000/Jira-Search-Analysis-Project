package com.xoricon.persistence.bo.multitenancy.test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import org.hibernate.service.config.spi.ConfigurationService;
import org.hibernate.service.jdbc.connections.internal.C3P0ConnectionProvider;
import org.hibernate.service.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.hibernate.service.spi.ServiceRegistryAwareService;
import org.hibernate.service.spi.ServiceRegistryImplementor;

public class SchemaBasedMultiTenantConnectionProvider 
	implements MultiTenantConnectionProvider, ServiceRegistryAwareService {

	private C3P0ConnectionProvider connectionProvider = null;
	
	@Override
	public void injectServices(ServiceRegistryImplementor serviceRegistry) {
		Map lSettings = serviceRegistry.getService( ConfigurationService.class ).getSettings();
		connectionProvider = new C3P0ConnectionProvider();
		connectionProvider.injectServices(serviceRegistry);
		connectionProvider.configure(lSettings);
	}
	
	@Override
	public Connection getAnyConnection() throws SQLException {
		System.out.println("SchemaBasedMultiTenantConnectionProvider.getAnyConnection()");
		final Connection connection = connectionProvider.getConnection();
		return connection;
	}

	
	@Override
	public boolean isUnwrappableAs(Class unwrapType) {
		return false;
	}

	@Override
	public <T> T unwrap(Class<T> unwrapType) {
		return null;
	}

	@Override
	public void releaseAnyConnection(Connection connection) throws SQLException {
		
	}

	@Override
	public Connection getConnection(String tenantIdentifier)
			throws SQLException {
		return null;
	}

	@Override
	public void releaseConnection(String tenantIdentifier, Connection connection)
			throws SQLException {
	}

	@Override
	public boolean supportsAggressiveRelease() {
		return false;
	}

}
