package org.hibernate.dialect;

import org.junit.Test;

import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseUnitTestCase;

import static org.junit.Assert.assertEquals;

public class Oracle8iDialectTestCase extends BaseUnitTestCase {

	@Test
	@TestForIssue(jiraKey = "HHH-9290")
	public void testTemporaryTableNameTruncation() throws Exception {
		String temporaryTableName = new Oracle8iDialect().generateTemporaryTableName(
				"TABLE_NAME_THAT_EXCEEDS_30_CHARACTERS"
		);

		assertEquals(
				"Temporary table names should be truncated to 30 characters",
				30,
				temporaryTableName.length()
		);
		assertEquals(
				"Temporary table names should start with HT_",
				"HT_TABLE_NAME_THAT_EXCEEDS_30_",
				temporaryTableName
		);
	}
}