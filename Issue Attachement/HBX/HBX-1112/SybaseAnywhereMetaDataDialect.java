

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.hibernate.cfg.reveng.dialect.JDBCMetaDataDialect;
import org.hibernate.cfg.reveng.dialect.ResultSetIterator;
import org.hibernate.mapping.Table;

public class SybaseAnywhereMetaDataDialect extends JDBCMetaDataDialect {

    public Iterator getPrimaryKeys(final String xcatalog, final String xschema, final String xtable) {
        try {
            final String catalog = caseForSearch( xcatalog );
            final String schema = caseForSearch( xschema );
            final String table = caseForSearch( xtable );
            
            log.debug("getPrimaryKeys(" + catalog + "." + schema + "." + table + ")");
            ResultSet tableRs = getMetaData().getPrimaryKeys(catalog, schema, table);
            
            return new ResultSetIterator(tableRs, getSQLExceptionConverter()) {
                
                Map element = new HashMap();
                protected Object convertRow(ResultSet rs) throws SQLException {
                    element.clear();
                    putTablePart(element, rs);
                    element.put("COLUMN_NAME", rs.getString("COLUMN_NAME"));
                    element.put("KEY_SEQ", new Short(rs.getShort("KEY_SEQ")));
                    
                    /*
                     * Sybase incorrectly returns the COLUMN_NAME for the PK_NAME
                     * This is incorrect when there is a composite primary key
                     */
                    element.put("PK_NAME", null);
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

    protected void putExportedKeysPart(Map element, ResultSet rs) throws SQLException {
        element.put( "PKTABLE_NAME", rs.getString("PKTABLE_NAME"));
        element.put( "PKTABLE_SCHEM", rs.getString("PKTABLE_SCHEM"));
        element.put( "PKTABLE_CAT", rs.getString("PKTABLE_CAT"));
        element.put( "FKTABLE_CAT", rs.getString("FKTABLE_CAT"));
        element.put( "FKTABLE_SCHEM",rs.getString("FKTABLE_SCHEM"));
        element.put( "FKTABLE_NAME", rs.getString("FKTABLE_NAME"));
        element.put( "FKCOLUMN_NAME", rs.getString("FKCOLUMN_NAME"));
        element.put( "PKCOLUMN_NAME", rs.getString("PKCOLUMN_NAME"));
        element.put( "KEY_SEQ", new Short(rs.getShort("KEY_SEQ")));
        
        /*
         * The FK_NAME returned by Sybase is not unique when multiple tables use the same column name for a FK
         * Working around this by appending the table name to the FK_NAME seems to work 
         * ... at least as far as hibernate code generation is concerned
         */
        element.put( "FK_NAME", String.format("%s.%s", rs.getString("FKTABLE_NAME"), rs.getString("FK_NAME")));
    }
}
