/*******************************************************************************
  * Copyright (c) 2007-2008 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributor:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package test;

import java.util.Set;
import java.util.TreeSet;

/**
 * @author Dmitry Geraskov
 *
 */
public class R {
   
   public static void main(String[] args) {
	   Set set = new TreeSet();	   
       set.add(null);	//should be the first line, or exception will be thrown.
       set.add(new T("tt3")); 
   }
}

class T implements Comparable<T>{
	private String value;
	
	public T(String str){
		value = str;
	}

	public int compareTo(T t) {
		if (t == null )
			return -1;
		return value.compareTo(t.value);
	}	
}
