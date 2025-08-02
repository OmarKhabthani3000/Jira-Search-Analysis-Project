/*
 * Copyright (c) 2005 by Lombard Odier Darier Hentsch, Geneva, Switzerland.
 * This software is subject to copyright protection under the laws of
 * Switzerland and other countries.  ALL RIGHTS RESERVED.
 * 
 */

package com.lodh.position.custodian.entity;

import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.CascadeType.REFRESH;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.lodh.position.portfolio.entity.PortfolioEty;
import com.lodh.util.jpa.AbstractEntity;

/**
 * @author moulin
 */
@Entity
@Table(name = "Tbl_Custodian")
public class CustodianEty extends AbstractEntity<Long> {

    private static final long serialVersionUID = -6823482219666088663L;

    @Id
    @SequenceGenerator(name = "seqCust", sequenceName = "Sq_Id_Custodian")
    @GeneratedValue(generator = "seqCust")
    @Column(name = "Id_Custodian")
    private Long id;

    @Embedded
    private CustodianRefEty reference;

    @Column(name = "Nm_Custodian", nullable = false)
    private String name;

    @OneToOne(cascade = { PERSIST, MERGE, REFRESH })
    @JoinColumn(name = "Id_Portfolio")
    private PortfolioEty portfolio;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "custodian")
    private List<CustodianAccountEty> accounts = new ArrayList<CustodianAccountEty>();

    public CustodianEty(CustodianRefEty reference, String name, PortfolioEty portfolio) {
        this();
        assert portfolio != null;
        this.reference = reference;
        this.name = name;
        this.portfolio = portfolio;
    }

    public CustodianEty() {
        super();
    }

    @Override
    public Long getId() {
        return id;
    }

    public CustodianRefEty getReference() {
        return reference;
    }

    public void setReference(CustodianRefEty reference) {
        this.reference = reference;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PortfolioEty getPortfolio() {
        return portfolio;
    }

    public void setPortfolio(PortfolioEty portfolio) {
        this.portfolio = portfolio;
    }

    public List<CustodianAccountEty> getAccounts() {
        return accounts;
    }

    public CustodianAccountEty getPrincipalAccount() {
        for (CustodianAccountEty acc : accounts) {
            if (acc.isPrincipal()) {
                return acc;
            }
        }
        return null;
    }

    public void addAccount(CustodianAccountEty account) {
        assert account != null;
        accounts.add(account);
        account.setCustodian(this);
    }

}
