package org.hibernate.dialect;

import org.hibernate.dialect.pagination.DB2390V12LimitHandler;

/**
 * An SQL dialect for DB2/390 version 12 that supports pagination with the new 
 * keyword "{@code OFFSET}".
 */
public class DB2390V12Dialect extends DB2390V8Dialect {

	private static final DB2390V12LimitHandler LIMIT_HANDLER = new DB2390V12LimitHandler();
	
	@Override
	public boolean supportsLimitOffset() {
		return getLimitHandler().supportsLimitOffset();
	}

	@Override
	public boolean useMaxForLimit() {
		return getLimitHandler().useMaxForLimit();
	}
	
	@Override
	public String getLimitString(String sql, int offset, int limit) {
		return getLimitHandler().getLimitString(sql, offset, limit);
	}

	@Override
	public DB2390V12LimitHandler getLimitHandler() {
		return LIMIT_HANDLER;
	}
}