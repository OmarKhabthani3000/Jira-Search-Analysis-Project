//$Id: EntityJoinWalker.java 7652 2005-07-26 05:51:47Z oneovthafew $
package org.hibernate.loader.entity;

import java.util.Collections;
import java.util.Map;

import org.hibernate.FetchMode;
import org.hibernate.LockMode;
import org.hibernate.MappingException;
import org.hibernate.engine.CascadeStyle;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.loader.AbstractEntityJoinWalker;
import org.hibernate.persister.entity.OuterJoinLoadable;
import org.hibernate.type.AssociationType;
import org.hibernate.util.StringHelper;

/**
 * A walker for loaders that fetch entities
 *
 * @see EntityLoader
 * @author Gavin King
 */
public class EntityJoinWalker extends AbstractEntityJoinWalker {
	
	private final LockMode lockMode;

	public EntityJoinWalker(
			OuterJoinLoadable persister, 
			String[] uniqueKey, 
			int batchSize, 
			LockMode lockMode,
			SessionFactoryImplementor factory, 
			Map enabledFilters) 
	throws MappingException {
		super(persister, factory, enabledFilters);

		this.lockMode = lockMode;
		
		StringBuffer whereCondition = whereString( getAlias(), uniqueKey, batchSize );
			//include the discriminator and class-level where, but not filters
		 String filter= persister.filterFragment( getAlias(), enabledFilters);
		 whereCondition.insert( 0, StringHelper.moveAndToBeginning(filter) );
  		initAll( whereCondition.toString(), "", lockMode );
		
	}

	/**
	 * Disable outer join fetching if this loader obtains an
	 * upgrade lock mode
	 */
	protected boolean isJoinedFetchEnabled(AssociationType type, FetchMode config, CascadeStyle cascadeStyle) {
		return lockMode.greaterThan(LockMode.READ) ?
			false :
			super.isJoinedFetchEnabled(type, config, cascadeStyle);
	}

	public String getComment() {
		return "load " + getPersister().getEntityName();
	}
	
}