package net;

import java.io.Serializable;

import javax.persistence.Column;

public class PersonPk implements Serializable
{
	private static final long	serialVersionUID	= 7494175680933460258L;

	private String firstName;
	private String lastName;

	PersonPk(){}

	public PersonPk(String firstName, String lastName)
	{
		this.firstName = firstName;
		this.lastName  = lastName;
	}

	@Column(name="first_name")
	public String getFirstName()
	{
		return firstName;
	}

	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}

	@Column(name="last_name")
	public String getLastName()
	{
		return lastName;
	}

	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}
}
