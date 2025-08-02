package com.medical.model.admin;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Set;

/**
 * The persistent class for the med_role database table.
 * 
 */
@Entity
@Table(name = "med_role")
public class Role implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	private long id;
	@Column(name = "role_name")
	private String roleName;

	// bi-directional many-to-one association to Account
	@OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
	private Set<Account> accounts;

	// bi-directional many-to-one association to RolePriv
	@OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
	private Set<RolePriv> rolePrivs;

	public Role() {
	}

	public Role(long id, String roleName) {
		this.id = id;
		this.roleName = roleName;
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getRoleName() {
		return this.roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public Set<Account> getAccounts() {
		return this.accounts;
	}

	public void setAccounts(Set<Account> accounts) {
		this.accounts = accounts;
	}

	public Set<RolePriv> getRolePrivs() {
		return this.rolePrivs;
	}

	public void setRolePrivs(Set<RolePriv> rolePrivs) {
		this.rolePrivs = rolePrivs;
	}

	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append("[id=").append(id).append(",roleName=").append(roleName)
				.append("]");

		return str.toString();

	}

}