package org.hibernate.transaction;

/**
 * TransactionManager lookup strategy for Apache Geronimo.
 * @author Jonas Andersen
 */
public class GeronimoTransactionManagerLookup extends JNDITransactionManagerLookup {

	public static final String TX_USER_NAME = "java:comp/UserTransaction";
	public static final String TX_MGR_NAME = "java:/TransactionManager";

	@Override
	public String getUserTransactionName() {
		return TX_USER_NAME;
	}

	@Override
	protected String getName() {
		return TX_MGR_NAME;
	}

}
