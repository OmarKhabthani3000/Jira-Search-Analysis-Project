package org.hibernate.bugs.transaction;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.RollbackException;
import javax.transaction.Synchronization;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.xa.XAResource;

public class MyJtaTransaction implements Transaction {

    @Override
    public void commit() throws RollbackException, HeuristicMixedException, HeuristicRollbackException,
            SecurityException, SystemException {
    }

    @Override
    public void rollback() throws IllegalStateException, SystemException {
    }

    @Override
    public void setRollbackOnly() throws IllegalStateException, SystemException {
    }

    @Override
    public int getStatus() throws SystemException {
        return 0;
    }

    @Override
    public boolean enlistResource(XAResource xaRes) throws RollbackException, IllegalStateException, SystemException {
        return false;
    }

    @Override
    public boolean delistResource(XAResource xaRes, int flag) throws IllegalStateException, SystemException {
        return false;
    }

    @Override
    public void registerSynchronization(Synchronization sync)
            throws RollbackException, IllegalStateException, SystemException {
    }

}
