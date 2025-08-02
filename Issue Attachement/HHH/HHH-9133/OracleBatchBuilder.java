package com.renxo.cms.dao.hibernate;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.cfg.Environment;
import org.hibernate.engine.jdbc.batch.spi.Batch;
import org.hibernate.engine.jdbc.batch.spi.BatchBuilder;
import org.hibernate.engine.jdbc.batch.spi.BatchKey;
import org.hibernate.engine.jdbc.spi.JdbcCoordinator;
import org.hibernate.internal.util.config.ConfigurationHelper;
import org.hibernate.service.spi.Configurable;

/**
 * A {@link BatchBuilder} implementation that build {@link OracleBatchingBatch}
 * instances.
 * 
 */
public class OracleBatchBuilder implements BatchBuilder, Configurable {

	private static final long serialVersionUID = -4346540510493510659L;

	private static final Log log = LogFactory.getLog(OracleBatchBuilder.class);

	// ----------------------------------------------------------------------
	// Fields
	// ----------------------------------------------------------------------

	/**
	 * The batch size.
	 */
	protected int size;

	// ----------------------------------------------------------------------
	// Constructors
	// ----------------------------------------------------------------------

	/**
	 * Constructs a builder.
	 */
	public OracleBatchBuilder() {
		super();
	}

	/**
	 * Constructs a builder for the given batch size.
	 * 
	 * @param size
	 *            the batch size to use.
	 */
	public OracleBatchBuilder(int size) {
		this.size = size;
	}

	// ----------------------------------------------------------------------
	// Public methods
	// ----------------------------------------------------------------------

	/**
	 * Configures this instance with the given map.
	 * 
	 * @param configurationValues
	 *            the configuration map.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void configure(Map configurationValues) {
		this.size = ConfigurationHelper.getInt(
				Environment.STATEMENT_BATCH_SIZE, configurationValues,
				this.size);
	}

	/**
	 * Sets the JDBC batch size.
	 * 
	 * @param size
	 *            the JDBC batch size.
	 */
	public void setJdbcBatchSize(int size) {
		this.size = size;
	}

	/**
	 * Builds a batch.
	 * 
	 * @param key
	 *            the batch key.
	 * @param jdbcCoordinator
	 *            the JDBC coordinator.
	 * @return the new batch.
	 */
	@Override
	public Batch buildBatch(BatchKey key, JdbcCoordinator jdbcCoordinator) {
		log.trace("Building batch [size=" + this.size + "]");
		return (this.size > 1) ? new OracleBatchingBatch(key, jdbcCoordinator,
				this.size) : new OracleNonBatchingBatch(key, jdbcCoordinator);
	}

	/**
	 * Returns always <code>null</code>.
	 * 
	 * @return always <code>null</code>.
	 */
	@Override
	public String getManagementDomain() {
		// Use default domain
		return null;
	}

	/**
	 * Returns always <code>null</code>.
	 * 
	 * @return always <code>null</code>.
	 */
	@Override
	public String getManagementServiceType() {
		// Use default scheme
		return null;
	}

	/**
	 * Returns this instance.
	 * 
	 * @return this instance.
	 */
	@Override
	public Object getManagementBean() {
		return this;
	}
}
