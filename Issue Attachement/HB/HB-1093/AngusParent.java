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
 * @hibernate.class table="AngusParent" proxy="org.guidestar.uk.testing.hibernate.AngusParent"
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class AngusParent implements Serializable {
    private AngusChild angusChild;
    private Integer id=null;
    private Date lastModified;
    private Integer number;

    /**
     * @hibernate.one-to-one cascade="all" constrained="true" outer-join="false"
     * hibernate.many-to-one name="angusChild" class="org.guidestar.uk.testing.hibernate.AngusChild" unique="true" cascade="all" outer-join="false"
     */
    public AngusChild getAngusChild() {
        return angusChild;
    }

    /**
	 * @hibernate.id generator-class="identity" type="java.lang.Integer" column="id" unsaved-value="null"
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

    /**
     * @hibernate.property name="number" column="number" type="java.lang.Integer"
     */
    public Integer getNumber() {
        return number;
    }
    public void setAngusChild(AngusChild child) {
        angusChild = child;
    }

    public void setId(Integer integer) {
        id = integer;
    }
    public void setLastModified(Date date) {
        lastModified = date;
    }
    public void setNumber(Integer integer) {
        number = integer;
    }

}
