package com.medical.model.admin;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The persistent class for the med_account database table.
 * 
 */
@Entity
@Table(name = "med_account")
public class Account implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	private long id;

	@Column(name = "account_name")
	private String accountName;
	private String encrypt;
	private String password;

	// bi-directional many-to-one association to Group
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "group_id")
	private Group group;

	// bi-directional one-to-one association to Personal
	@OneToOne
	@JoinColumn(name = "Personal_id")
	private Personal personal;

	// bi-directional many-to-one association to Role
	@ManyToOne
	@JoinColumn(name = "role_id")
	private Role role;

	public Account() {
	}
	public Account(Role role) {
		this.role=role;
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getAccountName() {
		return this.accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getEncrypt() {
		return this.encrypt;
	}

	public void setEncrypt(String encrypt) {
		this.encrypt = encrypt;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Group getGroup() {
		return this.group;
	}

	public void setGroup(Group Group) {
		this.group = Group;
	}

	public Personal getPersonal() {
		return this.personal;
	}

	public void setPersonal(Personal personal) {
		this.personal = personal;
	}

	public Role getRole() {
		return this.role;
	}

	public void setRole(Role Role) {
		this.role = Role;
	}

	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append("[id=").append(id).append(",accountName=").append(
				accountName).append(",encrypt=").append(encrypt).append(
				",password=").append(password).append( "]");

		return str.toString();
	}

}