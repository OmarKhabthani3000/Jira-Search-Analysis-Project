package com.medical.model.admin;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The persistent class for the med_personal database table.
 * 
 */
@Entity
@Table(name = "med_personal")
public class Personal implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	private long id;
	private String name;
	private String address;
	private String email;
	private Long fax;
	private Boolean gender;
	private Long mobil;
	private Long tele;
	private Integer brithDate;
	private Boolean smoker;
	private String note;
	private ListLookUp marity;
	private ListLookUp insurance;
	// bi-directional one-to-one association to Account
	@OneToOne(mappedBy = "personal" )
	private Account account;

	public Personal() {
	}

	public Personal(Account account) {
		this.account = account;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Long getFax() {
		return fax;
	}

	public void setFax(Long fax) {
		this.fax = fax;
	}

	public Boolean getGender() {
		return gender;
	}

	public void setGender(Boolean gender) {
		this.gender = gender;
	}

	public Long getMobil() {
		return mobil;
	}

	public void setMobil(Long mobil) {
		this.mobil = mobil;
	}

	public Long getTele() {
		return tele;
	}

	public void setTele(Long tele) {
		this.tele = tele;
	}

	public Integer getBrithDate() {
		return brithDate;
	}

	public void setBrithDate(Integer brithDate) {
		this.brithDate = brithDate;
	}

	public Boolean getSmoker() {
		return smoker;
	}

	public void setSmoker(Boolean smoker) {
		this.smoker = smoker;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public ListLookUp getMarity() {
		return marity;
	}

	public void setMarity(ListLookUp marity) {
		this.marity = marity;
	}

	public ListLookUp getInsurance() {
		return insurance;
	}

	public void setInsurance(ListLookUp insurance) {
		this.insurance = insurance;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append("[id=").append(id).append(",address=").append(address)
				.append(",email=").append(email).append(",fax=").append(fax)
				.append(",gender=").append(gender).append(",mobil=").append(
						mobil).append(",name =").append(name).append(",tele =")
				.append(tele).append("]");

		return str.toString();

	}
}