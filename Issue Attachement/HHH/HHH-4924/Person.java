package net;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

@Entity
@Table(name = "PERSON")
public class Person
{
	private PersonPk pk;
	private String   zodiac;
	private Office   office;

	@EmbeddedId
	public PersonPk getPk(){
		return pk;
	}

	public void setPk(PersonPk pk){
		this.pk = pk;
	}

	@OneToOne(optional=true, fetch=FetchType.EAGER)
	@PrimaryKeyJoinColumn
	public Office getOffice(){
		return office;
	}

	public void setOffice(Office office){
		this.office = office;
	}

	@Column
	public String getZodiac(){
		return zodiac;
	}

	public void setZodiac(String zodiac){
		this.zodiac = zodiac;
	}
}
