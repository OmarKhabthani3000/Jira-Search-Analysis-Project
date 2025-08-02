package com.pxpfs.excom2.finance.domainmodel;

import com.pxpfs.annotation.ExcomOIClass;
import com.pxpfs.annotation.ExcomOIField;
import com.pxpfs.annotation.ExcomWeb;
import com.pxpfs.excom2.finance.domainmodel.enumerationtype.*;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Proxy;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;

/**
 * @hibernate.class table="SUBJECT_TBL" proxy="com.pxpfs.excom2.finance.domainmodel.Subject"
 * * @ hibernate.cache usage="transactional"
 * @jboss-net.xml-schema urn="Excom2:Subject"
 */
@Entity
@Proxy(lazy = true, proxyClass = Subject.class)
@ExcomOIClass(show = "@getName@")
@Table(name = "SUBJECT_TBL")
@ExcomWeb(urn = "Excom2:Subject")
public class Subject implements java.io.Serializable, Client, Broker, Counterparty, Depository, Issuer {
    private Long subjectId;
    private String name = "not defined";
    private String symbol;
    private Long originalSubjectId;
    private String externalSubjectId;
    private Date validFrom;
    private Date validTo;
    private SubjectStatusEnu status = SubjectStatusEnu.ACTIVE;
    private ClientType clientType;
    private String firstname;
    private String surname;
    private String titleBeforeName;
    private String titleAfterName;
    private String identificationNumber;
    private String verificationPassword;
    private String freeText1;
    private String freeText2;
    private String freeText3;
    private BooleanEnu bankEmployee = BooleanEnu.FALSE;
    private BooleanEnu creditCheck = BooleanEnu.FALSE;
    private BooleanEnu assetCheck = BooleanEnu.FALSE;
    private RiskClass riskClass;
    private BigDecimal nominalCapital;
    private BooleanEnu depositeInsuranceFund = BooleanEnu.FALSE;
    private BooleanEnu investmentGuaranteeFund = BooleanEnu.FALSE;

    /* attributes from Counterparty */
    private Country country;
    private Country taxDomicile;
    private Language language;
    private RatingMoodyEnu ratingMoody = RatingMoodyEnu.NOT_RATED;
    private RatingStandardAndPoorEnu ratingStandardAndPoor = RatingStandardAndPoorEnu.NOT_RATED;
    private RiskvantageRating ratingInternal;
    private Portfolio assignedCounterpartyPortfolio;
    private AssetAccountConnection assetAccountConnection;
    private Subject nextCounterparty;
    private Long nextCounterpartyId;
    private String branchText;
    private TradingBookTypeEnu tradingBookType;
    private IssuerTypeEnu issuerType;

    private Set addresses;
    private Set contractTypes;
    private Set cashAccounts;
    private Set subjectIdentificationNumbers;
    private Set clientProprietaryAccounts;
    private Set depositoryProprietaryAccounts;

    private BooleanEnu isIssuer = BooleanEnu.FALSE;
    private BooleanEnu isBroker = BooleanEnu.FALSE;
    private BooleanEnu isCounterparty = BooleanEnu.FALSE;
    private BooleanEnu isDepository = BooleanEnu.FALSE;
    private BooleanEnu isClient = BooleanEnu.FALSE;
    private ClaimGenerateStrategyAIEnu claimGenerateStrategyAI = ClaimGenerateStrategyAIEnu.NET_AI;
    private FitchLongTermCreditRatingEnu fitchLongTermCreditRating;
    private FitchShortTermCreditRatingEnu fitchShortTermCreditRating;
    private Subject originalSubject;
    private SubjectLegalType subjectLegalType;
    private Okec okec;
    private Sna sna;
    private CustomReference customReference1;
    private CustomReference customReference2;
    private CustomReference customReference3;
    private SexEnu sex;

    private GroupMemberType groupMemberType;
    private Long groupMemberTypeId;
    private Subject parentGroupMember;
    private Long parentGroupMemberId;
    private BigDecimal percentageOwnedByParent;


    public Subject() {
    }

    /**
     * @hibernate.id generator-class="native"
     */
    @ExcomOIField(readOnly = true, orderPriority = 0)
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    /**
     * @hibernate.property length="100" not-null="true"
     */
    @ExcomOIField(readOnly = true, orderPriority = 1)
    @Column(length = 100, nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null) {
            throw new NullPointerException("name can not be null!");
        }
        this.name = name;
    }

    /**
     * @hibernate.property length="100"
     */
    @Column(length = 100)
    @ExcomOIField(readOnly = true, orderPriority = 2)
    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    /**
     * @hibernate.property
     */
    @Column
    public Long getOriginalSubjectId() {
        return originalSubjectId;
    }

    public void setOriginalSubjectId(Long originalSubjectId) {
        this.originalSubjectId = originalSubjectId;
    }

    /**
     * @hibernate.many-to-one column="originalSubjectId" class="com.pxpfs.excom2.finance.domainmodel.Subject" insert="false" update="false"
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "originalSubjectId", insertable = false, updatable = false)
    public Subject getOriginalSubject() {
        return originalSubject;
    }

    public void setOriginalSubject(Subject originalSubject) {
        if (originalSubject != null) {
            this.originalSubjectId = originalSubject.getSubjectId();
        } else {
            if (this.originalSubject != null) {
                this.originalSubjectId = null;
            }
        }
        this.originalSubject = originalSubject;
    }

    /**
     * @hibernate.property length="32"
     */
    @ExcomOIField(visible = false, readOnly = true, orderPriority = 5)
    @Column(length = 32)
    public String getExternalSubjectId() {
        return externalSubjectId;
    }

    public void setExternalSubjectId(String externalSubjectId) {
        this.externalSubjectId = externalSubjectId;
    }

    /**
     * @hibernate.property type="com.pxpfs.excom2.finance.domainmodel.usertype.NormalizedDate"
     */
    @Column
    @Type(type = "NormalizedDate")
    public Date getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(Date validFrom) {
        this.validFrom = validFrom;
    }

    public void setValidFrom(java.util.Calendar validFrom) {
        this.validFrom = validFrom == null ? null : validFrom.getTime();
    }

    /**
     * @hibernate.property type="com.pxpfs.excom2.finance.domainmodel.usertype.NormalizedDate"
     */
    @Column
    @Type(type = "NormalizedDate")
    public Date getValidTo() {
        return validTo;
    }

    public void setValidTo(Date validTo) {
        this.validTo = validTo;
    }

    public void setValidTo(Calendar validTo) {
        this.validTo = validTo == null ? null : validTo.getTime();
    }

    /**
     * @hibernate.property length="1" not-null="true"
     * type="com.pxpfs.excom2.finance.domainmodel.enumerationtype.SubjectStatusEnuUserType"
     */
    @ExcomOIField(readOnly = true, orderPriority = 3)
    @Column(length = 1, nullable = false)
    @Type(type = "SubjectStatusEnu")
    public SubjectStatusEnu getStatus() {
        return status;
    }

    public void setStatus(SubjectStatusEnu status) {
        if (status == null) {
            throw new NullPointerException("satus can not be null!");
        }
        this.status = status;
    }

    /**
     * @hibernate.many-to-one column="clientTypeId" class="com.pxpfs.excom2.finance.domainmodel.ClientType"
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clientTypeId")
    public ClientType getClientType() {
        return clientType;
    }

    public void setClientType(ClientType clientType) {
        this.clientType = clientType;
    }

    /**
     * @hibernate.property length="60"
     */
    @Column(length = 60)
    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
/*
        if (this.firstname == null)
            this.name = this.surname;
        else if (this.surname == null)
            this.name = this.firstname;
        else
            this.name = this.surname + " " + this.firstname;
*/
    }

    /**
     * @hibernate.property length="60"
     */
    @ExcomOIField(visible = true, readOnly = true, orderPriority = 6)
    @Column(length = 60)
    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
/*
        if (this.firstname == null)
            this.name = this.surname;
        else if (this.surname == null)
            this.name = this.firstname;
        else
            this.name = this.surname + " " + this.firstname;
*/
    }

    /**
     * @hibernate.property length="20"
     */
    @Column(length = 20)
    public String getTitleBeforeName() {
        return titleBeforeName;
    }

    public void setTitleBeforeName(String titleBeforeName) {
        this.titleBeforeName = titleBeforeName;
    }

    /**
     * @hibernate.property length="20"
     */
    @Column(length = 60)
    public String getTitleAfterName() {
        return titleAfterName;
    }

    public void setTitleAfterName(String titleAfterName) {
        this.titleAfterName = titleAfterName;
    }

    /**
     * @hibernate.property length="30"
     */
    @ExcomOIField(visible = true, readOnly = true, orderPriority = 7)
    @Column(length = 30)
    public String getIdentificationNumber() {
        return identificationNumber;
    }

    public void setIdentificationNumber(String identificationNumber) {
        this.identificationNumber = identificationNumber;
    }

    /**
     * @hibernate.property length="60"
     */
    @Column(length = 60)
    public String getVerificationPassword() {
        return verificationPassword;
    }

    public void setVerificationPassword(String verificationPassword) {
        this.verificationPassword = verificationPassword;
    }

    /**
     * @hibernate.property length="255"
     */
    @Column(length = 255)
    public String getFreeText1() {
        return freeText1;
    }

    public void setFreeText1(String freeText1) {
        this.freeText1 = freeText1;
    }

    /**
     * @hibernate.property length="255"
     */
    @Column(length = 255)
    public String getFreeText2() {
        return freeText2;
    }

    public void setFreeText2(String freeText2) {
        this.freeText2 = freeText2;
    }

    /**
     * @hibernate.property length="255"
     */
    @Column(length = 255)
    public String getFreeText3() {
        return freeText3;
    }

    public void setFreeText3(String freeText3) {
        this.freeText3 = freeText3;
    }

    /**
     * @hibernate.property type="com.pxpfs.excom2.finance.domainmodel.enumerationtype.BooleanEnuUserType" not-null="true"
     */
    @Column(nullable = false)
    @Type(type = "BooleanEnu")
    public BooleanEnu getBankEmployee() {
        return bankEmployee;
    }

    public void setBankEmployee(BooleanEnu isBankEmployee) {
        if (isBankEmployee == null) {
            throw new NullPointerException("BooleanEnu can not be null!");
        }
        this.bankEmployee = isBankEmployee;
    }

    /**
     * @hibernate.property type="com.pxpfs.excom2.finance.domainmodel.enumerationtype.BooleanEnuUserType"  not-null="true"
     */
    @Column(nullable = false)
    @Type(type = "BooleanEnu")
    public BooleanEnu getCreditCheck() {
        return creditCheck;
    }

    public void setCreditCheck(BooleanEnu isCreditCheck) {
        if (isCreditCheck == null) {
            throw new NullPointerException("BooleanEnu can not be null!");
        }
        this.creditCheck = isCreditCheck;
    }

    /**
     * @hibernate.property type="com.pxpfs.excom2.finance.domainmodel.enumerationtype.BooleanEnuUserType"  not-null="true"
     */
    @Column(nullable = false)
    @Type(type = "BooleanEnu")
    public BooleanEnu getAssetCheck() {
        return assetCheck;
    }

    public void setAssetCheck(BooleanEnu isAssetCheck) {
        if (isAssetCheck == null) {
            throw new NullPointerException("BooleanEnu can not be null!");
        }
        this.assetCheck = isAssetCheck;
    }

    /**
     * @hibernate.many-to-one column="riskClassId" class="com.pxpfs.excom2.finance.domainmodel.RiskClass"
     * @deprecated
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "riskClassId")
    public RiskClass getRiskClass() {
        return riskClass;
    }

    /**
     * @deprecated
     */
    public void setRiskClass(RiskClass riskClass) {
        this.riskClass = riskClass;
    }

    /**
     * @hibernate.property length="4"
     */
    @Column(precision = 19, scale = 4)
    public BigDecimal getNominalCapital() {
        return nominalCapital;
    }

    public void setNominalCapital(BigDecimal nominalCapital) {
        this.nominalCapital = nominalCapital;
    }

    /**
     * @hibernate.property type="com.pxpfs.excom2.finance.domainmodel.enumerationtype.BooleanEnuUserType" not-null="true"
     */
    @Column(nullable = false)
    @Type(type = "BooleanEnu")
    public BooleanEnu getDepositeInsuranceFund() {
        return depositeInsuranceFund;
    }

    public void setDepositeInsuranceFund(BooleanEnu isDepositeInsuranceFund) {
        if (isDepositeInsuranceFund == null) {
            throw new NullPointerException("BooleanEnu can not be null!");
        }
        this.depositeInsuranceFund = isDepositeInsuranceFund;
    }

    /**
     * @hibernate.property type="com.pxpfs.excom2.finance.domainmodel.enumerationtype.BooleanEnuUserType" not-null="true"
     */
    @Column(nullable = false)
    @Type(type = "BooleanEnu")
    public BooleanEnu getInvestmentGuaranteeFund() {
        return investmentGuaranteeFund;
    }

    public void setInvestmentGuaranteeFund(BooleanEnu isInvestmentGuaranteeFund) {
        if (isInvestmentGuaranteeFund == null) {
            throw new NullPointerException("BooleanEnu can not be null!");
        }
        this.investmentGuaranteeFund = isInvestmentGuaranteeFund;
    }

    /**
     * @hibernate.many-to-one column="countryId" class="com.pxpfs.excom2.finance.domainmodel.Country"
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "countryId")
    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    /**
     * @hibernate.many-to-one column="taxDomicileId" class="com.pxpfs.excom2.finance.domainmodel.Country"
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "taxDomicileId")
    public Country getTaxDomicile() {
        return taxDomicile;
    }

    public void setTaxDomicile(Country taxDomicile) {
        this.taxDomicile = taxDomicile;
    }

    /**
     * @hibernate.many-to-one column="languageId" class="com.pxpfs.excom2.finance.domainmodel.Language"
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "languageId")
    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    /**
     * @hibernate.property length="4" type="com.pxpfs.excom2.finance.domainmodel.enumerationtype.RatingMoodyEnuUserType"
     */
    @Column(length = 4)
    @Type(type = "RatingMoodyEnu")
    public RatingMoodyEnu getRatingMoody() {
        return ratingMoody;
    }

    public void setRatingMoody(RatingMoodyEnu ratingMoody) {
        this.ratingMoody = ratingMoody;
    }

    /**
     * @hibernate.property length="4" type="com.pxpfs.excom2.finance.domainmodel.enumerationtype.RatingStandardAndPoorEnuUserType"
     */
    @Column(length = 4)
    @Type(type = "RatingStandardAndPoorEnu")
    public RatingStandardAndPoorEnu getRatingStandardAndPoor() {
        return ratingStandardAndPoor;
    }

    public void setRatingStandardAndPoor(RatingStandardAndPoorEnu ratingStandardAndPoor) {
        this.ratingStandardAndPoor = ratingStandardAndPoor;
    }

    /**
     * @hibernate.many-to-one column="internalRatingId" class="com.pxpfs.excom2.finance.domainmodel.RiskvantageRating"
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "internalRatingId")
    public RiskvantageRating getRatingInternal() {
        return ratingInternal;
    }

    public void setRatingInternal(RiskvantageRating ratingInternal) {
        this.ratingInternal = ratingInternal;
    }

    /**
     * When checkbox is checked on trade entry, there
     * will be generated internal trade on this (assignedCounterpartyPortfolio) portfolio
     *
     * @hibernate.many-to-one column="portfolioId" class="com.pxpfs.excom2.finance.domainmodel.Portfolio"
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolioId")
    public Portfolio getAssignedCounterpartyPortfolio() {
        return assignedCounterpartyPortfolio;
    }

    public void setAssignedCounterpartyPortfolio(Portfolio assignedCounterpartyPortfolio) {
        this.assignedCounterpartyPortfolio = assignedCounterpartyPortfolio;
    }

    /**
     * @hibernate.many-to-one column="assetAccountConnectionId" class="com.pxpfs.excom2.finance.domainmodel.AssetAccountConnection"
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assetAccountConnectionId")
    public AssetAccountConnection getAssetAccountConnection() {
        return assetAccountConnection;
    }

    public void setAssetAccountConnection(AssetAccountConnection assetAccountConnection) {
        this.assetAccountConnection = assetAccountConnection;
    }

    /**
     * @hibernate.property
     */
    @Column
    public Long getNextCounterpartyId() {
        return nextCounterpartyId;
    }

    public void setNextCounterpartyId(Long nextCounterpartyId) {
        this.nextCounterpartyId = nextCounterpartyId;
    }

    /**
     * it is not used?
     * probably when internal trade is created on assignedCounterpartyPortfolio
     * trade should contain counterparte = nextCounterparty
     *
     * @hibernate.many-to-one column="nextCounterpartyId" class="com.pxpfs.excom2.finance.domainmodel.Subject" insert="false" update="false"
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nextCounterpartyId", insertable = false, updatable = false)
    public Subject getNextCounterparty() {
        return nextCounterparty;
    }

    public void setNextCounterparty(Subject nextCounterparty) {
        if (nextCounterparty != null) {
            this.nextCounterpartyId = nextCounterparty.getSubjectId();
        } else {
            if (this.nextCounterparty != null) {
                this.nextCounterpartyId = null;
            }
        }
        this.nextCounterparty = nextCounterparty;
    }

    /**
     * @hibernate.property length="255"
     */
    @Column(length = 255)
    public String getBranchText() {
        return branchText;
    }

    public void setBranchText(String branchText) {
        this.branchText = branchText;
    }

    /**
     * @hibernate.property length="1" type="com.pxpfs.excom2.finance.domainmodel.enumerationtype.TradingBookTypeEnuUserType"
     */
    @Column(length = 1)
    @Type(type = "TradingBookTypeEnu")
    public TradingBookTypeEnu getTradingBookType() {
        return tradingBookType;
    }

    public void setTradingBookType(TradingBookTypeEnu tradingBookType) {
        this.tradingBookType = tradingBookType;
    }

    /**
     * @hibernate.property length="1" type="com.pxpfs.excom2.finance.domainmodel.enumerationtype.IssuerTypeEnuUserType"
     */
    @Column(length = 1)
    @Type(type = "IssuerTypeEnu")
    public IssuerTypeEnu getIssuerType() {
        return issuerType;
    }

    public void setIssuerType(IssuerTypeEnu issuerType) {
        this.issuerType = issuerType;
    }

    /**
     * @hibernate.set inverse="true" cascade="all-delete-orphan" lazy="true"
     * @hibernate.collection-key column="subjectId"
     * @hibernate.collection-one-to-many class="com.pxpfs.excom2.finance.domainmodel.Address"
     */
    @OneToMany(targetEntity = Address.class, fetch = FetchType.LAZY)
    @Cascade({org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @JoinColumn(name = "subjectId")
    public Set getAddresses() {
        return addresses;
    }

    public void setAddresses(Set addresses) {
        this.addresses = addresses;
    }

    public void setAddresses(Object[] o) {
    }

    /**
     * @hibernate.set inverse="true" cascade="all-delete-orphan" lazy="true"
     * @hibernate.collection-key column="subjectId"
     * @hibernate.collection-one-to-many class="com.pxpfs.excom2.finance.domainmodel.ContractTypeSet"
     */
    @OneToMany(targetEntity = ContractTypeSet.class, fetch = FetchType.LAZY)
    @Cascade({org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @JoinColumn(name = "subjectId")
    public Set getContractTypes() {
        return contractTypes;
    }

    public void setContractTypes(Set contractTypes) {
        this.contractTypes = contractTypes;
    }

    public void setContractTypes(Object[] o) {
    }

    /**
     * @hibernate.set inverse="true" cascade="all-delete-orphan" lazy="true"
     * @hibernate.collection-key column="subjectId"
     * @hibernate.collection-one-to-many class="com.pxpfs.excom2.finance.domainmodel.CashAccount"
     */
    @OneToMany(targetEntity = CashAccount.class, fetch = FetchType.LAZY)
    @Cascade({org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @JoinColumn(name = "subjectId")
    public Set getCashAccounts() {
        return cashAccounts;
    }

    public void setCashAccounts(Set cashAccounts) {
        this.cashAccounts = cashAccounts;
    }

    public void setCashAccounts(Object[] o) {
    }

    /**
     * @hibernate.property type="com.pxpfs.excom2.finance.domainmodel.enumerationtype.BooleanEnuUserType" not-null="true"
     */
    @Column(nullable = false)
    @Type(type = "BooleanEnu")
    public BooleanEnu getIsIssuer() {
        return isIssuer;
    }

    public void setIsIssuer(BooleanEnu isIssuer) {
        if (isIssuer == null) {
            throw new NullPointerException("BooleanEnu can not be null!");
        }
        this.isIssuer = isIssuer;
    }

    /**
     * @hibernate.property type="com.pxpfs.excom2.finance.domainmodel.enumerationtype.BooleanEnuUserType" not-null="true"
     */
    @Column(nullable = false)
    @Type(type = "BooleanEnu")
    public BooleanEnu getIsBroker() {
        return isBroker;
    }

    public void setIsBroker(BooleanEnu isBroker) {
        if (isBroker == null) {
            throw new NullPointerException("BooleanEnu can not be null!");
        }
        this.isBroker = isBroker;
    }

    /**
     * @hibernate.property type="com.pxpfs.excom2.finance.domainmodel.enumerationtype.BooleanEnuUserType" not-null="true"
     */
    @Column(nullable = false)
    @Type(type = "BooleanEnu")
    public BooleanEnu getIsCounterparty() {
        return isCounterparty;
    }

    public void setIsCounterparty(BooleanEnu isCounterparty) {
        if (isCounterparty == null) {
            throw new NullPointerException("BooleanEnu can not be null!");
        }
        this.isCounterparty = isCounterparty;
    }

    /**
     * @hibernate.property type="com.pxpfs.excom2.finance.domainmodel.enumerationtype.BooleanEnuUserType" not-null="true"
     */
    @Column(nullable = false)
    @Type(type = "BooleanEnu")
    public BooleanEnu getIsDepository() {
        return isDepository;
    }

    public void setIsDepository(BooleanEnu isDepository) {
        if (isDepository == null) {
            throw new NullPointerException("BooleanEnu can not be null!");
        }
        this.isDepository = isDepository;
    }

    /**
     * @hibernate.property type="com.pxpfs.excom2.finance.domainmodel.enumerationtype.BooleanEnuUserType" not-null="true"
     */
    @Column(nullable = false)
    @Type(type = "BooleanEnu")
    public BooleanEnu getIsClient() {
        return isClient;
    }

    public void setIsClient(BooleanEnu isClient) {
        if (isClient == null) {
            throw new NullPointerException("BooleanEnu can not be null!");
        }
        this.isClient = isClient;
    }

    @Transient
    public Issuer getIssuer() {
        if (isIssuer.booleanValue()) {
            return this;
        } else {
            return null;
        }
    }

    @Transient
    public Broker getBroker() {
        if (isBroker.booleanValue()) {
            return this;
        } else {
            return null;
        }
    }

    @Transient
    public Counterparty getCounterparty() {
        if (isCounterparty.booleanValue()) {
            return this;
        } else {
            return null;
        }
    }

    @Transient
    public Depository getDepository() {
        if (isDepository.booleanValue()) {
            return this;
        } else {
            return null;
        }
    }

    @Transient
    public Client getClient() {
        if (isClient.booleanValue()) {
            return this;
        } else {
            return null;
        }
    }

    /**
     * Field used at generating of claims on AI of termDeposits and variable Deposits
     *
     * @hibernate.property length="1" type="com.pxpfs.excom2.finance.domainmodel.enumerationtype.ClaimGenerateStrategyAIEnuUserType"
     */
    @Column(length = 1)
    @Type(type = "ClaimGenerateStrategyAIEnu")
    public ClaimGenerateStrategyAIEnu getClaimGenerateStrategyAI() {
        return claimGenerateStrategyAI;
    }

    public void setClaimGenerateStrategyAI(ClaimGenerateStrategyAIEnu claimGenerateStrategyAI) {
        this.claimGenerateStrategyAI = claimGenerateStrategyAI;
    }

    /**
     * Long term Fitch credit ratings.
     * For more info see comments in enumerators.
     * (relevant for issuer)
     *
     * @hibernate.property type="com.pxpfs.excom2.finance.domainmodel.enumerationtype.FitchLongTermCreditRatingEnuUserType"
     */
    @Column
    @Type(type = "FitchLongTermCreditRatingEnu")
    public FitchLongTermCreditRatingEnu getFitchLongTermCreditRating() {
        return fitchLongTermCreditRating;
    }

    public void setFitchLongTermCreditRating(FitchLongTermCreditRatingEnu fitchLongTermCreditRating) {
        this.fitchLongTermCreditRating = fitchLongTermCreditRating;
    }

    /**
     * Short term Fitch credit ratings.
     * For more info see comments in enumerators.
     * (relevant for issuer)
     *
     * @hibernate.property type="com.pxpfs.excom2.finance.domainmodel.enumerationtype.FitchShortTermCreditRatingEnuUserType"
     */
    @Column
    @Type(type = "FitchShortTermCreditRatingEnu")
    public FitchShortTermCreditRatingEnu getFitchShortTermCreditRating() {
        return fitchShortTermCreditRating;
    }

    public void setFitchShortTermCreditRating(FitchShortTermCreditRatingEnu fitchShortTermCreditRating) {
        this.fitchShortTermCreditRating = fitchShortTermCreditRating;
    }

    /**
     * for PrivatBank on Gui privatBanker
     * @hibernate.many-to-one column="customReference1Id" class="com.pxpfs.excom2.finance.domainmodel.CustomReference"
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customReference1Id")
    public CustomReference getCustomReference1() {
        return customReference1;
    }

    public void setCustomReference1(CustomReference customReference1) {
        this.customReference1 = customReference1;
    }

    /**
     * for PrivatBank on Gui tipper
     * @hibernate.many-to-one column="customReference2Id" class="com.pxpfs.excom2.finance.domainmodel.CustomReference"
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customReference2Id")
    public CustomReference getCustomReference2() {
        return customReference2;
    }

    public void setCustomReference2(CustomReference customReference2) {
        this.customReference2 = customReference2;
    }

    /**
     * for PrivatBank on Gui branch
     * @hibernate.many-to-one column="customReference3Id" class="com.pxpfs.excom2.finance.domainmodel.CustomReference"
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customReference3Id")
    public CustomReference getCustomReference3() {
        return customReference3;
    }

    public void setCustomReference3(CustomReference customReference3) {
        this.customReference3 = customReference3;
    }

    /**
     * @hibernate.property type="com.pxpfs.excom2.finance.domainmodel.enumerationtype.SexEnuUserType"
     */
    @Column
    @Type(type = "SexEnu")
    public SexEnu getSex() {
        return sex;
    }

    public void setSex(SexEnu sex) {
        this.sex = sex;
    }


    /**
     * @hibernate.many-to-one column="subjectLegalTypeId" class="com.pxpfs.excom2.finance.domainmodel.SubjectLegalType"
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subjectLegalTypeId")
    public SubjectLegalType getSubjectLegalType() {
        return subjectLegalType;
    }

    public void setSubjectLegalType(SubjectLegalType subjectLegalType) {
        this.subjectLegalType = subjectLegalType;
    }

    /**
     * @hibernate.many-to-one column="okecId" class="com.pxpfs.excom2.finance.domainmodel.Okec"
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "okecId")
    public Okec getOkec() {
        return okec;
    }

    public void setOkec(Okec okec) {
        this.okec = okec;
    }

    /**
     * @hibernate.many-to-one column="snaId" class="com.pxpfs.excom2.finance.domainmodel.Sna"
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "snaId")
    public Sna getSna() {
        return sna;
    }

    public void setSna(Sna sna) {
        this.sna = sna;
    }

    /**
     * @hibernate.many-to-one column="groupMemberTypeId" class="com.pxpfs.excom2.finance.domainmodel.GroupMemberType" insert="false" update="false"
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "groupMemberTypeId", insertable = false, updatable = false)
    public GroupMemberType getGroupMemberType() {
        return groupMemberType;
    }

    public void setGroupMemberType(GroupMemberType groupMemberType) {
        if (groupMemberType != null) {
            this.groupMemberTypeId = groupMemberType.getGroupMemberTypeId();
        } else {
            if (this.groupMemberType != null) {
                this.groupMemberTypeId = null;
            }
        }
        this.groupMemberType = groupMemberType;
    }

    /**
     * @hibernate.property
     */
    @Column
    public Long getGroupMemberTypeId() {
        return groupMemberTypeId;
    }

    public void setGroupMemberTypeId(Long groupMemberTypeId) {
        this.groupMemberTypeId = groupMemberTypeId;
    }

    /**
     * @hibernate.many-to-one column="parentGroupMemberId" class="com.pxpfs.excom2.finance.domainmodel.Subject" insert="false" update="false"
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parentGroupMemberId", insertable = false, updatable = false)
    public Subject getParentGroupMember() {
        return parentGroupMember;
    }

    public void setParentGroupMember(Subject parentGroupMember) {
        if (parentGroupMember != null) {
            this.parentGroupMemberId = parentGroupMember.getSubjectId();
        } else {
            if (this.parentGroupMember != null) {
                this.parentGroupMemberId = null;
            }
        }
        this.parentGroupMember = parentGroupMember;
    }

    /**
     * @hibernate.property
     */
    @Column
    public Long getParentGroupMemberId() {
        return parentGroupMemberId;
    }

    public void setParentGroupMemberId(Long parentGroupMemberId) {
        this.parentGroupMemberId = parentGroupMemberId;
    }

    /**
     * @hibernate.property length="8"
     */
    @Column(precision = 19, scale = 8)
    public BigDecimal getPercentageOwnedByParent() {
        return percentageOwnedByParent;
    }

    public void setPercentageOwnedByParent(BigDecimal percentageOwnedByParent) {
        this.percentageOwnedByParent = percentageOwnedByParent;
    }

    /**
     * @hibernate.set inverse="true" cascade="all-delete-orphan" lazy="true"
     * @hibernate.collection-key column="subjectId"
     * @hibernate.collection-one-to-many class="com.pxpfs.excom2.finance.domainmodel.SubjectIdentificationNumber"
     */
    @OneToMany(targetEntity = SubjectIdentificationNumber.class, fetch = FetchType.LAZY)
    @Cascade({org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @JoinColumn(name = "subjectId")
    public Set getSubjectIdentificationNumbers() {
        return subjectIdentificationNumbers;
    }

    public void setSubjectIdentificationNumbers(Set personalIdentificationNumber) {
        this.subjectIdentificationNumbers = personalIdentificationNumber;
    }

    public void setPersonalIdentificationNumbers(Object[] o) {
    }

    /**
     * @hibernate.set inverse="true" cascade="all-delete-orphan" lazy="true"
     * @hibernate.collection-key column="clientId"
     * @hibernate.collection-one-to-many class="com.pxpfs.excom2.finance.domainmodel.ProprietaryAccount"
     */
    @OneToMany(targetEntity = ProprietaryAccount.class, fetch = FetchType.LAZY)
    @Cascade({org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @JoinColumn(name = "clientId")
    public Set getClientProprietaryAccounts() {
        return clientProprietaryAccounts;
    }

    public void setClientProprietaryAccounts(Set proprietaryAccounts) {
        clientProprietaryAccounts = proprietaryAccounts;
    }

    public void setClientProprietaryAccounts(Object[] o) {
    }

    /**
     * @hibernate.set inverse="true" cascade="all-delete-orphan" lazy="true"
     * @hibernate.collection-key column="depositoryId"
     * @hibernate.collection-one-to-many class="com.pxpfs.excom2.finance.domainmodel.ProprietaryAccount"
     */
    @OneToMany(targetEntity = ProprietaryAccount.class, fetch = FetchType.LAZY)
    @Cascade({org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @JoinColumn(name = "depositoryId")
    public Set getDepositoryProprietaryAccounts() {
        return depositoryProprietaryAccounts;
    }

    public void setDepositoryProprietaryAccounts(Set proprietaryAccounts) {
        depositoryProprietaryAccounts = proprietaryAccounts;
    }

    public void setDepositoryProprietaryAccounts(Object[] o) {
    }

}