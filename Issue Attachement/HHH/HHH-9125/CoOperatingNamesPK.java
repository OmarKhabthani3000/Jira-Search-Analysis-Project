/**
 * The CoOperatingNames entity bean primary key class .
 *
 * @author Steven Rose - Air Transport IT Services
 * @version $Revision: 13$, $Date: 9/5/2006 1:31:28 PM$
 */
package com.airit.propworks.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Transient;

/**
 * The com.airit.propworks.entity.cooperatingnames.CoOperatingNamesPK composite
 * primary key.
 * 
 * @author Steven Rose - Air Transport IT Services
 * @version $Revision: 13$, $Date: 9/5/2006 1:31:28 PM$
 * 
 */
@Embeddable
public class CoOperatingNamesPK implements java.io.Serializable, Comparable {
	static final long serialVersionUID = 42L;

	public java.lang.String companyNumber;

	public java.math.BigDecimal operatingNameId;

	public CoOperatingNamesPK() {
	}

	public CoOperatingNamesPK(java.lang.String companyNumber,
			java.math.BigDecimal operatingNameId) {
		this.companyNumber = companyNumber;
		this.operatingNameId = operatingNameId;
	}

	@Column(name = "COMPANY_NUMBER", length = 10)
	public java.lang.String getCompanyNumber() {
		return companyNumber;
	}

	public void setCompanyNumber(java.lang.String companyNumber) {
		this.companyNumber = companyNumber;
	}

	@Column(name = "OPERATING_NAME_ID", precision = 38, scale = 0)
	public java.math.BigDecimal getOperatingNameId() {
		return operatingNameId;
	}

	public void setOperatingNameId(java.math.BigDecimal operatingNameId) {
		this.operatingNameId = operatingNameId;
	}

	@Transient
	public String getPrimaryKey() {
		String primaryKey = "";
		primaryKey = primaryKey + getCompanyNumber();
		primaryKey = primaryKey + getOperatingNameId().toString();

		return primaryKey;
	}

	public int hashCode() {
		String code = "";
		code += companyNumber;
		code += operatingNameId;

		return code.hashCode();
	}

	public boolean equals(Object object) {
		CoOperatingNamesPK pk = (CoOperatingNamesPK) object;

		boolean eq = true;

		if (object == null) {
			eq = false;
		} else {
			// if both fields are null, they are equal.
			if ((this.companyNumber == null) && (pk.companyNumber == null)) {
				eq = eq && true;
			} else {
				// So not both fields are null, than if either one is null, they
				// are not equal.
				if ((this.companyNumber == null) || (pk.companyNumber == null)) {
					eq = false;
				} else {
					// If neither one is null, check the equality.
					eq = eq && this.companyNumber.equals(pk.companyNumber);
				}
			}

			// if both fields are null, they are equal.
			if ((this.operatingNameId == null) && (pk.operatingNameId == null)) {
				eq = eq && true;
			} else {
				// So not both fields are null, than if either one is null, they
				// are not equal.
				if ((this.operatingNameId == null)
						|| (pk.operatingNameId == null)) {
					eq = false;
				} else {
					// If neither one is null, check the equality.
					eq = eq && this.operatingNameId.equals(pk.operatingNameId);
				}
			}
		}

		return eq;
	}

	@Transient
	public int compareTo(Object obj) {
		CoOperatingNamesPK that = (CoOperatingNamesPK) obj;

		// Order of primary key: companyNumber, operatingNameId

		if (this.getCompanyNumber() != null && that.getCompanyNumber() != null) {
			if (!this.getCompanyNumber().equals(that.getCompanyNumber())) {
				return this.getCompanyNumber().compareTo(
						that.getCompanyNumber());
			}
		} else if (this.getCompanyNumber() == null
				&& that.getCompanyNumber() != null) {
			return -1;
		} else if (this.getCompanyNumber() != null
				&& that.getCompanyNumber() == null) {
			return +1;
		}

		if (this.getOperatingNameId() != null
				&& that.getOperatingNameId() != null) {
			if (!this.getOperatingNameId().equals(that.getOperatingNameId())) {
				return this.getOperatingNameId().compareTo(
						that.getOperatingNameId());
			}
		} else if (this.getOperatingNameId() == null
				&& that.getOperatingNameId() != null) {
			return -1;
		} else if (this.getOperatingNameId() != null
				&& that.getOperatingNameId() == null) {
			return +1;
		}

		return 0;
	}
}
