package net.codejava.hibernate;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

@Entity
@Table(name = "employees")
@Inheritance(strategy = InheritanceType.JOINED)
@Access(value = AccessType.FIELD)
@PrimaryKeyJoinColumn(name = "name")
public class Employee extends Person {

	private String idEmployee ;
	

	public String getIdEmployee() {
		return idEmployee;
	}

	public void setIdEmployee(String idEmployee) {
		this.idEmployee = idEmployee;
	}
}
