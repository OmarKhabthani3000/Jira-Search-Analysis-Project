/**
 * @(#)SampleEntity.java
 *
 * Copyright 2008 8D Technologies, Inc. All Rights Reserved.
 *
 * This software is the proprietary information of 8D Technologies, Inc.
 * Use is subject to license terms.
 */

package com.eightd.ftk.bike.alert.job;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class SampleEntity {

	private long id = 0;
	private String field1 = null;

	public SampleEntity() {
    }

	@Id
	@GeneratedValue
    public long getId() {
        return id;
    }

    protected void setId(long id) {
        this.id = id;
    }

	public String getField1() {
		return field1;
	}

	public void setField1(String field1) {
		this.field1 = field1;
	}
	
}
