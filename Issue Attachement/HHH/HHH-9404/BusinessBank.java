package com.model;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
@Table(name = "business_bank")

public class BusinessBank   {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "Generator")
    @GenericGenerator(name = "Generator", strategy = "org.hibernate.id.UUIDGenerator")
	@Column(name = "BusinessBank_PK", unique = true, nullable = false)
	private byte[] businessBankPk;

	@ManyToOne(targetEntity = Business.class, fetch = FetchType.LAZY, cascade = CascadeType.PERSIST, optional = false)
	@JoinColumn(name = "Business_FK", nullable = false)
	private Business business;

	@Column(name = "BankAccountNumber", length = 50)
	private String bankName;

    public byte[] getBusinessBankPk() {
        return businessBankPk;
    }

    public void setBusinessBankPk(byte[] businessBankPk) {
        this.businessBankPk = businessBankPk;
    }

    public Business getBusiness() {
        return business;
    }

    public void setBusiness(Business business) {
        this.business = business;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }
}
