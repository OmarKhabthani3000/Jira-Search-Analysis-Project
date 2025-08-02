import java.sql.Types;

import org.hibernate.dialect.SQLServerDialect;

public class SQLServerFixDialect extends SQLServerDialect {
	public SQLServer2005Dialect() {
		super();

		registerColumnType( Types.VARCHAR, "varchar(max)" );
		registerColumnType( Types.VARCHAR, 8000, "varchar($l)" );
	}
}

