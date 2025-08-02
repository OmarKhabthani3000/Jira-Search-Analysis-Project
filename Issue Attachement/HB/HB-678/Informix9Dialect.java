package net.sf.hibernate.dialect;

import java.sql.Types;

import net.sf.hibernate.MappingException;
import net.sf.hibernate.dialect.Dialect;
import net.sf.hibernate.util.StringHelper;

/**
 * Informix dialect for version 9 of Informix IDS. <br>
 * This class is required in order to use Hibernate with Informix 9.<br>
 * <br>
 * Seems to work with Informix Dynamic Server Version 9.3, 
 * Informix JDBC driver version 2.21JC5.
 * 
 * @author Matt Sanchez (matt at mattsanchez.com)
 */
public class Informix9Dialect extends Dialect {

	/**
	 * Creates new <code>InformixDialect</code> instance. Sets up the JDBC /
	 * Informix type mappings.
	 */
	public Informix9Dialect() {
		super();

		registerColumnType(Types.BIGINT, "INT8");
		registerColumnType(Types.BINARY, "BYTE");
		registerColumnType(Types.BIT, "SMALLINT"); // Informix doesn't have a bit type
		registerColumnType(Types.BOOLEAN, "BOOLEAN");
		registerColumnType(Types.CHAR, "CHAR($l)");
		registerColumnType(Types.DATE, "DATE");
		registerColumnType(Types.DECIMAL, "DECIMAL");
		registerColumnType(Types.DOUBLE, "DOUBLE");
		registerColumnType(Types.FLOAT, "FLOAT");
		registerColumnType(Types.INTEGER, "INTEGER");
		registerColumnType(Types.LONGVARBINARY, "BYTE"); // or CLOB
		registerColumnType(Types.LONGVARCHAR, "TEXT"); // or BLOB?
		registerColumnType(Types.NUMERIC, "DECIMAL"); // or MONEY
		registerColumnType(Types.REAL, "SMALLFLOAT");
		registerColumnType(Types.SMALLINT, "SMALLINT");
		registerColumnType(Types.TIME, "DATETIME YEAR TO FRACTION(5)");
		registerColumnType(Types.TIMESTAMP, "DATETIME HOUR TO SECOND");
		registerColumnType(Types.TINYINT, "SMALLINT");
		registerColumnType(Types.VARBINARY, "BYTE");
		registerColumnType(Types.VARCHAR, 255, "VARCHAR($l)");
		registerColumnType(Types.VARCHAR, 32739, "LVARCHAR");
		registerColumnType(Types.VARCHAR, "TEXT");
	}

	public String getAddColumnString() {
		return "add";
	}

	public boolean supportsIdentityColumns() {
		return true;
	}

	public String getIdentitySelectString() throws MappingException {
		return "select first 1 dbinfo('sqlca.sqlerrd1') from systables";
	}

	public String getIdentityColumnString() throws MappingException {
		return "SERIAL NOT NULL";
	}

	public boolean dropConstraints() {
		return false;
	}
	
	public String getAddForeignKeyConstraintString(
		String constraintName,
		String[] foreignKey,
		String referencedTable,
		String[] primaryKey) {
		
		return new StringBuffer(30)
			.append(" add constraint ")
			.append(" (foreign key (")
			.append( StringHelper.join(StringHelper.COMMA_SPACE, foreignKey) )
			.append(") references ")
			.append(referencedTable)
			.append(")")
			.toString();
	}
	
}