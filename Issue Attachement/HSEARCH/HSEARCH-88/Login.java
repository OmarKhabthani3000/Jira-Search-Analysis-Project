/*
 * Login.java
 * 
 * Created on Jun 4, 2007, 3:40:39 PM
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mc3.entity.party;

import com.Ostermiller.util.Base64;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.search.annotations.ContainedIn;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;

/**
 *
 * @author shadow
 */
@Entity
@Indexed(index="indexes/parties")
public class Login implements Serializable {
    private static final long serialVersionUID = 1L;


    protected String login;
    
    protected String password;
    
    protected Person owner;
    
    Date lastChanged;
    
    public void setLogin(String login) {
        this.login = login;
    }
    
    @Id
    @DocumentId
    public String getLogin() {
        return this.login;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = HashPW(password);
        this.lastChanged = new Date();
    }
    
    public boolean CheckPassword(String password) {
        return (this.password.compareTo(HashPW(password)) == 0);
    }
    
    public void setOwner(Person owner) {
        this.owner = owner;
        this.lastChanged = new Date();
    }

    @JoinColumn(nullable=false)
    @OneToOne
    @ContainedIn
    public Person getOwner() {
        return this.owner;
    }
        
    public void setLastChanged(Date lastChanged) {
        this.lastChanged = lastChanged;
    }
    
    @Temporal(value = TemporalType.TIMESTAMP)
    @Field(index=Index.TOKENIZED)
    public Date getLastChanged() {
        return this.lastChanged;
    }
  
    public Login() {
        lastChanged = new Date();
    }
    public Login(Person Owner, String login, String password) {
        lastChanged = new Date();
        this.login = login;
        this.password = HashPW(password);
        this.owner = Owner;
    }
    private String HashPW(String password) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
            return null;
        }
        return Base64.encodeToString(md.digest(password.getBytes()));
    }

    /**
     * Returns a hash code value for the object.  This implementation computes 
     * a hash code value based on the id fields in this object.
     * @return a hash code value for this object.
     */
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (this.login != null ? this.login.hashCode() : 0);
        return hash;
    }

    /**
     * Determines whether another object is equal to this Party.  The result is 
     * <code>true</code> if and only if the argument is not null and is a Party object that 
     * has the same id field values as this object.
     * @param object the reference object with which to compare
     * @return <code>true</code> if this object is the same as the argument;
     * <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Login)) {
            return false;
        }
        Login other = (Login)object;
        if (this.login != other.login && (this.login == null || !this.login.equals(other.login))) return false;
        return true;
    }

    /**
     * Returns a string representation of the object.  This implementation constructs 
     * that representation based on the id fields.
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return "org.mc3.entity.party.Login[login=" + login + "]";
    }
    
}
