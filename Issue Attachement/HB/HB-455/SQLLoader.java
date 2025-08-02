//$Id: SQLLoader.java,v 1.2 2003/11/05 06:31:11 amk Exp $
package net.sf.hibernate.loader;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.LockMode;
import net.sf.hibernate.QueryException;
import net.sf.hibernate.engine.QueryParameters;
import net.sf.hibernate.engine.SessionFactoryImplementor;
import net.sf.hibernate.engine.SessionImplementor;
import net.sf.hibernate.engine.TypedValue;
import net.sf.hibernate.hql.ParserHelper;
import net.sf.hibernate.persister.Loadable;
import net.sf.hibernate.util.ArrayHelper;

/**
 * @author Max Andersen
 *
 */
public class SQLLoader extends OuterJoinLoader {

	private int parameterCount = 0;
	private final Map namedParameters = new HashMap();
	private final String sqlQuery;
	private final Map alias2Persister;
	private final String[] aliases;

	public SQLLoader(String[] aliases, Loadable persisters[], SessionFactoryImplementor factory, String sqlQuery) throws HibernateException {
		super( factory.getDialect() );
		this.sqlQuery = sqlQuery;
		this.aliases = aliases;

		alias2Persister = new HashMap(persisters.length);
		for (int i = 0; i < persisters.length; i++) {
			alias2Persister.put( aliases[i], persisters[i] );

		}

		renderStatement(persisters);

		postInstantiate();
	}

	private void renderStatement(Loadable persisters[]) throws QueryException {
		int loadables = persisters.length;

		// is called persisters in QueryTranslator, but classPersisters in OuterJoinLoader..go figure ;)
		classPersisters = persisters;
		suffixes = generateSuffixes(loadables);

		lockModeArray = createLockModeArray(loadables, LockMode.NONE);

		sql = substituteBrackets();
		sql = substituteParams();

	}

	public List list(QueryParameters qp, SessionImplementor session)
	throws HibernateException, SQLException {
		return find(session, qp);
	}

	protected Object getResultColumnOrRow(Object[] row, ResultSet rs, SessionImplementor session) throws SQLException, HibernateException {

		if (classPersisters.length == 1) {
			return row[row.length - 1];
		}
		else {
			return row;
		}
	};

	// Inspired by the parsing done in TJDO
	// TODO: should "record" how many properties we have reffered to - and if we don't get'em'all we throw an exception! Way better than trial and error ;)
	public String substituteBrackets() throws QueryException {

		String sqlString = sqlQuery;

		StringBuffer result = new StringBuffer();
		int left, right;

		// replace {....} with corresponding column aliases
		for (int curr = 0; curr < sqlString.length(); curr = right + 1) {
			if ( (left = sqlString.indexOf('{', curr) ) < 0) {
				result.append( sqlString.substring(curr) );
				break;
			}

			result.append( sqlString.substring(curr, left ) );

			if ( (right = sqlString.indexOf('}', left + 1) ) < 0)
				throw new QueryException("Unmatched braces for alias path", sqlString);

			String aliasPath = sqlString.substring(left + 1, right);
			int firstDot = aliasPath.lastIndexOf('.');

			String aliasName = firstDot == -1 ? aliasPath : aliasPath.substring(0, firstDot);
			Loadable currentPersister = getPersisterByResultAlias(aliasName);
			if (currentPersister == null) throw new QueryException(
				"Alias [" + aliasName + "] does not correspond to any of the supplied return aliases = {" +
				ArrayHelper.asString(aliases) + "}", sqlQuery
			);

			int currentPersisterIndex = getPersisterIndex(currentPersister);

			if (firstDot == -1) {
				// TODO: should this one also be aliased/qouted instead of just directly inserted ?
				result.append(aliasPath);
			}
			else {

				if ( !aliasName.equals( aliases[currentPersisterIndex] ) ) throw new QueryException(
					"Alias [" + aliasName + "] does not correspond to return alias " +
					aliases[currentPersisterIndex], sqlQuery
				);

				String propertyName = aliasPath.substring(firstDot + 1);

				if ( "*".equals(propertyName) ) {
					result.append( currentPersister.identifierSelectFragment(aliasName, suffixes[currentPersisterIndex]) );
					result.append( currentPersister.propertySelectFragment(aliasName, suffixes[currentPersisterIndex]) );
				}
				else {

					// here it would be nice just to be able to do;
					//result.append( getAliasFor(currentPersister, propertyName) );
					// but that requires more exposure of the internal maps of the persister...
					// but it should be possible as propertyname should be unique for all persisters

					String[] columnAliases;

					/*if ( AbstractEntityPersister.ENTITY_CLASS.equals(propertyName) ) {
						columnAliases = new String[1];
						columnAliases[0] = currentPersister.getDiscriminatorAlias(suffixes[currentPersisterIndex]);
					}
					else {*/
						columnAliases = currentPersister.getSubclassPropertyColumnAliases(propertyName, suffixes[currentPersisterIndex]);
					//}

					if (columnAliases == null || columnAliases.length == 0) {
						throw new QueryException("No column name found for property [" + propertyName + "]", sqlQuery);
					}
					if (columnAliases.length != 1) {
						throw new QueryException("SQL queries only support properties mapped to a single column. Property [" + propertyName + "] is mapped to " + columnAliases.length + " columns.", sqlQuery);
					}
					// here we need to find the field of the aliasName
					// Find by alias first
					// Find by class second ?
					//result.append("$" + aliasName + "/" + propertyName + "$");
					result.append(columnAliases[0]);
				}
			}
		}

		// Possibly handle :something parameters for the query ?

		return result.toString();
	}


	private String substituteParams() {

		String sqlString = sql;
		StringBuffer result = new StringBuffer( sql.length() ); // can be shorter, but definitly not longer than sql.length
		int left, right;

		//	replace :... with ? and record the parameter. Naively just replaces ALL occurences of :... - including whatever is BEFORE FROM..
		// do not "fast-forward to the first or last FROM as "weird" sql might have parameters in places we do not know of, right ? ;)
		for (int curr = 0; curr < sqlString.length(); curr = right) {
			if ((left = sqlString.indexOf(ParserHelper.HQL_VARIABLE_PREFIX, curr)) < 0) {
				result.append(sqlString.substring(curr));
				break;
			}

			result.append(sqlString.substring(curr, left));

			for (right = left + 1; right < sqlString.length(); right++) {
				if (ParserHelper.HQL_SEPARATORS.indexOf(sqlString.charAt(right)) >= 0) {
					break;
				}
			}

			String param = sqlString.substring(left + 1, right);
			addNamedParameter(param);
			result.append("?");

		}
		return result.toString();
	}

	private int getPersisterIndex(Loadable currentPersister) {
		for (int i = 0; i < classPersisters.length; i++) {
			if (currentPersister == classPersisters[i])
				return i;
		}
		return -1;
	}

	private Loadable getPersisterByResultAlias(String aliasName) {
		return (Loadable) alias2Persister.get(aliasName);
	}

	// NAMED PARAMETERS SUPPORT, copy/pasted from QueryTranslator!
	void addNamedParameter(String name) {
		Integer loc = new Integer(parameterCount++);
		Object o = namedParameters.get(name);
		if (o == null) {
			namedParameters.put(name, loc);
		}
		else if (o instanceof Integer) {
			ArrayList list = new ArrayList(4);
			list.add(o);
			list.add(loc);
			namedParameters.put(name, list);
		}
		else {
			( (List) o ).add(loc);
		}
	}

	protected int[] getNamedParameterLocs(String name) throws QueryException {
		Object o = namedParameters.get(name);
		if (o == null) {
			QueryException qe = new QueryException("Named parameter does not appear in Query: " + name, sqlQuery);
			throw qe;
		}
		if (o instanceof Integer) {
			return new int[] { ( (Integer) o ).intValue() };
		}
		else {
			return ArrayHelper.toIntArray( (List) o );
		}
	}

	protected int bindNamedParameters(PreparedStatement ps, Map namedParams, int start, SessionImplementor session) throws SQLException, HibernateException {
		if (namedParams != null) {
			// assumes that types are all of span 1
			Iterator iter = namedParams.entrySet().iterator();
			int result = 0;
			while ( iter.hasNext() ) {
				Map.Entry e = (Map.Entry) iter.next();
				String name = (String) e.getKey();
				TypedValue typedval = (TypedValue) e.getValue();
				int[] locs = getNamedParameterLocs(name);
				for (int i = 0; i < locs.length; i++) {
					typedval.getType().nullSafeSet(ps, typedval.getValue(), locs[i] + start, session);
				}
				result += locs.length;
			}
			return result;
		}
		else {
			return 0;
		}
	}
}
