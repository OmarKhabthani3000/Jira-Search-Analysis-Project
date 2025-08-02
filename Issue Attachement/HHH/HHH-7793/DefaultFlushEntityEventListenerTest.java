/*
 * Copyright 2012 ETH Zuerich, CISD
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.hibernate.event.internal;

import static junit.framework.Assert.assertEquals;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dom4j.Node;
import org.hibernate.CacheMode;
import org.hibernate.Criteria;
import org.hibernate.EntityMode;
import org.hibernate.Filter;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.IdentifierLoadAccess;
import org.hibernate.Interceptor;
import org.hibernate.LobHelper;
import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.MappingException;
import org.hibernate.NaturalIdLoadAccess;
import org.hibernate.Query;
import org.hibernate.ReplicationMode;
import org.hibernate.SQLQuery;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.SharedSessionBuilder;
import org.hibernate.SimpleNaturalIdLoadAccess;
import org.hibernate.Transaction;
import org.hibernate.TypeHelper;
import org.hibernate.UnknownProfileException;
import org.hibernate.bytecode.spi.EntityInstrumentationMetadata;
import org.hibernate.cache.spi.CacheKey;
import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
import org.hibernate.cache.spi.entry.CacheEntryStructure;
import org.hibernate.collection.internal.PersistentList;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.engine.internal.StatefulPersistenceContext;
import org.hibernate.engine.jdbc.spi.JdbcConnectionAccess;
import org.hibernate.engine.query.spi.sql.NativeSQLQuerySpecification;
import org.hibernate.engine.spi.ActionQueue;
import org.hibernate.engine.spi.CascadeStyle;
import org.hibernate.engine.spi.EntityEntry;
import org.hibernate.engine.spi.EntityKey;
import org.hibernate.engine.spi.LoadQueryInfluencers;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.engine.spi.NonFlushedChanges;
import org.hibernate.engine.spi.PersistenceContext;
import org.hibernate.engine.spi.QueryParameters;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.engine.spi.Status;
import org.hibernate.engine.spi.ValueInclusion;
import org.hibernate.engine.transaction.spi.TransactionCoordinator;
import org.hibernate.event.spi.EventSource;
import org.hibernate.event.spi.FlushEntityEvent;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.internal.CriteriaImpl;
import org.hibernate.internal.FilterAliasGenerator;
import org.hibernate.jdbc.ReturningWork;
import org.hibernate.jdbc.Work;
import org.hibernate.loader.custom.CustomQuery;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.stat.SessionStatistics;
import org.hibernate.tuple.entity.EntityMetamodel;
import org.hibernate.tuple.entity.EntityTuplizer;
import org.hibernate.type.ForeignKeyDirection;
import org.hibernate.type.ListType;
import org.hibernate.type.ObjectType;
import org.hibernate.type.Type;
import org.hibernate.type.VersionType;
import org.junit.Test;

/**
 * 
 *
 * @author Franz-Josef Elmer
 */
public class DefaultFlushEntityEventListenerTest
{
    @Test
    public void test()
    {
        EventSource source = new MockEventSource();
        Object entity = null;
        StatefulPersistenceContext statefulPersistenceContext =
                new StatefulPersistenceContext(source);
        EntityPersister persister = new MockEntityPersister();
        EntityKey entityKey = new EntityKey(0, persister, null);
        EntityEntry entry =
                statefulPersistenceContext.addEntity(entity, Status.MANAGED, null, entityKey, null,
                        null, false, persister, false, false);
        FlushEntityEvent event = new FlushEntityEvent(source, entity, entry);
        event.setDirtyCheckPossible(true);
        PersistentList persistentList = new PersistentList();
        persistentList.dirty();
        event.setPropertyValues(new Object[] { persistentList });
        event.setDirtyProperties(new int[] { 42 });
        assertEquals(false, event.hasDirtyCollection());
        
        DefaultFlushEntityEventListener listener = new DefaultFlushEntityEventListener();
        listener.isUpdateNecessary(event);

        assertEquals(true, event.hasDirtyCollection());
    }

    private final class MockEventSource implements EventSource
    {
        @Override
        public void update(String entityName, Object object) throws HibernateException
        {
        }

        @Override
        public void update(Object object) throws HibernateException
        {
        }

        @Override
        public void setReadOnly(Object entityOrProxy, boolean readOnly)
        {
        }

        @Override
        public void setDefaultReadOnly(boolean readOnly)
        {
        }

        @Override
        public void saveOrUpdate(String entityName, Object object) throws HibernateException
        {
        }

        @Override
        public void saveOrUpdate(Object object) throws HibernateException
        {
        }

        @Override
        public Serializable save(String entityName, Object object) throws HibernateException
        {
            return null;
        }

        @Override
        public Serializable save(Object object) throws HibernateException
        {
            return null;
        }

        @Override
        public void replicate(String entityName, Object object, ReplicationMode replicationMode)
                throws HibernateException
        {
        }

        @Override
        public void replicate(Object object, ReplicationMode replicationMode)
                throws HibernateException
        {
        }

        @Override
        public void refresh(Object object, LockOptions lockOptions) throws HibernateException
        {
        }

        @Override
        public void refresh(Object object, LockMode lockMode) throws HibernateException
        {
        }

        @Override
        public void refresh(Object object) throws HibernateException
        {
        }

        @Override
        public void reconnect(Connection connection) throws HibernateException
        {
        }

        @Override
        public void persist(String entityName, Object object) throws HibernateException
        {
        }

        @Override
        public void persist(Object object) throws HibernateException
        {
        }

        @Override
        public Object merge(String entityName, Object object) throws HibernateException
        {
            return null;
        }

        @Override
        public Object merge(Object object) throws HibernateException
        {
            return null;
        }

        @Override
        public void lock(String entityName, Object object, LockMode lockMode)
                throws HibernateException
        {
        }

        @Override
        public void lock(Object object, LockMode lockMode) throws HibernateException
        {
        }

        @Override
        public void load(Object object, Serializable id) throws HibernateException
        {
        }

        @Override
        public Object load(String entityName, Serializable id) throws HibernateException
        {
            return null;
        }

        @Override
        public Object load(Class theClass, Serializable id) throws HibernateException
        {
            return null;
        }

        @Override
        public Object load(String entityName, Serializable id, LockOptions lockOptions)
                throws HibernateException
        {
            return null;
        }

        @Override
        public Object load(String entityName, Serializable id, LockMode lockMode)
                throws HibernateException
        {
            return null;
        }

        @Override
        public Object load(Class theClass, Serializable id, LockOptions lockOptions)
                throws HibernateException
        {
            return null;
        }

        @Override
        public Object load(Class theClass, Serializable id, LockMode lockMode)
                throws HibernateException
        {
            return null;
        }

        @Override
        public boolean isReadOnly(Object entityOrProxy)
        {
            return false;
        }

        @Override
        public boolean isFetchProfileEnabled(String name) throws UnknownProfileException
        {
            return false;
        }

        @Override
        public boolean isDirty() throws HibernateException
        {
            return false;
        }

        @Override
        public boolean isDefaultReadOnly()
        {
            return false;
        }

        @Override
        public TypeHelper getTypeHelper()
        {
            return null;
        }

        @Override
        public Transaction getTransaction()
        {
            return null;
        }

        @Override
        public SessionStatistics getStatistics()
        {
            return null;
        }

        @Override
        public SessionFactory getSessionFactory()
        {
            return null;
        }

        @Override
        public LobHelper getLobHelper()
        {
            return null;
        }

        @Override
        public Serializable getIdentifier(Object object) throws HibernateException
        {
            return null;
        }

        @Override
        public String getEntityName(Object object) throws HibernateException
        {
            return null;
        }

        @Override
        public Filter getEnabledFilter(String filterName)
        {
            return null;
        }

        @Override
        public LockMode getCurrentLockMode(Object object) throws HibernateException
        {
            return null;
        }

        @Override
        public Object get(String entityName, Serializable id, LockOptions lockOptions)
                throws HibernateException
        {
            return null;
        }

        @Override
        public Object get(String entityName, Serializable id, LockMode lockMode)
                throws HibernateException
        {
            return null;
        }

        @Override
        public Object get(String entityName, Serializable id) throws HibernateException
        {
            return null;
        }

        @Override
        public Object get(Class clazz, Serializable id, LockOptions lockOptions)
                throws HibernateException
        {
            return null;
        }

        @Override
        public Object get(Class clazz, Serializable id, LockMode lockMode)
                throws HibernateException
        {
            return null;
        }

        @Override
        public Object get(Class clazz, Serializable id) throws HibernateException
        {
            return null;
        }

        @Override
        public void evict(Object object) throws HibernateException
        {
        }

        @Override
        public Filter enableFilter(String filterName)
        {
            return null;
        }

        @Override
        public void enableFetchProfile(String name) throws UnknownProfileException
        {
        }

        @Override
        public void doWork(Work work) throws HibernateException
        {
        }

        @Override
        public Connection disconnect() throws HibernateException
        {
            return null;
        }

        @Override
        public void disableFilter(String filterName)
        {
        }

        @Override
        public void disableFetchProfile(String name) throws UnknownProfileException
        {
        }

        @Override
        public void delete(String entityName, Object object) throws HibernateException
        {
        }

        @Override
        public void delete(Object object) throws HibernateException
        {
        }

        @Override
        public SQLQuery createSQLQuery(String queryString) throws HibernateException
        {
            return null;
        }

        @Override
        public Query createQuery(String queryString) throws HibernateException
        {
            return null;
        }

        @Override
        public Query createFilter(Object collection, String queryString) throws HibernateException
        {
            return null;
        }

        @Override
        public Criteria createCriteria(String entityName, String alias)
        {
            return null;
        }

        @Override
        public Criteria createCriteria(String entityName)
        {
            return null;
        }

        @Override
        public Criteria createCriteria(Class persistentClass, String alias)
        {
            return null;
        }

        @Override
        public Criteria createCriteria(Class persistentClass)
        {
            return null;
        }

        @Override
        public boolean contains(Object object)
        {
            return false;
        }

        @Override
        public Connection close() throws HibernateException
        {
            return null;
        }

        @Override
        public void clear()
        {
        }

        @Override
        public void cancelQuery() throws HibernateException
        {
        }

        @Override
        public LockRequest buildLockRequest(LockOptions lockOptions)
        {
            return null;
        }

        @Override
        public Transaction beginTransaction() throws HibernateException
        {
            return null;
        }

        @Override
        public void setFlushMode(FlushMode fm)
        {
        }

        @Override
        public void setFetchProfile(String name)
        {
        }

        @Override
        public void setCacheMode(CacheMode cm)
        {
        }

        @Override
        public void setAutoClear(boolean enabled)
        {
        }

        @Override
        public ScrollableResults scrollCustomQuery(CustomQuery customQuery,
                QueryParameters queryParameters) throws HibernateException
        {
            return null;
        }

        @Override
        public ScrollableResults scroll(NativeSQLQuerySpecification spec,
                QueryParameters queryParameters) throws HibernateException
        {
            return null;
        }

        @Override
        public ScrollableResults scroll(CriteriaImpl criteria, ScrollMode scrollMode)
        {
            return null;
        }

        @Override
        public ScrollableResults scroll(String query, QueryParameters queryParameters)
                throws HibernateException
        {
            return null;
        }

        @Override
        public List listFilter(Object collection, String filter, QueryParameters queryParameters)
                throws HibernateException
        {
            return null;
        }

        @Override
        public List listCustomQuery(CustomQuery customQuery, QueryParameters queryParameters)
                throws HibernateException
        {
            return null;
        }

        @Override
        public List list(NativeSQLQuerySpecification spec, QueryParameters queryParameters)
                throws HibernateException
        {
            return null;
        }

        @Override
        public List list(CriteriaImpl criteria)
        {
            return null;
        }

        @Override
        public List list(String query, QueryParameters queryParameters) throws HibernateException
        {
            return null;
        }

        @Override
        public Iterator iterateFilter(Object collection, String filter,
                QueryParameters queryParameters) throws HibernateException
        {
            return null;
        }

        @Override
        public Iterator iterate(String query, QueryParameters queryParameters)
                throws HibernateException
        {
            return null;
        }

        @Override
        public boolean isTransactionInProgress()
        {
            return false;
        }

        @Override
        public boolean isOpen()
        {
            return false;
        }

        @Override
        public boolean isEventSource()
        {
            return false;
        }

        @Override
        public boolean isConnected()
        {
            return false;
        }

        @Override
        public boolean isClosed()
        {
            return false;
        }

        @Override
        public Object internalLoad(String entityName, Serializable id, boolean eager,
                boolean nullable) throws HibernateException
        {
            return null;
        }

        @Override
        public Object instantiate(String entityName, Serializable id) throws HibernateException
        {
            return null;
        }

        @Override
        public void initializeCollection(PersistentCollection collection, boolean writing)
                throws HibernateException
        {
        }

        @Override
        public Object immediateLoad(String entityName, Serializable id) throws HibernateException
        {
            return null;
        }

        @Override
        public String guessEntityName(Object entity) throws HibernateException
        {
            return null;
        }

        @Override
        public long getTimestamp()
        {
            return 0;
        }

        @Override
        public PersistenceContext getPersistenceContext()
        {
            return null;
        }

        @Override
        public NonFlushedChanges getNonFlushedChanges() throws HibernateException
        {
            return null;
        }

        @Override
        public Query getNamedSQLQuery(String name)
        {
            return null;
        }

        @Override
        public Query getNamedQuery(String name)
        {
            return null;
        }

        @Override
        public LoadQueryInfluencers getLoadQueryInfluencers()
        {
            return null;
        }

        @Override
        public Interceptor getInterceptor()
        {
            return null;
        }

        @Override
        public FlushMode getFlushMode()
        {

            return null;
        }

        @Override
        public Object getFilterParameterValue(String filterParameterName)
        {

            return null;
        }

        @Override
        public Type getFilterParameterType(String filterParameterName)
        {

            return null;
        }

        @Override
        public String getFetchProfile()
        {

            return null;
        }

        @Override
        public SessionFactoryImplementor getFactory()
        {

            return null;
        }

        @Override
        public Object getEntityUsingInterceptor(EntityKey key) throws HibernateException
        {

            return null;
        }

        @Override
        public EntityPersister getEntityPersister(String entityName, Object object)
                throws HibernateException
        {

            return null;
        }

        @Override
        public Map getEnabledFilters()
        {

            return null;
        }

        @Override
        public int getDontFlushFromFind()
        {

            return 0;
        }

        @Override
        public Serializable getContextEntityIdentifier(Object object)
        {

            return null;
        }

        @Override
        public CacheMode getCacheMode()
        {

            return null;
        }

        @Override
        public void flush()
        {

            
        }

        @Override
        public int executeUpdate(String query, QueryParameters queryParameters)
                throws HibernateException
        {

            return 0;
        }

        @Override
        public int executeNativeUpdate(NativeSQLQuerySpecification specification,
                QueryParameters queryParameters) throws HibernateException
        {

            return 0;
        }

        @Override
        public Connection connection()
        {
            return null;
        }

        @Override
        public String bestGuessEntityName(Object object)
        {
            return null;
        }


        @Override
        public void applyNonFlushedChanges(NonFlushedChanges nonFlushedChanges)
                throws HibernateException
        {
        }

        @Override
        public void afterScrollOperation()
        {
        }

        @Override
        public void refresh(Object object, Map refreshedAlready) throws HibernateException
        {

            
        }

        @Override
        public void persistOnFlush(String entityName, Object object, Map copiedAlready)
        {

            
        }

        @Override
        public void persist(String entityName, Object object, Map createdAlready)
                throws HibernateException
        {

            
        }

        @Override
        public void merge(String entityName, Object object, Map copiedAlready)
                throws HibernateException
        {

            
        }

        @Override
        public Object instantiate(EntityPersister persister, Serializable id)
                throws HibernateException
        {

            return null;
        }

        @Override
        public ActionQueue getActionQueue()
        {

            return null;
        }

        @Override
        public void forceFlush(EntityEntry e) throws HibernateException
        {

            
        }

        @Override
        public void delete(String entityName, Object child, boolean isCascadeDeleteEnabled,
                Set transientEntities)
        {

            
        }

        @Override
        public String getTenantIdentifier()
        {

            return null;
        }

        @Override
        public JdbcConnectionAccess getJdbcConnectionAccess()
        {

            return null;
        }

        @Override
        public EntityKey generateEntityKey(Serializable id, EntityPersister persister)
        {

            return null;
        }

        @Override
        public CacheKey generateCacheKey(Serializable id, Type type, String entityOrRoleName)
        {

            return null;
        }

        @Override
        public void disableTransactionAutoJoin()
        {

            
        }

        @Override
        public TransactionCoordinator getTransactionCoordinator()
        {

            return null;
        }

        @Override
        public <T> T execute(Callback<T> callback)
        {

            return null;
        }

        @Override
        public SharedSessionBuilder sessionWithOptions()
        {

            return null;
        }

        @Override
        public void refresh(String entityName, Object object)
        {

            
        }

        @Override
        public void refresh(String entityName, Object object, LockOptions lockOptions)
        {

            
        }

        @Override
        public IdentifierLoadAccess byId(String entityName)
        {

            return null;
        }

        @Override
        public IdentifierLoadAccess byId(Class entityClass)
        {

            return null;
        }

        @Override
        public NaturalIdLoadAccess byNaturalId(String entityName)
        {

            return null;
        }

        @Override
        public NaturalIdLoadAccess byNaturalId(Class entityClass)
        {

            return null;
        }

        @Override
        public SimpleNaturalIdLoadAccess bySimpleNaturalId(String entityName)
        {

            return null;
        }

        @Override
        public SimpleNaturalIdLoadAccess bySimpleNaturalId(Class entityClass)
        {

            return null;
        }

        @Override
        public <T> T doReturningWork(ReturningWork<T> work) throws HibernateException
        {

            return null;
        }
    }


    /**
     * 
     *
     * @author Franz-Josef Elmer
     */
    private final class MockEntityPersister implements EntityPersister
    {

        @Override
        public Comparator getVersionComparator()
        {

            return null;
        }

        @Override
        public void update(Serializable id, Object[] fields, int[] dirtyFields,
                boolean hasDirtyCollection, Object[] oldFields, Object oldVersion, Object object,
                Object rowId, SessionImplementor session) throws HibernateException
        {
        }

        @Override
        public void setIdentifier(Object entity, Serializable id, SessionImplementor session)
        {

        }

        @Override
        public void resetIdentifier(Object entity, Serializable currentId, Object currentVersion,
                SessionImplementor session)
        {
        }

        @Override
        public void processUpdateGeneratedProperties(Serializable id, Object entity,
                Object[] state, SessionImplementor session)
        {

            
        }

        @Override
        public void processInsertGeneratedProperties(Serializable id, Object entity,
                Object[] state, SessionImplementor session)
        {

            
        }

        @Override
        public void postInstantiate() throws MappingException
        {

            
        }

        @Override
        public void lock(Serializable id, Object version, Object object, LockOptions lockOptions,
                SessionImplementor session) throws HibernateException
        {

            
        }

        @Override
        public void lock(Serializable id, Object version, Object object, LockMode lockMode,
                SessionImplementor session) throws HibernateException
        {

            
        }

        @Override
        public Object load(Serializable id, Object optionalObject, LockOptions lockOptions,
                SessionImplementor session) throws HibernateException
        {

            return null;
        }

        @Override
        public Object load(Serializable id, Object optionalObject, LockMode lockMode,
                SessionImplementor session) throws HibernateException
        {

            return null;
        }

        @Override
        public boolean isVersioned()
        {

            return true;
        }

        @Override
        public boolean isVersionPropertyGenerated()
        {

            return false;
        }

        @Override
        public Boolean isTransient(Object object, SessionImplementor session)
                throws HibernateException
        {

            return null;
        }

        @Override
        public boolean isSubclassEntityName(String entityName)
        {

            return false;
        }

        @Override
        public boolean isSelectBeforeUpdateRequired()
        {

            return false;
        }

        @Override
        public boolean isMutable()
        {

            return false;
        }

        @Override
        public boolean isLazyPropertiesCacheable()
        {
            return false;
        }

        @Override
        public boolean isInherited()
        {

            return false;
        }

        @Override
        public boolean isIdentifierAssignedByInsert()
        {

            return false;
        }

        @Override
        public boolean isCacheInvalidationRequired()
        {

            return false;
        }

        @Override
        public boolean isBatchLoadable()
        {

            return false;
        }

        @Override
        public Object instantiate(Serializable id, SessionImplementor session)
        {

            return null;
        }

        @Override
        public Serializable insert(Object[] fields, Object object, SessionImplementor session)
                throws HibernateException
        {

            return null;
        }

        @Override
        public void insert(Serializable id, Object[] fields, Object object,
                SessionImplementor session) throws HibernateException
        {

            
        }


        @Override
        public boolean hasUpdateGeneratedProperties()
        {

            return false;
        }

        @Override
        public boolean hasSubselectLoadableCollections()
        {

            return false;
        }

        @Override
        public boolean hasProxy()
        {

            return false;
        }

        @Override
        public boolean hasNaturalIdentifier()
        {

            return false;
        }

        @Override
        public boolean hasMutableProperties()
        {

            return false;
        }

        @Override
        public boolean hasLazyProperties()
        {

            return false;
        }

        @Override
        public boolean hasInsertGeneratedProperties()
        {

            return false;
        }

        @Override
        public boolean hasIdentifierProperty()
        {

            return false;
        }

        @Override
        public boolean hasCollections()
        {
            return true;
        }

        @Override
        public boolean hasCascades()
        {

            return false;
        }

        @Override
        public boolean hasCache()
        {

            return false;
        }

        @Override
        public VersionType getVersionType()
        {

            return null;
        }

        @Override
        public int getVersionProperty()
        {

            return 0;
        }

        @Override
        public String getRootEntityName()
        {
            return "root";
        }

        @Override
        public Serializable[] getQuerySpaces()
        {

            return null;
        }

        @Override
        public boolean[] getPropertyVersionability()
        {
            return new boolean[] {true};
        }

        @Override
        public Object[] getPropertyValuesToInsert(Object object, Map mergeMap,
                SessionImplementor session) throws HibernateException
        {

            return null;
        }

        @Override
        public boolean[] getPropertyUpdateability()
        {

            return null;
        }

        @Override
        public ValueInclusion[] getPropertyUpdateGenerationInclusions()
        {

            return null;
        }

        @Override
        public Type[] getPropertyTypes()
        {
            return new Type[] {new ListType(null, null, null, false)};
        }

        @Override
        public Type getPropertyType(String propertyName) throws MappingException
        {

            return null;
        }

        @Override
        public Serializable[] getPropertySpaces()
        {

            return null;
        }

        @Override
        public boolean[] getPropertyNullability()
        {

            return null;
        }

        @Override
        public String[] getPropertyNames()
        {

            return null;
        }

        @Override
        public boolean[] getPropertyLaziness()
        {

            return null;
        }

        @Override
        public boolean[] getPropertyInsertability()
        {

            return null;
        }

        @Override
        public ValueInclusion[] getPropertyInsertGenerationInclusions()
        {

            return null;
        }

        @Override
        public boolean[] getPropertyCheckability()
        {

            return null;
        }

        @Override
        public CascadeStyle[] getPropertyCascadeStyles()
        {

            return null;
        }

        @Override
        public Object[] getNaturalIdentifierSnapshot(Serializable id, SessionImplementor session)
        {

            return null;
        }

        @Override
        public int[] getNaturalIdentifierProperties()
        {

            return null;
        }

        @Override
        public Type getIdentifierType()
        {
            return new ObjectType();
        }

        @Override
        public String getIdentifierPropertyName()
        {

            return null;
        }

        @Override
        public IdentifierGenerator getIdentifierGenerator()
        {

            return null;
        }

        @Override
        public Serializable getIdentifier(Object entity, SessionImplementor session)
        {

            return null;
        }

        @Override
        public SessionFactoryImplementor getFactory()
        {

            return null;
        }

        @Override
        public String getEntityName()
        {

            return null;
        }

        @Override
        public EntityMetamodel getEntityMetamodel()
        {

            return null;
        }

        @Override
        public Object[] getDatabaseSnapshot(Serializable id, SessionImplementor session)
                throws HibernateException
        {

            return null;
        }

        @Override
        public Object getCurrentVersion(Serializable id, SessionImplementor session)
                throws HibernateException
        {

            return null;
        }

        @Override
        public ClassMetadata getClassMetadata()
        {

            return null;
        }

        @Override
        public CacheEntryStructure getCacheEntryStructure()
        {

            return null;
        }

        @Override
        public EntityRegionAccessStrategy getCacheAccessStrategy()
        {

            return null;
        }

        @Override
        public Object forceVersionIncrement(Serializable id, Object currentVersion,
                SessionImplementor session) throws HibernateException
        {

            return null;
        }

        @Override
        public int[] findModified(Object[] old, Object[] current, Object object,
                SessionImplementor session)
        {

            return null;
        }

        @Override
        public int[] findDirty(Object[] currentState, Object[] previousState, Object owner,
                SessionImplementor session)
        {

            return null;
        }

        @Override
        public void delete(Serializable id, Object version, Object object,
                SessionImplementor session) throws HibernateException
        {

            
        }

        @Override
        public Object createProxy(Serializable id, SessionImplementor session)
                throws HibernateException
        {

            return null;
        }

        @Override
        public boolean canExtractIdOutOfEntity()
        {

            return false;
        }

        @Override
        public void afterReassociate(Object entity, SessionImplementor session)
        {

            
        }

        @Override
        public void afterInitialize(Object entity, boolean lazyPropertiesAreUnfetched,
                SessionImplementor session)
        {

            
        }

        @Override
        public Serializable loadEntityIdByNaturalId(Object[] naturalIdValues,
                LockOptions lockOptions, SessionImplementor session)
        {

            return null;
        }

        @Override
        public boolean hasNaturalIdCache()
        {

            return false;
        }

        @Override
        public NaturalIdRegionAccessStrategy getNaturalIdCacheAccessStrategy()
        {

            return null;
        }

        @Override
        public Serializable getIdByUniqueKey(Serializable key, String uniquePropertyName,
                SessionImplementor session)
        {

            return null;
        }

        @Override
        public boolean isInstrumented()
        {

            return false;
        }

        @Override
        public Class getMappedClass()
        {

            return null;
        }

        @Override
        public boolean implementsLifecycle()
        {

            return false;
        }

        @Override
        public Class getConcreteProxyClass()
        {

            return null;
        }

        @Override
        public void setPropertyValues(Object object, Object[] values)
        {

            
        }

        @Override
        public void setPropertyValue(Object object, int i, Object value)
        {

            
        }

        @Override
        public Object[] getPropertyValues(Object object)
        {

            return null;
        }

        @Override
        public Object getPropertyValue(Object object, int i) throws HibernateException
        {

            return null;
        }

        @Override
        public Object getPropertyValue(Object object, String propertyName)
        {

            return null;
        }

        @Override
        public Serializable getIdentifier(Object object) throws HibernateException
        {

            return null;
        }

        @Override
        public Object getVersion(Object object) throws HibernateException
        {

            return null;
        }

        @Override
        public boolean isInstance(Object object)
        {

            return false;
        }

        @Override
        public boolean hasUninitializedLazyProperties(Object object)
        {

            return false;
        }

        @Override
        public EntityPersister getSubclassEntityPersister(Object instance,
                SessionFactoryImplementor factory)
        {

            return null;
        }

        @Override
        public EntityMode getEntityMode()
        {

            return null;
        }

        @Override
        public EntityTuplizer getEntityTuplizer()
        {

            return null;
        }

        @Override
        public EntityInstrumentationMetadata getInstrumentationMetadata()
        {

            return null;
        }

        @Override
        public FilterAliasGenerator getFilterAliasGenerator(String rootAlias)
        {

            return null;
        }
    }


}
