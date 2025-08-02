package org.hibernate.dialect;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.engine.jdbc.CharacterStream;
import org.hibernate.type.descriptor.ValueExtractor;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
import org.hibernate.type.descriptor.sql.BasicBinder;
import org.hibernate.type.descriptor.sql.BasicExtractor;
import org.hibernate.type.descriptor.sql.ClobTypeDescriptor;
import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;

/**
 * @author Lukasz Antoniak (lukasz dot antoniak at gmail dot com)
 */
public class PostgreSQL84Dialect extends PostgreSQL82Dialect {
	private final static SqlTypeDescriptor CLOB_STREAMING_TYPE_DESCRIPTOR = new ClobTypeDescriptor() {
		@Override
		public <X> BasicBinder<X> getClobBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
			return new BasicBinder<X>( javaTypeDescriptor, this ) {
				@Override
				protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options)
						throws SQLException {
					final CharacterStream characterStream = javaTypeDescriptor.unwrap( value, CharacterStream.class, options );
					// Method PreparedStatement#setCharacterStream(int, Reader, long) is not yet implemented
					// by PostgreSQL JDBC driver. Casting to integer required.
					st.setCharacterStream( index, characterStream.asReader(), (int) characterStream.getLength() );
				}
			};
		}

		@Override
		public <X> ValueExtractor<X> getExtractor(final JavaTypeDescriptor<X> javaTypeDescriptor) {
			return new BasicExtractor<X>( javaTypeDescriptor, this ) {
				@Override
				protected X doExtract(ResultSet rs, String name, WrapperOptions options) throws SQLException {
					return javaTypeDescriptor.wrap( rs.getCharacterStream( name ), options );
				}

				@Override
				protected X doExtract(CallableStatement statement, int index, WrapperOptions options)
						throws SQLException {
					return javaTypeDescriptor.wrap( statement.getCharacterStream( index ), options );
				}

				@Override
				protected X doExtract(CallableStatement statement, String name, WrapperOptions options)
						throws SQLException {
					return javaTypeDescriptor.wrap( statement.getCharacterStream( name ), options );
				}
			};
		}
	};

	@Override
	public SqlTypeDescriptor getSqlTypeDescriptorOverride(int sqlCode) {
		SqlTypeDescriptor descriptor;
		switch ( sqlCode ) {
			case Types.CLOB: {
				descriptor = CLOB_STREAMING_TYPE_DESCRIPTOR;
				break;
			}
			default: {
				descriptor = super.getSqlTypeDescriptorOverride( sqlCode );
				break;
			}
		}
		return descriptor;
	}
}