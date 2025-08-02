/**
 * @(#)SampleEntityDAO.java
 *
 * Copyright 2008 8D Technologies, Inc. All Rights Reserved.
 *
 * This software is the proprietary information of 8D Technologies, Inc.
 * Use is subject to license terms.
 */

package com.eightd.ftk.bike.alert.job;

import com.eightd.ftk.db.DBEnum;
import com.eightd.ftk.db.model.GenericDAO;

public class SampleEntityDAO extends GenericDAO<SampleEntity>{

	public SampleEntityDAO() {
		super(DBEnum.MAIN);
	}

	public SampleEntityDAO(DBEnum dbType) {
		super(dbType);
	}

}
