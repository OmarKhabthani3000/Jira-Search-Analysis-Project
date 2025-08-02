package org.adithya.modelClasses;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Address 
{

	@Column(name="user_city")
	String city;
	@Column(name="user_state")
	String state;
	@Column(name="user_street")
	String street;
	@Column(name="user_pincode")
	String pincode;
	
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getStreet() {
		return street;
	}
	public void setStreet(String street) {
		this.street = street;
	}
	public String getPincode() {
		return pincode;
	}
	public void setPincode(String pincode) {
		this.pincode = pincode;
	}
	
}
