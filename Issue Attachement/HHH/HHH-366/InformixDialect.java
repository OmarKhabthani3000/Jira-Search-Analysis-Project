//$Id: InformixDialect.java,v 1.14 2005/04/04 14:49:03 oneovthafew Exp $
package org.hibernate.dialect;

import java.sql.SQLException;
import java.sql.Types;

import org.apache.regexp.RE;

import org.hibernate.MappingException;
import org.hibernate.util.StringHelper;
import org.hibernate.exception.ErrorCodeConverter;
import org.hibernate.exception.JDBCExceptionHelper;
import org.hibernate.exception.SQLExceptionConverter;
import org.hibernate.exception.ViolatedConstraintNameExtracter;

/**
 * Informix dialect.<br>
 * <br>
 * Seems to work with Informix Dynamic Server Version 7.31.UD3,
 * Informix JDBC driver version 2.21JC3.
 * @author Steve Molitor
 */
public class InformixDialect extends Dialect {

	/**
	 * Creates new <code>InformixDialect</code> instance. Sets up the JDBC /
	 * Informix type mappings.
	 */
	public InformixDialect() {
		super();

		registerColumnType(Types.BIGINT, "int8");
		registerColumnType(Types.BINARY, "byte");
		registerColumnType(Types.BIT, "smallint"); // Informix doesn't have a bit type
		registerColumnType(Types.CHAR, "char($l)");
		registerColumnType(Types.DATE, "date");
		registerColumnType(Types.DECIMAL, "decimal");
		registerColumnType(Types.DOUBLE, "double");
		registerColumnType(Types.FLOAT, "float");
		registerColumnType(Types.INTEGER, "integer");
		registerColumnType(Types.LONGVARBINARY, "blob"); // or BYTE
		registerColumnType(Types.LONGVARCHAR, "clob"); // or TEXT?
		registerColumnType(Types.NUMERIC, "decimal"); // or MONEY
		registerColumnType(Types.REAL, "smallfloat");
		registerColumnType(Types.SMALLINT, "smallint");
		registerColumnType(Types.TIMESTAMP, "datetime year to fraction(5)");
		registerColumnType(Types.TIME, "datetime hour to second");
		registerColumnType(Types.TINYINT, "smallint");
		registerColumnType(Types.VARBINARY, "byte");
		registerColumnType(Types.VARCHAR, "varchar($l)");
	}

	public String getAddColumnString() {
		return "add";
	}

	public boolean supportsIdentityColumns() {
		return true;
	}

	public String getIdentitySelectString(String table, String column, int type) 
	throws MappingException {
		return type==Types.BIGINT ?
			"select dbinfo('serial8') from systables where tabid=1" :
			"select dbinfo('sqlca.sqlerrd1') from systables where tabid=1";
	}

	public String getIdentityColumnString(int type) throws MappingException {
		return type==Types.BIGINT ?
			"serial8 not null" :
			"serial not null";
	}

	public boolean hasDataTypeInIdentityColumn() {
		return false;
	}

	/**
	 * The syntax used to add a foreign key constraint to a table.
	 * Informix constraint name must be at the end.
	 * @return String
	 */
	public String getAddForeignKeyConstraintString(
			String constraintName, 
			String[] foreignKey, 
			String referencedTable, 
			String[] primaryKey
	) {
		return new StringBuffer(30)
			.append(" add constraint ")
			.append(" foreign key (")
			.append( StringHelper.join(", ", foreignKey) )
			.append(") references ")
			.append(referencedTable)
			.append(" constraint ")
			.append(constraintName)
			.toString();
	}

	/**
	 * The syntax used to add a primary key constraint to a table.
	 * Informix constraint name must be at the end.
	 * @return String
	 */
	public String getAddPrimaryKeyConstraintString(String constraintName) {
		return " add constraint primary key constraint " + constraintName + " ";
	}

	public String getCreateSequenceString(String sequenceName) {
		return "create sequence " + sequenceName;
	}
	public String getDropSequenceString(String sequenceName) {
		return "drop sequence " + sequenceName + " restrict";
	}

	public String getSequenceNextValString(String sequenceName) {
		return "select " + sequenceName + ".nextval from systables where tabid=1";
	}

	public boolean supportsSequences() {
		return true;
	}

	public boolean supportsLimit() {
		return true;
	}

	public boolean useMaxForLimit() {
		return true;
	}

	public boolean supportsLimitOffset() {
		return false;
	}

	public String getLimitString(String querySelect, int offset, int limit) {
		if (offset>0) throw new UnsupportedOperationException("informix has no offset");
		return new StringBuffer( querySelect.length()+8 )
			.append(querySelect)
			.insert( querySelect.toLowerCase().indexOf( "select" ) + 6, " first " + limit )
			.toString();
	}

	public boolean supportsVariableLimit() {
		return false;
	}

	/**
	 * Build an instance of the SQLExceptionConverter preferred by this dialect for
	 * converting SQLExceptions into Hibernate's JDBCException hierarchy.  The default
	 * Dialect implementation simply returns a converter based on X/Open SQLState codes.
	 * <p/>
	 * It is strongly recommended that specific Dialect implementations override this
	 * method, since interpretation of a SQL error is much more accurate when based on
	 * the ErrorCode rather than the SQLState.  Unfortunately, the ErrorCode is a vendor-
	 * specific approach.
	 *
	 * @return The Dialect's preferred SQLExceptionConverter.
	 */

	public SQLExceptionConverter buildSQLExceptionConverter() {
		return new ExceptionConverter( getViolatedConstraintNameExtracter() );
	}

	public ViolatedConstraintNameExtracter getViolatedConstraintNameExtracter() {
        return EXTRACTER;
	}

	private static class ExceptionConverter extends ErrorCodeConverter {
		private int[] sqlGrammarCodes = new int[]{};
		private int[] integrityViolationCodes = new int[]{-268};

		public ExceptionConverter(ViolatedConstraintNameExtracter anExtracter) {
			super( anExtracter );
		}

		protected int[] getSQLGrammarErrorCodes() {
			return this.sqlGrammarCodes;
		}

		protected int[] getIntegrityViolationErrorCodes() {
			return this.integrityViolationCodes;
		}
	}

	private static ViolatedConstraintNameExtracter EXTRACTER = new ViolatedConstraintNameExtracter() {

		/**
		 * Extract the name of the violated constraint from the given SQLException.
		 *
		 * @param sqle The exception that was the result of the constraint violation.
		 * @return The extracted constraint name.
		 */
		public String extractConstraintName(SQLException sqle) {
			String constraintName = null;
			
			int errorCode = JDBCExceptionHelper.extractErrorCode(sqle);
			if ( errorCode == -268 ) {
				RE re = new RE("^Unique constraint \\(.*\\.(.*)\\) violated\\.$");
				if (re.match(sqle.getMessage())) {
					constraintName = re.getParen(1);

					if (constraintName != null) {
						constraintName = constraintName.toUpperCase();
					}
				}
			}

			return constraintName;
		}

	};
}