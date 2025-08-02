/**
 * 
 */
package fem.spi.hibernate.dialect;

import java.sql.SQLException;

import org.hibernate.exception.JDBCExceptionHelper;
import org.hibernate.exception.TemplatedViolatedConstraintNameExtracter;
import org.hibernate.exception.ViolatedConstraintNameExtracter;

/**
 * @author Denny Bartelt
 * @author $Author: deka $
 * @version $Revision: 184 $
 * @date $Date$
 * @see $HeadURL: https://subversion.projekt-spi.de/repository/spi2/branches/workspace/DEKA/spi-ejb/src/fem/spi/hibernate/dialect/PostgreSQLDialect.java $
 */
public class PostgreSQLDialect extends org.hibernate.dialect.PostgreSQLDialect {

	public ViolatedConstraintNameExtracter getViolatedConstraintNameExtracter() {
		return EXTRACTER;
	}

	private static ViolatedConstraintNameExtracter EXTRACTER = new TemplatedViolatedConstraintNameExtracter() {

		/**
		 * Extract the name of the violated constraint from the given SQLException.
		 * 
		 * @author Denny Bartelt
		 * 
		 * @param sqle The exception that was the result of the constraint violation.
		 * @return The extracted constraint name.
		 */
		public String extractConstraintName(SQLException sqle) {
			try {
				int sqlState = Integer.valueOf(JDBCExceptionHelper.extractSqlState(sqle)).intValue();
				switch (sqlState) {
					// CHECK VIOLATION
					case 23514: return extractUsingTemplate("violates check constraint \"","\"", sqle.getMessage());
					// UNIQUE VIOLATION
					case 23505: return extractUsingTemplate("violates unique constraint \"","\"", sqle.getMessage());
					// FOREIGN KEY VIOLATION
					case 23503: return extractUsingTemplate("violates foreign key constraint \"","\"", sqle.getMessage());
					// NOT NULL VIOLATION
					case 23502: return extractUsingTemplate("null value in column \"","\" violates not-null constraint", sqle.getMessage());
					// TODO: RESTRICT VIOLATION
					case 23001: return null;
					// ALL OTHER
					default: return null;
				}
			} catch (NumberFormatException nfe) {
				return null;
			}
		}
	};
}
