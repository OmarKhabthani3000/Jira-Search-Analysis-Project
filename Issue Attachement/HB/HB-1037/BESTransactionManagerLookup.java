package com.pyxis.hibernate.transaction;

import net.sf.hibernate.transaction.JNDITransactionManagerLookup;

/**
 * <code>TransactionManager</code> lookup strategy for Borland Enterprise
 * Server.
 *
 * @author 	E. Hardy
 * @version $Revision: $ $Date: $
 */
public class BESTransactionManagerLookup
        extends JNDITransactionManagerLookup
{
    public String getUserTransactionName()
    {
        return "java:comp/UserTransaction";
    }

    protected String getName()
    {
        return "java:pm/TransactionManager";
    }
}