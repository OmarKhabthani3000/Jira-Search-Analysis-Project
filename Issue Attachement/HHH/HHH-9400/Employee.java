package my.test.own.hibernate_xml_1_N_bidir_ehcache;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;

//@Entity
//@Table(name = "EMPLOYEE")
////@Cache(usage=CacheConcurrencyStrategy.READ_ONLY, region="employee")
//@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class Employee {
 
//    @Id
//    @GeneratedValue
//    //@GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "emp_id")
    private long id;
 
//    @Column(name = "emp_name")
    private String name;
 
//    @Column(name = "emp_salary")
    private double salary;
 
//    @OneToOne(mappedBy = "employee")
//    @Cascade(value = org.hibernate.annotations.CascadeType.ALL)
    private Address address;
    
    
    public Employee(){
    	
    }
	

	public Employee(String name, double salary) {
		super();
		this.name = name;
		this.salary = salary;
	}
	public Employee(Long id,String name, double salary) {
		super();
		this.id=id;
		this.name = name;
		this.salary = salary;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getSalary() {
		return salary;
	}

	public void setSalary(double salary) {
		this.salary = salary;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}
    
    
}
