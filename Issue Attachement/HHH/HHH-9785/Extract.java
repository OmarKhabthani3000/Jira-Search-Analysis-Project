============================================
package org.hibernate.loader.collection;
....

/**
 * Implements subselect fetching for a collection
 * @author Gavin King
 */
public class SubselectCollectionLoader extends BasicCollectionLoader {
	...
	public SubselectCollectionLoader(
			QueryableCollection persister, 
			String subquery,
			Collection entityKeys,
			QueryParameters queryParameters,
			Map<String, int[]> namedParameterLocMap,
			SessionFactoryImplementor factory, 
			LoadQueryInfluencers loadQueryInfluencers) throws MappingException {
		super( persister, 1, subquery, factory, loadQueryInfluencers );

		keys = new Serializable[ entityKeys.size() ];
		Iterator iter = entityKeys.iterator();
		int i=0;
		while ( iter.hasNext() ) {
			keys[i++] = ( (EntityKey) iter.next() ).getIdentifier();
		}
		
		this.namedParameters = queryParameters.getNamedParameters();
		this.types = queryParameters.getFilteredPositionalParameterTypes();
		this.values = queryParameters.getFilteredPositionalParameterValues();
		this.namedParameterLocMap = namedParameterLocMap;
		
	}
	
	
	}
============================================
public abstract class AbstractCollectionPersister
		implements CollectionMetadata, SQLLoadableCollection {

	...
	protected CollectionInitializer getAppropriateInitializer(Serializable key, SessionImplementor session) {
		if ( queryLoaderName != null ) {
			// if there is a user-specified loader, return that
			// TODO: filters!?
			return initializer;
		}
		CollectionInitializer subselectInitializer = getSubselectInitializer( key, session );

	...
	@Override
	public void initialize(Serializable key, SessionImplementor session) throws HibernateException {
		getAppropriateInitializer( key, session ).initialize( key, session );
	}	
============================================
package org.hibernate.loader.collection;
...
public class SubselectCollectionLoader extends BasicCollectionLoader {
	
	...
	@Override
	public void initialize(Serializable id, SessionImplementor session)
			throws HibernateException {
		loadCollectionSubselect( 
				session, 
				keys, 
				values,
				types,
				namedParameters,
				getKeyType() 
		);
	}		
============================================
	
package org.hibernate.loader;

...
public abstract class Loader {


	/**
	 * Called by subclasses that batch initialize collections
	 */
	protected final void loadCollectionSubselect(
			final SessionImplementor session,
			final Serializable[] ids,
			final Object[] parameterValues,
			final Type[] parameterTypes,
			final Map<String, TypedValue> namedParameters,
			final Type type) throws HibernateException {

		Type[] idTypes = new Type[ids.length];
		Arrays.fill( idTypes, type );
		try {
			doQueryAndInitializeNonLazyCollections( session,
					new QueryParameters( parameterTypes, parameterValues, namedParameters, ids ),
					true
				);
		}
		catch ( SQLException sqle ) {
			throw factory.getSQLExceptionHelper().convert(
					sqle,
					"could not load collection by subselect: " +
					MessageHelper.collectionInfoString( getCollectionPersisters()[0], ids, getFactory() ),
					getSQLString()
				);
		}
	}	