import org.hibernate.dialect.FirebirdDialect;

public class FirebirdDialectTest extends junit.framework.TestCase {
	private static final String PLAIN_SELECT = "select * from test";
	private static final String COMMENTED_SELECT = "/* test comment */ select * from test";
	private static final String PLAIN_LIMITED_SELECT = "select first ? * from test";
	private static final String COMMENTED_LIMITED_SELECT = "/* test comment */ select first ? * from test";
	private static final String PLAIN_LIMITED_SELECT_WITH_OFFSET = "select first ? skip ? * from test";
	private static final String COMMENTED_LIMITED_SELECT_WITH_OFFSET = "/* test comment */ select first ? skip ? * from test";

	public FirebirdDialectTest(String string) {
		super(string);
	}
	
	public void testLimitString() {
		final FirebirdDialect dialect = new FirebirdDialect();
		assertEquals(PLAIN_LIMITED_SELECT, dialect.getLimitString(PLAIN_SELECT, false));
		assertEquals(COMMENTED_LIMITED_SELECT, dialect.getLimitString(COMMENTED_SELECT, false));
		assertEquals(PLAIN_LIMITED_SELECT_WITH_OFFSET, dialect.getLimitString(PLAIN_SELECT, true));
		assertEquals(COMMENTED_LIMITED_SELECT_WITH_OFFSET, dialect.getLimitString(COMMENTED_SELECT, true));
	}

}

