package org.hibernate.jdbc;

import org.hibernate.Interceptor;

public class OracleBatchingBatcherFactory implements BatcherFactory {

	public Batcher createBatcher(ConnectionManager connectionManager,
			Interceptor interceptor) {

		return new OracleBatchingBatcher(connectionManager, interceptor);
	}
}
