/*
 * Created on 11/11/2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.hibernate.util;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Vector;

/**
 * @author root
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class NestedHolderClass {
    Class clazz;
    List params;
    Constructor holderConstructor;
    public NestedHolderClass (Class clazz){
        this.clazz = clazz;
        params = new Vector();
    }
    
    public List getParams(){
        return params;
    }
    
    public Class getClazz() {
        return clazz;
    }
    
    public Constructor getHolderConstructor() {
        return holderConstructor;
    }
    
    public void setHolderConstructor(Constructor holderConstructor) {
        this.holderConstructor = holderConstructor;
    }
}
