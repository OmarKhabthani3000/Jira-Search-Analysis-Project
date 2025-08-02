package org.hibernate.test;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class PurchaseOrder implements Serializable {

	@Id
	private Long purchaseOrderId;
	private Long customerId;
	private Long totalAmount;

	public PurchaseOrder() {
	}
	
	public PurchaseOrder(Long purchaseOrderId, Long customerId, Long totalAmount) {
		this.purchaseOrderId = purchaseOrderId;
		this.customerId = customerId;
		this.totalAmount = totalAmount;
	}

	public Long getPurchaseOrderId() {
		return purchaseOrderId;
	}

	public void setPurchaseOrderId(Long purchaseOrderId) {
		this.purchaseOrderId = purchaseOrderId;
	}

	public Long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}

	public Long getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(Long totalAmount) {
		this.totalAmount = totalAmount;
	}

}
