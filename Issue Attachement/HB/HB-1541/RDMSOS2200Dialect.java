/*
 * Created on Jan 8, 2005
 * This is the Hibernate dialect for the Unisys 2200 Relational Database (RDMS).
 * This dialect was developed for use with Hibernate 2.1.6. Other versions may
 * require modifications to the dialect.
 *
 * Version History:
 * Also change the version displayed below in the constructor
 * 1.3
 * 1.2  2005-10-24  CDH - Cleanup
 * 1.1  2005-08-03  CDH - Update the getSequenceNextValString() method
 * 1.0  2005-03-11  CDH - First dated version for use with RTC PoC v4.2
 */
package net.sf.hibernate.dialect;

import java.sql.Types;
import net.sf.hibernate.Hibernate;
import net.sf.hibernate.sql.CaseFragment;
import net.sf.hibernate.sql.DecodeCaseFragment;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * @author Ploski and Hanson
 *
 */
public class RDMSOS2200Dialect extends Dialect {

    private static Log log = LogFactory.getLog(RDMSOS2200Dialect.class); 

    public RDMSOS2200Dialect() {
        super();
        // Display the dialect version if logging is enabled.
        // Display the dialect version.
        log.info("RDMSOS2200Dialect version: 1.2");

        
        registerFunction("abs", new StandardSQLFunction() );
        registerFunction("sign", new StandardSQLFunction(Hibernate.INTEGER) );

        registerFunction("ascii", new StandardSQLFunction(Hibernate.INTEGER) );
        registerFunction("char_length", new StandardSQLFunction(Hibernate.INTEGER) );
        registerFunction("character_length", new StandardSQLFunction(Hibernate.INTEGER) );
        registerFunction("length", new StandardSQLFunction(Hibernate.INTEGER) );

        registerFunction("concat", new StandardSQLFunction(Hibernate.STRING) );
        registerFunction("instr", new StandardSQLFunction(Hibernate.STRING) );
        registerFunction("lpad", new StandardSQLFunction(Hibernate.STRING) );
        registerFunction("replace", new StandardSQLFunction(Hibernate.STRING) );
        registerFunction("rpad", new StandardSQLFunction(Hibernate.STRING) );
        registerFunction("substr", new StandardSQLFunction(Hibernate.STRING) );

        registerFunction("lcase", new StandardSQLFunction() );
        registerFunction("lower", new StandardSQLFunction() );
        registerFunction("ltrim", new StandardSQLFunction() );
        registerFunction("reverse", new StandardSQLFunction() );
        registerFunction("rtrim", new StandardSQLFunction() );
        registerFunction("soundex", new StandardSQLFunction() );
        registerFunction("space", new StandardSQLFunction(Hibernate.STRING) );
        registerFunction("ucase", new StandardSQLFunction() );
        registerFunction("upper", new StandardSQLFunction() );

        registerFunction("acos", new StandardSQLFunction(Hibernate.DOUBLE) );
        registerFunction("asin", new StandardSQLFunction(Hibernate.DOUBLE) );
        registerFunction("atan", new StandardSQLFunction(Hibernate.DOUBLE) );
        registerFunction("cos", new StandardSQLFunction(Hibernate.DOUBLE) );
        registerFunction("cosh", new StandardSQLFunction(Hibernate.DOUBLE) );
        registerFunction("cot", new StandardSQLFunction(Hibernate.DOUBLE) );
        registerFunction("exp", new StandardSQLFunction(Hibernate.DOUBLE) );
        registerFunction("ln", new StandardSQLFunction(Hibernate.DOUBLE) );
        registerFunction("log", new StandardSQLFunction(Hibernate.DOUBLE) );
        registerFunction("log10", new StandardSQLFunction(Hibernate.DOUBLE) );
        registerFunction("pi", new NoArgSQLFunction(Hibernate.DOUBLE) );
        registerFunction("rand", new NoArgSQLFunction(Hibernate.DOUBLE) );
        registerFunction("sin", new StandardSQLFunction(Hibernate.DOUBLE) );
        registerFunction("sinh", new StandardSQLFunction(Hibernate.DOUBLE) );
        registerFunction("sqrt", new StandardSQLFunction(Hibernate.DOUBLE) );
        registerFunction("tan", new StandardSQLFunction(Hibernate.DOUBLE) );
        registerFunction("tanh", new StandardSQLFunction(Hibernate.DOUBLE) );

        registerFunction("round", new StandardSQLFunction() );
        registerFunction("trunc", new StandardSQLFunction() );
        registerFunction("ceil", new StandardSQLFunction() );
        registerFunction("floor", new StandardSQLFunction() );

        registerFunction("chr", new StandardSQLFunction(Hibernate.CHARACTER) );
        registerFunction("initcap", new StandardSQLFunction() );

        registerFunction( "user", new NoArgSQLFunction(Hibernate.STRING, false) );

        registerFunction("curdate", new NoArgSQLFunction(Hibernate.DATE) );
        registerFunction("curtime", new NoArgSQLFunction(Hibernate.TIME) );
        registerFunction("days", new StandardSQLFunction(Hibernate.INTEGER) );
        registerFunction("dayofmonth", new StandardSQLFunction(Hibernate.INTEGER) );
        registerFunction("dayname", new StandardSQLFunction(Hibernate.STRING) );
        registerFunction("dayofweek", new StandardSQLFunction(Hibernate.INTEGER) );
        registerFunction("dayofyear", new StandardSQLFunction(Hibernate.INTEGER) );
        registerFunction("hour", new StandardSQLFunction(Hibernate.INTEGER) );

        // In RDMS, "last_day" really does include an underscore character in its name.
        registerFunction("last_day", new StandardSQLFunction(Hibernate.DATE) );
        registerFunction("microsecond", new StandardSQLFunction(Hibernate.INTEGER) );
        registerFunction("minute", new StandardSQLFunction(Hibernate.INTEGER) );
        registerFunction("month", new StandardSQLFunction(Hibernate.INTEGER) );
        registerFunction("monthname", new StandardSQLFunction(Hibernate.STRING) );
        registerFunction("now", new NoArgSQLFunction(Hibernate.TIMESTAMP) );
        registerFunction("quarter", new StandardSQLFunction(Hibernate.INTEGER) );
        registerFunction("second", new StandardSQLFunction(Hibernate.INTEGER) );
        registerFunction("time", new StandardSQLFunction(Hibernate.TIME) );
        registerFunction("timestamp", new StandardSQLFunction(Hibernate.TIMESTAMP) );
        registerFunction("week", new StandardSQLFunction(Hibernate.INTEGER) );
        registerFunction("year", new StandardSQLFunction(Hibernate.INTEGER) );
        registerFunction("atan2", new StandardSQLFunction(Hibernate.DOUBLE) );
        registerFunction( "mod", new StandardSQLFunction(Hibernate.INTEGER) );
        registerFunction( "nvl", new StandardSQLFunction() );
        registerFunction( "power", new StandardSQLFunction(Hibernate.DOUBLE) );


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
         */
        // Note that $l (dollar-L) will use the length value if provided.
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
    }

    // The following methods over-ride the default behaviour in the Dialect object.

    public boolean qualifyIndexName() {
        // Not supported by RDMS
        return false;
    }

    public boolean supportsForUpdate() {
        // RDMS does not support this function.
        // We can modify this function to return TRUE after a
        // change is made to RDMS.
        return false;
    }

//    public boolean supportsForUpdateOf() {
//        // While RDMS supports this, The JDBC driver does not.
//        return false;
//    }

    public String getAddColumnString() {
        return "add";
    }

    public String getNullColumnString() {
        // The keyword used to specify a nullable column.
        return " null";
    }

    public boolean supportsSequences() {
        return true;
    }

    // *** Sequence methods - start. The RDMS dialect needs these
    // methods to make it possible to use the Native Id generator
    public String getSequenceNextValString(String sequenceName) {
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

    public String getLimitString(String sql, boolean hasOffset, int limit) {
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

    public boolean useMaxForLimit() {
        return true;
    }

}   // End of class RDMSOS2200Dialect
