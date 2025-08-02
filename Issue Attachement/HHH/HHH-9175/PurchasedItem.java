
package com.noproblem.sanroque.model;

import java.io.Serializable;
import java.math.BigDecimal;
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
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Win 7
 */
@Entity
@NamedQueries({
    @NamedQuery(name = "PurchasedItem.findAll", query = "SELECT p FROM PurchasedItem p")})
public class PurchasedItem implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(nullable = false)
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Column(nullable = false)
    private int ordinal;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @NotNull
    @Column(nullable = false, precision = 12, scale = 4)
    private BigDecimal quantity;
    @Basic(optional = false)
    @NotNull
    @Column(nullable = false, precision = 12, scale = 4)
    private BigDecimal unitPrice;
    @Basic(optional = false)
    @NotNull
    @Column(nullable = false)
    private boolean active;
    @JoinColumn(name = "purchase_id", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false)
    private Purchase purchaseId;
    @JoinColumn(name = "measureUnit_id", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false)
    private MeasureUnit measureUnitid;
    @JoinColumn(name = "article_id", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false)
    private Article articleId;
    @JoinColumn(name = "requestedItem_id", referencedColumnName = "id")
    @ManyToOne
    private RequestedItem requestedItemid;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "purchaseItemid")

    private List<ItemLocationDetail> itemLocationDetailList;
    
    @Transient
    private Double totalPrice;

    public PurchasedItem() {
    	this.quantity = BigDecimal.ZERO;
    	this.unitPrice = BigDecimal.ZERO;
    }

    public PurchasedItem(Integer id) {
    	this();
        this.id = id;
    }

    public PurchasedItem(Integer id, int ordinal, BigDecimal quantity, BigDecimal unitPrice, boolean active) {
        this.id = id;
        this.ordinal = ordinal;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.active = active;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getOrdinal() {
        return ordinal;
    }

    public void setOrdinal(int ordinal) {
        this.ordinal = ordinal;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
    	BigDecimal fq = this.quantity.multiply(unitPrice);
    	BigDecimal ta = new BigDecimal(this.purchaseId.getTotalAmount());
    	BigDecimal newTa = ta.subtract(fq);
    	
    	this.quantity = quantity;
    	BigDecimal newFq = unitPrice.multiply(this.quantity);
    	newTa = newTa.add(newFq);
    	this.purchaseId.setTotalAmount(newTa.doubleValue());
        
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
    	BigDecimal fq = this.quantity.multiply(this.unitPrice);
    	BigDecimal ta = new BigDecimal(this.purchaseId.getTotalAmount());
    	BigDecimal newTa = ta.subtract(fq);

        this.unitPrice = unitPrice;
        BigDecimal newFq = unitPrice.multiply(this.quantity);
        newTa = newTa.add(newFq);
        this.purchaseId.setTotalAmount(newTa.doubleValue());
    }

    public boolean getActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Purchase getPurchaseId() {
        return purchaseId;
    }

    public void setPurchaseId(Purchase purchaseId) {
        this.purchaseId = purchaseId;
    }

    public MeasureUnit getMeasureUnitid() {
        return measureUnitid;
    }

    public void setMeasureUnitid(MeasureUnit measureUnitid) {
        this.measureUnitid = measureUnitid;
    }

    public Article getArticleId() {
        return articleId;
    }

    public void setArticleId(Article articleId) {
        this.articleId = articleId;
    }

    public RequestedItem getRequestedItemid() {
        return requestedItemid;
    }

    public void setRequestedItemid(RequestedItem requestedItemid) {
        this.requestedItemid = requestedItemid;
    }

    public List<ItemLocationDetail> getItemLocationDetailList() {
        return itemLocationDetailList;
    }

    public void setItemLocationDetailList(List<ItemLocationDetail> itemLocationDetailList) {
        this.itemLocationDetailList = itemLocationDetailList;
    }

    public Double getTotalPrice() {
        totalPrice = getUnitPrice().doubleValue() * getQuantity().doubleValue();
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
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
        if (!(object instanceof PurchasedItem)) {
            return false;
        }
        PurchasedItem other = (PurchasedItem) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.noproblem.sanroque.model.PurchasedItem[ id=" + id + " ]";
    }

}
