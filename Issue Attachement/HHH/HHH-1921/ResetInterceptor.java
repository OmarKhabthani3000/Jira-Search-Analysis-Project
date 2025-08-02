//$Id: PropertyInterceptor.java 7700 2005-07-30 05:02:47Z oneovthafew $
package org.hibernate.test.interceptor;

import java.io.Serializable;
import java.util.Calendar;

import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;

public class ResetInterceptor extends EmptyInterceptor {

	public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) {
		currentState[0] = "test";
		return true;
	}

}
