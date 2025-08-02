package org.hibernate.transform;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.transform.BasicTransformerAdapter;

/**
 * 
 * @author Ahmed Ali Elsayed Ali Soliman
 * 02/24/2011
 */
public class DistinctFieldsResultTransformer extends BasicTransformerAdapter implements Serializable 
{
	public static final DistinctFieldsResultTransformer INSTANCE = new DistinctFieldsResultTransformer();
	
	public DistinctFieldsResultTransformer() {}
		
	private static final class Identity {
		final Object[] entity;

		private Identity(Object[] entity) {
			this.entity = entity;
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean equals(Object other) {
			for(int i=0; i<entity.length; i++)
			{
				if(!Identity.class.isInstance( other )
					|| !this.entity[i].equals(( ( Identity ) other ).entity[i]))
					return false;
			}
			return true;
		}

		/**
		 * {@inheritDoc}
		 */
		public int hashCode() {
			return 0;
		}
	}
	
	public List<Object> transformList(List list) {
		List<Object> result = new ArrayList<Object>(list.size());
		Set<Identity> distinct = new HashSet<Identity>();
		for ( int i = 0; i < list.size(); i++ ) {
			Object[] entity = (Object[])list.get( i );
			if ( distinct.add( new Identity( entity ) ) ) {
				result.add( entity );
			}
		}
		return result;
	}
}
