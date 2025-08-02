
package com.noproblem.sanroque.model;

import java.io.Serializable;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Win 7
 */
@Entity @Table(name = "TX")
@NamedQueries({
    @NamedQuery(name = "Transaction.findAll", query = "SELECT t FROM Transaction t")})
public class Transaction implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(nullable = false)
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date transactionDate;
    @JoinColumn(name = "username", referencedColumnName = "username", nullable = false)
    @ManyToOne(optional = false)
    private User username;
    @JoinColumn(name = "company_id", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch=FetchType.LAZY)
    private Company companyId;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "transactionId")
    private List<ItemLocation> itemLocationList;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "transactionId")
    private Purchase purchase;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "transactionId")
    private Sale sale;
    
    @Transient
    private boolean newTransaction;
    @OneToMany(mappedBy = "transaction")
    private List<AccountMovement> accountMovements;

    public Transaction() {
    }

    public Transaction(Integer id) {
        this.id = id;
    }

    public Transaction(Integer id, Date transactionDate) {
        this.id = id;
        this.transactionDate = transactionDate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    public User getUsername() {
        return username;
    }

    public void setUsername(User username) {
        this.username = username;
    }

    public Company getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Company companyId) {
        this.companyId = companyId;
    }

    public List<ItemLocation> getItemLocationList() {
        return itemLocationList;
    }

    public void setItemLocationList(List<ItemLocation> itemLocationList) {
        this.itemLocationList = itemLocationList;
    }

    public Purchase getPurchase() {
        return purchase;
    }

    public void setPurchase(Purchase purchase) {
        this.purchase = purchase;
    }

    public Sale getSale() {
        return sale;
    }

    public void setSale(Sale sale) {
        this.sale = sale;
    }

    public List<AccountMovement> getAccountMovements() {
        return accountMovements;
    }

    public void setAccountMovements(List<AccountMovement> accountMovements) {
        this.accountMovements = accountMovements;
    }
    
    public boolean isNewTransaction() {
        return this.getId() == null;
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
        if (!(object instanceof Transaction)) {
            return false;
        }
        Transaction other = (Transaction) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.noproblem.sanroque.model.Transaction[ id=" + id + " ]";
    }

}
