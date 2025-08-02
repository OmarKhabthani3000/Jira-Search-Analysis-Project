package hibernateTesting;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Table;

@Entity
@Table (name="CONTACT")
public class Contact {
	
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column (name="CONTACT_ID")
	private long contactID;
	
	@Column (name="FIRST_NAME")
	private String fName;
	
	@Column(name="LAST_NAME")
	private String lName;
	
	@Column(name="SEX")
	private char sex;
	
	@ElementCollection
	@JoinTable(name="\"ADDRESS\"",joinColumns=@JoinColumn(name="CONTACT_ID"))
	List<Address> listOfAddress;
	
	@ElementCollection
	@JoinTable(name="\"PHONE\"",joinColumns=@JoinColumn(name="CONTACT_ID"))
	List<Phone> listOfPhone;
	
	public Contact() {
		listOfAddress = new ArrayList<Address>();
		listOfPhone = new ArrayList<Phone>();
	}
	
	public Contact(long contactID, String fName, String lName, char sex, List<Address> listOfAddress, List<Phone> listOfPhone) {
		super();
		this.contactID = contactID;
		this.fName = fName;
		this.lName = lName;
		this.sex = sex;
		this.listOfAddress = listOfAddress;
		this.listOfPhone = listOfPhone;
	}
	
	public long getContactID() {
		return contactID;
	}
	public void setContactID(long contactID) {
		this.contactID = contactID;
	}
	public String getfName() {
		return fName;
	}
	public void setfName(String fName) {
		this.fName = fName;
	}
	public String getlName() {
		return lName;
	}
	public void setlName(String lName) {
		this.lName = lName;
	}
	public char getSex() {
		return sex;
	}
	public void setSex(char sex) {
		this.sex = sex;
	}

	public List<Address> getListOfAddress() {
		return listOfAddress;
	}

	public void setListOfAddress(List<Address> listOfAddress) {
		this.listOfAddress = listOfAddress;
	}

	public List<Phone> getListOfPhone() {
		return listOfPhone;
	}

	public void setListOfPhone(List<Phone> listOfPhone) {
		this.listOfPhone = listOfPhone;
	}
	

}
