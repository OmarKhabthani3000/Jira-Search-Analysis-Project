package org.hibernate.jdbc;

import java.lang.reflect.Field;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import oracle.jdbc.OraclePreparedStatement;

import org.hibernate.HibernateException;
import org.hibernate.Interceptor;
import org.hibernate.StaleStateException;
import org.hibernate.util.JDBCExceptionReporter;

public class OracleBatchingBatcher extends AbstractBatcher {

	private int batchSize;

	private Expectation[] expectations;

	public OracleBatchingBatcher(ConnectionManager connectionManager,
			Interceptor interceptor) {

		super(connectionManager, interceptor);
		this.expectations = new Expectation[getFactory().getSettings()
				.getJdbcBatchSize()];
	}

	public PreparedStatement prepareBatchStatement(String sql)
			throws SQLException, HibernateException {

		// Obtain statement and set Oracle JDBC proprietary batch size
		PreparedStatement ps = super.prepareBatchStatement(sql);
		setExecuteBatch(ps);

		return ps;
	}

	public CallableStatement prepareBatchCallableStatement(String sql)
			throws SQLException, HibernateException {

		// This is ignored by the current Oracle JDBC implementation
		CallableStatement ps = super.prepareBatchCallableStatement(sql);
		setExecuteBatch(ps);

		return ps;
	}

	public void abortBatch(SQLException sqle) {

		// We must explicitly clear the batch,
		// otherwise a commit attempt might result
		PreparedStatement ps = getStatement();
		if (ps != null) {
			try {
				ps.clearBatch();
			} catch (SQLException e) {
				// Non-critical, swallow and let the other propagate
				JDBCExceptionReporter.logExceptions(e);
			}
		}

		// Close statement
		super.abortBatch(sqle);
	}

	public void addToBatch(Expectation expectation) throws SQLException,
			HibernateException {

		if (!expectation.canBeBatched()) {
			throw new HibernateException(
					"Attempting to batch an operation which cannot be batched");
		}

		PreparedStatement ps = getStatement();
		ps.executeUpdate(); // This always returns 0 while in a batch
		expectations[batchSize++] = expectation;
		if (batchSize == getFactory().getSettings().getJdbcBatchSize()) {
			doExecuteBatch(ps);
		}
	}

	protected void setExecuteBatch(PreparedStatement ps) throws SQLException {
		((OraclePreparedStatement) ps).setExecuteBatch(getFactory()
				.getSettings().getJdbcBatchSize());
	}

	protected int sendBatch(PreparedStatement ps) throws SQLException {
		return ((OraclePreparedStatement) ps).sendBatch();
	}

	protected void doExecuteBatch(PreparedStatement ps) throws SQLException,
			HibernateException {

		if (batchSize == 0) {
			log.debug("No batched statements to execute");
		} else {

			if (log.isDebugEnabled()) {
				log.debug("Executing batch size: " + batchSize);
			}

			try {
				checkRowCount(sendBatch(ps), ps);
			} catch (RuntimeException e) {
				log.error("Exception executing batch: ", e);
				throw e;
			} finally {
				batchSize = 0;
			}
		}
	}

	private void checkRowCount(int totalRowCount, PreparedStatement ps)
			throws SQLException, HibernateException {

		int expectedRowCount = 0;
		for (int i = 0; i < batchSize; i++) {
			Expectation expectation = expectations[i];
			if (expectation instanceof BasicExpectation) {
				try {
					Field field = BasicExpectation.class
							.getDeclaredField("expectedRowCount");
					field.setAccessible(true);
					int count = field.getInt(expectation);
					expectedRowCount += count;
				} catch (Exception e) {
					throw new HibernateException(
							"Error accessing private field", e);
				}
			} else if (expectation == Expectations.NONE) {
				if (log.isDebugEnabled()) {
					log.debug("Success of batch update cannot be verified");
				}
			} else {
				throw new HibernateException("Unknown expectation type");
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
}
