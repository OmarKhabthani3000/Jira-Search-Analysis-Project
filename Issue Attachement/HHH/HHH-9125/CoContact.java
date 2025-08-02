package com.airit.propworks.entity;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.QueryHint;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import javax.validation.constraints.Size;
import javax.validation.constraints.NotNull;

/**
 * The CoContact entity bean.
 * 
 * @author Steven Rose - Air Transport IT Services
 * @version $Revision: 29$, $Date: 3/17/2010 3:53:51 PM${date} $
 */
@Table(name = "CO_CONTACT")
@Entity

public class CoContact implements Serializable {
	
	private static final long serialVersionUID = 7L;

	public static final int CONTACT_NUMBER_LENGTH = 4;

	/**
	 * the composite primary key.
	 */
	private com.airit.propworks.entity.CoContactPK primaryKey;


	private CoSalutations salutationCoSalutations;

	private CoProvinceNames coProvinceNamesCompositeFK1;

	private CoCountries countryCoCountries;

	private CoCompany companyNumberCoCompany;

	private java.lang.String salutation;

	private java.lang.String firstName;

	private java.lang.String middleInitial;

	private java.lang.String lastName;

	private java.lang.String title;

	private java.lang.String address1;

	private java.lang.String address2;

	private java.lang.String address3;

	private java.lang.String city;

	private java.lang.String provinceAbbrv;

	private java.lang.String country;

	private java.lang.String postalCode;

	private java.lang.String emailAddress;

	private java.sql.Timestamp contactDate;

	private java.lang.String createdBy;

	private java.sql.Timestamp createdWhen;

	private java.lang.String changedBy;

	private java.sql.Timestamp changedWhen;

	/**
	 * Default constructor.
	 */
	public CoContact() {
	}

	/**
	 * Tells entity bean to use this class for primary key.
	 */
	@EmbeddedId
	public CoContactPK getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(CoContactPK id) {
		this.primaryKey = id;
	}

	/**
	 * Returns the value of the <code>companyNumber</code> property.
	 * 
	 */
	@Column(name = "COMPANY_NUMBER", insertable = false, updatable = false, length = 10)
	@Size(max = 10, message = "{CoContact.companyNumber} {Size}")
	@NotNull(message = "{CoContact.companyNumber} {NotNull}")
	public java.lang.String getCompanyNumber() {
		if (primaryKey == null) {
			return null;
		}

		return primaryKey.getCompanyNumber();
	}

	/**
	 * Sets the value of the <code>companyNumber</code> property.
	 * 
	 * @param companyNumber
	 *            the value for the <code>companyNumber</code> property
	 */
	public void setCompanyNumber(java.lang.String companyNumber) {
		if (primaryKey == null) {
			primaryKey = new CoContactPK();
		}

		primaryKey.setCompanyNumber(companyNumber);
	}

	/**
	 * Returns the value of the <code>contactNumber</code> property.
	 * 
	 */
	@Column(name = "CONTACT_NUMBER", insertable = false, updatable = false, length = 10)
	@Size(max = 10, message = "{CoContact.contactNumber} {Size}")
	@NotNull(message = "{CoContact.contactNumber} {NotNull}")
	public java.lang.String getContactNumber() {
		if (primaryKey == null) {
			return null;
		}

		return primaryKey.getContactNumber();
	}

	/**
	 * Sets the value of the <code>contactNumber</code> property.
	 * 
	 * @param contactNumber
	 *            the value for the <code>contactNumber</code> property
	 */
	public void setContactNumber(java.lang.String contactNumber) {
		if (primaryKey == null) {
			primaryKey = new CoContactPK();
		}

		primaryKey.setContactNumber(contactNumber);
	}

	/**
	 * Returns the value of the <code>salutation</code> property.
	 * 
	 */
	@Column(name = "SALUTATION", length = 4)
	@Size(max = 4, message = "{CoContact.salutation} {Size}")
	public java.lang.String getSalutation() {
		return salutation;
	}

	/**
	 * Sets the value of the <code>salutation</code> property.
	 * 
	 * @param salutation
	 *            the value for the <code>salutation</code> property
	 */
	public void setSalutation(java.lang.String salutation) {
		this.salutation = salutation;
	}

	/**
	 * Returns the value of the <code>firstName</code> property.
	 * 
	 */
	@Column(name = "FIRST_NAME", length = 25)
	@Size(max = 25, message = "{CoContact.firstName} {Size}")
	public java.lang.String getFirstName() {
		return firstName;
	}

	/**
	 * Sets the value of the <code>firstName</code> property.
	 * 
	 * @param firstName
	 *            the value for the <code>firstName</code> property
	 */
	public void setFirstName(java.lang.String firstName) {
		this.firstName = firstName;
	}

	/**
	 * Returns the value of the <code>middleInitial</code> property.
	 * 
	 */
	@Column(name = "MIDDLE_INITIAL", length = 1)
	@Size(max = 1, message = "{CoContact.middleInitial} {Size}")
	public java.lang.String getMiddleInitial() {
		return middleInitial;
	}

	/**
	 * Sets the value of the <code>middleInitial</code> property.
	 * 
	 * @param middleInitial
	 *            the value for the <code>middleInitial</code> property
	 */
	public void setMiddleInitial(java.lang.String middleInitial) {
		this.middleInitial = middleInitial;
	}

	/**
	 * Returns the value of the <code>lastName</code> property.
	 * 
	 */
	@Column(name = "LAST_NAME", length = 20)
	@Size(max = 20, message = "{CoContact.lastName} {Size}")
	@NotNull(message = "{CoContact.lastName} {NotNull}")
	public java.lang.String getLastName() {
		return lastName;
	}

	/**
	 * Sets the value of the <code>lastName</code> property.
	 * 
	 * @param lastName
	 *            the value for the <code>lastName</code> property
	 */
	public void setLastName(java.lang.String lastName) {
		this.lastName = lastName;
	}

	/**
	 * Returns the value of the <code>title</code> property.
	 * 
	 */
	@Column(name = "TITLE", length = 30)
	@Size(max = 30, message = "{CoContact.title} {Size}")
	public java.lang.String getTitle() {
		return title;
	}

	/**
	 * Sets the value of the <code>title</code> property.
	 * 
	 * @param title
	 *            the value for the <code>title</code> property
	 */
	public void setTitle(java.lang.String title) {
		this.title = title;
	}

	/**
	 * Returns the value of the <code>address1</code> property.
	 * 
	 */
	@Column(name = "ADDRESS_1", length = 35)
	@Size(max = 35, message = "{CoContact.address1} {Size}")
	@NotNull(message = "{CoContact.address1} {NotNull}")
	public java.lang.String getAddress1() {
		return address1;
	}

	/**
	 * Sets the value of the <code>address1</code> property.
	 * 
	 * @param address1
	 *            the value for the <code>address1</code> property
	 */
	public void setAddress1(java.lang.String address1) {
		this.address1 = address1;
	}

	/**
	 * Returns the value of the <code>address2</code> property.
	 * 
	 */
	@Column(name = "ADDRESS_2", length = 35)
	@Size(max = 35, message = "{CoContact.address2} {Size}")
	public java.lang.String getAddress2() {
		return address2;
	}

	/**
	 * Sets the value of the <code>address2</code> property.
	 * 
	 * @param address2
	 *            the value for the <code>address2</code> property
	 */
	public void setAddress2(java.lang.String address2) {
		this.address2 = address2;
	}

	/**
	 * Returns the value of the <code>address3</code> property.
	 * 
	 */
	@Column(name = "ADDRESS_3", length = 35)
	@Size(max = 35, message = "{CoContact.address3} {Size}")
	public java.lang.String getAddress3() {
		return address3;
	}

	/**
	 * Sets the value of the <code>address3</code> property.
	 * 
	 * @param address3
	 *            the value for the <code>address3</code> property
	 */
	public void setAddress3(java.lang.String address3) {
		this.address3 = address3;
	}

	/**
	 * Returns the value of the <code>city</code> property.
	 * 
	 */
	@Column(name = "CITY", length = 25)
	@Size(max = 25, message = "{CoContact.city} {Size}")
	@NotNull(message = "{CoContact.city} {NotNull}")
	public java.lang.String getCity() {
		return city;
	}

	/**
	 * Sets the value of the <code>city</code> property.
	 * 
	 * @param city
	 *            the value for the <code>city</code> property
	 */
	public void setCity(java.lang.String city) {
		this.city = city;
	}

	/**
	 * Returns the value of the <code>provinceAbbrv</code> property.
	 * 
	 */
	@Column(name = "PROVINCE_ABBRV", length = 3)
	@Size(max = 3, message = "{CoContact.provinceAbbrv} {Size}")
	public java.lang.String getProvinceAbbrv() {
		return provinceAbbrv;
	}

	/**
	 * Sets the value of the <code>provinceAbbrv</code> property.
	 * 
	 * @param provinceAbbrv
	 *            the value for the <code>provinceAbbrv</code> property
	 */
	public void setProvinceAbbrv(java.lang.String provinceAbbrv) {
		this.provinceAbbrv = provinceAbbrv;
	}

	/**
	 * Returns the value of the <code>country</code> property.
	 * 
	 */
	@Column(name = "COUNTRY", length = 4)
	@Size(max = 4, message = "{CoContact.country} {Size}")
	@NotNull(message = "{CoContact.country} {NotNull}")
	public java.lang.String getCountry() {
		return country;
	}

	/**
	 * Sets the value of the <code>country</code> property.
	 * 
	 * @param country
	 *            the value for the <code>country</code> property
	 */
	public void setCountry(java.lang.String country) {
		this.country = country;
	}

	/**
	 * Returns the value of the <code>postalCode</code> property.
	 * 
	 */
	@Column(name = "POSTAL_CODE", length = 10)
	@Size(max = 10, message = "{CoContact.postalCode} {Size}")
	public java.lang.String getPostalCode() {
		return postalCode;
	}

	/**
	 * Sets the value of the <code>postalCode</code> property.
	 * 
	 * @param postalCode
	 *            the value for the <code>postalCode</code> property
	 */
	public void setPostalCode(java.lang.String postalCode) {
		this.postalCode = postalCode;
	}

	/**
	 * Returns the value of the <code>emailAddress</code> property.
	 * 
	 */
	@Column(name = "EMAIL_ADDRESS", length = 50)
	@Size(max = 50, message = "{CoContact.emailAddress} {Size}")
	public java.lang.String getEmailAddress() {
		return emailAddress;
	}

	/**
	 * Sets the value of the <code>emailAddress</code> property.
	 * 
	 * @param emailAddress
	 *            the value for the <code>emailAddress</code> property
	 */
	public void setEmailAddress(java.lang.String emailAddress) {
		this.emailAddress = emailAddress;
	}

	/**
	 * Returns the value of the <code>contactDate</code> property.
	 * 
	 */
	@Column(name = "CONTACT_DATE")
	public java.sql.Timestamp getContactDate() {
		return contactDate;
	}

	/**
	 * Sets the value of the <code>contactDate</code> property.
	 * 
	 * @param contactDate
	 *            the value for the <code>contactDate</code> property
	 */
	public void setContactDate(java.sql.Timestamp contactDate) {
		this.contactDate = contactDate;
	}

	/**
	 * Returns the value of the <code>createdBy</code> property.
	 * 
	 */
	@Column(name = "CREATED_BY", length = 30)
	@Size(max = 30, message = "{CoContact.createdBy} {Size}")
	public java.lang.String getCreatedBy() {
		return createdBy;
	}

	/**
	 * Sets the value of the <code>createdBy</code> property.
	 * 
	 * @param createdBy
	 *            the value for the <code>createdBy</code> property
	 */
	public void setCreatedBy(java.lang.String createdBy) {
		this.createdBy = createdBy;
	}

	/**
	 * Returns the value of the <code>createdWhen</code> property.
	 * 
	 */
	@Column(name = "CREATED_WHEN")
	public java.sql.Timestamp getCreatedWhen() {
		return createdWhen;
	}

	/**
	 * Sets the value of the <code>createdWhen</code> property.
	 * 
	 * @param createdWhen
	 *            the value for the <code>createdWhen</code> property
	 */
	public void setCreatedWhen(java.sql.Timestamp createdWhen) {
		this.createdWhen = createdWhen;
	}

	/**
	 * Returns the value of the <code>changedBy</code> property.
	 * 
	 */
	@Column(name = "CHANGED_BY", length = 30)
	@Size(max = 30, message = "{CoContact.changedBy} {Size}")
	public java.lang.String getChangedBy() {
		return changedBy;
	}

	/**
	 * Sets the value of the <code>changedBy</code> property.
	 * 
	 * @param changedBy
	 *            the value for the <code>changedBy</code> property
	 */
	public void setChangedBy(java.lang.String changedBy) {
		this.changedBy = changedBy;
	}

	/**
	 * Returns the value of the <code>changedWhen</code> property.
	 * 
	 */
	@Column(name = "CHANGED_WHEN")
	public java.sql.Timestamp getChangedWhen() {
		return changedWhen;
	}

	/**
	 * Sets the value of the <code>changedWhen</code> property.
	 * 
	 * @param changedWhen
	 *            the value for the <code>changedWhen</code> property
	 */
	public void setChangedWhen(java.sql.Timestamp changedWhen) {
		this.changedWhen = changedWhen;
	}

	/**
	 * Returns the <code>CoSalutations </code> Entity Object . This is to
	 * implement the CMR ManyToOne relationship between FKSALUTATION and
	 * PKSALUTATION in the CoSalutations Entity Bean.
	 */
	@ManyToOne
	@JoinColumn(name = "SALUTATION", referencedColumnName = "SALUTATION", insertable = false, updatable = false)
	public CoSalutations getSalutationCoSalutations() {
		return salutationCoSalutations;
	}

	/**
	 * Sets the <code>CoSalutations </code> Entity Object . This is to
	 * implement the CMR ManyToOne relationship between FKSALUTATION and
	 * PKSALUTATION in the CoSalutations Entity Bean.
	 */
	public void setSalutationCoSalutations(CoSalutations cosalutations) {
		this.salutationCoSalutations = cosalutations;
	}

	/**
	 * Returns the <code>CoCountries </code> Entity Object . This is to
	 * implement the CMR ManyToOne relationship between FKCOUNTRY and
	 * PKCOUNTRY_ID in the CoCountries Entity Bean.
	 */
	@ManyToOne
	@JoinColumn(name = "COUNTRY", referencedColumnName = "COUNTRY_ID", insertable = false, updatable = false)
	public CoCountries getCountryCoCountries() {
		return countryCoCountries;
	}

	/**
	 * Sets the <code>CoCountries </code> Entity Object . This is to implement
	 * the CMR ManyToOne relationship between FKCOUNTRY and PKCOUNTRY_ID in the
	 * CoCountries Entity Bean.
	 */
	public void setCountryCoCountries(CoCountries cocountries) {
		this.countryCoCountries = cocountries;
	}

	/**
	 * Returns the <code>CoCompany </code> Entity Object . This is to implement
	 * the CMR ManyToOne relationship between FKCOMPANY_NUMBER and
	 * PKCOMPANY_NUMBER in the CoCompany Entity Bean.
	 */
	@ManyToOne
	@JoinColumn(name = "COMPANY_NUMBER", referencedColumnName = "COMPANY_NUMBER", insertable = false, updatable = false)
	public CoCompany getCompanyNumberCoCompany() {
		return companyNumberCoCompany;
	}

	/**
	 * Sets the <code>CoCompany </code> Entity Object . This is to implement
	 * the CMR ManyToOne relationship between FKCOMPANY_NUMBER and
	 * PKCOMPANY_NUMBER in the CoCompany Entity Bean.
	 */
	public void setCompanyNumberCoCompany(CoCompany cocompany) {
		this.companyNumberCoCompany = cocompany;
	}

	/**
	 * Returns the <code>CoProvinceNames </code> Entity Object . This is to
	 * implement the CMR ManyToOne relationship between FKPROVINCE_ABBRV and
	 * PK${Relation.Name} in the CoProvinceNames Entity Bean.
	 */
	@ManyToOne
	@JoinColumns( {
			@JoinColumn(name = "PROVINCE_ABBRV", referencedColumnName = "PROVINCE_ABBRV", insertable = false, updatable = false),
			@JoinColumn(name = "COUNTRY", referencedColumnName = "COUNTRY_ID", insertable = false, updatable = false) })
	public CoProvinceNames getCoProvinceNamesCompositeFK1() {
		return coProvinceNamesCompositeFK1;
	}

	/**
	 * Sets the <code>CoProvinceNames </code> Entity Object . This is to
	 * implement the CMR ManyToOne relationship between FKPROVINCE_ABBRV and
	 * PKCOMPANY_NUMBER in the CoProvinceNames Entity Bean.
	 */
	public void setCoProvinceNamesCompositeFK1(CoProvinceNames coprovincenames) {
		this.coProvinceNamesCompositeFK1 = coprovincenames;
	}


	public int hashCode() {
		String code = "";
                
                code += primaryKey != null ? primaryKey.hashCode(): "";
		code += salutation;
		code += firstName;
		code += middleInitial;
		code += lastName;
		code += title;
		code += address1;
		code += address2;
		code += address3;
		code += city;
		code += provinceAbbrv;
		code += country;
		code += postalCode;
		code += emailAddress;
		code += contactDate;
		code += createdBy;
		code += createdWhen;
		code += changedBy;
		code += changedWhen;

		return code.hashCode();
	}

	public boolean equals(Object object) {
		CoContact ent = (CoContact) object;

		boolean eq = true;

		if (object == null) {
			eq = false;
		} else {
			// if both fields are null, they are equal.
			if ((this.primaryKey == null) && (ent.primaryKey == null)) {
				eq = eq && true;
			} else {
				// So not both fields are null, than if either one is null, they
				// are not equal.
				if ((this.primaryKey == null) || (ent.primaryKey == null)) {
					eq = false;
				} else {
					// If neither one is null, check the equality.
					eq = eq && this.primaryKey.equals(ent.primaryKey);
				}
			}

			// if both fields are null, they are equal.
			if ((this.salutation == null) && (ent.salutation == null)) {
				eq = eq && true;
			} else {
				// So not both fields are null, than if either one is null, they
				// are not equal.
				if ((this.salutation == null) || (ent.salutation == null)) {
					eq = false;
				} else {
					// If neither one is null, check the equality.
					eq = eq && this.salutation.equals(ent.salutation);
				}
			}

			// if both fields are null, they are equal.
			if ((this.firstName == null) && (ent.firstName == null)) {
				eq = eq && true;
			} else {
				// So not both fields are null, than if either one is null, they
				// are not equal.
				if ((this.firstName == null) || (ent.firstName == null)) {
					eq = false;
				} else {
					// If neither one is null, check the equality.
					eq = eq && this.firstName.equals(ent.firstName);
				}
			}

			// if both fields are null, they are equal.
			if ((this.middleInitial == null) && (ent.middleInitial == null)) {
				eq = eq && true;
			} else {
				// So not both fields are null, than if either one is null, they
				// are not equal.
				if ((this.middleInitial == null) || (ent.middleInitial == null)) {
					eq = false;
				} else {
					// If neither one is null, check the equality.
					eq = eq && this.middleInitial.equals(ent.middleInitial);
				}
			}

			// if both fields are null, they are equal.
			if ((this.lastName == null) && (ent.lastName == null)) {
				eq = eq && true;
			} else {
				// So not both fields are null, than if either one is null, they
				// are not equal.
				if ((this.lastName == null) || (ent.lastName == null)) {
					eq = false;
				} else {
					// If neither one is null, check the equality.
					eq = eq && this.lastName.equals(ent.lastName);
				}
			}

			// if both fields are null, they are equal.
			if ((this.title == null) && (ent.title == null)) {
				eq = eq && true;
			} else {
				// So not both fields are null, than if either one is null, they
				// are not equal.
				if ((this.title == null) || (ent.title == null)) {
					eq = false;
				} else {
					// If neither one is null, check the equality.
					eq = eq && this.title.equals(ent.title);
				}
			}

			// if both fields are null, they are equal.
			if ((this.address1 == null) && (ent.address1 == null)) {
				eq = eq && true;
			} else {
				// So not both fields are null, than if either one is null, they
				// are not equal.
				if ((this.address1 == null) || (ent.address1 == null)) {
					eq = false;
				} else {
					// If neither one is null, check the equality.
					eq = eq && this.address1.equals(ent.address1);
				}
			}

			// if both fields are null, they are equal.
			if ((this.address2 == null) && (ent.address2 == null)) {
				eq = eq && true;
			} else {
				// So not both fields are null, than if either one is null, they
				// are not equal.
				if ((this.address2 == null) || (ent.address2 == null)) {
					eq = false;
				} else {
					// If neither one is null, check the equality.
					eq = eq && this.address2.equals(ent.address2);
				}
			}

			// if both fields are null, they are equal.
			if ((this.address3 == null) && (ent.address3 == null)) {
				eq = eq && true;
			} else {
				// So not both fields are null, than if either one is null, they
				// are not equal.
				if ((this.address3 == null) || (ent.address3 == null)) {
					eq = false;
				} else {
					// If neither one is null, check the equality.
					eq = eq && this.address3.equals(ent.address3);
				}
			}

			// if both fields are null, they are equal.
			if ((this.city == null) && (ent.city == null)) {
				eq = eq && true;
			} else {
				// So not both fields are null, than if either one is null, they
				// are not equal.
				if ((this.city == null) || (ent.city == null)) {
					eq = false;
				} else {
					// If neither one is null, check the equality.
					eq = eq && this.city.equals(ent.city);
				}
			}

			// if both fields are null, they are equal.
			if ((this.provinceAbbrv == null) && (ent.provinceAbbrv == null)) {
				eq = eq && true;
			} else {
				// So not both fields are null, than if either one is null, they
				// are not equal.
				if ((this.provinceAbbrv == null) || (ent.provinceAbbrv == null)) {
					eq = false;
				} else {
					// If neither one is null, check the equality.
					eq = eq && this.provinceAbbrv.equals(ent.provinceAbbrv);
				}
			}

			// if both fields are null, they are equal.
			if ((this.country == null) && (ent.country == null)) {
				eq = eq && true;
			} else {
				// So not both fields are null, than if either one is null, they
				// are not equal.
				if ((this.country == null) || (ent.country == null)) {
					eq = false;
				} else {
					// If neither one is null, check the equality.
					eq = eq && this.country.equals(ent.country);
				}
			}

			// if both fields are null, they are equal.
			if ((this.postalCode == null) && (ent.postalCode == null)) {
				eq = eq && true;
			} else {
				// So not both fields are null, than if either one is null, they
				// are not equal.
				if ((this.postalCode == null) || (ent.postalCode == null)) {
					eq = false;
				} else {
					// If neither one is null, check the equality.
					eq = eq && this.postalCode.equals(ent.postalCode);
				}
			}

			// if both fields are null, they are equal.
			if ((this.emailAddress == null) && (ent.emailAddress == null)) {
				eq = eq && true;
			} else {
				// So not both fields are null, than if either one is null, they
				// are not equal.
				if ((this.emailAddress == null) || (ent.emailAddress == null)) {
					eq = false;
				} else {
					// If neither one is null, check the equality.
					eq = eq && this.emailAddress.equals(ent.emailAddress);
				}
			}

			// if both fields are null, they are equal.
			if ((this.contactDate == null) && (ent.contactDate == null)) {
				eq = eq && true;
			} else {
				// So not both fields are null, than if either one is null, they
				// are not equal.
				if ((this.contactDate == null) || (ent.contactDate == null)) {
					eq = false;
				} else {
					// If neither one is null, check the equality.
					eq = eq && this.contactDate.equals(ent.contactDate);
				}
			}

			// if both fields are null, they are equal.
			if ((this.createdBy == null) && (ent.createdBy == null)) {
				eq = eq && true;
			} else {
				// So not both fields are null, than if either one is null, they
				// are not equal.
				if ((this.createdBy == null) || (ent.createdBy == null)) {
					eq = false;
				} else {
					// If neither one is null, check the equality.
					eq = eq && this.createdBy.equals(ent.createdBy);
				}
			}

			// if both fields are null, they are equal.
			if ((this.createdWhen == null) && (ent.createdWhen == null)) {
				eq = eq && true;
			} else {
				// So not both fields are null, than if either one is null, they
				// are not equal.
				if ((this.createdWhen == null) || (ent.createdWhen == null)) {
					eq = false;
				} else {
					// If neither one is null, check the equality.
					eq = eq && this.createdWhen.equals(ent.createdWhen);
				}
			}

			// if both fields are null, they are equal.
			if ((this.changedBy == null) && (ent.changedBy == null)) {
				eq = eq && true;
			} else {
				// So not both fields are null, than if either one is null, they
				// are not equal.
				if ((this.changedBy == null) || (ent.changedBy == null)) {
					eq = false;
				} else {
					// If neither one is null, check the equality.
					eq = eq && this.changedBy.equals(ent.changedBy);
				}
			}

			// if both fields are null, they are equal.
			if ((this.changedWhen == null) && (ent.changedWhen == null)) {
				eq = eq && true;
			} else {
				// So not both fields are null, than if either one is null, they
				// are not equal.
				if ((this.changedWhen == null) || (ent.changedWhen == null)) {
					eq = false;
				} else {
					// If neither one is null, check the equality.
					eq = eq && this.changedWhen.equals(ent.changedWhen);
				}
			}
		}

		return eq;
	}

	/**
	 * Returns Contact Name
	 */

	@Transient
	public String getName() {
		if (getFirstName() != null)
			return getFirstName() + " " + getLastName();
		else
			return getLastName();
	}

	@Transient
	public String getContactName() {
		String contactName;
		if (getFirstName() != null) {
			contactName = getLastName() + ", " + getFirstName();
		} else {
			contactName = getLastName();
		}
		return contactName;
	}

	@Transient
	public String getFullName() {
		String contactName = new String();

		if (getSalutation() != null) {
			contactName += getSalutation();
		}

		if (getFirstName() != null) {
			contactName += (contactName.length() == 0 ? "" : " ");
			contactName += getFirstName();
		}

		if (getMiddleInitial() != null) {
			contactName += (contactName.length() == 0 ? "" : " ");
			contactName += getMiddleInitial();
		}

		if (getLastName() != null) {
			contactName += (contactName.length() == 0 ? "" : " ");
			contactName += getLastName();
		}

		return contactName;
	}
        
        @Transient
        public String getCityStateZip() {
            String cityStateZip = new String();
            
            cityStateZip += getCity() == null ? "" : getCity() + ", ";
            cityStateZip += getProvinceAbbrv() == null ? "" : getProvinceAbbrv() + " ";
            cityStateZip += getPostalCode() == null ? "" : getPostalCode();
            
            return cityStateZip;
        }
}
