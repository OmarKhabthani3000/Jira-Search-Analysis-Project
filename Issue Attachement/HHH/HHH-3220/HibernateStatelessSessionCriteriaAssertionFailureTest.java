package org.wfp.rita.test.hibernate;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.CacheMode;
import org.hibernate.Criteria;
import org.hibernate.EntityMode;
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
import org.hibernate.cfg.Settings;
import org.hibernate.collection.PersistentCollection;
import org.hibernate.engine.EntityKey;
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
import org.hibernate.type.Type;
import org.wfp.rita.db.IdentifiedByInteger;
import org.wfp.rita.test.base.HibernateTestBase;

/**
 * This is a test for 
 * {@link http://opensource.atlassian.com/projects/hibernate/browse/HHH-3220},
 * which currently prevents us from using StatelessSession to retrieve large
 * numbers of objects for synchronization without loading them into the
 * Hibernate cache, which would eventually clog the cache and exhaust
 * the available memory.
 * 
 * When performing a query in a stateless session, the query loads objects
 * in a two-phase process in which a temporary persistence context is
 * populated with empty objects in the first phase, then the objects'
 * member data are read from the database in the second phase. If one
 * of the objects contains an association or a collection, it performs
 * a recursive call to the session's get() method. The get() method clears
 * the temporary persistence context, so if the parent object contains any
 * other associations to be read in the second phase, Hibernate throws an
 * assertion because they are not found in the persistence context.
 * 
 * @see http://opensource.atlassian.com/projects/hibernate/browse/HHH-3220
 * 
 * Change HibernateTestBase to org.hibernate.test.annotations.TestCase to
 * run under Hibernate.
 *
 * @author Chris Wilson <chris+hibernate@aptivate.org>
 */
public class HibernateStatelessSessionCriteriaAssertionFailureTest
extends HibernateTestBase
{
    @Entity
    @Table(name="contact")
    private static class Contact extends IdentifiedByInteger
    {
        /*
        public static class Id extends SiteKey
        { 
            // constructor for Hibernate
            public Id() { }

            // constructor for new records with ID to be assigned
            public Id(Integer siteId)
            {
                super(siteId);
            }

            // constructor for existing records with ID to be loaded
            public Id(Integer siteId, Integer id)
            {
                super(siteId, id);
            }
            
            public Id(String compositeKeyHash)
            {
                super(compositeKeyHash);
            }
            
            @Override public Class getIdentifiedClass()
            {
                return Contact.class;
            }
        }
        
        @EmbeddedId
        @GenericGenerator(name="generator",
            strategy="org.wfp.rita.db.CompositeKeyIdentityGenerator")
        @GeneratedValue(generator="generator")    
        @AttributeOverride(name="siteId", column=@Column(name="owner_site_id"))
        private Contact.Id id;
        */
        
        @Id
        @GeneratedValue
        private Integer id;
        
        /*
        @ManyToOne
        @JoinColumn(name="project_id")
        private Project project;

        @ManyToOne
        @JoinColumn(name="owner_site_id", insertable=false, updatable=false)
        private Site ownerSite;
        
        @ManyToOne
        @JoinColumns({
            @JoinColumn(name="project_id", referencedColumnName="project_id", 
                insertable=false, updatable=false),
            @JoinColumn(name="owner_site_id", referencedColumnName="site_id",
                insertable=false, updatable=false)
        })
        private ProjectSite projectSite;
        */
        
        @ManyToOne
        @JoinColumn(name="country_id")    
        private Country country;
        
        @ManyToOne(fetch=FetchType.EAGER)
        @JoinColumn(name="org_id")    
        private Org org;
        
        @Column(name="title", length=40, nullable=true)
        private String title;
        
        @Column(name="name", length=255, nullable=false)
        private String name;
        
        @Column(name="job_title", length=255, nullable=true)
        private String jobTitle;
        
        @Column(name="address", length=255, nullable=true)
        private String address;
        
        @Column(name="email1", length=255, nullable=true)
        private String email1;

        @Column(name="email2", length=255, nullable=true)
        private String email2;
        
        @Column(name="email3", length=255, nullable=true)
        private String email3;
        
        @Column(name="phone1", length=255, nullable=true)
        private String phone1;
        
        @Column(name="phone2", length=255, nullable=true)
        private String phone2;
        
        @Column(name="phone3", length=255, nullable=true)
        private String phone3;
           
        // Property accessors

        public Integer getId() {
            return this.id;
        }
        
        public void setId(Integer id) {
            this.id = id;
        }

        /*
        public Project getProject() {
            return this.project;
        }
        
        public void setProject(Project project) {
            this.project = project;
        }
        */
        
        public Country getCountry() {
            return this.country;
        }
        
        public void setCountry(Country country) {
            this.country = country;
        }

        public Org getOrg() {
            return this.org;
        }
        
        public void setOrg(Org org) {
            this.org = org;
        }

        public String getTitle() {
            return this.title;
        }
        
        public void setTitle(String title) {
            this.title = title;
        }

        public String getName() {
            return this.name;
        }
        
        public void setName(String name) {
            this.name = name;
        }

        public String getJobTitle() {
            return this.jobTitle;
        }
        
        public void setJobTitle(String position) {
            this.jobTitle = position;
        }

        public String getAddress() {
            return this.address;
        }
        
        public void setAddress(String address) {
            this.address = address;
        }

        public String getEmail1() {
            return this.email1;
        }
        
        public void setEmail1(String email1) {
            this.email1 = email1;
        }

        public String getEmail2() {
            return this.email2;
        }
        
        public void setEmail2(String email2) {
            this.email2 = email2;
        }

        public String getEmail3() {
            return this.email3;
        }
        
        public void setEmail3(String email3) {
            this.email3 = email3;
        }

        public String getPhone1() {
            return this.phone1;
        }
        
        public void setPhone1(String phone1) {
            this.phone1 = phone1;
        }

        public String getPhone2() {
            return this.phone2;
        }
        
        public void setPhone2(String phone2) {
            this.phone2 = phone2;
        }

        public String getPhone3() {
            return this.phone3;
        }
        
        public void setPhone3(String phone3) {
            this.phone3 = phone3;
        }
    }
    
    @Entity
    @Table(name="org")

    private static class Org extends IdentifiedByInteger
    {
        @Id
        @GeneratedValue
        private Integer id;
        
        @ManyToOne
        @JoinColumn(name="country_id")
        private Country country;
        
        /*
        @ManyToOne
        @JoinColumn(name="org_type_code")
        private OrgType orgType;
        */
        
        @Column(name="name", length=100, nullable=false, unique=true)
        private String name;
        
        @Column(name="abrv", length=40, unique=true)
        private String abrv;
        
        @Column(name="is_intl", nullable=false, columnDefinition="smallint")
        private Boolean isIntl;
        
        @Column(name="is_donor", nullable=false, columnDefinition="smallint")
        private Boolean isDonor;
        
        @Column(name="website")
        private String website;
        
        @Column(name="wings_idnf", length=40)
        private String wingsIdnf;
        
        @Column(name="compas_idnf", length=40)
        private String compasIdnf;
        
        @Column(name="dacota_idnf")
        private Integer dacotaIdnf;
        
        @OneToMany(mappedBy="org")
        private Set<Contact> contacts = new HashSet<Contact>(0);

        // Property accessors

        public Integer getId() {
            return this.id;
        }
        
        public void setId(Integer id) {
            this.id = id;
        }

        public Country getCountry() {
            return this.country;
        }
        
        public void setCountry(Country country) {
            this.country = country;
        }

        /*
        public OrgType getOrgType() {
            return this.orgType;
        }
        
        public void setOrgType(OrgType orgType) {
            this.orgType = orgType;
        }
        */
        
        public String getName() {
            return this.name;
        }
        
        public void setName(String name) {
            this.name = name;
        }

        public String getAbrv() {
            return this.abrv;
        }
        
        public void setAbrv(String abrv) {
            this.abrv = abrv;
        }

        public Boolean getIsIntl() {
            return this.isIntl;
        }
        
        public void setIsIntl(Boolean isIntl) {
            this.isIntl = isIntl;
        }

        public Boolean getIsDonor() {
            return this.isDonor;
        }
        
        public void setIsDonor(Boolean isDonor) {
            this.isDonor = isDonor;
        }

        public String getWebsite() {
            return this.website;
        }
        
        public void setWebsite(String website) {
            this.website = website;
        }

        public String getWingsIdnf() {
            return this.wingsIdnf;
        }
        
        public void setWingsIdnf(String wingsIdnf) {
            this.wingsIdnf = wingsIdnf;
        }

        public String getCompasIdnf() {
            return this.compasIdnf;
        }
        
        public void setCompasIdnf(String compasIdnf) {
            this.compasIdnf = compasIdnf;
        }

        public Integer getDacotaIdnf() {
            return this.dacotaIdnf;
        }
        
        public void setDacotaIdnf(Integer dacotaIdnf) {
            this.dacotaIdnf = dacotaIdnf;
        }

        public Set<Contact> getContacts()
        {
            return this.contacts;
        }
        
        public void setContacts(Set<Contact> contacts)
        {
            this.contacts = contacts;
        }
        
        @Override public String toString()
        {
            return "Org(" + id + "," + name + ")";
        }
    }
    
    @Entity
    @Table(name="country")

    private static class Country
    extends IdentifiedByInteger
    {
        @Id
        @GeneratedValue
        @Column(name="id")
        private Integer id;

        /*
        @ManyToOne
        @JoinColumn(name="subregion_id", nullable=false)
        private Subregion subregion;
        */
        
        @Column(name="name", length=40, nullable=false, unique=true)
        private String name;

        @Column(name="official_name", length=40, nullable=false, unique=true)
        private String officialName;

        @Column(name="alternate_name")
        private String alternateName;

        /*
        @ManyToOne
        @JoinColumn(name="country_type_code")
        private CountryType countryType;

        @ManyToOne
        @JoinColumn(name="currency_id", nullable=false)
        private Currency currency;
        */
        
        @Column(name="iso2_idnf", length=2, nullable=false, unique=true)
        private String iso2Idnf;
        
        @Column(name="iso3_idnf", length=3, nullable=false, unique=true)
        private String iso3Idnf;
        
        @Column(name="isonumb_idnf", nullable=false)
        private Integer isonumbIdnf;
        
        @Column(name="latitude", precision=8, scale=5)
        private Double latitude;
        
        @Column(name="longitude", precision=8, scale=5)
        private Double longitude;
        
        @Column(name="timezone", precision=3, scale=1, nullable=false)
        private Double timezone;
        
        @Column(name="capital_city", length=40)
        private String capitalCity;

        /*
        @OneToMany(mappedBy="country")
        private Set<Site> sites = new HashSet(0);
        */
        
        // Property accessors

        public Integer getId() {
            return this.id;
        }
        
        public void setId(Integer id) {
            this.id = id;
        }

        /*
        public Currency getCurrency() {
            return this.currency;
        }
        
        public void setCurrency(Currency currency) {
            this.currency = currency;
        }

        public CountryType getCountryType() {
            return this.countryType;
        }
        
        public void setCountryType(CountryType countryType) {
            this.countryType = countryType;
        }
        
        public Subregion getSubregion() {
            return this.subregion;
        }
        
        public void setSubregion(Subregion subregion) {
            this.subregion = subregion;
        }
        */

        public String getName() {
            return this.name;
        }
        
        public void setName(String name) {
            this.name = name;
        }

        public String getOfficialName() {
            return this.officialName;
        }
        
        public void setOfficialName(String officialName) {
            this.officialName = officialName;
        }

        public String getAlternateName() {
            return this.alternateName;
        }
        
        public void setAlternateName(String alternateName) {
            this.alternateName = alternateName;
        }

        public String getIso2Idnf() {
            return this.iso2Idnf;
        }
        
        public void setIso2Idnf(String iso2Idnf) {
            this.iso2Idnf = iso2Idnf;
        }

        public String getIso3Idnf() {
            return this.iso3Idnf;
        }
        
        public void setIso3Idnf(String iso3Idnf) {
            this.iso3Idnf = iso3Idnf;
        }

        public Integer getIsonumbIdnf() {
            return this.isonumbIdnf;
        }
        
        public void setIsonumbIdnf(Integer isonumbIdnf) {
            this.isonumbIdnf = isonumbIdnf;
        }

        public Double getLatitude() {
            return this.latitude;
        }
        
        public void setLatitude(Double latitude) {
            this.latitude = latitude;
        }

        public Double getLongitude() {
            return this.longitude;
        }
        
        public void setLongitude(Double longitude) {
            this.longitude = longitude;
        }

        public Double getTimezone() {
            return this.timezone;
        }
        
        public void setTimezone(Double timezone) {
            this.timezone = timezone;
        }

        public String getCapitalCity() {
            return this.capitalCity;
        }
        
        public void setCapitalCity(String capitalCity) {
            this.capitalCity = capitalCity;
        }

        /*
        public Set getSites() {
            return this.sites;
        }
        
        public void setSites(Set sites) {
            this.sites = sites;
        }
        */
        
        @Override public String toString()
        {
            return "Country(" + id + "," + name + ")";
        }
    }
    
    protected Class[] getMappings()
    {
        return new Class[]{Contact.class, Country.class, Org.class,
            /*
            Site.class, Project.class, ProjectSite.class, CountryType.class,
            Currency.class, Subregion.class, OrgType.class, BuildingType.class,
            DistributionType.class, PortType.class, ProjectPurpose.class,
            Region.class, ProjectClassify.class, ExchangeRate.class,
            UserRole.class, Role.class, User.class, 
            org.wfp.rita.dao.Contact.class, Journey.class, RouteCost.class,
            Vehicle.class, RouteDefinition.class, TransportSpecial.class,
            VehicleModel.class, VehicleCategory.class, TransportMode.class,
            ListActionRole.class, ListAction.class, ScreenCode.class,
            ListActionViewpoint.class, Viewpoint.class,
            UserParameter.class, JourneyComment.class
            */
            };
    }

    public void setUp() throws Exception
    {
        super.setUp();
        
        Transaction transaction = null;
        
        try
        {
            Session session = openSession();
            transaction = session.beginTransaction();

            /*
            Region americas = new Region();
            americas.setName("North America");
            americas.setIsDeleted(false);
            americas.setRecordVersion(0L);
            americas.setIsSystem(true);
            session.save(americas);

            Subregion northAmerica = new Subregion();
            northAmerica.setName("North America");
            northAmerica.setIsDeleted(false);
            northAmerica.setRecordVersion(0L);
            northAmerica.setRegion(americas);
            session.save(northAmerica);
            
            Currency dollar = new Currency();
            dollar.setName("USD");
            dollar.setIso3Idnf("USD");
            dollar.setIsDeleted(false);
            dollar.setRecordVersion(0L);
            session.save(dollar);
            */
            
            Country usa = new Country();
            usa.setName("USA");
            usa.setIso2Idnf("US");
            usa.setIso3Idnf("USA");
            usa.setIsonumbIdnf(1);
            usa.setOfficialName("United States of America");
            // usa.setSubregion(northAmerica);
            usa.setIsDeleted(false);
            usa.setRecordVersion(0L);
            // usa.setCurrency(dollar);
            usa.setTimezone(-5.0);
            session.save(usa);
            
            Org DISNEY = new Org();
            DISNEY.setName("Disney");
            DISNEY.setIsDeleted(false);
            DISNEY.setRecordVersion(0L);
            DISNEY.setIsDonor(false);
            DISNEY.setIsIntl(true);
            DISNEY.setCountry(usa);
            session.save(DISNEY);
            
            Contact DONALD_DUCK = new Contact();
            DONALD_DUCK.setName("Donald");
            DONALD_DUCK.setOrg(DISNEY);
            DONALD_DUCK.setIsDeleted(false);
            DONALD_DUCK.setRecordVersion(0L);
            ((SessionImpl) session).save(DONALD_DUCK, 1);
            
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

        SessionFactoryImplementor sfi = (SessionFactoryImplementor) getSessions();
        Settings settings = sfi.getSettings();
        Field maxFetchDepth = Settings.class.getDeclaredField("maximumFetchDepth");
        maxFetchDepth.setAccessible(true);
        maxFetchDepth.set(settings, 1);    
    }

    private void assertList(Criteria c)
    {
        // c.add(Restrictions.gt("recordVersion", 0L));
        // c.add(Restrictions.le("recordVersion", 1L));
        List<Contact> results = c.list();
        assertEquals(results.toString(), 1, results.size());
    }

    /**
     * This test passes, using a normal Session.
     * 
     * @throws Exception
     */
    public void testSuccessful()
    {
        Session s = getSessions().openSession();
        assertList(s.createCriteria(Contact.class));
        s.close();
    }

    /**
     * This test fails, using a StatelessSession
     */
    public void testFailing() throws Exception
    {
        StatelessSession s = getSessions().openStatelessSession();
        assertList(s.createCriteria(Contact.class));
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
        /**
         * Overridden to work around the bug.
         */
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
            /** patch starts here */
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
        public Serializable getContextEntityIdentifier(Object object)
        {
            return m_Impl.getContextEntityIdentifier(object);
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
     * workaround to not clear the cache 
     * {@link org.hibernate.impl.StatelessSessionImpl#internalLoad()}.
     */
    public void testWorkaround() throws Exception
    {
        SessionWrapper s = new SessionWrapper((SessionImplementor)
            getSessions().openStatelessSession());        
        assertList(s.createCriteria(Contact.class));
        s.close();
    }
}
