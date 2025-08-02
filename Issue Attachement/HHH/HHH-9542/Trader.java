/*
 * Trader.java
 * $Id$   
 * $Date$
 * $Revision$
 * 
 * Copyright (c) 2013 Doblones L.P.
 *
 * Copyrighted and proprietary code, all rights are reserved.
 *
 * @author Juan F. Arjona
 *
 */

package com.doblones.data;

import com.doblones.config.Encryption;
import com.jfarjona.encryption.EncryptionException;
import java.io.IOException;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Set;
import javax.annotation.security.PermitAll;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 *
 * @author jarjona
 */
@Entity
@PermitAll
@Table
public class Trader extends DobloniaEntity implements Serializable {
    private static final long   serialVersionUID = 1L;
    @ManyToMany(mappedBy = "authorizedTraders")
    private Set<TradingAccount> accounts;
    @Column(name = "gaccount", nullable = true)
    private String              gAccount;
    @Column(name = "gpassword", nullable = true)
    private String              gPassword;
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long                id;
    @Column(name = "level", nullable = false)
    private UserLevel           level = UserLevel.User;
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "manager")
    private Set<TradingAccount> managedAccounts;
    @Column(name = "name", nullable = false)
    private String              name;
    @Transient
    private transient String    unencryptedGPassword;
    @Column(name = "username", nullable = false)
    private String              username;

    /**
     * Constructs ...
     *
     */
    public Trader() {
        level = UserLevel.User;
    }

    /**
     * Builds a comparator for traders to compare by the natural order of their id.
     *
     * @return
     */
    public static Comparator<Trader> compareById() {
        Comparator<Trader> answ = new Comparator<Trader>() {
            @Override
            public int compare(Trader t1, Trader t2) {
                if ((t1 != null) && (t2 != null)) {
                    return t1.id.compareTo(t2.id);
                } else {
                    if (t1 != null) {
                        return 1;
                    } else {
                        if (t2 != null) {
                            return -1;
                        } else {
                            return 0;
                        }
                    }
                }
            }
        };

        return answ;
    }

    /**
     * Creates a comparator to compare traders naturally by name
     *
     *
     * @return a comparator
     */
    public static Comparator<Trader> compareByName() {
        Comparator<Trader> answ = new Comparator<Trader>() {
            @Override
            public int compare(Trader t1, Trader t2) {
                if ((t1 != null) && (t2 != null)) {
                    return t1.name.compareTo(t2.name);
                } else {
                    if (t1 != null) {
                        return 1;
                    } else {
                        if (t2 != null) {
                            return -1;
                        } else {
                            return 0;
                        }
                    }
                }
            }
        };

        return answ;
    }

    /**
     *
     * @param cryptor
     * @throws EncryptionException
     * @throws IOException
     */
    public void decrypt(Encryption cryptor) throws EncryptionException, IOException {
        this.unencryptedGPassword = (gPassword != null)
                                    ? cryptor.decryptString(this.gPassword)
                                    : null;
    }

    /**
     * Method description
     *
     *
     * @param cryptor
     *
     * @throws EncryptionException
     * @throws IOException
     */
    public void encrypt(Encryption cryptor) throws EncryptionException, IOException {
        this.gPassword = (unencryptedGPassword != null)
                         ? cryptor.encryptString(unencryptedGPassword)
                         : null;
    }

    /**
     * Method description
     *
     *
     * @param object
     *
     * @return
     */
    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Trader)) {
            return false;
        }

        Trader other = (Trader) object;

        if (((this.id == null) && (other.id != null)) || ((this.id != null) &&!this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    /**
     * @return the accounts
     */
    public Set<TradingAccount> getAccounts() {
        return accounts;
    }

    /**
     * @return the gAccount
     */
    public String getGAccount() {
        return gAccount;
    }

    /**
     * @return the gPassword
     */
    public String getGPassword() {
        return gPassword;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public Long getId() {
        return id;
    }

    /**
     * @return the level
     */
    public UserLevel getLevel() {
        return level;
    }

    /**
     * @return the managedAccounts
     */
    public Set<TradingAccount> getManagedAccounts() {
        return managedAccounts;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * @return the unencryptedGPassword
     */
    public String getUnencryptedGPassword() {
        return unencryptedGPassword;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public String getUsername() {
        return username;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    @Override
    public int hashCode() {
        int hash = 0;

        hash += ((id != null)
                 ? id.hashCode()
                 : 0);
        return hash;
    }

    /**
     *
     * @param accounts
     */
    public void setAccounts(Set<TradingAccount> accounts) {
        this.accounts = accounts;
    }

    /**
     * @param gAccount the gAccount to set
     */
    public void setGAccount(String gAccount) {
        this.gAccount = gAccount;
    }

    /**
     * @param gPassword the gPassword to set
     */
    public void setGPassword(String gPassword) {
        this.gPassword = gPassword;
    }

    /**
     * Method description
     *
     *
     * @param id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @param level the level to set
     */
    public void setLevel(UserLevel level) {
        this.level = level;
    }

    /**
     * @param managedAccounts the managedAccounts to set
     */
    public void setManagedAccounts(Set<TradingAccount> managedAccounts) {
        this.managedAccounts = managedAccounts;
    }

    /**
     * Method description
     *
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param unencryptedGPassword the unencryptedGPassword to set
     */
    public void setUnencryptedGPassword(String unencryptedGPassword) {
        this.unencryptedGPassword = unencryptedGPassword;
    }

    /**
     * Method description
     *
     *
     * @param username
     */
    public void setUsername(String username) {
        this.username = username;
    }
}
