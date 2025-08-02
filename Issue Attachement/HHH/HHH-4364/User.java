/**
 *
 */
package edu.upmc.ccweb.dosimetry.hibtest;

import javax.persistence.Entity;

import org.hibernate.annotations.NamedQuery;


@Entity
@NamedQuery(name="User.findByFirstName", query="FROM User WHERE firstName = :firstName")
public class User extends BaseUser {

	private String firstName;

	private String lastName;


	public String getFirstName() {
		return this.firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}


	public String getLastName() {
		return this.lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
}
