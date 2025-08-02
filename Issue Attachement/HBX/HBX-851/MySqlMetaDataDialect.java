package org.hibernate.cfg.reveng.dialect;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import org.hibernate.mapping.Table;

/**
 * MySQL-specific MetaDataDialect
 * @author Zeljko Trogrlic
 */
public class MySqlMetaDataDialect extends JDBCMetaDataDialect {

	public MySqlMetaDataDialect() {
	}

	/**
	 * Return iterator over the columns that mathces catalog, schema and table
	 */
	public Iterator getPrimaryKeys(String catalog, String schema, String table) {
		try {
			log.debug("getPrimaryKeys(" + catalog + "." + schema + "." + table + ")");
			Statement stmt = this.getConnection().createStatement();

			List pkList = new LinkedList();
			ResultSet columnRs = stmt.executeQuery("show columns from " + table);
			short seq = 0;
			while (columnRs.next()) {
				if ("PRI".equals(columnRs.getString("Key"))) {
					Map element = new HashMap();
					element.put("TABLE_NAME", table);
					element.put("COLUMN_NAME", columnRs.getString("Field"));
					element.put("KEY_SEQ", new Short(seq++));
					element.put("PK_NAME", columnRs.getString(4));
					element.put("TABLE_SCHEM", schema);
					element.put("TABLE_CAT", catalog);
					pkList.add(element);
				}
			}
			return pkList.iterator();
		} catch (SQLException e) {
			throw getSQLExceptionConverter().convert(e,
					"Error while reading primary key meta data for " +
					Table.qualify(catalog, schema, table), null);
		}
	}
}
