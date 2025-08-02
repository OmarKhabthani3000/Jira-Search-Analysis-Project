package com.swissmedical.rem.architecture.session;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Transformer;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.MappingException;
import org.hibernate.collection.PersistentCollection;
import org.hibernate.engine.BatchFetchQueue;
import org.hibernate.engine.CollectionEntry;
import org.hibernate.engine.CollectionKey;
import org.hibernate.engine.EntityEntry;
import org.hibernate.engine.EntityKey;
import org.hibernate.engine.EntityUniqueKey;
import org.hibernate.engine.PersistenceContext;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.engine.StatefulPersistenceContext;
import org.hibernate.engine.Status;
import org.hibernate.engine.loading.LoadContexts;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.persister.entity.EntityPersister;


public class ConfigurableReadOnlyPersistenceContext extends StatefulPersistenceContext {
	

	private PersistenceContext persistenceContext;
	
	private SortedSet<String> readOnlyEntities;
	
	
	public ConfigurableReadOnlyPersistenceContext(PersistenceContext persistenceContext, Collection<Class> readOnlyEntities) {
		super(persistenceContext.getSession());
		this.persistenceContext = persistenceContext;
		this.readOnlyEntities = new TreeSet<String>(CollectionUtils.collect(readOnlyEntities, new Transformer<Class, String>() {
			public String transform(Class input) {
				return input.getName();
			}
		}));
	}

	public EntityEntry addEntity(Object entity, Status status, Object[] loadedState, EntityKey entityKey, Object version, LockMode lockMode, boolean existsInDatabase, EntityPersister persister, boolean disableVersionIncrement, boolean lazyPropertiesAreUnfetched) {
		if(status == Status.MANAGED && isReadOnlyEntiy(persister.getEntityName())) {
			return persistenceContext.addEntity(entity, Status.READ_ONLY, loadedState, entityKey, version, lockMode, existsInDatabase, persister, disableVersionIncrement, lazyPropertiesAreUnfetched);
		} else {
			return persistenceContext.addEntity(entity, status, loadedState, entityKey, version, lockMode, existsInDatabase, persister, disableVersionIncrement, lazyPropertiesAreUnfetched);
		}
	}
	
	public EntityEntry addEntry(Object entity, Status status, Object[] loadedState, Object rowId, Serializable id, Object version, LockMode lockMode, boolean existsInDatabase, EntityPersister persister, boolean disableVersionIncrement, boolean lazyPropertiesAreUnfetched) {
		if(status == Status.MANAGED && isReadOnlyEntiy(persister.getEntityName())) {
			return persistenceContext.addEntry(entity, Status.READ_ONLY, loadedState, rowId, id, version, lockMode, existsInDatabase, persister, disableVersionIncrement, lazyPropertiesAreUnfetched);
		} else {
			return persistenceContext.addEntry(entity, status, loadedState, rowId, id, version, lockMode, existsInDatabase, persister, disableVersionIncrement, lazyPropertiesAreUnfetched);
		}
	}
	
	public void setEntryStatus(EntityEntry entry, Status status) {
		if(status == Status.MANAGED && isReadOnlyEntiy(entry.getEntityName())) {
			persistenceContext.setEntryStatus(entry, Status.READ_ONLY);
		} else {
			persistenceContext.setEntryStatus(entry, status);
		}
	}

	
	private boolean isReadOnlyEntiy(String entityName) {
		return readOnlyEntities.contains(entityName);
	}

	
	
	/*
	 * From here on, just delegates to the decorated object
	 */

	public void addEntity(EntityKey key, Object entity) {
		persistenceContext.addEntity(key, entity);
	}
	
	public void addEntity(EntityUniqueKey euk, Object entity) {
		persistenceContext.addEntity(euk, entity);
	}
	
	public void addCollectionHolder(PersistentCollection holder) {
		persistenceContext.addCollectionHolder(holder);
	}

	public CollectionEntry addInitializedCollection(CollectionPersister persister, PersistentCollection collection, Serializable id) throws HibernateException {
		return persistenceContext.addInitializedCollection(persister, collection, id);
	}

	public void addInitializedDetachedCollection(CollectionPersister collectionPersister, PersistentCollection collection) throws HibernateException {
		persistenceContext.addInitializedDetachedCollection(collectionPersister, collection);
	}

	public void addNewCollection(CollectionPersister persister, PersistentCollection collection) throws HibernateException {
		persistenceContext.addNewCollection(persister, collection);
	}

	public void addNonLazyCollection(PersistentCollection collection) {
		persistenceContext.addNonLazyCollection(collection);
	}

	public void addNullProperty(EntityKey ownerKey, String propertyName) {
		persistenceContext.addNullProperty(ownerKey, propertyName);
	}

	public void addProxy(EntityKey key, Object proxy) {
		persistenceContext.addProxy(key, proxy);
	}

	public void addUninitializedCollection(CollectionPersister persister, PersistentCollection collection, Serializable id) {
		persistenceContext.addUninitializedCollection(persister, collection, id);
	}

	public void addUninitializedDetachedCollection(CollectionPersister persister, PersistentCollection collection) {
		persistenceContext.addUninitializedDetachedCollection(persister, collection);
	}

	public void addUnownedCollection(CollectionKey key, PersistentCollection collection) {
		persistenceContext.addUnownedCollection(key, collection);
	}

	public void afterLoad() {
		persistenceContext.afterLoad();
	}

	public void afterTransactionCompletion() {
		persistenceContext.afterTransactionCompletion();
	}

	public void beforeLoad() {
		persistenceContext.beforeLoad();
	}

	public void checkUniqueness(EntityKey key, Object object) throws HibernateException {
		persistenceContext.checkUniqueness(key, object);
	}

	public void clear() {
		persistenceContext.clear();
	}

	public boolean containsCollection(PersistentCollection collection) {
		return persistenceContext.containsCollection(collection);
	}

	public boolean containsEntity(EntityKey key) {
		return persistenceContext.containsEntity(key);
	}

	public boolean containsProxy(Object entity) {
		return persistenceContext.containsProxy(entity);
	}

	public int decrementCascadeLevel() {
		return persistenceContext.decrementCascadeLevel();
	}

	public boolean equals(Object obj) {
		return persistenceContext.equals(obj);
	}

	public BatchFetchQueue getBatchFetchQueue() {
		return persistenceContext.getBatchFetchQueue();
	}

	public Object[] getCachedDatabaseSnapshot(EntityKey key) {
		return persistenceContext.getCachedDatabaseSnapshot(key);
	}

	public int getCascadeLevel() {
		return persistenceContext.getCascadeLevel();
	}

	public PersistentCollection getCollection(CollectionKey collectionKey) {
		return persistenceContext.getCollection(collectionKey);
	}

	public Map getCollectionEntries() {
		return persistenceContext.getCollectionEntries();
	}

	public CollectionEntry getCollectionEntry(PersistentCollection coll) {
		return persistenceContext.getCollectionEntry(coll);
	}

	public CollectionEntry getCollectionEntryOrNull(Object collection) {
		return persistenceContext.getCollectionEntryOrNull(collection);
	}

	public PersistentCollection getCollectionHolder(Object array) {
		return persistenceContext.getCollectionHolder(array);
	}

	public Object getCollectionOwner(Serializable key, CollectionPersister collectionPersister) throws MappingException {
		return persistenceContext.getCollectionOwner(key, collectionPersister);
	}

	public Map getCollectionsByKey() {
		return persistenceContext.getCollectionsByKey();
	}

	public Object[] getDatabaseSnapshot(Serializable id, EntityPersister persister) throws HibernateException {
		return persistenceContext.getDatabaseSnapshot(id, persister);
	}

	public Map getEntitiesByKey() {
		return persistenceContext.getEntitiesByKey();
	}

	public Object getEntity(EntityKey key) {
		return persistenceContext.getEntity(key);
	}

	public Object getEntity(EntityUniqueKey euk) {
		return persistenceContext.getEntity(euk);
	}

	public Map getEntityEntries() {
		return persistenceContext.getEntityEntries();
	}

	public EntityEntry getEntry(Object entity) {
		return persistenceContext.getEntry(entity);
	}

	public Object getIndexInOwner(String entity, String property, Object childEntity, Map mergeMap) {
		return persistenceContext.getIndexInOwner(entity, property, childEntity, mergeMap);
	}

	public LoadContexts getLoadContexts() {
		return persistenceContext.getLoadContexts();
	}

	public Object[] getNaturalIdSnapshot(Serializable id, EntityPersister persister) throws HibernateException {
		return persistenceContext.getNaturalIdSnapshot(id, persister);
	}

	public HashSet getNullifiableEntityKeys() {
		return persistenceContext.getNullifiableEntityKeys();
	}

	public Serializable getOwnerId(String entity, String property, Object childEntity, Map mergeMap) {
		return persistenceContext.getOwnerId(entity, property, childEntity, mergeMap);
	}

	public Object getProxy(EntityKey key) {
		return persistenceContext.getProxy(key);
	}

	public SessionImplementor getSession() {
		return persistenceContext.getSession();
	}

	public Serializable getSnapshot(PersistentCollection coll) {
		return persistenceContext.getSnapshot(coll);
	}

	public int hashCode() {
		return persistenceContext.hashCode();
	}

	public boolean hasNonReadOnlyEntities() {
		return persistenceContext.hasNonReadOnlyEntities();
	}

	public int incrementCascadeLevel() {
		return persistenceContext.incrementCascadeLevel();
	}

	public void initializeNonLazyCollections() throws HibernateException {
		persistenceContext.initializeNonLazyCollections();
	}

	public boolean isEntryFor(Object entity) {
		return persistenceContext.isEntryFor(entity);
	}

	public boolean isFlushing() {
		return persistenceContext.isFlushing();
	}

	public boolean isPropertyNull(EntityKey ownerKey, String propertyName) {
		return persistenceContext.isPropertyNull(ownerKey, propertyName);
	}

	public boolean isStateless() {
		return persistenceContext.isStateless();
	}

	public Object narrowProxy(Object proxy, EntityPersister persister, EntityKey key, Object object) throws HibernateException {
		return persistenceContext.narrowProxy(proxy, persister, key, object);
	}

	public Object proxyFor(EntityPersister persister, EntityKey key, Object impl) throws HibernateException {
		return persistenceContext.proxyFor(persister, key, impl);
	}

	public Object proxyFor(Object impl) throws HibernateException {
		return persistenceContext.proxyFor(impl);
	}

	public boolean reassociateIfUninitializedProxy(Object value) throws MappingException {
		return persistenceContext.reassociateIfUninitializedProxy(value);
	}

	public void reassociateProxy(Object value, Serializable id) throws MappingException {
		persistenceContext.reassociateProxy(value, id);
	}

	public PersistentCollection removeCollectionHolder(Object array) {
		return persistenceContext.removeCollectionHolder(array);
	}

	public Object removeEntity(EntityKey key) {
		return persistenceContext.removeEntity(key);
	}

	public EntityEntry removeEntry(Object entity) {
		return persistenceContext.removeEntry(entity);
	}

	public Object removeProxy(EntityKey key) {
		return persistenceContext.removeProxy(key);
	}

	public void replaceDelayedEntityIdentityInsertKeys(EntityKey oldKey, Serializable generatedId) {
		persistenceContext.replaceDelayedEntityIdentityInsertKeys(oldKey, generatedId);
	}

	public void setFlushing(boolean flushing) {
		persistenceContext.setFlushing(flushing);
	}

	public void setReadOnly(Object entity, boolean readOnly) {
		persistenceContext.setReadOnly(entity, readOnly);
	}

	public String toString() {
		return persistenceContext.toString();
	}

	public Object unproxy(Object maybeProxy) throws HibernateException {
		return persistenceContext.unproxy(maybeProxy);
	}

	public Object unproxyAndReassociate(Object maybeProxy) throws HibernateException {
		return persistenceContext.unproxyAndReassociate(maybeProxy);
	}

	public PersistentCollection useUnownedCollection(CollectionKey key) {
		return persistenceContext.useUnownedCollection(key);
	}
	
	
	
}
