//$Id: CriteriaImpl.java,v 1.8 2004/08/18 00:28:42 oneovthafew Exp $
package org.hibernate.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.QueryException;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.persister.PropertyMapping;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.hibernate.transform.RootEntityResultTransformer;
import org.hibernate.type.AssociationType;
import org.hibernate.type.Type;
import org.hibernate.util.StringHelper;

/**
 * Implementation of the <tt>Criteria</tt> interface
 * @author Gavin King
 */
public class CriteriaImpl implements Criteria {

	private List criteria = new ArrayList();
	private List orderings = new ArrayList();
	private Map fetchModes = new HashMap();
	private Set nonVisitedFetchModes = new HashSet();
	
	private Map associationPathByAlias = new HashMap();
	private Map aliasByAssociationPath = new HashMap();
	private Map classByAlias = new HashMap();
	private Map lockModes = new HashMap();
	private Integer maxResults;
	private Integer firstResult;
	private Integer timeout;
	private Integer fetchSize;
	private boolean cacheable;
	private String cacheRegion;
	private boolean forceCacheRefresh;
	private String comment;
	//private Class persistentClass;
	private SessionImpl session;
	private ResultTransformer resultTransformer = new RootEntityResultTransformer();

	private int counter=0;

	private String generateAlias() {
		return "x" + counter++ + '_';
	}

	public final class Subcriteria implements Criteria {

		private String rootAlias;
		private String rootPath;

		private Subcriteria(String rootAlias, String rootPath) {
			this.rootAlias = rootAlias;
			this.rootPath = rootPath;
		}

		public Criteria add(Criterion expression) {
			CriteriaImpl.this.add(rootAlias, expression);
			return this;
		}

		public Criteria createAlias(String associationPath, String alias)
			throws HibernateException {
			CriteriaImpl.this.createAlias(rootAlias, associationPath, alias);
			return this;
		}

		public Criteria addOrder(Order order) {
			throw new UnsupportedOperationException("subcriteria cannot be ordered");
		}

		public Criteria setCacheable(boolean cacheable) {
			CriteriaImpl.this.setCacheable(cacheable);
			return this;
		}

		public Criteria setCacheRegion(String cacheRegion) {
			CriteriaImpl.this.setCacheRegion(cacheRegion);
			return this;
		}

		public Criteria setForceCacheRefresh(boolean forceCacheRefresh) {
			CriteriaImpl.this.setForceCacheRefresh(forceCacheRefresh);
			return this;
		}


		public Criteria createCriteria(String associationPath)
			throws HibernateException {
			return CriteriaImpl.this.createCriteriaAt(rootAlias, associationPath);
		}

		public List list() throws HibernateException {
			return CriteriaImpl.this.list();
		}

		public ScrollableResults scroll() throws HibernateException {
			return CriteriaImpl.this.scroll();
		}

		public ScrollableResults scroll(ScrollMode scrollMode) throws HibernateException {
			return CriteriaImpl.this.scroll(scrollMode);
		}

		public Object uniqueResult() throws HibernateException {
			return CriteriaImpl.this.uniqueResult();
		}

		public Criteria setFetchMode(String associationPath, FetchMode mode)
			throws HibernateException {
			CriteriaImpl.this.setFetchMode( StringHelper.qualify(rootPath, associationPath), mode);
			return this;
		}

		public Criteria setFirstResult(int firstResult) {
			CriteriaImpl.this.setFirstResult(firstResult);
			return this;
		}

		public Criteria setMaxResults(int maxResults) {
			CriteriaImpl.this.setMaxResults(maxResults);
			return this;
		}

		public Criteria setTimeout(int timeout) {
			CriteriaImpl.this.setTimeout(timeout);
			return this;
		}

		public Criteria setFetchSize(int fetchSize) {
			CriteriaImpl.this.setFetchSize(fetchSize);
			return this;
		}

		public Class getCriteriaClass() {
			return CriteriaImpl.this.getCriteriaClass(rootAlias);
		}

		public Class getCriteriaClass(String alias) {
			return CriteriaImpl.this.getCriteriaClass(alias);
		}

		public String getCriteriaEntityName(String alias) {
			return CriteriaImpl.this.getCriteriaEntityName(alias);
		}

		public String getCriteriaEntityName() {
			return CriteriaImpl.this.getCriteriaEntityName(rootAlias);
		}

		public Criteria createCriteria(String associationPath, String alias)
			throws HibernateException {
			return CriteriaImpl.this.createCriteriaAt(rootAlias, associationPath, alias);
		}

		public Criteria returnMaps() {
			CriteriaImpl.this.returnMaps();
			return this;
		}

		public Criteria returnRootEntities() {
			CriteriaImpl.this.returnRootEntities();
			return this;
		}

		public Criteria setLockMode(LockMode lockMode) {
			CriteriaImpl.this.setLockMode(rootAlias, lockMode);
			return this;
		}

		public Criteria setLockMode(String alias, LockMode lockMode) {
			CriteriaImpl.this.setLockMode(alias, lockMode);
			return this;
		}

		public Criteria setResultTransformer(ResultTransformer resultProcessor) {
			CriteriaImpl.this.setResultTransformer(resultProcessor);
			return this;
		}

		public Criteria setComment(String comment) {
			CriteriaImpl.this.setComment(comment);
			return this;
		}

	}

	public Criteria setMaxResults(int maxResults) {
		this.maxResults = new Integer(maxResults);
		return this;
	}

	public Criteria setFirstResult(int firstResult) {
		this.firstResult = new Integer(firstResult);
		return this;
	}

	public Integer getFetchSize() {
		return fetchSize;
	}

	public Criteria setFetchSize(int fetchSize) {
		this.fetchSize = new Integer(fetchSize);
		return this;
	}

	public Criteria setTimeout(int timeout) {
		this.timeout = new Integer(timeout);
		return this;
	}

	public Criteria add(Criterion expression) {
		add(Criteria.ROOT_ALIAS, expression);
		return this;
	}

	public Integer getMaxResults() {
		return maxResults;
	}
	public Integer getFirstResult() {
		return firstResult;
	}
	public Integer getTimeout() {
		return timeout;
	}

	public CriteriaImpl(String entityName, SessionImpl session) {
		//this.persistentClass = null; //TODO!!!!!!!
		this.session = session;
		this.classByAlias.put(Criteria.ROOT_ALIAS, entityName);
		this.cacheable = false;
	}

	/**
	 * Copy all the internal attributes of the given CrtieriaImpl
	 * except alter the root persistent class type to be the given one.
	 */
	public CriteriaImpl(String entityName, CriteriaImpl original) {

		this.classByAlias = original.classByAlias;
		this.classByAlias.put(Criteria.ROOT_ALIAS, entityName);

		this.criteria = original.criteria;
		this.orderings = original.orderings;
		this.fetchModes = original.fetchModes;
		this.nonVisitedFetchModes = original.nonVisitedFetchModes;
		this.associationPathByAlias = original.associationPathByAlias;
		this.aliasByAssociationPath = original.aliasByAssociationPath;
		this.lockModes = original.lockModes;
		this.maxResults = original.maxResults;
		this.firstResult = original.firstResult;
		this.timeout = original.timeout;
		this.fetchSize = original.fetchSize;
		this.session = original.session;
		this.resultTransformer = original.resultTransformer;
		this.counter = original.counter;
		this.cacheable = original.cacheable;
		this.cacheRegion = original.cacheRegion;
		this.comment = original.comment;
	}

	public List list() throws HibernateException {
		return session.find(this);
	}
	
	public ScrollableResults scroll() {
		return session.scroll(this, ScrollMode.SCROLL_INSENSITIVE);
	}

	public ScrollableResults scroll(ScrollMode scrollMode) {
		return session.scroll(this, scrollMode);
	}

	void checkFetchModes() {
		if(! nonVisitedFetchModes.isEmpty()) {
			throw new QueryException("Invalid path : " + (String) nonVisitedFetchModes.iterator().next());
		}
	}
	
	public boolean getCacheable() {
		return this.cacheable;
	}

	public String getCacheRegion() {
		return this.cacheRegion;
	}

	public Criteria setCacheable(boolean cacheable) {
		this.cacheable = cacheable;
		return this;
	}

	public Criteria setCacheRegion(String cacheRegion) {
		this.cacheRegion = cacheRegion.trim();
		return this;
	}

	public Iterator iterateExpressionEntries() {
		return criteria.iterator();
	}

	public Iterator iterateOrderings() {
		return orderings.iterator();
	}

	public Map getAliasClasses() {
		return classByAlias;
	}

	public String toString() {
		return "CriteriaImpl(" + getCriteriaEntityName() + criteria.toString() + ')';
	}

	public Criteria addOrder(Order ordering) {
		orderings.add(ordering);
		return this;
	}

	public FetchMode getFetchMode(String path) {
		nonVisitedFetchModes.remove(path);
		return (FetchMode) fetchModes.get(path);
	}

	public Criteria setFetchMode(String associationPath, FetchMode mode) {
		fetchModes.put(associationPath, mode);
		nonVisitedFetchModes.add(associationPath);
		return this;
	}

	public Criteria createAlias(String associationPath, String alias) throws HibernateException {
		createAlias(ROOT_ALIAS, associationPath, alias);
		return this;
	}

	private void createAlias(String rootAlias, String associationPath, String alias) throws HibernateException {

		String testAlias = StringHelper.root(associationPath);
		if ( classByAlias.containsKey(testAlias) ) {
			rootAlias = testAlias;
			associationPath = associationPath.substring( rootAlias.length() + 1 );
		}

		String rootPath = (String) associationPathByAlias.get(rootAlias);
		String wholeAssociationPath;
		if (rootPath==null) {
			if ( !ROOT_ALIAS.equals(rootAlias) ) throw new HibernateException("unknown alias: " + rootAlias);
			wholeAssociationPath = associationPath;
		}
		else {
			wholeAssociationPath = StringHelper.qualify(rootPath, associationPath);
		}

		Object oldPath = associationPathByAlias.put(alias, wholeAssociationPath);
		if (oldPath!=null) throw new HibernateException("alias already defined: " + alias);
		Object oldAlias = aliasByAssociationPath.put(wholeAssociationPath, alias);
		if (oldAlias!=null) throw new HibernateException("association already joined: " + wholeAssociationPath);
		classByAlias.put( alias, getClassForPath(rootAlias, associationPath) );
	}

	public boolean isJoin(String path) {
		return aliasByAssociationPath.containsKey(path);
	}

	public String getAlias(String associationPath) {
		return (String) aliasByAssociationPath.get(associationPath);
	}

	public Criteria add(String alias, Criterion expression) {
		criteria.add( new CriterionEntry(expression, alias) );
		return this;
	}

	/*private Type getType(PropertyMapping pm, String associationPath, SessionFactoryImplementor factory)
	throws HibernateException {
		//whoa! reuseable code buried all the way down here!
		String[] pathComponents = StringHelper.split(".", associationPath);
		StringBuffer subpath = new StringBuffer( associationPath.length() );
		Type type = null;
		for ( int i=0; i<pathComponents.length; i++ ) {
			subpath.append( pathComponents[i] );
			type = pm.toType( subpath.toString() );
			if ( i<pathComponents.length-1 && type.isAssociationType() ) {
				return getType(
					( (AssociationType) type ).getJoinable(factory),
					associationPath.substring( subpath.length()+1 ),
					factory
				);
			}
			subpath.append(".");
		}
		return type;
	}*/

	public String getClassForPath(String rootAlias, String associationPath) throws HibernateException {
		SessionFactoryImplementor factory = session.getFactory();
		String clazz = (String) classByAlias.get(rootAlias);
		//Type type = getType( (Joinable) factory.getPersister(clazz), associationPath, factory );
		Type type = ( (PropertyMapping) factory.getEntityPersister(clazz) ).toType(associationPath);
		if ( !type.isAssociationType() ) throw new QueryException("not an association path: " + associationPath);
		String className = ( (AssociationType) type ).getAssociatedEntityName(factory);
		return className;
		//return factory.getPersister(className).getMappedClass();
	}

	public static final class CriterionEntry {
		private final Criterion criterion;
		private final String alias;

		private CriterionEntry(Criterion criterion, String alias) {
			this.alias = alias;
			this.criterion = criterion;
		}

		public Criterion getCriterion() {
			return criterion;
		}

		public String getAlias() {
			return alias;
		}

		public String toString() {
			return alias + ": " + criterion;
		}
	}

	public Criteria createCriteria(String associationPath) throws HibernateException {
		return createCriteriaAt( ROOT_ALIAS, associationPath );
	}

	private Criteria createCriteriaAt(String rootAlias, String associationPath)  throws HibernateException {
		return createCriteriaAt( rootAlias, associationPath, generateAlias() );
	}

	private Criteria createCriteriaAt(String rootAlias, String associationPath, String alias)  throws HibernateException {
		String testAlias = StringHelper.root(associationPath);
		if ( classByAlias.containsKey(testAlias) ) {
			rootAlias = testAlias;
			associationPath = associationPath.substring( rootAlias.length() + 1 );
		}

		createAlias(rootAlias, associationPath, alias);
		return new Subcriteria(alias, associationPath);
	}

	public Object uniqueResult() throws HibernateException {
		return AbstractQueryImpl.uniqueElement( list() );
	}

	public Class getCriteriaClass() {
		return getCriteriaClass(ROOT_ALIAS);
	}

	public Class getCriteriaClass(String alias) {
		return (Class) classByAlias.get(alias);
	}

	public String getCriteriaEntityName(String alias) {
		return (String) classByAlias.get(alias);
	}

	public String getCriteriaEntityName() {
		return getCriteriaEntityName(ROOT_ALIAS);
	}

	public Criteria createCriteria(String associationPath, String alias)
		throws HibernateException {
		return createCriteriaAt(ROOT_ALIAS, associationPath, alias);
	}

	public Criteria returnMaps() {
		setResultTransformer( new AliasToEntityMapResultTransformer() );
		return this;
	}

	public Criteria returnRootEntities() {
		setResultTransformer( new RootEntityResultTransformer() );
		return this;
	}

	public Criteria setLockMode(LockMode lockMode) {
		return setLockMode(Criteria.ROOT_ALIAS, lockMode);
	}

	public Criteria setLockMode(String alias, LockMode lockMode) {
		lockModes.put(alias, lockMode);
		return this;
	}

	public Map getLockModes() {
		return lockModes;
	}

	public ResultTransformer getResultTransformer() {
		return resultTransformer;
	}

	public Criteria setResultTransformer(ResultTransformer tupleMapper) {
		this.resultTransformer = tupleMapper;
		return this;
	}

	public Criteria setComment(String comment) {
		this.comment = comment;
		return this;
	}

	public String getComment() {
		return comment;
	}

	public boolean isForceCacheRefresh() {
		return forceCacheRefresh;
	}

	public Criteria setForceCacheRefresh(boolean forceCacheRefresh) {
		this.forceCacheRefresh = forceCacheRefresh;
		return this;
	}

}
