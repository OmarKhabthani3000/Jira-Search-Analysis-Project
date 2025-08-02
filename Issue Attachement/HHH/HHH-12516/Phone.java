package hibernateTesting;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Phone {
	
	@Column(name="PHONE_ID")
	private long phoneid;
	
	@Column(name="PHONE_TYPE")
	private String phoneType;
	
	@Column(name="PHONE_NUMBER")
	private String phoneNumber;
	
	public Phone() {}
	

	public Phone(long phoneid, String phoneType, String phoneNumber) {
		super();
		this.phoneid = phoneid;
		this.phoneType = phoneType;
		this.phoneNumber = phoneNumber;
	}


	public long getPhoneid() {
		return phoneid;
	}

	public void setPhoneid(long phoneid) {
		this.phoneid = phoneid;
	}

	public String getPhoneType() {
		return phoneType;
	}
	public void setPhoneType(String phoneType) {
		this.phoneType = phoneType;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

}
