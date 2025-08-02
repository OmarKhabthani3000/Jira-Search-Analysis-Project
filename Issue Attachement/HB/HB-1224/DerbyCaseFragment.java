/*
 * Created on Sep 13, 2004 by Eclipse
 *
 */
package net.sf.hibernate.dialect;

import net.sf.hibernate.sql.*;
import java.util.*;

/**
 * @author Simon Johnston
 *
 * @see net.sf.hibernate.dialect.DerbyDialect
 */
public class DerbyCaseFragment extends CaseFragment {

	/**
	 * From http://www.jroller.com/comments/kenlars99/Weblog/cloudscape_soon_to_be_derby
	 * 
	 * The problem we had, was when Hibernate does a select with a case statement, for joined subclasses.
	 * This seems to be because there was no else at the end of the case statement (other dbs seem to not mind).
	 */
	public String toFragmentString() {
		StringBuffer buf = new StringBuffer( cases.size() * 15 + 10 );
		buf.append("case"); 								//$NON-NLS-1
		Iterator iter = cases.entrySet().iterator();
		while ( iter.hasNext() ) {
			Map.Entry me = (Map.Entry) iter.next();
			buf.append(" when ") 							//$NON-NLS-1
			        .append( me.getKey() )
			        .append(" is not null then ") 			//$NON-NLS-1
			        .append( me.getValue() );
		}
		// null is not considered the same type as Integer.
		buf.append(" else -1");								//$NON-NLS-1	
		buf.append(" end");									//$NON-NLS-1
		if (returnColumnName!=null) {
			buf.append(" as ")								//$NON-NLS-1
			        .append(returnColumnName);
		}
		return buf.toString();
	}
}
