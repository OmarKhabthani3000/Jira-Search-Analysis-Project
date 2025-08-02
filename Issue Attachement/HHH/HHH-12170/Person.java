package model;

import javax.persistence.*;

@Entity
public class Person
{
	@Id
	@GeneratedValue
	private Long id;
	@Column(unique = true)
	private String name;
	private Integer age;

	public void setName(String name)
	{
		this.name = name;
	}

	public void setAge(Integer age)
	{
		this.age = age;
	}
}
