//$Id: DB2Dialect.java,v 1.7.2.9 2004/04/12 12:51:45 oneovthafew Exp $
package net.sf.hibernate.dialect;

import java.sql.Types;

import net.sf.hibernate.Hibernate;
import net.sf.hibernate.cfg.Environment;

/**
 * An SQL dialect for DB2.
 * @author Gavin King
 */
public class DB2Dialect extends Dialect {
	
	public DB2Dialect() {
		super();
		registerColumnType( Types.BIT, "SMALLINT" );
		registerColumnType( Types.BIGINT, "BIGINT" );
		registerColumnType( Types.SMALLINT, "SMALLINT" );
		registerColumnType( Types.TINYINT, "SMALLINT" );
		registerColumnType( Types.INTEGER, "INTEGER" );
		registerColumnType( Types.CHAR, "CHAR(1)" );
		registerColumnType( Types.VARCHAR, "VARCHAR($l)" );
		registerColumnType( Types.FLOAT, "FLOAT" );
		registerColumnType( Types.DOUBLE, "DOUBLE" );
		registerColumnType( Types.DATE, "DATE" );
		registerColumnType( Types.TIME, "TIME" );
		registerColumnType( Types.TIMESTAMP, "TIMESTAMP" );
		registerColumnType( Types.VARBINARY, "VARCHAR($l) FOR BIT DATA" );
		registerColumnType( Types.NUMERIC, "NUMERIC(19, $l)" );
		registerColumnType( Types.BLOB, "BLOB($l)" );
		registerColumnType( Types.CLOB, "CLOB($l)" );
		
		registerFunction("abs", new StandardSQLFunction() );
		registerFunction("absval", new StandardSQLFunction() );
		registerFunction("sign", new StandardSQLFunction(Hibernate.INTEGER) );
		
		registerFunction("ceiling", new StandardSQLFunction() );
		registerFunction("ceil", new StandardSQLFunction() );
		registerFunction("floor", new StandardSQLFunction() );
		registerFunction("round", new StandardSQLFunction() );

		registerFunction("acos", new StandardSQLFunction(Hibernate.DOUBLE) );
		registerFunction("asin", new StandardSQLFunction(Hibernate.DOUBLE) );
		registerFunction("atan", new StandardSQLFunction(Hibernate.DOUBLE) );
		registerFunction("cos", new StandardSQLFunction(Hibernate.DOUBLE) );
		registerFunction("cot", new StandardSQLFunction(Hibernate.DOUBLE) );
		registerFunction("degrees", new StandardSQLFunction(Hibernate.DOUBLE) );
		registerFunction("exp", new StandardSQLFunction(Hibernate.DOUBLE) );
		registerFunction("float", new StandardSQLFunction(Hibernate.DOUBLE) );
		registerFunction("hex", new StandardSQLFunction(Hibernate.STRING) );
		registerFunction("ln", new StandardSQLFunction(Hibernate.DOUBLE) );
		registerFunction("log", new StandardSQLFunction(Hibernate.DOUBLE) );
		registerFunction("log10", new StandardSQLFunction(Hibernate.DOUBLE) );
		registerFunction("radians", new StandardSQLFunction(Hibernate.DOUBLE) );					  
		registerFunction("rand", new NoArgSQLFunction(Hibernate.DOUBLE));
		registerFunction("sin", new StandardSQLFunction(Hibernate.DOUBLE) );
		registerFunction("soundex", new StandardSQLFunction(Hibernate.STRING) );
		registerFunction("sqrt", new StandardSQLFunction(Hibernate.DOUBLE) );
		registerFunction("stddev", new StandardSQLFunction(Hibernate.DOUBLE) );
		registerFunction("tan", new StandardSQLFunction(Hibernate.DOUBLE) );
		registerFunction("variance", new StandardSQLFunction(Hibernate.DOUBLE) );
		
		registerFunction("julian_day", new StandardSQLFunction(Hibernate.INTEGER) );
		registerFunction("microsecond", new StandardSQLFunction(Hibernate.INTEGER) );
		registerFunction("midnight_seconds", new StandardSQLFunction(Hibernate.INTEGER) );
		registerFunction("minute", new StandardSQLFunction(Hibernate.INTEGER) );
		registerFunction("month", new StandardSQLFunction(Hibernate.INTEGER) );
		registerFunction("monthname", new StandardSQLFunction(Hibernate.STRING) );
		registerFunction("quarter", new StandardSQLFunction(Hibernate.INTEGER) );
		registerFunction("hour", new StandardSQLFunction(Hibernate.INTEGER) );
		registerFunction("second", new StandardSQLFunction(Hibernate.INTEGER) );
		registerFunction("date", new StandardSQLFunction(Hibernate.DATE) );
		registerFunction("day", new StandardSQLFunction(Hibernate.INTEGER) );
		registerFunction("dayname", new StandardSQLFunction(Hibernate.STRING) );
		registerFunction("dayofweek", new StandardSQLFunction(Hibernate.INTEGER) );
		registerFunction("dayofweek_iso", new StandardSQLFunction(Hibernate.INTEGER) );
		registerFunction("dayofyear", new StandardSQLFunction(Hibernate.INTEGER) );
		registerFunction("days", new StandardSQLFunction(Hibernate.LONG) );
		registerFunction("time", new StandardSQLFunction(Hibernate.TIME) );
		registerFunction("timestamp", new StandardSQLFunction(Hibernate.TIMESTAMP) );
		registerFunction("timestamp_iso", new StandardSQLFunction(Hibernate.TIMESTAMP) );
		registerFunction("week", new StandardSQLFunction(Hibernate.INTEGER) );
		registerFunction("week_iso", new StandardSQLFunction(Hibernate.INTEGER) );
		registerFunction("year", new StandardSQLFunction(Hibernate.INTEGER) );	

		registerFunction("double", new StandardSQLFunction(Hibernate.DOUBLE) );
		registerFunction("varchar", new StandardSQLFunction(Hibernate.STRING) );
		registerFunction("real", new StandardSQLFunction(Hibernate.FLOAT) );
		registerFunction("bigint", new StandardSQLFunction(Hibernate.LONG) );
		registerFunction("char", new StandardSQLFunction(Hibernate.CHARACTER) );
		registerFunction("integer", new StandardSQLFunction(Hibernate.INTEGER) );
		registerFunction("smallint", new StandardSQLFunction(Hibernate.SHORT) );
		
		registerFunction("digits", new StandardSQLFunction(Hibernate.STRING) );
		registerFunction("chr", new StandardSQLFunction(Hibernate.CHARACTER) );
		registerFunction("upper", new StandardSQLFunction() );
		registerFunction("ucase", new StandardSQLFunction() );
		registerFunction("lcase", new StandardSQLFunction() );
		registerFunction("lower", new StandardSQLFunction() );
		registerFunction("length", new StandardSQLFunction(Hibernate.LONG) );
		registerFunction("ltrim", new StandardSQLFunction() );

		getDefaultProperties().setProperty(Environment.USE_OUTER_JOIN, "true");
		getDefaultProperties().setProperty(Environment.STATEMENT_BATCH_SIZE, NO_BATCH);
	}
	
	public String getAddColumnString() {
		return "add column";
	}
	public boolean dropConstraints() {
		return false;
	}
	public boolean supportsIdentityColumns() {
		return true;
	}
	public String getIdentitySelectString() {
		return "values IDENTITY_VAL_LOCAL()";
	}
	public String getIdentityColumnString() {
		return "not null generated by default as identity";
	}
	public String getIdentityInsertString() {
		return "default";
	}
	
	public String getSequenceNextValString(String sequenceName) {
		return "values nextval for " + sequenceName;
	}
	public String getCreateSequenceString(String sequenceName) {
		return "create sequence " + sequenceName;
	}
	public String getDropSequenceString(String sequenceName) {
		return "drop sequence " + sequenceName + " restrict";
	}
	
	public boolean supportsSequences() {
		return true;
	}

	public boolean supportsLimit() {
		return true;
	}

	public String getLimitString(String sql, boolean hasOffset) {
		StringBuffer rownumber = addRowNumberSql(sql);
		
		StringBuffer pagingSelect = new StringBuffer( sql.length()+100 )
					.append("select * from ( ");
		pagingSelect.append(sql)
			.insert( 22, rownumber.toString() );
		
		if (hasDistinct(sql))
		{
			pagingSelect.append(" ) as row_");
		}
		pagingSelect.append(" ) as temp_ where row_num_ ");
		if (hasOffset) {
			pagingSelect.append("between ?+1 and ?");
		}
		else {
			pagingSelect.append("<= ?");
		}
		return pagingSelect.toString();
	}

	private StringBuffer addRowNumberSql(String sql)
	{
		StringBuffer rownumber = new StringBuffer(50)
			.append(" rownumber() over(");
		int orderByIndex = sql.toLowerCase().indexOf("order by");
		
		if (orderByIndex>0 && !hasDistinct(sql))
		{
			rownumber.append( sql.substring(orderByIndex) );
		}
			 
		rownumber.append(") as row_num_");
		
		
		if (hasDistinct(sql))
		{
			rownumber.append(", row_.* from ( select");
		}
		else
		{
			rownumber.append(",");
		}
		return rownumber;
	}

	public boolean useMaxForLimit() {
		return true;
	}
	
	private static boolean hasDistinct(String sql)
	{
		return sql.startsWith("select distinct");
	}
	
	private static int getAfterSelectInsertPoint(String sql) {
		return 16 + (hasDistinct(sql)  ? 15 : 6 );
	}
}






