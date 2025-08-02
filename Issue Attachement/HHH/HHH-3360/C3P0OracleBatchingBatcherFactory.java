package org.hibernate.jdbc;

import org.hibernate.Interceptor;

public class C3P0OracleBatchingBatcherFactory implements BatcherFactory {

	public Batcher createBatcher(ConnectionManager connectionManager,
			Interceptor interceptor) {

		return new C3P0OracleBatchingBatcher(connectionManager, interceptor);
	}
}
