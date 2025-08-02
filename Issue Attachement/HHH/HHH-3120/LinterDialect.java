package org.hibernate.dialect;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.*;
import org.hibernate.sql.*;
import org.hibernate.cfg.Environment;
import org.hibernate.dialect.function.*;
import org.hibernate.util.StringHelper;

/**
 * LINTER RDBMS dialect.
 *
 * @author Boris Fedorov
 * @author Galina Ermakova
 */
public class LinterDialect extends Dialect
{
  public LinterDialect()
  {
    super();

    registerColumnType(Types.BIT, "SMALLINT");
    registerColumnType(Types.TINYINT, "SMALLINT");
    registerColumnType(Types.SMALLINT, "SMALLINT");
    registerColumnType(Types.INTEGER, "INTEGER");
    registerColumnType(Types.BIGINT, "BIGINT");
    registerColumnType(Types.REAL, "REAL");
    registerColumnType(Types.FLOAT, "REAL");
    registerColumnType(Types.DOUBLE, "DOUBLE");
    registerColumnType(Types.NUMERIC, "DECIMAL($p,$s)");
    registerColumnType(Types.DECIMAL, "DECIMAL($p,$s)");
    registerColumnType(Types.CHAR, "CHAR(1)");
    registerColumnType(Types.CHAR, 4000, "CHAR($l)");
    registerColumnType(Types.VARCHAR, 4000, "VARCHAR($l)");
    //registerColumnType(Types.LONGVARCHAR, 4000, "VARCHAR($l)");
    registerColumnType(Types.DATE, "DATE");
    registerColumnType(Types.TIME, "DATE");
    registerColumnType(Types.TIMESTAMP, "DATE");
    registerColumnType(Types.BINARY, 4000, "BYTE($l)");
    registerColumnType(Types.VARBINARY, 4000, "VARBYTE($l)");
    //registerColumnType(Types.LONGVARBINARY, 4000, "VARBYTE($l)");
    registerColumnType(Types.BLOB, "BLOB");
    registerColumnType(Types.CLOB, "BLOB");
    registerColumnType(Types.BOOLEAN, "BOOLEAN");
    //registerColumnType(Types.OTHER, "EXTFILE");


    getDefaultProperties().setProperty(Environment.USE_STREAMS_FOR_BINARY, "true");
    getDefaultProperties().setProperty(Environment.STATEMENT_BATCH_SIZE, "0");

    registerFunction("abs", new StandardSQLFunction("abs"));
    registerFunction("acos", new StandardSQLFunction("acos", Hibernate.DOUBLE));
    registerFunction("asin", new StandardSQLFunction("asin", Hibernate.DOUBLE));
    registerFunction("atan", new StandardSQLFunction("atan", Hibernate.DOUBLE));
    registerFunction("atan2", new StandardSQLFunction("atan2", Hibernate.DOUBLE));
    registerFunction("ceil", new StandardSQLFunction("ceil"));
    registerFunction("chr", new StandardSQLFunction("chr", Hibernate.CHARACTER));
    registerFunction("cos", new StandardSQLFunction("cos", Hibernate.DOUBLE));
    registerFunction("cosh", new StandardSQLFunction("cosh", Hibernate.DOUBLE));
    registerFunction("exp", new StandardSQLFunction("exp", Hibernate.DOUBLE));
    registerFunction("floor", new StandardSQLFunction("floor"));
    registerFunction("greatest", new StandardSQLFunction("greatest"));
    registerFunction("initcap", new StandardSQLFunction("initcap"));
    registerFunction("instr", new StandardSQLFunction("instr", Hibernate.INTEGER));
    registerFunction("least", new StandardSQLFunction("least"));
    registerFunction("length", new StandardSQLFunction("length", Hibernate.LONG));
    registerFunction("lower", new StandardSQLFunction("lower"));
    registerFunction("ln", new StandardSQLFunction("ln", Hibernate.DOUBLE));
    registerFunction("log", new StandardSQLFunction("log", Hibernate.DOUBLE));
    registerFunction("lpad", new StandardSQLFunction("lpad", Hibernate.STRING));
    registerFunction("ltrim", new StandardSQLFunction("ltrim"));
    registerFunction("mod", new StandardSQLFunction("mod"));
    registerFunction("nvl", new StandardSQLFunction("nvl"));
    registerFunction("octet_length", new StandardSQLFunction("octet_length", Hibernate.LONG));
    registerFunction("power", new StandardSQLFunction("power", Hibernate.DOUBLE));
    registerFunction("round", new StandardSQLFunction("round"));
    registerFunction("rpad", new StandardSQLFunction("rpad", Hibernate.STRING));
    registerFunction("rtrim", new StandardSQLFunction("rtrim"));
    registerFunction("sign", new StandardSQLFunction("sign", Hibernate.INTEGER));
    registerFunction("sin", new StandardSQLFunction("sin", Hibernate.DOUBLE));
    registerFunction("sinh", new StandardSQLFunction("sinh", Hibernate.DOUBLE));
    registerFunction("sqrt", new StandardSQLFunction("sqrt", Hibernate.DOUBLE));
    registerFunction("soundex", new StandardSQLFunction("soundex"));
    registerFunction("substr", new StandardSQLFunction("substr", Hibernate.STRING));
    registerFunction("substring", new StandardSQLFunction("substr", Hibernate.STRING));
    registerFunction("tan", new StandardSQLFunction("tan", Hibernate.DOUBLE));
    registerFunction("tanh", new StandardSQLFunction("tanh", Hibernate.DOUBLE));
    registerFunction("to_char", new StandardSQLFunction("to_char", Hibernate.STRING));
    registerFunction("to_timestamp", new StandardSQLFunction("to_date", Hibernate.TIMESTAMP));
    registerFunction("to_date", new StandardSQLFunction("to_date", Hibernate.TIMESTAMP));
    registerFunction("trim", new SQLFunctionTemplate( Hibernate.STRING, "trim(?1 ?2 ?3 ?4)" ) );
    registerFunction("trunc", new StandardSQLFunction("trunc"));
    registerFunction("upper", new StandardSQLFunction("upper"));
    registerFunction("rand", new StandardSQLFunction("rand", Hibernate.DOUBLE));
    registerFunction("replace", new StandardSQLFunction("replace", Hibernate.STRING));
    registerFunction("sysdate", new NoArgSQLFunction("sysdate", Hibernate.TIMESTAMP, false));
    registerFunction("systimestamp", new NoArgSQLFunction("sysdate", Hibernate.TIMESTAMP, false));
    registerFunction("concat", new VarArgsSQLFunction(Hibernate.STRING, "", "||", ""));
//  registerFunction("str", new StandardSQLFunction("to_char", Hibernate.STRING));
    registerFunction("year", new SQLFunctionTemplate(Hibernate.INTEGER, "datesplit(?1, 'Y')"));

    registerFunction("stddev", new StandardSQLFunction("stddev", Hibernate.DOUBLE) );
    registerFunction("variance", new StandardSQLFunction("variance", Hibernate.DOUBLE) );
//  registerFunction("current_date", new StandardSQLFunction("to_date(to_char(sysdate, 'DD.MM.YYYY'), 'DD.MM.YYYY')", Hibernate.DATE) );
    registerFunction("current_date", new NoArgSQLFunction("to_date(to_char(sysdate, 'DD.MM.YYYY'), 'DD.MM.YYYY')", Hibernate.DATE, false) );
    registerFunction("current_time", new NoArgSQLFunction("to_date(to_char(sysdate, 'hh:mi:ss'), 'hh:mi:ss')", Hibernate.TIME, false) );
    registerFunction("current_timestamp", new NoArgSQLFunction("sysdate", Hibernate.TIMESTAMP, false) );
    registerFunction("last_day", new StandardSQLFunction("last_day", Hibernate.TIMESTAMP) );
    registerFunction("user", new StandardSQLFunction("user", Hibernate.STRING) );
    registerFunction("translate", new StandardSQLFunction("translate", Hibernate.STRING) );
    registerFunction("locate", new SQLFunctionTemplate( Hibernate.INTEGER, "instr(?2,?1,?3)" ) );
    registerFunction("coalesce", new NvlFunction() );
    registerFunction("add_months", new StandardSQLFunction("add_months", Hibernate.TIMESTAMP) );
    registerFunction("months_between", new SQLFunctionTemplate( Hibernate.INTEGER, "divtime(64, ?1, ?2)" ) );
    registerFunction("next_day", new StandardSQLFunction("next_day", Hibernate.TIMESTAMP) );

    registerFunction("char_length", new StandardSQLFunction("char_length", Hibernate.LONG) );
    registerFunction("lcase", new StandardSQLFunction("lower", Hibernate.STRING));
    registerFunction("ucase", new StandardSQLFunction("upper", Hibernate.STRING));
    registerFunction("unhex", new SQLFunctionTemplate( Hibernate.STRING, "cast hextoraw(?1) as char" ) );
    registerFunction("cot", new SQLFunctionTemplate( Hibernate.DOUBLE, "1/tan(?1)" ) );
    registerFunction("log2", new SQLFunctionTemplate( Hibernate.DOUBLE, "log(?1,2)" ) );
    registerFunction("ceiling", new StandardSQLFunction("log(?1,10)", Hibernate.INTEGER ) );
    registerFunction("datediff", new SQLFunctionTemplate( Hibernate.INTEGER, "divtime(16, ?1, ?2)" ) );
    registerFunction("day", new SQLFunctionTemplate( Hibernate.INTEGER, "datesplit(?1, 'd')" ) );
    registerFunction("dayofmonth", new SQLFunctionTemplate( Hibernate.INTEGER, "datesplit(?1, 'd')" ) );
    registerFunction("dayname", new StandardSQLFunction("dayname", Hibernate.STRING));
    registerFunction("dayofweek", new SQLFunctionTemplate( Hibernate.INTEGER, "datesplit(?1, 'dw')" ) );
    registerFunction("dayofyear", new SQLFunctionTemplate( Hibernate.INTEGER, "datesplit(?1, 'dy')" ) );
    registerFunction("from_days", new StandardSQLFunction("from_days", Hibernate.TIMESTAMP)); // In new version!!
    registerFunction("hour", new SQLFunctionTemplate( Hibernate.INTEGER, "datesplit(?1, 'HH24')" ) );
    registerFunction("to_localtime", new StandardSQLFunction("to_localtime", Hibernate.TIMESTAMP));
    registerFunction("localtime", new StandardSQLFunction( "to_localtime", Hibernate.TIMESTAMP ) );
    registerFunction("localtimestamp", new StandardSQLFunction("to_localtime", Hibernate.TIMESTAMP) );
    registerFunction("microsecond", new SQLFunctionTemplate( Hibernate.INTEGER, "datesplit(?1, 'FF')" ) );
    registerFunction("minute", new SQLFunctionTemplate( Hibernate.INTEGER, "datesplit(?1, 'MI')" ) );
    registerFunction("month", new SQLFunctionTemplate( Hibernate.INTEGER, "datesplit(?1, 'M')" ) );
    registerFunction("monthname", new StandardSQLFunction("monthname", Hibernate.STRING));
    registerFunction("now", new StandardSQLFunction("now", Hibernate.TIMESTAMP)); // In new version!!
    //registerFunction("now", new StandardSQLFunction("sysdate", Hibernate.TIMESTAMP));
    registerFunction("quarter", new SQLFunctionTemplate( Hibernate.INTEGER, "datesplit(?1, 'QY')" ) );
    registerFunction("second", new SQLFunctionTemplate( Hibernate.INTEGER, "datesplit(?1, 'SS')" ) );
    registerFunction("to_days", new StandardSQLFunction("to_days", Hibernate.INTEGER)); // In new version!!
    registerFunction("unix_timestamp", new StandardSQLFunction("unix_timestamp", Hibernate.BIG_INTEGER)); // In new version!!
    registerFunction("week", new SQLFunctionTemplate( Hibernate.INTEGER, "datesplit(?1, 'WY') - 1" ) ); // ?????
    registerFunction("weekday", new SQLFunctionTemplate( Hibernate.INTEGER, "datesplit(?1, 'DW')" ) );
    registerFunction("weekofyear", new SQLFunctionTemplate( Hibernate.INTEGER, "datesplit(?1, 'WY')" ) );
    registerFunction("hex", new StandardSQLFunction("rawtohex", Hibernate.STRING));

    registerFunction("current_user", new StandardSQLFunction("user", Hibernate.STRING));
    registerFunction("session_user", new StandardSQLFunction("user", Hibernate.STRING));
    registerFunction("current_database", new StandardSQLFunction("dbname", Hibernate.STRING));
    registerFunction("current_schema", new StandardSQLFunction("user", Hibernate.STRING));
    registerFunction("to_number", new StandardSQLFunction("to_number", Hibernate.INTEGER));

    registerFunction("absval", new StandardSQLFunction("abs", Hibernate.INTEGER));
    registerFunction("float", new SQLFunctionTemplate( Hibernate.FLOAT, "cast ?1 as real" ) );
    registerFunction("dayofweek", new SQLFunctionTemplate( Hibernate.INTEGER, "datesplit(?1, 'DW')" ) );
    registerFunction("days", new SQLFunctionTemplate( Hibernate.INTEGER, "datesplit(?1, 'ND')" ) );
    registerFunction("week_iso", new SQLFunctionTemplate( Hibernate.INTEGER, "datesplit(?1, 'WY')" ) );
    registerFunction("double", new SQLFunctionTemplate( Hibernate.DOUBLE, "cast ?1 as double" ) );
    registerFunction("varchar", new SQLFunctionTemplate( Hibernate.STRING, "cast ?1 as varchar" ) );
    registerFunction("real", new SQLFunctionTemplate( Hibernate.DOUBLE, "cast ?1 as real" ) );
    registerFunction("bigint", new SQLFunctionTemplate( Hibernate.BIG_INTEGER, "cast ?1 as bigint" ) );
    registerFunction("char", new SQLFunctionTemplate( Hibernate.STRING, "cast ?1 as char" ) );
    registerFunction("integer", new SQLFunctionTemplate( Hibernate.INTEGER, "cast ?1 as integer	" ) );
    registerFunction("smallint", new SQLFunctionTemplate( Hibernate.SHORT, "cast ?1 as smallint" ) );
    registerFunction("posstr", new StandardSQLFunction("instr", Hibernate.INTEGER));

    registerFunction("len", new StandardSQLFunction("length", Hibernate.INTEGER));
    registerFunction("getdate", new NoArgSQLFunction("sysdate", Hibernate.TIMESTAMP, false));
    registerFunction("getutcdate", new StandardSQLFunction("to_localtime", Hibernate.TIMESTAMP) );
    registerFunction("square", new SQLFunctionTemplate( Hibernate.DOUBLE, "?1 * ?1" ) );


  }

  /** 
   * Provided we {@link #supportsInsertSelectIdentity}, then attch the
   * "select identity" clause to the  insert statement.
   *  <p/>
   * Note, if {@link #supportsInsertSelectIdentity} == false then
   * the insert-string should be returned without modification.
   *
   * @param insertString The insert command
   * @return The insert command with any necessary identity select
   * clause attached.
   */
  public String appendIdentitySelectToInsert(String insertString) {
    return insertString;
  }    

  /** 
   * Some dialects support an alternative means to <tt>SELECT FOR UPDATE</tt>,
   * whereby a "lock hint" is appends to the table name in the from clause.
   * <p/>
   * contributed by <a href="http://sourceforge.net/users/heschulz">Helge Schulz</a>
   *
   * @param mode The lock mode to apply
   * @param tableName The name of the table to which to apply the lock hint.
   * @return The table with any required lock hints.
   */
  public String appendLockHint(LockMode mode, String tableName) {
    return tableName;
  }

  /** 
   * Are string comparisons implicitly case insensitive.
   * <p/>
   * In other words, does [where 'XYZ' = 'xyz'] resolve to true?
   *
   * @return True if comparisons are case insensitive.
   * @since 3.2
   */
  public boolean areStringComparisonsCaseInsensitive() {
    return false;
  }	

  public boolean bindLimitParametersFirst() {
    return false;
  }

  public boolean bindLimitParametersInReverseOrder() {
    return false;
  }

  public char closeQuote() {
    return '"';
  }	

  /** 
   * Linter supports both strategy
   */
  public CaseFragment createCaseFragment() {
    return new ANSICaseFragment();
    //return new DecodeCaseFragment();
  }

  /**
   * Linter supports both style join, but Oracle-style join prefer.
   */
  public JoinFragment createOuterJoinFragment()
  {
    return new OracleJoinFragment();
    //return new ANSIJoinFragment();
  }	

  /** 
   * For Linter versions below 6.1 this is not always correct
   * 
   * For the underlying database, is READ_COMMITTED isolation implemented by
   * forcing readers to wait for write locks to be released?
   *
   * @return True if writers block readers to achieve READ_COMMITTED; false otherwise.
   */
  public boolean doesReadCommittedCauseWritersToBlockReaders() {
//  return false; 10.11.07 version (!) for Linter 6.1
    return true;
  }    

  /** 
   * For Linter versions below 6.1 this is not always correct
   * 
   * For the underlying database, is REPEATABLE_READ isolation implemented by
   * forcing writers to wait for read locks to be released?
   *
   * @return True if readers block writers to achieve REPEATABLE_READ; false otherwise.
   */
  public boolean doesRepeatableReadCauseReadersToBlockWriters() {
//  return false; 10.11.07 version(!) for Linter 6.1
    return true;
  }	

  /** 
   * Do we need to drop constraints before dropping tables in this dialect?
   *
   * @return True if constraints must be dropped prior to dropping
   * the table; false otherwise.
   */
  public boolean dropConstraints() {
    return true;
  }	

  /** 
   * Do we need to drop the temporary table after use?
   *
   * @return True if the table should be dropped.
   */
  public boolean dropTemporaryTableAfterUse() {
    return false;
  }	

  /** 
   * Is <tt>FOR UPDATE OF</tt> syntax supported?
   *
   * @return True if the database supports <tt>FOR UPDATE OF</tt> syntax;
   * false otherwise.
   */
  public boolean forUpdateOfColumns() {
    return true;
  }	

  /** 
   * Generate a temporary table name given the bas table.
   *
   * @param baseTableName The table name from which to base the temp table name.
   * @return The generated temp table name.
   */
  public String generateTemporaryTableName(String baseTableName) {
    String name = "$$$TMP_" + baseTableName;
    return name.length() > 30 ? name.substring( 1, 30 ) : name;
  }

  /**
   * The syntax used to add a column to a table (optional).
   */
  public String getAddColumnString()
  {
    return " add column ";
  }

  /**
   * The syntax used to add a foreign key constraint to a table.
   *
   * @param referencesPrimaryKey if false, constraint should be
   *                             explicit about which column names the constraint refers to
   * @return String
   */
  public String getAddForeignKeyConstraintString(
      String constraintName,
      String[] foreignKey,
      String referencedTable,
      String[] primaryKey,
      boolean referencesPrimaryKey
  )
  {
    StringBuffer res = new StringBuffer(30);

    res.append(" add foreign key (")
    .append(StringHelper.join(", ", foreignKey))
    .append(") references ")
    .append(referencedTable)
    .append(" (")
    .append(StringHelper.join(", ", primaryKey))
    .append(')');
    return res.toString();
  }

  /**
   * The syntax used to add a primary key constraint to a table.
   *
   * @return String
   */
  public String getAddPrimaryKeyConstraintString(String constraintName)
  {
    return " add primary key ";
  }

  /**
   * Completely optional cascading drop clause
   *
   * @return String
   */
  public String getCascadeConstraintsString()
  {
    return " cascade";
  }

  public String getColumnComment(String comment) {
    return "";
  }

  public String getCreateMultisetTableString() {
    return getCreateTableString();
  }

  /**
   * The syntax used to create a sequence, if sequences are supported.
   *
   * @param sequenceName the name of the sequence
   * @return String
   * @throws MappingException if no sequences
   */
  protected String getCreateSequenceString(String sequenceName) throws MappingException
  {
    return "create sequence " + sequenceName;
  }


  /** 
   * Overloaded form of {@link #getCreateSequenceString(String)}, additionally
   * taking the initial value and increment size to be applied to the sequence
   * definition.
   * </p>
   * The default definition is to suffix {@link #getCreateSequenceString(String)}
   * with the string: " start with {initialValue} increment by {incrementSize}" where
   * {initialValue} and {incrementSize} are replacement placeholders.  Generally
   * dialects should only need to override this method if different key phrases
   * are used to apply the allocation information.
   *
   * @param sequenceName The name of the sequence
   * @param initialValue The initial value to apply to 'create sequence' statement
   * @param incrementSize The increment value to apply to 'create sequence' statement
   * @return The sequence creation command
   * @throws MappingException If sequences are not supported.
   */
  protected String getCreateSequenceString(String sequenceName, int initialValue, int incrementSize) throws MappingException {
    if ( supportsPooledSequences() ) {
      return getCreateSequenceString( sequenceName ) + " start with " + initialValue + " increment by " + incrementSize;
    }
    throw new MappingException( "Dialect does not support pooled sequences" );
  }    

  public String getCreateTableString() {
    return "create table";
  }

  /** 
   * Get any fragments needing to be postfixed to the command for
   * temporary table creation.
   *
   * @return Any required postfix.
   */
  public String getCreateTemporaryTablePostfix() {
    return "on commit delete rows";
  }

  /** 
   * Command used to create a temporary table.
   *
   * @return The command used to create a temporary table.
   */
  public String getCreateTemporaryTableString() {
    return "create global temporary table";
  }

  public String getCurrentTimestampSelectString()
  {
    return "select sysdate";
  }

  /** 
   * The name of the database-specific SQL function for retrieving the
   * current timestamp.
   *
   * @return The function name.
   */
  public String getCurrentTimestampSQLFunctionName() {
    return "sysdate";
  }

  /**
   * Perhaps it is the old implementation of the function {@link #getDropForeignKeyString()}?!
   * In the current version (3.2.5) this function is not exist!
   */
  public String getDropForeignKeyConstraintString(String constraintName, String[] cols)
  {
    StringBuffer res = new StringBuffer(30);
    res.append(" drop foreign key (")
    .append(StringHelper.join(", ", cols))
    .append(") ");
    return res.toString();
  }    

  public String getDropForeignKeyString()
  {
    return " drop foreign key ";
  }

  /**
   * The syntax used to drop a sequence, if sequences are supported.
   *
   * @param sequenceName the name of the sequence
   * @return String
   * @throws MappingException if no sequences
   */
  protected String getDropSequenceString(String sequenceName) throws MappingException
  {
    return "drop sequence " + sequenceName;
  }

  public String getForUpdateNowaitString() {
    // by default we report no support for NOWAIT lock semantics
    return getForUpdateString();
  }

  /** 
   * Get the <tt>FOR UPDATE OF column_list NOWAIT</tt> fragment appropriate
   * for this dialect given the aliases of the columns to be write locked.
   *
   * @param aliases The columns to be write locked.
   * @return The appropriate <tt>FOR UPDATE colunm_list NOWAIT</tt> clause string.
   */
  public String getForUpdateNowaitString(String aliases) {
    return "for update nowait";
  }

  /** 
   * Get the string to append to SELECT statements to acquire locks
   * for this dialect.
   *
   * @return The appropriate <tt>FOR UPDATE</tt> clause string.
   */
  public String getForUpdateString() {
    return " for update";
  }	

  /** 
   * Get the <tt>FOR UPDATE OF column_list</tt> fragment appropriate for this
   * dialect given the aliases of the columns to be write locked.
   *
   * @param aliases The columns to be write locked.
   * @return The appropriate <tt>FOR UPDATE OF column_list</tt> clause string.
   */
  public String getForUpdateString(String aliases) {
    // by default we simply return the getForUpdateString() result since
    // the default is to say no support for "FOR UPDATE OF ..."
    return getForUpdateString() + " of " + aliases;
  }	

  protected String getIdentityColumnString() throws MappingException
  {
    return "autoinc";
  }

  /** 
   * The syntax used during DDL to define a column as being an IDENTITY of
   * a particular type.
   *
   * @param type The {@link java.sql.Types} type code.
   * @return The appropriate DDL fragment.
   * @throws MappingException If IDENTITY generation is not supported.
   */
  public String getIdentityColumnString(int type) throws MappingException {
    switch (type)
    {
    case Types.INTEGER: 
    case Types.TINYINT:
    case Types.SMALLINT:
    case Types.BIT:
    case Types.BIGINT:
      return getIdentityColumnString();
    default: 
      throw new MappingException( "Dialect does not support identity key for this type" );
    }
  }

  /** 
   * The keyword used to insert a generated value into an identity column (or null).
   * Need if the dialect does not support inserts that specify no column values.
   *
   * @return The appropriate keyword.
   */
  public String getIdentityInsertString() {
    return "default";
  }

  protected String getIdentitySelectString() throws MappingException
  {
    return "select last_autoinc";
  }

  /** 
   * The syntax that returns the identity value of the last insert, if
   * identity column key generation is supported.
   */
  public String getIdentitySelectString(String table, String column, int type)
  throws MappingException
  {
    return getIdentitySelectString();
  }

  /**
   *  Linter don't support this method (with prepared statements)
   * 
   * Apply s limit clause to the query.
   * <p/>
   * Typically dialects utilize {@link #supportsVariableLimit() variable}
   * limit caluses when they support limits.  Thus, when building the
   * select command we do not actually need to know the limit or the offest
   * since we will just be using placeholders.
   * <p/>
   * Here we do still pass along whether or not an offset was specified
   * so that dialects not supporting offsets can generate proper exceptions.
   * In general, dialects will override one or the other of this method and
   * {@link #getLimitString(String, int, int)}.
   *
   * @param query The query to which to apply the limit.
   * @param hasOffset Is the query requesting an offset?
   * @return the modified SQL
   */
  protected String getLimitString(String query, boolean hasOffset) {
    throw new UnsupportedOperationException( "paged queries not supported" );
  }

  /** 
   * Given a limit and an offset, apply the limit clause to the query.
   *
   * @param query The query to which to apply the limit.
   * @param offset The offset of the limit
   * @param limit The limit of the limit ;)
   * @return The modified query statement with the limit applied.
   */
  public String getLimitString(String query, int offset, int limit) {
    return new StringBuffer( query.length()+20 )
    .append(query)
    .append( offset > 0 ? " limit " + offset + ", " + limit : " limit " + limit)
    .toString();
  }

  public String getLowercaseFunction() {
    return "lower";
  }

  public int getMaxAliasLength()
  {
    return 66;
  }	

  /**
   * The keyword used to insert a row without specifying any column values.
   * This is not possible on some databases.
   */
  public String getNoColumnsInsertString()
  {
    return "default values";
  }

  /**
   * The keyword used to specify a nullable column.
   *
   * @return String
   */
  public String getNullColumnString()
  {
    return " null";
  }

  /**
   * A query used to find all sequences
   *
   * @see org.hibernate.tool.hbm2ddl.SchemaUpdate
   */
  public String getQuerySequencesString()
  {
    return "select $$$NAME from LINTER_SYSTEM_USER.$$$SEQ";
  }

  public ResultSet getResultSet(CallableStatement ps) throws SQLException
  {
    return ps.getResultSet();
  }

  /** 
   * Given a {@link java.sql.Types} type code, determine an appropriate
   * null value to use in a select clause.
   * <p/>
   * One thing to consider here is that certain databases might
   * require proper casting for the nulls here since the select here
   * will be part of a UNION/UNION ALL.
   *
   * @param sqlType The {@link java.sql.Types} type code.
   * @return The appropriate select clause value fragment.
   */
  public String getSelectClauseNullString(int sqlType) {
    return "null";
  }

  /** 
   * Get the command used to select a GUID from the underlying database.
   * <p/>
   * Optional operation.
   *
   * @return The appropriate command.
   */
  public String getSelectGUIDString() {
    return "select sys_guid()";
  }

  /**
   * Generate the select expression fragment that will retreive the next
   * value of a sequence, if sequences are supported.
   * <p/>
   * This differs from {@link #getSequenceNextValString(String)} in that this
   * should return an expression usable within another select statement.
   *
   * @param sequenceName the name of the sequence
   * @return String
   * @throws MappingException if no sequences
   */
  public String getSelectSequenceNextValString(String sequenceName) throws MappingException
  {
    return sequenceName + ".nextval";
  }

  public String getSequenceNextValString(String sequenceName) throws MappingException
  {
    return "select " + getSelectSequenceNextValString(sequenceName);
  }

  public String getTableComment(String comment) {
    return "";
  }

  public String getTableTypeString() {
    // grrr... for differentiation of mysql storage engines
    return "";
  }

  public boolean hasAlterTable() {
    return true;
  }	

  public boolean hasDataTypeInIdentityColumn() {
    return true;
  }

  public boolean hasSelfReferentialForeignKeyBug() {
    return false;
  }

  public boolean isCurrentTimestampSelectStringCallable()
  {
    return false;
  }	

  public char openQuote() {
    return '"';
  }

  /** 
   * Does the dialect require that temporary table DDL statements occur in
   * isolation from other statements?  This would be the case if the creation
   * would cause any current transaction to get committed implicitly.
   * <p/>
   * JDBC defines a standard way to query for this information via the
   * {@link java.sql.DatabaseMetaData#dataDefinitionCausesTransactionCommit()}
   * method.  However, that does not distinguish between temporary table
   * DDL and other forms of DDL; MySQL, for example, reports DDL causing a
   * transaction commit via its driver, even though that is not the case for
   * temporary table DDL.
   * <p/>
   * Possible return values and their meanings:<ul>
   * <li>{@link Boolean#TRUE} - Unequivocally, perform the temporary table DDL
   * in isolation.</li>
   * <li>{@link Boolean#FALSE} - Unequivocally, do <b>not</b> perform the
   * temporary table DDL in isolation.</li>
   * <li><i>null</i> - defer to the JDBC driver response in regards to
   * {@link java.sql.DatabaseMetaData#dataDefinitionCausesTransactionCommit()}</li>
   * </ul>
   *
   * @return see the result matrix above.
   */
  public Boolean performTemporaryTableDDLInIsolation() {
    return null;
  }

  /** 
   * Do we need to qualify index names with the schema name?
   *
   * @return boolean
   */
  public boolean qualifyIndexName() {
    return false;
  }

  public boolean supportsBindAsCallableArgument() {
    return true;
  }

  public boolean supportsCascadeDelete() {
    return true;
  }

  public boolean supportsCircularCascadeDeleteConstraints() {
    return true;
  }	

  public boolean supportsColumnCheck() {
    return true;
  }

  public boolean supportsCommentOn() {
    return false; 
  }

  public boolean supportsCurrentTimestampSelection()
  {
    return true;
  }

  /** 
   * Does this dialect support empty IN lists?
   * For example, is [where XYZ in ()] a supported construct?
   *
   * @return True if empty in lists are supported; false otherwise.
   * @since 3.2
   */
  public boolean supportsEmptyInList() {
    return false;
  }    

  /** 
   * Does the dialect support an exists statement in the select clause?
   *
   * @return True if exists checks are allowed in the select clause; false otherwise.
   */
  public boolean supportsExistsInSelect() {
    return true;
  }

  public boolean supportsExpectedLobUsagePattern() {
    return true;
  }	

  /** 
   * Does this dialect support identity column key generation?
   * 
   * @return True if IDENTITY columns are supported; false otherwise.
   * 
   * @return boolean
   */
  public boolean supportsIdentityColumns()
  {
    return true;
  }

  public boolean supportsIfExistsAfterTableName() {
    return false;
  }

  public boolean supportsIfExistsBeforeTableName() {
    return false;
  }

  /** 
   * Does the dialect support some form of inserting and selecting
   * the generated IDENTITY value all in the same statement.
   *
   * @return True if the dialect supports selecting the just
   * generated IDENTITY in the insert statement.
   */
  public boolean supportsInsertSelectIdentity() {
    return false;
  }

  /** 
   * Does this dialect support some form of limiting query results
   * via a SQL clause?
   *
   * @return True if this dialect supports some form of LIMIT.
   */
  public boolean supportsLimit() {
    return true;
  }

  /** 
   * Does this dialect's LIMIT support (if any) additionally
   * support specifying an offset?
   *
   * @return True if the dialect supports an offset within the limit support.
   */
  public boolean supportsLimitOffset() {
    return true;
  }

  public boolean supportsLobValueChangePropogation() {
    return false;
  }	

  public boolean supportsNotNullUnique() {
    return false;
  }

  /** 
   * Does this dialect support <tt>FOR UPDATE</tt> in conjunction with
   * outer joined rows?
   *
   * @return True if outer joined rows can be locked via <tt>FOR UPDATE</tt>.
   */
  public boolean supportsOuterJoinForUpdate() {
    return true;	
  }

  /** 
   * Does this dialect support parameters within the select clause of
   * INSERT ... SELECT ... statements?
   *
   * @return True if this is supported; false otherwise.
   * @since 3.2
   */
  public boolean supportsParametersInInsertSelect() {
    return true;
  }	

  /** 
   * Does this dialect support "pooled" sequences.  Not aware of a better
   * name for this.  Essentially can we specify the initial and increment values?
   *
   * @return True if such "pooled" sequences are supported; false otherwise.
   * @see #getCreateSequenceStrings(String, int, int)
   * @see #getCreateSequenceString(String, int, int)
   */
  public boolean supportsPooledSequences() {
    return true;
  }	

  /** 
   * Does this dialect support asking the result set its positioning
   * information on forward only cursors.  Specifically, in the case of
   * scrolling fetches, Hibernate needs to use
   * {@link java.sql.ResultSet#isAfterLast} and
   * {@link java.sql.ResultSet#isBeforeFirst}.  Certain drivers do not
   * allow access to these methods for forward only cursors.
   * <p/>
   * NOTE : this is highly driver dependent!
   *
   * @return True if methods like {@link java.sql.ResultSet#isAfterLast} and
   * {@link java.sql.ResultSet#isBeforeFirst} are supported for forward
   * only cursors; false otherwise.
   * @since 3.2
   */
  public boolean supportsResultSetPositionQueryMethodsOnForwardOnlyCursor() {
    return false;
  }	

  /** 
   * Is this dialect known to support what ANSI-SQL terms "row value
   * constructor" syntax; sometimes called tuple syntax.
   * <p/>
   * Basically, does it support syntax like
   * "... where (FIRST_NAME, LAST_NAME) = ('Steve', 'Ebersole') ...".
   *
   * @return True if this SQL dialect is known to support "row value
   * constructor" syntax; false otherwise.
   * @since 3.2
   */
  public boolean supportsRowValueConstructorSyntax() {
    return true;
  }	

  /** 
   * If the dialect supports {@link #supportsRowValueConstructorSyntax() row values},
   * does it offer such support in IN lists as well?
   * <p/>
   * For example, "... where (FIRST_NAME, LAST_NAME) IN ( (?, ?), (?, ?) ) ..."
   *
   * @return True if this SQL dialect is known to support "row value
   * constructor" syntax in the IN list; false otherwise.
   * @since 3.2
   */
  public boolean supportsRowValueConstructorSyntaxInInList() {
    return true;
  }

  /**
   * Does this dialect support sequences?
   *
   * @return boolean
   */
  public boolean supportsSequences()
  {
    return true;
  }

  public boolean supportsSubqueryOnMutatingTable() {
    return true;
  }    

  /** 
   * Are subselects supported as the left-hand-side (LHS) of
   * IN-predicates.
   * <p/>
   * In other words, is syntax like "... <subquery> IN (1, 2, 3) ..." supported?
   *
   * @return True if subselects can appear as the LHS of an in-predicate;
   * false otherwise.
   * @since 3.2
   */
  public boolean  supportsSubselectAsInPredicateLHS() {
    return false;
  }

  /** 
   * Does this dialect support table-level check constraints?
   */
  public boolean supportsTableCheck()
  {
    return true;
  }	

  /** 
   * Does this dialect support temporary tables?
   *
   * @return True if temp tables are supported; false otherwise.
   */
  public boolean supportsTemporaryTables() {
    return true;
  }   

  /** 
   * Is it supported to materialize a LOB locator outside the transaction in
   * which it was created?
   * <p/>
   * Again, part of the trickiness here is the fact that this is largely
   * driver dependent.
   * <p/>
   * NOTE: all database I have tested which {@link #supportsExpectedLobUsagePattern()}
   * also support the ability to materialize a LOB outside the owning transaction...
   *
   * @return True if unbounded materialization is supported; false otherwise.
   * @since 3.2
   */
  public boolean supportsUnboundedLobLocatorMaterialization() {
    return true;
  }

  public boolean supportsUnionAll()
  {
    return true;
  }

  public boolean supportsUnique() {
    return true;
  }

  public boolean supportsUniqueConstraintInCreateAlterTable()
  {
    return true;
  }

  /** 
   * Does this dialect support bind variables (i.e., prepared statememnt
   * parameters) for its limit/offset?
   *
   * @return True if bind variables can be used; false otherwise.
   */
  public boolean supportsVariableLimit() {
    return false;
  }

  /**
   * The SQL value that the JDBC driver maps boolean values to
   */
  public String toBooleanValueString(boolean bool)
  {
    return bool ? "1" : "0";
  }

  public String transformSelectString(String select) {
    return select;
  }    

  public boolean useInputStreamToInsertBlob() {
    return true;
  }	

  public boolean useMaxForLimit() {
    return false;
  }	
}