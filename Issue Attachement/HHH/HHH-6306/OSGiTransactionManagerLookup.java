package org.hibernate.transaction;

import java.util.Properties;

import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import org.hibernate.HibernateException;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

public class OSGiTransactionManagerLookup implements TransactionManagerLookup {

	
	public TransactionManager getTransactionManager(Properties props) throws HibernateException {
		Bundle hostBundle = FrameworkUtil.getBundle(TransactionManager.class);
		ServiceReference ref = hostBundle.getBundleContext().getServiceReference(TransactionManager.class.getName());
		return (TransactionManager) hostBundle.getBundleContext().getService(ref);

	}

	public String getUserTransactionName() {
		return TransactionManager.class.getName();
	}

	// new in Hibernate 3.3
	public Object getTransactionIdentifier(Transaction transaction) {
		return transaction;
	}

}
