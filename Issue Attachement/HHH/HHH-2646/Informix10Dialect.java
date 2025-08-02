package org.hibernate.dialect;

import java.sql.Types;

/**
 * Dialect for Informix 10.
 * <ul>
 *   <li>hint for first rows</li>
 *   <li>support for limitOffset</li>
 *   <li>added types for BLOB and CLOB</li>
 * </ul> 
 */
public class Informix10Dialect extends InformixDialect {
    
    /**
     * 
     */
    public Informix10Dialect() {
        super();
        registerColumnType( Types.BLOB, "blob" );
        registerColumnType( Types.CLOB, "clob" );
    } 

    public boolean useMaxForLimit() {
        return false;
    }

    public boolean supportsLimitOffset() {
        return true;
    }
    
    public String getLimitString(String querySelect, int offset, int limit) {
        String os = "";
        if (offset > 0) os = " skip " + offset;        
        return new StringBuffer( querySelect.length() + 24 + os.length())
            .append(querySelect)
            .insert( querySelect.toLowerCase().indexOf( "select" ) + 6,
                    " {+ FIRST_ROWS }" + os + " first " + limit )
            .toString();
    }
}

