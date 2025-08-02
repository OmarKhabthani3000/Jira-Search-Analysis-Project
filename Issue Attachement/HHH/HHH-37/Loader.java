//$Id: Loader.java,v 1.40 2004/08/21 10:51:57 oneovthafew Exp $
package org.hibernate.loader;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.AssertionFailure;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.QueryException;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.StaleObjectStateException;
import org.hibernate.WrongClassException;
import org.hibernate.cache.QueryCache;
import org.hibernate.cache.QueryKey;
import org.hibernate.collection.CollectionPersister;
import org.hibernate.collection.PersistentCollection;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.EntityKey;
import org.hibernate.engine.EntityUniqueKey;
import org.hibernate.engine.QueryParameters;
import org.hibernate.engine.RowSelection;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.engine.TwoPhaseLoad;
import org.hibernate.impl.ScrollableResultsImpl;
import org.hibernate.persister.Loadable;
import org.hibernate.persister.UniqueKeyLoadable;
import org.hibernate.pretty.MessageHelper;
import org.hibernate.type.Type;
import org.hibernate.util.JDBCExceptionReporter;
import org.hibernate.util.NestedHolderClass;
import org.hibernate.util.StringHelper;

/**
 * Abstract superclass of object loading (and querying) strategies. This class implements
 * useful common functionality that concrete loaders delegate to. It is not intended that this
 * functionality would be directly accessed by client code. (Hence, all methods of this class
 * are declared <tt>protected</tt> or <tt>private</tt>.) This class relies heavily upon the
 * <tt>Loadable</tt> interface, which is the contract between this class and
 * <tt>EntityPersister</tt>s that may be loaded by it.<br>
 * <br>
 * The present implementation is able to load any number of columns of entities and at most
 * one collection role per query.
 *
 * @see org.hibernate.persister.Loadable
 * @author Gavin King
 */
public abstract class Loader {

	private static final Log log = LogFactory.getLog(Loader.class);

	/**
	 * The SQL query string to be called; implemented by all subclasses
	 */
	protected abstract String getSQLString();
	/**
	 * An array of persisters of entity classes contained in each row of results;
	 * implemented by all subclasses
	 */
	protected abstract Loadable[] getPersisters();
	/**
	 * The suffix identifies a particular column of results in the SQL <tt>ResultSet</tt>;
	 * implemented by all subclasses
	 */
	protected abstract String[] getSuffixes();
	/**
	 * An array of indexes of the entity that owns a one-to-one association
	 * to the entity at the given index (-1 if there is no "owner")
	 */
	protected int[] getOwners() {
		return null;
	}
	/**
	 * An array of unique key property names by which the corresponding
	 * entities are referenced by other entities in the result set
	 */
	protected String[] getUniqueKeyReferences() {
		return null;
	}
	/**
	 * An (optional) persister for a collection to be initialized; only collection loaders
	 * return a non-null value
	 */
	protected CollectionPersister getCollectionPersister() {
		return null;
	}
	/**
	 * Get the index of the entity that owns the collection, or -1
	 * if there is no owner in the query results (ie. in the case of a
	 * collection initializer) or no collection.
	 */
	protected int getCollectionOwner() {
		return -1;
	}
	/**
	 * What lock mode does this load entities with?
	 * @param lockModes a collection of lock modes specified dynamically via the Query interface
	 */
	protected abstract LockMode[] getLockModes(Map lockModes);
	/**
	 * Append <tt>FOR UPDATE OF</tt> clause, if necessary. This
	 * empty superclass implementation merely returns its first
	 * argument.
	 */
	protected String applyLocks(String sql, Map lockModes, Dialect dialect) throws HibernateException {
		return sql;
	}
	/**
	 * Does this query return objects that might be already cached
	 * by the session, whose lock mode may need upgrading
	 */
	protected boolean upgradeLocks() {
		return false;
	}
	/**
	 * Return false is this loader is a batch entity loader
	 */
	protected boolean isSingleRowLoader() {
		return false;
	}
	
	/**
	 * Modify the SQL, adding lock hints and comments, if necessary
	 */
	protected String preprocessSQL(String sql, QueryParameters parameters, Dialect dialect)
	throws HibernateException {
		sql = applyLocks( sql, parameters.getLockModes(), dialect );
		String comment = parameters.getComment();
		if (comment==null) {
			return sql;
		}
		else {
			return new StringBuffer( comment.length() + sql.length() + 5 )
				.append("/*")
				.append(comment)
				.append("*/ ")
				.append(sql)
				.toString();
		}
	}
	
	/**
	 * Execute an SQL query and attempt to instantiate instances of the class mapped by the given
	 * persister from each row of the <tt>ResultSet</tt>. If an object is supplied, will attempt to
	 * initialize that object. If a collection is supplied, attempt to initialize that collection.
	 */
	private List doQueryAndInitializeNonLazyCollections(
		final SessionImplementor session,
		final QueryParameters queryParameters,
		final boolean returnProxies
	) throws SQLException, HibernateException {

		session.beforeLoad();
		List result;
		try {
			result = doQuery(session, queryParameters, returnProxies);
		}
		finally {
			session.afterLoad();
		}
		session.initializeNonLazyCollections();
		return result;
	}

	public Object loadSingleRow(
			final ResultSet resultSet,
			final SessionImplementor session,
			final QueryParameters queryParameters,
			final boolean returnProxies) 
	throws SQLException, HibernateException {
		
		final int entitySpan = getPersisters().length;
		final List hydratedObjects = entitySpan==0 ? null : new ArrayList(entitySpan);
		final Object result = getRowFromResultSet(
				resultSet,
				session,
				queryParameters,
				getLockModes( queryParameters.getLockModes() ),
				null,
				hydratedObjects,
				new EntityKey[entitySpan],
				returnProxies
		);
		initializeEntitiesAndCollections(hydratedObjects, resultSet, session);
		session.initializeNonLazyCollections();
		return result;
	}
	
	private static EntityKey getOptionalObjectKey(QueryParameters queryParameters, SessionImplementor session) {
		final Object optionalObject = queryParameters.getOptionalObject();
		final Serializable optionalId = queryParameters.getOptionalId();
		final String optionalEntityName = queryParameters.getOptionalEntityName();
		
		final EntityKey optionalObjectKey;
		if (optionalObject!=null && optionalEntityName!=null) {
			return new EntityKey( 
					optionalId, 
					session.getEntityPersister(optionalEntityName, optionalObject) 
			);
		}
		else {
			return null;
		}
		
	}

	private Object getRowFromResultSet(
		final ResultSet resultSet,
		final SessionImplementor session,
		final QueryParameters queryParameters,
		final LockMode[] lockModeArray,
		final EntityKey optionalObjectKey,
		final List hydratedObjects,
		final EntityKey[] keys,
		boolean returnProxies) 
	throws SQLException, HibernateException {

		final Loadable[] persisters = getPersisters();
		final int entitySpan = persisters.length;
		
		for ( int i=0; i<entitySpan; i++ ) {
			keys[i] = getKeyFromResultSet(
					i,
					persisters[i],
					i==entitySpan-1 ? 
							queryParameters.getOptionalId() : 
							null,
					resultSet,
					session
			);
			//TODO: the i==entitySpan-1 bit depends upon subclass implementation (very bad)
		}

		registerNonExists(keys, persisters, session);

		// this call is side-effecty
		Object[] row = getRow(
				resultSet,
				persisters,
				keys,
				queryParameters.getOptionalObject(),
				optionalObjectKey,
				lockModeArray,
				hydratedObjects,
				session
		);
		
		readCollectionElements(row, resultSet, session);

		if (returnProxies) {
			// now get an existing proxy for each row element (if there is one)
			for ( int i=0; i<entitySpan; i++ ) row[i] = session.proxyFor( persisters[i], keys[i], row[i] );
		}

		return getResultColumnOrRow(row, resultSet, session);

	}
	
	/**
	 * Read any collection elements contained in a single row of the result set
	 */
	private void readCollectionElements(Object[] row, ResultSet resultSet, SessionImplementor session) 
	throws SQLException, HibernateException {
		
		//TODO: make this handle multiple collection roles!
		
		final CollectionPersister collectionPersister = getCollectionPersister();
		if (collectionPersister!=null) {
			
			final int collectionOwner = getCollectionOwner();
			final boolean hasCollectionOwners = collectionOwner>=0;
			//true if this is a query and we are loading multiple instances of the same collection role
			//otherwise this is a CollectionInitializer and we are loading up a single collection or batch

			final Object owner = hasCollectionOwners ? 
					row[collectionOwner] : 
					null; //if null, owner will be retrieved from session
					
			final Serializable key;
			if (owner==null) {
				key = null;
			}
			else {
				key = collectionPersister.getCollectionType().getKeyOfOwner(owner, session);
				//TODO: old version did not require hashmap lookup:
				//keys[collectionOwner].getIdentifier()
			}
			
			readCollectionElement(owner, key, resultSet, session);
			
		}
	}

	private List doQuery(
		final SessionImplementor session,
		final QueryParameters queryParameters,
		final boolean returnProxies
	) throws SQLException, HibernateException {

		final RowSelection selection = queryParameters.getRowSelection();
		final int maxRows = hasMaxRows(selection) ?
			selection.getMaxRows().intValue() :
			Integer.MAX_VALUE;

		final int entitySpan = getPersisters().length;

		final ArrayList hydratedObjects = entitySpan==0 ? null : new ArrayList(entitySpan*10);
		final List results = new ArrayList();
		final PreparedStatement st = prepareQueryStatement(queryParameters, false, session);
		final ResultSet rs = getResultSet(st, selection, session);

		final LockMode[] lockModeArray = getLockModes( queryParameters.getLockModes() );
		final EntityKey optionalObjectKey = getOptionalObjectKey(queryParameters, session);
		
		try {

			handleEmptyCollections( queryParameters.getCollectionKeys(), rs, session );

			final EntityKey[] keys = new EntityKey[entitySpan]; //we can reuse it each time

			if ( log.isTraceEnabled() ) log.trace("processing result set");

			int count;
			for ( count=0; count<maxRows && rs.next(); count++ ) {

				Object result = getRowFromResultSet(
						rs,
						session,
						queryParameters,
						lockModeArray,
						optionalObjectKey,
						hydratedObjects,
						keys,
						returnProxies
				);
				results.add(result);
			}

			if ( log.isTraceEnabled() ) log.trace("done processing result set (" + count + " rows)");

		}
		catch (SQLException sqle) {
			JDBCExceptionReporter.logExceptions(sqle);
			throw sqle;
		}
		finally {
			session.getBatcher().closeQueryStatement(st, rs);
		}

		initializeEntitiesAndCollections(hydratedObjects, rs, session);

		return results; //getResultList(results);

	}
	
	private void initializeEntitiesAndCollections(List hydratedObjects, Object resultSetId, SessionImplementor session) 
	throws HibernateException {
		if ( getPersisters().length > 0 ) { //if no persisters, hydratedObjects is null
			int hydratedObjectsSize = hydratedObjects.size();
			if ( log.isTraceEnabled() ) log.trace("total objects hydrated: " + hydratedObjectsSize);
			for ( int i=0; i<hydratedObjectsSize; i++ ) {
				TwoPhaseLoad.initializeEntity( hydratedObjects.get(i), session );
			}
		}
		final CollectionPersister collectionPersister = getCollectionPersister();
		if ( collectionPersister!=null ) {
			//this is a query and we are loading multiple instances of the same collection role
			session.endLoadingCollections(collectionPersister, resultSetId);
		}
	}

	protected List getResultList(List results) throws QueryException {
		return results;
	}

	/**
	 * Get the actual object that is returned in the user-visible result list.
	 * This empty implementation merely returns its first argument. This is
	 * overridden by some subclasses.
	 */
	protected Object getResultColumnOrRow(Object[] row, ResultSet rs, SessionImplementor session)
	throws SQLException, HibernateException {
		return row;
	}

	/**
	 * For missing objects associated by one-to-one with another object in the
	 * result set, register the fact that the the object is missing with the
	 * session.
	 */
	private void registerNonExists(
		final EntityKey[] keys,
		final Loadable[] persisters,
		final SessionImplementor session
	) {
		final int[] owners = getOwners();
		if (owners!=null) {
			String[] uniqueKeyNames = getUniqueKeyReferences();
			for (int i=0; i<keys.length; i++) {
				int owner = owners[i];
				if (owner>-1) {
					EntityKey ownerKey = keys[owner];
					if ( keys[i]==null && ownerKey!=null ) {
						if ( uniqueKeyNames==null || uniqueKeyNames[i]==null ) {
							session.addNonExist( new EntityKey( ownerKey.getIdentifier(), persisters[i] ) );
						}
						else {
							session.addNonExist( new EntityUniqueKey( 
									persisters[i].getEntityName(), 
									uniqueKeyNames[i], 
									ownerKey.getIdentifier() 
							) );
						}
					}
				}
			}
		}
	}

	/**
	 * Read one collection element from the current row of the JDBC result set
	 */
	private void readCollectionElement(
		final Object optionalOwner,
		final Serializable optionalKey,
		final ResultSet rs,
		final SessionImplementor session)
	throws HibernateException, SQLException {
		
		final CollectionPersister collectionPersister = getCollectionPersister();
		final Serializable collectionRowKey = (Serializable) collectionPersister.readKey(rs, session);
		if (collectionRowKey!=null) {
			
			if ( log.isDebugEnabled() ) {
				log.debug( 
						"found row of collection: " + 
						MessageHelper.infoString(collectionPersister, collectionRowKey) 
				);
			}
			
			Object owner = optionalOwner;
			if (owner==null) {
				owner = session.getCollectionOwner(collectionRowKey, collectionPersister);
				if (owner==null) {
					//TODO: This is assertion is disabled because there is a bug that means the
					//	  original owner of a transient, uninitialized collection is not known
					//	  if the collection is re-referenced by a different object associated
					//	  with the current Session
					// I don't think this bug exists anymore in 2.1!!
					//throw new AssertionFailure("bug loading unowned collection");
				}
			}
			
			PersistentCollection rowCollection = session.getLoadingCollection(
					collectionPersister, 
					collectionRowKey, 
					rs
			);
			if (rowCollection!=null) rowCollection.readFrom(rs, collectionPersister, owner);
			
		}
		else if (optionalKey!=null) {
			
			if ( log.isDebugEnabled() ) {
				log.debug( 
					"result set contains (possibly empty) collection: " + 
					MessageHelper.infoString(collectionPersister, optionalKey) 
				);
			}
			
			session.getLoadingCollection(collectionPersister, optionalKey, rs); //handle empty collection
			
		}
	}

	/**
	 * If this is a collection initializer, we need to tell the session that a collection
	 * is being initilized, to account for the possibility of the collection having
	 * no elements (hence no rows in the result set).
	 */
	private void handleEmptyCollections(
		final Serializable[] keys,
		final Object resultSetId,
		final SessionImplementor session)
	throws HibernateException {
		if (keys!=null) {
			
			CollectionPersister collectionPersister = getCollectionPersister();
			for ( int i=0; i<keys.length; i++ ) {
				//handle empty collections
				
				if ( log.isDebugEnabled() ) {
					log.debug( 
						"result set contains (possibly empty) collection: " + 
						MessageHelper.infoString(collectionPersister, keys[i]) 
					);
				}
				
				session.getLoadingCollection(collectionPersister, keys[i], resultSetId);
			}
			
		}
	}

	/**
	 * Read a row of <tt>Key</tt>s from the <tt>ResultSet</tt> into the given array.
	 * Warning: this method is side-effecty.
	 *
	 * If an <tt>id</tt> is given, don't bother going to the <tt>ResultSet</tt>.
	 */
	private EntityKey getKeyFromResultSet(
		final int i,
		final Loadable persister,
		final Serializable id,
		final ResultSet rs,
		final SessionImplementor session)
	throws HibernateException, SQLException {

		Serializable resultId;

		// if we know there is exactly 1 row, we can skip.
		// it would be great if we could _always_ skip this;
		// it is a problem for <key-many-to-one>

		if ( isSingleRowLoader() && id!=null ) {
			resultId = id;
		}
		else {
			Type idType = persister.getIdentifierType();
			resultId = (Serializable) idType.nullSafeGet(rs, suffixedKeyColumns[i], session, null); //problematic for <key-many-to-one>!
			if ( id!=null && resultId!=null && id.equals(resultId) ) resultId = id; //use the id passed in
		}

		return resultId==null ?
			null :
			new EntityKey(resultId, persister);
	}

	/**
	 * Check the version of the object in the <tt>ResultSet</tt> against
	 * the object version in the session cache, throwing an exception
	 * if the version numbers are different
	 */
	private void checkVersion(
		final int i,
		final Loadable persister,
		final Serializable id,
		final Object version,
		final ResultSet rs,
		final SessionImplementor session)
	throws HibernateException, SQLException {

		if (version!=null) { //null version means the object is in the process of being loaded somewhere else in the ResultSet
			Type versionType = persister.getVersionType();
			Object currentVersion = versionType.nullSafeGet(rs, suffixedVersionColumns[i], session, null);
			if ( !versionType.equals(version, currentVersion) ) {
				throw new StaleObjectStateException( persister.getEntityName(), id );
			}
		}
	}

	/**
	 * Resolve any ids for currently loaded objects, duplications within the
	 * <tt>ResultSet</tt>, etc. Instantiate empty objects to be initialized from the
	 * <tt>ResultSet</tt>. Return an array of objects (a row of results) and an
	 * array of booleans (by side-effect) that determine whether the corresponding
	 * object should be initialized.
	 */
	private Object[] getRow(
		final ResultSet rs,
		final Loadable[] persisters,
		final EntityKey[] keys,
		final Object optionalObject,
		final EntityKey optionalObjectKey,
		final LockMode[] lockModes,
		final List hydratedObjects,
		final SessionImplementor session)
	throws HibernateException, SQLException {

		final int cols = persisters.length;
		final String[] suffixes = getSuffixes();

		if ( log.isDebugEnabled() ) log.debug( "result row: " + StringHelper.toString(keys) );

		final Object[] rowResults = new Object[cols];

		for ( int i=0; i<cols; i++ ) {

			Object object=null;
			EntityKey key = keys[i];

			if ( keys[i]==null ) {
				//do nothing
			}
			else {

				//If the object is already loaded, return the loaded one
				object = session.getEntityUsingInterceptor(key); //TODO: should it be getSessionEntity() ?
				if (object!=null) {
					//its already loaded so don't need to hydrate it
					instanceAlreadyLoaded(
						rs, 
						i, 
						persisters[i], 
						suffixes[i], 
						key, 
						object, 
						lockModes[i], 
						session
					);
				}
				else {
					object = instanceNotYetLoaded(
						rs, 
						i, 
						persisters[i], 
						suffixes[i], 
						key, 
						lockModes[i], 
						optionalObjectKey, 
						optionalObject, 
						hydratedObjects, 
						session
					);
				}

			}

			rowResults[i]=object;

		}

		return rowResults;

	}

	/**
	 * The entity instance is already in the session cache
	 */
	private void instanceAlreadyLoaded(
		final ResultSet rs,
		final int i,
		final Loadable persister,
		final String suffix,
		final EntityKey key,
		final Object object,
		final LockMode lockMode,
		final SessionImplementor session)
	throws HibernateException, SQLException {

		if ( !persister.isInstance(object) ) {
			throw new WrongClassException( "loaded object was of wrong class", key.getIdentifier(), persister.getEntityName() );
		}

		if ( LockMode.NONE!=lockMode && upgradeLocks() ) { //no point doing this if NONE was requested

			if (
				persister.isVersioned() &&
				session.getLockMode(object).lessThan(lockMode)
				// we don't need to worry about existing version being uninitialized
				// because this block isn't called by a re-entrant load (re-entrant
				// loads _always_ have lock mode NONE)
			) {
				//we only check the version when _upgrading_ lock modes
				checkVersion(i, persister, key.getIdentifier(), session.getVersion(object), rs, session);
				//we need to upgrade the lock mode to the mode requested
				session.setLockMode(object, lockMode);
			}

		}

	}

	/**
	 * The entity instance is not in the session cache
	 */
	private Object instanceNotYetLoaded(
		final ResultSet rs,
		final int i,
		final Loadable persister,
		final String suffix,
		final EntityKey key,
		final LockMode lockMode,
		final EntityKey optionalObjectKey,
		final Object optionalObject,
		final List hydratedObjects,
		final SessionImplementor session)
	throws HibernateException, SQLException {
		Object object;

		final String instanceClass = getInstanceClass(rs, i, persister, key.getIdentifier(), session);

		if ( optionalObjectKey!=null && key.equals(optionalObjectKey) ) {
			//its the given optional object
			object=optionalObject;
		}
		else {
			// instantiate a new instance
			object = session.instantiate( instanceClass, key.getIdentifier() );
		}

		//need to hydrate it.

		// grab its state from the ResultSet and keep it in the Session
		// (but don't yet initialize the object itself)
		// note that we acquire LockMode.READ even if it was not requested
		LockMode acquiredLockMode = lockMode==LockMode.NONE ? LockMode.READ : lockMode;
		loadFromResultSet(rs, i, object, instanceClass, key, suffix, acquiredLockMode, persister, session);

		//materialize associations (and initialize the object) later
		hydratedObjects.add(object);

		return object;
	}


	/**
	 * Hydrate the state an object from the SQL <tt>ResultSet</tt>, into
	 * an array or "hydrated" values (do not resolve associations yet),
	 * and pass the hydrates state to the session.
	 */
	private void loadFromResultSet(
		final ResultSet rs,
		final int i,
		final Object object,
		final String instanceEntityName,
		final EntityKey key,
		final String suffix,
		final LockMode lockMode,
		final Loadable rootPersister,
		final SessionImplementor session)
	throws SQLException, HibernateException {

		if ( log.isTraceEnabled() ) log.trace( "Initializing object from ResultSet: " + key );

		final Serializable id = key.getIdentifier();

		// Get the persister for the _subclass_
		final Loadable persister = (Loadable) session.getFactory().getEntityPersister(instanceEntityName);

		// add temp entry so that the next step is circular-reference
		// safe - only needed because some types don't take proper
		// advantage of two-phase-load (esp. components)
		TwoPhaseLoad.addUninitializedEntity(id, object, persister, lockMode, session);
		
		//This is not very nice (and quite slow):
		final String[][] cols = persister==rootPersister ?
			suffixedPropertyColumns[i] :
			getSuffixedPropertyAliases(persister, suffix);

		final Object[] values = persister.hydrate(rs, id, object, rootPersister, session, cols, suffix);
		
		final Object rowId = persister.hasRowId() ? rs.getObject(Loadable.ROWID_ALIAS + suffix) : null;
		
		final String[] ukNames = getUniqueKeyReferences();
		if ( ukNames!=null && ukNames[i]!=null ) {
			Serializable uk = (Serializable) values[ 
					( (UniqueKeyLoadable) persister ).getPropertyIndex( ukNames[i] ) 
			];
			//polymorphism not really handled completely correctly, 
			//perhaps...well, actually its ok, assuming that the 
			//entity name used in the lookup is the same as the 
			//the one used here, which it will be
			String ren = rootPersister.getEntityName();
			
			session.addEntity( new EntityUniqueKey(ren, ukNames[i], uk), object );
		}

		TwoPhaseLoad.postHydrate(persister, id, values, rowId, object, lockMode, session);

	}

	/**
	 * Determine the concrete class of an instance in the <tt>ResultSet</tt>
	 */
	private String getInstanceClass(
		final ResultSet rs,
		final int i,
		final Loadable persister,
		final Serializable id,
		final SessionImplementor session)
	throws HibernateException, SQLException {

		if ( persister.hasSubclasses() ) {

			// Code to handle subclasses of topClass
			Object discriminatorValue = persister.getDiscriminatorType().nullSafeGet(
				rs, suffixedDiscriminatorColumn[i], session, null
			);

			final String result = persister.getSubclassForDiscriminatorValue(discriminatorValue);

			if (result==null) {
				//woops we got an instance of another class heirarchy branch
				throw new WrongClassException( 
						"Discriminator: " + discriminatorValue, 
						id, 
						persister.getEntityName() 
				);
			}

			return result;

		}
		else {
			return persister.getEntityName();
		}
	}

	/**
	 * Advance the cursor to the first required row of the <tt>ResultSet</tt>
	 */
	private void advance(
		final ResultSet rs,
		final RowSelection selection,
		final SessionImplementor session)
	throws SQLException {

		final int firstRow = getFirstRow(selection);
		if ( firstRow!=0 ) {
			if ( session.getFactory().isScrollableResultSetsEnabled() ) {
				// we can go straight to the first required row
				rs.absolute(firstRow);
			}
			else {
				// we need to step through the rows one row at a time (slow)
				for ( int m=0; m<firstRow; m++ ) rs.next();
			}
		}
	}

	private static boolean hasMaxRows(RowSelection selection) {
		return selection!=null && selection.getMaxRows()!=null;
	}

	private static int getFirstRow(RowSelection selection) {
		if ( selection==null || selection.getFirstRow()==null ) {
			return 0;
		}
		else {
			return selection.getFirstRow().intValue();
		}
	}

	/**
	 * Should we pre-process the SQL string, adding a dialect-specific
	 * LIMIT clause.
	 */
	private static boolean useLimit(final RowSelection selection, final Dialect dialect) {
		return dialect.supportsLimit() && hasMaxRows(selection);
	}

	/**
	 * Bind positional parameter values to the <tt>PreparedStatement</tt>
	 * (these are parameters specified by a JDBC-style ?).
	 */
	protected int bindPositionalParameters(
		final PreparedStatement st,
		final QueryParameters queryParameters,
		final int start,
		final SessionImplementor session)
	throws SQLException, HibernateException {

		final Object[] values = queryParameters.getPositionalParameterValues();
		final Type[] types = queryParameters.getPositionalParameterTypes();
		int span=0;
		for ( int i=0; i<values.length; i++) {
			types[i].nullSafeSet( st, values[i], start + span, session );
			span += types[i].getColumnSpan( session.getFactory() );
		}
		return span;
	}

	private String processFilterParameters(String sql, QueryParameters queryParameters, SessionImplementor session) {
		Dialect dialect = session.getFactory().getDialect();
		String symbols = new StringBuffer().append(" =><!+-*/()',")
			.append( dialect.openQuote() )
			.append( dialect.closeQuote() )
			.toString();
		StringTokenizer tokens = new StringTokenizer(sql, symbols, true);
		StringBuffer result = new StringBuffer();

		List parameters = new ArrayList();
		List parameterTypes = new ArrayList();

		while ( tokens.hasMoreTokens() ) {
			final String token = tokens.nextToken();
			if ( token.startsWith(":") ) {
				result.append('?');
				String filterParameterName = token.substring(1);
				parameters.add( session.getFilterParameterValue(filterParameterName) );
				parameterTypes.add( session.getFilterParameterType(filterParameterName) );
			}
			else {
				result.append(token);
			}
		}
		parameters.addAll( Arrays.asList( queryParameters.getPositionalParameterValues() ) );
		parameterTypes.addAll( Arrays.asList( queryParameters.getPositionalParameterTypes() ) );
		queryParameters.setPositionalParameterValues( parameters.toArray() );
		queryParameters.setPositionalParameterTypes( (Type[]) parameterTypes.toArray(new Type[0]) );
		return result.toString();
	}



	/**
	 * Obtain a <tt>PreparedStatement</tt> with all parameters pre-bound.
	 * Bind JDBC-style <tt>?</tt> parameters, named parameters, and
	 * limit parameters.
	 */
	protected final PreparedStatement prepareQueryStatement(
		final QueryParameters queryParameters,
		final boolean scroll,
		final SessionImplementor session)
	throws SQLException, HibernateException {

		String sql = getSQLString();
		sql = processFilterParameters(sql, queryParameters, session);
		Dialect dialect = session.getFactory().getDialect();
		RowSelection selection = queryParameters.getRowSelection();
		boolean useLimit = useLimit(selection, dialect);
		boolean hasFirstRow = getFirstRow(selection)>0;
		boolean useOffset = hasFirstRow && useLimit && dialect.supportsLimitOffset();
		
		boolean useScrollableResultSetToSkip = hasFirstRow && 
			!useOffset && 
			session.getFactory().isScrollableResultSetsEnabled();
		ScrollMode scrollMode = scroll ? queryParameters.getScrollMode() : ScrollMode.SCROLL_INSENSITIVE;
		
		if (useLimit) sql = dialect.getLimitString( sql.trim(), useOffset, getMaxOrLimit(selection, dialect) ); //use of trim() here is ugly?

		sql = preprocessSQL(sql, queryParameters, dialect);

		PreparedStatement st = session.getBatcher()
			.prepareQueryStatement(sql, scroll || useScrollableResultSetToSkip, scrollMode);

		try {

			int col=1;

			if ( useLimit && dialect.bindLimitParametersFirst() ) {
				col += bindLimitParameters(st, col, selection, session);
			}
			col += bindPositionalParameters(st, queryParameters, col, session);
			col += bindNamedParameters(st, queryParameters.getNamedParameters(), col, session);

			if ( useLimit && !dialect.bindLimitParametersFirst() ) {
				col += bindLimitParameters(st, col, selection, session);
			}

			if (!useLimit) setMaxRows(st, selection);
			if (selection!=null) {
				if ( selection.getTimeout()!=null ) {
					st.setQueryTimeout( selection.getTimeout().intValue() );
				}
				if ( selection.getFetchSize()!=null ) {
					st.setFetchSize( selection.getFetchSize().intValue() );
				}
			}
		}
		catch (SQLException sqle) {
			JDBCExceptionReporter.logExceptions(sqle);
			session.getBatcher().closeQueryStatement(st, null);
			throw sqle;
		}
		catch (HibernateException he) {
			session.getBatcher().closeQueryStatement(st, null);
			throw he;
		}

		return st;
	}

	/**
	 * Some dialect-specific LIMIT clauses require the maximium last row number,
	 * others require the maximum returned row count.
	 */
	private static int getMaxOrLimit(final RowSelection selection, final Dialect dialect) {
		final int firstRow = getFirstRow(selection);
		final int lastRow  = selection.getMaxRows().intValue();
		if ( dialect.useMaxForLimit() ) {
			return lastRow + firstRow;
		}
		else {
			return lastRow;
		}
	}

	/**
	 * Bind parameters needed by the dialect-specific LIMIT clause
	 */
	private int bindLimitParameters(
		final PreparedStatement st,
		final int index,
		final RowSelection selection,
		final SessionImplementor session) throws SQLException {

		Dialect dialect = session.getFactory().getDialect();
		if ( !dialect.supportsVariableLimit() ) return 0;
		if ( !hasMaxRows(selection) ) throw new AssertionFailure("no max results set");
		int firstRow = getFirstRow(selection);
		int lastRow = getMaxOrLimit(selection, dialect);
		boolean hasFirstRow = firstRow>0 && dialect.supportsLimitOffset();
		boolean reverse = dialect.bindLimitParametersInReverseOrder();
		if (hasFirstRow) st.setInt( index + (reverse ? 1 : 0 ), firstRow );
		st.setInt( index + ( reverse || !hasFirstRow ? 0 : 1 ), lastRow );
		return hasFirstRow?2:1;
	}

	/**
	 * Use JDBC API to limit the number of rows returned by the SQL query if necessary
	 */
	private void setMaxRows(final PreparedStatement st, final RowSelection selection) 
	throws SQLException {
		if ( hasMaxRows(selection) ) {
			st.setMaxRows( selection.getMaxRows().intValue() + getFirstRow(selection) );
		}
	}

	/**
	 * Fetch a <tt>PreparedStatement</tt>, call <tt>setMaxRows</tt> and then execute it,
	 * advance to the first result and return an SQL <tt>ResultSet</tt>
	 */
	protected final ResultSet getResultSet(
		final PreparedStatement st,
		final RowSelection selection,
		final SessionImplementor session)
	throws SQLException, HibernateException {

		ResultSet rs = null;
		try {
			rs = session.getBatcher().getResultSet(st);
			Dialect dialect = session.getFactory().getDialect();
			if ( !dialect.supportsLimitOffset() || !useLimit(selection, dialect) ) {
				advance(rs, selection, session);
			}
			return rs;
		}
		catch (SQLException sqle) {
			JDBCExceptionReporter.logExceptions(sqle);
			session.getBatcher().closeQueryStatement(st, rs);
			throw sqle;
		}
	}

	/**
	 * Bind named parameters to the <tt>PreparedStatement</tt>. This has an empty
	 * implementation on this superclass and should be implemented by subclasses
	 * (queries) which allow named parameters.
	 */
	protected int bindNamedParameters(
		PreparedStatement st,
		Map namedParams,
		int start,
		SessionImplementor session)
	throws SQLException, HibernateException {
		return 0;
	}

	/**
	 * Called by subclasses that load entities
	 */
	protected final List loadEntity(
		final SessionImplementor session,
		final Serializable id,
		final Type identifierType,
		final Object optionalObject,
		final String optionalEntityName,
		final Serializable optionalIdentifier
	) throws SQLException, HibernateException {
		return doQueryAndInitializeNonLazyCollections(
			session,
			new QueryParameters(
					new Type[] {identifierType}, 
					new Object[] {id}, 
					optionalObject, 
					optionalEntityName, 
					optionalIdentifier
			),
			false
		);
	}

	/**
	 * Called by subclasses that batch load entities
	 */
	protected final List loadEntityBatch(
		final SessionImplementor session,
		final Serializable[] ids,
		final Type idType,
		final Object optionalObject,
		final String optionalEntityName,
		final Serializable optionalId
	) throws SQLException, HibernateException {

		Type[] types = new Type[ids.length];
		Arrays.fill(types, idType);
		return doQueryAndInitializeNonLazyCollections(
			session,
			new QueryParameters(types, ids, optionalObject, optionalEntityName, optionalId),
			false
		);
	}

	/**
	 * Called by subclasses that initialize collections
	 */
	protected final void loadCollection(
		final SessionImplementor session,
		final Serializable id,
		final Type type)
	throws SQLException, HibernateException {

		Serializable[] ids = new Serializable[] {id};
		doQueryAndInitializeNonLazyCollections(
			session, 
			new QueryParameters( new Type[] {type}, ids, ids ), 
			true
		);
	}

	/**
	 * Called by subclasses that batch initialize collections
	 */
	protected final void loadCollectionBatch(
		final SessionImplementor session,
		final Serializable[] ids,
		final Type type)
	throws SQLException, HibernateException {

		Type[] idTypes = new Type[ids.length];
		Arrays.fill(idTypes, type);
		doQueryAndInitializeNonLazyCollections(
			session, 
			new QueryParameters(idTypes, ids, ids), 
			true
		);
	}

	/**
	 * Return the query results, using the query cache, called
	 * by subclasses that implement cacheable queries
	 */
	protected List list(
		final SessionImplementor session,
		final QueryParameters queryParameters,
		final Set querySpaces,
		final Type[] resultTypes)
	throws SQLException, HibernateException {

		final SessionFactoryImplementor factory = session.getFactory();

		final boolean cacheable = factory.isQueryCacheEnabled() && queryParameters.isCacheable();

		if (cacheable) {
			QueryCache queryCache = factory.getQueryCache( queryParameters.getCacheRegion() );
			QueryKey key = new QueryKey( getSQLString(), queryParameters );
			List result = null;
			if ( !queryParameters.isForceCacheRefresh() ) {
				result = queryCache.get(key, resultTypes, querySpaces, session);
			}
			if (result==null) {
				result = doList(session, queryParameters);
				if (cacheable) queryCache.put(key, resultTypes, result, session);
			} 
			else {
				if ( getQueryIdentifier()!=null && factory.getStatistics().isStatisticsEnabled() ) {
					factory.getStatisticsImplementor().queryCacheHit( getQueryIdentifier() );
				}
			}
			return getResultList(result);
		}
		else {
			return getResultList( doList(session, queryParameters) );
		}
	}

	/**
	 * Actually execute a query, ignoring the query cache
	 */
	protected List doList(final SessionImplementor session, final QueryParameters queryParameters)
	throws SQLException, HibernateException {	

		final boolean stats = getQueryIdentifier()!=null && 
			session.getFactory().getStatistics().isStatisticsEnabled();
		long startTime = 0;
		if (stats) startTime = System.currentTimeMillis();
		
		List result = doQueryAndInitializeNonLazyCollections(session, queryParameters, true);
		
		if (stats) {
			session.getFactory().getStatisticsImplementor().queryExecuted( 
				getQueryIdentifier(), 
				result.size(), 
				System.currentTimeMillis() - startTime 
			);
		}
		
		return result;
	}
	
	/**
	 * Return the query results, as an instance of <tt>ScrollableResults</tt>
	 */
	protected ScrollableResults scroll(
		final QueryParameters queryParameters, 
		final Type[] returnTypes, 
		final NestedHolderClass holderClass, 
		final SessionImplementor session) 
	throws HibernateException, SQLException {
		
		if ( getCollectionPersister()!=null ) {
			throw new HibernateException("Cannot scroll queries which initialize collections");
		}

		final boolean stats = getQueryIdentifier()!=null && 
			session.getFactory().getStatistics().isStatisticsEnabled();
		long startTime = 0;
		if (stats) startTime = System.currentTimeMillis();
		
		PreparedStatement st = prepareQueryStatement(queryParameters, true, session);
		ResultSet rs = getResultSet( st, queryParameters.getRowSelection(), session );
		
		if (stats) {
			session.getFactory().getStatisticsImplementor().queryExecuted( 
				getQueryIdentifier(), 
				0, 
				System.currentTimeMillis() - startTime 
			);
		}
		
		ScrollableResults result = new ScrollableResultsImpl( 
			rs, 
			st, 
			session, 
			this, 
			queryParameters, 
			returnTypes,
			holderClass 
		);
		
		return result;
	}
	
	protected String getQueryIdentifier() {
		return null;
	}

	private String[][] suffixedKeyColumns;
	private String[][] suffixedVersionColumns;
	private String[][][] suffixedPropertyColumns;
	private String[] suffixedDiscriminatorColumn;

	protected static final String[] NO_SUFFIX = { "" };

	/**
	 * Calculate and cache select-clause suffixes. Must be
	 * called by subclasses after instantiation.
	 */
	protected void postInstantiate() {
		Loadable[] persisters = getPersisters();
		String[] suffixes = getSuffixes();
		suffixedKeyColumns = new String[persisters.length][];
		suffixedPropertyColumns = new String[persisters.length][][];
		suffixedVersionColumns = new String[persisters.length][];
		suffixedDiscriminatorColumn = new String[persisters.length];
		for ( int i=0; i<persisters.length; i++ ) {
			suffixedKeyColumns[i] = persisters[i].getIdentifierAliases( suffixes[i] );
			suffixedPropertyColumns[i] = getSuffixedPropertyAliases( persisters[i], suffixes[i] );
			suffixedDiscriminatorColumn[i] = persisters[i].getDiscriminatorAlias( suffixes[i] );
			if ( persisters[i].isVersioned() ) {
				suffixedVersionColumns[i] = suffixedPropertyColumns[i][ persisters[i].getVersionProperty() ];
			}
		}
	}

	private static String[][] getSuffixedPropertyAliases(Loadable persister, String suffix) {
		int size = persister.getPropertyNames().length;
		String[][] suffixedPropertyAliases = new String[size][];
		for ( int j=0; j<size; j++ ) {
			suffixedPropertyAliases[j] = persister.getPropertyAliases( suffix, j );
		}
		return suffixedPropertyAliases;
	}

	/**
	 * Utility method that generate 0_, 1_ suffixes. Subclasses don't
	 * necessarily need to use this algorithm, but it is intended that
	 * they will in most cases.
	 */
	protected static String[] generateSuffixes(int length) {

		if (length==0) return NO_SUFFIX;

		String[] suffixes = new String[length];
		for ( int i=0; i<length; i++ ) {
			suffixes[i] = /*StringHelper.UNDERSCORE +*/ Integer.toString(i) + '_';
		}
		return suffixes;
	}
			
	public String toString() {
		return getClass().getName() + '(' + getSQLString() + ')';
	}

}
