package net.codejava.hibernate;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

/**
 * Book.java This class maps to a table in database.
 * 
 * @author www.codejava.net
 *
 */

@Entity
@IdClass(value = PersonId.class)
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "persons")
@Access(value = AccessType.FIELD)
public class Person {

	public Person() {
	}

	@javax.persistence.Column(name = "name", unique = false, nullable = false, insertable = true, updatable = false, length = 255)
	@Id
	private String name;
	
	

	@javax.persistence.Column(name = "test", unique = false, nullable = false, insertable = true, updatable = true, length = 255)
	private String test ;

	public String getTest() {
		return test;
	}

	public void setTest(String test) {
		this.test = test;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
