package net;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

@Entity
@Table(name = "OFFICE")
public class Office
{
	private PersonPk pk;
	private Person   person;
	private String   address;

	@EmbeddedId
	public PersonPk getPk(){
		return pk;
	}

	public void setPk(PersonPk pk){
		this.pk = pk;
	}

	@OneToOne(optional=false, fetch=FetchType.LAZY)
	@PrimaryKeyJoinColumn
	public Person getPerson(){
		return person;
	}

	public void setPerson(Person person){
		this.person = person;
	}

	@Column(name="address")
	public String getAddress(){
		return address;
	}

	public void setAddress(String address){
		this.address = address;
	}
}
