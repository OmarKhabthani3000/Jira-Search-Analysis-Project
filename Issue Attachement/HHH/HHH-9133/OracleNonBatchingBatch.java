package com.renxo.cms.dao.hibernate;

import org.hibernate.engine.jdbc.batch.internal.NonBatchingBatch;
import org.hibernate.engine.jdbc.batch.spi.BatchKey;
import org.hibernate.engine.jdbc.spi.JdbcCoordinator;

/**
 * Extends {@link NonBatchingBatch} in order to expose its protected
 * constructor.
 * 
 */
public class OracleNonBatchingBatch extends NonBatchingBatch {

	// ----------------------------------------------------------------------
	// Constructor
	// ----------------------------------------------------------------------

	/**
	 * Constructs a batch.
	 * 
	 * @param key
	 *            the batch key.
	 * @param jdbcCoordinator
	 *            the JDBC coordinator.
	 */
	public OracleNonBatchingBatch(BatchKey key, JdbcCoordinator jdbcCoordinator) {
		super(key, jdbcCoordinator);
	}
}
