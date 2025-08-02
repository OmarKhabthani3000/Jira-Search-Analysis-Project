package org.wfp.rita.test.hibernate;

import java.io.Serializable;
import java.sql.Connection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.CacheMode;
import org.hibernate.Criteria;
import org.hibernate.EntityMode;
import org.hibernate.FetchMode;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.Interceptor;
import org.hibernate.LockMode;
import org.hibernate.MappingException;
import org.hibernate.Query;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.StatelessSession;
import org.hibernate.Transaction;
import org.hibernate.collection.PersistentCollection;
import org.hibernate.engine.EntityEntry;
import org.hibernate.engine.EntityKey;
import org.hibernate.engine.ForeignKeys;
import org.hibernate.engine.PersistenceContext;
import org.hibernate.engine.QueryParameters;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.engine.query.sql.NativeSQLQuerySpecification;
import org.hibernate.event.EventListeners;
import org.hibernate.impl.CriteriaImpl;
import org.hibernate.impl.SessionImpl;
import org.hibernate.impl.StatelessSessionImpl;
import org.hibernate.jdbc.Batcher;
import org.hibernate.jdbc.JDBCContext;
import org.hibernate.loader.criteria.CriteriaLoader;
import org.hibernate.loader.custom.CustomQuery;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.persister.entity.OuterJoinLoadable;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.type.Type;
import org.wfp.rita.test.base.HibernateTestBase;

/**
 * This is a test for 
 * {@link http://opensource.atlassian.com/projects/hibernate/browse/HHH-},
 * which currently prevents us from using StatelessSession to retrieve large
 * numbers of objects for synchronization without loading them into the
 * Hibernate cache, which would eventually clog the cache and exhaust
 * the available memory.
 * 
 * When performing a {@link org.hibernate.Criteria} query in a stateless
 * session, and a (@link org.hibernate.mapping.PersistentClass} links to
 * another using a unique non-primary key reference, the referenced object
 * is not fully populated, only enough to store the linked primary key
 * fields. This makes Hibernate think that it's just loaded a transient
 * object (as the ID is still left null), which it doesn't like at all:
 * 
 * <pre>
 * org.hibernate.TransientObjectException: object references an unsaved transient instance - save the transient instance before flushing: org.wfp.rita.test.hibernate.HibernateStatelessSessionCriteriaUniqueKeyJoinTest$Project
 * at org.hibernate.engine.ForeignKeys.getEntityIdentifierIfNotUnsaved(ForeignKeys.java:242)
 * at org.hibernate.type.EntityType.getIdentifier(EntityType.java:430)
 * at org.hibernate.type.ManyToOneType.nullSafeSet(ManyToOneType.java:110)
 * at org.hibernate.type.ComponentType.nullSafeSet(ComponentType.java:307)
 * at org.hibernate.loader.Loader.bindPositionalParameters(Loader.java:1732)
 * at org.hibernate.loader.Loader.bindParameterValues(Loader.java:1703)
 * at org.hibernate.loader.Loader.prepareQueryStatement(Loader.java:1593)
 * at org.hibernate.loader.Loader.doQuery(Loader.java:696)
 * at org.hibernate.loader.Loader.doQueryAndInitializeNonLazyCollections(Loader.java:259)
 * at org.hibernate.loader.Loader.loadEntity(Loader.java:1885)
 * at org.hibernate.loader.entity.AbstractEntityLoader.load(AbstractEntityLoader.java:71)
 * at org.hibernate.loader.entity.EntityLoader.loadByUniqueKey(EntityLoader.java:108)
 * at org.hibernate.persister.entity.AbstractEntityPersister.loadByUniqueKey(AbstractEntityPersister.java:1662)
 * at org.hibernate.type.EntityType.loadByUniqueKey(EntityType.java:641)
 * at org.hibernate.type.EntityType.resolve(EntityType.java:415)
 * at org.hibernate.engine.TwoPhaseLoad.initializeEntity(TwoPhaseLoad.java:139)
 * at org.hibernate.loader.Loader.initializeEntitiesAndCollections(Loader.java:877)
 * at org.hibernate.loader.Loader.doQuery(Loader.java:752)
 * at org.hibernate.loader.Loader.doQueryAndInitializeNonLazyCollections(Loader.java:259)
 * at org.hibernate.loader.Loader.doList(Loader.java:2232)
 * at org.hibernate.loader.Loader.listIgnoreQueryCache(Loader.java:2129)
 * at org.hibernate.loader.Loader.list(Loader.java:2124)
 * at org.hibernate.loader.criteria.CriteriaLoader.list(CriteriaLoader.java:118)
 * at org.hibernate.impl.StatelessSessionImpl.list(StatelessSessionImpl.java:565)
 * at org.hibernate.impl.CriteriaImpl.list(CriteriaImpl.java:306)
 * at org.wfp.rita.test.hibernate.HibernateStatelessSessionCriteriaUniqueKeyJoinTest.assertList(HibernateStatelessSessionCriteriaUniqueKeyJoinTest.java:162)
 * at org.wfp.rita.test.hibernate.HibernateStatelessSessionCriteriaUniqueKeyJoinTest.testFailingDefaultLazyStatelessSession(HibernateStatelessSessionCriteriaUniqueKeyJoinTest.java:218)
 * </pre>
 * 
 * This call chain:
 * 
 * <ul>
 * <li>org.hibernate.type.EntityType.resolve(EntityType.java:415)</li>
 * <li>org.hibernate.engine.TwoPhaseLoad.initializeEntity(TwoPhaseLoad.java:139)</li>
 * <li>org.hibernate.loader.Loader.initializeEntitiesAndCollections(Loader.java:877)</li>
 * </ul>
 * 
 * will find the object ({@link Project} or {@link Site}) in the
 * {@link org.hibernate.engine.StatefulPersistenceContext} if it was
 * eagerly loaded, because it has already been hydrated.
 * 
 * Otherwise, it calls 
 * {@link org.hibernate.type.EntityType#loadByUniqueKey(java.lang.String, java.lang.String, java.lang.Object, org.hibernate.engine.SessionImplementor)}
 * which ends up calling
 * {@link ForeignKeys#getEntityIdentifierIfNotUnsaved}
 * which calls
 * {@link SessionImplementor#getContextEntityIdentifier(java.lang.Object)}
 * to retrieve the {@link Project} or {@link Site}'s ID, to bind to the
 * query to retrieve the {@link ProjectSite}.
 * 
 * If the {@link Session} is stateful, this works fine, because
 * {@link SessionImpl#getContextEntityIdentifier(java.lang.Object)}
 * knows how to retrieve the identifier from the 
 * {@link HibernateProxy} even though the object is not loaded yet.
 * 
 * However, {@link org.hibernate.StatelessSession} has a lame
 * implementation of 
 * {@link org.hibernate.impl.StatelessSessionImpl#getContextEntityIdentifier}:
 * 
 * <pre>
 * public Serializable getContextEntityIdentifier(Object object) {
 *     errorIfClosed();
 *     return null;
 * }
 * </pre>
 * 
 * And because the object is a lazy proxy which has not yet been initialized,
 * its fields are all null, so {@link ForeignKeys#isTransient} returns true,
 * and {@link ForeignKeys#getEntityIdentifierIfNotUnsaved} throws the exception
 * shown above.
 * 
 * I think the best fix is to improve the implementation of
 * {@link org.hibernate.impl.StatelessSessionImpl#getContextEntityIdentifier}
 * to match
 * {@link org.hibernate.impl.SessionImpl#getContextEntityIdentifier}.
 * We could instead improve {@link ForeignKeys#isTransient} to check for
 * a proxy object, but I think it makes more sense for there to be more
 * shared code between
 * {@link Session} and {@link StatelessSession} instead. Ideally these should
 * inherit from a common base class or the statefulness should be extracted
 * into a wrapper around a stateless {@link Session} instead.
 * 
 * The included {@link SessionWrapper} is used by
 * {@link HibernateStatelessSessionCriteriaUniqueKeyJoinTest#testSuccessfulWorkaround}
 * to show that replacing the implementation of
 * {@link org.hibernate.impl.StatelessSessionImpl#getContextEntityIdentifier}
 * will fix the problem. It can also be used to work around the problem
 * without patching Hibernate, until the official fix is released. 
 * 
 * @see http://opensource.atlassian.com/projects/hibernate/browse/HHH-
 * 
 * Change HibernateTestBase to org.hibernate.test.annotations.TestCase to
 * run under Hibernate.
 *
 * @author Chris Wilson <chris+hibernate@aptivate.org>
 */
public class HibernateStatelessSessionCriteriaUniqueKeyJoinTest
extends HibernateTestBase
{
    @Entity
    @Table(name="project")
    private static class Project implements Serializable
    {        
        @Id
        @GeneratedValue
        Integer id;
    }

    @Entity
    @Table(name="site")
    private static class Site implements Serializable
    {        
        @Id
        @GeneratedValue
        Integer id;
    }

    @Entity
    @Table(name="project_site", uniqueConstraints={
        @UniqueConstraint(columnNames={"project_id", "site_id"})
    })
    private static class ProjectSite implements Serializable
    {
        /*
        private static class Id implements Serializable
        {
            @Column(name="project_id")
            Integer projectId;

            @Column(name="site_id")
            Integer siteId;
        }
        */
        
        @Id
        @GeneratedValue
        Integer id;
        
        @ManyToOne(fetch=FetchType.LAZY)
        @JoinColumn(name="project_id")
        Project project;
        
        @ManyToOne(fetch=FetchType.LAZY)
        @JoinColumn(name="site_id")
        Site site;
    }
    
    @Entity
    @Table(name="request")
    private static class Request implements Serializable
    {
        @Id
        @GeneratedValue
        Integer id;
            
        @ManyToOne(fetch=FetchType.LAZY)
        @JoinColumns({
            @JoinColumn(name="project_id", referencedColumnName="project_id"),   
            @JoinColumn(name="site_id", referencedColumnName="site_id")    
        })
        ProjectSite projectSite;
    }
    
    protected Class[] getMappings()
    {
        return new Class[]{Project.class, Site.class, ProjectSite.class,
            Request.class};
    }

    public void setUp() throws Exception
    {
        super.setUp();
        
        Transaction transaction = null;
        
        try
        {
            Session session = openSession();
            transaction = session.beginTransaction();
            
            Project p = new Project();
            session.save(p);
            
            Site s = new Site();
            session.save(s);
            
            ProjectSite ps = new ProjectSite();
            // ps.id = new ProjectSite.Id();
            // ps.id.projectId = p.id;
            // ps.id.siteId = s.id;
            ps.project = p;
            ps.site = s;
            session.save(ps);
            
            Request r = new Request();
            r.projectSite = ps;
            session.save(r);
            
            transaction.commit();
            transaction = null;
            session.close();
        }
        finally
        {
            if (transaction != null)
            {
                transaction.rollback();
            }
        }
    }

    private void assertList(Criteria c)
    {
        List<Request> results = c.list();
        assertEquals(results.toString(), 1, results.size());
    }

    /**
     * This test passes, using a stateful {@link Session} in Lazy mode. 
     */
    public void testSuccessfulLazyStatefulSession()
    {
        Session s = getSessions().openSession();
        Criteria c = s.createCriteria(Request.class);
        assertList(c);
        s.close();
    }

    /**
     * This test passes, using a {@link StatelessSession} but overriding
     * the {@link FetchMode} for all three joins from the default
     * {@link FetchMode#SELECT} (lazy) to {@link FetchMode#EAGER} (eager).
     */
    public void testSuccessfulEagerStatelessSession() throws Exception
    {
        StatelessSession s = getSessions().openStatelessSession();
        Criteria c = s.createCriteria(Request.class);
        c.setFetchMode("projectSite", FetchMode.JOIN);
        c.setFetchMode("projectSite.project", FetchMode.JOIN);
        c.setFetchMode("projectSite.site", FetchMode.JOIN);
        assertList(c);
        s.close();
    }
    
    /**
     * This test fails, using a {@link StatelessSession} and not explicitly 
     * specifying the loading type for the association, allowing it to
     * default to {@link FetchMode#SELECT} (lazy loading) from the
     * {@link ManyToOne} annotation.  
     */
    public void testFailingDefaultLazyStatelessSession() throws Exception
    {
        StatelessSession s = getSessions().openStatelessSession();
        Criteria c = s.createCriteria(Request.class);
        assertList(c);
        s.close();
    }

    private static class SessionWrapper implements SessionImplementor
    {
        private SessionImplementor m_Impl;
        
        public SessionWrapper(SessionImplementor impl)
        {
            m_Impl = impl;
        }
        
        public Interceptor getInterceptor()
        {
            return m_Impl.getInterceptor();
        }
        public void setAutoClear(boolean enabled)
        {
            m_Impl.setAutoClear(enabled);
        }
        public boolean isTransactionInProgress()
        {
            return m_Impl.isTransactionInProgress();
        }
        public void initializeCollection(PersistentCollection collection,
            boolean writing) 
        throws HibernateException
        {
            m_Impl.initializeCollection(collection, writing);
        }
        // overridden to work around HHH-3220.
        public Object internalLoad(String entityName, Serializable id,
            boolean eager, boolean nullable) 
        throws HibernateException
        {
            EntityPersister persister = getFactory().getEntityPersister( entityName );
            // first, try to load it from the temp PC associated to this SS
            EntityKey key = new EntityKey(id, persister, getEntityMode());
            Object loaded = getPersistenceContext().getEntity(key);
            if ( loaded != null ) {
                // we found it in the temp PC.  Should indicate we are in the midst of processing a result set
                // containing eager fetches via join fetch
                return loaded;
            }
            if ( !eager && persister.hasProxy() ) {
                // if the metadata allowed proxy creation and caller did not request forceful eager loading,
                // generate a proxy
                return persister.createProxy( id, this );
            }
            // otherwise immediately materialize it
            return getFactory().getEntityPersister(entityName).load(id,
                null, LockMode.NONE, this);
        }
        public Object immediateLoad(String entityName, Serializable id)
        throws HibernateException
        {
            return m_Impl.immediateLoad(entityName, id);
        }
        public long getTimestamp()
        {
            return m_Impl.getTimestamp();
        }
        public SessionFactoryImplementor getFactory()
        {
            return m_Impl.getFactory();
        }
        public Batcher getBatcher()
        {
            return m_Impl.getBatcher();
        }
        public List list(String query, QueryParameters queryParameters) 
        throws HibernateException
        {
            return m_Impl.list(query, queryParameters);
        }
        public Iterator iterate(String query, QueryParameters queryParameters)
        throws HibernateException
        {
            return m_Impl.iterate(query, queryParameters);
        }
        public ScrollableResults scroll(String query,
            QueryParameters queryParameters) throws HibernateException
        {
            return m_Impl.scroll(query, queryParameters);
        }
        public ScrollableResults scroll(CriteriaImpl criteria,
            ScrollMode scrollMode)
        {
            return m_Impl.scroll(criteria, scrollMode);
        }
        // pass ourselves as the Session, not the wrapped one, which
        // would happen if we called m_Impl.list().
        public List list(CriteriaImpl criteria) throws HibernateException
        {
            String[] implementors = getFactory().getImplementors( 
                criteria.getEntityOrClassName());
            int size = implementors.length;

            CriteriaLoader[] loaders = new CriteriaLoader[size];
            for( int i=0; i <size; i++ ) {
                loaders[i] = new CriteriaLoader(
                        getOuterJoinLoadable( implementors[i] ),
                        getFactory(),
                        criteria,
                        implementors[i],
                        getEnabledFilters()
                );
            }


            List results = Collections.EMPTY_LIST;
            boolean success = false;
            try {
                for( int i=0; i<size; i++ ) {
                    final List currentResults = loaders[i].list(this);
                    currentResults.addAll(results);
                    results = currentResults;
                }
                success = true;
            }
            finally
            {
                if (m_Impl instanceof SessionImpl)
                {
                    ((SessionImpl) m_Impl).afterOperation(success);
                }
                else if (m_Impl instanceof StatelessSession)
                {
                    ((StatelessSessionImpl) m_Impl).afterOperation(success);
                }
                else
                {
                    throw new UnsupportedOperationException();
                }
                
            }
            // getPersistenceContext.clear();
            afterScrollOperation();
            return results;
        }
        // Copied from parent class as it's private
        private OuterJoinLoadable getOuterJoinLoadable(String entityName) throws MappingException {
            EntityPersister persister = getFactory().getEntityPersister(entityName);
            if ( !(persister instanceof OuterJoinLoadable) ) {
                throw new MappingException( "class persister is not OuterJoinLoadable: " + entityName );
            }
            return ( OuterJoinLoadable ) persister;
        }
        public List listFilter(Object collection, String filter,
            QueryParameters queryParameters) throws HibernateException
        {
            return m_Impl.listFilter(collection, filter, queryParameters);
        }
        public Iterator iterateFilter(Object collection, String filter,
            QueryParameters queryParameters) throws HibernateException
        {
            return m_Impl.iterateFilter(collection, filter, queryParameters);
        }
        public EntityPersister getEntityPersister(String entityName,
            Object object) throws HibernateException
        {
            return m_Impl.getEntityPersister(entityName, object);
        }
        public Object getEntityUsingInterceptor(EntityKey key)
        throws HibernateException
        {
            return m_Impl.getEntityUsingInterceptor(key);
        }
        public void afterTransactionCompletion(boolean successful,
            Transaction tx)
        {
            m_Impl.afterTransactionCompletion(successful, tx);
        }
        public void beforeTransactionCompletion(Transaction tx)
        {
            m_Impl.beforeTransactionCompletion(tx);
        }
        // Patched to work around the problem
        public Serializable getContextEntityIdentifier(Object object)
        {
            // errorIfClosed();
            if ( object instanceof HibernateProxy ) {
                return getProxyIdentifier(object);
            }
            else {
                EntityEntry entry = getPersistenceContext().getEntry(object);
                return entry != null ? entry.getId() : null;
            }
        }
        // copied from SessionImpl
        private Serializable getProxyIdentifier(Object proxy) {
            return ( (HibernateProxy) proxy ).getHibernateLazyInitializer().getIdentifier();
        }

        public String bestGuessEntityName(Object object)
        {
            return m_Impl.bestGuessEntityName(object);
        }
        public String guessEntityName(Object entity) throws HibernateException
        {
            return m_Impl.guessEntityName(entity);
        }
        public Object instantiate(String entityName, Serializable id)
        throws HibernateException
        {
            return m_Impl.instantiate(entityName, id);
        }
        public List listCustomQuery(CustomQuery customQuery,
            QueryParameters queryParameters) 
        throws HibernateException
        {
            return m_Impl.listCustomQuery(customQuery, queryParameters);
        }
        public ScrollableResults scrollCustomQuery(CustomQuery customQuery,
            QueryParameters queryParameters) 
        throws HibernateException
        {
            return m_Impl.scrollCustomQuery(customQuery, queryParameters);
        }
        public List list(NativeSQLQuerySpecification spec,
            QueryParameters queryParameters)
        throws HibernateException
        {
            return m_Impl.list(spec, queryParameters);
        }
        public ScrollableResults scroll(NativeSQLQuerySpecification spec,
            QueryParameters queryParameters)
        throws HibernateException
        {
            return m_Impl.scroll(spec, queryParameters);
        }
        public Object getFilterParameterValue(String filterParameterName)
        {
            return m_Impl.getFilterParameterValue(filterParameterName);
        }
        public Type getFilterParameterType(String filterParameterName)
        {
            return m_Impl.getFilterParameterType(filterParameterName);
        }
        public Map getEnabledFilters()
        {
            return m_Impl.getEnabledFilters();
        }
        public int getDontFlushFromFind()
        {
            return m_Impl.getDontFlushFromFind();
        }
        public EventListeners getListeners()
        {
            return m_Impl.getListeners();
        }
        public PersistenceContext getPersistenceContext()
        {
            return m_Impl.getPersistenceContext();
        }
        public int executeUpdate(String query, QueryParameters queryParameters)
        throws HibernateException
        {
            return m_Impl.executeUpdate(query, queryParameters);
        }
        public int executeNativeUpdate(NativeSQLQuerySpecification specification, 
            QueryParameters queryParameters) throws HibernateException
        {
            return m_Impl.executeNativeUpdate(specification, queryParameters);
        }
        public EntityMode getEntityMode()
        {
            return m_Impl.getEntityMode();
        }
        public CacheMode getCacheMode()
        {
            return m_Impl.getCacheMode();
        }
        public void setCacheMode(CacheMode cm)
        {
            m_Impl.setCacheMode(cm);
        }
        public boolean isOpen()
        {
            return m_Impl.isOpen();
        }
        public boolean isConnected()
        {
            return m_Impl.isConnected();
        }
        public FlushMode getFlushMode()
        {
            return m_Impl.getFlushMode();
        }
        public void setFlushMode(FlushMode fm)
        {
            m_Impl.setFlushMode(fm);
        }
        public Connection connection()
        {
            return m_Impl.connection();
        }
        public void flush()
        {
            m_Impl.flush();
        }
        public Query getNamedQuery(String name)
        {
            return m_Impl.getNamedQuery(name);
        }
        public Query getNamedSQLQuery(String name)
        {
            return m_Impl.getNamedSQLQuery(name);
        }        
        public boolean isEventSource()
        {
            return m_Impl.isEventSource();
        }
        public void afterScrollOperation()
        {
            m_Impl.afterScrollOperation();
        }
        public void setFetchProfile(String name)
        {
            m_Impl.setFetchProfile(name);
        }
        public String getFetchProfile()
        {
            return m_Impl.getFetchProfile();
        }
        public JDBCContext getJDBCContext()
        {
            return m_Impl.getJDBCContext();
        }
        public boolean isClosed()
        {
            return m_Impl.isClosed();
        }
        public Criteria createCriteria(Class persistentClass)
        {
            if (m_Impl instanceof Session)
            {
                return ((Session) m_Impl).createCriteria(persistentClass);
            }
            else if (m_Impl instanceof StatelessSession)
            {
                return new CriteriaImpl(persistentClass.getName(), this);
            }
            else
            {
                throw new UnsupportedOperationException();
            }
        }
        public Connection close() throws HibernateException
        {
            if (m_Impl instanceof Session)
            {
                return ((Session) m_Impl).close();
            }
            else if (m_Impl instanceof StatelessSession)
            {
                ((StatelessSession) m_Impl).close();
                return null;
            }
            else
            {
                throw new UnsupportedOperationException();
            }
        }
    }
    
    /**
     * This test succeeds, using a StatelessSession, by implementing a
     * workaround to improve
     * {@link StatelessSessionImpl#getContextEntityIdentifier} to be able
     * to extract the ID from the {@link HibernateProxy}, as
     * {@link SessionImpl#getContextEntityIdentifier} can.
     */
    
    public void testSuccessfulWorkaround() throws Exception
    {
        SessionWrapper s = new SessionWrapper((SessionImplementor)
            getSessions().openStatelessSession());        
        assertList(s.createCriteria(Request.class));
        s.close();
    }
}
