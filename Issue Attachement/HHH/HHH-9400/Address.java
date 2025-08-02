package my.test.own.hibernate_xml_1_N_bidir_ehcache;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;


	
	public class Address {
    
	    private long id;
	
	    private String addressLine1;
	 
	 
	    private String zipcode;
	    
	    private String city;
	 
	    //private Employee employee;
	    //List<Employee>employees=new ArrayList<Employee>();
	    Set<Employee>employees=new HashSet<Employee>();
	    
	    public Address(){
	    	
	    }

		public Address(String addressLine1, String zipcode, String city) {
			super();
			this.addressLine1 = addressLine1;
			this.zipcode = zipcode;
			this.city = city;
		}
		public Address(Long id,String addressLine1, String zipcode, String city) {
			super();
			this.id=id;
			this.addressLine1 = addressLine1;
			this.zipcode = zipcode;
			this.city = city;
		}

		public long getId() {
			System.out.println("Address.getId()");
			return id;
		}

		public void setId(long id) {
			System.out.println("Address.setId()");
			this.id = id;
		}

		public String getAddressLine1() {
			System.out.println("Address.getAddressLine1()");
			return addressLine1;
		}

		public void setAddressLine1(String addressLine1) {
			System.out.println("Address.setAddressLine1()");

			this.addressLine1 = addressLine1;
		}

		public String getZipcode() {
			return zipcode;
		}

		public void setZipcode(String zipcode) {
			this.zipcode = zipcode;
		}

		public String getCity() {
			return city;
		}

		public void setCity(String city) {
			this.city = city;
		}

		public Set<Employee> getEmployees() {
			return employees;
		}

		public void setEmployees(Set<Employee> employees) {
			this.employees = employees;
		}

/*		public List<Employee> getEmployees() {
			return employees;
		}

		public void setEmployees(List<Employee> employees) {
			this.employees = employees;
		}
*/


		
	    
	    
	}


