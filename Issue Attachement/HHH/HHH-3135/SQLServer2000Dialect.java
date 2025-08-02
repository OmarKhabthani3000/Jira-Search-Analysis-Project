
package org.hibernate.dialect;

import java.sql.Types;

/**
 * Since SQL Server 2000 we have bigints and bits
 * This way, longs don't get created as numeric(19,0), and Booleans don't get created as tinyints.
 * This should lead to performance improvements.
 */
public class SQLServer2000Dialect extends SQLServerDialect {

	public SQLServer2000Dialect() {
		super();
		registerColumnType(Types.BIGINT, "bigint");
		registerColumnType(Types.BIT, "bit");
	}

}
