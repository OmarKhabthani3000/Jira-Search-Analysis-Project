
package com.noproblem.sanroque.model;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author Win 7
 */
@Entity
@NamedQueries({
    @NamedQuery(name = "Company.findAll", query = "SELECT c FROM Company c")})
public class Company implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(nullable = false)
    private Integer id;

    @Basic(optional = false)
    @NotNull
    @Column(nullable = false)
    private boolean active;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "companyId")
    private List<Transaction> transactionList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "companyId")
    private List<Location> locationList;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "companyId")
    private List<Customer> customerList;
    @OneToMany(mappedBy = "parentCompanyid")
    private List<Company> companyList;
    @JoinColumn(name = "parentCompany_id", referencedColumnName = "id")
    @ManyToOne
    private Company parentCompanyid;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "companyId")
    private List<Supplier> supplierList;
    
    @OneToOne(optional = false)
    private Party party;

    public Company() {
    }

    public Company(Integer id) {
        this.id = id;
    }

   public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public boolean getActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<Transaction> getTransactionList() {
        return transactionList;
    }

    public void setTransactionList(List<Transaction> transactionList) {
        this.transactionList = transactionList;
    }

    public List<Location> getLocationList() {
        return locationList;
    }

    public void setLocationList(List<Location> locationList) {
        this.locationList = locationList;
    }

    public List<Customer> getCustomerList() {
        return customerList;
    }

    public void setCustomerList(List<Customer> customerList) {
        this.customerList = customerList;
    }

    public List<Company> getCompanyList() {
        return companyList;
    }

    public void setCompanyList(List<Company> companyList) {
        this.companyList = companyList;
    }

    public Company getParentCompanyid() {
        return parentCompanyid;
    }

    public void setParentCompanyid(Company parentCompanyid) {
        this.parentCompanyid = parentCompanyid;
    }

    public List<Supplier> getSupplierList() {
        return supplierList;
    }

    public void setSupplierList(List<Supplier> supplierList) {
        this.supplierList = supplierList;
    }

    public Party getParty() {
        return party;
    }

    public void setParty(Party party) {
        this.party = party;
    }
    
    

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Company)) {
            return false;
        }
        Company other = (Company) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.noproblem.sanroque.model.Company[ id=" + id + " ]";
    }

}
