package org.hibernate.transaction;

import org.hibernate.transaction.JNDITransactionManagerLookup;

public class IMSTransactionManagerLookup extends JNDITransactionManagerLookup
{

    public IMSTransactionManagerLookup()
    {
    }

    protected String getName()
    {
        return "java:comp/UserTransactionManager";
    }

    public String getUserTransactionName()
    {
        return "java:comp/UserTransaction";
    }
}