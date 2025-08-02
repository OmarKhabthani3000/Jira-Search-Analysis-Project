package com.noproblem.sanroque.model;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author asantiagod
 */
@Entity
public class Party implements java.io.Serializable {
    private List<Account> accounts;
    private Company company;
    private Supplier supplier;
    private Customer customer;
    private List<PartyContact> partyContacts;
    private Long id;
    private String code;
    private DocumentType documentType;
    private String displayName;
    private boolean active;

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    @Size(max = 50)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    
    @ManyToOne(fetch=FetchType.LAZY)
    public DocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
    }

    @NotNull
    @Size(max=120)
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @OneToMany(mappedBy = "party")
    public List<PartyContact> getPartyContacts() {
        return partyContacts;
    }

    public void setPartyContacts(List<PartyContact> partyContacts) {
        this.partyContacts = partyContacts;
    }

    @OneToOne(mappedBy = "party", fetch = FetchType.LAZY)
    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    @OneToOne(mappedBy = "party", fetch = FetchType.LAZY)
    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    @OneToOne(mappedBy = "party", fetch = FetchType.LAZY)
    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }
    
    public String getContactDetail(String key) {
        for(PartyContact cp:getPartyContacts()) {
            if(cp.getContactType().getDisplayName().equals(key)) {
                return cp.getContactValue();
            }
        }
        return null;
    }

    @OneToMany(mappedBy = "party")
    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }
    
    public Account addAccount(AccountType actype){ 
        Account ac = new Account();
        ac.setAccountType(actype);
        ac.setParty(this);
        ac.setTotalIn(BigDecimal.ZERO);
        ac.setTotalOut(BigDecimal.ZERO);
        if(this.accounts == null) {
            this.accounts = new LinkedList<>();
        }
        this.accounts.add(ac);
        return ac;
    }
    
    
}
