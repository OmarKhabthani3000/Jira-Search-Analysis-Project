//$Id: SessionImpl.java,v 1.52 2004/08/21 08:39:47 oneovthafew Exp $
package org.hibernate.impl;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.transaction.SystemException;
import javax.transaction.TransactionManager;

import org.apache.commons.collections.SequencedHashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.AssertionFailure;
import org.hibernate.Criteria;
import org.hibernate.Filter;
import org.hibernate.FlushMode;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Interceptor;
import org.hibernate.JDBCException;
import org.hibernate.LockMode;
import org.hibernate.MappingException;
import org.hibernate.NonUniqueObjectException;
import org.hibernate.ObjectDeletedException;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.PersistentObjectException;
import org.hibernate.Query;
import org.hibernate.QueryException;
import org.hibernate.ReplicationMode;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.TransactionException;
import org.hibernate.TransientObjectException;
import org.hibernate.UnresolvableObjectException;
import org.hibernate.action.CollectionRemoveAction;
import org.hibernate.action.Executable;
import org.hibernate.cache.CacheException;
import org.hibernate.collection.ArrayHolder;
import org.hibernate.collection.CollectionPersister;
import org.hibernate.collection.PersistentCollection;
import org.hibernate.collection.QueryableCollection;
import org.hibernate.engine.CacheSynchronization;
import org.hibernate.engine.CollectionKey;
import org.hibernate.engine.CollectionSnapshot;
import org.hibernate.engine.EntityKey;
import org.hibernate.engine.EntityUniqueKey;
import org.hibernate.engine.FilterDefinition;
import org.hibernate.engine.NamedQueryDefinition;
import org.hibernate.engine.NamedSQLQueryDefinition;
import org.hibernate.engine.QueryParameters;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.event.AutoFlushEvent;
import org.hibernate.event.CopyEvent;
import org.hibernate.event.DeleteEvent;
import org.hibernate.event.DirtyCheckEvent;
import org.hibernate.event.EvictEvent;
import org.hibernate.event.FlushEvent;
import org.hibernate.event.InitializeCollectionEvent;
import org.hibernate.event.LoadEvent;
import org.hibernate.event.LoadEventListener;
import org.hibernate.event.LockEvent;
import org.hibernate.event.RefreshEvent;
import org.hibernate.event.ReplicateEvent;
import org.hibernate.event.SaveEvent;
import org.hibernate.event.SessionEventListenerConfig;
import org.hibernate.event.SessionEventSource;
import org.hibernate.event.UpdateEvent;
import org.hibernate.hql.FilterTranslator;
import org.hibernate.hql.QuerySplitter;
import org.hibernate.hql.QueryTranslator;
import org.hibernate.jdbc.Batcher;
import org.hibernate.loader.CriteriaLoader;
import org.hibernate.loader.SQLLoader;
import org.hibernate.persister.EntityPersister;
import org.hibernate.persister.OuterJoinLoadable;
import org.hibernate.persister.SQLLoadable;
import org.hibernate.pretty.MessageHelper;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.HibernateProxyHelper;
import org.hibernate.proxy.LazyInitializer;
import org.hibernate.type.PersistentCollectionType;
import org.hibernate.type.Type;
import org.hibernate.util.ArrayHelper;
import org.hibernate.util.EmptyIterator;
import org.hibernate.util.IdentityMap;
import org.hibernate.util.JoinedIterator;
import org.hibernate.util.MarkerObject;
import org.hibernate.util.StringHelper;


/**
 * Concrete implementation of a Session, and also the central, organizing component
 * of Hibernate's internal implementation. As such, this class exposes two interfaces;
 * Session itself, to the application, and SessionImplementor, to other components
 * of Hibernate.
 *
 * This is where all the hard work goes on.
 *
 * NOT THREADSAFE
 *
 * @author Gavin King
 */
public final class SessionImpl implements SessionEventSource {

	private static final Log log = LogFactory.getLog(SessionImpl.class);

	SessionFactoryImpl factory;

	private final boolean autoClose;
	private final long timestamp;
	private boolean isCurrentTransaction; //a bit dodgy...
	private boolean closed = false;
	private FlushMode flushMode = FlushMode.AUTO;

	private final Map entitiesByKey;  //key=Key, value=Object
	private final Map entitiesByUniqueKey;  //key=Key, value=Object
	private final Map proxiesByKey;  //key=Key, value=HibernateProxy
	private transient Map entityEntries;  //key=Object, value=Entry
	private transient Map arrayHolders; //key=array, value=ArrayHolder
	private transient Map collectionEntries; //key=PersistentCollection, value=CollectionEntry
	private final Map collectionsByKey; //key=CollectionKey, value=PersistentCollection

	private HashSet nullifiables = new HashSet(); //set of Keys of deleted objects

	private final HashSet nonExists;
	private final HashSet uniqueKeyNonExists;

	private Interceptor interceptor;

	private transient Connection connection;
	private transient boolean connect;

	// We keep scheduled insertions, deletions and updates in collections
	// and actually execute them as part of the flush() process. Actually,
	// not every flush() ends in execution of the scheduled actions. Auto-
	// flushes initiated by a query execution might be "shortcircuited".

	// Object insertions and deletions have list semantics because they
	// must happen in the right order so as to respect referential integrity
	private ArrayList insertions;
	private ArrayList deletions;
	// updates are kept in a Map because successive flushes might need to add
	// extra, new changes for an object that is already scheduled for update.
	// Note: we *could* treat updates the same way we treat collection actions
	// (discarding them at the end of a "shortcircuited" auto-flush) and then
	// we would keep them in a list
	private ArrayList updates;
	// Actually the semantics of the next three are really "Bag"
	// Note that, unlike objects, collection insertions, updates,
	// deletions are not really remembered between flushes. We
	// just re-use the same Lists for convenience.
	private ArrayList collectionCreations;
	private ArrayList collectionUpdates;
	private ArrayList collectionRemovals;

	private transient ArrayList executions;

	// The collections we are currently loading
	private transient Map loadingCollections;
	private transient List nonlazyCollections;
	// A set of entity keys that we predict might be needed for
	// loading soon
	private transient Map batchLoadableEntityKeys; //actually, a Set
	public static final Object MARKER = new MarkerObject("MARKER");

	private transient int dontFlushFromFind = 0;
	//private transient boolean reentrantCallback = false;
	private transient int cascading = 0;
	private transient int loadCounter = 0;
	private transient boolean flushing = false;

	transient Batcher batcher;

	private SessionEventListenerConfig listeners;

	private Map enabledFilters = new HashMap();


	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {

		log.trace("deserializing session");

		interceptor = (Interceptor) ois.readObject();
		factory = (SessionFactoryImpl) ois.readObject();
		ois.defaultReadObject();

		entityEntries = IdentityMap.deserialize( ois.readObject() );
		collectionEntries = IdentityMap.deserialize( ois.readObject() );
		arrayHolders = IdentityMap.deserialize( ois.readObject() );
		initTransientState();

		// we need to reconnect all proxies and collections to this session
		// the association is transient because serialization is used for
		// different things.

		Iterator iter = collectionEntries.entrySet().iterator();
		while ( iter.hasNext() ) {
			try {
				Map.Entry e = (Map.Entry) iter.next();
				( (PersistentCollection) e.getKey() ).setCurrentSession(this);
				CollectionEntry ce = (CollectionEntry) e.getValue();
				if ( ce.getRole() != null ) {
					ce.setLoadedPersister( factory.getCollectionPersister( ce.getRole() ) );
				}
			}
			catch (HibernateException he) {
				throw new InvalidObjectException( he.getMessage() );
			}
		}
		iter = proxiesByKey.values().iterator();
		while ( iter.hasNext() ) {
			Object proxy = iter.next();
			if ( proxy instanceof HibernateProxy ) {
				HibernateProxyHelper.getLazyInitializer( (HibernateProxy) proxy ).setSession(this);
			}
			else {
				iter.remove(); //the proxy was pruned during the serialization process
			}
		}

		iter = entityEntries.entrySet().iterator();
		while ( iter.hasNext() ) {
			EntityEntry e = (EntityEntry) ( (Map.Entry) iter.next() ).getValue();
			try {
				e.setPersister( getEntityPersister( e.getEntityName() ) );
			}
			catch (MappingException me) {
				throw new InvalidObjectException( me.getMessage() );
			}
		}
	}

	private void writeObject(ObjectOutputStream oos) throws IOException {
		if ( isConnected() ) throw new IllegalStateException( "Cannot serialize a Session while connected" );

		log.trace( "serializing session" );

		oos.writeObject(interceptor);
		oos.writeObject(factory);
		oos.defaultWriteObject();

		oos.writeObject( IdentityMap.serialize( entityEntries ) );
		oos.writeObject( IdentityMap.serialize( collectionEntries ) );
		oos.writeObject( IdentityMap.serialize( arrayHolders ) );
	}


	public void clear() {

		arrayHolders.clear();
		entitiesByKey.clear();
		entitiesByUniqueKey.clear();
		entityEntries.clear();
		collectionsByKey.clear();
		collectionEntries.clear();
		proxiesByKey.clear();
		batchLoadableEntityKeys.clear();
		nonExists.clear();
		uniqueKeyNonExists.clear();

		updates.clear();
		insertions.clear();
		deletions.clear();
		collectionCreations.clear();
		collectionRemovals.clear();
		collectionUpdates.clear();
	}

	SessionImpl(
		final Connection connection,
		final SessionFactoryImpl factory,
		final boolean autoclose,
		final long timestamp,
		final Interceptor interceptor,
		final SessionEventListenerConfig listeners
	) {

		this.connection = connection;
		connect = connection == null;
		this.interceptor = interceptor;
        this.listeners = listeners;

		this.autoClose = autoclose;
		this.timestamp = timestamp;

		this.factory = factory;

		entitiesByKey = new HashMap(50);
		entitiesByUniqueKey = new HashMap(10);
		proxiesByKey = new HashMap(10);
		nonExists = new HashSet(10);
		uniqueKeyNonExists = new HashSet(10);
		entityEntries = IdentityMap.instantiateSequenced(50);
		collectionEntries = IdentityMap.instantiateSequenced(30);
		collectionsByKey = new HashMap(30);
		arrayHolders = IdentityMap.instantiate(10);

		insertions = new ArrayList(20);
		deletions = new ArrayList(20);
		updates = new ArrayList(20);
		collectionCreations = new ArrayList(20);
		collectionRemovals = new ArrayList(20);
		collectionUpdates = new ArrayList(20);

		initTransientState();

		log.debug( "opened session" );

	}

	public Batcher getBatcher() {
		return batcher;
	}

	public SessionFactoryImplementor getFactory() {
		return factory;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public Connection close() throws HibernateException {

		log.trace( "closing session" );

		if ( factory.getStatistics().isStatisticsEnabled() ) 
			factory.getStatisticsImplementor().closeSession();

		try {
			return connection==null ? null : disconnect();
		}
		finally {
			cleanup();
		}
	}

	public void afterTransactionCompletion(boolean success) {

		log.trace( "transaction completion" );

		isCurrentTransaction = false;

		// Downgrade locks
		Iterator iter = entityEntries.values().iterator();
		while ( iter.hasNext() ) {
			( (EntityEntry) iter.next() ).setLockMode(LockMode.NONE);
		}

		// Release cache softlocks
		int size = executions.size();
		final boolean invalidateQueryCache = factory.isQueryCacheEnabled();
		for ( int i = 0; i < size; i++ ) {
			try {
				Executable exec = (Executable) executions.get(i);
				try {
					exec.afterTransactionCompletion(success);
				}
				finally {
					if (invalidateQueryCache) factory.getUpdateTimestampsCache().invalidate( exec.getPropertySpaces() );
				}
			}
			catch (CacheException ce) {
				log.error( "could not release a cache lock", ce );
				// continue loop
			}
			catch (Exception e) {
				throw new AssertionFailure( "Exception releasing cache locks", e );
			}
		}
		executions.clear();

	}

	private void initTransientState() {
		executions = new ArrayList(50);
		batchLoadableEntityKeys = new SequencedHashMap(30);
		loadingCollections = new HashMap();
		nonlazyCollections = new ArrayList(20);

		batcher = factory.getBatcherFactory().createBatcher(this);
	}

	private void cleanup() {
		closed = true;
		entitiesByKey.clear();
		entitiesByUniqueKey.clear();
		proxiesByKey.clear();
		entityEntries.clear();
		arrayHolders.clear();
		collectionEntries.clear();
		nullifiables.clear();
		batchLoadableEntityKeys.clear();
		collectionsByKey.clear();
		nonExists.clear();
		uniqueKeyNonExists.clear();
	}

	public LockMode getCurrentLockMode(Object object) throws HibernateException {
		if ( object == null ) throw new NullPointerException( "null object passed to getCurrentLockMode()" );
		if ( object instanceof HibernateProxy ) {
			object = HibernateProxyHelper.getLazyInitializer( (HibernateProxy) object ).getImplementation(this);
			if ( object == null ) return LockMode.NONE;
		}
		EntityEntry e = getEntry(object);
		if ( e == null ) throw new TransientObjectException( "Given object not associated with the session" );
		if ( e.getStatus() != Status.MANAGED ) throw new ObjectDeletedException( 
				"The given object was deleted", 
				e.getId(), e.getPersister().getEntityName() 
		);
		return e.getLockMode();
	}

	public LockMode getLockMode(Object object) {
		return getEntry(object).getLockMode();
	}

	public void addEntity(EntityKey key, Object object) {
		entitiesByKey.put(key, object);
		batchLoadableEntityKeys.remove(key);
	}

	public Object getEntity(EntityKey key) {
		return entitiesByKey.get(key);
	}

	public boolean containsEntity(EntityKey key) {
		return entitiesByKey.containsKey(key);
	}

	public Object removeEntity(EntityKey key) {
		return entitiesByKey.remove(key);
	}
	
	public Object getEntity(EntityUniqueKey euk) {
		return entitiesByUniqueKey.get(euk);
	}
	
	public void addEntity(EntityUniqueKey euk, Object entity) {
		entitiesByUniqueKey.put(euk, entity);
	}

	public Object getEntityUsingInterceptor(EntityKey key) throws HibernateException {
		final Object result = getEntity( key );
		if ( result == null ) {
			final Object newObject = interceptor.getEntity( key.getEntityName(), key.getIdentifier() );
			if ( newObject != null ) lock(newObject, LockMode.NONE);
			return newObject;
		}
		else {
			return result;
		}
	}

	public void setLockMode(Object entity, LockMode lockMode) {
		getEntry(entity).setLockMode(lockMode);
	}

	public EntityEntry addEntity(
		final Object object,
		final Status status,
		final Object[] loadedState,
		final Serializable id,
		final Object version,
		final LockMode lockMode,
		final boolean existsInDatabase,
		final EntityPersister persister,
		final boolean disableVersionIncrement
	) {
		addEntity( new EntityKey(id, persister), object );
		return addEntry(object, status, loadedState, null, id, version, lockMode, existsInDatabase, persister, disableVersionIncrement);
	}

	public EntityEntry addEntry(
			final Object object,
			final Status status,
			final Object[] loadedState,
			final Object rowId,
			final Serializable id,
			final Object version,
			final LockMode lockMode,
			final boolean existsInDatabase,
			final EntityPersister persister, 
			final boolean disableVersionIncrement
	) {
		EntityEntry e = new EntityEntry(status, loadedState, rowId, id, version, lockMode, existsInDatabase, persister, disableVersionIncrement);
		entityEntries.put(object, e);
		return e;
	}

	public EntityEntry getEntry(Object object) {
		return (EntityEntry) entityEntries.get( object );
	}

	public EntityEntry removeEntry(Object object) {
		return (EntityEntry) entityEntries.remove( object );
	}

	public boolean isEntryFor(Object object) {
		return entityEntries.containsKey( object );
	}

	public CollectionEntry getCollectionEntry(PersistentCollection coll) {
		return (CollectionEntry) collectionEntries.get( coll );
	}

	public boolean isOpen() {
		return !closed;
	}

	/**
	 * Save a transient object.
	 * An id is generated, assigned to the given object and returned.
	 */
	public Serializable save(Object obj) throws HibernateException {
		SaveEvent event = new SaveEvent(obj, this);
		return listeners.getSaveEventListener().onSave(event);
	}

	public void forceFlush(EntityEntry e) throws HibernateException {
		if ( log.isDebugEnabled() ) {
			log.debug(
				"flushing to force deletion of re-saved object: " +
				MessageHelper.infoString( e.getPersister(), e.getId() )
			);
		}
		if (cascading>0) {
			throw new ObjectDeletedException(
				"deleted object would be re-saved by cascade (remove deleted object from associations)",
				e.getId(),
				e.getPersister().getEntityName()
			);
		}
		flush();
	}

	/**
	 * Save a transient object with a manually assigned ID.
	 */
	public void save(Object obj, Serializable id) throws HibernateException {
		SaveEvent event = new SaveEvent(obj, id, this);
		listeners.getSaveEventListener().onSave(event);
	}

	public boolean reassociateIfUninitializedProxy(Object value) throws MappingException {
		if ( !Hibernate.isInitialized(value) ) {
			HibernateProxy proxy = (HibernateProxy) value;
			LazyInitializer li = HibernateProxyHelper.getLazyInitializer( proxy );
			reassociateProxy(li, proxy);
			return true;
		}
		else {
			return false;
		}
	}

	public void reassociateProxy(Object value, Serializable id) throws MappingException {
		if ( value instanceof HibernateProxy ) {
			if ( log.isDebugEnabled() ) log.debug("generated identifier: " + id);
			HibernateProxy proxy = (HibernateProxy) value;
			LazyInitializer li = HibernateProxyHelper.getLazyInitializer(proxy);
			li.setIdentifier(id);
			reassociateProxy(li, proxy);
		}
	}

	public Object unproxy(Object maybeProxy) throws HibernateException {
		if ( maybeProxy instanceof HibernateProxy ) {
			HibernateProxy proxy = (HibernateProxy) maybeProxy;
			LazyInitializer li = HibernateProxyHelper.getLazyInitializer(proxy);
			if ( li.isUninitialized() ) {
				throw new PersistentObjectException(
						"object was an uninitialized proxy for: " +
						li.getEntityName()
				);
			}
			return li.getImplementation(); //unwrap the object
		}
		else {
			return maybeProxy;
		}
	}

	public Object unproxyAndReassociate(Object maybeProxy) throws HibernateException {
		if ( maybeProxy instanceof HibernateProxy ) {
			HibernateProxy proxy = (HibernateProxy) maybeProxy;
			LazyInitializer li = HibernateProxyHelper.getLazyInitializer(proxy);
			reassociateProxy(li, proxy);
			return li.getImplementation(); //initialize + unwrap the object
		}
		else {
			return maybeProxy;
		}
	}

	/**
	 * associate a proxy that was instantiated by another session with this session
	 */
	void reassociateProxy(LazyInitializer li, HibernateProxy proxy) throws MappingException {
		if ( li.getSession() != this ) {
			EntityPersister persister = getEntityPersister( li.getEntityName() );
			EntityKey key = new EntityKey( li.getIdentifier(), persister );
			if ( !proxiesByKey.containsKey(key) ) proxiesByKey.put(key, proxy); // any earlier proxy takes precedence
			HibernateProxyHelper.getLazyInitializer(proxy).setSession(this);
		}
	}

	/**
	 * Delete a persistent object
	 */
	public void delete(Object object) throws HibernateException {
		DeleteEvent event = new DeleteEvent(object, this);
        listeners.getDeleteEventListener().onDelete(event);
	}

	/**
	 * Delete a persistent object
	 */
	public void delete(String entityName, Object object, boolean isCascadeDeleteEnabled) throws HibernateException {
		DeleteEvent event = new DeleteEvent(entityName, object, isCascadeDeleteEnabled, this);
        listeners.getDeleteEventListener().onDelete(event);
	}

	public Filter enableFilter(String filterName) {
        FilterImpl filter = new FilterImpl( factory.getFilterDefinition(filterName) );
		enabledFilters.put(filterName, filter);
		return filter;
	}

	public Filter getEnabledFilter(String filterName) {
		return (Filter) enabledFilters.get(filterName);
	}

	public void disableFilter(String filterName) {
		enabledFilters.remove(filterName);
	}

	public Object getFilterParameterValue(String filterParameterName) {
        String[] parsed = parseFilterParameterName(filterParameterName);
		FilterImpl filter = (FilterImpl) enabledFilters.get( parsed[0] );
		if (filter == null) {
			throw new IllegalArgumentException("Filter [" + parsed[0] + "] currently not enabled");
		}
		return filter.getParameter( parsed[1] );
	}

	public Type getFilterParameterType(String filterParameterName) {
		String[] parsed = parseFilterParameterName(filterParameterName);
		FilterDefinition filterDef = factory.getFilterDefinition( parsed[0] );
		if (filterDef == null) {
			throw new IllegalArgumentException("Filter [" + parsed[0] + "] not defined");
		}
		Type type = filterDef.getParameterType( parsed[1] );
		if (type == null) {
			// this is an internal error of some sort...
			throw new InternalError("Unable to locate type for filter parameter");
		}
		return type;
	}

	public Map getEnabledFilters() {
		// First, validate all the enabled filters...
		Iterator itr = enabledFilters.values().iterator();
		while ( itr.hasNext() ) {
			final Filter filter = (Filter) itr.next();
			filter.validate();
		}
		return enabledFilters;
	}

	private String[] parseFilterParameterName(String filterParameterName) {
		int dot = filterParameterName.indexOf('.');
		if (dot <= 0) {
			throw new IllegalArgumentException("Invalid filter-parametter name format"); // TODO: what type?
		}
		String filterName = filterParameterName.substring(0, dot);
		String parameterName = filterParameterName.substring(dot+1);
		return new String[] {filterName, parameterName};
	}

	public void removeCollection(CollectionPersister role, Serializable id) throws HibernateException {
		if ( log.isTraceEnabled() )
			log.trace(
					"collection dereferenced while transient " +
					MessageHelper.infoString( role, id )
			);
		/*if ( role.hasOrphanDelete() ) {
			throw new HibernateException(
				"You may not dereference a collection with cascade=\"all-delete-orphan\": " +
				MessageHelper.infoString(role, id)
			);
		}*/
		collectionRemovals.add( new CollectionRemoveAction( role, id, false, this ) );
	}

	public void update(Object obj) throws HibernateException {
		UpdateEvent event = new UpdateEvent(obj, this);
		listeners.getUpdateEventListener().onUpdate(event);
	}

	public void saveOrUpdate(String entityName, Object obj) throws HibernateException {
		if ( obj == null ) {
			throw new NullPointerException( "attempted to save/update null" );
		}

		if ( reassociateIfUninitializedProxy(obj) ) {
			return;
		}

		Object object = unproxyAndReassociate(obj); //a proxy is always "update", never "save"

		EntityEntry e = getEntry(object);
		if ( e != null && e.getStatus() != Status.DELETED ) {
			// do nothing for persistent instances
			log.trace( "saveOrUpdate() persistent instance" );
		}
		else if ( e != null ) { //ie. e.status==DELETED
			log.trace( "saveOrUpdate() deleted instance" );
			save(obj);
		}
		else {
			// the object is transient
			final Boolean isUnsaved = interceptor.isUnsaved(object);
			final boolean doSave;
			if ( isUnsaved == null ) {
				// use unsaved-value
				doSave = getEntityPersister(entityName, object).isUnsaved(object);
			}
			else {
				doSave = isUnsaved.booleanValue();
			}
			
			final boolean isEntityNameKnown = entityName!=null;
			if (doSave) {
				log.trace( "saveOrUpdate() unsaved instance" );
				if (isEntityNameKnown) {
					save(entityName, obj); 
				}
				else {
					save(obj);
				}
			}
			else {
				log.trace( "saveOrUpdate() previously saved instance" );
				if (isEntityNameKnown) {
					update(entityName, obj);
				}
				else {
					update(obj); 
				}
			}

		}

	}

	public void update(Object obj, Serializable id) throws HibernateException {
		UpdateEvent event = new UpdateEvent(obj, id, this);
		listeners.getUpdateEventListener().onUpdate(event);
	}

	/**
	 * Retrieve a list of persistent objects using a hibernate query
	 */
	public List find(String query) throws HibernateException {
		return find( query, new QueryParameters() );
	}

	public List find(String query, Object value, Type type) throws HibernateException {
		return find( query, new QueryParameters(type, value) );
	}

	public List find(String query, Object[] values, Type[] types) throws HibernateException {
		return find( query, new QueryParameters(types, values) );
	}

	public List find(String query, QueryParameters queryParameters) throws HibernateException {

		if ( log.isTraceEnabled() ) {
			log.trace( "find: " + query );
			queryParameters.traceParameters(factory);
		}

		queryParameters.validateParameters();
		QueryTranslator[] q = getQueries(query, false);

		List results = Collections.EMPTY_LIST;

		dontFlushFromFind++;   //stops flush being called multiple times if this method is recursively called

		//execute the queries and return all result lists as a single list
		try {
			for ( int i = 0; i < q.length; i++ ) {
				List currentResults;
				try {
					currentResults = q[i].list(this, queryParameters);
				}
				catch (SQLException sqle) {
					throw new JDBCException( "Could not execute query", sqle );
				}
				currentResults.addAll(results);
				results = currentResults;
			}
		}
		finally {
			dontFlushFromFind--;
		}
		return results;
	}

	private QueryTranslator[] getQueries(String query, boolean scalar) throws HibernateException {

		// take the union of the query spaces (ie. the queried tables)
		QueryTranslator[] q = factory.getQuery( query, scalar, getEnabledFilters() );
		HashSet qs = new HashSet();
		for ( int i = 0; i < q.length; i++ ) {
			qs.addAll( q[i].getQuerySpaces() );
		}

		autoFlushIfRequired(qs);

		return q;
	}

	public Iterator iterate(String query) throws HibernateException {
		return iterate( query, new QueryParameters() );
	}

	public Iterator iterate(String query, Object value, Type type) throws HibernateException {
		return iterate( query, new QueryParameters(type, value) );
	}

	public Iterator iterate(String query, Object[] values, Type[] types) throws HibernateException {
		return iterate( query, new QueryParameters(types, values) );
	}

	public Iterator iterate(String query, QueryParameters queryParameters) throws HibernateException {

		if ( log.isTraceEnabled() ) {
			log.trace( "iterate: " + query );
			queryParameters.traceParameters( factory );
		}

		queryParameters.validateParameters();
		QueryTranslator[] q = getQueries( query, true );

		if ( q.length == 0 ) return EmptyIterator.INSTANCE;

		Iterator result = null;
		Iterator[] results = null;
		boolean many = q.length > 1;
		if ( many ) results = new Iterator[q.length];

		dontFlushFromFind++; //stops flush being called multiple times if this method is recursively called

		try {

			//execute the queries and return all results as a single iterator
			for ( int i = 0; i < q.length; i++ ) {

				try {
					result = q[i].iterate( queryParameters, this );
				}
				catch (SQLException sqle) {
					throw new JDBCException( "Could not execute query", sqle );
				}
				if ( many ) {
					results[i] = result;
				}

			}

			return many ? new JoinedIterator( results ) : result;

		}
		finally {
			dontFlushFromFind--;
		}
	}

	public ScrollableResults scroll(String query, QueryParameters queryParameters) throws HibernateException {

		if ( log.isTraceEnabled() ) {
			log.trace( "scroll: " + query );
			queryParameters.traceParameters( factory );
		}

		QueryTranslator[] q = factory.getQuery( query, false, getEnabledFilters() );
		if ( q.length != 1 ) throw new QueryException( "implicit polymorphism not supported for scroll() queries" );
		autoFlushIfRequired( q[0].getQuerySpaces() );

		dontFlushFromFind++; //stops flush being called multiple times if this method is recursively called
		try {
			return q[0].scroll( queryParameters, this );
		}
		catch (SQLException sqle) {
			throw new JDBCException( "Could not execute query", sqle );
		}
		finally {
			dontFlushFromFind--;
		}
	}

	public int delete(String query) throws HibernateException {
		return delete( query, ArrayHelper.EMPTY_OBJECT_ARRAY, ArrayHelper.EMPTY_TYPE_ARRAY );
	}

	public int delete(String query, Object value, Type type) throws HibernateException {
		return delete( query, new Object[]{value}, new Type[]{type} );
	}

	public int delete(String query, Object[] values, Type[] types) throws HibernateException {
		if ( query == null ) {
			throw new IllegalArgumentException("attempt to perform delete-by-query with null query");
		}

		if ( log.isTraceEnabled() ) {
			log.trace( "delete: " + query );
			if ( values.length != 0 ) {
				log.trace( "parameters: " + StringHelper.toString( values ) );
			}
		}

		List list = find( query, values, types );
		int deletionCount = list.size();
		for ( int i = 0; i < deletionCount; i++ ) {
			delete( list.get( i ) );
		}

		return deletionCount;
	}

	public void checkUniqueness(Serializable id, EntityPersister persister, Object object) throws HibernateException {
		Object entity = getEntity( new EntityKey(id, persister) );
		if ( entity == object ) {
			throw new AssertionFailure( "object already associated, but no entry was found" );
		}
		if ( entity != null ) {
			throw new NonUniqueObjectException( id, persister.getEntityName() );
		}
	}

	public void lock(Object object, LockMode lockMode) throws HibernateException {
        listeners.getLockEventListener().onLock( new LockEvent(object, lockMode, this) );
	}

	public Query createFilter(Object collection, String queryString) {
		return new CollectionFilterImpl(queryString, collection, this);
	}
	
	public Query createQuery(String queryString) {
		return new QueryImpl(queryString, this);
	}

	private Query createQuery(String queryString, FlushMode queryFlushMode) {
		return new QueryImpl(queryString, queryFlushMode, this);
	}
	
	public Query getNamedQuery(String queryName) throws MappingException {
		NamedQueryDefinition nqd = factory.getNamedQuery(queryName);
		final Query query;
		if ( nqd != null ) {
			query = createQuery( 
					nqd.getQueryString(), 
					nqd.getFlushMode()
			);
			if ( factory.isCommentsEnabled() ) {
				query.setComment("named query " + queryName);
			}
		}
		else {
			NamedSQLQueryDefinition nsqlqd = factory.getNamedSQLQuery( queryName );
			if (nsqlqd==null) throw new MappingException("Named query not known: " + queryName);
			query = createSQLQuery(
				nsqlqd.getQueryString(),
				nsqlqd.getReturnAliases(),
				nsqlqd.getReturnClasses(),
			    nsqlqd.getCollectionAlias(),
				nsqlqd.getCollectionPath(),
				nsqlqd.getLockModes(),
				nsqlqd.getQuerySpaces(),
				nsqlqd.getFlushMode()
			);
			nqd = nsqlqd;
			if ( factory.isCommentsEnabled() ) {
				query.setComment("named SQL query " + queryName);
			}
		}
		query.setCacheable( nqd.isCacheable() );
		query.setCacheRegion( nqd.getCacheRegion() );
		if ( nqd.getTimeout()!=null ) query.setTimeout( nqd.getTimeout().intValue() );
		if ( nqd.getFetchSize()!=null ) query.setFetchSize( nqd.getFetchSize().intValue() );
		return query;
	}

	public Object instantiate(String entityName, Serializable id) throws HibernateException {
		return instantiate( getEntityPersister( entityName ), id );
	}

	/**
	 * give the interceptor an opportunity to override the default instantiation
	 */
	public Object instantiate(EntityPersister persister, Serializable id) throws HibernateException {
		Object result = interceptor.instantiate( persister.getEntityName(), id );
		if ( result == null ) result = persister.instantiate(id);
		return result;
	}

	public void setFlushMode(FlushMode flushMode) {
		if ( log.isTraceEnabled() ) log.trace("setting flush mode to: " + flushMode);
		this.flushMode = flushMode;
	}

	public FlushMode getFlushMode() {
		return flushMode;
	}

	/**
	 * detect in-memory changes, determine if the changes are to tables
	 * named in the query and, if so, complete execution the flush
	 */
	private boolean autoFlushIfRequired(Set querySpaces) throws HibernateException {
		AutoFlushEvent event = new AutoFlushEvent(querySpaces, this);
		return listeners.getAutoFlushEventListener().onAutoFlush(event);
	}

	/**
	 * If the existing proxy is insufficiently "narrow" (derived), instantiate a new proxy
	 * and overwrite the registration of the old one. This breaks == and occurs only for
	 * "class" proxies rather than "interface" proxies.
	 */
	public Object narrowProxy(Object proxy, EntityPersister persister, EntityKey key, Object object) throws HibernateException {

		if ( !persister.getConcreteProxyClass().isAssignableFrom( proxy.getClass() ) ) {

			if ( log.isWarnEnabled() )
				log.warn(
						"Narrowing proxy to " +
						persister.getConcreteProxyClass() +
						" - this operation breaks =="
				);

			if ( object != null ) {
				proxiesByKey.remove(key);
				return object; //return the proxied object
			}
			else {
				proxy = persister.createProxy( key.getIdentifier(), this );
				proxiesByKey.put(key, proxy); //overwrite old proxy
				return proxy;
			}

		}
		else {
			return proxy;
		}
	}

	/**
	 * Grab the existing proxy for an instance, if
	 * one exists. (otherwise return the instance)
	 */
	public Object proxyFor(EntityPersister persister, EntityKey key, Object impl) throws HibernateException {
		if ( !persister.hasProxy() ) return impl;
		Object proxy = proxiesByKey.get(key);
		if ( proxy != null ) {
			return narrowProxy(proxy, persister, key, impl);
		}
		else {
			return impl;
		}
	}

	public Object proxyFor(Object impl) throws HibernateException {
		EntityEntry e = getEntry(impl);
		//can't use e.persister since it is null after addUninitializedEntity (when this method is called)
		//EntityPersister p = getPersister(impl);
		EntityPersister p = e.getPersister();
		return proxyFor( p, new EntityKey( e.getId(), p ), impl );
	}

	public void load(Object object, Serializable id) throws HibernateException {
        LoadEvent event = new LoadEvent(id, object, this);
        listeners.getLoadEventListener().onLoad(event, null);
	}

	public Object load(Class entityClass, Serializable id) throws HibernateException {
		return load( entityClass.getName(), id );
	}

	public Object load(String entityName, Serializable id) throws HibernateException {
        LoadEvent event = new LoadEvent(id, entityName, false, this);
        Object result = listeners.getLoadEventListener().onLoad(event, LoadEventListener.LOAD);

		ObjectNotFoundException.throwIfNull(result, id, entityName);
		return result;
	}

	public Object get(Class entityClass, Serializable id) throws HibernateException {
		return get( entityClass.getName(), id );
	}

	public Object get(String entityName, Serializable id) throws HibernateException {
        LoadEvent event = new LoadEvent(id, entityName, false, this);
        return listeners.getLoadEventListener().onLoad(event, LoadEventListener.GET);
	}

	/**
	 * Load the data for the object with the specified id into a newly created object.
	 * Do NOT return a proxy.
	 */
	public Object immediateLoad(String entityName, Serializable id) throws HibernateException {
        LoadEvent event = new LoadEvent(id, entityName, true, this);
        Object result = listeners.getLoadEventListener().onLoad(event, LoadEventListener.IMMEDIATE_LOAD);

		ObjectNotFoundException.throwIfNull(result, id, entityName); //should it be UnresolvableObject?
		return result;
	}

	/**
	 * Return the object with the specified id or null if no row with that id exists. Do not defer the load
	 * or return a new proxy (but do return an existing proxy). Do not check if the object was deleted.
	 */
	public Object internalLoadOneToOne(String entityName, Serializable id) throws HibernateException {
        LoadEvent event = new LoadEvent(id, entityName, true, this);
        return listeners.getLoadEventListener().onLoad(event, LoadEventListener.INTERNAL_LOAD_ONE_TO_ONE);
	}

	/**
	 * Return the object with the specified id or throw exception if no row with that id exists. Defer the load,
	 * return a new proxy or return an existing proxy if possible. Do not check if the object was deleted.
	 */
	public Object internalLoad(String entityName, Serializable id) throws HibernateException {
        LoadEvent event = new LoadEvent(id, entityName, true, this);
        Object result = listeners.getLoadEventListener().onLoad(event, LoadEventListener.INTERNAL_LOAD);

		UnresolvableObjectException.throwIfNull(result, id, entityName);
		return result;
	}

	public Object load(Class entityClass, Serializable id, LockMode lockMode) throws HibernateException {
		return load( entityClass.getName(), id, lockMode );
	}

	public Object load(String entityName, Serializable id, LockMode lockMode) throws HibernateException {
        LoadEvent event = new LoadEvent(id, entityName, lockMode, this);
        return listeners.getLoadEventListener().onLoad(event, LoadEventListener.LOAD);
	}

	public Object get(Class entityClass, Serializable id, LockMode lockMode) throws HibernateException {
		return get( entityClass.getName(), id, lockMode );
	}

	public Object get(String entityName, Serializable id, LockMode lockMode) throws HibernateException {
        LoadEvent event = new LoadEvent(id, entityName, lockMode, this);
        return listeners.getLoadEventListener().onLoad(event, LoadEventListener.GET);
	}

	public void refresh(Object object) throws HibernateException {
		listeners.getRefreshEventListener().onRefresh( new RefreshEvent(object, this) );
	}

	public void refresh(Object object, LockMode lockMode) throws HibernateException {
		listeners.getRefreshEventListener().onRefresh( new RefreshEvent(object, lockMode, this) );
	}

	public Transaction beginTransaction() throws HibernateException {
		Transaction tx = factory.getTransactionFactory().beginTransaction( this );
		isCurrentTransaction = true;
		return tx;
	}

	public void flush() throws HibernateException {
		if (cascading>0) throw new HibernateException("Flush during cascade is dangerous");
		listeners.getFlushEventListener().onFlush( new FlushEvent(this) );
	}

	public boolean areTablesToBeUpdated(Set tables) {
		return areTablesToUpdated( updates, tables ) ||
				areTablesToUpdated( insertions, tables ) ||
				areTablesToUpdated( deletions, tables ) ||
				areTablesToUpdated( collectionUpdates, tables ) ||
				areTablesToUpdated( collectionCreations, tables ) ||
				areTablesToUpdated( collectionRemovals, tables );
	}

	private static boolean areTablesToUpdated(List executables, Set set) {
		int size = executables.size();
		for ( int j = 0; j < size; j++ ) {
			Serializable[] spaces = ( (Executable) executables.get(j) ).getPropertySpaces();
			for ( int i = 0; i < spaces.length; i++ ) {
				if ( set.contains( spaces[i] ) ) {
					if ( log.isDebugEnabled() ) log.debug( "changes must be flushed to space: " + spaces[i] );
					return true;
				}
			}
		}
		return false;
	}

	public void executeAll(List list) throws HibernateException {
		final boolean lockQueryCache = factory.isQueryCacheEnabled();
		int size = list.size();
		for ( int i = 0; i < size; i++ ) {
			Executable executable = (Executable) list.get( i );
			if ( executable.hasAfterTransactionCompletion() ) executions.add(executable);
			if (lockQueryCache) factory.getUpdateTimestampsCache().preinvalidate( executable.getPropertySpaces() );
			executable.execute();
		}
		list.clear();
		/*if ( batcher != null )*/ batcher.executeBatch();
	}

	public void checkId(Object object, EntityPersister persister, Serializable id)
	throws HibernateException {

		// make sure user didn't mangle the id
		if ( persister.hasIdentifierPropertyOrEmbeddedCompositeIdentifier() ) {

		Serializable oid = persister.getIdentifier(object);
		if (id==null) throw new AssertionFailure("null id in entry (don't flush the Session after an exception occurs)");
			if ( !id.equals(oid) ) {
				throw new HibernateException(
						"identifier of an instance of " +
						persister.getEntityName() +
						" altered from " + id +
						" to " + oid
				);
			}
		}

	}

	public void beforeLoad() {
		loadCounter++;
	}

	public void afterLoad() {
		loadCounter--;
	}

	private EntityPersister getEntityPersister(String entityName) throws MappingException {
		return factory.getEntityPersister(entityName);
	}
	
	public EntityPersister getEntityPersister(final String entityName, final Object object) {
		if (entityName==null) {
			return getEntityPersister( guessEntityName(object) );
		}
		else {
			return getEntityPersister(entityName).getSubclassEntityPersister( object, getFactory() );
		}
	}

	// not for internal use:
	public Serializable getIdentifier(Object object) throws HibernateException {
		if ( object instanceof HibernateProxy ) {
			LazyInitializer li = HibernateProxyHelper.getLazyInitializer( (HibernateProxy) object );
			if ( li.getSession() != this ) throw new TransientObjectException( "The proxy was not associated with this session" );
			return li.getIdentifier();
		}
		else {
			EntityEntry entry = getEntry(object);
			if ( entry == null ) throw new TransientObjectException( "The instance was not associated with this session" );
			return entry.getId();
		}
	}

	/**
	 * Get the id value for an object that is actually associated with the session. This
	 * is a bit stricter than getEntityIdentifierIfNotUnsaved().
	 */
	public Serializable getEntityIdentifier(Object object) {
		if ( object instanceof HibernateProxy ) {
			return getProxyIdentifier(object);
		}
		else {
			EntityEntry entry = getEntry(object);
			return entry != null ? entry.getId() : null;
		}
	}
	
	private Serializable getProxyIdentifier(Object proxy) {
		return HibernateProxyHelper.getLazyInitializer( (HibernateProxy) proxy ).getIdentifier();
	}

	public boolean isSaved(String entityName, Object object) throws HibernateException {
		if (object instanceof HibernateProxy) return true;
		EntityEntry entry = getEntry(object);
		if ( entry != null ) return true;
		Boolean isUnsaved = interceptor.isUnsaved(object);
		if ( isUnsaved != null ) return !isUnsaved.booleanValue();
		return !getEntityPersister(entityName, object).isUnsaved(object);
	}

	public boolean isDirty() throws HibernateException {
		log.debug("checking session dirtiness");
		if ( insertions.size() > 0 || deletions.size() > 0 ) {
			log.debug("session dirty (scheduled updates and insertions)");
			return true;
		}
		else {
			DirtyCheckEvent event = new DirtyCheckEvent(this);
			return listeners.getDirtyCheckEventListener().onDirtyCheck(event);
		}
	}

	private static final class LoadingCollectionEntry {

		final PersistentCollection collection;
		final Serializable id;
		final Object resultSetId;

		LoadingCollectionEntry(PersistentCollection collection, Serializable id, Object resultSetId) {
			this.collection = collection;
			this.id = id;
			this.resultSetId = resultSetId;
		}
	}

	public Object getCollectionOwner(Serializable key, CollectionPersister collectionPersister) throws MappingException {
		//TODO:give collection persister a reference to the owning class persister
		return getEntity( new EntityKey(key, factory.getEntityPersister( collectionPersister.getOwnerEntityName() ) ) );
	}

	private LoadingCollectionEntry getLoadingCollectionEntry(CollectionKey collectionKey) {
		return (LoadingCollectionEntry) loadingCollections.get(collectionKey);
	}

	private void addLoadingCollectionEntry(CollectionKey collectionKey, PersistentCollection collection, Serializable id, Object resultSetId) {
		loadingCollections.put( collectionKey, new LoadingCollectionEntry(collection, id, resultSetId) );
	}

	public PersistentCollection getLoadingCollection(CollectionPersister persister, Serializable id, Object resultSetId) 
	throws HibernateException {

		CollectionKey ckey = new CollectionKey(persister, id);
		LoadingCollectionEntry lce = getLoadingCollectionEntry(ckey);
		if ( lce == null ) {
			//look for existing collection
			PersistentCollection pc = getCollection(ckey);
			if ( pc != null ) {
				CollectionEntry ce = getCollectionEntry(pc);
				if ( ce.isInitialized() ) {
					log.trace( "collection already initialized: ignoring" );
					return null; //ignore this row of results! Note the early exit
				}
				else {
					//initialize this collection
					log.trace( "uninitialized collection: initializing" );
				}
			}
			else {
				Object entity = getCollectionOwner(id, persister);				
				if ( entity != null && getEntry(entity).getStatus() != Status.LOADING ) {
					//important, to account for newly saved entities in query
					log.trace( "owning entity already loaded: ignoring" );
					return null;
				}
				else {
					//create one
					log.trace( "new collection: instantiating" );
					pc = persister.getCollectionType().instantiate(this, persister);
				}
			}
			pc.beforeInitialize(persister);
			pc.beginRead();
			addLoadingCollectionEntry(ckey, pc, id, resultSetId);
			return pc;
		}
		else {
			if ( lce.resultSetId == resultSetId ) {
				log.trace( "reading row" );
				return lce.collection;
			}
			else {
				//ignore this row, the collection is in process of being loaded somewhere further "up" the stack
				log.trace( "collection is already being initialized: ignoring row" );
				return null;
			}
		}
	}

	public void endLoadingCollections(CollectionPersister persister, Object resultSetId) throws HibernateException {

		// scan the loading collections for collections from this result set
		// put them in a new temp collection so that we are safe from concurrent
		// modification when the call to endRead() causes a proxy to be
		// initialized
		List resultSetCollections = null; //TODO: make this the resultSetId?
		Iterator iter = loadingCollections.values().iterator();
		while ( iter.hasNext() ) {
			LoadingCollectionEntry lce = ( LoadingCollectionEntry ) iter.next();
			if ( lce.resultSetId == resultSetId ) {
				if ( resultSetCollections == null ) resultSetCollections = new ArrayList();
				resultSetCollections.add(lce);
				iter.remove();
			}
		}

		endLoadingCollections(persister, resultSetCollections);
	}

	private void endLoadingCollections(CollectionPersister persister, List resultSetCollections)
	throws HibernateException {

		final int count = (resultSetCollections == null) ? 0 : resultSetCollections.size();

		if ( log.isDebugEnabled() ) log.debug( count + " collections were found in result set" );

		//now finish them
		for ( int i = 0; i < count; i++ ) {
			LoadingCollectionEntry lce = (LoadingCollectionEntry) resultSetCollections.get(i);
			boolean addToCache = lce.collection.endRead(); //warning: can cause a recursive query! (proxy initialization)
			CollectionEntry ce = getCollectionEntry(lce.collection);
			if ( ce == null ) {
				addInitializedCollection(lce.collection, persister, lce.id);
			}
			else {
				ce.postInitialize(lce.collection);
			}
			if ( persister.hasCache() && addToCache ) {
				if ( log.isDebugEnabled() )
					log.debug(
							"Caching collection: " +
							MessageHelper.infoString(persister, lce.id)
					);
				persister.getCache().put( lce.id, lce.collection.disassemble(persister), getTimestamp() );
				
				if ( getFactory().getStatistics().isStatisticsEnabled() ) {
					getFactory().getStatisticsImplementor().secondLevelCachePut( persister.getCache().getRegionName() );
				}
			}

			if ( log.isDebugEnabled() )
				log.debug(
						"collection fully initialized: " +
						MessageHelper.infoString(persister, lce.id)
				);

			
			if ( getFactory().getStatistics().isStatisticsEnabled() ) {
				getFactory().getStatisticsImplementor().loadCollection( persister.getRole() );
			}
		
		}

		if ( log.isDebugEnabled() ) log.debug( count + " collections initialized" );
	}

	public PersistentCollection getLoadingCollection(CollectionPersister persister, Serializable id) {
		LoadingCollectionEntry lce = getLoadingCollectionEntry( new CollectionKey(persister, id) );
		return (lce != null) ? lce.collection : null;
	}
	
	public void addNonLazyCollection(PersistentCollection collection) {
		nonlazyCollections.add(collection);
	}

	public void initializeNonLazyCollections() throws HibernateException {
		if ( loadCounter == 0 ) {
			log.debug( "initializing non-lazy collections" );
			//do this work only at the very highest level of the load
			loadCounter++; //don't let this method be called recursively
			try {
				int size;
				while ( ( size = nonlazyCollections.size() ) > 0 ) {
					//note that each iteration of the loop may add new elements
					( (PersistentCollection) nonlazyCollections.remove( size - 1 ) ).forceInitialization();
				}
			}
			finally {
				loadCounter--;
			}
		}
	}

	private void addCollection(PersistentCollection coll, CollectionEntry entry, Serializable key) {
		collectionEntries.put(coll, entry);
		PersistentCollection old = (PersistentCollection) collectionsByKey.put(
				new CollectionKey( entry.getLoadedPersister(), key ),
				coll
		);
		if ( old != null ) {
			if (old==coll) throw new AssertionFailure("bug adding collection twice");
			// or should it actually throw an exception?
			old.unsetSession(this);
			collectionEntries.remove(old);
			// watch out for a case where old is still referenced
			// somewhere in the object graph! (which is a user error)
		}
	}

	private PersistentCollection getCollection(CollectionKey collectionKey) {
		return (PersistentCollection) collectionsByKey.get(collectionKey);
	}

	/**
	 * add a collection we just loaded up (still needs initializing)
	 */
	public void addUninitializedCollection(PersistentCollection collection, CollectionPersister persister, Serializable id) {
		CollectionEntry ce = new CollectionEntry(persister, id, flushing);
		collection.setCollectionSnapshot(ce);
		addCollection(collection, ce, id);
	}

	/**
	 * add a detached uninitialized collection
	 */
	public void addUninitializedDetachedCollection(PersistentCollection collection, CollectionPersister persister, Serializable id) {
		CollectionEntry ce = new CollectionEntry(persister, id);
		collection.setCollectionSnapshot(ce);
		addCollection(collection, ce, id);
	}

	/**
	 * add a collection we just pulled out of the cache (does not need initializing)
	 */
	private void addInitializedCollection(PersistentCollection collection, CollectionPersister persister, Serializable id)
	throws HibernateException {
		CollectionEntry ce = new CollectionEntry(persister, id, flushing);
		ce.postInitialize(collection);
		collection.setCollectionSnapshot(ce);
		addCollection(collection, ce, id);
	}

	private CollectionEntry addCollection(PersistentCollection collection)
	throws HibernateException {
		CollectionEntry ce = new CollectionEntry();
		collectionEntries.put(collection, ce);
		collection.setCollectionSnapshot(ce);
		return ce;
	}

  	/**
  	 * add a new collection (ie. a newly created one, just instantiated by the
  	 * application, with no database state or snapshot)
  	 */
	public void addNewCollection(PersistentCollection collection, CollectionPersister persister)
	throws HibernateException {
		CollectionEntry ce = addCollection(collection);
		if ( persister.hasOrphanDelete() ) ce.initSnapshot(collection, persister);
	}

	/**
	 * add an (initialized) collection that was created by another session and passed
	 * into update() (ie. one with a snapshot and existing state on the database)
	 */
	public void addInitializedDetachedCollection(PersistentCollection collection, CollectionSnapshot cs)
			throws HibernateException {
		if ( cs.wasDereferenced() ) {
			addCollection(collection);
		}
		else {
			CollectionEntry ce = new CollectionEntry(cs, factory);
			collection.setCollectionSnapshot(ce);
			addCollection( collection, ce, cs.getKey() );
		}
	}

	public ArrayHolder getArrayHolder(Object array) {
		return (ArrayHolder) arrayHolders.get(array);
	}

	/**
	 * associate a holder with an array - called after loading array
	 */
	public void addArrayHolder(ArrayHolder holder) {
		//TODO:refactor + make this method private
		arrayHolders.put( holder.getArray(), holder );
	}

	public CollectionPersister getCollectionPersister(String role) throws MappingException {
		return factory.getCollectionPersister(role);
	}

	public Serializable getSnapshot(PersistentCollection coll) {
		return getCollectionEntry(coll).getSnapshot();
	}

	public Serializable getLoadedCollectionKey(PersistentCollection coll) {
		return getCollectionEntry(coll).getLoadedKey();
	}

	public boolean isInverseCollection(PersistentCollection collection) {
		CollectionEntry ce = getCollectionEntry(collection);
		return ce != null && ce.getLoadedPersister().isInverse();
	}

	public Connection connection() throws HibernateException {
		if (connection==null) {
			if (connect) {
				connect();
			}
			else if ( isOpen() ) {
				throw new HibernateException("Session is currently disconnected");
			}
			else {
				throw new HibernateException("Session is closed");
			}
		}
		return connection;
	}

	private boolean isJTATransactionActive(javax.transaction.Transaction tx) throws SystemException {
		return tx != null && (
				tx.getStatus() == javax.transaction.Status.STATUS_ACTIVE ||
				tx.getStatus() == javax.transaction.Status.STATUS_MARKED_ROLLBACK
		);
	}

	private void connect() throws HibernateException {
		connection = batcher.openConnection();
		connect = false;
		if ( !isCurrentTransaction ) {
			//if there is no current transaction callback registered
			//when we obtain the connection, try to register one now
			//note that this is not going to handle the case of
			//multiple-transactions-per-connection when the user is
			//manipulating transactions (need to use Hibernate txn)
			TransactionManager tm = factory.getTransactionManager();
			if ( tm != null ) {
				try {
					javax.transaction.Transaction tx = tm.getTransaction();
					if ( isJTATransactionActive(tx) ) {
						tx.registerSynchronization( new CacheSynchronization(this) );
						isCurrentTransaction = true;
					}
				}
				catch (Exception e) {
					throw new TransactionException( "could not register synchronization with JTA TransactionManager", e );
				}
			}
		}
		
		if ( factory.getStatistics().isStatisticsEnabled() ) {
			factory.getStatisticsImplementor().connect();
		}

	}

	public boolean isConnected() {
		return connection != null || connect;
	}

	public Connection disconnect() throws HibernateException {

		log.debug( "disconnecting session" );

		try {

			if (connect) {
				connect = false;
				return null;
			}
			else {

				if (connection==null) throw new HibernateException( "Session already disconnected" );

				batcher.closeStatements();
				Connection c = connection;
				connection = null;
				if (autoClose) {
					batcher.closeConnection(c);
					return null;
				}
				else {
					return c;
				}

			}

		}
		finally {
			if ( !isCurrentTransaction ) {
				afterTransactionCompletion(false); //false because we don't know the outcome of the transaction
			}
		}
	}

	public void reconnect() throws HibernateException {
		if ( isConnected() ) throw new HibernateException( "Session already connected" );

		log.debug( "reconnecting session" );

		connect = true;
		//connection = factory.openConnection();
	}

	public void reconnect(Connection conn) throws HibernateException {
		if ( isConnected() ) throw new HibernateException( "Session already connected" );
		this.connection = conn;
	}

	/**
	 * Just in case user forgot to commit()/cancel() or close()
	 */
	protected void finalize() throws Throwable {

		log.debug( "running Session.finalize()" );

		if (isCurrentTransaction) log.warn( "afterTransactionCompletion() was never called" );

		if ( connection != null ) { //ie it was never disconnected

			//afterTransactionCompletion(false);

			if ( connection.isClosed() ) {
				log.warn( "finalizing unclosed session with closed connection" );
			}
			else {
				log.warn("unclosed connection");
				if (autoClose) connection.close();
				//TODO: Should I also call closeStatements() from here?
			}
		}
	}

	public Collection filter(Object collection, String filter) throws HibernateException {
		return filter( collection, filter, new QueryParameters( new Type[1], new Object[1] ) );
	}

	public Collection filter(Object collection, String filter, Object value, Type type) throws HibernateException {
		return filter( collection, filter, new QueryParameters( new Type[]{null, type}, new Object[]{null, value} ) );
	}

	public Collection filter(Object collection, String filter, Object[] values, Type[] types) throws HibernateException {
		Object[] vals = new Object[values.length + 1];
		Type[] typs = new Type[types.length + 1];
		System.arraycopy( values, 0, vals, 1, values.length );
		System.arraycopy( types, 0, typs, 1, types.length );
		return filter( collection, filter, new QueryParameters( typs, vals ) );
	}

	/**
	 * 1. determine the collection role of the given collection (this may require a flush, if the
	 *    collecion is recorded as unreferenced)
	 * 2. obtain a compiled filter query
	 * 3. autoflush if necessary
	 */
	private FilterTranslator getFilterTranslator(Object collection, String filter, QueryParameters parameters, boolean scalar)
			throws HibernateException {

		if ( collection == null ) throw new NullPointerException( "null collection passed to filter" );

		if ( log.isTraceEnabled() ) {
			log.trace( "filter: " + filter );
			parameters.traceParameters(factory);
		}

		CollectionEntry entry = getCollectionEntryOrNull(collection);
		final CollectionPersister roleBeforeFlush = (entry == null) ? null : entry.getLoadedPersister();

		FilterTranslator filterTranslator;
		if ( roleBeforeFlush == null ) {
			// if it was previously unreferenced, we need
			// to flush in order to get its state into the
			// database to query
			flush();
			entry = getCollectionEntryOrNull(collection);
			CollectionPersister roleAfterFlush = (entry == null) ? null : entry.getLoadedPersister();
			if ( roleAfterFlush == null ) throw new QueryException( "The collection was unreferenced" );
			filterTranslator = factory.getFilter( filter, roleAfterFlush.getRole(), scalar, getEnabledFilters() );
		}
		else {
			// otherwise, we only need to flush if there are
			// in-memory changes to the queried tables
			filterTranslator = factory.getFilter( filter, roleBeforeFlush.getRole(), scalar, getEnabledFilters() );
			if ( autoFlushIfRequired( filterTranslator.getQuerySpaces() ) ) {
				// might need to run a different filter entirely after the flush
				// because the collection role may have changed
				entry = getCollectionEntryOrNull(collection);
				CollectionPersister roleAfterFlush = (entry == null) ? null : entry.getLoadedPersister();
				if ( roleBeforeFlush != roleAfterFlush ) {
					if ( roleAfterFlush == null ) throw new QueryException( "The collection was dereferenced" );
					filterTranslator = factory.getFilter( filter, roleAfterFlush.getRole(), scalar, getEnabledFilters() );
				}
			}
		}

		parameters.getPositionalParameterValues()[0] = entry.getLoadedKey();
		parameters.getPositionalParameterTypes()[0] = entry.getLoadedPersister().getKeyType();

		return filterTranslator;
	}

	/**
	 * Get the collection entry for a collection passed to filter,
	 * which might be a collection wrapper, an array, or an unwrapped
	 * collection. Return null if there is no entry.
	 */
	private CollectionEntry getCollectionEntryOrNull(Object collection) {

		PersistentCollection coll;
		if ( collection instanceof PersistentCollection ) {
			coll = (PersistentCollection) collection;
			//if (collection==null) throw new TransientObjectException("Collection was not yet persistent");
		}
		else {
			coll = getArrayHolder(collection);
			if ( coll == null ) {
				//it might be an unwrapped collection reference!
				//try to find a wrapper (slowish)
				Iterator wrappers = IdentityMap.keyIterator(collectionEntries);
				while ( wrappers.hasNext() ) {
					PersistentCollection pc = (PersistentCollection) wrappers.next();
					if ( pc.isWrapper(collection) ) {
						coll = pc;
						break;
					}
				}
			}
		}

		return (coll == null) ? null : getCollectionEntry(coll);

	}

	public List filter(Object collection, String filter, QueryParameters queryParameters) throws HibernateException {

		String[] concreteFilters = QuerySplitter.concreteQueries( filter, factory );
		FilterTranslator[] filters = new FilterTranslator[concreteFilters.length];

		for ( int i = 0; i < concreteFilters.length; i++ ) {
			filters[i] = getFilterTranslator( collection, concreteFilters[i], queryParameters, false );
		}

		dontFlushFromFind++;   //stops flush being called multiple times if this method is recursively called

		List results = Collections.EMPTY_LIST;
		try {
			for ( int i = 0; i < concreteFilters.length; i++ ) {
				List currentResults;
				try {
					currentResults = filters[i].list( this, queryParameters );
				}
				catch (SQLException sqle) {
					throw new JDBCException( "Could not execute query", sqle );
				}
				currentResults.addAll(results);
				results = currentResults;
			}
		}
		finally {
			dontFlushFromFind--;
		}
		return results;

	}

	public Iterator iterateFilter(Object collection, String filter, QueryParameters queryParameters) throws HibernateException {

		String[] concreteFilters = QuerySplitter.concreteQueries( filter, factory );
		FilterTranslator[] filters = new FilterTranslator[concreteFilters.length];

		for ( int i = 0; i < concreteFilters.length; i++ ) {
			filters[i] = getFilterTranslator( collection, concreteFilters[i], queryParameters, true );
		}

		if ( filters.length == 0 ) return Collections.EMPTY_LIST.iterator();

		Iterator result = null;
		Iterator[] results = null;
		boolean many = filters.length > 1;
		if (many) results = new Iterator[filters.length];

		//execute the queries and return all results as a single iterator
		for ( int i=0; i<filters.length; i++ ) {

			try {
				result = filters[i].iterate( queryParameters, this );
			}
			catch (SQLException sqle) {
				throw new JDBCException( "Could not execute query", sqle );
			}
			if (many) {
				results[i] = result;
			}

		}

		return many ? new JoinedIterator(results) : result;

	}

	public Criteria createCriteria(Class persistentClass) {
		return new CriteriaImpl( persistentClass.getName(), this );
	}

	public Criteria createCriteria(String entityName) {
		return new CriteriaImpl( entityName, this );
	}

	public ScrollableResults scroll(CriteriaImpl criteria, ScrollMode scrollMode) {
		String entityName = criteria.getCriteriaEntityName();
		CriteriaLoader loader = new CriteriaLoader(
				getOuterJoinLoadable(entityName),
				factory,
				new CriteriaImpl(entityName, criteria),
		        getEnabledFilters()
		);
		
		criteria.checkFetchModes();
		
		autoFlushIfRequired( loader.getQuerySpaces() );
		dontFlushFromFind++;
		try {
			return loader.scroll(this, scrollMode);
		}
		catch (SQLException sqle) {
			throw new JDBCException(sqle);
		}
		finally {
			dontFlushFromFind--;
		}
	}

	public List find(CriteriaImpl criteria) throws HibernateException {

		String[] implementors = factory.getImplementors( criteria.getCriteriaEntityName() );
		int size = implementors.length;

		CriteriaLoader[] loaders = new CriteriaLoader[size];
		Set spaces = new HashSet();
		for( int i=0; i <size; i++ ) {

			loaders[i] = new CriteriaLoader(
					getOuterJoinLoadable(implementors[i]),
					factory,
					new CriteriaImpl(implementors[i], criteria),
			        getEnabledFilters()
			);

			spaces.addAll( loaders[i].getQuerySpaces() );

		}

		criteria.checkFetchModes();
		
		autoFlushIfRequired(spaces);

		List results = Collections.EMPTY_LIST;
		dontFlushFromFind++;
		try {
			for( int i=0; i<size; i++ ) {
				List currentResults;
				try {
					currentResults = loaders[i].list(this);
				}
				catch (SQLException sqle) {
					throw new JDBCException(sqle);
				}
				currentResults.addAll(results);
				results = currentResults;
			}
		}
		finally {
			dontFlushFromFind--;
		}

		return results;
	}

	private OuterJoinLoadable getOuterJoinLoadable(String entityName) throws MappingException {
		EntityPersister persister = getEntityPersister( entityName );
		if ( !(persister instanceof OuterJoinLoadable) ) {
			throw new MappingException( "class persister is not OuterJoinLoadable: " + entityName );
		}
		return ( OuterJoinLoadable ) persister;
	}

	public boolean contains(Object object) {
		if ( object instanceof HibernateProxy ) {
			//do not use proxiesByKey, since not all
			//proxies that point to this session's
			//instances are in that collection!
			LazyInitializer li = HibernateProxyHelper.getLazyInitializer( (HibernateProxy) object );
			if ( li.isUninitialized() ) {
				//if it is an uninitialized proxy, pointing
				//with this session, then when it is accessed,
				//the underlying instance will be "contained"
				return li.getSession()==this;
			}
			else {
				//if it is initialized, see if the underlying
				//instance is contained, since we need to 
				//account for the fact that it might have been
				//evicted
				object = li.getImplementation();
			}
		}
		return isEntryFor(object);
	}

	/**
	 * remove any hard references to the entity that are held by the infrastructure
	 * (references held by application or other persistant instances are okay)
	 */
	public void evict(Object object) throws HibernateException {
        listeners.getEvictEventListener().onEvict( new EvictEvent(object, this) );
	}

	public void evictCollection(Object value, PersistentCollectionType type) {

		final Object pc;
		if ( type.isArrayType() ) {
			pc = arrayHolders.remove( value );
		}
		else if ( value instanceof PersistentCollection ) {
			pc = value;
		}
		else {
			return; //EARLY EXIT!
		}

		PersistentCollection collection = ( PersistentCollection ) pc;
		if ( collection.unsetSession( this ) ) evictCollection( collection );
	}

	private void evictCollection(PersistentCollection collection) {
		CollectionEntry ce = ( CollectionEntry ) collectionEntries.remove( collection );
		if ( log.isDebugEnabled() )
			log.debug(
					"evicting collection: " +
					MessageHelper.infoString( ce.getLoadedPersister(), ce.getLoadedKey() )
			);
		if ( ce.getLoadedPersister() != null && ce.getLoadedKey() != null ) {
			//TODO: is this 100% correct?
			collectionsByKey.remove( new CollectionKey( ce.getLoadedPersister(), ce.getLoadedKey() ) );
		}
	}

	public Object getVersion(Object entity) {
		return getEntry( entity ).getVersion();
	}

	public Serializable[] getCollectionBatch(CollectionPersister collectionPersister, Serializable id, int batchSize) {
		Serializable[] keys = new Serializable[batchSize];
		keys[0] = id;
		int i = 0;
		Iterator iter = collectionEntries.values().iterator();
		while ( iter.hasNext() ) {
			CollectionEntry ce = ( CollectionEntry ) iter.next();
			if (
				!ce.isInitialized() &&
				ce.getLoadedPersister() == collectionPersister &&
				!id.equals( ce.getLoadedKey() )
			) {
				keys[++i] = ce.getLoadedKey();
				if ( i == batchSize - 1 ) return keys;
			}
		}
		return keys;
	}

	public Serializable[] getEntityBatch(String entityName, Serializable id, int batchSize) {
		Serializable[] ids = new Serializable[batchSize];
		ids[0] = id;
		int i = 0;
		Iterator iter = batchLoadableEntityKeys.keySet().iterator();
		while ( iter.hasNext() ) {
			EntityKey key = ( EntityKey ) iter.next();
			if (
					key.getEntityName().equals( entityName ) &&
					!id.equals( key.getIdentifier() )
			) {
				ids[++i] = key.getIdentifier();
				if ( i == batchSize - 1 ) return ids;
			}
		}
		return ids;
	}

	public void scheduleBatchLoad(String entityName, Serializable id) throws MappingException {
		EntityPersister persister = getEntityPersister( entityName );
		if ( persister.isBatchLoadable() ) {
			batchLoadableEntityKeys.put( new EntityKey( id, persister ), MARKER );
		}
	}

	public Query createSQLQuery(String sql, String returnAlias, Class returnClass) {
		return new SQLQueryImpl(sql, new String[] { returnAlias }, new Class[] { returnClass }, this);
	}

	public Query createSQLQuery(String sql, String returnAliases[], Class returnClasses[]) {
		return new SQLQueryImpl(sql, returnAliases, returnClasses, this);
	}

	private Query createSQLQuery(
			String sql, 
			String returnAliases[], 
			Class returnClasses[],
			String collectionAlias,
			String collectionRole,
			LockMode[] lockModes,
			Collection querySpaces, 
			FlushMode queryFlushMode
	) {
		return new SQLQueryImpl(
				sql, 
				returnAliases, 
				returnClasses,
		        collectionAlias,
		        collectionRole,
				lockModes,
				this, 
				querySpaces, 
				queryFlushMode
		);
	}

	public ScrollableResults scrollBySQL(
		final String sqlQuery, 
		final String[] aliases, 
		final Class[] classes,
		final String collectionAlias,
		final String collectionRole,
		final LockMode[] lockModes,
		final QueryParameters queryParameters, 
		final Collection querySpaces) 
	throws HibernateException {

		if ( log.isTraceEnabled() ) {
			log.trace( "scroll SQL query: " + sqlQuery );
		}

		SQLLoadable persisters[] = new SQLLoadable[classes.length];
		for ( int i = 0; i < classes.length; i++ ) {
			persisters[i] = getSQLLoadable( classes[i].getName() );
		}

		//TODO: we could cache these!!
		QueryableCollection collectionPersister =
		        collectionRole == null
					? null
					: (QueryableCollection) getCollectionPersister(collectionRole);

		SQLLoader loader = new SQLLoader(
		        aliases,
		        persisters,
		        collectionAlias,
		        collectionPersister,
		        lockModes,
		        sqlQuery,
		        querySpaces
		);

		autoFlushIfRequired( loader.getQuerySpaces() );

		dontFlushFromFind++; //stops flush being called multiple times if this method is recursively called
		try {
			return loader.scroll(queryParameters, this);
		}
		catch (SQLException sqle) {
			throw new JDBCException( "Could not execute native SQL query", sqle );
		}
		finally {
			dontFlushFromFind--;
		}
	}

	// basically just an adapted copy of find(CriteriaImpl)
	public List findBySQL(
		final String sqlQuery, 
		final String[] aliases, 
		final Class[] classes,
		final String collectionAlias,
		final String collectionRole,
		final LockMode[] lockModes, 
		final QueryParameters queryParameters, 
		final Collection querySpaces) 
	throws HibernateException {

		if ( log.isTraceEnabled() ) log.trace( "SQL query: " + sqlQuery );

		SQLLoadable persisters[] = new SQLLoadable[classes.length];
		for ( int i = 0; i < classes.length; i++ ) {
			persisters[i] = getSQLLoadable( classes[i].getName() );
		}

		//TODO: we could cache these!!
		QueryableCollection collectionPersister =
		        collectionRole == null
					? null
					: (QueryableCollection) getCollectionPersister(collectionRole);

		SQLLoader loader = new SQLLoader(
		        aliases,
		        persisters,
		        collectionAlias,
		        collectionPersister,
		        lockModes,
		        sqlQuery,
		        querySpaces
		);

		autoFlushIfRequired( loader.getQuerySpaces() );

		dontFlushFromFind++;
		try {
			return loader.list(this, queryParameters);
		}
		catch (SQLException sqle) {
			throw new JDBCException( "Could not execute native SQL query", sqle );
		}
		finally {
			dontFlushFromFind--;
		}
	}

	private SQLLoadable getSQLLoadable(String entityName) throws MappingException {
		EntityPersister cp = getEntityPersister( entityName );
		if ( !(cp instanceof SQLLoadable) ) {
			throw new MappingException( "class persister is not SQLLoadable: " + entityName );
		}
		return ( SQLLoadable ) cp;
	}

	public void replicate(Object obj, ReplicationMode replicationMode) throws HibernateException {
		ReplicateEvent event = new ReplicateEvent(obj, replicationMode, this);
		listeners.getReplicateEventListener().onReplicate(event);
	}

	public void replicate(String entityName, Object obj, ReplicationMode replicationMode) 
	throws HibernateException {
		ReplicateEvent event = new ReplicateEvent(entityName, obj, replicationMode, this);
		listeners.getReplicateEventListener().onReplicate(event);
	}

	public SessionFactory getSessionFactory() {
		return factory;
	}
	
	public void initializeCollection(PersistentCollection collection, boolean writing) 
	throws HibernateException {
		getListeners().getInitializeCollectionEventListener()
			.onInitializeCollection( new InitializeCollectionEvent(collection, this) );
	}

	public String bestGuessEntityName(Object object) {
		if (object instanceof HibernateProxy) {
			object = HibernateProxyHelper.getLazyInitializer( (HibernateProxy) object ).getImplementation();
		}
		EntityEntry entry = getEntry(object);
		if (entry==null) {
			return guessEntityName(object);
		}
		else {
			return entry.getPersister().getEntityName();
		}
	}
	
	public String getEntityName(Object object) {
		if (object instanceof HibernateProxy) {
			if ( !proxiesByKey.values().contains(object) ) {
				throw new TransientObjectException("proxy was not associated with the session");
			}
			object = HibernateProxyHelper.getLazyInitializer( (HibernateProxy) object ).getImplementation();
		}

		EntityEntry entry = getEntry(object);
		if (entry==null) throwTransientObjectException(object);
		return entry.getPersister().getEntityName();
	}

	private void throwTransientObjectException(Object object) throws HibernateException {
		throw new TransientObjectException(
				"object references an unsaved transient instance - save the transient instance before flushing: " +
				guessEntityName(object)
		);
	}

	public String guessEntityName(Object object) throws HibernateException {
		String entity = interceptor.getEntityName(object);
		if ( entity == null ) {
			if ( object instanceof Map ) {
				entity = (String) ( (Map) object ).get( "type" );
				if ( entity == null ) throw new HibernateException( "could not determine type of dynamic entity" );
			}
			else {
				entity = object.getClass().getName();
			}
		}
		return entity;
	}

	public void cancelQuery() throws HibernateException {
		getBatcher().cancelLastQuery();
	}

	public void addNonExist(EntityKey key) {
		nonExists.add(key);
	}

	public void addNonExist(EntityUniqueKey key) {
		uniqueKeyNonExists.add(key);
	}

	public void removeNonExist(EntityKey key) {
		nonExists.remove(key);
	}

	public Object saveOrUpdateCopy(Object object) throws HibernateException {
		CopyEvent event = new CopyEvent(object, this);
		return listeners.getCopyEventListener().onCopy(event);
	}

	public void lock(String entityName, Object object, LockMode lockMode)
	throws HibernateException {
		LockEvent event = new LockEvent(entityName, object, lockMode, this);
		listeners.getLockEventListener().onLock(event);
	}

	public void save(String entityName, Object object, Serializable id)
	throws HibernateException {
		SaveEvent event = new SaveEvent(entityName, object, id, this);
		listeners.getSaveEventListener().onSave(event);
	}

	public Serializable save(String entityName, Object object)
	throws HibernateException {
		SaveEvent event = new SaveEvent(entityName, object, this);
		return listeners.getSaveEventListener().onSave(event);
	}

	public void saveOrUpdate(Object object) throws HibernateException {
		saveOrUpdate(null, object);
	}

	public Object saveOrUpdateCopy(String entityName, Object object, Serializable id) 
	throws HibernateException {
		CopyEvent event = new CopyEvent(entityName, object, id, this);
		return listeners.getCopyEventListener().onCopy(event);
	}

	public Object saveOrUpdateCopy(String entityName, Object object)
	throws HibernateException {
		CopyEvent event = new CopyEvent(entityName, object, this);
		return listeners.getCopyEventListener().onCopy(event);
	}

	public void update(String entityName, Object object, Serializable id)
	throws HibernateException {
		UpdateEvent event = new UpdateEvent(entityName, object, id, this);
		listeners.getUpdateEventListener().onUpdate(event);
	}

	public void update(String entityName, Object object)
	throws HibernateException {
		UpdateEvent event = new UpdateEvent(entityName, object, this);
		listeners.getUpdateEventListener().onUpdate(event);
	}
	
	public Object copy(Object object, Map copiedAlready) throws HibernateException {
		CopyEvent event = new CopyEvent(object, this);
		return listeners.getCopyEventListener().onCopy(event, copiedAlready);
	}

	public Object saveOrUpdateCopy(Object object, Serializable id) throws HibernateException {
		CopyEvent event = new CopyEvent(object, id, this);
		return listeners.getCopyEventListener().onCopy(event);
	}

	public int getCascadeLevel() {
		return cascading;
	}

	public int incrementCascadeLevel() {
		return ++cascading;
	}

	public int decrementCascadeLevel() {
		return --cascading;
	}

	public Interceptor getInterceptor() {
		return interceptor;
	}

	public Object getProxy(EntityKey key) {
		return proxiesByKey.get(key);
	}
	public void addProxy(EntityKey key, Object proxy) {
		proxiesByKey.put(key, proxy);
	}
	public Object removeProxy(EntityKey key) {
		return proxiesByKey.remove(key);
	}

	public HashSet getNullifiables() {
		return nullifiables;
	}

	public void setNullifiables(HashSet nullifiables) {
		this.nullifiables = nullifiables;
	}

	public ArrayList getInsertions() {
		return insertions;
	}

	public ArrayList getDeletions() {
		return deletions;
	}

	public void setDeletions(ArrayList deletions) {
		this.deletions = deletions;
	}

	public ArrayList getUpdates() {
		return updates;
	}

	public ArrayList getCollectionCreations() {
		return collectionCreations;
	}

	public ArrayList getCollectionUpdates() {
		return collectionUpdates;
	}

	public ArrayList getCollectionRemovals() {
		return collectionRemovals;
	}

	public ArrayList getExecutions() {
		return executions;
	}

	public Map getEntitiesByKey() {
		return entitiesByKey;
	}

	public boolean isFlushing() {
		return flushing;
	}

	public void setFlushing(boolean flushing) {
		this.flushing = flushing;
	}

	public Map getEntityEntries() {
		return entityEntries;
	}

	public Map getCollectionEntries() {
		return collectionEntries;
	}

	public Map getCollectionsByKey() {
		return collectionsByKey;
	}

	public Map getBatchLoadableEntityKeys() {
		return batchLoadableEntityKeys;
	}

	public boolean isNonExistant(EntityKey key) {
		return nonExists.contains(key);
	}

	public boolean isNonExistant(EntityUniqueKey key) {
		return uniqueKeyNonExists.contains(key);
	}

	public int getDontFlushFromFind() {
		return dontFlushFromFind;
	}

	public SessionEventListenerConfig getListeners() {
		return listeners;
	}
	
	public String toString() {
		StringBuffer buf = new StringBuffer(500)
			.append( getClass().getName() )
			.append('(');
		if ( isOpen() ) {
			buf.append("entitiesByKey=").append(entitiesByKey) 
				.append(" deletions=").append(deletions)
				.append(" insertions=").append(insertions)
				.append(" updates=").append(updates)
				.append(" collectionCreations=").append(collectionCreations)
				.append(" collectionRemovals=").append(collectionRemovals)
				.append(" collectionUpdates=").append(collectionUpdates);
		}
		else {
			buf.append("<closed>");
		}
		return buf.append(')').toString();
	}

}
