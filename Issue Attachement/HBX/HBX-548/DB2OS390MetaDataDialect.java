package org.hibernate.cfg.reveng.dialect;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.hibernate.mapping.Table;

/**
 * MetaData dialect that uses standard JDBC for reading DB2 OS/390 metadata.
 * 
 * @author Norm Lee
 * 
 */
public class DB2OS390MetaDataDialect extends AbstractMetaDataDialect {

	protected String getDatabaseStructure(String catalog, String schema) {
	      ResultSet schemaRs = null;
	      ResultSet catalogRs = null;
	      String nl = System.getProperty("line.separator");
	      StringBuffer sb = new StringBuffer(nl);
	      // Let's give the user some feedback. The exception
	      // is probably related to incorrect schema configuration.
	      sb.append("Configured schema:").append(schema).append(nl);
	      sb.append("Configured catalog:").append(catalog ).append(nl);

	      try {
	         schemaRs = getMetaData().getSchemas();
	         sb.append("Available schemas:").append(nl);
	         while (schemaRs.next() ) {
	            sb.append("  ").append(schemaRs.getString(1) ).append(nl);
	         }
	      } 
	      catch (SQLException e2) {
	         log.warn("Could not get schemas", e2);
	         sb.append("  <SQLException while getting schemas>").append(nl);
	      } 
	      finally {
	         try {
	            schemaRs.close();
	         } 
	         catch (Exception ignore) {
	         }
	      }

	      try {
	         catalogRs = getMetaData().getCatalogs();
	         sb.append("Available catalogs:").append(nl);
	         while (catalogRs.next() ) {
	            sb.append("  ").append(catalogRs.getString(1) ).append(nl);
	         }
	      } 
	      catch (SQLException e2) {
	         log.warn("Could not get catalogs", e2);
	         sb.append("  <SQLException while getting catalogs>").append(nl);
	      } 
	      finally {
	         try {
	            catalogRs.close();
	         } 
	         catch (Exception ignore) {
	         }
	      }
	      return sb.toString();
	   }
	
	public Iterator getTables(String xcatalog, String xschema, String xtable) {
		try {
			final String catalog = caseForSearch(xcatalog);
			final String schema = caseForSearch(xschema);
			final String table = caseForSearch(xtable);

			log.debug("getTables(" + catalog + "." + schema + "." + table + ")");

			ResultSet tableRs = getMetaData().getTables(catalog, schema, table, new String[] { "TABLE", "VIEW" });

			return new ResultSetIterator(tableRs, getSQLExceptionConverter()) {

				Map element = new HashMap();

				protected Object convertRow(ResultSet tableRs) throws SQLException {
					element.clear();
					putTablePart(element, tableRs);
					element.put("TABLE_TYPE", tableRs.getString(4));
					element.put("REMARKS", tableRs.getString(5));
					return element;
				}

				protected Throwable handleSQLException(SQLException e) {
					// schemaRs and catalogRs are only used for error reporting
					// if
					// we get an exception
					String databaseStructure = getDatabaseStructure(catalog, schema);
					throw getSQLExceptionConverter().convert(e, "Could not get list of tables from database. Probably a JDBC driver problem. " + databaseStructure, null);
				}
			};
		} catch (SQLException e) {
			// schemaRs and catalogRs are only used for error reporting if we
			// get an exception
			String databaseStructure = getDatabaseStructure(xcatalog, xschema);
			throw getSQLExceptionConverter().convert(e, "Could not get list of tables from database. Probably a JDBC driver problem. " + databaseStructure, null);
		}
	}

	private void dumpHeader(ResultSet columnRs) throws SQLException {
		ResultSetMetaData md2 = columnRs.getMetaData();

		int columnCount = md2.getColumnCount();
		for (int i = 1; i <= columnCount; i++) {
			System.out.print(md2.getColumnName(i) + "|");
		}
		System.out.println();
	}

	private void dumpRow(ResultSet columnRs) throws SQLException {
		ResultSetMetaData md2 = columnRs.getMetaData();

		int columnCount = md2.getColumnCount();
		for (int i = 1; i <= columnCount; i++) {
			System.out.print(columnRs.getObject(i) + "|");
		}
		System.out.println();
	}

	public Iterator getIndexInfo(final String xcatalog, final String xschema, final String xtable) {
		try {
			final String catalog = caseForSearch(xcatalog);
			final String schema = caseForSearch(xschema);
			final String table = caseForSearch(xtable);

			log.debug("getIndexInfo(" + catalog + "." + schema + "." + table + ")");
			ResultSet tableRs = getMetaData().getIndexInfo(catalog, schema, table, false, true);

			return new ResultSetIterator(tableRs, getSQLExceptionConverter()) {

				Map element = new HashMap();

				protected Object convertRow(ResultSet rs) throws SQLException {
					element.clear();
					putTablePart(element, rs);
					element.put("INDEX_NAME", rs.getString(6));
					element.put("COLUMN_NAME", rs.getString(9));
					element.put("NON_UNIQUE", Boolean.valueOf(rs.getBoolean(4)));
					element.put("TYPE", new Short(rs.getShort(7)));
					return element;
				}

				protected Throwable handleSQLException(SQLException e) {
					throw getSQLExceptionConverter().convert(e, "Exception while getting index info for " + Table.qualify(catalog, schema, table), 	null);
				}
			};
		} catch (SQLException e) {
			throw getSQLExceptionConverter().convert(e, "Exception while getting index info for " + Table.qualify(xcatalog, xschema, xtable), null);
		}
	}

	private void putTablePart(Map element, ResultSet tableRs) throws SQLException {
		element.put("TABLE_NAME", tableRs.getString(3));
		element.put("TABLE_SCHEM", tableRs.getString(2));
		element.put("TABLE_CAT", tableRs.getString(1));
	}

	public Iterator getColumns(final String xcatalog, final String xschema, final String xtable, String xcolumn) {
		try {
			final String catalog = caseForSearch(xcatalog);
			final String schema = caseForSearch(xschema);
			final String table = caseForSearch(xtable);
			final String column = caseForSearch(xcolumn);

			log.debug("getColumns(" + catalog + "." + schema + "." + table + "." + column + ")");
			ResultSet tableRs = getMetaData().getColumns(catalog, schema, table, column);

			return new ResultSetIterator(tableRs, getSQLExceptionConverter()) {

				Map element = new HashMap();

				protected Object convertRow(ResultSet rs) throws SQLException {
					element.clear();
					putTablePart(element, rs);
					element.put("DATA_TYPE", new Integer(rs.getInt(5)));
					element.put("TYPE_NAME", rs.getString(6));
					element.put("COLUMN_NAME", rs.getString(4));
					element.put("NULLABLE", new Integer(rs.getInt(11)));
					element.put("COLUMN_SIZE", new Integer(rs.getInt(7)));
					element.put("DECIMAL_DIGITS", new Integer(rs.getInt(9)));
					element.put("REMARKS", rs.getString(12));
					return element;
				}

				protected Throwable handleSQLException(SQLException e) { 
					throw getSQLExceptionConverter().convert(e, "Error while reading column meta data for "	+ Table.qualify(catalog, schema, table), null);
				}
			};
		} catch (SQLException e) {
			throw getSQLExceptionConverter().convert(e, "Error while reading column meta data for "	+ Table.qualify(xcatalog, xschema, xtable), null);
		}
	}

	public Iterator getPrimaryKeys(final String xcatalog, final String xschema, final String xtable) {
		try {
			final String catalog = caseForSearch(xcatalog);
			final String schema = caseForSearch(xschema);
			final String table = caseForSearch(xtable);

			log.debug("getPrimaryKeys(" + catalog + "." + schema + "." + table + ")");
			ResultSet tableRs = getMetaData().getPrimaryKeys(catalog, schema, table);

			return new ResultSetIterator(tableRs, getSQLExceptionConverter()) {

				Map element = new HashMap();

				protected Object convertRow(ResultSet rs) throws SQLException {
					element.clear();
					putTablePart(element, rs);
					element.put("COLUMN_NAME", rs.getString(4));
					element.put("KEY_SEQ", new Short(rs.getShort(5)));
					element.put("PK_NAME", rs.getString(6));
					return element;
				}

				protected Throwable handleSQLException(SQLException e) {
					throw getSQLExceptionConverter().convert(e, "Error while reading primary key meta data for " + Table.qualify(catalog, schema, table), null);
				}
			};
		} catch (SQLException e) {
			throw getSQLExceptionConverter().convert(e, "Error while reading primary key meta data for " + Table.qualify(xcatalog, xschema, xtable), null);
		}
	}

	public Iterator getExportedKeys(final String xcatalog, final String xschema, final String xtable) {
		try {
			final String catalog = caseForSearch(xcatalog);
			final String schema = caseForSearch(xschema);
			final String table = caseForSearch(xtable);

			log.debug("getExportedKeys(" + catalog + "." + schema + "." + table	+ ")");
			ResultSet tableRs = getMetaData().getExportedKeys(catalog, schema, table);

			return new ResultSetIterator(tableRs, getSQLExceptionConverter()) {

				Map element = new HashMap();

				protected Object convertRow(ResultSet rs) throws SQLException {
					element.clear();
					element.put("PKTABLE_NAME", rs.getString(3));
					element.put("PKTABLE_SCHEM", rs.getString(2));
					element.put("PKTABLE_CAT", rs.getString(1));
					element.put("FKTABLE_CAT", rs.getString(5));
					element.put("FKTABLE_SCHEM", rs.getString(6));
					element.put("FKTABLE_NAME", rs.getString(7));
					element.put("FKCOLUMN_NAME", rs.getString(8));
					element.put("PKCOLUMN_NAME", rs.getString(4));
					element.put("FK_NAME", rs.getString(12));
					element.put("KEY_SEQ", new Short(rs.getShort(9)));
					return element;
				}

				protected Throwable handleSQLException(SQLException e) {
					throw getSQLExceptionConverter().convert(e, "Error while reading exported keys meta data for " + Table.qualify(catalog, schema, table), null);
				}
			};
		} catch (SQLException e) {
			throw getSQLExceptionConverter().convert(e, "Error while reading exported keys meta data for " + Table.qualify(xcatalog, xschema, xtable), null);
		}
	}
}
