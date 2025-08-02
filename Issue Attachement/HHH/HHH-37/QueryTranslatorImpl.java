//$Id: QueryTranslatorImpl.java,v 1.20 2004/08/20 07:41:23 oneovthafew Exp $
package org.hibernate.hql;

import org.apache.commons.collections.SequencedHashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.MappingException;
import org.hibernate.PropertyNotFoundException;
import org.hibernate.QueryException;
import org.hibernate.ScrollableResults;
import org.hibernate.collection.CollectionPersister;
import org.hibernate.collection.QueryableCollection;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.JoinSequence;
import org.hibernate.engine.QueryParameters;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.engine.TypedValue;
import org.hibernate.impl.IteratorImpl;
import org.hibernate.loader.Loader;
import org.hibernate.persister.Loadable;
import org.hibernate.persister.PropertyMapping;
import org.hibernate.persister.Queryable;
import org.hibernate.sql.ForUpdateFragment;
import org.hibernate.sql.JoinFragment;
import org.hibernate.sql.QuerySelect;
import org.hibernate.type.AssociationType;
import org.hibernate.type.EntityType;
import org.hibernate.type.Type;
import org.hibernate.type.TypeFactory;
import org.hibernate.util.ArrayHelper;
import org.hibernate.util.NestedHolderClass;
import org.hibernate.util.ReflectHelper;
import org.hibernate.util.StringHelper;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Stack;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * An instance of <tt>QueryTranslator</tt> translates a Hibernate
 * query string to SQL.
 */
public class QueryTranslatorImpl extends Loader implements QueryTranslator, FilterTranslator {

	private final String queryString;

	private final Map typeMap = new SequencedHashMap();
	private final Map collections = new SequencedHashMap();
	private List returnedTypes = new ArrayList();
	private final List fromTypes = new ArrayList();
	private final List scalarTypes = new ArrayList();
	private final Map namedParameters = new HashMap();
	private final Map aliasNames = new HashMap();
	private final Map oneToOneOwnerNames = new HashMap();
	private final Map uniqueKeyOwnerReferences = new HashMap();
	private final Map decoratedPropertyMappings = new HashMap();

	private final List scalarSelectTokens = new ArrayList();
	private final List whereTokens = new ArrayList();
	private final List havingTokens = new ArrayList();
	private final Map joins = new SequencedHashMap();
	private final List orderByTokens = new ArrayList();
	private final List groupByTokens = new ArrayList();
	private final Set querySpaces = new HashSet();
	private final Set entitiesToFetch = new HashSet();

	private final Map pathAliases = new HashMap();
	private final Map pathJoins = new HashMap();

	private Queryable[] persisters;
	private int[] owners;
	private String[] uniqueKeyReferences;
	private String[] names;
	private boolean[] includeInSelect;
	private int selectLength;
	private Type[] returnTypes;
	private Type[] actualReturnTypes;
	private String[][] scalarColumnNames;
	private SessionFactoryImplementor factory;
	private Map tokenReplacements;
	private int nameCount = 0;
	private int parameterCount = 0;
	private boolean distinct = false;
	private boolean compiled;
	private String sqlString;
	private Stack holderClassesToProcess;
	private NestedHolderClass holderClass;
	private boolean hasScalars;
	private boolean shallowQuery;
	private QueryTranslatorImpl superQuery;

	private QueryableCollection collectionPersister;
	private int collectionOwnerColumn = -1;
	private String collectionOwnerName;
	private String fetchName;

	private String[] suffixes;

	private Map enabledFilters;

	private static final Log log = LogFactory.getLog( QueryTranslatorImpl.class );

	/**
	 * Construct a query translator
	 */
	public QueryTranslatorImpl(String queryString, Map enabledFilters) {
		this.queryString = queryString;
		this.enabledFilters = enabledFilters;
	}

	/**
	 * Compile a subquery
	 */
	void compile(QueryTranslatorImpl superquery) 
	throws QueryException, MappingException {
		this.factory = superquery.factory;
		this.tokenReplacements = superquery.tokenReplacements;
		this.superQuery = superquery;
		this.shallowQuery = true;
		this.enabledFilters = superquery.getEnabledFilters();
		compile();
	}


	/**
	 * Compile a "normal" query. This method may be called multiple
	 * times. Subsequent invocations are no-ops.
	 */
	public synchronized void compile(
	        SessionFactoryImplementor sessionFactory, 
	        Map replacements, 
	        boolean scalar) 
	throws QueryException, MappingException {
		if ( !compiled ) {
			this.factory = sessionFactory;
			this.tokenReplacements = replacements;
			this.shallowQuery = scalar;
			compile();
		}
	}

	/**
	 * Compile a filter. This method may be called multiple
	 * times. Subsequent invocations are no-ops.
	 */
	public synchronized void compile(
	        String collectionRole, 
	        SessionFactoryImplementor sessionFactory, 
	        Map replacements, 
	        boolean scalar) 
	throws QueryException, MappingException {

		if ( !isCompiled() ) {
			setFactory( sessionFactory ); // yick!
			addFromAssociation( "this", collectionRole );
			compile(sessionFactory, replacements, scalar);
		}
	}

	/**
	 * Compile the query (generate the SQL).
	 */
	private void compile() throws QueryException, MappingException {

		log.trace( "compiling query" );
		try {
			ParserHelper.parse( new PreprocessingParser( tokenReplacements ),
					queryString,
					ParserHelper.HQL_SEPARATORS,
					this );
			renderSQL();
		}
		catch (QueryException qe) {
			qe.setQueryString(queryString);
			throw qe;
		}
		catch (MappingException me) {
			throw me;
		}
		catch (Exception e) {
			log.debug( "unexpected query compilation problem", e );
			e.printStackTrace();
			QueryException qe = new QueryException( "Incorrect query syntax", e );
			qe.setQueryString( queryString );
			throw qe;
		}

		postInstantiate();

		compiled = true;

	}

	public String getSQLString() {
		return sqlString;
	}

	/**
	 * Persisters for the return values of a <tt>find()</tt> style query.
	 *
	 * @return an array of <tt>EntityPersister</tt>s.
	 */
	protected Loadable[] getPersisters() {
		return persisters;
	}

	/**
	 * Types of the return values of an <tt>iterate()</tt> style query.
	 *
	 * @return an array of <tt>Type</tt>s.
	 */
	public Type[] getReturnTypes() {
		return actualReturnTypes;
	}

	private String[][] getScalarColumnNames() {
		return scalarColumnNames;
	}

	private static void logQuery(String hql, String sql) {
		if ( log.isDebugEnabled() ) {
			log.debug( "HQL: " + hql );
			log.debug( "SQL: " + sql );
		}
	}

	void setAliasName(String alias, String name) {
		aliasNames.put( alias, name );
	}

	private String getAliasName(String alias) {
		String name = (String) aliasNames.get(alias);
		if ( name == null ) {
			if ( superQuery != null ) {
				name = superQuery.getAliasName(alias);
			}
			else {
				name = alias;
			}
		}
		return name;
	}

	String unalias(String path) {
		String alias = StringHelper.root(path);
		String name = getAliasName(alias);
		if ( name != null ) {
			return name + path.substring( alias.length() );
		}
		else {
			return path;
		}
	}

	void addEntityToFetch(String name, String oneToOneOwnerName, String uniqueKeyReference) {
		addEntityToFetch(name);
		if ( oneToOneOwnerName != null ) oneToOneOwnerNames.put( name, oneToOneOwnerName );
		if ( uniqueKeyReference != null ) uniqueKeyOwnerReferences.put( name, uniqueKeyReference );
	}

	private void addEntityToFetch(String name) {
		entitiesToFetch.add(name);
	}

	private int nextCount() {
		return ( superQuery == null ) ? nameCount++ : superQuery.nameCount++;
	}

	String createNameFor(String type) {
		return StringHelper.generateAlias( type, nextCount() );
	}

	String createNameForCollection(String role) {
		return StringHelper.generateAlias( role, nextCount() );
	}

	private String getType(String name) {
		String type = (String) typeMap.get(name);
		if ( type == null && superQuery != null ) {
			type = superQuery.getType(name);
		}
		return type;
	}

	private String getRole(String name) {
		String role = (String) collections.get(name);
		if ( role == null && superQuery != null ) {
			role = superQuery.getRole(name);
		}
		return role;
	}

	boolean isName(String name) {
		return aliasNames.containsKey(name) ||
				typeMap.containsKey(name) ||
				collections.containsKey(name) || (
				superQuery != null && superQuery.isName(name)
		);
	}

	PropertyMapping getPropertyMapping(String name) throws QueryException {
		PropertyMapping decorator = getDecoratedPropertyMapping(name);
		if ( decorator != null ) return decorator;

		String type = getType(name);
		if ( type == null ) {
			String role = getRole(name);
			if ( role == null ) {
				throw new QueryException( "alias not found: " + name );
			}
			return getCollectionPersister(role); //.getElementPropertyMapping();
		}
		else {
			Queryable persister = getEntityPersister(type);
			if ( persister == null ) throw new QueryException( "persistent class not found: " + type );
			return persister;
		}
	}

	private PropertyMapping getDecoratedPropertyMapping(String name) {
		return (PropertyMapping) decoratedPropertyMappings.get(name);
	}

	void decoratePropertyMapping(String name, PropertyMapping mapping) {
		decoratedPropertyMappings.put(name, mapping);
	}

	private Queryable getEntityPersisterForName(String name) throws QueryException {
		String type = getType(name);
		Queryable persister = getEntityPersister(type);
		if ( persister == null ) throw new QueryException( "persistent class not found: " + type );
		return persister;
	}

	Queryable getEntityPersisterUsingImports(String className) {
		final String importedClassName = factory.getImportedClassName(className);
		if (importedClassName==null) return null;
		try {
			return ( Queryable ) factory.getEntityPersister(importedClassName);
		}
		catch (MappingException me) {
			return null;
		}
	}

	Queryable getEntityPersister(String entityName) throws QueryException {
		try {
			return (Queryable) factory.getEntityPersister(entityName);
		}
		catch (Exception e) {
			throw new QueryException( "persistent class not found: " + entityName );
		}
	}

	QueryableCollection getCollectionPersister(String role) throws QueryException {
		try {
			return (QueryableCollection) factory.getCollectionPersister(role);
		}
		catch (ClassCastException cce) {
			throw new QueryException( "collection role is not queryable: " + role );
		}
		catch (Exception e) {
			throw new QueryException( "collection role not found: " + role );
		}
	}

	void addType(String name, String type) {
		typeMap.put(name, type);
	}

	void addCollection(String name, String role) {
		collections.put(name, role);
	}

	void addFrom(String name, String type, JoinSequence joinSequence)
	throws QueryException {
		addType(name, type);
		addFrom(name, joinSequence);
	}

	void addFromCollection(String name, String collectionRole, JoinSequence joinSequence)
	throws QueryException {
		//register collection role
		addCollection(name, collectionRole);
		addJoin(name, joinSequence);
	}

	void addFrom(String name, JoinSequence joinSequence)
			throws QueryException {
		fromTypes.add(name);
		addJoin(name, joinSequence);
	}

	void addFromClass(String name, Queryable classPersister)
			throws QueryException {
		JoinSequence joinSequence = new JoinSequence(factory)
				.setRoot(classPersister, name);
		//crossJoins.add(name);
		addFrom( name, classPersister.getEntityName(), joinSequence );
	}

	void addSelectClass(String name) {
		returnedTypes.add(name);
	}

	void addSelectScalar(Type type) {
		scalarTypes.add(type);
	}

	void appendWhereToken(String token) {
		whereTokens.add(token);
	}

	void appendHavingToken(String token) {
		havingTokens.add(token);
	}

	void appendOrderByToken(String token) {
		orderByTokens.add(token);
	}

	void appendGroupByToken(String token) {
		groupByTokens.add(token);
	}

	void appendScalarSelectToken(String token) {
		scalarSelectTokens.add(token);
	}

	void appendScalarSelectTokens(String[] tokens) {
		scalarSelectTokens.add(tokens);
	}

	void addFromJoinOnly(String name, JoinSequence joinSequence) throws QueryException {
		addJoin( name, joinSequence.getFromPart() );
	}

	void addJoin(String name, JoinSequence joinSequence) throws QueryException {
		if ( !joins.containsKey(name) ) joins.put(name, joinSequence);
	}

	void addNamedParameter(String name) {
		if ( superQuery != null ) superQuery.addNamedParameter(name);
		Integer loc = new Integer( parameterCount++ );
		Object o = namedParameters.get(name);
		if ( o == null ) {
			namedParameters.put(name, loc);
		}
		else if ( o instanceof Integer ) {
			ArrayList list = new ArrayList( 4 );
			list.add(o);
			list.add(loc);
			namedParameters.put(name, list);
		}
		else {
			( (ArrayList) o ).add( loc );
		}
	}

	private int[] getNamedParameterLocs(String name) throws QueryException {
		Object o = namedParameters.get(name);
		if ( o == null ) {
			QueryException qe = new QueryException( "Named parameter does not appear in Query: " + name );
			qe.setQueryString(queryString);
			throw qe;
		}
		if ( o instanceof Integer ) {
			return new int[]{ ( (Integer) o ).intValue() };
		}
		else {
			return ArrayHelper.toIntArray( (ArrayList) o );
		}
	}

	private static String scalarName(int x, int y) {
		return NameGenerator.scalarName( x, y );
	}

	private void renderSQL() throws QueryException, MappingException {

		final int rtsize;
		if ( returnedTypes.size() == 0 && scalarTypes.size() == 0 ) {
			//ie no select clause in HQL
			returnedTypes = fromTypes;
			rtsize = returnedTypes.size();
		}
		else {
			rtsize = returnedTypes.size();
			Iterator iter = entitiesToFetch.iterator();
			while ( iter.hasNext() ) {
				returnedTypes.add( iter.next() );
			}
		}
		int size = returnedTypes.size();
		persisters = new Queryable[size];
		names = new String[size];
		owners = new int[size];
		uniqueKeyReferences = new String[size];
		suffixes = new String[size];
		includeInSelect = new boolean[size];
		for ( int i = 0; i < size; i++ ) {
			String name = (String) returnedTypes.get( i );
			//if ( !isName(name) ) throw new QueryException("unknown type: " + name);
			persisters[i] = getEntityPersisterForName(name);
			// TODO: cannot use generateSuffixes() - it handles the initial suffix differently.
			suffixes[i] = ( size == 1 ) ? "" : Integer.toString(i) + '_';
			names[i] = name;
			includeInSelect[i] = !entitiesToFetch.contains( name );
			if ( includeInSelect[i] ) selectLength++;
			if ( name.equals( collectionOwnerName ) ) collectionOwnerColumn = i;
			String oneToOneOwner = (String) oneToOneOwnerNames.get(name);
			owners[i] = ( oneToOneOwner == null ) ? -1 : returnedTypes.indexOf(oneToOneOwner);
			uniqueKeyReferences[i] = (String) uniqueKeyOwnerReferences.get(name);
		}

		if ( ArrayHelper.isAllNegative(owners) ) owners = null;

		String scalarSelect = renderScalarSelect(); //Must be done here because of side-effect! yuck...

		int scalarSize = scalarTypes.size();
		hasScalars = scalarTypes.size() != rtsize;

		returnTypes = new Type[scalarSize];
		for ( int i = 0; i < scalarSize; i++ ) {
			returnTypes[i] = (Type) scalarTypes.get(i);
		}

		QuerySelect sql = new QuerySelect( factory.getDialect() );
		sql.setDistinct(distinct);

		if ( !shallowQuery ) {
			renderIdentifierSelect(sql);
			renderPropertiesSelect(sql);
		}

		if ( collectionPersister != null ) {
			sql.addSelectFragmentString( collectionPersister.selectFragment(fetchName) );
		}

		if ( hasScalars || shallowQuery ) sql.addSelectFragmentString(scalarSelect);

		//TODO: for some dialiects it would be appropriate to add the renderOrderByPropertiesSelect() to other select strings
		mergeJoins( sql.getJoinFragment() );

		sql.setWhereTokens( whereTokens.iterator() );

		sql.setGroupByTokens( groupByTokens.iterator() );
		sql.setHavingTokens( havingTokens.iterator() );
		sql.setOrderByTokens( orderByTokens.iterator() );

		if ( collectionPersister != null && collectionPersister.hasOrdering() ) {
			sql.addOrderBy( collectionPersister.getSQLOrderByString(fetchName) );
		}

		scalarColumnNames = generateColumnNames( returnTypes, factory );

		// initialize the Set of queried identifier spaces (ie. tables)
		Iterator iter = collections.values().iterator();
		while ( iter.hasNext() ) {
			CollectionPersister p = getCollectionPersister( (String) iter.next() );
			addQuerySpaces( p.getCollectionSpaces() );
		}
		iter = typeMap.keySet().iterator();
		while ( iter.hasNext() ) {
			Queryable p = getEntityPersisterForName( (String) iter.next() );
			addQuerySpaces( p.getQuerySpaces() );
		}

		sqlString = sql.toQueryString();

		if (holderClass!=null) processHolderConstructor(holderClass, returnTypes);

		if (hasScalars) {
			actualReturnTypes = returnTypes;
		}
		else {
			actualReturnTypes = new Type[selectLength];
			int j = 0;
			for ( int i = 0; i < persisters.length; i++ ) {
				if ( includeInSelect[i] ) actualReturnTypes[j++] = TypeFactory.manyToOne( persisters[i].getEntityName() );
			}
		}

	}

	private void processHolderConstructor(NestedHolderClass nestedHolderClass, Type[] returnTypes) 
		throws PropertyNotFoundException {
	    
	    List fields = nestedHolderClass.getParams();
	    Type[] types = new Type[fields.size()];
	    for (int i = 0; i < fields.size(); i++) {
	        if (fields.get(i) instanceof Integer)
	            types[i] = returnTypes[((Integer)fields.get(i)).intValue()];
	        else {
	            types[i] = Hibernate.entity(((NestedHolderClass)fields.get(i)).getClazz());
	            processHolderConstructor((NestedHolderClass)fields.get(i), returnTypes);
	        }
	    }
	    nestedHolderClass.setHolderConstructor(ReflectHelper.getConstructor(
	            nestedHolderClass.getClazz(), types));
	}
	
	private void renderIdentifierSelect(QuerySelect sql) {
		int size = returnedTypes.size();

		for ( int k = 0; k < size; k++ ) {
			String name = (String) returnedTypes.get(k);
			String suffix = size == 1 ? "" : Integer.toString(k) + '_';
			sql.addSelectFragmentString( persisters[k].identifierSelectFragment(name, suffix) );
		}

	}

	/*private String renderOrderByPropertiesSelect() {
		StringBuffer buf = new StringBuffer(10);

		//add the columns we are ordering by to the select ID select clause
		Iterator iter = orderByTokens.iterator();
		while ( iter.hasNext() ) {
			String token = (String) iter.next();
			if ( token.lastIndexOf(".") > 0 ) {
				//ie. it is of form "foo.bar", not of form "asc" or "desc"
				buf.append(StringHelper.COMMA_SPACE).append(token);
			}
		}

		return buf.toString();
	}*/

	private void renderPropertiesSelect(QuerySelect sql) {
		int size = returnedTypes.size();
		for ( int k = 0; k < size; k++ ) {
			String suffix = ( size == 1 ) ? "" : Integer.toString(k) + '_';
			String name = (String) returnedTypes.get( k );
			sql.addSelectFragmentString( persisters[k].propertySelectFragment(name, suffix) );
		}
	}

	/**
	 * WARNING: side-effecty
	 */
	private String renderScalarSelect() {

		boolean isSubselect = superQuery != null;

		StringBuffer buf = new StringBuffer( 20 );

		if ( scalarTypes.size() == 0 ) {
			//ie. no select clause
			int size = returnedTypes.size();
			for ( int k = 0; k < size; k++ ) {

				scalarTypes.add( TypeFactory.manyToOne( persisters[k].getEntityName() ) );

				String[] idColumnNames = persisters[k].getIdentifierColumnNames();
				for ( int i = 0; i < idColumnNames.length; i++ ) {
					buf.append( returnedTypes.get( k ) ).append('.').append( idColumnNames[i] );
					if ( !isSubselect ) buf.append( " as " ).append( scalarName(k, i) );
					if ( i != idColumnNames.length - 1 || k != size - 1 ) buf.append(", ");
				}

			}

		}
		else {
			//there _was_ a select clause
			Iterator iter = scalarSelectTokens.iterator();
			int c = 0;
			boolean nolast = false; //real hacky...
			int parenCount = 0; // used to count the nesting of parentheses
			while ( iter.hasNext() ) {
				Object next = iter.next();
				if ( next instanceof String ) {
					String token = (String) next;

					if ( "(".equals(token) ) {
						parenCount++;
					}
					else if ( ")".equals(token) ) {
						parenCount--;
					}

					String lc = token.toLowerCase();
					if ( lc.equals(", ") ) {
						if (nolast) {
							nolast = false;
						}
						else {
							if ( !isSubselect && parenCount == 0 ) {
								buf.append(" as ")
									.append( scalarName(c++, 0) );
							}
						}
					}
					buf.append( token );
					if ( lc.equals("distinct") || lc.equals("all") ) {
						buf.append(' ');
					}
				}
				else {
					nolast = true;
					String[] tokens = (String[]) next;
					for ( int i = 0; i < tokens.length; i++ ) {
						buf.append( tokens[i] );
						if ( !isSubselect ) {
							buf.append( " as " )
								.append( scalarName(c, i) );
						}
						if ( i != tokens.length - 1 ) buf.append(", ");
					}
					c++;
				}
			}
			if ( !isSubselect && !nolast ) {
				buf.append(" as ")
					.append( scalarName(c++, 0) );
			}

		}

		return buf.toString();
	}

	private void mergeJoins(JoinFragment ojf) throws MappingException, QueryException {

		Iterator iter = joins.entrySet().iterator();
		while ( iter.hasNext() ) {
			Map.Entry me = (Map.Entry) iter.next();
			String name = (String) me.getKey();
			JoinSequence join = (JoinSequence) me.getValue();
			join.setSelector( new JoinSequence.Selector() {
				public boolean includeSubclasses(String alias) {
					return returnedTypes.contains( alias) && !isShallowQuery();
				}
			} );

			if ( typeMap.containsKey(name) ) {
				ojf.addFragment( join.toJoinFragment(enabledFilters) );
			}
			else if ( collections.containsKey(name) ) {
				ojf.addFragment( join.toJoinFragment(enabledFilters) );
			}
			else {
				//name from a super query (a bit inelegant that it shows up here)
			}

		}

	}

	public final Set getQuerySpaces() {
		return querySpaces;
	}

	/**
	 * Is this query called by scroll() or iterate()?
	 *
	 * @return true if it is, false if it is called by find() or list()
	 */
	boolean isShallowQuery() {
		return shallowQuery;
	}

	void addQuerySpaces(Serializable[] spaces) {
		for ( int i=0; i<spaces.length; i++ ) {
			querySpaces.add( spaces[i] );
		}
		if ( superQuery != null ) superQuery.addQuerySpaces(spaces);
	}

	void setDistinct(boolean distinct) {
		this.distinct = distinct;
	}

	boolean isSubquery() {
		return superQuery != null;
	}

	/**
	 * Overrides method from Loader
	 */
	protected CollectionPersister getCollectionPersister() {
		return collectionPersister;
	}

	void setCollectionToFetch(String role, String name, String ownerName, String entityName) 
	throws QueryException {
		fetchName = name;
		collectionPersister = getCollectionPersister(role);
		collectionOwnerName = ownerName;
		if ( collectionPersister.getElementType().isEntityType() ) {
			addEntityToFetch(entityName);
		}
	}

	protected String[] getSuffixes() {
		return suffixes;
	}

	/**
	 * Used for collection filters
	 */
	private void addFromAssociation(final String elementName, final String collectionRole) 
	throws QueryException {
		//q.addCollection(collectionName, collectionRole);
		QueryableCollection persister = getCollectionPersister(collectionRole);
		Type collectionElementType = persister.getElementType();
		if ( !collectionElementType.isEntityType() ) {
			throw new QueryException( "collection of values in filter: " + elementName );
		}

		String[] keyColumnNames = persister.getKeyColumnNames();
		//if (keyColumnNames.length!=1) throw new QueryException("composite-key collection in filter: " + collectionRole);

		String collectionName;
		JoinSequence join = new JoinSequence(factory);
		collectionName = persister.isOneToMany() ?
				elementName :
				createNameForCollection(collectionRole);
		join.setRoot(persister, collectionName);
		if ( !persister.isOneToMany() ) {
			//many-to-many
			addCollection(collectionName, collectionRole);
			try {
				join.addJoin( (AssociationType) persister.getElementType(),
						elementName,
						JoinFragment.INNER_JOIN,
						StringHelper.qualify( collectionName, persister.getElementColumnNames() ) );
			}
			catch (MappingException me) {
				throw new QueryException(me);
			}
		}
		join.addCondition( collectionName, keyColumnNames, " = ?" );
		//if ( persister.hasWhere() ) join.addCondition( persister.getSQLWhereString(collectionName) );
		EntityType elemType = (EntityType) collectionElementType;
		addFrom( elementName, elemType.getAssociatedEntityName(), join );

	}

	String getPathAlias(String path) {
		return (String) pathAliases.get(path);
	}

	JoinSequence getPathJoin(String path) {
		return (JoinSequence) pathJoins.get(path);
	}

	void addPathAliasAndJoin(String path, String alias, JoinSequence joinSequence) {
		pathAliases.put(path, alias);
		pathJoins.put(path, joinSequence);
	}

	protected int bindNamedParameters(PreparedStatement ps, Map namedParams, int start, SessionImplementor session)
			throws SQLException, HibernateException {
		if ( namedParams != null ) {
			// assumes that types are all of span 1
			Iterator iter = namedParams.entrySet().iterator();
			int result = 0;
			while ( iter.hasNext() ) {
				Map.Entry e = (Map.Entry) iter.next();
				String name = (String) e.getKey();
				TypedValue typedval = (TypedValue) e.getValue();
				int[] locs = getNamedParameterLocs(name);
				for ( int i = 0; i < locs.length; i++ ) {
					typedval.getType().nullSafeSet( ps, typedval.getValue(), locs[i] + start, session );
				}
				result += locs.length;
			}
			return result;
		}
		else {
			return 0;
		}
	}

	public List list(SessionImplementor session, QueryParameters queryParameters)
	throws HibernateException, SQLException {
		return list( session, queryParameters, getQuerySpaces(), actualReturnTypes );
	}

	/**
	 * Return the query results as an iterator
	 */
	public Iterator iterate(QueryParameters queryParameters, SessionImplementor session)
	throws HibernateException, SQLException {
		
		boolean stats = session.getFactory().getStatistics().isStatisticsEnabled();
		long startTime = 0;
		if (stats) startTime = System.currentTimeMillis();
		
		PreparedStatement st = prepareQueryStatement( queryParameters, false, session );
		ResultSet rs = getResultSet( st, queryParameters.getRowSelection(), session );
		Iterator result = new IteratorImpl( rs, st, session, returnTypes, getScalarColumnNames(), holderClass );
		
		if (stats) {
			session.getFactory().getStatisticsImplementor().queryExecuted( 
				"HQL: " + queryString, 
				0, 
				System.currentTimeMillis() - startTime 
			);
		}
		
		return result;

	}

	String getImportedClass(String name) {
		return QuerySplitter.getImportedClass( name, factory );
	}

	private static String[][] generateColumnNames(Type[] types, SessionFactoryImplementor f) throws MappingException {
		String[][] columnNames = new String[types.length][];
		for ( int i = 0; i < types.length; i++ ) {
			int span = types[i].getColumnSpan(f);
			columnNames[i] = new String[span];
			for ( int j = 0; j < span; j++ ) {
				columnNames[i][j] = scalarName(i, j);
			}
		}
		return columnNames;
	}

	protected Object getResultColumnOrRow(Object[] row, ResultSet rs, SessionImplementor session)
	throws SQLException, HibernateException {
		row = toResultRow(row);
		if (hasScalars) {
			String[][] scalarColumns = getScalarColumnNames();
			int queryCols = returnTypes.length;
			if ( holderClass == null && queryCols == 1 ) {
				return returnTypes[0].nullSafeGet( rs, scalarColumns[0], session, null );
			}
			else {
				row = new Object[queryCols];
				for ( int i = 0; i < queryCols; i++ )
					row[i] = returnTypes[i].nullSafeGet( rs, scalarColumns[i], session, null );
				return row;
			}
		}
		else if ( holderClass == null ) {
			return row.length == 1 ? row[0] : row;
		}
		else {
			return row;
		}

	}

	protected List getResultList(List results) throws QueryException {
		if ( holderClass != null ) {
			for ( int i = 0; i < results.size(); i++ ) {
				Object[] row = (Object[]) results.get(i);
				try {
					results.set( i, newHolderClassInstance(holderClass, row));
				}
				catch (Exception e) {
					throw new QueryException( "could not instantiate: " + holderClass, e );
				}
			}
		}
		return results;
	}
	
	public static Object newHolderClassInstance(
	        NestedHolderClass nestedHolderClass, 
	        Object[] result) 
		throws IllegalArgumentException, InstantiationException, 
			IllegalAccessException, InvocationTargetException  {
	    
	    Object[] params = new Object[nestedHolderClass.getParams().size()];
	    Object param;
	    for (int i = 0; i < params.length; i++) {
	        param = nestedHolderClass.getParams().get(i);
	        if (param instanceof Integer) 
	            params[i] = result[((Integer)param).intValue()];
	        else 
	            params[i] = newHolderClassInstance((NestedHolderClass)param, result);
	    }
	    
	    return nestedHolderClass.getHolderConstructor().newInstance(params);
	}
	
	private Object[] toResultRow(Object[] row) {
		if ( selectLength == row.length ) {
			return row;
		}
		else {
			Object[] result = new Object[selectLength];
			int j = 0;
			for ( int i = 0; i < row.length; i++ ) {
				if ( includeInSelect[i] ) result[j++] = row[i];
			}
			return result;
		}
	}

//	void setHolderClass(Class clazz) {
//		holderClass = clazz;
//	}


	void addHolderClass(Class clazz) {
	    if (holderClassesToProcess == null) holderClassesToProcess = new Stack();
	    
	    NestedHolderClass classToProcess = new NestedHolderClass(clazz);
	    if (holderClass != null) {
		    holderClassesToProcess.push(holderClass);
		    holderClass.getParams().add(classToProcess);
	    }
	    holderClass = classToProcess;
	}
	
	void addScalarIndexInHolderClass(int index) {
	    holderClass.getParams().add(new Integer(index));
	}
	
	boolean finalizeActualHolderClass(){
	    if (holderClassesToProcess.size() > 0) {
	        holderClass = (NestedHolderClass) holderClassesToProcess.pop();
	        return true;
	    } else
	        return false;
	}

	protected LockMode[] getLockModes(Map lockModes) {
		// unfortunately this stuff can't be cached because
		// it is per-invocation, not constant for the
		// QueryTranslator instance
		HashMap nameLockModes = new HashMap();
		if ( lockModes != null ) {
			Iterator iter = lockModes.entrySet().iterator();
			while ( iter.hasNext() ) {
				Map.Entry me = (Map.Entry) iter.next();
				nameLockModes.put( 
						getAliasName( (String) me.getKey() ),
						me.getValue() 
				);
			}
		}
		LockMode[] lockModeArray = new LockMode[names.length];
		for ( int i = 0; i < names.length; i++ ) {
			LockMode lm = (LockMode) nameLockModes.get( names[i] );
			if ( lm == null ) lm = LockMode.NONE;
			lockModeArray[i] = lm;
		}
		return lockModeArray;
	}

	protected String applyLocks(String sql, Map lockModes, Dialect dialect) throws QueryException {
		// can't cache this stuff either (per-invocation)
		final String result;
		if ( lockModes == null || lockModes.size() == 0 ) {
			result = sql;
		}
		else {
			Map aliasedLockModes = new HashMap();
			Iterator iter = lockModes.entrySet().iterator();
			while ( iter.hasNext() ) {
				Map.Entry me = ( Map.Entry ) iter.next();
				aliasedLockModes.put( getAliasName( (String) me.getKey() ), me.getValue() );
			}
			Map keyColumnNames = null;
			if ( dialect.forUpdateOfColumns() ) {
				keyColumnNames = new HashMap();
				for ( int i=0; i<names.length; i++ ) {
					keyColumnNames.put( names[i], persisters[i].getIdentifierColumnNames() );
				}
			}
			result = sql + new ForUpdateFragment(dialect, aliasedLockModes, keyColumnNames).toFragmentString();
		}
		logQuery( queryString, result );
		return result;
	}

	protected boolean upgradeLocks() {
		return true;
	}

	protected int getCollectionOwner() {
		return collectionOwnerColumn;
	}

	protected void setFactory(SessionFactoryImplementor factory) {
		this.factory = factory;
	}

	protected SessionFactoryImplementor getFactory() {
		return factory;
	}

	protected boolean isCompiled() {
		return compiled;
	}

	public String toString() {
		return queryString;
	}

	protected int[] getOwners() {
		return owners;
	}

	protected String[] getUniqueKeyReferences() {
		return uniqueKeyReferences;
	}

//	public Class getHolderClass() {
//		return holderClass;
//	}

	public Map getEnabledFilters() {
		return enabledFilters;
	}
	
	public ScrollableResults scroll(
			final QueryParameters queryParameters,
			final SessionImplementor session) 
	throws HibernateException, SQLException {
		return scroll(queryParameters, returnTypes, holderClass, session);
	}

	protected String getQueryIdentifier() {
		return queryString;
	}
}
