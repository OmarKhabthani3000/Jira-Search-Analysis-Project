package org.hibernate.dialect;

import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.ArrayList;

import org.hibernate.Hibernate;
import org.hibernate.JDBCException;
import org.hibernate.QueryException;
import org.hibernate.cfg.Environment;
import org.hibernate.dialect.function.NoArgSQLFunction;
import org.hibernate.dialect.function.SQLFunction;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.dialect.function.VarArgsSQLFunction;
import org.hibernate.engine.Mapping;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.LockAcquisitionException;
import org.hibernate.exception.JDBCConnectionException;
import org.hibernate.exception.JDBCExceptionHelper;
import org.hibernate.exception.SQLGrammarException;
import org.hibernate.exception.SQLExceptionConverter;
import org.hibernate.exception.SQLStateConverter;
import org.hibernate.exception.TemplatedViolatedConstraintNameExtracter;
import org.hibernate.exception.ViolatedConstraintNameExtracter;
import org.hibernate.type.Type;
import org.hibernate.util.StringHelper;


/**
 * An SQL dialect for the Solid database (www.solidtech.com) release 4.5 onwards.
 * Release 4.2: full support for ALTER TABLE
 * Release 4.5: SQL-99 referention actions e.g. ON DELETE CASCADE, LIMIT/OFFSET support
 * 
 * @author Dave Richardson
 * 
 * 22.06.2005 Richardson
 * - First Version for Solid 4.2 and Hibernate 3.0.5
 * 17.05.2006 Richardson
 * - Hibernate 3.1 support: e.g. getSelectSequenceNextValString, supportsCurrentTimestampSelection, temporary tables 
 * - Solid 4.5 support: e.g. on delete cascade, drop table cascade constraints, select ... limit x offset y 
 */
public class SolidDialect extends Dialect {

	public SolidDialect() {
		super();
		registerColumnType( Types.BIT, "tinyint" );
		registerColumnType( Types.TINYINT, "tinyint" );
		registerColumnType( Types.SMALLINT, "smallint" );
		registerColumnType( Types.INTEGER, "integer" );
		registerColumnType( Types.BIGINT, "bigint" );
		registerColumnType( Types.REAL, "real" );
		registerColumnType( Types.FLOAT, "float" );
		registerColumnType( Types.DOUBLE, "double precision" );
		registerColumnType( Types.NUMERIC, "numeric($p,$s)" );
		registerColumnType( Types.DECIMAL, "decimal($l)" );

		registerColumnType( Types.CHAR, "char(1)" );
		registerColumnType( Types.VARCHAR, "varchar($l)" );
		registerColumnType( Types.LONGVARCHAR, "long varchar" );

		registerColumnType( Types.DATE, "date" );
		registerColumnType( Types.TIME, "time" );
		registerColumnType( Types.TIMESTAMP, "timestamp" );

		registerColumnType( Types.BINARY, "binary($l)" );
		registerColumnType( Types.VARBINARY, "varbinary($l)" );
		registerColumnType( Types.LONGVARBINARY, "long varbinary" );

        // Note: Solid JDBC driver [up to at least 04.50.0058] doesn't support get/set Blob/Clob
		registerColumnType( Types.BLOB, "long varbinary" );
		registerColumnType( Types.CLOB, "long varchar" );

		// String Functions
		registerFunction( "ascii", new StandardSQLFunction( "ascii", Hibernate.INTEGER) );
		registerFunction( "char", new StandardSQLFunction( "char", Hibernate.CHARACTER ) );
		registerFunction( "concat", new VarArgsSQLFunction( Hibernate.STRING, "", "||", "" ) );
		registerFunction( "get_unique_string", new StandardSQLFunction( "get_unique_string") );
		registerFunction( "insert", new StandardSQLFunction( "insert") );
		registerFunction( "lcase", new StandardSQLFunction( "lcase") );
		registerFunction( "left", new StandardSQLFunction( "left") );
		//in base class: registerFunction( "length", new StandardSQLFunction( "length", Hibernate.INTEGER ) );
		//in base class: registerFunction( "locate", new SQLFunctionTemplate( Hibernate.INTEGER, "locate(?1, ?2, ?3)" ) );
		//in base class: registerFunction( "lower", new StandardSQLFunction( "lower") );  // not listed in Solid SQL guide, but it works!
		registerFunction( "ltrim", new StandardSQLFunction( "ltrim") );
		registerFunction( "position", new StandardSQLFunction( "position", Hibernate.LONG) );
		registerFunction( "repeat", new StandardSQLFunction( "repeat") );
		registerFunction( "replace", new StandardSQLFunction( "replace") );
		registerFunction( "right", new StandardSQLFunction( "right") );
		registerFunction( "rtrim", new StandardSQLFunction( "rtrim") );
		registerFunction( "soundex", new StandardSQLFunction( "soundex") );
		registerFunction( "space", new StandardSQLFunction( "space", Hibernate.STRING) );
		registerFunction( "substring", new StandardSQLFunction( "substring", Hibernate.STRING) );
		registerFunction( "trim", new TrimFunction() );	// full syntax not supported by Solid
		registerFunction( "ucase", new StandardSQLFunction( "ucase") );
		//in base class: registerFunction( "upper", new StandardSQLFunction( "upper") );  // not listed in Solid SQL guide, but it works!


		// Numeric Functions
		//in base class: registerFunction( "abs", new StandardSQLFunction( "abs") );
		registerFunction( "acos", new StandardSQLFunction( "acos", Hibernate.DOUBLE) );
		registerFunction( "asin", new StandardSQLFunction( "asin", Hibernate.DOUBLE) );
		registerFunction( "atan", new StandardSQLFunction( "atan", Hibernate.DOUBLE) );
		registerFunction( "atan2", new StandardSQLFunction( "atan2", Hibernate.DOUBLE) );
		registerFunction( "ceil", new StandardSQLFunction( "ceiling", Hibernate.INTEGER) );
		registerFunction( "ceiling", new StandardSQLFunction( "ceiling", Hibernate.INTEGER) );
		registerFunction( "cos", new StandardSQLFunction( "cos", Hibernate.DOUBLE) );
		registerFunction( "cot", new StandardSQLFunction( "cot", Hibernate.DOUBLE) );
		registerFunction( "degrees", new StandardSQLFunction( "degrees", Hibernate.DOUBLE) );
		registerFunction( "difference", new StandardSQLFunction( "difference") );
		registerFunction( "exp", new StandardSQLFunction( "exp", Hibernate.DOUBLE) );
		registerFunction( "floor", new StandardSQLFunction( "floor", Hibernate.INTEGER) );
		registerFunction( "ln", new StandardSQLFunction( "log", Hibernate.DOUBLE) );
		registerFunction( "log", new StandardSQLFunction( "log", Hibernate.DOUBLE) );
		registerFunction( "log10", new StandardSQLFunction( "log10", Hibernate.DOUBLE) );
		//in base class: registerFunction( "mod", new StandardSQLFunction( "mod", Hibernate.INTEGER) );
		registerFunction( "pi", new NoArgSQLFunction( "pi", Hibernate.DOUBLE) );
		registerFunction( "power", new StandardSQLFunction( "power", Hibernate.DOUBLE) );
		registerFunction( "radians", new StandardSQLFunction( "radians", Hibernate.DOUBLE) );
		registerFunction( "round", new StandardSQLFunction( "round", Hibernate.INTEGER) );
		registerFunction( "sign", new StandardSQLFunction( "sign", Hibernate.INTEGER) );
		registerFunction( "sin", new StandardSQLFunction( "sin", Hibernate.DOUBLE) );
		//in base class: registerFunction( "sqrt", new StandardSQLFunction( "sqrt", Hibernate.DOUBLE) );
		registerFunction( "tan", new StandardSQLFunction( "tan", Hibernate.DOUBLE) );
		registerFunction( "truncate", new StandardSQLFunction( "mod", Hibernate.INTEGER) );

		// Date Time Functions
		registerFunction( "curdate", new NoArgSQLFunction( "curdate", Hibernate.DATE, true ) );
		registerFunction( "current_date", new NoArgSQLFunction( "curdate", Hibernate.DATE, true ) );
		registerFunction( "curtime", new NoArgSQLFunction( "curtime", Hibernate.TIME, true ) );
		registerFunction( "current_time", new NoArgSQLFunction( "curtime", Hibernate.TIME, true ) );
		registerFunction( "current_timestamp", new NoArgSQLFunction( "now", Hibernate.TIMESTAMP, true ) );
		registerFunction( "day", new StandardSQLFunction( "dayofmonth", Hibernate.INTEGER ) );
		registerFunction( "dayname", new StandardSQLFunction( "dayname", Hibernate.STRING ) );
		registerFunction( "dayofmonth", new StandardSQLFunction( "dayofmonth", Hibernate.INTEGER ) );
		registerFunction( "dayofweek", new StandardSQLFunction( "dayofweek", Hibernate.INTEGER ) );
		registerFunction( "dayofyear", new StandardSQLFunction( "dayofyear", Hibernate.INTEGER ) );
		//in base class: registerFunction( "extract", new SQLFunctionTemplate(Hibernate.INTEGER, "extract(?1 ?2 ?3)") );
		registerFunction( "hour", new StandardSQLFunction( "hour", Hibernate.INTEGER) );
		registerFunction( "minute", new StandardSQLFunction( "minute", Hibernate.INTEGER ) );
		registerFunction( "month", new StandardSQLFunction( "month", Hibernate.INTEGER ) );
		registerFunction( "monthname", new StandardSQLFunction( "monthname", Hibernate.STRING ) );
		registerFunction( "now", new NoArgSQLFunction( "now", Hibernate.TIMESTAMP, true ) );
		registerFunction( "quarter", new StandardSQLFunction( "quarter", Hibernate.INTEGER ) );
		registerFunction( "second", new StandardSQLFunction( "second", Hibernate.INTEGER ) );
		registerFunction( "timestampadd", new StandardSQLFunction( "timestampadd", Hibernate.TIMESTAMP ) );
		registerFunction( "timestampdiff", new StandardSQLFunction( "timestampdiff", Hibernate.INTEGER ) );
		registerFunction( "week", new StandardSQLFunction( "week", Hibernate.INTEGER ) );
		registerFunction( "weekday", new StandardSQLFunction( "dayofweek", Hibernate.INTEGER ) );
		registerFunction( "weekofyear", new StandardSQLFunction( "week", Hibernate.INTEGER ) );
		registerFunction( "year", new StandardSQLFunction( "year", Hibernate.INTEGER ) );
		registerFunction( "yearweek", new StandardSQLFunction( "week", Hibernate.INTEGER ) );

		// System Functions
		registerFunction( "user", new NoArgSQLFunction( "user", Hibernate.STRING, true ) );

		// Miscellaneous Functions
		registerFunction( "bit_and", new StandardSQLFunction( "bit_and", Hibernate.INTEGER ) );
		registerFunction( "bit_length", new SQLFunctionTemplate( Hibernate.INTEGER, "bit_length(cast(?1 as varchar))" ) );
		registerFunction( "str", new SQLFunctionTemplate( Hibernate.STRING, "cast(?1 as varchar)" ) );


		// e.g. current_date without parentheses is not recognized by registerFunction above.
		getDefaultProperties().setProperty( Environment.QUERY_SUBSTITUTIONS,
	    "true 1, false 0, yes 'Y', no 'N', current_date curdate(), current_time curtime(), current_timestamp now()" );

	}
	
	public boolean dropConstraints() {
		return false;	// since release 4.5
	}

	public String getCascadeConstraintsString() {
		return " cascade constraints";	// since release 4.5
	}

	public String getAddColumnString() {
		return "add";
	}

	public String getAddForeignKeyConstraintString(String constraintName,
												   String[] foreignKey,
												   String referencedTable,
												   String[] primaryKey) {
		return new StringBuffer( 30 )
			.append( " add constraint " )
			.append( constraintName )
			.append( " foreign key (" )
			.append( StringHelper.join( ", ", foreignKey ) )
			.append( ") references " )
			.append( referencedTable )
			.append(" (")
			.append( StringHelper.join(", ", primaryKey) )
			.append(')')
			.toString();
	}

	public String getAddPrimaryKeyConstraintString(String constraintName) {
		return " primary key ";
	}
	public boolean supportsSequences() {
		return true;
	}

	public String getSequenceNextValString(String sequenceName) {
		return "select " + sequenceName + ".nextval";
	}

	public String getSelectSequenceNextValString(String sequenceName) {
		return sequenceName + ".nextval";
	}

	public String getCreateSequenceString(String sequenceName) {
		// if you need to guarantee there are no holes, use "create dense sequence"
		return "create sequence " + sequenceName; //starts with 1, implicitly
	}

	public String getDropSequenceString(String sequenceName) {
		return "drop sequence " + sequenceName;
	}

	public String getQuerySequencesString() {
		return "select sequence_name from sys_sequences";
	}

	public String getLowercaseFunction() {
		return "lcase";
	}

	public boolean supportsCascadeDelete() {
		return true;  // since release 4.5
	}

	public boolean supportsUnionAll() {
		return true;
	}
	
	public boolean supportsCurrentTimestampSelection() {
		return true;
	}

	public boolean isCurrentTimestampSelectStringCallable() {
		return false;
	}

	public String getCurrentTimestampSelectString() {
		return "select now()";
	}

	public boolean supportsLimit() {
		return true;  // since release 4.5
	}

	public boolean bindLimitParametersInReverseOrder() {
		return true;	// i.e. limit, offset
	}

	public String getLimitString(String sql, boolean hasOffset) {
		return new StringBuffer( sql.length()+20 )
			.append(sql)
			.append( hasOffset ? " limit ? offset ?" : " limit ?")
			.toString();
	}

	public boolean supportsTemporaryTables() {
		return true;  // depends on license
	}

	public String getCreateTemporaryTableString() {
		return "create temporary table"; // may fail due to license restrictions
	}

	public SQLExceptionConverter buildSQLExceptionConverter() {
		return new ExceptionConverter( getViolatedConstraintNameExtracter() );
	}

	/**
	 * An exception converter based on the vendor specific ErrorCode.
	 * Introduced due to Solid error 10029 which is signalled with an SQLState "HY000".
	 * "HY" is not a standard integrity violation code.
	 * Currently only a few error codes are examined, any other exception
	 * is delegated to the standard Hibernate SQLExceptionConvertor based on the SQLState.
	 */
	private static class ExceptionConverter extends SQLStateConverter {
		private int[] sqlGrammarCodes = null;
		private int[] integrityViolationCodes = new int[] {
				10005,	// Unique constraint violation
				10029,	// Foreign key constraint (constraint-name) violation, referenced column values do not exist
				10033	// Primary key unique constraint violation
		};
		private int[] connectionCodes = null;
		private int[] lockAcquisitionErrorCodes = null;
		private ViolatedConstraintNameExtracter extracter;  // no access to superclass field

		public ExceptionConverter(ViolatedConstraintNameExtracter extracter) {
			super( extracter );
			this.extracter = extracter;
		}

		/**
		 * Convert the given SQLException into Hibernate's JDBCException hierarchy.
		 *
		 * @param sqlException The SQLException to be converted.
		 * @param message      An optional error message.
		 * @param sql          Optionally, the sql being performed when the exception occurred.
		 * @return The resulting JDBCException.
		 */
		public JDBCException convert(SQLException sqlException, String message, String sql) {
			int errorCode = JDBCExceptionHelper.extractErrorCode( sqlException );
			String sqlState = sqlException.getSQLState();
			SQLException nested = sqlException.getNextException();
			while ( sqlState == null && nested != null ) {
				sqlState = nested.getSQLState();
				nested = nested.getNextException();
			}
			if ( isMatch( getConnectionErrorCodes(), errorCode ) ) {
				return new JDBCConnectionException( message, sqlException, sql );
			}
			else if ( isMatch( getSQLGrammarErrorCodes(), errorCode ) ) {
				return new SQLGrammarException( message, sqlException, sql );
			}
			else if ( isMatch( getIntegrityViolationErrorCodes(), errorCode ) ) {
				String constraintName = extracter.extractConstraintName( sqlException );
				return new ConstraintViolationException( message, sqlException, sql, constraintName );
			}
			else if ( isMatch( getLockAcquisitionErrorCodes(), errorCode ) ) {
				return new LockAcquisitionException( message, sqlException, sql );
			}
			// no specific action: delegate to SQLState based super class
            return super.convert( sqlException, message, sql );
		}

		protected int[] getSQLGrammarErrorCodes() {
			return sqlGrammarCodes;
		}

		protected int[] getIntegrityViolationErrorCodes() {
			return integrityViolationCodes;
		}

		protected int[] getConnectionErrorCodes() {
			return connectionCodes;
		}

		protected int[] getLockAcquisitionErrorCodes() {
			return lockAcquisitionErrorCodes;
		}

		private boolean isMatch(int[] errorCodes, int errorCode) {
			if ( errorCodes != null ) {
				for ( int i = 0, max = errorCodes.length; i < max; i++ ) {
					if ( errorCodes[i] == errorCode ) {
						return true;
					}
				}
			}
			return false;
		}
	}

	public ViolatedConstraintNameExtracter getViolatedConstraintNameExtracter() {
        return EXTRACTER;
	}

	private static ViolatedConstraintNameExtracter EXTRACTER = new TemplatedViolatedConstraintNameExtracter() {

		/**
		 * Extract the name of the violated constraint from the given SQLException.
		 *
		 * @param sqle The exception that was the result of the constraint violation.
		 * @return The extracted constraint name.
		 */
		public String extractConstraintName(SQLException sqle) {
			String constraintName = null;
			
			int errorCode = JDBCExceptionHelper.extractErrorCode(sqle);

			if ( errorCode == 10029 ) {
				constraintName = extractUsingTemplate( "Foreign key constraint (", ") violation", sqle.getMessage() );
			}
      else if ( errorCode == 10005 ) {
        constraintName = extractUsingTemplate( "Unique constraint (", ") violation", sqle.getMessage() );
      }

			return constraintName;
		}

	};

	/**
	 * A Solid-specific version of the ANSI-SQL trim function as Solid
	 * (at least up to release 4.5) does not support the full syntax.
	 * Specifically LEADING/TRAILING/BOTH and a trim_character specification
	 * are not supported.
	 */
	private static class TrimFunction implements SQLFunction {
		private static final SQLFunction LEADING_SPACE_TRIM = new SQLFunctionTemplate( Hibernate.STRING, "ltrim( ?1 )" );
		private static final SQLFunction TRAILING_SPACE_TRIM = new SQLFunctionTemplate( Hibernate.STRING, "rtrim( ?1 )" );
		private static final SQLFunction BOTH_SPACE_TRIM = new SQLFunctionTemplate( Hibernate.STRING, "trim( ?1 )" );
		private static final SQLFunction BOTH_SPACE_TRIM_FROM = new SQLFunctionTemplate( Hibernate.STRING, "trim( ?2 )" );

		private static final SQLFunction LEADING_TRIM = new SQLFunctionTemplate( Hibernate.STRING, "replace( replace( ltrim( replace( replace( ?1, ' ', '${space}$' ), ?2, ' ' ) ), ' ', ?2 ), '${space}$', ' ' )" );
		private static final SQLFunction TRAILING_TRIM = new SQLFunctionTemplate( Hibernate.STRING, "replace( replace( rtrim( replace( replace( ?1, ' ', '${space}$' ), ?2, ' ' ) ), ' ', ?2 ), '${space}$', ' ' )" );
		private static final SQLFunction BOTH_TRIM = new SQLFunctionTemplate( Hibernate.STRING, "replace( replace( ltrim( rtrim( replace( replace( ?1, ' ', '${space}$' ), ?2, ' ' ) ) ), ' ', ?2 ), '${space}$', ' ' )" );

		public Type getReturnType(Type columnType, Mapping mapping) throws QueryException {
			return Hibernate.STRING;
		}

		public boolean hasArguments() {
			return true;
		}

		public boolean hasParenthesesIfNoArguments() {
			return false;
		}

		public String render(List args, SessionFactoryImplementor factory) throws QueryException {
			// according to both the ANSI-SQL and EJB3 specs, trim can either take
			// exactly one parameter or a variable number of parameters between 1 and 4.
			// from the SQL spec:
			//
			// <trim function> ::=
			//      TRIM <left paren> <trim operands> <right paren>
			//
			// <trim operands> ::=
			//      [ [ <trim specification> ] [ <trim character> ] FROM ] <trim source>
			//
			// <trim specification> ::=
			//      LEADING
			//      | TRAILING
			//      | BOTH
			//
			// If only <trim specification> is omitted, BOTH is assumed;
			// if <trim character> is omitted, space is assumed
			if ( args.size() == 1 ) {
				// we have the form: trim(trimSource)
				//      so we trim leading and trailing spaces
				return BOTH_SPACE_TRIM.render( args, factory );
			}
			else if ( "from".equalsIgnoreCase( ( String ) args.get( 0 ) ) ) {
				// we have the form: trim(from trimSource).
				//      This is functionally equivalent to trim(trimSource)
				return BOTH_SPACE_TRIM_FROM.render( args, factory );
			}
			else {
				// otherwise, a trim-specification and/or a trim-character
				// have been specified;  we need to decide which options
				// are present and "do the right thing"
				boolean leading = true;         // should leading trim-characters be trimmed?
				boolean trailing = true;        // should trailing trim-characters be trimmed?
				String trimCharacter = null;    // the trim-character
				String trimSource = null;       // the trim-source

				// potentialTrimCharacterArgIndex = 1 assumes that a
				// trim-specification has been specified.  we handle the
				// exception to that explicitly
				int potentialTrimCharacterArgIndex = 1;
				String firstArg = ( String ) args.get( 0 );
				if ( "leading".equalsIgnoreCase( firstArg ) ) {
					trailing = false;
				}
				else if ( "trailing".equalsIgnoreCase( firstArg ) ) {
					leading = false;
				}
				else if ( "both".equalsIgnoreCase( firstArg ) ) {
				}
				else {
					potentialTrimCharacterArgIndex = 0;
				}

				String potentialTrimCharacter = ( String ) args.get( potentialTrimCharacterArgIndex );
				if ( "from".equalsIgnoreCase( potentialTrimCharacter ) ) {
					trimCharacter = "' '";
					trimSource = ( String ) args.get( potentialTrimCharacterArgIndex + 1 );
				}
				else if ( potentialTrimCharacterArgIndex + 1 >= args.size() ) {
					trimCharacter = "' '";
					trimSource = potentialTrimCharacter;
				}
				else {
					trimCharacter = potentialTrimCharacter;
					if ( "from".equalsIgnoreCase( ( String ) args.get( potentialTrimCharacterArgIndex + 1 ) ) ) {
						trimSource = ( String ) args.get( potentialTrimCharacterArgIndex + 2 );
					}
					else {
						trimSource = ( String ) args.get( potentialTrimCharacterArgIndex + 1 );
					}
				}

				List argsToUse = null;
				argsToUse = new ArrayList();
				argsToUse.add( trimSource );
				argsToUse.add( trimCharacter );

				if ( trimCharacter.equals( "' '" ) ) {
					if ( leading && trailing ) {
						return BOTH_SPACE_TRIM.render( argsToUse, factory );
					}
					else if ( leading ) {
						return LEADING_SPACE_TRIM.render( argsToUse, factory );
					}
					else {
						return TRAILING_SPACE_TRIM.render( argsToUse, factory );
					}
				}
				else {
					if ( leading && trailing ) {
						return BOTH_TRIM.render( argsToUse, factory );
					}
					else if ( leading ) {
						return LEADING_TRIM.render( argsToUse, factory );
					}
					else {
						return TRAILING_TRIM.render( argsToUse, factory );
					}
				}
			}
		}
	}

}






