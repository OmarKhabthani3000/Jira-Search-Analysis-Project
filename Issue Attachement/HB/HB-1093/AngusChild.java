/*
 * Created on Jul 16, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.guidestar.uk.testing.hibernate;

import java.io.Serializable;
import java.util.Date;

/**
 * @author amezick
 * @hibernate.class table="AngusChild" proxy="org.guidestar.uk.testing.hibernate.AngusChild"
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class AngusChild implements Serializable {
    private AngusParent angusParent;
    private Integer childNumber;
    private Integer id=null;
    private Date lastModified;

    /**
    * @hibernate.one-to-one class="org.guidestar.uk.testing.hibernate.AngusParent" outer-join="false" constrained="true"
    */
    public AngusParent getAngusParent() {
        return angusParent;
    }

    /**
     * @hibernate.property name="childNumber" column="childNumber" type="java.lang.Integer"
     */
    public Integer getChildNumber() {
        return childNumber;
    }

    /**
     * @hibernate.id name="id" generator-class="foreign" type="java.lang.Integer" column="id" unsaved-value="null"
     * @hibernate.generator-param name="property" value="angusParent"
     */
    public Integer getId() {
        return id;
    }

    /**
     * @hibernate.version column="last_modified" 
     */
    public Date getLastModified() {
        return lastModified;
    }
    public void setAngusParent(AngusParent parent) {
        angusParent = parent;
    }
    public void setChildNumber(Integer integer) {
        childNumber = integer;
    }

    public void setId(Integer integer) {
        id = integer;
    }
    public void setLastModified(Date date) {
        lastModified = date;
    }

}
