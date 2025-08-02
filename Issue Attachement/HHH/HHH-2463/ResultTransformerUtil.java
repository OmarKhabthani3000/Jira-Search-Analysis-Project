/**
 * ResultTransformerUtil.java
 * 
 * Mercer Inc.
 * JBossMHR
 * Copyright 2008 All Rights Reserved
 * @since 1.0 May 14, 2008
 * =============================================================================================
 * $Id: ResultTransformerUtil.java,v 1.1 2008/05/14 14:44:23 abhishekm Exp $
 * =============================================================================================
 */
package com.mercer.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.transform.AliasToBeanResultTransformer;

/**
 * The Class ResultTransformerUtil.
 * 
 * @author Abhishek Mirge
 */
public class ResultTransformerUtil {

	/**
	 * Transform to bean.
	 * 
	 * @param resultClass the result class
	 * @param aliasList the alias list
	 * @param resultList the result list
	 * 
	 * @return the list
	 */
	public static List transformToBean(Class resultClass, String aliasList[], List resultList) {
		List transformList = new ArrayList();
		if (aliasList != null && !CollectionUtil.isEmpty(resultList)) {
			AliasToBeanResultTransformer tr = new AliasToBeanResultTransformer(resultClass);
			Iterator it = resultList.iterator();
			Object[] obj;
			while (it.hasNext()) {
				obj = (Object[]) it.next();
				transformList.add(tr.transformTuple(obj, aliasList));
			}
		}
		return transformList;
	}

	/**
	 * Transform to map.
	 * 
	 * @param aliasList the alias list
	 * @param resultList the result list
	 * 
	 * @return the list
	 */
	public static List transformToMap(String aliasList[], List resultList) {
		List transformList = new ArrayList();
		if (aliasList != null && !CollectionUtil.isEmpty(resultList)) {
			Iterator it = resultList.iterator();
			Object[] obj;
			while (it.hasNext()) {
				obj = (Object[]) it.next();
				transformList.add(transformTupleTOMap(obj, aliasList));
			}
		}
		return transformList;
	}

	/**
	 * Transform tuple TO map.
	 * 
	 * @param tuple the tuple
	 * @param aliases the aliases
	 * 
	 * @return the object
	 */
	public static Object transformTupleTOMap(Object[] tuple, String[] aliases) {
		Map result = new HashMap(tuple.length);
		for (int i = 0; i < tuple.length; i++) {
			String alias = aliases[i];
			if (alias != null) {
				result.put(alias, tuple[i]);
			}
		}
		return result;
	}

}
