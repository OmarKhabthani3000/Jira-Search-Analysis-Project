package org.hibernate.dialect;

import java.sql.Types;

import org.hibernate.dialect.SQLServerDialect;

public class SQLServer2005Dialect extends SQLServerDialect {

	public SQLServer2005Dialect() 
	{ 
		super();
		
		registerColumnType(Types.VARCHAR, 1073741823, "NVARCHAR(MAX)"); 
		registerColumnType(Types.VARCHAR, 2147483647, "VARCHAR(MAX)"); 
		registerColumnType(Types.VARBINARY, 2147483647, "VARBINARY(MAX)"); 
	}

	/*
	 * Add a LIMIT clause to the given SQL SELECT 
	 * 
	 * @param querySqlString The SQL statement to base the limit query off of. 
	 * @param offset Offset of the first row to be returned by the query (zero-based) 
	 * @param last Maximum number of rows to be returned by the query 
	 * @returns A new SQL statement with the LIMIT clause applied. 
	 * 
	 * The LIMIT SQL will look like:
	 * 
	 * SELECT TOP {last} * FROM ( 
	 * SELECT ROW_NUMBER() OVER(ORDER BY __hibernate_sort_expr_1__ {sort direction 1} [, __hibernate_sort_expr_2__ {sort direction 2}, ...]) as row, query.* FROM ( 
	 * {original select query part}, {sort field 1} as __hibernate_sort_expr_1__ [, {sort field 2} as __hibernate_sort_expr_2__, ...] 
	 * {remainder of original query minus the order by clause} 
	 * ) query 
	 * ) page WHERE page.row > offset 
	 */
	@Override
	public String getLimitString(String querySqlString, int offset, int last) {
		int fromIndex = querySqlString.toLowerCase().indexOf(" from "); 
		String select = querySqlString.substring(0, fromIndex); 

		int orderIndex = querySqlString.toLowerCase().lastIndexOf(" order by "); 
		String from; 
		String[] sortExpressions; 
		if (orderIndex > 0) 
		{ 
			from = querySqlString.substring(fromIndex, orderIndex).trim(); 
			String orderBy = querySqlString.substring(orderIndex).toString().trim(); 
			sortExpressions = orderBy.substring(9).split(","); 
		} 
		else 
		{ 
			from = querySqlString.substring(fromIndex).trim(); 
	//		 Use dummy sort to avoid errors 
			sortExpressions = new String[] { "CURRENT_TIMESTAMP" }; 
		} 

		StringBuilder result = new StringBuilder(); 
		result.append("SELECT TOP "); 
		result.append(last + ""); 
		result.append(" * FROM (SELECT ROW_NUMBER() OVER(ORDER BY "); 

		for (int i = 1; i <= sortExpressions.length; i++) 
		{ 
			if (i > 1) 
				result.append(", "); 
	
			result.append("__hibernate_sort_expr_") 
			.append(i + "") 
			.append("__"); 
	
			if (sortExpressions[i - 1].trim().toLowerCase().endsWith("desc")) 
				result.append(" DESC"); 
		} 

		result.append(") as row, query.* FROM (") 
		.append(select); 

		for (int i = 1; i <= sortExpressions.length; i++) 
		{ 
			result.append(", ") 
			.append(sortExpressions[i - 1].trim().split(" ")[0]) 
			.append(" as __hibernate_sort_expr_") 
			.append(i + "") 
			.append("__"); 
		} 

		result.append(" ") 
		.append(from) 
		.append(") query ) page WHERE page.row > ") 
		.append(offset + ""); 

		return result.toString(); 
	}

	@Override
	public boolean supportsLimit() {
		return true;
	}

	@Override
	public boolean supportsLimitOffset() {
		return true;
	}

	@Override
	public boolean useMaxForLimit() {
		return false;
	} 
	
}