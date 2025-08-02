//$Id: IteratorImpl.java,v 1.2 2004/06/28 23:57:51 epbernard Exp $
package org.hibernate.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.lang.reflect.Constructor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.hibernate.HibernateException;
import org.hibernate.LazyInitializationException;
import org.hibernate.QueryException;
import org.hibernate.util.NestedHolderClass;
import org.hibernate.util.ReflectHelper;
import org.hibernate.engine.HibernateIterator;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.hql.QueryTranslatorImpl;
import org.hibernate.type.Type;

/**
 * An implementation of <tt>java.util.Iterator</tt> that is
 * returned by <tt>iterate()</tt> query execution methods.
 * @author Gavin King
 */
public final class IteratorImpl implements HibernateIterator {

	private static final Log log = LogFactory.getLog(IteratorImpl.class);

	private ResultSet rs;
	private final SessionImplementor sess;
	private final Type[] types;
	private final boolean single;
	private Object currentResult;
	private boolean hasNext;
	private final String[][] names;
	private PreparedStatement ps;
	private Object nextResult;
	private NestedHolderClass nestedHolderClass;
	private Map nestedHolderConstructors;

	public IteratorImpl(
	        ResultSet rs,
	        PreparedStatement ps,
	        SessionImplementor sess,
	        Type[] types,
	        String[][] columnNames,
			NestedHolderClass nestedHolderClass)
	throws HibernateException, SQLException {

		this.rs=rs;
		this.ps=ps;
		this.sess = sess;
		this.types = types;
		this.names = columnNames;
		this.nestedHolderClass = nestedHolderClass;

		if (nestedHolderClass != null) {
			nestedHolderConstructors = new HashMap();
		}

		single = types.length==1;

		postNext();
	}

	public void close() throws SQLException {
		if (ps!=null) {
			log.debug("closing iterator");
			nextResult = null;
			sess.getBatcher().closeQueryStatement(ps, rs);
			ps = null;
			rs = null;
			hasNext = false;
		}
	}

	private void postNext() throws HibernateException, SQLException {
		this.hasNext = rs.next();
		if (!hasNext) {
			log.debug("exhausted results");
			close();
		}
		else {
			log.debug("retrieving next results");
			if (single) {
				nextResult = types[0].nullSafeGet( rs, names[0], sess, null );
			}
			else {
				Object[] nextResults = new Object[types.length];
				for (int i=0; i<types.length; i++) {
					nextResults[i] = types[i].nullSafeGet( rs, names[i], sess, null );
				}
				nextResult = nextResults;
			}

			if (nestedHolderClass != null) {
				try {
					if (nextResult == null || !nextResult.getClass().isArray()) {
						nextResult = QueryTranslatorImpl.newHolderClassInstance( nestedHolderClass, new Object[] {nextResult});
					}
					else {
						nextResult = QueryTranslatorImpl.newHolderClassInstance( nestedHolderClass, (Object[]) nextResult );
					}
				}
				catch(Exception e) {
					throw new QueryException("Could not instantiate: " + nestedHolderClass.getClazz(), e);
				}
			}
		}
	}

	public boolean hasNext() {
		return hasNext;
	}

	public Object next() {
		if ( !hasNext ) throw new NoSuchElementException("No more results");
		try {
			currentResult = nextResult;
			postNext();
			log.debug("returning current results");
			return currentResult;
		}
		catch (Exception sqle) {
			log.error("could not get next result", sqle);
			throw new LazyInitializationException(sqle);
		}
	}

	public void remove() {
		if (!single) throw new UnsupportedOperationException("Not a single column hibernate query result set");
		if (currentResult==null) throw new IllegalStateException("Called Iterator.remove() before next()");
		try {
			sess.delete(currentResult);
		}
		catch (Exception sqle) {
			log.error("could not remove", sqle);
			throw new LazyInitializationException(sqle);
		}
	}

}







