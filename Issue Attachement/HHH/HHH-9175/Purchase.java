package com.noproblem.sanroque.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PostLoad;
import javax.persistence.PostUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author Win 7
 */
@Entity
@Table(uniqueConstraints = {
    @UniqueConstraint(columnNames = {"transaction_id"})})
@NamedQueries({
    @NamedQuery(name = "Purchase.findAll", query = "SELECT p FROM Purchase p")})
public class Purchase implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(nullable = false)
    private Integer id;
    @Size(max = 255)
    @Column(length = 255)
    private String comments;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "purchaseId")
    private List<PurchasedItem> purchasedItemList;
    @JoinColumn(name = "supplier_id", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false)
    private Supplier supplierId;
    @JoinColumn(name = "transaction_id", referencedColumnName = "id", nullable = false)
    @OneToOne(optional = false, cascade = {CascadeType.REFRESH, CascadeType.DETACH})
    private Transaction transactionId;
    
    //@Transient
    private Double totalAmount;
    @Transient
    private Double totalAfterAccountMovement;
    @Transient
    private String uniqueValue;
    @Transient
    private LinkedList<Object> accounts;

    public Purchase() {
    	this.totalAmount = 0D;
    }

    public Purchase(Integer id) {
    	this();
        this.id = id;
    }

    public PurchasedItem addArticle() {
        PurchasedItem item = new PurchasedItem();
        item.setActive(true);
        item.setPurchaseId(this);
        if (getPurchasedItemList() == null) {
            setPurchasedItemList(new LinkedList<PurchasedItem>());
        }
        item.setOrdinal(getPurchasedItemList().size() + 1);
        getPurchasedItemList().add(item);
        return item;
    }

    public void removeItem(PurchasedItem pi) {
    	BigDecimal fq = pi.getUnitPrice().multiply(pi.getQuantity());
    	setTotalAmount(new BigDecimal(totalAmount).subtract(fq).doubleValue());
        getPurchasedItemList().remove(pi);
    }

    public void removeItem(Integer index) {
    	PurchasedItem pi = getPurchasedItemList().get(index.intValue());
    	BigDecimal fq = pi.getUnitPrice().multiply(pi.getQuantity());
    	setTotalAmount(new BigDecimal(totalAmount).subtract(fq).doubleValue());

        getPurchasedItemList().remove(index.intValue());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public List<PurchasedItem> getPurchasedItemList() {
        return purchasedItemList;
    }

    public void setPurchasedItemList(List<PurchasedItem> purchasedItemList) {
        this.purchasedItemList = purchasedItemList;
    }

    public Supplier getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Supplier supplierId) {
        this.supplierId = supplierId;
    }

    public Transaction getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Transaction transactionId) {
        this.transactionId = transactionId;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public List<Article> findArticles() {
        List<Article> articles = new LinkedList<>();
        for (PurchasedItem pi : getPurchasedItemList()) {
            articles.add(pi.getArticleId());
        }
        return articles;
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
        if (!(object instanceof Purchase)) {
            return false;
        }
        Purchase other = (Purchase) object;
        if (this.id == null && other.id == null && this.getUniqueValue() != null && this.getUniqueValue().equals(other.getUniqueValue())) {
            return true;
        }

        return this.id != null && other.id != null && this.getId().equals(other.getId());
    }

    @Override
    public String toString() {
        return "com.noproblem.sanroque.model.Purchase[ id=" + id + ", uniqueValue=" + uniqueValue + " ]";
    }

    public boolean isNewTransaction() {
        return id == null;
    }

    public String getUniqueValue() {
        return uniqueValue;
    }

    public void setUniqueValue(String uniqueValue) {
        this.uniqueValue = uniqueValue;
    }

    public Double getTotalAfterAccountMovement() {
        if (id == null) {
            return getTotalAmount();
        }
        return totalAfterAccountMovement;
    }

    public LinkedList<Object> getAccounts() {
        return accounts;
    }

    public void setAccounts(LinkedList<Object> accounts) {
        this.accounts = accounts;
    }

    public void updateTotalAfterAccountMovement() {
        BigDecimal totalMovementAccounts = BigDecimal.ZERO;

        if (getTransactionId() != null && getTransactionId().getAccountMovements() != null) {
            this.accounts = new LinkedList<>();
            for (AccountMovement m : getTransactionId().getAccountMovements()) {
                totalMovementAccounts = totalMovementAccounts.add(m.getSignedMovementValue());
                if (!accounts.contains(m.getAccount())) {
                    accounts.add(m.getAccount());
                }
            }

        }
        this.totalAfterAccountMovement = getTotalAmount() + totalMovementAccounts.doubleValue();
    }
}
