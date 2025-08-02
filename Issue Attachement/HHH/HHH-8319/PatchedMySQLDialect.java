package com.suberjus.configurationModeller;

import java.sql.SQLException;

import org.hibernate.dialect.MySQL5Dialect;
import org.hibernate.exception.spi.TemplatedViolatedConstraintNameExtracter;
import org.hibernate.exception.spi.ViolatedConstraintNameExtracter;
import org.hibernate.internal.util.JdbcExceptionHelper;

public class PatchedMySQLDialect extends MySQL5Dialect {

	public ViolatedConstraintNameExtracter getViolatedConstraintNameExtracter() {
		return EXTRACTER;
	}

	private static ViolatedConstraintNameExtracter EXTRACTER = new TemplatedViolatedConstraintNameExtracter() {
		public String extractConstraintName(SQLException sqle) {
			try {
				int sqlState = Integer.valueOf(JdbcExceptionHelper.extractSqlState(sqle)).intValue();
				switch (sqlState) {
				case 23000:
					return extractUsingTemplate(" for key '", "'", sqle.getMessage());
				default:
					return null;
				}
			} catch (NumberFormatException nfe) {
				return null;
			}
		}
	};

}
