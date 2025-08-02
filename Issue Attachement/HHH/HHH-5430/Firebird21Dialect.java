/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.hibernate.dialect;

/**
 * A simple extension of {@link FirebirdDialect} to make use of Firebird 2.1
 * support for temporary tables.
 * @author MaCe
 */
public class Firebird21Dialect extends FirebirdDialect{

    @Override
    public boolean dropTemporaryTableAfterUse() {
        /*
         * The table should be deleted, because its contents is set to survive commit.
         * Data surviving commit seems to be a prudent choice
         */
        return true;
    }

    @Override
    public String getCreateTemporaryTablePostfix() {
        /*
         * The table preserves rows on commit - this seems to be a prudent choice
         * but forces dropTemporaryTableAfterUse to be set to true
         */
        return " ON COMMIT PRESERVE ROWS";
    }

    @Override
    public String getCreateTemporaryTableString() {
        return "CREATE GLOBAL TEMPORARY TABLE ";
    }

    @Override
    public boolean supportsTemporaryTables() {
        return true;
    }

}
