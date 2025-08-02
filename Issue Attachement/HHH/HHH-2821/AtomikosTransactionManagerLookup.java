package org.hibernate.transaction;

import java.util.Properties;

import javax.transaction.TransactionManager;

import org.hibernate.HibernateException;

/**
 * TransactionManager lookup strategy for Atomikos Transactions.
 * @author Ludovic Orban
 */
public class AtomikosTransactionManagerLookup implements TransactionManagerLookup {

	/**
	 * @see org.hibernate.transaction.TransactionManagerLookup#getTransactionManager(Properties)
	 */
    public TransactionManager getTransactionManager(Properties props) throws HibernateException {
		try {
			Class clazz = Class.forName("com.atomikos.icatch.jta.UserTransactionManager");
			return (TransactionManager) clazz.newInstance();
		} catch (Exception e) {
			throw new HibernateException("Could not obtain Atomikos transaction manager instance", e);
		}
    }

    public String getUserTransactionName() {
        return "java:comp/UserTransaction";
    }

}
