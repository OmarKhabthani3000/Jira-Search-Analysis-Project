package com.airit.propworks.entity;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

//import org.hibernate.annotations.Cache;
//import org.hibernate.annotations.CacheConcurrencyStrategy;
import javax.validation.constraints.Size;
import javax.validation.constraints.NotNull;

/**
 * The CoOperatingNames entity bean.
 * 
 * @author Steven Rose - Air Transport IT Services
 * @version $Revision: 27$, $Date: 3/24/2008 4:14:01 PM${date} $
 */
@Table(name = "CO_OPERATING_NAMES")
@Entity
//@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class CoOperatingNames implements Serializable, Comparable {

	public static final String SEQ_PK_NAME = "OPERATING_NAMES_SEQ";

	public static final String SEQ_PK_FIELD = "operatingNameId";
	
	/**
	 * the composite primary key.
	 */
	private com.airit.propworks.entity.CoOperatingNamesPK primaryKey;

	private Set<Agreements> agreementssCompositeFK1;

	private CoCompany companyNumberCoCompany;

	private java.lang.String operatingName;

	private java.lang.String createdBy;

	private java.sql.Timestamp createdWhen;

	private java.lang.String changedBy;

	private java.sql.Timestamp changedWhen;

	/**
	 * Default constructor.
	 */
	public CoOperatingNames() {
	}

	/**
	 * Tells entity bean to use this class for primary key.
	 */
	@EmbeddedId
	public CoOperatingNamesPK getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(CoOperatingNamesPK id) {
		this.primaryKey = id;
	}

	/**
	 * Returns the value of the <code>companyNumber</code> property.
	 * 
	 */
	@Column(name = "COMPANY_NUMBER", insertable = false, updatable = false, length = 10)
	@Size(max = 10, message = "{CoOperatingNames.companyNumber} {Size}")
	@NotNull(message = "{CoOperatingNames.companyNumber} {NotNull}")
	public java.lang.String getCompanyNumber() {
		if (primaryKey == null) {
			return null;
		}

		return primaryKey.getCompanyNumber();
	}

	/**
	 * Sets the value of the <code>companyNumber</code> property.
	 * 
	 * @param companyNumber
	 *            the value for the <code>companyNumber</code> property
	 */
	public void setCompanyNumber(java.lang.String companyNumber) {
		if (primaryKey == null) {
			primaryKey = new CoOperatingNamesPK();
		}

		primaryKey.setCompanyNumber(companyNumber);
	}

	/**
	 * Returns the value of the <code>operatingNameId</code> property.
	 * 
	 */
	@Column(name = "OPERATING_NAME_ID", precision = 38, scale = 0, insertable = false, updatable = false)
	@NotNull(message = "{CoOperatingNames.operatingNameId} {NotNull}")
	public java.math.BigDecimal getOperatingNameId() {
		if (primaryKey == null) {
			return null;
		}

		return primaryKey.getOperatingNameId();
	}

	/**
	 * Sets the value of the <code>operatingNameId</code> property.
	 * 
	 * @param operatingNameId
	 *            the value for the <code>operatingNameId</code> property
	 */
	public void setOperatingNameId(java.math.BigDecimal operatingNameId) {
		if (primaryKey == null) {
			primaryKey = new CoOperatingNamesPK();
		}
		primaryKey.setOperatingNameId(operatingNameId);
	}

	/**
	 * Returns the value of the <code>operatingName</code> property.
	 * 
	 */
	@Column(name = "OPERATING_NAME", length = 50)
	@Size(max = 50, message = "{CoOperatingNames.operatingName} {Size}")
	@NotNull(message = "{CoOperatingNames.operatingName} {NotNull}")
	public java.lang.String getOperatingName() {
		return operatingName;
	}

	/**
	 * Sets the value of the <code>operatingName</code> property.
	 * 
	 * @param operatingName
	 *            the value for the <code>operatingName</code> property
	 */
	public void setOperatingName(java.lang.String operatingName) {
		this.operatingName = operatingName;
	}

	/**
	 * Returns the value of the <code>createdBy</code> property.
	 * 
	 */
	@Column(name = "CREATED_BY", length = 30)
	@Size(max = 30, message = "{CoOperatingNames.createdBy} {Size}")
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
	 * Returns the value of the <code>changedBy</code> property.
	 * 
	 */
	@Column(name = "CHANGED_BY", length = 30)
	@Size(max = 30, message = "{CoOperatingNames.changedBy} {Size}")
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
	 * Returns the <code>CoCompany </code> Entity Object . This is to implement
	 * the CMR ManyToOne relationship between FKCOMPANY_NUMBER and
	 * PKCOMPANY_NUMBER in the CoCompany Entity Bean.
	 */
	@ManyToOne
	@JoinColumn(name = "COMPANY_NUMBER", referencedColumnName = "COMPANY_NUMBER", insertable = false, updatable = false)
	public CoCompany getCompanyNumberCoCompany() {
		return companyNumberCoCompany;
	}

	/**
	 * Sets the <code>CoCompany </code> Entity Object . This is to implement
	 * the CMR ManyToOne relationship between FKCOMPANY_NUMBER and
	 * PKCOMPANY_NUMBER in the CoCompany Entity Bean.
	 */
	public void setCompanyNumberCoCompany(CoCompany cocompany) {
		this.companyNumberCoCompany = cocompany;
	}

	/**
	 * Returns a collection of <code>Agreements </code> Entity Objects . This
	 * is to implement the CMR OneToMany relationship between CoOperatingNames
	 * and Composite CoOperatingNames variable in the CoOperatingNames Entity
	 * Bean.
	 */
	@OneToMany(mappedBy = "coOperatingNamesCompositeFK1", fetch = FetchType.LAZY)
	public Set<Agreements> getAgreementssCompositeFK1() {
		return agreementssCompositeFK1;
	}

	/**
	 * Sets a collection of <code>Agreements </code> Entity Objects . This is
	 * to implement the CMR OneToMany relationship between CoOperatingNames and
	 * operatingNameIdCoOperatingNames variable in the CoOperatingNames Entity
	 * Bean.
	 */
	public void setAgreementssCompositeFK1(Set col) {
		this.agreementssCompositeFK1 = col;
	}

	@Transient
	public String getCompanyName() {
		if (getCompanyNumberCoCompany() != null) {
			return getCompanyNumberCoCompany().getCompanyName();
		}

		return null;
	}

	@Transient
	public String getOperatingNameIdAsString() {
		if (getOperatingNameId() != null) {
			return getOperatingNameId().toString();
		}
		return null;
	}

	public int hashCode() {
		String code = "";
                
                code += primaryKey != null ? primaryKey.hashCode(): "";
		code += operatingName;
		code += createdBy;
		code += createdWhen;
		code += changedBy;
		code += changedWhen;

		return code.hashCode();
	}

	public boolean equals(Object object) {
		CoOperatingNames ent = (CoOperatingNames) object;

		boolean eq = true;

		if (object == null) {
			eq = false;
		} else {
			// if both fields are null, they are equal.
			if ((this.primaryKey == null) && (ent.primaryKey == null)) {
				eq = eq && true;
			} else {
				// So not both fields are null, than if either one is null, they
				// are not equal.
				if ((this.primaryKey == null) || (ent.primaryKey == null)) {
					eq = false;
				} else {
					// If neither one is null, check the equality.
					eq = eq && this.primaryKey.equals(ent.primaryKey);
				}
			}

			// if both fields are null, they are equal.
			if ((this.operatingName == null) && (ent.operatingName == null)) {
				eq = eq && true;
			} else {
				// So not both fields are null, than if either one is null, they
				// are not equal.
				if ((this.operatingName == null) || (ent.operatingName == null)) {
					eq = false;
				} else {
					// If neither one is null, check the equality.
					eq = eq && this.operatingName.equals(ent.operatingName);
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
		}

		return eq;
	}

	@Transient
	public int compareTo(Object o) {
		CoOperatingNames con1 = (CoOperatingNames) o;
		CoOperatingNames con2 = this;

		if (con1.getOperatingNameId() != null
				&& con2.getOperatingNameId() != null) {
			if (!con1.getOperatingNameId().equals(con2.getOperatingNameId()))
				return con2.getOperatingNameId().compareTo(
						con1.getOperatingNameId());
		} else if (con1.getOperatingNameId() != null
				&& con2.getOperatingNameId() == null)
			return -1;
		else if (con1.getOperatingNameId() == null
				&& con2.getOperatingNameId() != null)
			return +1;

		return 0;
	}
}
