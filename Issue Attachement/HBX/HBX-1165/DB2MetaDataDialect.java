package org.hibernate.cfg.reveng.dialect;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * MetaDataDialect for Postgres
 *
 * Attempts to determine when the "identity" strategy should
 * be used for table primary keys
 *
 * @author Adam Retter <adam.retter@googlemail.com> / www.adamretter.org.uk
 * @serial 201006281231
 */
public class DB2MetaDataDialect extends JDBCMetaDataDialect {

    @Override
    public Iterator getSuggestedPrimaryKeyStrategyName(String catalog, String schema, String table) {

        String sql = null;
        try {
            catalog = caseForSearch( catalog );
            schema = caseForSearch( schema );
            table = caseForSearch( table );

            log.debug("getSuggestedPrimaryKeyStrategyName(" + catalog + "." + schema + "." + table + ")");

            sql = "SELECT TABNAME, IDENTITY, GENERATED FROM SYSCAT.COLUMNS";

            if(schema != null || table != null) {
                sql += " where ";

                if(schema != null) {
                    sql += "TABSCHEMA='" + schema + "'";
                }

                if(schema != null && table != null) {
                    sql += " AND ";
                }

                if(table != null) {
                    sql += "TABNAME='" + table + "'";
                }
            }

            PreparedStatement statement = getConnection().prepareStatement(sql);

            final String sc = schema;
            return new ResultSetIterator(statement.executeQuery(), getSQLExceptionConverter()) {
                Map<String, String> element = new HashMap<String, String>();
                protected Object convertRow(ResultSet rs) throws SQLException {
                    element.clear();
                    element.put("TABLE_NAME", rs.getString("TABNAME"));
                    element.put("TABLE_SCHEM", sc);
                    element.put("TABLE_CAT", null);

                    String string = rs.getString("IDENTITY");
                    if(string != null && string.equals("Y")) {
                        element.put("HIBERNATE_STRATEGY", "identity");
                    } else {
                        element.put("HIBERNATE_STRATEGY", null);
                    }

                    return element;
                }
                protected Throwable handleSQLException(SQLException e) {
                    // schemaRs and catalogRs are only used for error reporting if
                    // we get an exception
                    throw getSQLExceptionConverter().convert(e, "Could not get list of suggested identity strategies from database. Probably a JDBC driver problem. ", null);
                }
            };
        } catch (SQLException e) {
            throw getSQLExceptionConverter().convert(e, "Could not get list of suggested identity strategies from database. Probably a JDBC driver problem. ", sql);
        }
    }
}