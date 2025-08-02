package com.yoochoose.services.internal.persister;

import org.hibernate.MappingException;
import org.hibernate.type.CompositeCustomType;

import com.yoochoose.domain.login.Username;



/** Persists an {@link Username} into two columns. First for the provider and the second is for the username. 
 *  Used by Hibernate.
 * 
 *  @author rodion.alukhanov 
 */
public class PersistentUsername extends CompositeCustomType {

	public PersistentUsername() throws MappingException {
		super(new UserTypeUsername(), new String[] {Username.class.getName()});
	}
}



