package com.airit.propworks.entity;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Type;
import javax.validation.constraints.Size;
import javax.validation.constraints.NotNull;

/**
 * The AgreementTypes entity bean.
 * 
 * @author Steven Rose - Air Transport IT Services
 * @version $Revision: 16$, $Date: 1/23/2008 9:36:19 AM${date} $
 */
@Table(name = "AGREEMENT_TYPES")
@Entity
public class AgreementTypes implements Serializable {
	private Set<Agreements> agreementsAgreementTypes;

	private AgreementClasses agreementClassAgreementClasses;

	private java.lang.String agreementType;

	private java.lang.String typeDescription;

	private java.lang.Boolean confidentialFlag;

	private java.lang.String agreementClass;

	private java.lang.String changedBy;

	private java.sql.Timestamp changedWhen;

	private java.lang.Boolean datesUsed;

	private java.lang.Boolean billRulesUsed;

	private java.lang.Boolean leaseholdsUsed;

	private java.lang.Boolean provisionsUsed;

	private java.lang.Boolean maintenaceRespUsed;

	private java.lang.Boolean utilityRespUsed;

	private java.lang.Boolean productCategoriesUsed;

	private java.lang.Boolean suretiesUsed;
        
        private java.lang.Boolean insuranceUsed;

	private java.lang.Boolean invoicesUsed;

	private java.lang.Boolean amendmentsUsed;

	private java.lang.Boolean activitiesUsed;

	private java.lang.Boolean eventsUsed;

	private java.lang.String createdBy;

	private java.sql.Timestamp createdWhen;

	/**
	 * Default constructor.
	 */
	public AgreementTypes() {
	}

	/**
	 * Set the primary key.
	 * 
	 * @param primaryKey
	 *            the primary key
	 */
	public void setPrimaryKey(java.lang.String primaryKey) {
		setAgreementType(primaryKey);
	}

	/**
	 * Return the primary key.
	 * 
	 * @return java.lang.String with the primary key.
	 */
	@Transient
	public java.lang.String getPrimaryKey() {
		return getAgreementType();
	}

	/**
	 * Returns the value of the <code>agreementType</code> property.
	 * 
	 */
	@Id
	@Column(name = "AGREEMENT_TYPE", length = 10)
	@Size(max = 10, message = "{AgreementTypes.agreementType} {Size}")
	@NotNull(message = "{AgreementTypes.agreementType} {NotNull}")
	public java.lang.String getAgreementType() {
		return agreementType;
	}

	/**
	 * Sets the value of the <code>agreementType</code> property.
	 * 
	 * @param agreementType
	 *            the value for the <code>agreementType</code> property
	 */
	public void setAgreementType(java.lang.String agreementType) {
		this.agreementType = agreementType;
	}

	/**
	 * Returns the value of the <code>typeDescription</code> property.
	 * 
	 */
	@Column(name = "TYPE_DESCRIPTION", length = 35)
	@Size(max = 35, message = "{AgreementTypes.typeDescription} {Size}")
	@NotNull(message = "{AgreementTypes.typeDescription} {NotNull}")
	public java.lang.String getTypeDescription() {
		return typeDescription;
	}

	/**
	 * Sets the value of the <code>typeDescription</code> property.
	 * 
	 * @param typeDescription
	 *            the value for the <code>typeDescription</code> property
	 */
	public void setTypeDescription(java.lang.String typeDescription) {
		this.typeDescription = typeDescription;
	}

	/**
	 * Returns the value of the <code>confidentialFlag</code> property.
	 * 
	 */
	@Column(name = "CONFIDENTIAL_FLAG")
	@Type(type = "yes_no")
	@NotNull(message = "{AgreementTypes.confidentialFlag} {NotNull}")
	public java.lang.Boolean getConfidentialFlag() {
		return confidentialFlag;
	}

	/**
	 * Sets the value of the <code>confidentialFlag</code> property.
	 * 
	 * @param confidentialFlag
	 *            the value for the <code>confidentialFlag</code> property
	 */
	public void setConfidentialFlag(java.lang.Boolean confidentialFlag) {
		this.confidentialFlag = confidentialFlag;
	}

	/**
	 * Returns the value of the <code>agreementClass</code> property.
	 * 
	 */
	@Column(name = "AGREEMENT_CLASS", length = 10)
	@Size(max = 10, message = "{AgreementTypes.agreementClass} {Size}")
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
	 * Returns the value of the <code>changedBy</code> property.
	 * 
	 */
	@Column(name = "CHANGED_BY", length = 30)
	@Size(max = 30, message = "{AgreementTypes.changedBy} {Size}")
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
	 * Returns the value of the <code>datesUsed</code> property.
	 * 
	 */
	@Column(name = "DATES_USED")
	@Type(type = "yes_no")
	@NotNull(message = "{AgreementTypes.datesUsed} {NotNull}")
	public java.lang.Boolean getDatesUsed() {
		return datesUsed;
	}

	/**
	 * Sets the value of the <code>datesUsed</code> property.
	 * 
	 * @param datesUsed
	 *            the value for the <code>datesUsed</code> property
	 */
	public void setDatesUsed(java.lang.Boolean datesUsed) {
		this.datesUsed = datesUsed;
	}

	/**
	 * Returns the value of the <code>billRulesUsed</code> property.
	 * 
	 */
	@Column(name = "BILL_RULES_USED")
	@Type(type = "yes_no")
	@NotNull(message = "{AgreementTypes.billRulesUsed} {NotNull}")
	public java.lang.Boolean getBillRulesUsed() {
		return billRulesUsed;
	}

	/**
	 * Sets the value of the <code>billRulesUsed</code> property.
	 * 
	 * @param billRulesUsed
	 *            the value for the <code>billRulesUsed</code> property
	 */
	public void setBillRulesUsed(java.lang.Boolean billRulesUsed) {
		this.billRulesUsed = billRulesUsed;
	}

	/**
	 * Returns the value of the <code>leaseholdsUsed</code> property.
	 * 
	 */
	@Column(name = "LEASEHOLDS_USED")
	@Type(type = "yes_no")
	@NotNull(message = "{AgreementTypes.leaseholdsUsed} {NotNull}")
	public java.lang.Boolean getLeaseholdsUsed() {
		return leaseholdsUsed;
	}

	/**
	 * Sets the value of the <code>leaseholdsUsed</code> property.
	 * 
	 * @param leaseholdsUsed
	 *            the value for the <code>leaseholdsUsed</code> property
	 */
	public void setLeaseholdsUsed(java.lang.Boolean leaseholdsUsed) {
		this.leaseholdsUsed = leaseholdsUsed;
	}

	/**
	 * Returns the value of the <code>provisionsUsed</code> property.
	 * 
	 */
	@Column(name = "PROVISIONS_USED")
	@Type(type = "yes_no")
	@NotNull(message = "{AgreementTypes.provisionsUsed} {NotNull}")
	public java.lang.Boolean getProvisionsUsed() {
		return provisionsUsed;
	}

	/**
	 * Sets the value of the <code>provisionsUsed</code> property.
	 * 
	 * @param provisionsUsed
	 *            the value for the <code>provisionsUsed</code> property
	 */
	public void setProvisionsUsed(java.lang.Boolean provisionsUsed) {
		this.provisionsUsed = provisionsUsed;
	}

	/**
	 * Returns the value of the <code>maintenaceRespUsed</code> property.
	 * 
	 */
	@Column(name = "MAINTENACE_RESP_USED")
	@Type(type = "yes_no")
	@NotNull(message = "{AgreementTypes.maintenaceRespUsed} {NotNull}")
	public java.lang.Boolean getMaintenaceRespUsed() {
		return maintenaceRespUsed;
	}

	/**
	 * Sets the value of the <code>maintenaceRespUsed</code> property.
	 * 
	 * @param maintenaceRespUsed
	 *            the value for the <code>maintenaceRespUsed</code> property
	 */
	public void setMaintenaceRespUsed(java.lang.Boolean maintenaceRespUsed) {
		this.maintenaceRespUsed = maintenaceRespUsed;
	}

	/**
	 * Returns the value of the <code>utilityRespUsed</code> property.
	 * 
	 */
	@Column(name = "UTILITY_RESP_USED")
	@Type(type = "yes_no")
	@NotNull(message = "{AgreementTypes.utilityRespUsed} {NotNull}")
	public java.lang.Boolean getUtilityRespUsed() {
		return utilityRespUsed;
	}

	/**
	 * Sets the value of the <code>utilityRespUsed</code> property.
	 * 
	 * @param utilityRespUsed
	 *            the value for the <code>utilityRespUsed</code> property
	 */
	public void setUtilityRespUsed(java.lang.Boolean utilityRespUsed) {
		this.utilityRespUsed = utilityRespUsed;
	}

	/**
	 * Returns the value of the <code>productCategoriesUsed</code> property.
	 * 
	 */
	@Column(name = "PRODUCT_CATEGORIES_USED")
	@Type(type = "yes_no")
	@NotNull(message = "{AgreementTypes.productCategoriesUsed} {NotNull}")
	public java.lang.Boolean getProductCategoriesUsed() {
		return productCategoriesUsed;
	}

	/**
	 * Sets the value of the <code>productCategoriesUsed</code> property.
	 * 
	 * @param productCategoriesUsed
	 *            the value for the <code>productCategoriesUsed</code>
	 *            property
	 */
	public void setProductCategoriesUsed(java.lang.Boolean productCategoriesUsed) {
		this.productCategoriesUsed = productCategoriesUsed;
	}

	/**
	 * Returns the value of the <code>suretiesUsed</code> property.
	 * 
	 */
	@Column(name = "SURETIES_USED")
	@Type(type = "yes_no")
	@NotNull(message = "{AgreementTypes.suretiesUsed} {NotNull}")
	public java.lang.Boolean getSuretiesUsed() {
		return suretiesUsed;
	}

	/**
	 * Sets the value of the <code>suretiesUsed</code> property.
	 * 
	 * @param suretiesUsed
	 *            the value for the <code>suretiesUsed</code> property
	 */
	public void setSuretiesUsed(java.lang.Boolean suretiesUsed) {
		this.suretiesUsed = suretiesUsed;
	}

        
        /**
	 * Returns the value of the <code>insuranceUsed</code> property.
	 * 
	 */
	@Column(name = "INSURANCE_USED")
	@Type(type = "yes_no")
	@NotNull(message = "{AgreementTypes.insuranceUsed} {NotNull}")
	public java.lang.Boolean getInsuranceUsed() {
		return insuranceUsed;
	}

	/**
	 * Sets the value of the <code>insuranceUsed</code> property.
	 * 
	 * @param insuranceUsed
	 *            the value for the <code>insuranceUsed</code> property
	 */
	public void setInsuranceUsed(java.lang.Boolean insuranceUsed) {
		this.insuranceUsed = insuranceUsed;
	}
        
	/**
	 * Returns the value of the <code>invoicesUsed</code> property.
	 * 
	 */
	@Column(name = "INVOICES_USED")
	@Type(type = "yes_no")
	@NotNull(message = "{AgreementTypes.invoicesUsed} {NotNull}")
	public java.lang.Boolean getInvoicesUsed() {
		return invoicesUsed;
	}

	/**
	 * Sets the value of the <code>invoicesUsed</code> property.
	 * 
	 * @param invoicesUsed
	 *            the value for the <code>invoicesUsed</code> property
	 */
	public void setInvoicesUsed(java.lang.Boolean invoicesUsed) {
		this.invoicesUsed = invoicesUsed;
	}

	/**
	 * Returns the value of the <code>amendmentsUsed</code> property.
	 * 
	 */
	@Column(name = "AMENDMENTS_USED")
	@Type(type = "yes_no")
	@NotNull(message = "{AgreementTypes.amendmentsUsed} {NotNull}")
	public java.lang.Boolean getAmendmentsUsed() {
		return amendmentsUsed;
	}

	/**
	 * Sets the value of the <code>amendmentsUsed</code> property.
	 * 
	 * @param amendmentsUsed
	 *            the value for the <code>amendmentsUsed</code> property
	 */
	public void setAmendmentsUsed(java.lang.Boolean amendmentsUsed) {
		this.amendmentsUsed = amendmentsUsed;
	}

	/**
	 * Returns the value of the <code>activitiesUsed</code> property.
	 * 
	 */
	@Column(name = "ACTIVITIES_USED")
	@Type(type = "yes_no")
	@NotNull(message = "{AgreementTypes.activitiesUsed} {NotNull}")
	public java.lang.Boolean getActivitiesUsed() {
		return activitiesUsed;
	}

	/**
	 * Sets the value of the <code>activitiesUsed</code> property.
	 * 
	 * @param activitiesUsed
	 *            the value for the <code>activitiesUsed</code> property
	 */
	public void setActivitiesUsed(java.lang.Boolean activitiesUsed) {
		this.activitiesUsed = activitiesUsed;
	}

	/**
	 * Returns the value of the <code>eventsUsed</code> property.
	 * 
	 */
	@Column(name = "EVENTS_USED")
	@Type(type = "yes_no")
	@NotNull(message = "{AgreementTypes.eventsUsed} {NotNull}")
	public java.lang.Boolean getEventsUsed() {
		return eventsUsed;
	}

	/**
	 * Sets the value of the <code>eventsUsed</code> property.
	 * 
	 * @param eventsUsed
	 *            the value for the <code>eventsUsed</code> property
	 */
	public void setEventsUsed(java.lang.Boolean eventsUsed) {
		this.eventsUsed = eventsUsed;
	}

	/**
	 * Returns the value of the <code>createdBy</code> property.
	 * 
	 */
	@Column(name = "CREATED_BY", length = 30)
	@Size(max = 30, message = "{AgreementTypes.createdBy} {Size}")
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
	 * Returns the <code>AgreementClasses </code> Entity Object . This is to
	 * implement the CMR ManyToOne relationship between FKAGREEMENT_CLASS and
	 * PKAGREEMENT_CLASS in the AgreementClasses Entity Bean.
	 */
	@ManyToOne
	@JoinColumn(name = "AGREEMENT_CLASS", referencedColumnName = "AGREEMENT_CLASS", insertable = false, updatable = false)
	public AgreementClasses getAgreementClassAgreementClasses() {
		return agreementClassAgreementClasses;
	}

	/**
	 * Sets the <code>AgreementClasses </code> Entity Object . This is to
	 * implement the CMR ManyToOne relationship between FKAGREEMENT_CLASS and
	 * PKAGREEMENT_CLASS in the AgreementClasses Entity Bean.
	 */
	public void setAgreementClassAgreementClasses(
			AgreementClasses agreementclasses) {
		this.agreementClassAgreementClasses = agreementclasses;
	}

	/**
	 * Returns a collection of <code>Agreements </code> Entity Objects . This
	 * is to implement the CMR OneToMany relationship between AgreementTypes and
	 * agreementTypeAgreementTypes variable in the AgreementTypes Entity Bean.
	 */
	@OneToMany(mappedBy = "agreementTypeAgreementTypes", fetch = FetchType.LAZY)
	public Set<Agreements> getAgreementsAgreementTypes() {
		return agreementsAgreementTypes;
	}

	/**
	 * Sets a collection of <code>Agreements </code> Entity Objects . This is
	 * to implement the CMR OneToMany relationship between AgreementTypes and
	 * agreementTypeAgreementTypes variable in the AgreementTypes Entity Bean.
	 */
	public void setAgreementsAgreementTypes(Set col) {
		this.agreementsAgreementTypes = col;
	}

	@Transient
	public String getAgreementClassDescription() {
		if (getAgreementClassAgreementClasses() != null) {
			return getAgreementClassAgreementClasses().getClassDesc();
		}
		return null;
	}

	public int hashCode() {
		String code = "";

		code += agreementType;
		code += typeDescription;
		code += confidentialFlag;
		code += agreementClass;
		code += changedBy;
		code += changedWhen;
		code += datesUsed;
		code += billRulesUsed;
		code += leaseholdsUsed;
		code += provisionsUsed;
		code += maintenaceRespUsed;
		code += utilityRespUsed;
		code += productCategoriesUsed;
		code += suretiesUsed;
                code += insuranceUsed;
		code += invoicesUsed;
		code += amendmentsUsed;
		code += activitiesUsed;
		code += createdBy;
		code += createdWhen;

		return code.hashCode();
	}

	public boolean equals(Object object) {
		AgreementTypes ent = (AgreementTypes) object;

		boolean eq = true;

		if (object == null) {
			eq = false;
		} else {
			// if both fields are null, they are equal.
			if ((this.agreementType == null) && (ent.agreementType == null)) {
				eq = eq && true;
			} else {
				// So not both fields are null, than if either one is null, they
				// are not equal.
				if ((this.agreementType == null) || (ent.agreementType == null)) {
					eq = false;
				} else {
					// If neither one is null, check the equality.
					eq = eq && this.agreementType.equals(ent.agreementType);
				}
			}

			// if both fields are null, they are equal.
			if ((this.typeDescription == null) && (ent.typeDescription == null)) {
				eq = eq && true;
			} else {
				// So not both fields are null, than if either one is null, they
				// are not equal.
				if ((this.typeDescription == null)
						|| (ent.typeDescription == null)) {
					eq = false;
				} else {
					// If neither one is null, check the equality.
					eq = eq && this.typeDescription.equals(ent.typeDescription);
				}
			}

			// if both fields are null, they are equal.
			if ((this.confidentialFlag == null)
					&& (ent.confidentialFlag == null)) {
				eq = eq && true;
			} else {
				// So not both fields are null, than if either one is null, they
				// are not equal.
				if ((this.confidentialFlag == null)
						|| (ent.confidentialFlag == null)) {
					eq = false;
				} else {
					// If neither one is null, check the equality.
					eq = eq
							&& this.confidentialFlag
									.equals(ent.confidentialFlag);
				}
			}

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
			if ((this.datesUsed == null) && (ent.datesUsed == null)) {
				eq = eq && true;
			} else {
				// So not both fields are null, than if either one is null, they
				// are not equal.
				if ((this.datesUsed == null) || (ent.datesUsed == null)) {
					eq = false;
				} else {
					// If neither one is null, check the equality.
					eq = eq && this.datesUsed.equals(ent.datesUsed);
				}
			}

			// if both fields are null, they are equal.
			if ((this.billRulesUsed == null) && (ent.billRulesUsed == null)) {
				eq = eq && true;
			} else {
				// So not both fields are null, than if either one is null, they
				// are not equal.
				if ((this.billRulesUsed == null) || (ent.billRulesUsed == null)) {
					eq = false;
				} else {
					// If neither one is null, check the equality.
					eq = eq && this.billRulesUsed.equals(ent.billRulesUsed);
				}
			}

			// if both fields are null, they are equal.
			if ((this.leaseholdsUsed == null) && (ent.leaseholdsUsed == null)) {
				eq = eq && true;
			} else {
				// So not both fields are null, than if either one is null, they
				// are not equal.
				if ((this.leaseholdsUsed == null)
						|| (ent.leaseholdsUsed == null)) {
					eq = false;
				} else {
					// If neither one is null, check the equality.
					eq = eq && this.leaseholdsUsed.equals(ent.leaseholdsUsed);
				}
			}

			// if both fields are null, they are equal.
			if ((this.provisionsUsed == null) && (ent.provisionsUsed == null)) {
				eq = eq && true;
			} else {
				// So not both fields are null, than if either one is null, they
				// are not equal.
				if ((this.provisionsUsed == null)
						|| (ent.provisionsUsed == null)) {
					eq = false;
				} else {
					// If neither one is null, check the equality.
					eq = eq && this.provisionsUsed.equals(ent.provisionsUsed);
				}
			}

			// if both fields are null, they are equal.
			if ((this.maintenaceRespUsed == null)
					&& (ent.maintenaceRespUsed == null)) {
				eq = eq && true;
			} else {
				// So not both fields are null, than if either one is null, they
				// are not equal.
				if ((this.maintenaceRespUsed == null)
						|| (ent.maintenaceRespUsed == null)) {
					eq = false;
				} else {
					// If neither one is null, check the equality.
					eq = eq
							&& this.maintenaceRespUsed
									.equals(ent.maintenaceRespUsed);
				}
			}

			// if both fields are null, they are equal.
			if ((this.utilityRespUsed == null) && (ent.utilityRespUsed == null)) {
				eq = eq && true;
			} else {
				// So not both fields are null, than if either one is null, they
				// are not equal.
				if ((this.utilityRespUsed == null)
						|| (ent.utilityRespUsed == null)) {
					eq = false;
				} else {
					// If neither one is null, check the equality.
					eq = eq && this.utilityRespUsed.equals(ent.utilityRespUsed);
				}
			}

			// if both fields are null, they are equal.
			if ((this.productCategoriesUsed == null)
					&& (ent.productCategoriesUsed == null)) {
				eq = eq && true;
			} else {
				// So not both fields are null, than if either one is null, they
				// are not equal.
				if ((this.productCategoriesUsed == null)
						|| (ent.productCategoriesUsed == null)) {
					eq = false;
				} else {
					// If neither one is null, check the equality.
					eq = eq
							&& this.productCategoriesUsed
									.equals(ent.productCategoriesUsed);
				}
			}

			// if both fields are null, they are equal.
			if ((this.suretiesUsed == null)
					&& (ent.suretiesUsed == null)) {
				eq = eq && true;
			} else {
				// So not both fields are null, than if either one is null, they
				// are not equal.
				if ((this.suretiesUsed == null)
						|| (ent.suretiesUsed == null)) {
					eq = false;
				} else {
					// If neither one is null, check the equality.
					eq = eq
							&& this.suretiesUsed
									.equals(ent.suretiesUsed);
				}
			}

			// if both fields are null, they are equal.
			if ((this.insuranceUsed == null)
					&& (ent.insuranceUsed == null)) {
				eq = eq && true;
			} else {
				// So not both fields are null, than if either one is null, they
				// are not equal.
				if ((this.insuranceUsed == null)
						|| (ent.insuranceUsed == null)) {
					eq = false;
				} else {
					// If neither one is null, check the equality.
					eq = eq
							&& this.insuranceUsed
									.equals(ent.insuranceUsed);
				}
			}
			// if both fields are null, they are equal.
			if ((this.invoicesUsed == null) && (ent.invoicesUsed == null)) {
				eq = eq && true;
			} else {
				// So not both fields are null, than if either one is null, they
				// are not equal.
				if ((this.invoicesUsed == null) || (ent.invoicesUsed == null)) {
					eq = false;
				} else {
					// If neither one is null, check the equality.
					eq = eq && this.invoicesUsed.equals(ent.invoicesUsed);
				}
			}

			// if both fields are null, they are equal.
			if ((this.amendmentsUsed == null) && (ent.amendmentsUsed == null)) {
				eq = eq && true;
			} else {
				// So not both fields are null, than if either one is null, they
				// are not equal.
				if ((this.amendmentsUsed == null)
						|| (ent.amendmentsUsed == null)) {
					eq = false;
				} else {
					// If neither one is null, check the equality.
					eq = eq && this.amendmentsUsed.equals(ent.amendmentsUsed);
				}
			}

			// if both fields are null, they are equal.
			if ((this.activitiesUsed == null) && (ent.activitiesUsed == null)) {
				eq = eq && true;
			} else {
				// So not both fields are null, than if either one is null, they
				// are not equal.
				if ((this.activitiesUsed == null)
						|| (ent.activitiesUsed == null)) {
					eq = false;
				} else {
					// If neither one is null, check the equality.
					eq = eq && this.activitiesUsed.equals(ent.activitiesUsed);
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
}
