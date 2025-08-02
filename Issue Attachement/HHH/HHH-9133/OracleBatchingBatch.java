package com.renxo.cms.dao.hibernate;

import java.lang.reflect.Field;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import oracle.jdbc.OraclePreparedStatement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.StaleStateException;
import org.hibernate.engine.jdbc.batch.internal.AbstractBatchImpl;
import org.hibernate.engine.jdbc.batch.spi.Batch;
import org.hibernate.engine.jdbc.batch.spi.BatchKey;
import org.hibernate.engine.jdbc.spi.JdbcCoordinator;
import org.hibernate.jdbc.Expectation;
import org.hibernate.jdbc.Expectations;
import org.hibernate.jdbc.Expectations.BasicExpectation;
import org.hibernate.jdbc.TooManyRowsAffectedException;

/**
 * An Oracle-specific implementation of the {@link Batch} interface that allows
 * update batching for versioned data using the Oracle JDBC driver. This works
 * around deficiencies of the standard JDBC 2.0 batching implementation by the
 * Oracle JDBC driver by using Oracle proprietary batching instead.
 * <p>
 * Namely, the standard JDBC 2.0 implementation of the Oracle JDBC driver does
 * not return update counts for each statement in a batch. However, it is
 * possible to obtain the total row update count for the batch, and this alone
 * is enough to verify whether there were stale updates for versioned data.
 * <p>
 * <strong>Note</strong>: This class is no longer necessary when using 12.1 or
 * higher drivers with a 12c database instance. However, using any other driver
 * or database version still requires use of this class for proper batching of
 * versioned data.
 */
public class OracleBatchingBatch extends AbstractBatchImpl {

	private static final Log log = LogFactory.getLog(OracleBatchingBatch.class);

	// ----------------------------------------------------------------------
	// Static block
	// ----------------------------------------------------------------------

	private static final Field EXPECTED_ROW_COUNT_FIELD;

	static {
		try {
			EXPECTED_ROW_COUNT_FIELD = BasicExpectation.class
					.getDeclaredField("expectedRowCount");
			EXPECTED_ROW_COUNT_FIELD.setAccessible(true);
		} catch (Exception e) {
			throw new HibernateException("Reflection error while "
					+ "initializing OracleBatchingBatcher", e);
		}
	}

	// ----------------------------------------------------------------------
	// Protected fields
	// ----------------------------------------------------------------------

	/**
	 * The batch size.
	 */
	protected final int batchSize;

	// ----------------------------------------------------------------------
	// Private ields
	// ----------------------------------------------------------------------

	private PreparedStatement currentStatement;

	// Until HHH-5797 is fixed, there will only be one statement in a batch
	private int statementPosition;

	private int batchPosition;

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
	public OracleBatchingBatch(BatchKey key, JdbcCoordinator jdbcCoordinator,
			int batchSize) {

		super(key, jdbcCoordinator);
		if (!key.getExpectation().canBeBatched()) {
			throw new HibernateException(
					"Attempting to batch an operation which cannot be batched");
		}

		this.batchSize = batchSize;
		reset();
	}

	// ----------------------------------------------------------------------
	// Public methods
	// ----------------------------------------------------------------------

	/**
	 * Obtains a batch statement for the given SQL.
	 * 
	 * @param sql
	 *            the SQL string.
	 * @param callable
	 *            where to return a {@link CallableStatement}
	 * @return a {@link PreparedStatement}
	 */
	@Override
	public PreparedStatement getBatchStatement(String sql, boolean callable) {

		// Obtain statement and set Oracle JDBC proprietary batch size
		currentStatement = super.getBatchStatement(sql, callable);
		try {
			setExecuteBatchIfNecessary(currentStatement);
		} catch (SQLException e) {
			throw sqlExceptionHelper().convert(e,
					"Error while invoking setExecuteBatch()");
		}

		return currentStatement;
	}

	/**
	 * Aborts the batch.
	 */
	@Override
	public void abortBatch() {

		// We must explicitly clear the batch,
		// otherwise a commit attempt might result
		for (PreparedStatement ps : getStatements().values()) {
			try {
				ps.clearBatch();
			} catch (SQLException e) {
				// Non-critical, swallow
				log.error("Error while invoking clearBatch()", e);
			}
		}

		// Reset internal state
		reset();

		// Close statement
		super.abortBatch();
	}

	/**
	 * Indicates completion of the current part of the batch.
	 */
	@Override
	public void addToBatch() {

		if (currentStatement == null) {
			throw new HibernateException("Current statement cannot be null");
		}

		int count;
		try {
			// Always returns 0 while batching
			count = currentStatement.executeUpdate();
		} catch (SQLException e) {
			throw sqlExceptionHelper().convert(e,
					"Error while invoking executeUpdate()");
		}

		statementPosition++;
		if (statementPosition >= getKey().getBatchedStatementCount()) {
			batchPosition++;
			statementPosition = 0;
		}

		if (count > 0) {
			notifyObserversImplicitExecution();
			try {
				// Batch execution has actually already started, however
				// we cannot notify this any sooner in this scenario
				transactionContext().startBatchExecution();
				checkTotalRowCount(count);
			} finally {
				transactionContext().endBatchExecution();
				reset();
			}
		} else if (batchPosition == batchSize) {
			throw new StaleStateException(
					"No rows have been affected by batched statements, "
							+ "indicating optimistic locking failure");
		}
	}

	/**
	 * Releases internal state.
	 */
	@Override
	protected void releaseStatements() {
		reset();
		super.releaseStatements();
	}

	// ----------------------------------------------------------------------
	// Protected methods
	// ----------------------------------------------------------------------

	/**
	 * Gets the execute batch size for the underlying
	 * {@link OraclePreparedStatement}. The default implementation casts the
	 * prepared statement to the Oracle implementation and executes
	 * {@link OraclePreparedStatement#getExecuteBatch()}. This extension point
	 * allows other strategies, for instance when a connection pool is involved
	 * and the {@link OraclePreparedStatement} is not readily available.
	 * 
	 * @param ps
	 *            the prepared statement.
	 * @throws SQLException
	 *             on SQL errors.
	 * @return the batch size for the statement.
	 */
	@SuppressWarnings({ "deprecation", "javadoc" })
	protected int getExecuteBatch(PreparedStatement ps) {
		return ((OraclePreparedStatement) ps).getExecuteBatch();
	}

	/**
	 * Sets the execute batch size for the underlying
	 * {@link OraclePreparedStatement}. The default implementation casts the
	 * prepared statement to the Oracle implementation and executes
	 * {@link OraclePreparedStatement#setExecuteBatch(int)}. This extension
	 * point allows other strategies, for instance when a connection pool is
	 * involved and the {@link OraclePreparedStatement} is not readily
	 * available.
	 * 
	 * @param ps
	 *            the prepared statement.
	 * @param batchSize
	 *            the batch size.
	 * @throws SQLException
	 *             on SQL errors.
	 */
	@SuppressWarnings({ "deprecation", "javadoc" })
	protected void setExecuteBatch(PreparedStatement ps, int batchSize)
			throws SQLException {

		((OraclePreparedStatement) ps).setExecuteBatch(batchSize);
	}

	/**
	 * Forces batch execution for the underlying {@link OraclePreparedStatement}
	 * <p>
	 * The default implementation casts the prepared statement to the Oracle
	 * implementation and executes {@link OraclePreparedStatement#sendBatch()}.
	 * This extension point allows other strategies, for instance when a
	 * connection pool is involved and the {@link OraclePreparedStatement} is
	 * not readily available.
	 * 
	 * @param ps
	 *            the prepared statement.
	 * @return the number of updated rows.
	 * @throws SQLException
	 *             on SQL errors.
	 */
	@SuppressWarnings({ "deprecation", "javadoc" })
	protected int sendBatch(PreparedStatement ps) throws SQLException {
		return ((OraclePreparedStatement) ps).sendBatch();
	}

	/**
	 * Perform batch execution.
	 */
	@Override
	protected void doExecuteBatch() {

		if (batchPosition == 0) {
			log.debug("No batched statements to execute");
			return;
		}

		if (log.isDebugEnabled()) {
			log.debug("Executing batch size: " + batchPosition);
		}

		try {
			for (PreparedStatement ps : getStatements().values()) {
				try {
					int totalRowCount;
					try {
						transactionContext().startBatchExecution();
						totalRowCount = sendBatch(ps);
					} finally {
						transactionContext().endBatchExecution();
					}
					checkTotalRowCount(totalRowCount);
				} catch (SQLException e) {
					abortBatch();
					throw sqlExceptionHelper().convert(e,
							"Could not execute batch");
				}
			}
		} finally {
			reset();
		}
	}

	// ----------------------------------------------------------------------
	// Private methods
	// ----------------------------------------------------------------------

	private void setExecuteBatchIfNecessary(PreparedStatement ps)
			throws SQLException {

		int currentExecuteBatch = getExecuteBatch(ps);
		if (currentExecuteBatch != this.batchSize) {
			setExecuteBatch(ps, batchSize);
		}
	}

	private void checkTotalRowCount(int totalRowCount) {

		int batchedStatementCount = getKey().getBatchedStatementCount();
		Expectation expectation = getKey().getExpectation();

		int expectedRowCount = 0;
		for (int i = 0; i < batchedStatementCount; i++) {
			for (int j = 0; j < batchPosition; j++) {
				if (expectation instanceof BasicExpectation) {
					try {
						int count = EXPECTED_ROW_COUNT_FIELD
								.getInt(expectation);
						expectedRowCount += count;
					} catch (Exception e) {
						throw new HibernateException(
								"Error accessing private field", e);
					}
				} else if (expectation == Expectations.NONE) {
					if (log.isDebugEnabled()) {
						log.debug("Success of batch update cannot be verified");
					}
					return;
				} else {
					throw new HibernateException("Unknown expectation type");
				}
			}
		}

		if (expectedRowCount > totalRowCount) {
			throw new StaleStateException("Unexpected row count: "
					+ totalRowCount + "; expected: " + expectedRowCount);
		}

		if (expectedRowCount < totalRowCount) {
			String msg = "Unexpected row count: " + totalRowCount
					+ "; expected: " + expectedRowCount;
			throw new TooManyRowsAffectedException(msg, expectedRowCount,
					totalRowCount);
		}
	}

	private void reset() {
		this.currentStatement = null;
		this.statementPosition = 0;
		this.batchPosition = 0;
	}
}
