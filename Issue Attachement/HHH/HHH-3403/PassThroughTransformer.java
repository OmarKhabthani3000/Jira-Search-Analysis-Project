/**
 * 
 */
package org.hibernate.transform;

import java.util.HashMap;
import java.util.Map;

/**
 * Pass through transformer for convenience.
 * 
 * @author Antony Stubbs <antony.stubbs@gmail.com>
 */
public class PassThroughTransformer implements ResultElementTransformer {

	/*
	 * (non-Javadoc)
	 * @see org.hibernate.transform.ResultElementTransformer#generateMap(java.lang.Object, java.lang.Object, java.lang.Object[])
	 */
	public Map generateMap(Object key, Object value, Object[] entireResult) {
		Map m = new HashMap();
		m.put(key, value);
		return m;
	}

}
