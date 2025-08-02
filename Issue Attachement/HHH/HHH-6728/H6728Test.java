package org.hibernate.dialect;

import static org.junit.Assert.*;

import org.hibernate.dialect.SQLServer2005Dialect;
import org.junit.Test;

/**
 * Bug exposure testcase for https://hibernate.onjira.com/browse/HHH-6728.
 */
public class H6728Test {
	@Test
	public void testSQLServer2005DialectGetLimitStringShouldNotChangeCharacterCasing() {
		String sqlInput =
				"select this_.dataField as dataField1_0_ from Testable this_"
						+ " order by this_.sortField desc";

		SQLServer2005Dialect dialect = new SQLServer2005Dialect();
		String actual = dialect.getLimitString(sqlInput, false);

		assertEquals("WITH query AS (select ROW_NUMBER() OVER (order by this_.sortField desc)"
				+ " as __hibernate_row_nr__, this_.dataField as dataField1_0_ from Testable this_"
				+ " ) SELECT * FROM query WHERE __hibernate_row_nr__ BETWEEN ? AND ?", actual);
	}
}
