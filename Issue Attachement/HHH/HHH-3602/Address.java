package com.amin.gigaspaces.common.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.hibernate.search.annotations.ContainedIn;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;
import org.jboss.envers.Versioned;

import com.amin.gigaspaces.common.util.Constants;

@Entity
@org.hibernate.annotations.Entity(
        selectBeforeUpdate = true,
        dynamicInsert = true, dynamicUpdate = true)
@Indexed
@Table(name="T_ADDRESS")
@Versioned
@Cache(usage=CacheConcurrencyStrategy.TRANSACTIONAL)
public class Address implements Serializable {
	private static final long serialVersionUID = 1L;


	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="A_ADDRESS_ID")
	@Type(type="java.lang.Long")
    @DocumentId
	private Long id;

	@Column(name="A_ADDRESS1")
	@Field(index=Index.TOKENIZED, store=Store.YES)
	private String address1;

	@Column(name="A_ADDRESS2")
	@Field(index=Index.TOKENIZED, store=Store.YES)
	private String address2;

	@Column(name="A_TOWN")
	@Field(index=Index.TOKENIZED, store=Store.YES)
	private String town;

	@Column(name="A_COUNTY")
	@Field(index=Index.TOKENIZED, store=Store.YES)
	private String county;

	@Column(name="A_COUNTRY")
	@Field(index=Index.TOKENIZED, store=Store.YES)
	
	private String country;

	@Column(name="A_POSTCODE")
	@Field(index=Index.TOKENIZED, store=Store.YES)
	private String postcode;

	@Column(name="A_ACTIVE")
	@Type(type="boolean")
	private boolean active;

	@Column(name="A_CREATEDON")
	@Type(type="java.util.Date")
	private Date createdOn;

	@Column(name="A_LASTUPDATEDON")
	@Type(type="java.util.Date")
	private Date lastUpdatedOn;

	@ContainedIn
	@ManyToOne
	@JoinColumn(name="C_CONTACT_ID", nullable=false, insertable=true, updatable=true)
	private Contact contact;


	public Address(String address1, String address2, String town,
			String county, String country, String postcode, boolean active, Contact contact) {
		super();
		this.address1 = address1;
		this.address2 = address2;
		this.town = town;
		this.county = county;
		this.country = country;
		this.postcode = postcode;
		this.active = active;
		this.contact = contact;
	}

	public Address() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
		if (null == this.address2 || "".equals(this.address2)) {
			return "N/A";
		}
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public String getTown() {
		return town;
	}

	public void setTown(String town) {
		this.town = town;
	}

	public String getCounty() {
		if (null == this.county || "".equals(this.county)) {
			return "N/A";
		}
		return county;
	}

	public void setCounty(String county) {
		this.county = county;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getPostcode() {
		return postcode;
	}

	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}


	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public Date getLastUpdatedOn() {
		return lastUpdatedOn;
	}

	public void setLastUpdatedOn(Date lastUpdatedOn) {
		this.lastUpdatedOn = lastUpdatedOn;
	}



	public Contact getContact() {
		return contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}


	public String toString() {
		StringBuffer buf = new StringBuffer();
		displayAddress(buf, this);
		return buf.toString();
	}
	private void displayAddress(StringBuffer buf, Address address) {
		buf.append(Constants.TAB + Constants.TAB + "Address 1: " + address.getAddress1() + Constants.NEW_LINE);
		buf.append(Constants.TAB + Constants.TAB +"Address 2: " + address.getAddress2() + Constants.NEW_LINE);
		buf.append(Constants.TAB + Constants.TAB +"Town: " + address.getTown() + Constants.NEW_LINE);
		buf.append(Constants.TAB + Constants.TAB +"County: " + address.getCounty() + Constants.NEW_LINE);
		buf.append(Constants.TAB + Constants.TAB +"Postcode: " + address.getPostcode() + Constants.NEW_LINE);
		buf.append(Constants.TAB + Constants.TAB +"Country: " + address.getCountry() + Constants.NEW_LINE);
		buf.append(Constants.TAB + Constants.TAB +"Is current: " + (address.isActive()? "Yes" : "No") + Constants.NEW_LINE);
		buf.append(Constants.NEW_LINE);
	}

	public boolean isValidPostcode() {

		return false;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (active ? 1231 : 1237);
		result = prime * result
				+ ((address1 == null) ? 0 : address1.hashCode());
		result = prime * result
				+ ((address2 == null) ? 0 : address2.hashCode());
		result = prime * result + ((country == null) ? 0 : country.hashCode());
		result = prime * result + ((county == null) ? 0 : county.hashCode());
		result = prime * result
				+ ((lastUpdatedOn == null) ? 0 : lastUpdatedOn.hashCode());
		result = prime * result
				+ ((postcode == null) ? 0 : postcode.hashCode());
		result = prime * result + ((town == null) ? 0 : town.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Address other = (Address) obj;
		if (active != other.active)
			return false;
		if (address1 == null) {
			if (other.address1 != null)
				return false;
		} else if (!address1.equals(other.address1))
			return false;
		if (address2 == null) {
			if (other.address2 != null)
				return false;
		} else if (!address2.equals(other.address2))
			return false;
		if (country == null) {
			if (other.country != null)
				return false;
		} else if (!country.equals(other.country))
			return false;
		if (county == null) {
			if (other.county != null)
				return false;
		} else if (!county.equals(other.county))
			return false;
		if (lastUpdatedOn == null) {
			if (other.lastUpdatedOn != null)
				return false;
		} else if (!lastUpdatedOn.equals(other.lastUpdatedOn))
			return false;
		if (postcode == null) {
			if (other.postcode != null)
				return false;
		} else if (!postcode.equals(other.postcode))
			return false;
		if (town == null) {
			if (other.town != null)
				return false;
		} else if (!town.equals(other.town))
			return false;
		return true;
	}

}
