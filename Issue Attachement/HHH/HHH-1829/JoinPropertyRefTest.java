package org.hibernate.test.joinpropertyref;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class JoinPropertyRefTest extends AbstractJoinTest {

	public JoinPropertyRefTest(String name) {
		super(name);
	}

	protected String[] getMappings() {
		return new String[]{"joinpropertyref/Person.hbm.xml"};
	}

	protected void setJoinedTableKeyProperty(Person person, PreparedStatement statement) throws SQLException {
		statement.setString(1,person.getName());
	}

}
