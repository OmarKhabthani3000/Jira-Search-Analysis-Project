package com.amin.gigaspaces.common.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.hibernate.annotations.Type;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;
import org.jboss.envers.Unversioned;
import org.jboss.envers.Versioned;

import com.amin.gigaspaces.common.util.Constants;
import com.amin.gigaspaces.common.util.ContactType;

@Entity
@DiscriminatorValue("PersonalContact")
@SuppressWarnings("serial")
@Versioned
@Indexed
public class PersonalContact extends Contact {

	@Column(name="P_FIRSTNAME")
	@Field(index=Index.TOKENIZED, store=Store.YES)
	private String firstname;

	@Column(name="P_SURNAME")
	@Field(index=Index.TOKENIZED, store=Store.YES)
	private String surname;

	@Column(name="P_DATEOFBIRTH")
	@Type(type="java.util.Date")
	private Date dateOfBirth;

	@Column(name="P_NOTIFYBIRTHDAY")
	@Type(type="boolean")
	private boolean notifyBirthDay;

	@Column(name="P_MYFACESURL")
	@Field(index=Index.TOKENIZED, store=Store.YES)
	private String myFacesUrl;

	@Column(name="P_REMINDERCOUNT")
	private int reminderCount;

	@Column(name="P_REMINDERRESET")
	@Type(type="boolean")
	private boolean reset;
	
	@Unversioned
	private boolean validate;


	public PersonalContact() {
	}

	public String getFirstname() {
		return firstname;
	}
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	public String getSurname() {
		return surname;
	}
	public void setSurname(String surname) {
		this.surname = surname;
	}
	public Date getDateOfBirth() {
		return dateOfBirth;
	}
	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public boolean isNotifyBirthDay() {
		return notifyBirthDay;
	}



	public boolean isValidate() {
		return validate;
	}

	public void setValidate(boolean validate) {
		this.validate = validate;
	}

	public void setNotifyBirthDay(boolean notifyBirthDay) {
		if (validate) {
			boolean noBirthDayPresent = null == this.getDateOfBirth();
			boolean notifyBirthDayWithNoBirthDayPresent = notifyBirthDay && noBirthDayPresent;
			if (notifyBirthDayWithNoBirthDayPresent) {
				throw new IllegalArgumentException("You cannot request notify birthday if you have not entered a date.");
			}

			boolean noBirthdayReminderCount = this.getReminderCount() == 0;
			boolean notifyBirthDayWithNoReminderCount = notifyBirthDay && noBirthdayReminderCount;
			if (notifyBirthDayWithNoReminderCount) {
				throw new IllegalArgumentException("You cannot request notify birthday if no reminder count has been set.");
			}
		}
		this.notifyBirthDay = notifyBirthDay;
	}


	public String getMyFacesUrl() {
		return myFacesUrl;
	}

	public void setMyFacesUrl(String myFacesUrl) {
		this.myFacesUrl = myFacesUrl;
	}



	public int getReminderCount() {
		return reminderCount;
	}

	public void setReminderCount(int reminderCount) {
		this.reminderCount = reminderCount;
	}


	public boolean isReset() {
		return reset;
	}

	public void setReset(boolean reset) {
		this.reset = reset;
	}

	public String getContactType() {
		return ContactType.personalcontact.toString();
	}

//	public boolean equals(Object object) {
//		if (!(object instanceof PersonalContact)) {
//			return false;
//		}
//		PersonalContact personalContact = (PersonalContact)object;
//		return new EqualsBuilder().append(new Object[]{this.getId(), this.getFirstname(), this.getSurname()}, new Object[]{personalContact.getId(), personalContact.getFirstname(), personalContact.getSurname()}).isEquals();
//	}
//
//	public int hashCode() {
//		return new HashCodeBuilder().append(new Object[]{new Long(this.getId()), this.getFirstname(), this.getSurname()}).toHashCode();
//	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("First Name: " + this.getFirstname()+ Constants.NEW_LINE);
		buf.append("Surname: " + this.getSurname() + Constants.NEW_LINE);
		buf.append("Email: " + this.getEmail() + Constants.NEW_LINE);
		buf.append("Date of Birth: " + (null == this.getDateOfBirth() ? "Not Provided" : this.getDateOfBirth()) + Constants.NEW_LINE);
		displayPhonesAndAddresses(buf);
		return buf.toString();
	}


}
