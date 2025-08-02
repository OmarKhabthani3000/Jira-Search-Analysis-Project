package org.hibernate.orm.test.mapping.converted.converter;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Objects;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.type.SqlTypes;
import org.hibernate.usertype.UserType;

public class BooleanLongUserType implements UserType<Boolean> {

	private static final Long LONG_TRUE = 1L;

	@Override
	public int getSqlType() {
		return SqlTypes.BIGINT;
	}

	@Override
	public Class<Boolean> returnedClass() {
		return Boolean.class;
	}

	@Override
	public boolean equals(Boolean x, Boolean y) {
		return Objects.equals( x, y );
	}

	@Override
	public int hashCode(Boolean x) {
		return Objects.hashCode( x );
	}

	@Override
	public Boolean nullSafeGet(ResultSet rs, int position, SharedSessionContractImplementor session, Object owner)
			throws SQLException {
		final var dbData = rs.getLong( position );
		return !rs.wasNull() && LONG_TRUE.equals( dbData );
	}

	@Override
	public void nullSafeSet(
			PreparedStatement st,
			Boolean value,
			int index,
			SharedSessionContractImplementor session) throws SQLException {
		if ( Boolean.TRUE.equals( value ) ) {
			st.setLong( index, LONG_TRUE );
		}
		else {
			st.setNull( index, Types.NUMERIC );
		}
	}

	@Override
	public Boolean deepCopy(Boolean value) {
		return value;
	}

	@Override
	public boolean isMutable() {
		return false;
	}

	@Override
	public Serializable disassemble(Boolean value) {
		return value;
	}

	@Override
	public Boolean assemble(Serializable cached, Object owner) {
		return (Boolean) cached;
	}
}
