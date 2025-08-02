package com.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "business")
public class Business  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "Generator")
    @GenericGenerator(name = "Generator", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "Business_PK", unique = true, nullable = false)
    private byte[] businessPk;

    @Column(name = "BusinessName", nullable = false, length = 100)
    private String businessName;

    @OneToMany(targetEntity = BusinessBank.class, fetch = FetchType.LAZY, mappedBy = "business",
            cascade = {CascadeType.ALL})
    private List<BusinessBank> businessBanks = new ArrayList<BusinessBank>(0);

    public byte[] getBusinessPk() {
        return businessPk;
    }

    public void setBusinessPk(byte[] businessPk) {
        this.businessPk = businessPk;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public List<BusinessBank> getBusinessBanks() {
        return businessBanks;
    }

    public void setBusinessBanks(List<BusinessBank> businessBanks) {
        this.businessBanks = businessBanks;
    }
}
