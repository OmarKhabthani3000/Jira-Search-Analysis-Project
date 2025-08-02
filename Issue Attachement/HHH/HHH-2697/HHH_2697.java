/*
 * Copyright 2012 8D Technologies, Inc. All Rights Reserved.
 *
 * This software is the proprietary information of 8D Technologies, Inc.
 * Use is subject to license terms.
 *
 */
package com.eightd.ftk.bike.alert.job;

import org.hibernate.SQLQuery;


public class HHH_2697 {

	public static void main( String[] args ) throws Exception {
		SampleEntityDAO sampleEntityDAO = new SampleEntityDAO();

		String sql = "select " + //
				"@sequenceNumber := if(@previous_field1 = field1, @sequenceNumber,@sequenceNumber + 1) AS sequenceNumber, " + //
				"@previous_field1 := field1 " + //
				"from " + //
				"(select @sequenceNumber := 0) x, " + //
				"(select @previous_field1 := '') y, " + //
				"SampleEntity";

		SQLQuery query = sampleEntityDAO.getSession().createSQLQuery( sql );
		query.executeUpdate();

	}
}
