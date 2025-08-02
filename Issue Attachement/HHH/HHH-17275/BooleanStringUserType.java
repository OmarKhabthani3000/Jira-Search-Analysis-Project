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

public class BooleanStringUserType implements UserType<Boolean> {

	private static final String STRING_TRUE = "1";

	@Override
	public int getSqlType() {
		return SqlTypes.VARCHAR;
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
		final var dbData = rs.getString( position );
		return STRING_TRUE.equals( dbData ) ? Boolean.TRUE : Boolean.FALSE;
	}

	@Override
	public void nullSafeSet(
			PreparedStatement st,
			Boolean value,
			int index,
			SharedSessionContractImplementor session) throws SQLException {
		if ( Boolean.TRUE.equals( value ) ) {
			st.setString( index, STRING_TRUE );
		}
		else {
			st.setNull( index, Types.VARCHAR );
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
