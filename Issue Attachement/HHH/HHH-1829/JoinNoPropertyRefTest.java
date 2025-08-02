package org.hibernate.test.joinpropertyref;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class JoinNoPropertyRefTest extends AbstractJoinTest {

	public JoinNoPropertyRefTest(String name) {
		super(name);
	}

	protected String[] getMappings() {
		return new String[]{"joinpropertyref/PersonNoPropertyRef.hbm.xml"};
	}

	protected void setJoinedTableKeyProperty(Person person, PreparedStatement statement) throws SQLException {
		statement.setLong(1,person.getId());
	}

}
