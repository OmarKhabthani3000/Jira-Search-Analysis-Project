package hibernateTesting;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Address {
	
	@Column(name="ADDRESS_ID")
	private long addressid;
	
	@Column(name="ADDRESS_TYPE")
	private String addressType;
	
	@Column(name="APT_NO")
	private String aptNo;
	
	@Column(name="FIRST_LINE")
	private String firstLine;
	
	@Column(name="SECOND_LINE")
	private String secondLine;
	
	@Column(name="CITY")
	private String city;
	
	@Column(name="STATE")
	private String state;
	
	@Column(name="ZIP")
	private String zip;
	
	@Column(name="COUNTRY")
	private String country;
	
	public Address() {}
	
	
	public Address(long addressid, String addressType, String aptNo, String firstLine,
			String secondLine, String city, String state, String zip, String country) {
		super();
		this.addressid = addressid;
		this.addressType = addressType;
		this.aptNo = aptNo;
		this.firstLine = firstLine;
		this.secondLine = secondLine;
		this.city = city;
		this.state = state;
		this.zip = zip;
		this.country = country;
	}


	public long getAddressid() {
		return addressid;
	}

	public void setAddressid(long addressid) {
		this.addressid = addressid;
	}

	public String getAddressType() {
		return addressType;
	}

	public void setAddressType(String addressType) {
		this.addressType = addressType;
	}

	public String getAptNo() {
		return aptNo;
	}
	public void setAptNo(String aptNo) {
		this.aptNo = aptNo;
	}
	public String getFirstLine() {
		return firstLine;
	}
	public void setFirstLine(String firstLine) {
		this.firstLine = firstLine;
	}
	public String getSecondLine() {
		return secondLine;
	}
	public void setSecondLine(String secondLine) {
		this.secondLine = secondLine;
	}
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
	public String getZip() {
		return zip;
	}
	public void setZip(String zip) {
		this.zip = zip;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}

}
