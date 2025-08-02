/*
 * Created on Aug 24, 2005
 * This is the Hibernate dialect for the Unisys 2200 Relational Database (RDMS).
 * This dialect was developed for use with Hibernate 3.0.5. Other versions may
 * require modifications to the dialect.
 *
 * Version History:
 * Also change the version displayed below in the constructor
 * 1.0  2005-10-24  CDH - First dated version for use with CP 11 (in Hib. 3.1)
 * 1.1  2006-02-23  CDH - Update the RegisterFunction list
 * 1.2
 */
package org.hibernate.dialect;

import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.function.NoArgSQLFunction;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.dialect.function.SQLFunctionTemplate;
/*
import org.hibernate.exception.ErrorCodeConverter;
import org.hibernate.exception.SQLExceptionConverter;
import org.hibernate.exception.ViolatedConstraintNameExtracter;
*/

import java.sql.Types;
import org.hibernate.Hibernate;
import org.hibernate.sql.CaseFragment;
import org.hibernate.sql.DecodeCaseFragment;
import org.apache.commons.logging.Log; 
import org.apache.commons.logging.LogFactory;


/**
 * @author Ploski and Hanson
 */
public class RDMSOS2200Dialect extends Dialect {
	
	private static Log log = LogFactory.getLog(RDMSOS2200Dialect.class); 

	public RDMSOS2200Dialect() {
		super();
        // Display the dialect version.
		log.info("RDMSOS2200Dialect version: 1.1");
		
        /**
         * This section registers RDMS Biult-in Functions (BIFs) with Hibernate.
         * The first parameter is the 'register' function name with Hibernate.
         * The second parameter is the defined RDMS SQL Function and it's
         * characteristics. If StandardSQLFunction(...) is used, the RDMS BIF
         * name and the return type (if any) is specified.  If
         * SQLFunctionTemplate(...) is used, the return type and a template
         * string is provided, plus an optional hasParenthesesIfNoArgs flag. 
         */
        
		registerFunction("abs", new StandardSQLFunction("abs") );
        registerFunction("absval", new StandardSQLFunction("absval") );
        registerFunction("acos", new StandardSQLFunction("acos", Hibernate.DOUBLE) );
        registerFunction("active_version", new NoArgSQLFunction("active_version", Hibernate.STRING) );
        registerFunction("add_months", new StandardSQLFunction("add_months") );
        registerFunction("ascii", new StandardSQLFunction("ascii", Hibernate.INTEGER) );
        registerFunction("asin", new StandardSQLFunction("asin", Hibernate.DOUBLE) );
        registerFunction("atan", new StandardSQLFunction("atan", Hibernate.DOUBLE) );
        registerFunction("atan2", new StandardSQLFunction("atan2",Hibernate.DOUBLE) );
        registerFunction("cast", new StandardSQLFunction("cast") );
        registerFunction("ceil", new StandardSQLFunction("ceil") );
        registerFunction("ceiling", new StandardSQLFunction("ceiling") );
        registerFunction("char", new StandardSQLFunction("char", Hibernate.CHARACTER) );
        registerFunction("char_length", new StandardSQLFunction("char_length", Hibernate.INTEGER) );
        registerFunction("character_length", new StandardSQLFunction("character_length", Hibernate.INTEGER) );
        registerFunction("charindex", new StandardSQLFunction("charindex", Hibernate.INTEGER) );
        registerFunction("chr", new StandardSQLFunction("chr", Hibernate.CHARACTER) );
        registerFunction("coalesce", new StandardSQLFunction("coalesce") );
        // The RDMS concat() function only supports 2 parameters
        registerFunction("concat", new SQLFunctionTemplate(Hibernate.STRING, "concat(?1, ?2)") );
        registerFunction("concatrtrim", new StandardSQLFunction("concatrtrim",Hibernate.STRING) );
        registerFunction("concatstrim", new StandardSQLFunction("concatstrim",Hibernate.STRING) );
        registerFunction("convert", new StandardSQLFunction("convert",Hibernate.STRING) );
        registerFunction("cos", new StandardSQLFunction("cos", Hibernate.DOUBLE) );
        registerFunction("cosh", new StandardSQLFunction("cosh", Hibernate.DOUBLE) );
        registerFunction("cot", new StandardSQLFunction("cot", Hibernate.DOUBLE) );
        registerFunction("curdate", new NoArgSQLFunction("curdate",Hibernate.DATE) );
        registerFunction("current_date", new NoArgSQLFunction("current_date",Hibernate.DATE, false) );
        registerFunction("curtime", new NoArgSQLFunction("curtime",Hibernate.TIME) );
        registerFunction("current_time", new NoArgSQLFunction("current_time",Hibernate.TIME, false) );
        registerFunction("current_timestamp", new NoArgSQLFunction("current_timestamp", Hibernate.TIMESTAMP, false) );
        registerFunction("datalength", new StandardSQLFunction("datalength", Hibernate.INTEGER) );
        registerFunction("dateadd", new StandardSQLFunction("dateadd") );
        registerFunction("datediff", new StandardSQLFunction("datediff", Hibernate.INTEGER) );
        registerFunction("datename", new StandardSQLFunction("datename",Hibernate.STRING) );
        registerFunction("datepart", new StandardSQLFunction("datepart", Hibernate.INTEGER) );
        registerFunction("dayname", new StandardSQLFunction("dayname",Hibernate.STRING) );
        registerFunction("dayofmonth", new StandardSQLFunction("dayofmonth",Hibernate.INTEGER) );
        registerFunction("dayofweek", new StandardSQLFunction("dayofweek",Hibernate.INTEGER) );
        registerFunction("dayofyear", new StandardSQLFunction("dayofyear",Hibernate.INTEGER) );
        registerFunction("days", new StandardSQLFunction("days",Hibernate.INTEGER) );
        registerFunction("decode", new StandardSQLFunction("decode",Hibernate.INTEGER) );
        registerFunction("degree", new StandardSQLFunction("degree", Hibernate.DOUBLE) );
        registerFunction("degrees", new StandardSQLFunction("degrees", Hibernate.DOUBLE) );
        registerFunction("difference", new StandardSQLFunction("difference",Hibernate.INTEGER) );
        registerFunction("exp", new StandardSQLFunction("exp", Hibernate.DOUBLE) );
        registerFunction("floor", new StandardSQLFunction("floor") );
        registerFunction("getdate", new StandardSQLFunction("getdate") );
        registerFunction("hour", new StandardSQLFunction("hour",Hibernate.INTEGER) );
        registerFunction("ifnull", new StandardSQLFunction("ifnull") );
        registerFunction("initcap", new StandardSQLFunction("initcap", Hibernate.STRING) );
        registerFunction("insert", new StandardSQLFunction("insert", Hibernate.STRING) );
        registerFunction("instr", new StandardSQLFunction("instr", Hibernate.INTEGER) );
        registerFunction("isalnum", new StandardSQLFunction("isalnum", Hibernate.INTEGER) );
        registerFunction("isalpha", new StandardSQLFunction("isalpha", Hibernate.INTEGER) );
        registerFunction("iscntrl", new StandardSQLFunction("iscntrl", Hibernate.INTEGER) );
        registerFunction("isdigit", new StandardSQLFunction("isdigit", Hibernate.INTEGER) );
        registerFunction("isgraph", new StandardSQLFunction("isgraph", Hibernate.INTEGER) );
        registerFunction("islower", new StandardSQLFunction("islower", Hibernate.INTEGER) );
        registerFunction("isprint", new StandardSQLFunction("isprint", Hibernate.INTEGER) );
        registerFunction("ispunct", new StandardSQLFunction("ispunct", Hibernate.INTEGER) );
        registerFunction("isspace", new StandardSQLFunction("isspace", Hibernate.INTEGER) );
        registerFunction("isupper", new StandardSQLFunction("isupper", Hibernate.INTEGER) );
        registerFunction("isxdigit", new StandardSQLFunction("isxdigit", Hibernate.INTEGER) );
        registerFunction("julian_day", new StandardSQLFunction("julian_day",Hibernate.INTEGER) );
        registerFunction("last_day", new StandardSQLFunction("last_day",Hibernate.INTEGER) );
        registerFunction("left", new StandardSQLFunction("left", Hibernate.STRING) );
        registerFunction("lcase", new StandardSQLFunction("lcase", Hibernate.STRING) );
        registerFunction("left", new StandardSQLFunction("left", Hibernate.STRING) );
        registerFunction("length", new StandardSQLFunction("length", Hibernate.INTEGER) );
        registerFunction("ln", new StandardSQLFunction("ln", Hibernate.DOUBLE) );
        registerFunction("lob_crc", new StandardSQLFunction("lob_crc", Hibernate.BIG_INTEGER) );
        registerFunction("lob_end_address", new StandardSQLFunction("lob_end_address", Hibernate.INTEGER) );
        registerFunction("lob_end_page", new StandardSQLFunction("lob_end_page", Hibernate.INTEGER) );
        registerFunction("lob_file", new StandardSQLFunction("lob_file", Hibernate.STRING) );
        registerFunction("lob_id", new StandardSQLFunction("lob_id", Hibernate.STRING) );
        registerFunction("lob_id_crc", new StandardSQLFunction("lob_id_crc", Hibernate.BIG_INTEGER) );
        registerFunction("lob_start_address", new StandardSQLFunction("lob_start_address", Hibernate.INTEGER) );
        registerFunction("lob_start_page", new StandardSQLFunction("lob_start_page", Hibernate.INTEGER) );
        registerFunction("locate", new StandardSQLFunction("locate", Hibernate.INTEGER) );
        registerFunction("log", new StandardSQLFunction("log", Hibernate.DOUBLE) );
        registerFunction("log10", new StandardSQLFunction("log10", Hibernate.DOUBLE) );
        registerFunction("lower", new StandardSQLFunction("lower", Hibernate.STRING) );
        registerFunction("lpad", new StandardSQLFunction("lpad", Hibernate.STRING) );
        registerFunction("ltrim", new StandardSQLFunction("ltrim", Hibernate.STRING) );
        registerFunction("microsecond", new StandardSQLFunction("microsecond",Hibernate.INTEGER) );
        registerFunction("midnight_seconds", new StandardSQLFunction("midnight_seconds",Hibernate.INTEGER) );
        registerFunction("minute", new StandardSQLFunction("minute",Hibernate.INTEGER) );
        registerFunction("mod", new StandardSQLFunction("mod",Hibernate.INTEGER) );
        registerFunction("month", new StandardSQLFunction("month",Hibernate.INTEGER) );
        registerFunction("monthname", new StandardSQLFunction("monthname",Hibernate.STRING) );
        registerFunction("months_between", new StandardSQLFunction("months_between",Hibernate.DOUBLE) );
        registerFunction("new_time", new StandardSQLFunction("new_time") );
        registerFunction("next_day", new StandardSQLFunction("next_day") );
        registerFunction("now", new NoArgSQLFunction("now",Hibernate.TIMESTAMP) );
        registerFunction("nvl", new StandardSQLFunction("nvl") );
        registerFunction("octet_length", new StandardSQLFunction("octet_length", Hibernate.INTEGER) );
        registerFunction("patindex", new StandardSQLFunction("patindex", Hibernate.INTEGER) );
        registerFunction("pi", new NoArgSQLFunction("pi", Hibernate.DOUBLE) );
        registerFunction("position", new StandardSQLFunction("position", Hibernate.INTEGER) );
        registerFunction("posstr", new StandardSQLFunction("posstr", Hibernate.INTEGER) );
        registerFunction("power", new StandardSQLFunction("power", Hibernate.DOUBLE) );
        registerFunction("quarter", new StandardSQLFunction("quarter",Hibernate.INTEGER) );
        registerFunction("radians", new StandardSQLFunction("radians", Hibernate.DOUBLE) );
        registerFunction("rand", new NoArgSQLFunction("rand", Hibernate.DOUBLE) );
        registerFunction("repeat", new StandardSQLFunction("repeat", Hibernate.STRING) );
        registerFunction("replace", new StandardSQLFunction("replace", Hibernate.STRING) );
        registerFunction("replicate", new StandardSQLFunction("replicate", Hibernate.STRING) );
        registerFunction("reverse", new StandardSQLFunction("reverse", Hibernate.STRING) );
        registerFunction("right", new StandardSQLFunction("right", Hibernate.STRING) );
        registerFunction("round", new StandardSQLFunction("round") );
        registerFunction("rpad", new StandardSQLFunction("rpad", Hibernate.STRING) );
        registerFunction("rtrim", new StandardSQLFunction("rtrim", Hibernate.STRING) );
        registerFunction("second", new StandardSQLFunction("second",Hibernate.INTEGER) );
        registerFunction("sign", new StandardSQLFunction("sign", Hibernate.INTEGER) );
        registerFunction("sin", new StandardSQLFunction("sin", Hibernate.DOUBLE) );
        registerFunction("sinh", new StandardSQLFunction("sinh", Hibernate.DOUBLE) );
        registerFunction("soundex", new StandardSQLFunction("soundex", Hibernate.STRING) );
        registerFunction("space", new StandardSQLFunction("space", Hibernate.STRING) );
        registerFunction("sqrt", new StandardSQLFunction("sqrt", Hibernate.DOUBLE) );
        registerFunction("str", new StandardSQLFunction("str", Hibernate.STRING) );
        registerFunction("strftime", new StandardSQLFunction("strftime", Hibernate.STRING) );
        registerFunction("strptime", new StandardSQLFunction("strptime", Hibernate.TIMESTAMP) );
        registerFunction("stuff", new StandardSQLFunction("stuff", Hibernate.STRING) );
        registerFunction("substr", new StandardSQLFunction("substr", Hibernate.STRING) );
        registerFunction("substring", new StandardSQLFunction("substring", Hibernate.STRING) );
        registerFunction("tan", new StandardSQLFunction("tan", Hibernate.DOUBLE) );
        registerFunction("tanh", new StandardSQLFunction("tanh", Hibernate.DOUBLE) );
        registerFunction("time", new StandardSQLFunction("time",Hibernate.TIME) );
        registerFunction("timestamp", new StandardSQLFunction("timestamp",Hibernate.TIMESTAMP) );
        registerFunction("timestampadd", new StandardSQLFunction("timestampadd",Hibernate.TIMESTAMP) );
        registerFunction("timestampdiff", new StandardSQLFunction("timestampdiff",Hibernate.INTEGER) );
        registerFunction("to_chr", new StandardSQLFunction("to_chr",Hibernate.STRING) );
        // RDMS does not directly support the trim() function, we use rtrim() and ltrim() 
        registerFunction("trim", new SQLFunctionTemplate(Hibernate.INTEGER, "ltrim(rtrim(?1))" ) );
        registerFunction("trunc", new StandardSQLFunction("trunc") );
        registerFunction("truncate", new StandardSQLFunction("truncate") );
        registerFunction("ucase", new StandardSQLFunction("ucase", Hibernate.STRING) );
        registerFunction("upper", new StandardSQLFunction("upper", Hibernate.STRING) );
        registerFunction("user", new NoArgSQLFunction("user", Hibernate.STRING, false) );
        registerFunction("value", new StandardSQLFunction("value") );
        registerFunction("week", new StandardSQLFunction("week",Hibernate.INTEGER) );
        registerFunction("year", new StandardSQLFunction("year",Hibernate.INTEGER) );

		/**
		 * For a list of column types to register, see section A-1
		 * in 7862 7395, the Unisys JDBC manual.
		 * 
		 * Here are column sizes as documented in Table A-1 of 
		 * 7831 0760, "Enterprise Relational Database Server
		 * for ClearPath OS2200 Administration Guide"
		 * Numeric - 21
		 * Decimal - 22 (21 digits plus one for sign)
		 * Float   - 60 bits
		 * Char    - 28000
		 * NChar   - 14000
		 * BLOB+   - 4294967296 (4 Gb)
		 * + RDMS JDBC driver does not support BLOBs
		 * 
		 * DATE, TIME and TIMESTAMP literal formats are
		 * are all described in section 2.3.4 DATE Literal Format
		 * in 7830 8160.  
		 * The DATE literal format is: YYYY-MM-DD
		 * The TIME literal format is: HH:MM:SS[.[FFFFFF]]
		 * The TIMESTAMP literal format is: YYYY-MM-DD HH:MM:SS[.[FFFFFF]]
		 * 
		 * Note that $l (dollar-L) will use the length value if provided.
		 * Also new for Hibernate3 is the $p percision and $s (scale) parameters
		 */
		registerColumnType(Types.BIT, "SMALLINT");
		registerColumnType(Types.TINYINT, "SMALLINT");
		registerColumnType(Types.BIGINT, "NUMERIC(21,0)");
		registerColumnType(Types.SMALLINT, "SMALLINT");
		registerColumnType(Types.CHAR, "CHARACTER(1)");
		registerColumnType(Types.DOUBLE, "DOUBLE PRECISION");
		registerColumnType(Types.FLOAT, "FLOAT");
		registerColumnType(Types.REAL, "REAL");
		registerColumnType(Types.INTEGER, "INTEGER");
		registerColumnType(Types.NUMERIC, "NUMERIC(21,$l)");
		registerColumnType(Types.DECIMAL, "NUMERIC(21,$l)");
		registerColumnType(Types.DATE, "DATE");
		registerColumnType(Types.TIME, "TIME");
		registerColumnType(Types.TIMESTAMP, "TIMESTAMP");
		registerColumnType(Types.VARCHAR, "CHARACTER($l)");
        registerColumnType(Types.BLOB, "BLOB($l)" );
        /*
         * The following types are not supported in RDMS/JDBC and therefore commented out.
         * However, in some cases, mapping them to CHARACTER columns works
         * for many applications, but does not work for all cases.
         */
        // registerColumnType(Types.VARBINARY, "CHARACTER($l)");
        // registerColumnType(Types.BLOB, "CHARACTER($l)" );  // For use prior to CP 11.0
        // registerColumnType(Types.CLOB, "CHARACTER($l)" );
	}

    // The following methods over-ride the default behaviour in the Dialect object.

    /**
     * RDMS does not support qualifing index names with the schema name.
     */
	public boolean qualifyIndexName() {
		return false;
	}

	/**
	 * The RDMS DB supports the 'FOR UPDATE OF' clause. However, the RDMS-JDBC
     * driver does not support this feature, so a false is return.
     * The base dialect also returns a false, but we will leave this over-ride
     * in to make sure it stays false.
	 */
	public boolean forUpdateOfColumns() {
		return false;
	}

	/**
	 * Since the RDMS-JDBC driver does not support for updates, this string is
     * set to an empty string. Whenever, the driver does support this feature,
     * the returned string should be " FOR UPDATE OF". Note that RDMS does not
     * support the string 'FORE UPDATE' string.
	 */
	public String getForUpdateString() {
		return ""; // Original Dialect.java returns " for update";
	}
	
    /**
     * RDMS does not support adding Unique constraints via create and alter table.
     */
	public boolean supportsUniqueConstraintInCreateAlterTable() {
	    return true;
	}
	
	// Verify the state of this new method in Hibernate 3.0 Dialect.java
    /**
     * RDMS does not support Cascade Deletes.
     * Need to review this in the future when support is provided. 
     */
		public boolean supportsCascadeDelete() {
		return false; // Origial Dialect.java returns true;
	}

	/**
     * Currently, RDMS-JDBC does not support ForUpdate.
     * Need to review this in the future when support is provided. 
	 */
    public boolean supportsOuterJoinForUpdate() {
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
//    public SQLExceptionConverter buildSQLExceptionConverter() {
//        return new ExceptionConverter( getViolatedConstraintNameExtracter() );
//    }
//
//    private static class ExceptionConverter extends ErrorCodeConverter {
//        private int[] sqlGrammarCodes = new int[] { 1054, 1064, 1146 };
//        private int[] integrityViolationCodes = new int[] { 1062, 1216, 1217 };
//        private int[] connectionCodes = new int[] { 1049 };
//        private int[] lockAcquisitionErrorCodes = new int[] { 1099, 1100, 1150, 1165, 1192, 1205, 1206, 1207, 1213, 1223 };
//
//        public ExceptionConverter(ViolatedConstraintNameExtracter extracter) {
//            super(extracter);
//        }
//
//        protected int[] getSQLGrammarErrorCodes() {
//            return sqlGrammarCodes;
//        }
//
//        protected int[] getIntegrityViolationErrorCodes() {
//            return integrityViolationCodes;
//        }
//
//        protected int[] getConnectionErrorCodes() {
//            return connectionCodes;
//        }
//
//        protected int[] getLockAcquisitionErrorCodes() {
//            return lockAcquisitionErrorCodes;
//        }
//    }
  

	public String getAddColumnString() {
		return "add";
	}	

	public String getNullColumnString() {
		// The keyword used to specify a nullable column.
		return " null";
	}

    // *** Sequence methods - start. The RDMS dialect needs these
    // methods to make it possible to use the Native Id generator
	public boolean supportsSequences() {
		return true;
	}
	
	public String getSequenceNextValString(String sequenceName) {
	    // The where clause was added to eliminate this statement from Brute Force Searches.
        return  "select permuted_id('NEXT',31) from rdms.rdms_dummy where key_col = 1 ";
	}
	
	public String getCreateSequenceString(String sequenceName) {
        // We must return a valid RDMS/RSA command from this method to
        // prevent RDMS/RSA from issuing *ERROR 400
        return "";
	}
	
	public String getDropSequenceString(String sequenceName) {
        // We must return a valid RDMS/RSA command from this method to
        // prevent RDMS/RSA from issuing *ERROR 400
        return "";
	}
	
	// *** Sequence methods - end	
	
    public String getCascadeConstraintsString() {
        // Used with DROP TABLE to delete all records in the table.
        return " including contents";
    }
	
	public CaseFragment createCaseFragment() {
		return new DecodeCaseFragment();
	}
	
	public boolean supportsLimit() { 
		return true; 
	} 

	public boolean supportsLimitOffset() { 
		return false; 
	} 

    public String getLimitString(String sql, int offset, int limit) {
        if (offset>0) throw new UnsupportedOperationException("RDMS does not support paged queries");
		return new StringBuffer(sql.length() + 40) 
			.append(sql) 
			.append(" fetch first ") 
			.append(limit) 
			.append(" rows only ") 
			.toString(); 
	} 

	public boolean supportsVariableLimit() { 
		return false;
	}

	public boolean supportsUnionAll() {
		// RDMS supports the UNION ALL clause.
          return true;
	}
	
}   // End of class RDMSOS2200Dialect
