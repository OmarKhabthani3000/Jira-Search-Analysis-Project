//$Id: Being.java,v 1.3 2005/02/12 07:27:32 steveebersole Exp $
package org.hibernate.test.separateunionsubclass;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Gavin King
 */
public abstract class Being {
	private long id;
	private String identity;
	private Location location;
	private List things = new ArrayList();
	/**
	 * @return Returns the id.
	 */
	public long getId() {
		return id;
	}
	/**
	 * @param id The id to set.
	 */
	public void setId(long id) {
		this.id = id;
	}
	/**
	 * @return Returns the identity.
	 */
	public String getIdentity() {
		return identity;
	}
	/**
	 * @param identity The identity to set.
	 */
	public void setIdentity(String identity) {
		this.identity = identity;
	}
	/**
	 * @return Returns the location.
	 */
	public Location getLocation() {
		return location;
	}
	/**
	 * @param location The location to set.
	 */
	public void setLocation(Location location) {
		this.location = location;
	}
	public String getSpecies() {
		return null;
	}

	public List getThings() {
		return things;
	}
	public void setThings(List things) {
		this.things = things;
	}
}
