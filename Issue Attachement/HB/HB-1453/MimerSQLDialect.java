package net.sf.hibernate.dialect;

import java.sql.Types;

import net.sf.hibernate.Hibernate;
import net.sf.hibernate.cfg.Environment;

/**
 * An Hibernate 2 SQL dialect for Mimer SQL. This dialect requires Mimer SQL 9.2.1 or later
 * because of the mappings to NCLOB, BINARY, and BINARY VARYING.
 * @author Fredrik Ålund <fredrik.alund@mimer.se>
 */
public class MimerSQLDialect extends Dialect {

	private static final int CHAR_MAX_LENTH = 4000;
	private static final int NATIONAL_CHAR_LENGTH = 2000;
	private static final int BINARY_MAX_LENGTH = 2000;
	
	/**
	 * Even thoug Mimer SQL supports character and binary columns up to 15 000 in lenght,
	 * this is also the maximum width of the table (exluding LOBs). To avoid breaking the limit all the
	 * time we limit the length of the character columns to CHAR_MAX_LENTH, NATIONAL_CHAR_LENGTH for national
	 * characters, and BINARY_MAX_LENGTH for binary types.
	 *
	 */
	public MimerSQLDialect() {
		super();
		registerColumnType( Types.BIT, "ODBC.BIT" );
		registerColumnType( Types.BIGINT, "BIGINT" );
		registerColumnType( Types.SMALLINT, "SMALLINT" );
		registerColumnType( Types.TINYINT, "ODBC.TINYINT" );
		registerColumnType( Types.INTEGER, "INTEGER" );
		registerColumnType( Types.CHAR, "NCHAR(1)" );
		registerColumnType( Types.VARCHAR, NATIONAL_CHAR_LENGTH, "NATIONAL CHARACTER VARYING($l)" );
		registerColumnType( Types.VARCHAR, "NCLOB($l)" );
		registerColumnType( Types.LONGVARCHAR, "CLOB($1)");
		registerColumnType( Types.FLOAT, "FLOAT" );
		registerColumnType( Types.DOUBLE, "DOUBLE PRECISION" );
		registerColumnType( Types.DATE, "DATE" );
		registerColumnType( Types.TIME, "TIME" );
		registerColumnType( Types.TIMESTAMP, "TIMESTAMP" );
		registerColumnType( Types.VARBINARY, BINARY_MAX_LENGTH, "BINARY VARYING($l)" );
		registerColumnType( Types.VARBINARY, "BLOB($1)" );
		registerColumnType( Types.LONGVARBINARY, "BLOB($1)");
		registerColumnType( Types.BINARY, BINARY_MAX_LENGTH, "BINARY" );
		registerColumnType( Types.BINARY, "BLOB($1)" );
		registerColumnType( Types.NUMERIC, "NUMERIC(19, $l)" );
		registerColumnType( Types.BLOB, "BLOB($l)" );
		registerColumnType( Types.CLOB, "NCLOB($l)" );

		registerFunction("abs", new StandardSQLFunction() );
		registerFunction("sign", new StandardSQLFunction(Hibernate.INTEGER) );
		registerFunction("ceiling", new StandardSQLFunction() );
		registerFunction("floor", new StandardSQLFunction() );
		registerFunction("round", new StandardSQLFunction() );

		registerFunction("dacos", new StandardSQLFunction(Hibernate.DOUBLE) );
		registerFunction("dasin", new StandardSQLFunction(Hibernate.DOUBLE) );
		registerFunction("datan", new StandardSQLFunction(Hibernate.DOUBLE) );
		registerFunction("datan2", new StandardSQLFunction(Hibernate.DOUBLE) );
		registerFunction("dcos", new StandardSQLFunction(Hibernate.DOUBLE) );
		registerFunction("dcot", new StandardSQLFunction(Hibernate.DOUBLE) );
		registerFunction("ddegrees", new StandardSQLFunction(Hibernate.DOUBLE) );
		registerFunction("dexp", new StandardSQLFunction(Hibernate.DOUBLE) );
		registerFunction("dlog", new StandardSQLFunction(Hibernate.DOUBLE) );
		registerFunction("dlog10", new StandardSQLFunction(Hibernate.DOUBLE) );
		registerFunction("dradian", new StandardSQLFunction(Hibernate.DOUBLE) );
		registerFunction("dsin", new StandardSQLFunction(Hibernate.DOUBLE) );
		registerFunction("soundex", new StandardSQLFunction(Hibernate.STRING) );
		registerFunction("dsqrt", new StandardSQLFunction(Hibernate.DOUBLE) );
		registerFunction("dtan", new StandardSQLFunction(Hibernate.DOUBLE) );
		registerFunction("dpower", new StandardSQLFunction() );

		registerFunction("date", new StandardSQLFunction(Hibernate.DATE) );
		registerFunction("dayofweek", new StandardSQLFunction(Hibernate.INTEGER) );
		registerFunction("dayofyear", new StandardSQLFunction(Hibernate.INTEGER) );
		registerFunction("time", new StandardSQLFunction(Hibernate.TIME) );
		registerFunction("timestamp", new StandardSQLFunction(Hibernate.TIMESTAMP) );
		registerFunction("week", new StandardSQLFunction(Hibernate.INTEGER) );

		
		registerFunction("varchar", new StandardSQLFunction(Hibernate.STRING) );
		registerFunction("real", new StandardSQLFunction(Hibernate.FLOAT) );
		registerFunction("bigint", new StandardSQLFunction(Hibernate.LONG) );
		registerFunction("char", new StandardSQLFunction(Hibernate.CHARACTER) );
		registerFunction("integer", new StandardSQLFunction(Hibernate.INTEGER) );
		registerFunction("smallint", new StandardSQLFunction(Hibernate.SHORT) );

		registerFunction("ascii_char", new StandardSQLFunction(Hibernate.CHARACTER) );
		registerFunction("ascii_code", new StandardSQLFunction(Hibernate.STRING));
		registerFunction("unicode_char", new StandardSQLFunction(Hibernate.LONG));
		registerFunction("unicode_code", new StandardSQLFunction(Hibernate.STRING));
		registerFunction("upper", new StandardSQLFunction() );
		registerFunction("lower", new StandardSQLFunction() );
		registerFunction("char_length", new StandardSQLFunction(Hibernate.LONG) );
		registerFunction("bit_length", new StandardSQLFunction(Hibernate.STRING));
		
		getDefaultProperties().setProperty(Environment.USE_STREAMS_FOR_BINARY, "true");
		getDefaultProperties().setProperty(Environment.STATEMENT_BATCH_SIZE, "50");
	}

	/**
	 * The syntax used to add a column to a table
	 */
	public String getAddColumnString() {
		return "add column";
	}
	
	/**
	 * Mimer SQL supports drop constraint.
	 */
	public boolean dropConstraints() {
		return true;
	}

	/**
	* TODO: Check if Mimer SQL cannot handle the way DB2 does
	 */
	public boolean supportsIdentityColumns() {
		return false;
	}
	
	/**
	 * Mimer SQL supports sequences
	 * @return boolean
	 */
	public boolean supportsSequences() {
		return true;
	}
	
	/**
	 * The syntax used to get the next value of a sequence in Mimer SQL
	 */
	public String getSequenceNextValString(String sequenceName) {
		return "select next_value of " + sequenceName + " from system.onerow";
	}
	
	/**
	 * The syntax used to create a sequence. Since we presume the sequences will be used as keys,
	 * we make them unique.
	 */
	public String getCreateSequenceString(String sequenceName) {
		return "create unique sequence " + sequenceName;
	}
	
	/**
	* The syntax used to drop sequences
	*/
	public String getDropSequenceString(String sequenceName) {
		return "drop sequence " + sequenceName + " restrict";
	}

	/**
	* Mimer SQL does not support limit
	*/
	public boolean supportsLimit() {
		return false;
	}
	
	/**
	* The syntax for using cascade on constraints
	*/
	public String getCascadeConstraintsString() {
		return " cascade";
	}
	
	/**
	* The syntax for fetching all sequnces avialable in the current schema.
	*/
	public String getQuerySequencesString() {
		return "select sequence_schema || '.' || sequence_name from information_schema.ext_sequences";
	}
	
	/**
	 * Support the FOR UPDATE syntax? For now, returns false since
	 * the current version of the Mimer SQL JDBC Driver does not support
	 * updatable resultsets.
	 * @return boolean
	 */
	public boolean supportsForUpdate() {
		return false;
	}
	
}






