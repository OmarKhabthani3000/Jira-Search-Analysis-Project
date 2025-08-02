package org.hibernate.transform;

import java.util.Map;

/**
 * Used to enable generic creation of objects from value. For example, it can be
 * used to construct an 'enum' object from a given String value which uniquely
 * identifies the enum.
 * 
 * @author Antony Stubbs <antony.stubbs@gmail.com>
 * @see PassThroughTransformer
 */
public interface ResultElementTransformer {
	/**
	 * Generates custom Map which instead of simply generating a String->String
	 * map as in {@link PassThroughTransformer}, it could generate any types of
	 * object to object mapping as per the Map interface.
	 */
	public Map generateMap(Object key, Object value, Object[] entireResult);
}