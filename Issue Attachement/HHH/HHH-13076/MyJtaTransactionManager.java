package org.hibernate.bugs.transaction;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.InvalidTransactionException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.UserTransaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyJtaTransactionManager implements javax.transaction.TransactionManager, UserTransaction {

    private Logger logger = LoggerFactory.getLogger(MyJtaTransactionManager.class.getName());

    private MyJtaTransaction transaction;

    public MyJtaTransactionManager() {
    }

    @Override
    public void setTransactionTimeout(int seconds) throws SystemException {
        String msg = "Set transaction timeout failed, unsupported operation.";
        logger.error(msg);
        throw new UnsupportedOperationException(msg);
    }

    @Override
    public int getStatus() throws SystemException {
        MyJtaTransaction tx = getTransaction();
        return (tx != null) ? tx.getStatus() : Status.STATUS_NO_TRANSACTION;
    }

    @Override
    public void begin() throws NotSupportedException, SystemException {
        if (getStatus() != Status.STATUS_NO_TRANSACTION) {
            String msg = "Begin transaction failed, nested transactions are not supported.";
            logger.error(msg);
            throw new NotSupportedException(msg);
        }

        transaction = new MyJtaTransaction();
    }


    @Override
    public MyJtaTransaction getTransaction() {
       return transaction;
    }

    @Override
    public MyJtaTransaction suspend() throws SystemException {
        MyJtaTransaction tx = null;
        tx = getTransaction();
        return tx;
    }

    @Override
    public void resume(Transaction obj) throws IllegalStateException, InvalidTransactionException, SystemException {
        if ((obj == null) || !(obj instanceof MyJtaTransaction)) {
            String msg = "Resume transaction failed, invalid transaction.";
            logger.error(msg);
            throw new InvalidTransactionException(msg);
        }
    }

    @Override
    public void setRollbackOnly() throws IllegalStateException, SystemException {
        MyJtaTransaction tx = getTransaction();
        if (tx == null) {
            String msg = "Set rollback only failed, no transaction associated with current thread.";
            logger.error(msg);
            throw new IllegalStateException(msg);
        }
        tx.setRollbackOnly();
    }

    @Override
    public void commit() throws HeuristicMixedException, HeuristicRollbackException, IllegalStateException, RollbackException, SecurityException, SystemException {
        MyJtaTransaction tx = getTransaction();
        if (tx == null) {
            String msg = "Commit failed, no transaction associated with current thread.";
            logger.error(msg);
            throw new IllegalStateException(msg);
        }
        tx.commit();
        transaction = null;
    }

    @Override
    public void rollback() throws IllegalStateException, SecurityException, SystemException {
        MyJtaTransaction tx = getTransaction();
        if (tx == null) {
            String msg = "Rollback failed, no transaction associated with current thread.";
            logger.error(msg);
            throw new IllegalStateException(msg);
        }
        tx.rollback();
        transaction = null;
    }


}


