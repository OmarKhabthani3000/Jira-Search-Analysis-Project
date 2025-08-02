package org.hibernate.test.annotations.derivedidentities.e1.b.specjmapid.ondemand;

import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertNotNull;

 public class AutoLazyLoadingFailsTest extends BaseCoreFunctionalTestCase {

	 @Before
	  public void setUpData() {
		  Session s = openSession();
		  s.beginTransaction();
		  Company officeNet = new Company("Acme")
			  .addEmployee(
				  new Person("andreak", "Andreas", "Krogh", Arrays.asList(
					  new Car("Volvo")
				  ))
			  ).addEmployee(
				  new Person("foo", "Foo", "Bar", Arrays.asList(
					  new Car("Ferrari")
				  ))
			  );
		  s.persist( officeNet);
		  s.getTransaction().commit();
		  s.close();
	  }

	  @After
	  public void cleanUpData() {
		  Session s = openSession();
		  s.beginTransaction();
		  s.delete( s.get( Company.class, 1L ) );
		  s.getTransaction().commit();
		  s.close();
	  }

	 @Test
	 public void testLazyLoad() {
		 Session s = openSession();
		 s.beginTransaction();
		 Company officeNet = (Company) s.get(Company.class, 1L);
		 s.getTransaction().commit();
		 s.close();

		 assertNotNull(officeNet);
		 for (Person person : officeNet.getEmployees()) {
			 for (Car car : person.getCars()) {
				 System.out.println("Employee " + person.getFirstName() + " has car: " + car.getModel());
			 }
		 }

	 }

	 public AutoLazyLoadingFailsTest() {
		 System.setProperty( "hibernate.enable_specj_proprietary_syntax", "true" );
	 }

	  @Override
	  protected void configure(Configuration cfg) {
		  super.configure( cfg );
		  cfg.setProperty( Environment.ENABLE_LAZY_LOAD_NO_TRANS, "true" );
		  cfg.setProperty( Environment.GENERATE_STATISTICS, "true" );
	  }

	  @Override
	  protected Class[] getAnnotatedClasses() {
		  return new Class[] { AbstractEntity.class, Company.class, Person.class, Car.class};
	  }

	 @Entity
	 @Table(name = "abstract_entity")
	 @Inheritance(strategy = InheritanceType.JOINED)
	 @SequenceGenerator(name = "SEQ_STORE", sequenceName = "abstract_entity_id_seq", allocationSize = 1)
	 public static abstract class AbstractEntity implements Serializable {

		 @Version
		 private long version = 0;

		 @Id
		 @Column(name = "id")
		 @GeneratedValue(strategy = GenerationType.AUTO, generator = "SEQ_STORE")
		 private Long id = null;

		 public Long getId() {
			 return id;
		 }
	 }

	 @Entity
	 @Table(name = "car")
	 @PrimaryKeyJoinColumn(name = "id")
	 public static class Car extends AbstractEntity {

		 public Car() {
		 }

		 public Car(String model) {
			 this.model = model;
		 }

		 @ManyToOne(fetch = FetchType.LAZY, optional = false)
		 @JoinColumn(name = "owner_name", referencedColumnName = "username") // Don't use PK to show non-lazy behavior
		 private Person owner = null;

		 @Column(name = "model")
		 private String model = null;

		 public Person getOwner() {
			 return owner;
		 }

		 public void setOwner(Person owner) {
			 this.owner = owner;
		 }

		 public String getModel() {
			 return model;
		 }

		 public void setModel(String model) {
			 this.model = model;
		 }
	 }

	 @Entity
	 @Table(name = "company")
	 @PrimaryKeyJoinColumn(name = "id")
	 public static class Company extends AbstractEntity {

		 public Company() {
		 }

		 public Company(String name) {
			 this.name = name;
		 }

		 @Column(name = "name")
		 private String name = null;

		 @OneToMany(fetch = FetchType.LAZY, mappedBy = "company", cascade = CascadeType.ALL)
		 private List<Person> employees = new ArrayList<Person>();

		 public Company addEmployee(Person person) {
			 person.setCompany(this);
			 employees.add(person);
			 return this;
		 }

		 public String getName() {
			 return name;
		 }

		 public void setName(String name) {
			 this.name = name;
		 }

		 public List<Person> getEmployees() {
			 return employees;
		 }

		 public void setEmployees(List<Person> employees) {
			 this.employees = employees;
		 }
	 }

	 @Entity
	 @Table(name = "person")
	 @PrimaryKeyJoinColumn(name = "id")
	 public static class Person extends AbstractEntity{

		 public Person() {
		 }

		 public Person(String userName, String firstName, String lastName, List<Car> cars) {
			 this.userName = userName;
			 this.firstName = firstName;
			 this.lastName = lastName;
			 this.cars = cars;
			 for (Car car : cars) {
				 car.setOwner(this);
			 }
		 }

		 @Column(name = "username", nullable = false)
		 private String userName = null;

		 @Column(name = "firstname")
		 private String firstName = null;

		 @Column(name = "lastname")
		 private String lastName = null;

		 @OneToMany(fetch = FetchType.LAZY, mappedBy = "owner", cascade = CascadeType.ALL)
		 List<Car> cars = new ArrayList<Car>();

		 @ManyToOne(fetch = FetchType.LAZY, optional = false)
		 @JoinColumn(name = "company_id")
		 private Company company = null;

		 public String getUserName() {
			 return userName;
		 }

		 public void setUserName(String userName) {
			 this.userName = userName;
		 }

		 public String getFirstName() {
			 return firstName;
		 }

		 public void setFirstName(String firstName) {
			 this.firstName = firstName;
		 }

		 public String getLastName() {
			 return lastName;
		 }

		 public void setLastName(String lastName) {
			 this.lastName = lastName;
		 }

		 public List<Car> getCars() {
			 return cars;
		 }

		 public void setCars(List<Car> cars) {
			 this.cars = cars;
		 }

		 public Company getCompany() {
			 return company;
		 }

		 public void setCompany(Company company) {
			 this.company = company;
		 }
	 }

 }
