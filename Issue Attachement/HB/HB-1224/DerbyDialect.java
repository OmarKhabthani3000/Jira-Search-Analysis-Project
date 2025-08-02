/*
 * Created on Sep 13, 2004 by Eclipse
 *
 */
package net.sf.hibernate.dialect;

import net.sf.hibernate.sql.*;

/**
 * @author Simon Johnston
 *
 * Hibernate Dialect for Cloudscape 10 - aka Derby. This implements both an 
 * override for the identity column generator as well as for the case statement
 * issue documented at:
 * http://www.jroller.com/comments/kenlars99/Weblog/cloudscape_soon_to_be_derby
 */
public class DerbyDialect extends DB2Dialect {

	/**
	 * This is different in Cloudscape to DB2.
	 */
	public String getIdentityColumnString() {
		return "not null generated always as identity"; //$NON-NLS-1
	}

	/**
	 * Return the case statement modified for Cloudscape.
	 */
	public CaseFragment createCaseFragment()
	{
		return new DerbyCaseFragment();
	}
}
