package hibernatebugtest;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="person")
public class Person {

	public Person(){}

	@Id
	@Column(name="id")
	private String id;

	@Column(name="name")
	private String name;

	@Column(name="year")
	private int year;

	@Column(name="birth_date")
	private Date birthDate;

	public Person(String id,
				   String name,
				   int year,
				   Date birthDate){
		this.id = id;
		this.name = name;
		this.year = year;
		this.birthDate = birthDate;
	}

	public String getId(){
		return id;
	}

	public String getName(){
		return name;
	}

	public int getYear(){
		return year;
	}

	public Date getBirthDate(){
		return birthDate;
	}

}
