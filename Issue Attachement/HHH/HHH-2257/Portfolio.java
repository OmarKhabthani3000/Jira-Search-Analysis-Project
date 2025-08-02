package com.pxpfs.excom2.finance.domainmodel;

import com.pxpfs.annotation.ExcomOIClass;
import com.pxpfs.annotation.ExcomOIField;
import com.pxpfs.annotation.ExcomWeb;
import com.pxpfs.excom2.finance.domainmodel.enumerationtype.*;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Proxy;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 * @hibernate.class table="PORTFOLIO_TBL" proxy="com.pxpfs.excom2.finance.domainmodel.Portfolio"
 * @ hibernate.cache usage="transactional"
 * @jboss-net.xml-schema urn="Excom2:Portfolio"
 */
@Entity
@Proxy(lazy = true, proxyClass = Portfolio.class )
@ExcomOIClass(show = "@getName@")
@Table(name = "PORTFOLIO_TBL")
@ExcomWeb(urn = "Excom2:Portfolio")
public class Portfolio implements java.io.Serializable {
    private Long portfolioId;
    private Long parentId;
    private String name;
    private PortfolioTypeEnu portfolioType;
    private String externalId;
    private Long clientId;
    private PortfolioStatusEnu portfolioStatus = PortfolioStatusEnu.OPENED;
    /**
     * benchmark can have assigned portfolio
     */
    private Long assignedPortfolioId;
    private PerformanceMethodEnu defPerformanceMethod;
    private BooleanEnu fixPrices = BooleanEnu.TRUE;
    private BooleanEnu handleClaimDebt = BooleanEnu.TRUE;
    private BooleanEnu automatedPropertySettlement = BooleanEnu.FALSE;
    private BooleanEnu automatedClaimSettlement = BooleanEnu.FALSE; // todo:(PB) rename to automatedFinancialSettlement
    private BooleanEnu automatedCashTransaction = BooleanEnu.FALSE;
    private EoyProfitHandlingTypeEnu eoyProfitHandlingType = EoyProfitHandlingTypeEnu.NO_RESET;
    private EoyAPPHandlingTypeEnu eoyAPPHandlingType = EoyAPPHandlingTypeEnu.NO_RESET;

    private Portfolio parent;
    private Subject client;
    private Portfolio assignedPortfolio;
    private Set childs;
    private Portfolio[] childsArray;

    private Set assignedPortfolioSet;

    private Set userDepotCriterions;
//    private Set userDepotLimits;

    private UserDepotCriteria[] arrayUserDepotCriterions;
//    private UserDepotLimit[] arrayUserDepotLimits;
    private BooleanEnu commitmentApproach = BooleanEnu.FALSE;
    private String currencyId;
    private Currency currency;
    private BooleanEnu massTrade = BooleanEnu.FALSE;

    private BigDecimal managementFee;
    private BigDecimal successFee;

    private BooleanEnu fxRiskFactor = BooleanEnu.TRUE;
    private BooleanEnu indexRiskFactor = BooleanEnu.TRUE;
    private BooleanEnu interestRateRiskFactor = BooleanEnu.TRUE;
    private BooleanEnu vegaRiskFactor = BooleanEnu.TRUE;

    private Set feeGroups;
    private FeeGroup[] feeGroupsArray;

    private User banker;
    private Date  creationDate;

    public Portfolio() {
        super();
    }

    public void setPortfolioId(Long portfolioId) {
        this.portfolioId = portfolioId;
        if (externalId == null && portfolioId != null) {
            externalId = portfolioId.toString();
        }
    }

    /**
     * @hibernate.id generator-class="native"
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getPortfolioId() {
        return portfolioId;
    }

    /**
     * @hibernate.property
     */
    @Column
    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * @hibernate.property length="256"
     */
    @ExcomOIField(widthColumn = 200)
    @Column(length = 256)
    public String getName() {
        return name;
    }

    /**
     * @hibernate.property type="com.pxpfs.excom2.finance.domainmodel.enumerationtype.PortfolioTypeEnuUserType"
     */
    @Column
    @Type(type = "PortfolioTypeEnu")
    public PortfolioTypeEnu getPortfolioType() {
        return portfolioType;
    }

    public void setPortfolioType(PortfolioTypeEnu portfolioType) {
        this.portfolioType = portfolioType;
    }

    /**
     * @hibernate.property length="100"
     */
    @ExcomOIField(widthColumn = 200)
    @Column(length = 100)
    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    /**
     * @hibernate.property insert="true" update="true"
     */
    @Column(insertable = true, updatable = true)
    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    /**
     * @hibernate.property length="1" type="com.pxpfs.excom2.finance.domainmodel.enumerationtype.PortfolioStatusEnuUserType"
     */
    @Column(length = 1)
    @Type(type = "PortfolioStatusEnu")
    public PortfolioStatusEnu getPortfolioStatus() {
        return portfolioStatus;
    }

    public void setPortfolioStatus(PortfolioStatusEnu portfolioStatus) {
        this.portfolioStatus = portfolioStatus;
    }

    /**
     * @hibernate.property insert="false" update="false"
     */
    @Column(insertable = false, updatable = false)
    public Long getAssignedPortfolioId() {
        return assignedPortfolioId;
    }

    public void setAssignedPortfolioId(Long assignedPortfolioId) {
        this.assignedPortfolioId = assignedPortfolioId;
    }

    /**
     * @hibernate.property type="com.pxpfs.excom2.finance.domainmodel.enumerationtype.PerformanceMethodEnuUserType"
     */
    @Column
    @Type(type = "PerformanceMethodEnu")
    public PerformanceMethodEnu getDefPerformanceMethod() {
        return defPerformanceMethod;
    }

    public void setDefPerformanceMethod(PerformanceMethodEnu defPerformanceMethod) {
        this.defPerformanceMethod = defPerformanceMethod;
    }

    /**
     * @hibernate.many-to-one column="parentId" class="com.pxpfs.excom2.finance.domainmodel.Portfolio"  insert="false" update="false"
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parentId", insertable = false, updatable = false)
    public Portfolio getParent() {
        return parent;
    }

    public void setParent(Portfolio parent) {
        if (parent != null) {
            this.parentId = parent.getPortfolioId();
        } else {
            if (this.parent != null) {
                this.parentId = null;
            }
        }
        this.parent = parent;
    }

    /**
     * @hibernate.many-to-one column="clientId" class="com.pxpfs.excom2.finance.domainmodel.Subject"  insert="false" update="false"
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clientId", insertable = false, updatable = false)
    public Subject getClient() {
        return client;
    }

    public void setClient(Subject client) {
        if (client != null) {
            this.clientId = client.getSubjectId();
        } else {
            if (this.client != null) {
                this.clientId = null;
            }
        }
        this.client = client;
    }

    public void setClient(Object o) {
    }

    /**
     * @hibernate.many-to-one column="assignedPortfolioId" class="com.pxpfs.excom2.finance.domainmodel.Portfolio"  insert="true" update="true"
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignedPortfolioId", insertable = true, updatable = true)
    public Portfolio getAssignedPortfolio() {
        return assignedPortfolio;
    }

    public void setAssignedPortfolio(Portfolio assignedPortfolio) {
        this.assignedPortfolio = assignedPortfolio;
    }

    /**
     * @hibernate.set inverse="true" order-by="name" where="portfolioStatus in ('O','C')"
     * @hibernate.collection-key column="parentId"
     * @hibernate.collection-one-to-many class="com.pxpfs.excom2.finance.domainmodel.Portfolio"
     */
    @OneToMany(targetEntity = Portfolio.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "parentId")
    @Where(clause = "portfolioStatus in ('O','C')")
    @OrderBy("name")
    public Set getChilds() {
        return childs;
    }

    public void setChilds(Set childs) {
        this.childs = childs;
    }

    public void setChilds(Object[] o) {
    }

    /**
     * @hibernate.set inverse="true" order-by="name" where="portfolioStatus='O'"
     * @hibernate.collection-key column="assignedPortfolioId"
     * @hibernate.collection-one-to-many class="com.pxpfs.excom2.finance.domainmodel.Portfolio"
     */
    @OneToMany(targetEntity = Portfolio.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "assignedPortfolioId")
    @Where(clause = "portfolioStatus='O'")
    @OrderBy("name")
    public Set getAssignedPortfolioSet() {
        return assignedPortfolioSet;
    }

    public void setAssignedPortfolioSet(Set assignedPortfolioSet) {
        this.assignedPortfolioSet = assignedPortfolioSet;
    }

    public void setAssignedPortfolioSet(Object[] o) {
    }

    @Transient
    public Portfolio[] getChildsArray() {
        childsArray = new Portfolio[childs.size()];
        int i = -1;
        for (Iterator iterator = childs.iterator(); iterator.hasNext();) {
            i++;
            Object o = (Object) iterator.next();
            childsArray[i] = (Portfolio) o;
        }
        return childsArray;
    }

    public void setChildsArray(Portfolio[] childsArray) {
        this.childsArray = childsArray;
        childs = new HashSet(childsArray.length);
        for (int i = 0; i < this.childsArray.length; i++) {
            childs.add(childsArray[i]);
        }
    }

    /**
     * @hibernate.set inverse="true" cascade="all-delete-orphan"
     * @hibernate.collection-key column="portfolioId"
     * @hibernate.collection-one-to-many class="com.pxpfs.excom2.finance.domainmodel.UserDepotCriteria"
     */
    @OneToMany(targetEntity = UserDepotCriteria.class,  fetch = FetchType.EAGER)
    @Cascade( {org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN} )
    @JoinColumn(name = "portfolioId")
    public Set getUserDepotCriterions() {
        return userDepotCriterions;
    }

    public void setUserDepotCriterions(Set userDepotCriterions) {
        this.userDepotCriterions = userDepotCriterions;
    }

    public void setUserDepotCriterions(Object[] o) {
    }

//    /**
//     * @hibernate.set inverse="true" cascade="all-delete-orphan"
//     * @hibernate.collection-key column="portfolioId"
//     * @hibernate.collection-one-to-many class="com.pxpfs.excom2.finance.domainmodel.UserDepotLimit"
//     */
//    public Set getUserDepotLimits() {
//        return userDepotLimits;
//    }
//
//    public void setUserDepotLimits(Set userDepotLimits) {
//        this.userDepotLimits = userDepotLimits;
//    }

    @Transient
    public UserDepotCriteria[] getArrayUserDepotCriterions() {
        arrayUserDepotCriterions = new UserDepotCriteria[userDepotCriterions.size()];
        int i = -1;
        for (Iterator iterator = userDepotCriterions.iterator(); iterator.hasNext();) {
            i++;
            Object o = (Object) iterator.next();
            arrayUserDepotCriterions[i] = (UserDepotCriteria) o;
        }
        return arrayUserDepotCriterions;
    }

    public void setArrayUserDepotCriterions(UserDepotCriteria[] newArrayUserDepotCriterions) {
        this.arrayUserDepotCriterions = newArrayUserDepotCriterions;
        userDepotCriterions = new HashSet(newArrayUserDepotCriterions.length);
        for (int i = 0; i < this.arrayUserDepotCriterions.length; i++) {
            userDepotCriterions.add(arrayUserDepotCriterions[i]);
        }
    }

//    public UserDepotLimit[] getArrayUserDepotLimits() {
//        arrayUserDepotLimits = new UserDepotLimit[userDepotLimits.size()];
//        int i = -1;
//        for (Iterator iterator = userDepotLimits.iterator(); iterator.hasNext();) {
//            i++;
//            Object o = (Object) iterator.next();
//            arrayUserDepotLimits[i] = (UserDepotLimit) o;
//        }
//        return arrayUserDepotLimits;
//    }
//
//    public void setArrayUserDepotLimits(UserDepotLimit[] newArrayUserDepotLimits) {
//        this.arrayUserDepotLimits = newArrayUserDepotLimits;
//        userDepotLimits = new HashSet(newArrayUserDepotLimits.length);
//        for (int i = 0; i < this.arrayUserDepotLimits.length; i++) {
//            userDepotLimits.add(arrayUserDepotLimits[i]);
//        }
//    }

    /**
     * @hibernate.property type="com.pxpfs.excom2.finance.domainmodel.enumerationtype.BooleanEnuUserType" not-null="true"
     */
    @Column(nullable = false)
    @Type(type = "BooleanEnu")
    public BooleanEnu getCommitmentApproach() {
        return commitmentApproach;
    }

    public void setCommitmentApproach(BooleanEnu commitmentApproach) {
        this.commitmentApproach = commitmentApproach;
    }

    public void setCurrencyId(String currencyId) {
        this.currencyId = currencyId;
    }

    /**
     * @hibernate.property length="3"
     */
    @Column(length = 3)
    public String getCurrencyId() {
        return currencyId;
    }

    public void setCurrency(Currency currency) {
        if (currency != null) {
            this.currencyId = currency.getCurrencyId();
        } else {
            if (this.currency != null) {
                this.currencyId = null;
            }
        }

        this.currency = currency;
    }

    /**
     * @hibernate.many-to-one column="currencyId" class="com.pxpfs.excom2.finance.domainmodel.Currency" insert="false" update="false"
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "currencyId", insertable = false, updatable = false)
    public Currency getCurrency() {
        return currency;
    }

    /**
     * This property detetects if periodical fix of prices should be done.
     *
     * @hibernate.property type="com.pxpfs.excom2.finance.domainmodel.enumerationtype.BooleanEnuUserType" not-null="true"
     */
    @Column(nullable = false)
    @Type(type = "BooleanEnu")
    public BooleanEnu getFixPrices() {
        return fixPrices;
    }

    public void setFixPrices(BooleanEnu fixPrices) {
        this.fixPrices = fixPrices;
    }

    /**
     * If claims shoud be generated for positions that are included
     * in this portfolio.
     *
     * @hibernate.property type="com.pxpfs.excom2.finance.domainmodel.enumerationtype.BooleanEnuUserType" not-null="true"
     */
    @Column(nullable = false)
    @Type(type = "BooleanEnu")
    public BooleanEnu getHandleClaimDebt() {
        return handleClaimDebt;
    }

    public void setHandleClaimDebt(BooleanEnu generateClaim) {
        this.handleClaimDebt = generateClaim;
    }

    /**
     * @hibernate.property type="com.pxpfs.excom2.finance.domainmodel.enumerationtype.BooleanEnuUserType" not-null="false"
     */
    @Column(nullable = false)
    @Type(type = "BooleanEnu")
    public BooleanEnu getAutomatedPropertySettlement() {
        return automatedPropertySettlement;
    }

    public void setAutomatedPropertySettlement(BooleanEnu automatedPropertySettlement) {
        this.automatedPropertySettlement = automatedPropertySettlement;
    }

    /**
     * @hibernate.property type="com.pxpfs.excom2.finance.domainmodel.enumerationtype.BooleanEnuUserType" not-null="false"
     */
    @Column(nullable = false)
    @Type(type = "BooleanEnu")
    public BooleanEnu getAutomatedClaimSettlement() {
        return automatedClaimSettlement;
    }

    public void setAutomatedClaimSettlement(BooleanEnu automatedClaimSettlement) {
        this.automatedClaimSettlement = automatedClaimSettlement;
    }

    /**
     * @hibernate.property type="com.pxpfs.excom2.finance.domainmodel.enumerationtype.EoyProfitHandlingTypeEnuUserType" not-null="true"
     */
    @Column(nullable = false)
    @Type(type = "EoyProfitHandlingTypeEnu")
    public EoyProfitHandlingTypeEnu getEoyProfitHandlingType() {
        return eoyProfitHandlingType;
    }

    public void setEoyProfitHandlingType(EoyProfitHandlingTypeEnu eoyProfitHandlingType) {
        this.eoyProfitHandlingType = eoyProfitHandlingType;
    }

    /**
     * @hibernate.property type="com.pxpfs.excom2.finance.domainmodel.enumerationtype.EoyAPPHandlingTypeEnuUserType" not-null="true"
     */
    @Column(nullable = false)
    @Type(type = "EoyAPPHandlingTypeEnu")
    public EoyAPPHandlingTypeEnu getEoyAPPHandlingType() {
        return eoyAPPHandlingType;
    }

    public void setEoyAPPHandlingType(EoyAPPHandlingTypeEnu eoyAPPHandlingType) {
        this.eoyAPPHandlingType = eoyAPPHandlingType;
    }

/*
    //not used now
    public BooleanEnu getGenerateTrade() {
        return generateTrade;
    }

    public void setGenerateTrade(BooleanEnu generateTrade) {
        this.generateTrade = generateTrade;
    }
*/

/*
    //this is not clear what it should be used for

    public Portfolio getCounterPartyPortfolio() {
        return counterPartyPortfolio;
    }

    public void setCounterPartyPortfolio(Portfolio counterPartyPortfolio) {
        this.counterPartyPortfolio = counterPartyPortfolio;
    }
*/

    /**
     * @hibernate.property type="com.pxpfs.excom2.finance.domainmodel.enumerationtype.BooleanEnuUserType" not-null="false"
     */
    @Column
    @Type(type = "BooleanEnu")
    public BooleanEnu getMassTrade() {
        return massTrade;
    }

    public void setMassTrade(BooleanEnu massTrade) {
        this.massTrade = massTrade;
    }

    public String toString() {
        return getName();
    }

    /**
     * @hibernate.property length="5"
     */
    @Column(precision = 19, scale = 5)
    public BigDecimal getManagementFee() {
        return managementFee;
    }

    public void setManagementFee(BigDecimal managementFee) {
        this.managementFee = managementFee;
    }

    /**
     * @hibernate.property length="5"
     */
    @Column(precision = 19, scale = 5)
    public BigDecimal getSuccessFee() {
        return successFee;
    }

    public void setSuccessFee(BigDecimal aValue) {
        successFee = aValue;
    }

    /**
     * @hibernate.property type="com.pxpfs.excom2.finance.domainmodel.enumerationtype.BooleanEnuUserType" not-null="false"
     */
    @Column(nullable = false)
    @Type(type = "BooleanEnu")
    public BooleanEnu getAutomatedCashTransaction() {
        return automatedCashTransaction;
    }

    public void setAutomatedCashTransaction(BooleanEnu automatedCashTransaction) {
        this.automatedCashTransaction = automatedCashTransaction;
    }

    /**
     * fx risk factor
     *
     * @hibernate.property type="com.pxpfs.excom2.finance.domainmodel.enumerationtype.BooleanEnuUserType" not-null="true"
     */
    @Column(nullable = false)
    @Type(type = "BooleanEnu")
    public BooleanEnu getFxRiskFactor() {
        return fxRiskFactor;
    }

    public void setFxRiskFactor(BooleanEnu fxRiskFactor) {
        this.fxRiskFactor = fxRiskFactor;
    }

    /**
     * index risk factor
     *
     * @hibernate.property type="com.pxpfs.excom2.finance.domainmodel.enumerationtype.BooleanEnuUserType" not-null="true"
     */
    @Column(nullable = false)
    @Type(type = "BooleanEnu")
    public BooleanEnu getIndexRiskFactor() {
        return indexRiskFactor;
    }

    public void setIndexRiskFactor(BooleanEnu indexRiskFactor) {
        this.indexRiskFactor = indexRiskFactor;
    }

    /**
     * interest rate fisk factor
     *
     * @hibernate.property type="com.pxpfs.excom2.finance.domainmodel.enumerationtype.BooleanEnuUserType" not-null="true"
     */
    @Column(nullable = false)
    @Type(type = "BooleanEnu")
    public BooleanEnu getInterestRateRiskFactor() {
        return interestRateRiskFactor;
    }

    public void setInterestRateRiskFactor(BooleanEnu interestRateRiskFactor) {
        this.interestRateRiskFactor = interestRateRiskFactor;
    }

    /**
     * vega risk factor
     *
     * @hibernate.property type="com.pxpfs.excom2.finance.domainmodel.enumerationtype.BooleanEnuUserType" not-null="true"
     */
    @Column(nullable = false)
    @Type(type = "BooleanEnu")
    public BooleanEnu getVegaRiskFactor() {
        return vegaRiskFactor;
    }

    public void setVegaRiskFactor(BooleanEnu vegaRiskFactor) {
        this.vegaRiskFactor = vegaRiskFactor;
    }

    /**
     * @hibernate.set cascade="save-update" lazy="true" table="PORTFOLIO_FEEGROUP_TBL"
     * @hibernate.collection-key column="portfolioId"
     * @hibernate.collection-many-to-many column="feeGroupId" class="com.pxpfs.excom2.finance.domainmodel.FeeGroup"
     */
    @ManyToMany(targetEntity = FeeGroup.class, fetch = FetchType.LAZY)
    @Cascade( {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @JoinTable(
            name = "PORTFOLIO_FEEGROUP_TBL",
            joinColumns = {@JoinColumn(name = "portfolioId")},
            inverseJoinColumns = {@JoinColumn(name = "feeGroupId")})
    public Set getFeeGroups() {
        return feeGroups;
    }

    public void setFeeGroups(Set feeGroups) {
        this.feeGroups = feeGroups;
    }

    public void setFeeGroups(Object[] feeGroupsArray) {
    }

    @Transient
    public FeeGroup[] getFeeGroupsArray() {
        FeeGroup[] ret = null;
        if (feeGroups != null) {
            ret = (FeeGroup[]) feeGroups.toArray();
        }
        feeGroupsArray = ret;
        return feeGroupsArray;
    }

    public void setFeeGroupsArray(FeeGroup[] feeGroupsArray) {
        this.feeGroupsArray = feeGroupsArray;
        if (feeGroupsArray != null) {
            feeGroups = new HashSet(feeGroupsArray.length);
            for (FeeGroup feeGroup : feeGroupsArray) {
                feeGroups.add(feeGroup);
            }
        } else {
            feeGroups = null;
        }
    }

    /**
     * Portfolio can be assigned to one private banker.
     *
     * @hibernate.many-to-one column="bankerId" class="com.pxpfs.excom2.finance.domainmodel.User"
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bankerId")
    public User getBanker() {
        return banker;
    }

    public void setBanker(User banker) {
        this.banker = banker;
    }

    /**
     * @hibernate.property
     */
    @Column
    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public void setCreationDate(java.util.Calendar creationDate) {
        this.creationDate = creationDate == null ? null : creationDate.getTime();
    }
}
