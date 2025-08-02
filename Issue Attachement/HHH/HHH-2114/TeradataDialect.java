package org.hibernate.dialect;

import java.sql.Types;
import java.util.StringTokenizer;

import org.hibernate.Hibernate;
import org.hibernate.MappingException;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.function.NoArgSQLFunction;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.dialect.function.VarArgsSQLFunction;
import org.hibernate.util.StringHelper;

/**
 * Teradata Dialect for Hibernate 3.1
 *
 * <p>
 * TODO:
 * <ul>
 * <li> Fix Hibernate generated CREATE INDEX syntax.
 * <li> Add all other Teradata functions.
 * </ul>
 * @author Mike Hazelwood
 */
public class TeradataDialect extends Dialect
{
    private static final String KEYWORDS = "A, ABORT, ABORTSESSION, ABS, ACCESS, ACCESS_LOCK, ACCOUNT, ACOS, ACOSH, ADA, ADD_MONTHS, " +
            "ADMIN, AFTER, AG, AGGREGATE, ALIAS, ALLOCATION, ALWAYS, AMP, ANALYSIS, ANSIDATE, ARGLPAREN, ARRAY, ASCII, ASENSITIVE, ASIN, " +
            "ASINH, ASSIGNMENT, ASYMMETRIC, ATAN, ATAN2, ATANH, ATOMIC, ATTR, ATTRIBUTE, ATTRIBUTES, ATTRS, AVE, AVERAGE, BEFORE, BERNOULLI, " +
            "BIGINT, BINARY, BLOB, BOOLEAN, BREADTH, BT, BUT, BYTE, BYTEINT, BYTES, C, CALL, CALLED, CARDINALITY, CASESPECIFIC, CASE_N, " +
            "CATALOG_NAME, CD, CEIL, CEILING, CHAIN, CHANGERATE, CHAR2HEXINT, CHARACTERISTICS, CHARACTERS, CHARACTER_SET_CATALOG, " +
            "CHARACTER_SET_NAME, CHARACTER_SET_SCHEMA, CHARS, CHARSET_COLL, CHECKPOINT, CHECKSUM, CLASS, CLASS_ORIGIN, CLOB, CLUSTER, " +
            "CM, COBOL, COLLATION_CATALOG, COLLATION_NAME, COLLATION_SCHEMA, COLLECT, COLUMNSPERINDEX, COLUMN_NAME, COMMAND_FUNCTION, " +
            "COMMAND_FUNCTION_CODE, COMMENT, COMMITTED, COMPILE, COMPRESS, CONDITION, CONDITION_NUMBER, CONNECTION_NAME, CONSTRAINT_CATALOG, " +
            "CONSTRAINT_NAME, CONSTRAINT_SCHEMA, CONSTRUCTOR, CONSUME, CONTAINS, CONVERT_TABLE_HEADER, CORR, COS, COSH, COSTS, COVAR_POP, " +
            "COVAR_SAMP, CPP, CPUTIME, CS, CSUM, CT, CUBE, CUME_DIST, CURRENT_DEFAULT_TRANSFORM_GROUP, CURRENT_PATH, CURRENT_ROLE, " +
            "CURRENT_TRANSFORM_GROUP_FOR_TYPE, CURSOR_NAME, CV, CYCLE, DATA, DATABASE, DATABLOCKSIZE, DATEFORM, DATETIME_INTERVAL_CODE, " +
            "DATETIME_INTERVAL_PRECISION, DBC, DEBUG, DEFAULTS, DEFINED, DEFINER, DEGREES, DEL, DEMOGRAPHICS, DENIALS, DENSE_RANK, DEPTH, " +
            "DEREF, DERIVED, DETERMINISTIC, DIAGNOSTIC, DIGITS, DISABLED, DISPATCH, DO, DUAL, DUMP, DYNAMIC, DYNAMIC_FUNCTION, " +
            "DYNAMIC_FUNCTION_CODE, EACH, EBCDIC, ECHO, ELEMENT, ELSEIF, ENABLED, ENCRYPT, EQ, EQUALS, ERROR, ERRORFILES, ERRORTABLES, " +
            "ET, EVERY, EXCL, EXCLUDE, EXCLUDING, EXCLUSIVE, EXIT, EXP, EXPIRE, EXPLAIN, FALLBACK, FASTEXPORT, FILTER, FINAL, FLOOR, " +
            "FOLLOWING, FORMAT, FORTRAN, FREE, FREESPACE, FUNCTION, FUSION, G, GE, GENERAL, GENERATED, GIVE, GRANTED, GRAPHIC, GROUPING, " +
            "GT, HANDLER, HASH, HASHAMP, HASHBAKAMP, HASHBUCKET, HASHROW, HELP, HIERARCHY, HIGH, HOLD, HOST, IF, IFP, IMPLEMENTATION, " +
            "INCLUDING, INCONSISTENT, INCREMENT, INDEX, INDEXESPERTABLE, INITIATE, INOUT, INS, INSTANCE, INSTANTIABLE, INSTEAD, " +
            "INTEGERDATE, INTERSECTION, INVOKER, IOCOUNT, ITERATE, JAVA, JIS_COLL, JOURNAL, K, KANJI1, KANJISJIS, KBYTE, KBYTES, KEEP, " +
            "KEY_MEMBER, KEY_TYPE, KILOBYTES, KURTOSIS, LARGE, LATERAL, LATIN, LE, LEAVE, LENGTH, LIMIT, LN, LOADING, LOCALTIME, " +
            "LOCALTIMESTAMP, LOCATOR, LOCK, LOCKEDUSEREXPIRE, LOCKING, LOG, LOGGING, LOGON, LONG, LOOP, LOW, LT, M, MACRO, MAP, MATCHED, " +
            "MAVG, MAXCHAR, MAXIMUM, MAXLOGONATTEMPTS, MAXVALUE, MCHARACTERS, MDIFF, MEDIUM, MEMBER, MERGE, MESSAGE_LENGTH, " +
            "MESSAGE_OCTET_LENGTH, MESSAGE_TEXT, METHOD, MINCHAR, MINDEX, MINIMUM, MINUS, MINVALUE, MLINREG, MLOAD, MOD, MODE, MODIFIED, " +
            "MODIFIES, MODIFY, MONITOR, MONRESOURCE, MONSESSION, MORE, MSUBSTR, MSUM, MULTINATIONAL, MULTISET, MUMPS, NAME, NAMED, NCLOB, " +
            "NE, NESTING, NEW, NEW_TABLE, NONE, NORMALIZE, NORMALIZED, NOWAIT, NULLABLE, NULLIFZERO, NULLS, NUMBER, OA, OBJECT, OBJECTS, " +
            "OCTETS, OFF, OLD, OLD_TABLE, OPTIONS, ORDERED_ANALYTIC, ORDERING, ORDINALITY, OTHERS, OUT, OVER, OVERLAY, OVERRIDE, " +
            "OVERRIDING, PARAMETER, PARAMETER_MODE, PARAMETER_NAME, PARAMETER_ORDINAL_POSITION, PARAMETER_SPECIFIC_CATALOG, " +
            "PARAMETER_SPECIFIC_NAME, PARAMETER_SPECIFIC_SCHEMA, PARTITION, PARTITIONED, PASCAL, PASSWORD, PATH, PERCENT, PERCENTILE_CONT, " +
            "PERCENTILE_DISC, PERCENT_RANK, PERM, PERMANENT, PLACING, PLI, POWER, PRECEDING, PRINT, PRIVATE, PROFILE, PROTECTED, " +
            "PROTECTION, QUALIFIED, QUALIFY, QUANTILE, QUERY, QUEUE, RADIANS, RANDOM, RANDOMIZED, RANGE, RANGE_N, RANK, READS, RECALC, " +
            "RECURSIVE, REF, REFERENCING, REGR_AVGX, REGR_AVGY, REGR_COUNT, REGR_INTERCEPT, REGR_R2, REGR_SLOPE, REGR_SXX, REGR_SXY, " +
            "REGR_SYY, RELEASE, RENAME, REPEAT, REPEATABLE, REPLACE, REPLACEMENT, REPLCONTROL, REPLICATION, REQUEST, RESTART, RESTORE, " +
            "RESULT, RESUME, RET, RETRIEVE, RETURN, RETURNED_CARDINALITY, RETURNED_LENGTH, RETURNED_OCTET_LENGTH, RETURNED_SQLSTATE, " +
            "RETURNS, REUSE, REVALIDATE, RIGHTS, ROLE, ROLLFORWARD, ROLLUP, ROUTINE, ROUTINE_CATALOG, ROUTINE_NAME, ROUTINE_SCHEMA, " +
            "ROW, ROWID, ROW_COUNT, ROW_NUMBER, SAMPLE, SAMPLEID, SAMPLES, SAVEPOINT, SCALE, SCHEMA_NAME, SCOPE, SCOPE_CATALOG, " +
            "SCOPE_NAME, SCOPE_SCHEMA, SEARCH, SEARCHSPACE, SECURITY, SEL, SELF, SENSITIVE, SEQUENCE, SERIALIZABLE, SERVER_NAME, " +
            "SETRESRATE, SETS, SETSESSRATE, SHARE, SHOW, SIMILAR, SIMPLE, SIN, SINH, SKEW, SOUNDEX, SOURCE, SPECCHAR, SPECIFIC, " +
            "SPECIFICTYPE, SPECIFIC_NAME, SPL, SPOOL, SQLEXCEPTION, SQLTEXT, SQLWARNING, SQRT, SS, START, STARTUP, STAT, STATE, " +
            "STATEMENT, STATIC, STATISTICS, STATS, STDDEV_POP, STDDEV_SAMP, STEPINFO, STRING_CS, STRUCTURE, STYLE, SUBCLASS_ORIGIN, " +
            "SUBMULTISET, SUBSCRIBER, SUBSTR, SUMMARY, SUMMARYONLY, SUSPEND, SYMMETRIC, SYSTEM, SYSTEMTEST, TABLESAMPLE, TABLE_NAME, " +
            "TAN, TANH, TARGET, TBL_CS, TD_GENERAL, TD_INTERNAL, TERMINATE, TEXT, THRESHOLD, TIES, TITLE, TOP, TOP_LEVEL_COUNT, TPA, " +
            "TRACE, TRANSACTIONS_COMMITTED, TRANSACTIONS_ROLLED_BACK, TRANSACTION_ACTIVE, TRANSFORM, TRANSFORMS, TRANSLATE_CHK, TREAT, " +
            "TRIGGER, TRIGGER_CATALOG, TRIGGER_NAME, TRIGGER_SCHEMA, TYPE, UC, UDTCASTAS, UDTCASTLPAREN, UDTMETHOD, UDTTYPE, UDTUSAGE, " +
            "UESCAPE, UNBOUNDED, UNCOMMITTED, UNDEFINED, UNDER, UNDO, UNICODE, UNNAMED, UNNEST, UNTIL, UPD, UPPERCASE, USE, " +
            "USER_DEFINED_TYPE_CATALOG, USER_DEFINED_TYPE_CODE, USER_DEFINED_TYPE_NAME, USER_DEFINED_TYPE_SCHEMA, VARBYTE, VARGRAPHIC, " +
            "VAR_POP, VAR_SAMP, VOLATILE, WAIT, WARNING, WHILE, WIDTH_BUCKET, WINDOW, WITHIN, WITHOUT, ZEROIFNULL";

    public TeradataDialect()
    {
        super();

        registerColumnType( Types.BIT, "BYTEINT");
        registerColumnType( Types.BIGINT, "INTEGER" );
        registerColumnType( Types.SMALLINT, "SMALLINT");
        registerColumnType( Types.TINYINT, "BYTEINT");
        registerColumnType( Types.INTEGER, "INTEGER" );
        registerColumnType( Types.NUMERIC, "NUMERIC(18)" );

        registerColumnType( Types.CHAR, "CHAR" );
        registerColumnType( Types.VARCHAR, 32000, "VARCHAR($l)" );

        registerColumnType( Types.FLOAT, "FLOAT" );
        registerColumnType( Types.REAL, "REAL");
        registerColumnType( Types.DOUBLE, "DOUBLE PRECISION" );

        registerColumnType( Types.DATE, "DATE" );
        registerColumnType( Types.TIME, "TIME" );
        registerColumnType( Types.TIMESTAMP, "TIMESTAMP" );

        registerColumnType( Types.VARBINARY, 64000, "VARBYTE($l)" );
        registerColumnType( Types.LONGVARBINARY, "VARBYTE(32000)" );

        registerColumnType( Types.BLOB, "BLOB" );
        registerColumnType( Types.CLOB, "CLOB" );


        registerFunction("lower", new StandardSQLFunction("lower") );
        registerFunction("upper", new StandardSQLFunction("upper") );
        registerFunction("trim", new StandardSQLFunction("trim") );

        registerFunction("abs", new StandardSQLFunction("abs") );
        registerFunction("cast", new StandardSQLFunction("cast") );
        registerFunction("ln", new StandardSQLFunction("ln", Hibernate.INTEGER) );
        registerFunction("log", new StandardSQLFunction("log", Hibernate.INTEGER) );
        registerFunction("sin", new StandardSQLFunction("sin", Hibernate.INTEGER) );
        registerFunction("sqrt", new StandardSQLFunction("sqrt", Hibernate.INTEGER) );
        registerFunction("tan", new StandardSQLFunction("tan", Hibernate.INTEGER) );
        registerFunction("acos", new StandardSQLFunction("acos", Hibernate.INTEGER) );
        registerFunction("asin", new StandardSQLFunction("asin", Hibernate.INTEGER) );
        registerFunction("atan", new StandardSQLFunction("atan", Hibernate.INTEGER) );
        registerFunction("cos", new StandardSQLFunction("cos", Hibernate.INTEGER) );
        registerFunction("exp", new StandardSQLFunction("exp", Hibernate.INTEGER) );
        registerFunction("substring", new StandardSQLFunction("substr", Hibernate.STRING));
        registerFunction("substr", new StandardSQLFunction("substr", Hibernate.STRING));
        registerFunction("format", new StandardSQLFunction("format", Hibernate.STRING));
        registerFunction("dayofyear",new StandardSQLFunction("dayofyear", Hibernate.INTEGER));
        registerFunction("char_length", new StandardSQLFunction("char_length", Hibernate.INTEGER) );
        registerFunction("character_length", new StandardSQLFunction("character_length", Hibernate.INTEGER) );
        registerFunction("soundex", new StandardSQLFunction("soundex") );
        registerFunction("radians", new StandardSQLFunction("radians", Hibernate.FLOAT) );
        registerFunction("degrees", new StandardSQLFunction("degrees", Hibernate.FLOAT) );
        registerFunction("date", new NoArgSQLFunction("date", Hibernate.DATE, false) );
        registerFunction("current_date", new NoArgSQLFunction("current_date", Hibernate.DATE, false) );
        registerFunction("time", new NoArgSQLFunction("time", Hibernate.TIME, false) );
        registerFunction("current_time", new NoArgSQLFunction("current_time", Hibernate.TIME, false) );
        registerFunction("current_timestamp", new NoArgSQLFunction("current_timestamp", Hibernate.TIMESTAMP, false) );
        registerFunction("random", new NoArgSQLFunction("rand", Hibernate.INTEGER) );
        registerFunction("concat", new VarArgsSQLFunction(Hibernate.STRING," ", " || ", " "));
        registerFunction("round", new SQLFunctionTemplate(Hibernate.BIG_DECIMAL, "CAST(?1 AS DECIMAL(18,0))"));
        registerFunction("floor", new SQLFunctionTemplate(Hibernate.INTEGER, "CAST(?1 AS INTEGER)"));

        parseKeywords();
    }


    private void parseKeywords() {
        StringTokenizer st = new StringTokenizer(KEYWORDS, ", ");
        while(st.hasMoreTokens()) {
            String kw = st.nextToken();
            registerKeyword(kw);
            registerKeyword(kw.toLowerCase());
        }
    }

    public boolean supportsSequences()
    {
        return false;
    }

    public boolean supportsIdentityColumns()
    {
        return true;
    }


    public boolean supportsUnique()
    {
        return true;
    }

    public String getIdentitySelectString(String table, String column, int type) throws MappingException
    {
        return "SELECT MAX("+column + ") FROM " + table;
    }


    public String getIdentityColumnString() {
        return "NOT NULL GENERATED BY DEFAULT AS IDENTITY (START WITH 1 INCREMENT BY 1 MINVALUE 1 MAXVALUE 999999999 NO CYCLE)";
    }

    public boolean supportsLimit() {
        return true;
    }


    public boolean supportsLimitOffset()
    {
        return true;
    }


    public boolean useMaxForLimit()
    {
        return true;
    }

    public String getLimitString(String sql, boolean hasOffset) {
        StringBuffer sb = new StringBuffer(sql.length()+50);
        sb.append(sql);
        int orderByIndex = sb.toString().toLowerCase().lastIndexOf("order by");
        if(orderByIndex>0) {
            // TODO this assumes that the ORDER BY is the last clause of the SQL statement
            String orderBy = sb.substring(orderByIndex);

            // the ORDER BY clause has to be included with in the row_number computation
            // and included again at the end so that the results within the page are sorted
            sb.insert(orderByIndex, " QUALIFY row_number() OVER( ");
            sb.append(" ) ");
            if(hasOffset) {
                sb.append(" between ? and ? ");
            } else {
                sb.append(" <= ? ");
            }
            sb.append(orderBy);
        } else {
            // no ORDER BY
            if(hasOffset) {
                sb.append(" QUALIFY sum(1) over (rows unbounded preceding) between ? and ? ");
            } else {
                sb.append(" QUALIFY sum(1) over (rows unbounded preceding) <=? ");
            }
        }
        return sb.toString();
    }

    public Class getNativeIdentifierGeneratorClass() {
        return org.hibernate.id.IdentityGenerator.class;
    }

    public boolean supportsUniqueConstraintInCreateAlterTable()
    {
        return true;
    }


    public String getAddColumnString() {
        return "add";
    }

    public String getAddForeignKeyConstraintString(String constraintName, String[] foreignKey,
            String referencedTable, String[] primaryKey, boolean referencesPrimaryKey)
    {
        String cols = StringHelper.join(", ", foreignKey);
        return new StringBuffer(30).append(" add constraint ").append(constraintName).append(
                        " foreign key (").append(cols).append(") references ").append(
                        referencedTable).append(" (").append(StringHelper.join(", ", primaryKey))
                .append(')').toString();
    }

    public String getDropForeignKeyString() {
        return " drop constraint ";
    }


    public char closeQuote() {
        return '\'';
    }

    public char openQuote() {
        return '\'';
    }

    public boolean supportsTemporaryTables() {
        return true;
    }

    public String getCreateTemporaryTableString() {
        return "create volatile table";
    }

    public boolean supportsCurrentTimestampSelection() {
        return true;
    }

    public boolean isCurrentTimestampSelectStringCallable() {
        return false;
    }

    public String getCurrentTimestampSelectString() {
        return "select current_timestamp";
    }
}
