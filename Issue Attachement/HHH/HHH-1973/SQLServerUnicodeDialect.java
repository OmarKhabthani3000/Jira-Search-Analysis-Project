package org.hibernate.dialect;

import java.sql.Types;

import org.hibernate.dialect.SQLServerDialect;

/**
 * Allows to use Unicode types (nchar, nvarchar and ntext) in SQL Server. <br/>
 * @author ggrussenmeyer
 */
public class SQLServerUnicodeDialect extends SQLServerDialect {

    /**
     * Creates a new SQLServer Unicode dialect. <br/>
     */
    public SQLServerUnicodeDialect() {
        this.registerColumnType( Types.CHAR, "nchar(1)" );
        this.registerColumnType( Types.VARCHAR, "nvarchar($l)" );
        this.registerColumnType( Types.CLOB, "ntext" );
    }
}
