/**
 * <b>Created:<b> Jul 14, 2004<br><br>
 *
 * <b>Description:</b><br>
 * 
 * <br> 
 * <d>Revision History:</b><br> 
 * ----------------------------------------------------------------------------------<br> 
 * Version			Date			Author		Comments<br> 
 * ----------------------------------------------------------------------------------<br> 
 * 1.0			Jul 14, 2004		fus8882		Initial Version.
 *<br> <br> 
 * @author fus8882
 * @version 1.0
 *
 */
package org.hibernate.test;

import net.sf.hibernate.dialect.DB2Dialect;
import junit.framework.TestCase;

/**
 * @author Chris Nelson
 *
 * This test case checks to make sure that this dialect handles limit queries
 * properly, ie, doesn't break on order by or distinct
 */
public class DB2DialectTest extends TestCase
{

	/**
	 * 
	 */
	public DB2DialectTest()
	{
		super();
	}

	/**
	 * @param arg0
	 */
	public DB2DialectTest(String arg0)
	{
		super(arg0);
	}

	DB2Dialect dialect = new DB2Dialect();
	
	public void testLimitQuery() throws Exception
	{
		String sql = "select foo from bar";
		String expectedSql = "select * from ( select rownumber() over() " +
			"as row_num_, foo from bar ) as temp_ where row_num_ <= ?";
		
		System.out.println(dialect.getLimitString(sql, false));
		assertEquals("basic query", expectedSql, dialect.getLimitString(sql, false));
	}

	public void testLimitWithOrderBy()
	{
		String sql;
		String expectedSql;
		sql = "select foo from bar order by baz";
		expectedSql = "select * from ( select rownumber() over(order by baz) " +
					"as row_num_, foo from bar order by baz ) as temp_ where row_num_ <= ?";
		
		System.out.println(dialect.getLimitString(sql, false));
		assertEquals("basic query", expectedSql, dialect.getLimitString(sql, false));
	}

	public void testLimitWithDistinctAndOrderBy()
	{
		String sql;
		String expectedSql;
		sql = "select distinct foo from bar order by buzz.baz";
		expectedSql = "select * from ( select rownumber() over() " +
		"as row_num_, row_.* from ( select distinct foo from bar order by buzz.baz ) as row_ ) " + 
		"as temp_ where row_num_ <= ?";
		System.out.println(dialect.getLimitString(sql, false));
		assertEquals("basic query", expectedSql, dialect.getLimitString(sql, false));
	}
}
