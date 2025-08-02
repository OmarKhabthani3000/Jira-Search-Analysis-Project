package com.airit.propworks.entity;

import java.io.Serializable;
import java.util.Set;
import javax.persistence.CascadeType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Type;
import javax.validation.constraints.Size;
import javax.validation.constraints.NotNull;

/**
 * The AgreementClasses entity bean.
 * 
 * @author Steven Rose - Air Transport IT Services
 * @version $Revision: 17$, $Date: 11/9/2010 12:38:03 PM${date} $
 */
@Table(name = "AGREEMENT_CLASSES")
@Entity
public class AgreementClasses implements Serializable, Comparable {
	private Set<AgreementTypes> agreementTypesAgreementClasss;

	private Set<RoleAgrClasses> roleAgrClassesAgreementClasss;

	private java.lang.String agreementClass;

	private java.lang.String classDesc;

	private java.lang.Boolean productsAllowed;

	private java.lang.String changedBy;

	private java.sql.Timestamp changedWhen;

	private java.lang.Boolean enforceBillRuleDates;

	private java.lang.String createdBy;

	private java.sql.Timestamp createdWhen;

	/**
	 * Default constructor.
	 */
	public AgreementClasses() {
	}

	/**
	 * Set the primary key.
	 * 
	 * @param primaryKey
	 *            the primary key
	 */
	public void setPrimaryKey(java.lang.String primaryKey) {
		setAgreementClass(primaryKey);
	}

	/**
	 * Return the primary key.
	 * 
	 * @return java.lang.String with the primary key.
	 */
	@Transient
	public java.lang.String getPrimaryKey() {
		return getAgreementClass();
	}

	/**
	 * Returns the value of the <code>agreementClass</code> property.
	 * 
	 */
	@Id
	@Column(name = "AGREEMENT_CLASS", length = 10)
	@Size(max = 10, message = "{AgreementClasses.agreementClass} {Size}")
	@NotNull(message = "{AgreementClasses.agreementClass} {NotNull}")
	public java.lang.String getAgreementClass() {
		return agreementClass;
	}

	/**
	 * Sets the value of the <code>agreementClass</code> property.
	 * 
	 * @param agreementClass
	 *            the value for the <code>agreementClass</code> property
	 */
	public void setAgreementClass(java.lang.String agreementClass) {
		this.agreementClass = agreementClass;
	}

	/**
	 * Returns the value of the <code>classDesc</code> property.
	 * 
	 */
	@Column(name = "CLASS_DESC", length = 30)
	@Size(max = 30, message = "{AgreementClasses.classDesc} {Size}")
	public java.lang.String getClassDesc() {
		return classDesc;
	}

	/**
	 * Sets the value of the <code>classDesc</code> property.
	 * 
	 * @param classDesc
	 *            the value for the <code>classDesc</code> property
	 */
	public void setClassDesc(java.lang.String classDesc) {
		this.classDesc = classDesc;
	}

	/**
	 * Returns the value of the <code>productsAllowed</code> property.
	 * 
	 */
	@Column(name = "PRODUCTS_ALLOWED")
	@Type(type = "yes_no")
	public java.lang.Boolean getProductsAllowed() {
		return productsAllowed;
	}

	/**
	 * Sets the value of the <code>productsAllowed</code> property.
	 * 
	 * @param productsAllowed
	 *            the value for the <code>productsAllowed</code> property
	 */
	public void setProductsAllowed(java.lang.Boolean productsAllowed) {
		this.productsAllowed = productsAllowed;
	}

	/**
	 * Returns the value of the <code>changedBy</code> property.
	 * 
	 */
	@Column(name = "CHANGED_BY", length = 30)
	@Size(max = 30, message = "{AgreementClasses.changedBy} {Size}")
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
	 * Returns the value of the <code>enforceBillRuleDates</code> property.
	 * 
	 */
	@Column(name = "ENFORCE_BILL_RULE_DATES")
	@Type(type = "yes_no")
	public java.lang.Boolean getEnforceBillRuleDates() {
		return enforceBillRuleDates;
	}

	/**
	 * Sets the value of the <code>enforceBillRuleDates</code> property.
	 * 
	 * @param enforceBillRuleDates
	 *            the value for the <code>enforceBillRuleDates</code> property
	 */
	public void setEnforceBillRuleDates(java.lang.Boolean enforceBillRuleDates) {
		this.enforceBillRuleDates = enforceBillRuleDates;
	}

	/**
	 * Returns the value of the <code>createdBy</code> property.
	 * 
	 */
	@Column(name = "CREATED_BY", length = 30)
	@Size(max = 30, message = "{AgreementClasses.createdBy} {Size}")
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
	 * Returns a collection of <code>AgreementTypes </code> Entity Objects .
	 * This is to implement the CMR OneToMany relationship between
	 * AgreementClasses and agreementClassAgreementClasses variable in the
	 * AgreementClasses Entity Bean.
	 */
	@OneToMany(mappedBy = "agreementClassAgreementClasses", fetch = FetchType.LAZY)
	public Set<AgreementTypes> getAgreementTypesAgreementClasss() {
		return agreementTypesAgreementClasss;
	}

	/**
	 * Sets a collection of <code>AgreementTypes </code> Entity Objects . This
	 * is to implement the CMR OneToMany relationship between AgreementClasses
	 * and agreementClassAgreementClasses variable in the AgreementClasses
	 * Entity Bean.
	 */
	public void setAgreementTypesAgreementClasss(Set col) {
		this.agreementTypesAgreementClasss = col;
	}

	/**
	 * Returns a collection of <code>RoleAgrClasses </code> Entity Objects .
	 * This is to implement the CMR OneToMany relationship between
	 * AgreementClasses and agreementClassAgreementClasses variable in the
	 * AgreementClasses Entity Bean.
	 */
	@OneToMany(mappedBy = "agreementClassAgreementClasses",cascade = {
			CascadeType.REMOVE} , fetch = FetchType.LAZY)
	public Set<RoleAgrClasses> getRoleAgrClassesAgreementClasss() {
		return roleAgrClassesAgreementClasss;
	}

	/**
	 * Sets a collection of <code>RoleAgrClasses </code> Entity Objects . This
	 * is to implement the CMR OneToMany relationship between AgreementClasses
	 * and agreementClassAgreementClasses variable in the AgreementClasses
	 * Entity Bean.
	 */
	public void setRoleAgrClassesAgreementClasss(Set col) {
		this.roleAgrClassesAgreementClasss = col;
	}

	public int hashCode() {
		String code = "";

		code += agreementClass;
		code += classDesc;
		code += productsAllowed;
		code += changedBy;
		code += changedWhen;
		code += enforceBillRuleDates;
		code += createdBy;
		code += createdWhen;

		return code.hashCode();
	}

	public boolean equals(Object object) {
		AgreementClasses ent = (AgreementClasses) object;

		boolean eq = true;

		if (object == null) {
			eq = false;
		} else {
			// if both fields are null, they are equal.
			if ((this.agreementClass == null) && (ent.agreementClass == null)) {
				eq = eq && true;
			} else {
				// So not both fields are null, than if either one is null, they
				// are not equal.
				if ((this.agreementClass == null)
						|| (ent.agreementClass == null)) {
					eq = false;
				} else {
					// If neither one is null, check the equality.
					eq = eq && this.agreementClass.equals(ent.agreementClass);
				}
			}

			// if both fields are null, they are equal.
			if ((this.classDesc == null) && (ent.classDesc == null)) {
				eq = eq && true;
			} else {
				// So not both fields are null, than if either one is null, they
				// are not equal.
				if ((this.classDesc == null) || (ent.classDesc == null)) {
					eq = false;
				} else {
					// If neither one is null, check the equality.
					eq = eq && this.classDesc.equals(ent.classDesc);
				}
			}

			// if both fields are null, they are equal.
			if ((this.productsAllowed == null) && (ent.productsAllowed == null)) {
				eq = eq && true;
			} else {
				// So not both fields are null, than if either one is null, they
				// are not equal.
				if ((this.productsAllowed == null)
						|| (ent.productsAllowed == null)) {
					eq = false;
				} else {
					// If neither one is null, check the equality.
					eq = eq && this.productsAllowed.equals(ent.productsAllowed);
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

			// if both fields are null, they are equal.
			if ((this.enforceBillRuleDates == null)
					&& (ent.enforceBillRuleDates == null)) {
				eq = eq && true;
			} else {
				// So not both fields are null, than if either one is null, they
				// are not equal.
				if ((this.enforceBillRuleDates == null)
						|| (ent.enforceBillRuleDates == null)) {
					eq = false;
				} else {
					// If neither one is null, check the equality.
					eq = eq
							&& this.enforceBillRuleDates
									.equals(ent.enforceBillRuleDates);
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
		}

		return eq;
	}

	@Transient
	public int compareTo(Object o) {
		AgreementClasses ac1 = (AgreementClasses) o;
		AgreementClasses ac2 = this;

		if (ac1.getAgreementClass() != null && ac2.getAgreementClass() != null) {
			if (!ac1.getAgreementClass().equals(ac2.getAgreementClass()))
				return ac2.getAgreementClass().compareTo(
						ac1.getAgreementClass());
		} else if (ac1.getAgreementClass() != null
				&& ac2.getAgreementClass() == null)
			return -1;
		else if (ac1.getAgreementClass() == null
				&& ac2.getAgreementClass() != null)
			return +1;

		return 0;
	}
}
