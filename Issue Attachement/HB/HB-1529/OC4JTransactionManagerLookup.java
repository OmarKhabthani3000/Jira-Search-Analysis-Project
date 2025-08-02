package be.cm.comps.entitymanager.plugin.hibernate2.util;

import net.sf.hibernate.transaction.JNDITransactionManagerLookup;

/**
 * Locates a <tt>TransactionManager</tt> in JNDI.
 * @author Stijn Janssens
 */
public class OC4JTransactionManagerLookup extends JNDITransactionManagerLookup {
		
		/**
		 * @see net.sf.hibernate.transaction.JNDITransactionManagerLookup#getName()
		 */
		public String getName() {
			return "java:comp/pm/TransactionManager";
		}
		
		public String getUserTransactionName() {
			return "java:comp/UserTransaction";
		}
		
	}
