package net.sf.hibernate.id;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.engine.SessionImplementor;
import net.sf.hibernate.util.JDBCExceptionReporter;

/**
 * Generates <tt>string</tt> values using the SQL Server NEWID() function.
 * 
 * @author Joseph Fifield
 */
public class SQLServerGUIDGenerator implements IdentifierGenerator {

	private static final Log log = LogFactory.getLog(SQLServerGUIDGenerator.class);

	public Serializable generate(SessionImplementor session, Object obj) throws SQLException, HibernateException {
		PreparedStatement st = session.getBatcher().prepareStatement("SELECT NEWID()");
		try {
			ResultSet rs = st.executeQuery();
			final String result;
			try {
				rs.next();
				result = (String)rs.getString(1);
			} finally {
				rs.close();
			}
			log.debug("SQL Server GUID identifier generated: " + result);
			return result;
		} catch (SQLException sqle) {
			JDBCExceptionReporter.logExceptions(sqle);
			throw sqle;
		} finally {
			session.getBatcher().closeStatement(st);
		}
	}

}
