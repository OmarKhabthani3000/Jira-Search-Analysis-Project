package additional.hibernate;

import java.sql.Types;

import org.hibernate.dialect.InformixDialect;

/**
 * @author Nick Airey
 */
public class Informix10Dialect extends InformixDialect {
	public Informix10Dialect() {
		super();
		// register support for BLOB type in informix 10
		registerColumnType( Types.BLOB, "blob" );
	}
}
