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
 * Attempts to determine when the "sequence" strategy should
 * be used for table primary keys
 *
 * @author Adam Retter <adam.retter@googlemail.com> / www.adamretter.org.uk
 * @serial 201006281233
 */
public class PostgresMetaDataDialect extends JDBCMetaDataDialect {

    @Override
    public Iterator getSuggestedPrimaryKeyStrategyName(String catalog, String schema, String table) {

        String sql = null;
        try {
            catalog = caseForSearch( catalog );
            schema = caseForSearch( schema );
            table = caseForSearch( table );

            log.debug("getSuggestedPrimaryKeyStrategyName(" + catalog + "." + schema + "." + table + ")");

            sql = "select nsp.nspname, " +
                  "cls.relname, " +
                  "att.attname, " +
                  "con.conname, " +
                  "pg_get_serial_sequence(nsp.nspname || '.' || cls.relname, att.attname) seqname " +
                  "from pg_catalog.pg_namespace nsp, pg_catalog.pg_class cls, pg_catalog.pg_constraint con, pg_catalog.pg_attribute att " +
                  "where " +
                  "cls.relnamespace = nsp.oid and " +
                  "con.conrelid = cls.oid and " +
                  "att.attrelid = cls.oid and " +
                  "att.attnum = ANY(con.conkey) and " +
                  "con.contype = 'p'";

            if(schema != null || table != null) {
                sql += " and ";

                if(schema != null) {
                    sql += "nsp.nspname = '" + schema + "'";
                }

                if(schema != null && table != null) {
                    sql += " and ";
                }

                if(table != null) {
                    sql += "cls.relname= '" + table + "'";
                }
            }

            PreparedStatement statement = getConnection().prepareStatement(sql);

            final String sc = schema;
            return new ResultSetIterator(statement.executeQuery(), getSQLExceptionConverter()) {
                Map<String, String> element = new HashMap<String, String>();
                protected Object convertRow(ResultSet rs) throws SQLException {
                    element.clear();
                    element.put("TABLE_NAME", rs.getString("relname"));
                    element.put("TABLE_SCHEM", rs.getString("nspname"));
                    element.put("TABLE_CAT", null);

                    String string = rs.getString("seqname");
                    if(string != null && string.length() > 0) {
                        element.put("HIBERNATE_STRATEGY", "sequence");
                    } else {
                        element.put("HIBERNATE_STRATEGY", null);
                    }

                    //TODO how to also set the sequence name?

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
