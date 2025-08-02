//$Id: ScrollableResultsImpl.java,v 1.7 2004/08/09 07:30:06 oneovthafew Exp $
package org.hibernate.impl;

import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.lang.reflect.Constructor;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.JDBCException;
import org.hibernate.MappingException;
import org.hibernate.ScrollableResults;
import org.hibernate.QueryException;
import org.hibernate.loader.Loader;
import org.hibernate.util.NestedHolderClass;
import org.hibernate.util.ReflectHelper;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.engine.QueryParameters;
import org.hibernate.hql.QueryTranslatorImpl;
import org.hibernate.type.Type;

/**
 * Implementation of the <tt>ScrollableResults</tt> interface
 * @author Gavin King
 */
public class ScrollableResultsImpl implements ScrollableResults {

	private final ResultSet rs;
	private final PreparedStatement ps;
	private final SessionImplementor sess;
	private final Loader loader;
	private final QueryParameters queryParameters;
	private final Type[] types;
	private NestedHolderClass nestedHolderClass;
	private Map nestedHolderConstructors;

	private Object[] currentRow;


	/**
	 * @see org.hibernate.ScrollableResults#scroll(int)
	 */
	public boolean scroll(int i) throws HibernateException {
		try {
			boolean result = rs.relative(i);
			prepareCurrentRow(result);
			return result;
		}
		catch (SQLException sqle) {
			throw new JDBCException(sqle);
		}
	}

	/**
	 * @see org.hibernate.ScrollableResults#first()
	 */
	public boolean first() throws HibernateException {
		try {
			boolean result = rs.first();
			prepareCurrentRow(result);
			return result;
		}
		catch (SQLException sqle) {
			throw new JDBCException(sqle);
		}
	}

	/**
	 * @see org.hibernate.ScrollableResults#last()
	 */
	public boolean last() throws HibernateException {
		try {
			boolean result = rs.last();
			prepareCurrentRow(result);
			return result;
		}
		catch (SQLException sqle) {
			throw new JDBCException(sqle);
		}
	}

	/**
	 * @see org.hibernate.ScrollableResults#next()
	 */
	public boolean next() throws HibernateException {
		try {
			boolean result = rs.next();
			prepareCurrentRow(result);
			return result;
		}
		catch (SQLException sqle) {
			throw new JDBCException(sqle);
		}
	}

	/**
	 * @see org.hibernate.ScrollableResults#previous()
	 */
	public boolean previous() throws HibernateException {
		try {
			boolean result = rs.previous();
			prepareCurrentRow(result);
			return result;
		}
		catch (SQLException sqle) {
			throw new JDBCException(sqle);
		}
	}

	/**
	 * @see org.hibernate.ScrollableResults#get()
	 */
	public Object[] get() throws HibernateException {
		return currentRow;
	}

	/**
	 * @see org.hibernate.ScrollableResults#get(int)
	 */
	public Object get(int col) throws HibernateException {
		return currentRow[col];
	}

	/**
	 * Check that the requested type is compatible with the
	 * result type, and return the column value
	 * @param col the column
	 * @param returnType a "final" type
	 */
	private Object getFinal(int col, Type returnType) throws HibernateException {
		if ( nestedHolderClass!=null ) throw new HibernateException("query specifies a holder class");
		if ( returnType.getReturnedClass()==types[col].getReturnedClass() ) {
			return get(col);
		}
		else {
			return throwInvalidColumnTypeException(col, types[col], returnType);
		}
	}

	/**
	 * Check that the requested type is compatible with the
	 * result type, and return the column value
	 * @param col the column
	 * @param returnType any type
	 */
	private Object getNonFinal(int col, Type returnType) throws HibernateException {
		if ( nestedHolderClass!=null ) throw new HibernateException("query specifies a holder class");
		if ( returnType.getReturnedClass().isAssignableFrom( types[col].getReturnedClass() ) ) {
			return get(col);
		}
		else {
			return throwInvalidColumnTypeException(col, types[col], returnType);
		}
	}

	public ScrollableResultsImpl(
	        ResultSet rs,
	        PreparedStatement ps,
	        SessionImplementor sess,
			Loader loader,
			QueryParameters queryParameters,
	        Type[] types,
			NestedHolderClass nestedHolderClass) throws MappingException {

		this.rs=rs;
		this.ps=ps;
		this.sess = sess;
		this.loader = loader;
		this.queryParameters = queryParameters;
		this.types = types;
		this.nestedHolderClass = nestedHolderClass;

		if (nestedHolderClass != null) {
			nestedHolderConstructors = new HashMap();
		}
	}

	/**
	 * @see org.hibernate.ScrollableResults#getBigDecimal(int)
	 */
	public BigDecimal getBigDecimal(int col)
	throws HibernateException {
		return (BigDecimal) getFinal(col, Hibernate.BIG_DECIMAL);
	}

	/**
	 * @see org.hibernate.ScrollableResults#getBinary(int)
	 */
	public byte[] getBinary(int col) throws HibernateException {
		return (byte[]) getFinal(col, Hibernate.BINARY);
	}

	public String getText(int col) throws HibernateException {
		return (String) getFinal(col, Hibernate.TEXT);
	}

	/**
	 * @see org.hibernate.ScrollableResults#getBlob(int)
	 */
	public Blob getBlob(int col) throws HibernateException {
		return (Blob) getNonFinal(col, Hibernate.BLOB);
	}

	/**
	 * @see org.hibernate.ScrollableResults#getClob(int)
	 */
	public Clob getClob(int col) throws HibernateException {
		return (Clob) getNonFinal(col, Hibernate.CLOB);
	}

	/**
	 * @see org.hibernate.ScrollableResults#getBoolean(int)
	 */
	public Boolean getBoolean(int col) throws HibernateException {
		return (Boolean) getFinal(col, Hibernate.BOOLEAN);
	}

	/**
	 * @see org.hibernate.ScrollableResults#getByte(int)
	 */
	public Byte getByte(int col) throws HibernateException {
		return (Byte) getFinal(col, Hibernate.BYTE);
	}

	/**
	 * @see org.hibernate.ScrollableResults#getCharacter(int)
	 */
	public Character getCharacter(int col) throws HibernateException {
		return (Character) getFinal(col, Hibernate.CHARACTER);
	}

	/**
	 * @see org.hibernate.ScrollableResults#getDate(int)
	 */
	public Date getDate(int col) throws HibernateException {
		return (Date) getNonFinal(col, Hibernate.TIMESTAMP);
	}

	public Calendar getCalendar(int col) throws HibernateException {
		return (Calendar) getNonFinal(col, Hibernate.CALENDAR);
	}

	/**
	 * @see org.hibernate.ScrollableResults#getDouble(int)
	 */
	public Double getDouble(int col) throws HibernateException {
		return (Double) getFinal(col, Hibernate.DOUBLE);
	}

	/**
	 * @see org.hibernate.ScrollableResults#getFloat(int)
	 */
	public Float getFloat(int col) throws HibernateException {
		return (Float) getFinal(col, Hibernate.FLOAT);
	}

	/**
	 * @see org.hibernate.ScrollableResults#getInteger(int)
	 */
	public Integer getInteger(int col) throws HibernateException {
		return (Integer) getFinal(col, Hibernate.INTEGER);
	}

	/**
	 * @see org.hibernate.ScrollableResults#getLong(int)
	 */
	public Long getLong(int col) throws HibernateException {
		return (Long) getFinal(col, Hibernate.LONG);
	}

	/**
	 * @see org.hibernate.ScrollableResults#getShort(int)
	 */
	public Short getShort(int col) throws HibernateException {
		return (Short) getFinal(col, Hibernate.SHORT);
	}

	/**
	 * @see org.hibernate.ScrollableResults#getString(int)
	 */
	public String getString(int col) throws HibernateException {
		return (String) getFinal(col, Hibernate.STRING);
	}

	/**
	 * @see org.hibernate.ScrollableResults#afterLast()
	 */
	public void afterLast() throws HibernateException {
		try {
			rs.afterLast();
		}
		catch (SQLException sqle) {
			throw new JDBCException(sqle);
		}
	}

	/**
	 * @see org.hibernate.ScrollableResults#beforeFirst()
	 */
	public void beforeFirst() throws HibernateException {
		try {
			rs.beforeFirst();
		}
		catch (SQLException sqle) {
			throw new JDBCException(sqle);
		}
	}

	/**
	 * @see org.hibernate.ScrollableResults#close()
	 */
	public void close() throws HibernateException {
		try {
			sess.getBatcher().closeQueryStatement(ps, rs); //not absolutely necessary
		}
		catch (SQLException sqle) {
			throw new JDBCException(sqle);
		}
	}

	/**
	 * @see org.hibernate.ScrollableResults#getLocale(int)
	 */
	public Locale getLocale(int col) throws HibernateException {
		return (Locale) getFinal(col, Hibernate.LOCALE);
	}

	/**
	 * @see org.hibernate.ScrollableResults#getCurrency(int)
	 */
	/*public Currency getCurrency(int col) throws HibernateException {
		return (Currency) get(col);
	}*/

	/**
	 * @see org.hibernate.ScrollableResults#getTimeZone(int)
	 */
	public TimeZone getTimeZone(int col) throws HibernateException {
		return (TimeZone) getNonFinal(col, Hibernate.TIMEZONE);
	}


	/**
	 * @see org.hibernate.ScrollableResults#getType(int)
	 */
	public Type getType(int i) {
		return types[i];
	}

	/**
	 * @see org.hibernate.ScrollableResults#isFirst()
	 */
	public boolean isFirst() throws HibernateException {
		try {
			return rs.isFirst();
		}
		catch (SQLException sqle) {
			throw new JDBCException(sqle);
		}
	}

	/**
	 * @see org.hibernate.ScrollableResults#isLast()
	 */
	public boolean isLast() throws HibernateException {
		try {
			return rs.isLast();
		}
		catch (SQLException sqle) {
			throw new JDBCException(sqle);
		}
	}

	private Object throwInvalidColumnTypeException(int i, Type type, Type returnType) throws HibernateException {
		throw new HibernateException( "incompatible column types: " + type.getName() + ", " + returnType.getName() );
	}

	public int getRowNumber() throws HibernateException {
		try {
			return rs.getRow()-1;
		}
		catch (SQLException sqle) {
			throw new JDBCException(sqle);
		}
	}

	public boolean setRowNumber(int rowNumber) throws HibernateException {
		if (rowNumber>=0) rowNumber++;
		try {
			boolean result = rs.absolute(rowNumber);
			prepareCurrentRow(result);
			return result;
		}
		catch (SQLException sqle) {
			throw new JDBCException(sqle);
		}
	}

	private void prepareCurrentRow(boolean underlyingScrollSuccessful) throws HibernateException {
		if (!underlyingScrollSuccessful) {
			currentRow = null;
			return;
		}

		try {
			Object result = loader.loadSingleRow(
					rs,
					sess,
					queryParameters,
					false
			);
			if ( result != null && result.getClass().isArray() ) {
				currentRow = (Object[]) result;
			}
			else {
				currentRow = new Object[] {result};
			}
		}
		catch(SQLException e) {
			throw new JDBCException(e);
		}

		if (nestedHolderClass!= null) {
			try {
				currentRow = new Object[] {
					QueryTranslatorImpl.newHolderClassInstance(nestedHolderClass, currentRow)
				};
			}
			catch(Throwable t) {
				throw new QueryException("Could not instantiate: " + nestedHolderClass.getClazz(), t);
			}
		}
	}

}






