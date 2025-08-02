package com.airit.propworks.entity;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.QueryHint;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import javax.validation.constraints.Size;
import javax.validation.constraints.NotNull;

/**
 * The Agreements entity bean.
 * 
 * @author Steven Rose - Air Transport IT Services
 * @version $Revision: 34$, $Date: 6/25/2010 2:45:29 PM${date} $
 */
@Table(name = "AGREEMENTS")
@Entity
//@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
@NamedQuery(name = "allAgreements", query = "FROM Agreements e ORDER BY e.agreementNumber", hints = { @QueryHint(name = "org.hibernate.cacheable", value = "false") })
public class Agreements implements Serializable {
	
	private static final long serialVersionUID = 7L;

	public static final String SEQ_PK_NAME = "AGREEMENTS_NUMBER_SEQ";

	public static int AGREEMENTS_NUMBER_LENGTH = 6;

	private CoOperatingNames coOperatingNamesCompositeFK1;

	private CoContact coContactCompositeFK2;

	private CoCompany companyNumberCoCompany;	

	private BillingStatuses billingStatusBillingStatuses;
	
	private AgreementTypes agreementTypeAgreementTypes;

	private AgreementStatuses agreementStatusAgreementStatuses;

	private java.lang.String agreementNumber;

	private java.lang.String campus;

	private java.lang.String billingStatus;

	private java.lang.String agreementStatus;

	private java.lang.String agreementType;

	private java.lang.String companyNumber;

	private java.lang.String contractNumber;

	private java.sql.Timestamp effectiveDate;

	private java.sql.Timestamp expirationDate;

	private java.lang.Boolean subAgreementFlag;

	private java.lang.Boolean directIndirectBillFlag;

	private java.lang.String agreementComment;

	private java.lang.String masterAgreementNumber;

	private java.lang.String manageCompany;

	private java.lang.String manageContact;

	private java.lang.String agreementClass;

	private java.lang.String changedBy;

	private java.sql.Timestamp changedWhen;

	private java.lang.String followAgreementNumber;

	private java.lang.String responsibleOrganization;

	private java.math.BigDecimal operatingNameId;

	private java.lang.String createdBy;

	private java.sql.Timestamp createdWhen;

	/**
	 * Default constructor.
	 */
	public Agreements() {
	}

        public Agreements(String agreementNumber, String companyName) {
            this.agreementNumber = agreementNumber;
            setCompanyName(companyName);
        }
        
	/**
	 * Set the primary key.
	 * 
	 * @param primaryKey
	 *            the primary key
	 */
	public void setPrimaryKey(java.lang.String primaryKey) {
		setAgreementNumber(primaryKey);
	}

	/**
	 * Return the primary key.
	 * 
	 * @return java.lang.String with the primary key.
	 */
	@Transient
	public java.lang.String getPrimaryKey() {
		return getAgreementNumber();
	}

	/**
	 * Returns the value of the <code>agreementNumber</code> property.
	 * 
	 */
	@Id
	@Column(name = "AGREEMENT_NUMBER", length = 6)
	@Size(max = 6, message = "{Agreements.agreementNumber} {Size}")
	@NotNull(message = "{Agreements.agreementNumber} {NotNull}")
	public java.lang.String getAgreementNumber() {
		return agreementNumber;
	}

	/**
	 * Sets the value of the <code>agreementNumber</code> property.
	 * 
	 * @param agreementNumber
	 *            the value for the <code>agreementNumber</code> property
	 */
	public void setAgreementNumber(java.lang.String agreementNumber) {
		this.agreementNumber = agreementNumber;
	}

	/**
	 * Returns the value of the <code>campus</code> property.
	 * 
	 */
	@Column(name = "CAMPUS", length = 3)
	@Size(max = 3, message = "{Agreements.campus} {Size}")
	@NotNull(message = "{Agreements.campus} {NotNull}")
	public java.lang.String getCampus() {
		return campus;
	}

	/**
	 * Sets the value of the <code>campus</code> property.
	 * 
	 * @param campus
	 *            the value for the <code>campus</code> property
	 */
	public void setCampus(java.lang.String campus) {
		this.campus = campus;
	}

	/**
	 * Returns the value of the <code>billingStatus</code> property.
	 * 
	 */
	@Column(name = "BILLING_STATUS", length = 4)
	@Size(max = 4, message = "{Agreements.billingStatus} {Size}")
	@NotNull(message = "{Agreements.billingStatus} {NotNull}")
	public java.lang.String getBillingStatus() {
		return billingStatus;
	}

	/**
	 * Sets the value of the <code>billingStatus</code> property.
	 * 
	 * @param billingStatus
	 *            the value for the <code>billingStatus</code> property
	 */
	public void setBillingStatus(java.lang.String billingStatus) {
		this.billingStatus = billingStatus;
	}

	/**
	 * Returns the value of the <code>agreementStatus</code> property.
	 * 
	 */
	@Column(name = "AGREEMENT_STATUS", length = 4)
	@Size(max = 4, message = "{Agreements.agreementStatus} {Size}")
	@NotNull(message = "{Agreements.agreementStatus} {NotNull}")
	public java.lang.String getAgreementStatus() {
		return agreementStatus;
	}

	/**
	 * Sets the value of the <code>agreementStatus</code> property.
	 * 
	 * @param agreementStatus
	 *            the value for the <code>agreementStatus</code> property
	 */
	public void setAgreementStatus(java.lang.String agreementStatus) {
		this.agreementStatus = agreementStatus;
	}

	/**
	 * Returns the value of the <code>agreementType</code> property.
	 * 
	 */
	@Column(name = "AGREEMENT_TYPE", length = 10)
	@Size(max = 10, message = "{Agreements.agreementType} {Size}")
	@NotNull(message = "{Agreements.agreementType} {NotNull}")
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

        public void setCompanyName(String companyName) {
            if(getCompanyNumberCoCompany() == null) {
                setCompanyNumberCoCompany(new CoCompany());
            }
            
            getCompanyNumberCoCompany().setCompanyName(companyName);
        }
        
	@Transient
	public String getCompanyName() {
		if (getCompanyNumberCoCompany() == null)
			return null;

		return getCompanyNumberCoCompany().getCompanyName();
	}

	/**
	 * Returns the value of the <code>companyNumber</code> property.
	 * 
	 */
	@Column(name = "COMPANY_NUMBER", length = 10)
	@Size(max = 10, message = "{Agreements.companyNumber} {Size}")
	@NotNull(message = "{Agreements.companyNumber} {NotNull}")
	public java.lang.String getCompanyNumber() {
		return companyNumber;
	}

	/**
	 * Sets the value of the <code>companyNumber</code> property.
	 * 
	 * @param companyNumber
	 *            the value for the <code>companyNumber</code> property
	 */
	public void setCompanyNumber(java.lang.String companyNumber) {
		this.companyNumber = companyNumber;
	}

	/**
	 * Returns the value of the <code>contractNumber</code> property.
	 * 
	 */
	@Column(name = "CONTRACT_NUMBER", length = 20)
	@Size(max = 20, message = "{Agreements.contractNumber} {Size}")
	public java.lang.String getContractNumber() {
		return contractNumber;
	}

	/**
	 * Sets the value of the <code>contractNumber</code> property.
	 * 
	 * @param contractNumber
	 *            the value for the <code>contractNumber</code> property
	 */
	public void setContractNumber(java.lang.String contractNumber) {
		this.contractNumber = contractNumber;
	}

	/**
	 * Returns the value of the <code>effectiveDate</code> property.
	 * 
	 */
	@Column(name = "EFFECTIVE_DATE")
	public java.sql.Timestamp getEffectiveDate() {
		return effectiveDate;
	}

	/**
	 * Sets the value of the <code>effectiveDate</code> property.
	 * 
	 * @param effectiveDate
	 *            the value for the <code>effectiveDate</code> property
	 */
	public void setEffectiveDate(java.sql.Timestamp effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	/**
	 * Returns the value of the <code>expirationDate</code> property.
	 * 
	 */
	@Column(name = "EXPIRATION_DATE")
	public java.sql.Timestamp getExpirationDate() {
		return expirationDate;
	}

	/**
	 * Sets the value of the <code>expirationDate</code> property.
	 * 
	 * @param expirationDate
	 *            the value for the <code>expirationDate</code> property
	 */
	public void setExpirationDate(java.sql.Timestamp expirationDate) {
		this.expirationDate = expirationDate;
	}

	/**
	 * Returns the value of the <code>subAgreementFlag</code> property.
	 * 
	 */
	@Column(name = "SUB_AGREEMENT_FLAG")
	@Type(type = "yes_no")
	public java.lang.Boolean getSubAgreementFlag() {
		return subAgreementFlag;
	}

	/**
	 * Sets the value of the <code>subAgreementFlag</code> property.
	 * 
	 * @param subAgreementFlag
	 *            the value for the <code>subAgreementFlag</code> property
	 */
	public void setSubAgreementFlag(java.lang.Boolean subAgreementFlag) {
		this.subAgreementFlag = subAgreementFlag;
	}

	/**
	 * Returns the value of the <code>directIndirectBillFlag</code> property.
	 * 
	 */
	@Column(name = "DIRECT_INDIRECT_BILL_FLAG")
	@Type(type = "yes_no")
	public java.lang.Boolean getDirectIndirectBillFlag() {
		return directIndirectBillFlag;
	}

	/**
	 * Sets the value of the <code>directIndirectBillFlag</code> property.
	 * 
	 * @param directIndirectBillFlag
	 *            the value for the <code>directIndirectBillFlag</code>
	 *            property
	 */
	public void setDirectIndirectBillFlag(
			java.lang.Boolean directIndirectBillFlag) {
		this.directIndirectBillFlag = directIndirectBillFlag;
	}

	/**
	 * Returns the value of the <code>agreementComment</code> property.
	 * 
	 */
	@Column(name = "AGREEMENT_COMMENT", length = 2000)
	@Size(max = 2000, message = "{Agreements.agreementComment} {Size}")
	public java.lang.String getAgreementComment() {
		return agreementComment;
	}

	/**
	 * Sets the value of the <code>agreementComment</code> property.
	 * 
	 * @param agreementComment
	 *            the value for the <code>agreementComment</code> property
	 */
	public void setAgreementComment(java.lang.String agreementComment) {
		this.agreementComment = agreementComment;
	}

	/**
	 * Returns the value of the <code>masterAgreementNumber</code> property.
	 * 
	 */
	@Column(name = "MASTER_AGREEMENT_NUMBER", length = 6)
	@Size(max = 6, message = "{Agreements.masterAgreementNumber} {Size}")
	public java.lang.String getMasterAgreementNumber() {
		return masterAgreementNumber;
	}

	/**
	 * Sets the value of the <code>masterAgreementNumber</code> property.
	 * 
	 * @param masterAgreementNumber
	 *            the value for the <code>masterAgreementNumber</code>
	 *            property
	 */
	public void setMasterAgreementNumber(java.lang.String masterAgreementNumber) {
		this.masterAgreementNumber = masterAgreementNumber;
	}

	/**
	 * Returns the value of the <code>manageCompany</code> property.
	 * 
	 */
	@Column(name = "MANAGE_COMPANY", length = 10)
	@Size(max = 10, message = "{Agreements.manageCompany} {Size}")
	public java.lang.String getManageCompany() {
		return manageCompany;
	}

	/**
	 * Sets the value of the <code>manageCompany</code> property.
	 * 
	 * @param manageCompany
	 *            the value for the <code>manageCompany</code> property
	 */
	public void setManageCompany(java.lang.String manageCompany) {
		this.manageCompany = manageCompany;
	}

	/**
	 * Returns the value of the <code>manageContact</code> property.
	 * 
	 */
	@Column(name = "MANAGE_CONTACT", length = 10)
	@Size(max = 10, message = "{Agreements.manageContact} {Size}")
	public java.lang.String getManageContact() {
		return manageContact;
	}

	/**
	 * Sets the value of the <code>manageContact</code> property.
	 * 
	 * @param manageContact
	 *            the value for the <code>manageContact</code> property
	 */
	public void setManageContact(java.lang.String manageContact) {
		this.manageContact = manageContact;
	}

	/**
	 * Returns the value of the <code>agreementClass</code> property.
	 * 
	 */
	@Column(name = "AGREEMENT_CLASS", length = 10)
	@Size(max = 10, message = "{Agreements.agreementClass} {Size}")
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
	@Size(max = 30, message = "{Agreements.changedBy} {Size}")
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
	 * Returns the value of the <code>followAgreementNumber</code> property.
	 * 
	 */
	@Column(name = "FOLLOW_AGREEMENT_NUMBER", length = 6)
	@Size(max = 6, message = "{Agreements.followAgreementNumber} {Size}")
	public java.lang.String getFollowAgreementNumber() {
		return followAgreementNumber;
	}

	/**
	 * Sets the value of the <code>followAgreementNumber</code> property.
	 * 
	 * @param followAgreementNumber
	 *            the value for the <code>followAgreementNumber</code>
	 *            property
	 */
	public void setFollowAgreementNumber(java.lang.String followAgreementNumber) {
		this.followAgreementNumber = followAgreementNumber;
	}

	/**
	 * Returns the value of the <code>responsibleOrganization</code> property.
	 * 
	 */
	@Column(name = "RESPONSIBLE_ORGANIZATION", length = 20)
	@Size(max = 20, message = "{Agreements.responsibleOrganization} {Size}")
	public java.lang.String getResponsibleOrganization() {
		return responsibleOrganization;
	}

	/**
	 * Sets the value of the <code>responsibleOrganization</code> property.
	 * 
	 * @param responsibleOrganization
	 *            the value for the <code>responsibleOrganization</code>
	 *            property
	 */
	public void setResponsibleOrganization(
			java.lang.String responsibleOrganization) {
		this.responsibleOrganization = responsibleOrganization;
	}

	/**
	 * Returns the value of the <code>operatingNameId</code> property.
	 * 
	 */
	@Column(name = "OPERATING_NAME_ID", precision = 38, scale = 0)
	public java.math.BigDecimal getOperatingNameId() {
		return operatingNameId;
	}

	/**
	 * Sets the value of the <code>operatingNameId</code> property.
	 * 
	 * @param operatingNameId
	 *            the value for the <code>operatingNameId</code> property
	 */
	public void setOperatingNameId(java.math.BigDecimal operatingNameId) {
		this.operatingNameId = operatingNameId;
	}

	/**
	 * Returns the value of the <code>createdBy</code> property.
	 * 
	 */
	@Column(name = "CREATED_BY", length = 30)
	@Size(max = 30, message = "{Agreements.createdBy} {Size}")
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
	 * Returns the <code>BillingStatuses </code> Entity Object . This is to
	 * implement the CMR ManyToOne relationship between FKBILLING_STATUS and
	 * PKBILLING_STATUS in the BillingStatuses Entity Bean.
	 */
	@ManyToOne
	@JoinColumn(name = "BILLING_STATUS", referencedColumnName = "BILLING_STATUS", insertable = false, updatable = false)
	public BillingStatuses getBillingStatusBillingStatuses() {
		return billingStatusBillingStatuses;
	}

	/**
	 * Sets the <code>BillingStatuses </code> Entity Object . This is to
	 * implement the CMR ManyToOne relationship between FKBILLING_STATUS and
	 * PKBILLING_STATUS in the BillingStatuses Entity Bean.
	 */
	public void setBillingStatusBillingStatuses(BillingStatuses billingstatuses) {
		this.billingStatusBillingStatuses = billingstatuses;
	}

	

	/**
	 * Returns the <code>AgreementTypes </code> Entity Object . This is to
	 * implement the CMR ManyToOne relationship between FKAGREEMENT_TYPE and
	 * PKAGREEMENT_TYPE in the AgreementTypes Entity Bean.
	 */
	@ManyToOne
	@JoinColumn(name = "AGREEMENT_TYPE", referencedColumnName = "AGREEMENT_TYPE", insertable = false, updatable = false)
	public AgreementTypes getAgreementTypeAgreementTypes() {
		return agreementTypeAgreementTypes;
	}

	/**
	 * Sets the <code>AgreementTypes </code> Entity Object . This is to
	 * implement the CMR ManyToOne relationship between FKAGREEMENT_TYPE and
	 * PKAGREEMENT_TYPE in the AgreementTypes Entity Bean.
	 */
	public void setAgreementTypeAgreementTypes(AgreementTypes agreementtypes) {
		this.agreementTypeAgreementTypes = agreementtypes;
	}

	/**
	 * Returns the <code>AgreementStatuses </code> Entity Object . This is to
	 * implement the CMR ManyToOne relationship between FKAGREEMENT_STATUS and
	 * PKAGREEMENT_STATUS in the AgreementStatuses Entity Bean.
	 */
	@ManyToOne
	@JoinColumn(name = "AGREEMENT_STATUS", referencedColumnName = "AGREEMENT_STATUS", insertable = false, updatable = false)
	public AgreementStatuses getAgreementStatusAgreementStatuses() {
		return agreementStatusAgreementStatuses;
	}

	/**
	 * Sets the <code>AgreementStatuses </code> Entity Object . This is to
	 * implement the CMR ManyToOne relationship between FKAGREEMENT_STATUS and
	 * PKAGREEMENT_STATUS in the AgreementStatuses Entity Bean.
	 */
	public void setAgreementStatusAgreementStatuses(
			AgreementStatuses agreementstatuses) {
		this.agreementStatusAgreementStatuses = agreementstatuses;
	}



	/**
	 * Returns the <code>CoOperatingNames </code> Entity Object . This is to
	 * implement the CMR ManyToOne relationship between FKOPERATING_NAME_ID and
	 * PK${Relation.Name} in the CoOperatingNames Entity Bean.
	 */
	@ManyToOne
	@JoinColumns( {
			@JoinColumn(name = "OPERATING_NAME_ID", referencedColumnName = "OPERATING_NAME_ID", insertable = false, updatable = false),
			@JoinColumn(name = "COMPANY_NUMBER", referencedColumnName = "COMPANY_NUMBER", insertable = false, updatable = false) })
	public CoOperatingNames getCoOperatingNamesCompositeFK1() {
		return coOperatingNamesCompositeFK1;
	}

	/**
	 * Sets the <code>CoOperatingNames </code> Entity Object . This is to
	 * implement the CMR ManyToOne relationship between FKOPERATING_NAME_ID and
	 * PKAGREEMENT_NUMBER in the CoOperatingNames Entity Bean.
	 */
	public void setCoOperatingNamesCompositeFK1(
			CoOperatingNames cooperatingnames) {
		this.coOperatingNamesCompositeFK1 = cooperatingnames;
	}

	/**
	 * Returns the <code>CoContact </code> Entity Object . This is to implement
	 * the CMR ManyToOne relationship between FKMANAGE_CONTACT and
	 * PK${Relation.Name} in the CoContact Entity Bean.
	 */
	@ManyToOne
	@JoinColumns( {
			@JoinColumn(name = "MANAGE_CONTACT", referencedColumnName = "CONTACT_NUMBER", insertable = false, updatable = false),
			@JoinColumn(name = "MANAGE_COMPANY", referencedColumnName = "COMPANY_NUMBER", insertable = false, updatable = false) })
	public CoContact getCoContactCompositeFK2() {
		return coContactCompositeFK2;
	}

	/**
	 * Sets the <code>CoContact </code> Entity Object . This is to implement
	 * the CMR ManyToOne relationship between FKMANAGE_CONTACT and
	 * PKAGREEMENT_NUMBER in the CoContact Entity Bean.
	 */
	public void setCoContactCompositeFK2(CoContact cocontact) {
		this.coContactCompositeFK2 = cocontact;
	}

	@Transient
	public String getContactName() {
		if (coContactCompositeFK2 != null) {
			return coContactCompositeFK2.getContactName();
		}
		return null;
	}

	public int hashCode() {
		String code = "";

		code += agreementNumber;
		code += campus;
		code += billingStatus;
		code += agreementStatus;
		code += agreementType;
		code += companyNumber;
		code += contractNumber;
		code += effectiveDate;
		code += expirationDate;
		code += subAgreementFlag;
		code += directIndirectBillFlag;
		code += agreementComment;
		code += masterAgreementNumber;
		code += manageCompany;
		code += manageContact;
		code += agreementClass;
		code += changedBy;
		code += changedWhen;
		code += followAgreementNumber;
		code += responsibleOrganization;
		code += operatingNameId;
		code += createdBy;
		code += createdWhen;

		return code.hashCode();
	}

	public boolean equals(Object object) {
		Agreements ent = (Agreements) object;

		boolean eq = true;

		if (object == null) {
			eq = false;
		} else {
			// if both fields are null, they are equal.
			if ((this.agreementNumber == null) && (ent.agreementNumber == null)) {
				eq = eq && true;
			} else {
				// So not both fields are null, than if either one is null, they
				// are not equal.
				if ((this.agreementNumber == null)
						|| (ent.agreementNumber == null)) {
					eq = false;
				} else {
					// If neither one is null, check the equality.
					eq = eq && this.agreementNumber.equals(ent.agreementNumber);
				}
			}

			// if both fields are null, they are equal.
			if ((this.campus == null) && (ent.campus == null)) {
				eq = eq && true;
			} else {
				// So not both fields are null, than if either one is null, they
				// are not equal.
				if ((this.campus == null) || (ent.campus == null)) {
					eq = false;
				} else {
					// If neither one is null, check the equality.
					eq = eq && this.campus.equals(ent.campus);
				}
			}

			// if both fields are null, they are equal.
			if ((this.billingStatus == null) && (ent.billingStatus == null)) {
				eq = eq && true;
			} else {
				// So not both fields are null, than if either one is null, they
				// are not equal.
				if ((this.billingStatus == null) || (ent.billingStatus == null)) {
					eq = false;
				} else {
					// If neither one is null, check the equality.
					eq = eq && this.billingStatus.equals(ent.billingStatus);
				}
			}

			// if both fields are null, they are equal.
			if ((this.agreementStatus == null) && (ent.agreementStatus == null)) {
				eq = eq && true;
			} else {
				// So not both fields are null, than if either one is null, they
				// are not equal.
				if ((this.agreementStatus == null)
						|| (ent.agreementStatus == null)) {
					eq = false;
				} else {
					// If neither one is null, check the equality.
					eq = eq && this.agreementStatus.equals(ent.agreementStatus);
				}
			}

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
			if ((this.companyNumber == null) && (ent.companyNumber == null)) {
				eq = eq && true;
			} else {
				// So not both fields are null, than if either one is null, they
				// are not equal.
				if ((this.companyNumber == null) || (ent.companyNumber == null)) {
					eq = false;
				} else {
					// If neither one is null, check the equality.
					eq = eq && this.companyNumber.equals(ent.companyNumber);
				}
			}

			// if both fields are null, they are equal.
			if ((this.contractNumber == null) && (ent.contractNumber == null)) {
				eq = eq && true;
			} else {
				// So not both fields are null, than if either one is null, they
				// are not equal.
				if ((this.contractNumber == null)
						|| (ent.contractNumber == null)) {
					eq = false;
				} else {
					// If neither one is null, check the equality.
					eq = eq && this.contractNumber.equals(ent.contractNumber);
				}
			}

			// if both fields are null, they are equal.
			if ((this.effectiveDate == null) && (ent.effectiveDate == null)) {
				eq = eq && true;
			} else {
				// So not both fields are null, than if either one is null, they
				// are not equal.
				if ((this.effectiveDate == null) || (ent.effectiveDate == null)) {
					eq = false;
				} else {
					// If neither one is null, check the equality.
					eq = eq && this.effectiveDate.equals(ent.effectiveDate);
				}
			}

			// if both fields are null, they are equal.
			if ((this.expirationDate == null) && (ent.expirationDate == null)) {
				eq = eq && true;
			} else {
				// So not both fields are null, than if either one is null, they
				// are not equal.
				if ((this.expirationDate == null)
						|| (ent.expirationDate == null)) {
					eq = false;
				} else {
					// If neither one is null, check the equality.
					eq = eq && this.expirationDate.equals(ent.expirationDate);
				}
			}

			// if both fields are null, they are equal.
			if ((this.subAgreementFlag == null)
					&& (ent.subAgreementFlag == null)) {
				eq = eq && true;
			} else {
				// So not both fields are null, than if either one is null, they
				// are not equal.
				if ((this.subAgreementFlag == null)
						|| (ent.subAgreementFlag == null)) {
					eq = false;
				} else {
					// If neither one is null, check the equality.
					eq = eq
							&& this.subAgreementFlag
									.equals(ent.subAgreementFlag);
				}
			}

			// if both fields are null, they are equal.
			if ((this.directIndirectBillFlag == null)
					&& (ent.directIndirectBillFlag == null)) {
				eq = eq && true;
			} else {
				// So not both fields are null, than if either one is null, they
				// are not equal.
				if ((this.directIndirectBillFlag == null)
						|| (ent.directIndirectBillFlag == null)) {
					eq = false;
				} else {
					// If neither one is null, check the equality.
					eq = eq
							&& this.directIndirectBillFlag
									.equals(ent.directIndirectBillFlag);
				}
			}

			// if both fields are null, they are equal.
			if ((this.agreementComment == null)
					&& (ent.agreementComment == null)) {
				eq = eq && true;
			} else {
				// So not both fields are null, than if either one is null, they
				// are not equal.
				if ((this.agreementComment == null)
						|| (ent.agreementComment == null)) {
					eq = false;
				} else {
					// If neither one is null, check the equality.
					eq = eq
							&& this.agreementComment
									.equals(ent.agreementComment);
				}
			}

			// if both fields are null, they are equal.
			if ((this.masterAgreementNumber == null)
					&& (ent.masterAgreementNumber == null)) {
				eq = eq && true;
			} else {
				// So not both fields are null, than if either one is null, they
				// are not equal.
				if ((this.masterAgreementNumber == null)
						|| (ent.masterAgreementNumber == null)) {
					eq = false;
				} else {
					// If neither one is null, check the equality.
					eq = eq
							&& this.masterAgreementNumber
									.equals(ent.masterAgreementNumber);
				}
			}

			// if both fields are null, they are equal.
			if ((this.manageCompany == null) && (ent.manageCompany == null)) {
				eq = eq && true;
			} else {
				// So not both fields are null, than if either one is null, they
				// are not equal.
				if ((this.manageCompany == null) || (ent.manageCompany == null)) {
					eq = false;
				} else {
					// If neither one is null, check the equality.
					eq = eq && this.manageCompany.equals(ent.manageCompany);
				}
			}

			// if both fields are null, they are equal.
			if ((this.manageContact == null) && (ent.manageContact == null)) {
				eq = eq && true;
			} else {
				// So not both fields are null, than if either one is null, they
				// are not equal.
				if ((this.manageContact == null) || (ent.manageContact == null)) {
					eq = false;
				} else {
					// If neither one is null, check the equality.
					eq = eq && this.manageContact.equals(ent.manageContact);
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
			if ((this.followAgreementNumber == null)
					&& (ent.followAgreementNumber == null)) {
				eq = eq && true;
			} else {
				// So not both fields are null, than if either one is null, they
				// are not equal.
				if ((this.followAgreementNumber == null)
						|| (ent.followAgreementNumber == null)) {
					eq = false;
				} else {
					// If neither one is null, check the equality.
					eq = eq
							&& this.followAgreementNumber
									.equals(ent.followAgreementNumber);
				}
			}

			// if both fields are null, they are equal.
			if ((this.responsibleOrganization == null)
					&& (ent.responsibleOrganization == null)) {
				eq = eq && true;
			} else {
				// So not both fields are null, than if either one is null, they
				// are not equal.
				if ((this.responsibleOrganization == null)
						|| (ent.responsibleOrganization == null)) {
					eq = false;
				} else {
					// If neither one is null, check the equality.
					eq = eq
							&& this.responsibleOrganization
									.equals(ent.responsibleOrganization);
				}
			}

			// if both fields are null, they are equal.
			if ((this.operatingNameId == null) && (ent.operatingNameId == null)) {
				eq = eq && true;
			} else {
				// So not both fields are null, than if either one is null, they
				// are not equal.
				if ((this.operatingNameId == null)
						|| (ent.operatingNameId == null)) {
					eq = false;
				} else {
					// If neither one is null, check the equality.
					eq = eq && this.operatingNameId.equals(ent.operatingNameId);
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
