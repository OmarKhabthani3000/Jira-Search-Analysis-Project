package put.your.package.name.here;

import java.sql.Types;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.dialect.SQLServerDialect;

/**
 * This class extends the Hibernate <code>SQLServerDialect</code>.  It essentially corrects
 * deficiencies in the Hibernate code that are either bugs or serious performance problems.
 * <p>
 * The problem that was noticed with the Hibernate <code>SQLServerDialect</code> was that 
 * every time a new query was issued against the SQL Server 2005 database with "TOP" and a 
 * value (ex. <code>SELECT TOP 125</code>....), the database thought this was a new query and would recompile
 * the plan for the prepared statement.  This caused significant performance problems, especially 
 * under load.  To correct this, the <code>BindingSqlServerDialect</code> now uses a bind parameter
 * to send the value of the TOP into the prepared statement.  The database now recognizes the
 * statement and goes to the cache for its execution plan, as opposed to always recompiling.
 * <p>
 * This class also registers a decimal column and Hibernate type.
 * 
 * @author Ed Wallen
 */
public class BindingSqlServerDialect extends SQLServerDialect {
    
    /**
     * Commons logger.
     */
    private static Log logger = LogFactory.getLog(BindingSqlServerDialect.class);
    
    /**
     * Constant string value for adding TOP bind parameter (" TOP (?)").
     */
    private static final String TOP_STATEMENT = " TOP (?)";
	
    /**
     * Default constructor.  Registers decimal types.
     */
    public BindingSqlServerDialect() {
        super();
        registerColumnType( Types.DECIMAL, "number($p,$s)" );
        registerHibernateType(Types.DECIMAL, "big_decimal");
    }  
    
    /**
     * This method determines the point at which to insert the 
     * TOP command.  It bases it on the position of the select and the
     * distinct keyword (if it exists).  This is pulled in to this class
     * from the <code>SQLServerDialect</code>, because it was incorrectly 
     * limited in terms of visibility by the Hibernate code.
     * 
     * @param sql The SQL string.
     * @return The position at which the insert should take place.
     */
    protected static int getAfterSelectInsertPoint(String sql) {
    	final String lowercaseSql = sql.toLowerCase();
		int selectIndex = lowercaseSql.indexOf( "select" );
		final int selectDistinctIndex = lowercaseSql.indexOf( "select distinct" );
		return selectIndex + ( selectDistinctIndex == selectIndex ? 15 : 6 );
	}
    
    /**
     * This method overrides that of the <code>SQLServerDialect</code> in order to 
     * pass the TOP value as a bind parameter (ex. the super class would create 
     * "TOP (250)" whereas this method is going to return "TOP (?)").  This provide 
     * a significant performance increase over the <code>SQLServerDialect</code> 
     * implementation.
     * 
     * @param querySelect The SQL statement to base the limit query off of.
	 * @param offset Offset of the first row to be returned by the query (zero-based).
	 * @param limit Maximum number of rows to be returned by the query.
	 * @return A new SQL statement with the LIMIT clause (in this case, TOP (?)) applied.
	 * @throws UnsupportedOperationException if the offset is greater than 0 (this implementation
	 * does not support offset).
     */
    public String getLimitString(String querySelect, int offset, int limit) {
    	
    	if ( offset > 0 ) {
			throw new UnsupportedOperationException( "SQL Server has no offset" );
		}
    	
    	StringBuffer result = new StringBuffer( querySelect.length()+8 )
			.append(querySelect)
			.insert( getAfterSelectInsertPoint(querySelect), TOP_STATEMENT );
    	
    	if (logger.isDebugEnabled())
    		logger.debug("BindingSqlServerDialect.getLimitString query: " + result);
    	
    	return result.toString();
    }
    
    /**
     * Answers the question: Does this dialect support bind variables 
     * (i.e., prepared statement parameters) for its limit/offset?
     * <p>
     * In the case of the <code>SQLServerDialect</code>, the answer is 
     * <strong>true</strong>.
     */
    public boolean supportsVariableLimit() {
		return true;
	}
    
    /**
     * Answers the question: Does the LIMIT clause come at the 
     * start of the SELECT statement, rather than at the end? 
     * <p>
     * In the case of the <code>SQLServerDialect</code>, the answer is 
     * <strong>true</strong>.
     */
    public boolean bindLimitParametersFirst() {
		return true;
	}
    
    /**
     * Answers the question: Does this dialect support some form of 
     * limiting query results via a SQL clause?
     * <p>
     * In the case of the <code>SQLServerDialect</code>, the answer is 
     * <strong>true</strong>.
     */
    public boolean supportsLimit() {
		return true;
	}
    
}
