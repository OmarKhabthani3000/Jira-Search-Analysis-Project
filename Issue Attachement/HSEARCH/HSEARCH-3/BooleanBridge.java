//$Id: $
package org.hibernate.search.bridge.builtin;

import org.hibernate.search.bridge.TwoWayStringBridge;


/**
 * Map a boolean field
 *
 * @author Sylvain Vieujot
 */
public class BooleanBridge implements TwoWayStringBridge {

	public Boolean stringToObject(String stringValue) {
		return Boolean.valueOf( stringValue );
	}

	public String objectToString(Object object) {
		Boolean b = (Boolean) object;
		return b.toString();
	}
}
