package com.renxo.cms.dao.hibernate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.engine.jdbc.batch.spi.Batch;
import org.hibernate.engine.jdbc.batch.spi.BatchKey;
import org.hibernate.engine.jdbc.spi.JdbcCoordinator;

/**
 * Extends {@link OracleBatchBuilder} for use with C3P0 connection pooling.
 * 
 */
public class C3P0OracleBatchBuilder extends OracleBatchBuilder {

	private static final long serialVersionUID = -9022617986898717867L;

	private static final Log log = LogFactory
			.getLog(C3P0OracleBatchBuilder.class);

	// ----------------------------------------------------------------------
	// Overridden methods
	// ----------------------------------------------------------------------

	@Override
	public Batch buildBatch(BatchKey key, JdbcCoordinator jdbcCoordinator) {
		log.trace("Building batch [size=" + this.size + "]");
		return (this.size > 1) ? new C3P0OracleBatchingBatch(key,
				jdbcCoordinator, this.size) : new OracleNonBatchingBatch(key,
				jdbcCoordinator);
	}
}
