/**
 * 
 */
package be.stesch.poc.core.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SecondaryTable;

/**
 * The Person entity.
 * 
 * @author Steve Schols
 * 
 */
@Entity
/*
 * We are experiencing problems with referencedColumnName which doesn't appear
 * to work when it doesn't reference the primary key of the original table.. See
 * http://opensource.atlassian.com/projects/hibernate/browse/HHH-1829 for more
 * information.
 */
// @SecondaryTable(name = "PersonIdentification", pkJoinColumns = {
// @PrimaryKeyJoinColumn(referencedColumnName = "nationalIdentificationNumber")
// })
@SecondaryTable(name = "PersonIdentification", pkJoinColumns = { @PrimaryKeyJoinColumn(name = "personId") })
public class Person extends AbstractObject {

	@Id
	@GeneratedValue
	private Long id;

	private String firstName;

	private String lastName;

	@Embedded
	private Address address;

	@Column(unique = true)
	private String nationalIdentificationNumber;

	@Column(table = "PersonIdentification")
	private Date dateOfBirth;

	@Column(table = "PersonIdentification")
	private String placeOfBirth;

	@Column(table = "PersonIdentification")
	private Gender gender;

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * @param firstName
	 *            the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * @param lastName
	 *            the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * @return the address
	 */
	public Address getAddress() {
		return address;
	}

	/**
	 * @param address
	 *            the address to set
	 */
	public void setAddress(Address address) {
		this.address = address;
	}

	/**
	 * @return the nationalIdentificationNumber
	 */
	public String getNationalIdentificationNumber() {
		return nationalIdentificationNumber;
	}

	/**
	 * @param nationalIdentificationNumber
	 *            the nationalIdentificationNumber to set
	 */
	public void setNationalIdentificationNumber(
			String nationalIdentificationNumber) {
		this.nationalIdentificationNumber = nationalIdentificationNumber;
	}

	/**
	 * @return the dateOfBirth
	 */
	public Date getDateOfBirth() {
		return dateOfBirth;
	}

	/**
	 * @param dateOfBirth
	 *            the dateOfBirth to set
	 */
	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	/**
	 * @return the placeOfBirth
	 */
	public String getPlaceOfBirth() {
		return placeOfBirth;
	}

	/**
	 * @param placeOfBirth
	 *            the placeOfBirth to set
	 */
	public void setPlaceOfBirth(String placeOfBirth) {
		this.placeOfBirth = placeOfBirth;
	}

	/**
	 * @return the gender
	 */
	public Gender getGender() {
		return gender;
	}

	/**
	 * @param gender
	 *            the gender to set
	 */
	public void setGender(Gender gender) {
		this.gender = gender;
	}

}
