package org.hibernate.dialect.pagination;

import org.hibernate.dialect.pagination.AbstractLimitHandler;
import org.hibernate.engine.spi.RowSelection;

/**
 * LIMIT clause handler compatible with DB2/390 version 12 and later.
 */
public class DB2390V12LimitHandler extends AbstractLimitHandler {
	
	@Override
	public String processSql(String sql, RowSelection selection) {
		return getLimitString(sql, selection.getFirstRow(), getMaxOrLimit(selection));
	}
	
	/**
	 * Given a limit and an offset, apply the limit clause to the sql.
	 * 
	 * @param sql The query to which to apply the limit
	 * @param offset The offset of the limit
	 * @param limit The limit of the limit ;)
	 * 
	 * @return The modified query statement with the limit applied
	 */
	public String getLimitString(String sql, int offset, int limit) {
		StringBuilder newSql = new StringBuilder(sql);
		if (offset > 0) {
			newSql.append(" offset " + offset + " rows");
		}
		if (limit > 0) {
			newSql.append(" fetch first " + limit + " rows only");
		}
		return newSql.toString();
	}

	@Override
	public boolean supportsLimit() {
		return true;
	}

	@Override
	public boolean supportsVariableLimit() {
		return false;
	}
}