package at.jku.camas.core.persistence.hibernate;

import java.io.InputStream;
import java.io.Reader;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.type.CustomType;
import org.hibernate.type.Type;
import org.hibernate.type.TypeFactory;
import org.hibernate.usertype.ParameterizedType;
import org.hibernate.usertype.UserType;

public class EnumType extends org.hibernate.type.EnumType implements UserType, ParameterizedType, Serializable {
	private static final long serialVersionUID = 1L;
	
	protected UserType delegate = null;

	protected String typeName;

	protected String enumName;
	
	public EnumType() {
        super();
    }

    public int[] sqlTypes() {
        return getDelegate().sqlTypes();
    }

    public Class<?> returnedClass() {
        return super.returnedClass();
    }

    public boolean equals(Object x, Object y) throws HibernateException {
        return super.equals(x, y);
        //return (x == y) || (x != null && y != null && (x.equals(y)));
    }

    public Object nullSafeGet(ResultSet rs, String[] names, Object owner) throws HibernateException, SQLException {
        final Object o = getDelegate().nullSafeGet(rs, names, owner);
        rs = new ResultSet() {
			@Override public <T> T unwrap(Class<T> iface) throws SQLException { throw new UnsupportedOperationException(); }
			@Override public boolean isWrapperFor(Class<?> iface) throws SQLException { throw new UnsupportedOperationException(); }
			@Override public boolean next() throws SQLException { return false; }
			@Override
			public void close() throws SQLException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public boolean wasNull() throws SQLException {
				return o == null;
			}

			@Override
			public String getString(int columnIndex) throws SQLException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public boolean getBoolean(int columnIndex) throws SQLException {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public byte getByte(int columnIndex) throws SQLException {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public short getShort(int columnIndex) throws SQLException {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public int getInt(int columnIndex) throws SQLException {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public long getLong(int columnIndex) throws SQLException {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public float getFloat(int columnIndex) throws SQLException {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public double getDouble(int columnIndex) throws SQLException {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public BigDecimal getBigDecimal(int columnIndex, int scale)
					throws SQLException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public byte[] getBytes(int columnIndex) throws SQLException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Date getDate(int columnIndex) throws SQLException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Time getTime(int columnIndex) throws SQLException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Timestamp getTimestamp(int columnIndex) throws SQLException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public InputStream getAsciiStream(int columnIndex)
					throws SQLException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public InputStream getUnicodeStream(int columnIndex)
					throws SQLException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public InputStream getBinaryStream(int columnIndex)
					throws SQLException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getString(String columnLabel) throws SQLException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public boolean getBoolean(String columnLabel) throws SQLException {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public byte getByte(String columnLabel) throws SQLException {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public short getShort(String columnLabel) throws SQLException {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public int getInt(String columnLabel) throws SQLException {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public long getLong(String columnLabel) throws SQLException {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public float getFloat(String columnLabel) throws SQLException {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public double getDouble(String columnLabel) throws SQLException {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public BigDecimal getBigDecimal(String columnLabel, int scale)
					throws SQLException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public byte[] getBytes(String columnLabel) throws SQLException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Date getDate(String columnLabel) throws SQLException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Time getTime(String columnLabel) throws SQLException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Timestamp getTimestamp(String columnLabel)
					throws SQLException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public InputStream getAsciiStream(String columnLabel)
					throws SQLException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public InputStream getUnicodeStream(String columnLabel)
					throws SQLException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public InputStream getBinaryStream(String columnLabel)
					throws SQLException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public SQLWarning getWarnings() throws SQLException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void clearWarnings() throws SQLException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public String getCursorName() throws SQLException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public ResultSetMetaData getMetaData() throws SQLException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Object getObject(int columnIndex) throws SQLException {
				return o;
			}

			@Override
			public Object getObject(String columnLabel) throws SQLException {
				return o;
			}

			@Override
			public int findColumn(String columnLabel) throws SQLException {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public Reader getCharacterStream(int columnIndex)
					throws SQLException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Reader getCharacterStream(String columnLabel)
					throws SQLException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public BigDecimal getBigDecimal(int columnIndex)
					throws SQLException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public BigDecimal getBigDecimal(String columnLabel)
					throws SQLException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public boolean isBeforeFirst() throws SQLException {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean isAfterLast() throws SQLException {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean isFirst() throws SQLException {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean isLast() throws SQLException {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void beforeFirst() throws SQLException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void afterLast() throws SQLException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public boolean first() throws SQLException {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean last() throws SQLException {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public int getRow() throws SQLException {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public boolean absolute(int row) throws SQLException {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean relative(int rows) throws SQLException {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean previous() throws SQLException {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void setFetchDirection(int direction) throws SQLException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public int getFetchDirection() throws SQLException {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public void setFetchSize(int rows) throws SQLException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public int getFetchSize() throws SQLException {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public int getType() throws SQLException {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public int getConcurrency() throws SQLException {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public boolean rowUpdated() throws SQLException {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean rowInserted() throws SQLException {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean rowDeleted() throws SQLException {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void updateNull(int columnIndex) throws SQLException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void updateBoolean(int columnIndex, boolean x)
					throws SQLException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void updateByte(int columnIndex, byte x) throws SQLException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void updateShort(int columnIndex, short x)
					throws SQLException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void updateInt(int columnIndex, int x) throws SQLException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void updateLong(int columnIndex, long x) throws SQLException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void updateFloat(int columnIndex, float x)
					throws SQLException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void updateDouble(int columnIndex, double x)
					throws SQLException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void updateBigDecimal(int columnIndex, BigDecimal x)
					throws SQLException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void updateString(int columnIndex, String x)
					throws SQLException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void updateBytes(int columnIndex, byte[] x)
					throws SQLException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void updateDate(int columnIndex, Date x) throws SQLException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void updateTime(int columnIndex, Time x) throws SQLException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void updateTimestamp(int columnIndex, Timestamp x)
					throws SQLException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void updateAsciiStream(int columnIndex, InputStream x,
					int length) throws SQLException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void updateBinaryStream(int columnIndex, InputStream x,
					int length) throws SQLException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void updateCharacterStream(int columnIndex, Reader x,
					int length) throws SQLException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void updateObject(int columnIndex, Object x,
					int scaleOrLength) throws SQLException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void updateObject(int columnIndex, Object x)
					throws SQLException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void updateNull(String columnLabel) throws SQLException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void updateBoolean(String columnLabel, boolean x)
					throws SQLException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void updateByte(String columnLabel, byte x)
					throws SQLException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void updateShort(String columnLabel, short x)
					throws SQLException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void updateInt(String columnLabel, int x)
					throws SQLException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void updateLong(String columnLabel, long x)
					throws SQLException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void updateFloat(String columnLabel, float x)
					throws SQLException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void updateDouble(String columnLabel, double x)
					throws SQLException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void updateBigDecimal(String columnLabel, BigDecimal x)
					throws SQLException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void updateString(String columnLabel, String x)
					throws SQLException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void updateBytes(String columnLabel, byte[] x)
					throws SQLException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void updateDate(String columnLabel, Date x)
					throws SQLException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void updateTime(String columnLabel, Time x)
					throws SQLException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void updateTimestamp(String columnLabel, Timestamp x)
					throws SQLException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void updateAsciiStream(String columnLabel, InputStream x,
					int length) throws SQLException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void updateBinaryStream(String columnLabel, InputStream x,
					int length) throws SQLException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void updateCharacterStream(String columnLabel,
					Reader reader, int length) throws SQLException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void updateObject(String columnLabel, Object x,
					int scaleOrLength) throws SQLException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void updateObject(String columnLabel, Object x)
					throws SQLException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void insertRow() throws SQLException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void updateRow() throws SQLException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void deleteRow() throws SQLException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void refreshRow() throws SQLException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void cancelRowUpdates() throws SQLException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void moveToInsertRow() throws SQLException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void moveToCurrentRow() throws SQLException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public Statement getStatement() throws SQLException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Object getObject(int columnIndex, Map<String, Class<?>> map)
					throws SQLException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Ref getRef(int columnIndex) throws SQLException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Blob getBlob(int columnIndex) throws SQLException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Clob getClob(int columnIndex) throws SQLException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Array getArray(int columnIndex) throws SQLException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Object getObject(String columnLabel,
					Map<String, Class<?>> map) throws SQLException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Ref getRef(String columnLabel) throws SQLException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Blob getBlob(String columnLabel) throws SQLException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Clob getClob(String columnLabel) throws SQLException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Array getArray(String columnLabel) throws SQLException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Date getDate(int columnIndex, Calendar cal)
					throws SQLException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Date getDate(String columnLabel, Calendar cal)
					throws SQLException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Time getTime(int columnIndex, Calendar cal)
					throws SQLException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Time getTime(String columnLabel, Calendar cal)
					throws SQLException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Timestamp getTimestamp(int columnIndex, Calendar cal)
					throws SQLException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Timestamp getTimestamp(String columnLabel, Calendar cal)
					throws SQLException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public URL getURL(int columnIndex) throws SQLException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public URL getURL(String columnLabel) throws SQLException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void updateRef(int columnIndex, Ref x) throws SQLException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void updateRef(String columnLabel, Ref x)
					throws SQLException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void updateBlob(int columnIndex, Blob x) throws SQLException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void updateBlob(String columnLabel, Blob x)
					throws SQLException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void updateClob(int columnIndex, Clob x) throws SQLException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void updateClob(String columnLabel, Clob x)
					throws SQLException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void updateArray(int columnIndex, Array x)
					throws SQLException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void updateArray(String columnLabel, Array x)
					throws SQLException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public RowId getRowId(int columnIndex) throws SQLException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public RowId getRowId(String columnLabel) throws SQLException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void updateRowId(int columnIndex, RowId x)
					throws SQLException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void updateRowId(String columnLabel, RowId x)
					throws SQLException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public int getHoldability() throws SQLException {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public boolean isClosed() throws SQLException {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void updateNString(int columnIndex, String nString)
					throws SQLException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void updateNString(String columnLabel, String nString)
					throws SQLException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void updateNClob(int columnIndex, NClob nClob)
					throws SQLException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void updateNClob(String columnLabel, NClob nClob)
					throws SQLException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public NClob getNClob(int columnIndex) throws SQLException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public NClob getNClob(String columnLabel) throws SQLException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public SQLXML getSQLXML(int columnIndex) throws SQLException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public SQLXML getSQLXML(String columnLabel) throws SQLException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void updateSQLXML(int columnIndex, SQLXML xmlObject)
					throws SQLException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void updateSQLXML(String columnLabel, SQLXML xmlObject)
					throws SQLException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public String getNString(int columnIndex) throws SQLException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getNString(String columnLabel) throws SQLException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Reader getNCharacterStream(int columnIndex)
					throws SQLException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Reader getNCharacterStream(String columnLabel)
					throws SQLException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void updateNCharacterStream(int columnIndex, Reader x,
					long length) throws SQLException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void updateNCharacterStream(String columnLabel,
					Reader reader, long length) throws SQLException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void updateAsciiStream(int columnIndex, InputStream x,
					long length) throws SQLException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void updateBinaryStream(int columnIndex, InputStream x,
					long length) throws SQLException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void updateCharacterStream(int columnIndex, Reader x,
					long length) throws SQLException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void updateAsciiStream(String columnLabel, InputStream x,
					long length) throws SQLException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void updateBinaryStream(String columnLabel, InputStream x,
					long length) throws SQLException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void updateCharacterStream(String columnLabel,
					Reader reader, long length) throws SQLException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void updateBlob(int columnIndex, InputStream inputStream,
					long length) throws SQLException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void updateBlob(String columnLabel, InputStream inputStream,
					long length) throws SQLException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void updateClob(int columnIndex, Reader reader, long length)
					throws SQLException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void updateClob(String columnLabel, Reader reader,
					long length) throws SQLException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void updateNClob(int columnIndex, Reader reader, long length)
					throws SQLException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void updateNClob(String columnLabel, Reader reader,
					long length) throws SQLException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void updateNCharacterStream(int columnIndex, Reader x)
					throws SQLException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void updateNCharacterStream(String columnLabel, Reader reader)
					throws SQLException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void updateAsciiStream(int columnIndex, InputStream x)
					throws SQLException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void updateBinaryStream(int columnIndex, InputStream x)
					throws SQLException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void updateCharacterStream(int columnIndex, Reader x)
					throws SQLException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void updateAsciiStream(String columnLabel, InputStream x)
					throws SQLException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void updateBinaryStream(String columnLabel, InputStream x)
					throws SQLException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void updateCharacterStream(String columnLabel, Reader reader)
					throws SQLException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void updateBlob(int columnIndex, InputStream inputStream)
					throws SQLException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void updateBlob(String columnLabel, InputStream inputStream)
					throws SQLException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void updateClob(int columnIndex, Reader reader)
					throws SQLException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void updateClob(String columnLabel, Reader reader)
					throws SQLException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void updateNClob(int columnIndex, Reader reader)
					throws SQLException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void updateNClob(String columnLabel, Reader reader)
					throws SQLException {
				// TODO Auto-generated method stub
				
			}
        	
        	
        };
        return super.nullSafeGet(rs, names, owner);
    }

    public void nullSafeSet(final PreparedStatement st, Object value, int index) throws HibernateException, SQLException {
    	PreparedStatement s = new PreparedStatement() {
			@Override
			public <T> T unwrap(Class<T> iface) throws SQLException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public boolean isWrapperFor(Class<?> iface) throws SQLException {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public void setQueryTimeout(int seconds) throws SQLException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setPoolable(boolean poolable) throws SQLException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setMaxRows(int max) throws SQLException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setMaxFieldSize(int max) throws SQLException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setFetchSize(int rows) throws SQLException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setFetchDirection(int direction) throws SQLException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setEscapeProcessing(boolean enable) throws SQLException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setCursorName(String name) throws SQLException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public boolean isPoolable() throws SQLException {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public boolean isClosed() throws SQLException {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public SQLWarning getWarnings() throws SQLException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public int getUpdateCount() throws SQLException {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public int getResultSetType() throws SQLException {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public int getResultSetHoldability() throws SQLException {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public int getResultSetConcurrency() throws SQLException {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public ResultSet getResultSet() throws SQLException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public int getQueryTimeout() throws SQLException {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public boolean getMoreResults(int current) throws SQLException {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public boolean getMoreResults() throws SQLException {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public int getMaxRows() throws SQLException {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public int getMaxFieldSize() throws SQLException {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public ResultSet getGeneratedKeys() throws SQLException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public int getFetchSize() throws SQLException {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public int getFetchDirection() throws SQLException {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public Connection getConnection() throws SQLException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public int executeUpdate(String sql, String[] columnNames)
					throws SQLException {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public int executeUpdate(String sql, int[] columnIndexes)
					throws SQLException {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public int executeUpdate(String sql, int autoGeneratedKeys)
					throws SQLException {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public int executeUpdate(String sql) throws SQLException {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public ResultSet executeQuery(String sql) throws SQLException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public int[] executeBatch() throws SQLException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public boolean execute(String sql, String[] columnNames)
					throws SQLException {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public boolean execute(String sql, int[] columnIndexes) throws SQLException {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public boolean execute(String sql, int autoGeneratedKeys)
					throws SQLException {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public boolean execute(String sql) throws SQLException {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public void close() throws SQLException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void clearWarnings() throws SQLException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void clearBatch() throws SQLException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void cancel() throws SQLException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void addBatch(String sql) throws SQLException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setUnicodeStream(int parameterIndex, InputStream x, int length)
					throws SQLException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setURL(int parameterIndex, URL x) throws SQLException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal)
					throws SQLException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setTimestamp(int parameterIndex, Timestamp x)
					throws SQLException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setTime(int parameterIndex, Time x, Calendar cal)
					throws SQLException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setTime(int parameterIndex, Time x) throws SQLException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setString(int parameterIndex, String x) throws SQLException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setShort(int parameterIndex, short x) throws SQLException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setSQLXML(int parameterIndex, SQLXML xmlObject)
					throws SQLException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setRowId(int parameterIndex, RowId x) throws SQLException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setRef(int parameterIndex, Ref x) throws SQLException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setObject(int parameterIndex, Object x, int targetSqlType,
					int scaleOrLength) throws SQLException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {
				getDelegate().nullSafeSet(st, x, parameterIndex);
			}
			
			@Override
			public void setObject(int parameterIndex, Object x) throws SQLException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setNull(int parameterIndex, int sqlType, String typeName)
					throws SQLException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setNull(int parameterIndex, int sqlType) throws SQLException {
				getDelegate().nullSafeSet(st, null, parameterIndex);
			}
			
			@Override
			public void setNString(int parameterIndex, String value)
					throws SQLException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setNClob(int parameterIndex, Reader reader, long length)
					throws SQLException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setNClob(int parameterIndex, Reader reader) throws SQLException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setNClob(int parameterIndex, NClob value) throws SQLException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setNCharacterStream(int parameterIndex, Reader value,
					long length) throws SQLException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setNCharacterStream(int parameterIndex, Reader value)
					throws SQLException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setLong(int parameterIndex, long x) throws SQLException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setInt(int parameterIndex, int x) throws SQLException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setFloat(int parameterIndex, float x) throws SQLException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setDouble(int parameterIndex, double x) throws SQLException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setDate(int parameterIndex, Date x, Calendar cal)
					throws SQLException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setDate(int parameterIndex, Date x) throws SQLException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setClob(int parameterIndex, Reader reader, long length)
					throws SQLException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setClob(int parameterIndex, Reader reader) throws SQLException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setClob(int parameterIndex, Clob x) throws SQLException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setCharacterStream(int parameterIndex, Reader reader,
					long length) throws SQLException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setCharacterStream(int parameterIndex, Reader reader, int length)
					throws SQLException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setCharacterStream(int parameterIndex, Reader reader)
					throws SQLException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setBytes(int parameterIndex, byte[] x) throws SQLException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setByte(int parameterIndex, byte x) throws SQLException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setBoolean(int parameterIndex, boolean x) throws SQLException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setBlob(int parameterIndex, InputStream inputStream, long length)
					throws SQLException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setBlob(int parameterIndex, InputStream inputStream)
					throws SQLException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setBlob(int parameterIndex, Blob x) throws SQLException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setBinaryStream(int parameterIndex, InputStream x, long length)
					throws SQLException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setBinaryStream(int parameterIndex, InputStream x, int length)
					throws SQLException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setBinaryStream(int parameterIndex, InputStream x)
					throws SQLException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setBigDecimal(int parameterIndex, BigDecimal x)
					throws SQLException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setAsciiStream(int parameterIndex, InputStream x, long length)
					throws SQLException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setAsciiStream(int parameterIndex, InputStream x, int length)
					throws SQLException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setAsciiStream(int parameterIndex, InputStream x)
					throws SQLException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setArray(int parameterIndex, Array x) throws SQLException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public ParameterMetaData getParameterMetaData() throws SQLException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public ResultSetMetaData getMetaData() throws SQLException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public int executeUpdate() throws SQLException {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public ResultSet executeQuery() throws SQLException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public boolean execute() throws SQLException {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public void clearParameters() throws SQLException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void addBatch() throws SQLException {
				// TODO Auto-generated method stub
				
			}
		}; 
        super.nullSafeSet(s, value, index);
    }

    public Object deepCopy(Object value) throws HibernateException {
        return super.deepCopy(value);
    }

    public boolean isMutable() {
        return super.isMutable();
    }

	public Object assemble(Serializable cached, Object owner) throws HibernateException {
		return super.assemble(cached, owner);
	}

	public Serializable disassemble(Object value) throws HibernateException {
		return super.disassemble(value);
	}

	public int hashCode(Object x) throws HibernateException {
		return super.hashCode(x);
	}

	public Object replace(Object original, Object target, Object owner) throws HibernateException {
		return super.replace(original, target, owner);
	}

	public void setParameterValues(Properties parameters) {
		if (parameters == null) return;
		this.typeName = parameters.getProperty("typeName");
		this.enumName = parameters.getProperty("enumName");
//		Properties typeProperties = new Properties(parameters);
//		Set<Object> ks = parameters.keySet();
//		for(Object key : keySet) {
//			if ("typeName".equals(key)) continue;
//			
//		}
	}
	
	public UserType getDelegate() {
		if (delegate != null) return delegate;
		Properties properties = new Properties();
		Type type = TypeFactory.heuristicType(typeName, properties);
		if (type instanceof UserType) {
			delegate = (UserType)type;
		}
		else
		if (type instanceof CustomType) {
			try {
				Field f = type.getClass().getDeclaredField("userType");
				boolean accessible = f.isAccessible();
				f.setAccessible(true);
				delegate = (UserType)f.get(type);
				f.setAccessible(accessible);
			} catch (SecurityException e) {
				throw new IllegalArgumentException("typeName=" + typeName + ", type=" + type + " not usable as UserType", e);
			} catch (NoSuchFieldException e) {
				throw new IllegalArgumentException("typeName=" + typeName + ", type=" + type + " not usable as UserType", e);
			} catch (IllegalArgumentException e) {
				throw new IllegalArgumentException("typeName=" + typeName + ", type=" + type + " not usable as UserType", e);
			} catch (IllegalAccessException e) {
				throw new IllegalArgumentException("typeName=" + typeName + ", type=" + type + " not usable as UserType", e);
			}
		}
		else
			throw new IllegalArgumentException("typeName=" + typeName + ", type=" + type + " not usable as UserType");
		
		Properties parameters = new Properties();
		parameters.put(ENUM, this.enumName);
		int[] sqlTypes = delegate.sqlTypes();
		parameters.put(TYPE, sqlTypes[0]);
		super.setParameterValues(parameters);
		return delegate;
	}
}
