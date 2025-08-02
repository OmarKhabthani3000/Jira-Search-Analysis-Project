package org.hibernate.test.legacy;

import static org.junit.Assert.assertFalse;

import java.util.List;

import org.hibernate.QueryException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.cfg.Environment;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.dialect.function.SQLFunction;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.type.Type;
import org.junit.Test;

public class SQLFunctionsTest2 extends BaseCoreFunctionalTestCase {
	private static final String FNAME = "afunc";

	@Override
	public String[] getMappings() {
		return new String[] { "legacy/AltSimple.hbm.xml", "legacy/Broken.hbm.xml", "legacy/Blobber.hbm.xml" };
	}

	@Test
	public void testSQLFunctions() throws Exception {
		rebuildSessionFactory(c -> c.setProperty(Environment.DIALECT, CustomH2Dialect.class.getName()));
		Session s = openSession();
		Transaction t = s.beginTransaction();
		Simple simple = new Simple(Long.valueOf(10));
		simple.setName("Simple 1");
		s.save(simple);

		s.createQuery("select " + FNAME + "(s.date) from Simple s ");

		assertFalse("render should not be called with firstArgumentType null", CustomH2Dialect.function.firstArgumentTypeWasNull);

		s.delete(simple);
		t.commit();
		s.close();

	}

	private static class CustomTruncFunction implements SQLFunction {
		public boolean firstArgumentTypeWasNull;

		@Override
		public boolean hasArguments() {
			return true;
		}

		@Override
		public boolean hasParenthesesIfNoArguments() {
			return true;
		}

		@Override
		public Type getReturnType(Type firstArgumentType, Mapping mapping) throws QueryException {
			return firstArgumentType;
		}

		@Override
		public String render(Type firstArgumentType, List arguments, SessionFactoryImplementor factory)
				throws QueryException {
			if (firstArgumentType == null)
				firstArgumentTypeWasNull = true;

			final StringBuilder buf = new StringBuilder();
			buf.append(FNAME).append('(');
			for (int i = 0; i < arguments.size(); i++) {
				buf.append(arguments.get(i));
				if (i < arguments.size() - 1) {
					buf.append(", ");
				}
			}
			return buf.append(')').toString();
		}

		@Override
		public String toString() {
			return FNAME;
		}
	}

	public static class CustomH2Dialect extends H2Dialect {
		public static final CustomTruncFunction function = new CustomTruncFunction();

		public CustomH2Dialect() {
			getFunctions().put(FNAME, function);
		}
	}
}
