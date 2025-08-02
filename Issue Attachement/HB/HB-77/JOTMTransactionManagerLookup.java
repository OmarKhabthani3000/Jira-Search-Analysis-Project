package net.sf.hibernate.transaction;

import java.util.Properties;

import javax.transaction.TransactionManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.hibernate.HibernateException;

/**
 * TransactionManager lookup strategy for JOTM
 * @author lowhs
 */
public class JOTMTransactionManagerLookup implements TransactionManagerLookup {
	
	private static final Log log = LogFactory.getLog(JOTMTransactionManagerLookup.class);
	
	/**
	 * @see net.sf.hibernate.transaction.TransactionManagerLookup#getTransactionManager(Properties)
	 */
	public TransactionManager getTransactionManager(Properties props) throws HibernateException {
		try {
			Class clazz=null;
			try {
				clazz = Class.forName("org.objectweb.jotm.Current");
			}
			catch (Exception e) {
			}
			return (TransactionManager) clazz
			.getMethod("getTransactionManager", null)
			.invoke(null, null);
		}
		catch (Exception e) {
			throw new HibernateException( "Could not obtain JOTM transaction manager instance", e );
		}
	}
	
	public String getUserTransactionName() {
		return "java:comp/UserTransaction";
	}
	
}






