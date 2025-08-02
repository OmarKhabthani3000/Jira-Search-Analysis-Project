//$Id: JoinHelper.java,v 1.8 2005/02/13 11:49:58 oneovthafew Exp $
package org.hibernate.engine;

import org.hibernate.persister.entity.Joinable;
import org.hibernate.persister.entity.OuterJoinLoadable;
import org.hibernate.persister.entity.PropertyMapping;
import org.hibernate.type.AssociationType;
import org.hibernate.util.ArrayHelper;
import org.hibernate.util.StringHelper;

/**
 * @author Gavin King
 */
public final class JoinHelper {
	
	private JoinHelper() {}
	
	/**
	 * Get the aliased columns of the owning entity which are to 
	 * be used in the join
	 */
	public static String[] getAliasedLHSColumnNames(
			AssociationType type, 
			String alias, 
			int property, 
			OuterJoinLoadable lhsPersister,
			Mapping mapping
	) {
		return getAliasedLHSColumnNames(type, alias, property, 0, lhsPersister, mapping);
	}
	
	/**
	 * Get the columns of the owning entity which are to 
	 * be used in the join
	 */
	public static String[] getLHSColumnNames(
			AssociationType type, 
			int property, 
			OuterJoinLoadable lhsPersister,
			Mapping mapping
	) {
		return getLHSColumnNames(type, property, 0, lhsPersister, mapping);
	}
	
	/**
	 * Get the aliased columns of the owning entity which are to 
	 * be used in the join
	 */
	public static String[] getAliasedLHSColumnNames(
			AssociationType type, 
			String alias, 
			int property, 
			int begin, 
			OuterJoinLoadable lhsPersister,
			Mapping mapping
	) {
		if ( type.useLHSPrimaryKey() ) {
			return StringHelper.qualify( alias, lhsPersister.getIdentifierColumnNames() );
		}
		else {
			String propertyName = type.getLHSPropertyName();
			if (propertyName==null) {
				return ArrayHelper.slice( 
						lhsPersister.toColumns(alias, property), 
						begin, 
						type.getColumnSpan(mapping) 
				);
			}
			else {
				return ( (PropertyMapping) lhsPersister ).toColumns(alias, propertyName); //bad cast
			}
		}
	}
	
	/**
	 * Get the columns of the owning entity which are to 
	 * be used in the join
	 */
	public static String[] getLHSColumnNames(
			AssociationType type, 
			int property, 
			int begin, 
			OuterJoinLoadable lhsPersister,
			Mapping mapping
	) {
		if ( type.useLHSPrimaryKey() ) {
			//return lhsPersister.getSubclassPropertyColumnNames(property);
			return lhsPersister.getIdentifierColumnNames();
		}
		else {
			String propertyName = type.getLHSPropertyName();
			if (propertyName==null) {
				//slice, to get the columns for this component
				//property
				return ArrayHelper.slice( 
						lhsPersister.getSubclassPropertyColumnNames(property),
						begin, 
						type.getColumnSpan(mapping) 
				);
			}
			else {
				//property-refs for association defined on
				//component are not supported, so no need to 
				//slice
				return lhsPersister.getPropertyColumnNames(propertyName);
			}
		}
	}
	
	public static String getLHSTableName(
		AssociationType type, 
		int property, 
		OuterJoinLoadable lhsPersister
	) {
		if ( type.useLHSPrimaryKey() ) {
			return lhsPersister.getTableName();
		}
		else {
			String propertyName = type.getLHSPropertyName();
			if (propertyName==null) {
				return lhsPersister.getSubclassPropertyTableName(property);
			}
			//handle a property-ref
			//If the propertyName does is not null, the property may not exist on the current Class. (joined-subclass)  
			//Check the subclasses for its existence, if it is on the subclass, get the table name for the subclass. 
			//The type.getLHSPropertyName() returns the property-ref correctly, however the property is defined on a subclass of the current mapping.
			
			//subclass property ref
			else if(lhsPersister.isDefinedOnSubclass(property)){
				return lhsPersister.getSubclassPropertyTableName(property);
			}else{
				//currentClass property ref
				return lhsPersister.getPropertyTableName(propertyName); 
			}
		}
	}
	
	/**
	 * Get the columns of the associated table which are to 
	 * be used in the join
	 */
	public static String[] getRHSColumnNames(AssociationType type, SessionFactoryImplementor factory) {
		String uniqueKeyPropertyName = type.getRHSUniqueKeyPropertyName();
		Joinable joinable = type.getAssociatedJoinable(factory);
		if (uniqueKeyPropertyName==null) {
			return joinable.getKeyColumnNames();
		}
		else {
			return ( (OuterJoinLoadable) joinable ).getPropertyColumnNames(uniqueKeyPropertyName);
		}
	}
}
