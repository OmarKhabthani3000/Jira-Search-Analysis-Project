package com.renxo.cms.dao.hibernate;

import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import oracle.jdbc.OraclePreparedStatement;

import org.hibernate.HibernateException;
import org.hibernate.engine.jdbc.batch.spi.BatchKey;
import org.hibernate.engine.jdbc.spi.JdbcCoordinator;

import com.mchange.v2.c3p0.C3P0ProxyStatement;

/**
 * Extends {@link OracleBatchingBatch} for use with C3P0 connection pooling.
 * 
 */
public class C3P0OracleBatchingBatch extends OracleBatchingBatch {

	// ----------------------------------------------------------------------
	// Static block
	// ----------------------------------------------------------------------

	private static final Method GET_EXECUTE_BATCH_METHOD;

	private static final Method SET_EXECUTE_BATCH_METHOD;

	private static final Method SEND_BATCH_METHOD;

	static {
		try {
			GET_EXECUTE_BATCH_METHOD = OraclePreparedStatement.class
					.getDeclaredMethod("getExecuteBatch");
			SET_EXECUTE_BATCH_METHOD = OraclePreparedStatement.class
					.getDeclaredMethod("setExecuteBatch", Integer.TYPE);
			SEND_BATCH_METHOD = OraclePreparedStatement.class
					.getDeclaredMethod("sendBatch");
		} catch (Exception e) {
			throw new HibernateException("Reflection error while "
					+ "initializing C3P0OracleBatchingBatcher", e);
		}
	}

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
	 * @param batchSize
	 *            the batch size.
	 */
	public C3P0OracleBatchingBatch(BatchKey key,
			JdbcCoordinator jdbcCoordinator, int batchSize) {

		super(key, jdbcCoordinator, batchSize);
	}

	// ----------------------------------------------------------------------
	// Overridden methods
	// ----------------------------------------------------------------------

	@Override
	protected int getExecuteBatch(PreparedStatement ps) {

		try {
			C3P0ProxyStatement stmt = (C3P0ProxyStatement) ps;
			return (Integer) stmt.rawStatementOperation(
					GET_EXECUTE_BATCH_METHOD, C3P0ProxyStatement.RAW_STATEMENT,
					new Object[] {});
		} catch (Exception e) {
			throw new HibernateException("Reflection error "
					+ "while attempting to get execution batch size", e);
		}
	}

	@Override
	protected void setExecuteBatch(PreparedStatement ps, int batchSize)
			throws SQLException {

		try {
			C3P0ProxyStatement stmt = (C3P0ProxyStatement) ps;
			stmt.rawStatementOperation(SET_EXECUTE_BATCH_METHOD,
					C3P0ProxyStatement.RAW_STATEMENT,
					new Object[] { this.batchSize });
		} catch (Exception e) {
			throw new HibernateException("Reflection error "
					+ "while attempting to set execution batch size", e);
		}
	}

	@Override
	protected int sendBatch(PreparedStatement ps) throws SQLException {
		try {
			C3P0ProxyStatement stmt = (C3P0ProxyStatement) ps;
			return (Integer) stmt.rawStatementOperation(SEND_BATCH_METHOD,
					C3P0ProxyStatement.RAW_STATEMENT, new Object[] {});
		} catch (Exception e) {
			throw new HibernateException("Reflection error "
					+ "while attempting to send batch", e);
		}
	}
}
