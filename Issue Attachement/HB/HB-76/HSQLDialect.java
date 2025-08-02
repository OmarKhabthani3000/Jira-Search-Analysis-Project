//$Id: HSQLDialect.java,v 1.11 2003/05/14 12:20:23 oneovthafew Exp $
// Contributed by Phillip Baird

package net.sf.hibernate.dialect;

import java.sql.Types;

import net.sf.hibernate.cfg.Environment;
import net.sf.hibernate.sql.CaseFragment;
import net.sf.hibernate.sql.HSQLCaseFragment;

/**
 * An SQL dialect compatible with HSQLDB (Hypersonic SQL).
 * @author Christoph Sturm
 */
public class HSQLDialect extends Dialect {
	
	public HSQLDialect() {
		super();
		register( Types.BIGINT, "BIGINT" );
		register( Types.BINARY, "BINARY" );
		register( Types.BIT, "BIT" );
		register( Types.CHAR, "CHAR(1)" );
		register( Types.DATE, "DATE" );
		register( Types.DECIMAL, "DECIMAL" );
		register( Types.DOUBLE, "DOUBLE" );
		register( Types.FLOAT, "FLOAT" );
		register( Types.INTEGER, "INTEGER" );
		register( Types.LONGVARBINARY, "LONGVARBINARY" );
		register( Types.LONGVARCHAR, "LONGVARCHAR" );
		register( Types.SMALLINT, "SMALLINT" );
		register( Types.TINYINT, "TINYINT" );
		register( Types.TIME, "TIME" );
		register( Types.TIMESTAMP, "TIMESTAMP" );
		register( Types.VARCHAR, "VARCHAR($l)" );
		register( Types.VARBINARY, "VARBINARY($l)" );
		register( Types.NUMERIC, "NUMERIC" );
		
		getDefaultProperties().setProperty(Environment.USE_OUTER_JOIN, "false"); //HSQL has outer joins but not for composite keys!
		getDefaultProperties().setProperty(Environment.STATEMENT_BATCH_SIZE, NO_BATCH);
	}
	
	public String getAddColumnString() {
		return "add column";
	}
	
	public boolean supportsIdentityColumns() {
		return true;
	}
	public String getIdentityColumnString() {
		return "IDENTITY";
	}
	public String getIdentitySelectString() {
		return "call IDENTITY()";
	}
	public String getIdentityInsertString() {
		return "null";
	}
	
	public boolean supportsForUpdate() {
		return false;
	}
	
	/**
	 * Not supported in 1.7.1 (1.7.2 only)
	 */
	public boolean supportsUnique() {
		return false;
	}
	
    public boolean supportsLimit() {
        return true;
    }

	public String getLimitString(String sql) {
		StringBuffer pagingSelect = new StringBuffer(100);
		pagingSelect.append(sql);
		pagingSelect.insert(6, " limit ? ?");
		return pagingSelect.toString();
	}
	
	public CaseFragment createCaseFragment() {
		return new HSQLCaseFragment();
	}

}






