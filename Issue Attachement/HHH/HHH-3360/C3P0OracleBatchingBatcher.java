package org.hibernate.jdbc;

import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import oracle.jdbc.OraclePreparedStatement;

import org.hibernate.HibernateException;
import org.hibernate.Interceptor;
import org.hibernate.jdbc.ConnectionManager;

import com.mchange.v2.c3p0.C3P0ProxyStatement;

public class C3P0OracleBatchingBatcher extends OracleBatchingBatcher {

	private static final Method EXECUTE_BATCH_METHOD;

	private static final Method SEND_BATCH_METHOD;

	static {
		try {
			EXECUTE_BATCH_METHOD = OraclePreparedStatement.class
					.getDeclaredMethod("setExecuteBatch", Integer.TYPE);
			SEND_BATCH_METHOD = OraclePreparedStatement.class
					.getDeclaredMethod("sendBatch");
		} catch (Exception e) {
			throw new HibernateException("Reflection error while "
					+ "initializing C3P0OracleBatchingBatcher", e);
		}
	}

	public C3P0OracleBatchingBatcher(ConnectionManager connectionManager,
			Interceptor interceptor) {

		super(connectionManager, interceptor);
	}

	protected void setExecuteBatch(PreparedStatement ps) throws SQLException {
		try {
			C3P0ProxyStatement stmt = (C3P0ProxyStatement) ps;
			stmt.rawStatementOperation(EXECUTE_BATCH_METHOD,
					C3P0ProxyStatement.RAW_STATEMENT,
					new Object[] { getFactory().getSettings()
							.getJdbcBatchSize() });
		} catch (Exception e) {
			throw new HibernateException("Reflection error "
					+ "while attempting set execution batch size", e);
		}
	}

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
