/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package app.test.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


/**
 *
 * @author harrodr
 */
@Entity
@Table(name = "login_user")
@NamedQueries({@NamedQuery(name = "LoginUser.findAll", query = "SELECT l FROM LoginUser l"), @NamedQuery(name = "LoginUser.findByUserName", query = "SELECT l FROM LoginUser l WHERE l.userName = :userName"), @NamedQuery(name = "LoginUser.findById", query = "SELECT l FROM LoginUser l WHERE l.id = :id"), @NamedQuery(name = "LoginUser.findByCreated", query = "SELECT l FROM LoginUser l WHERE l.created = :created"), @NamedQuery(name = "LoginUser.findByUpdated", query = "SELECT l FROM LoginUser l WHERE l.updated = :updated"), @NamedQuery(name = "LoginUser.findByFirstName", query = "SELECT l FROM LoginUser l WHERE l.firstName = :firstName"), @NamedQuery(name = "LoginUser.findByLastName", query = "SELECT l FROM LoginUser l WHERE l.lastName = :lastName")})
public class LoginUser extends DomainObject implements Serializable {
    private static final long serialVersionUID = 1L;
    @Basic(optional = false)
    @Column(name = "user_name")
    private String userName;
    @Id
    @Basic(optional = false)
    @Column(name = "id")
    @GeneratedValue(generator="SqLoginUser")
    @SequenceGenerator(name="SqLoginUser",sequenceName="sq_login_user", allocationSize=1)
    private Integer id;
    @Column(name = "_created")
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;
    @Column(name = "_updated")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updated;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "loginUserId")
    private Collection<LoginUnitUser> loginUnitUserCollection;
    @OneToMany(mappedBy = "loginUserId")
    private Collection<Audit> auditCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "loginUserId")
    private Collection<LoginUserRank> loginUserRankCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "loginUserId")
    private Collection<LoginRoleUser> loginRoleUserCollection;

    public LoginUser() {
    }

    public LoginUser(Integer id) {
        this.id = id;
    }

    public LoginUser(Integer id, String userName) {
        this.id = id;
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Collection<LoginUnitUser> getLoginUnitUserCollection() {
        return loginUnitUserCollection;
    }

    public void setLoginUnitUserCollection(Collection<LoginUnitUser> loginUnitUserCollection) {
        this.loginUnitUserCollection = loginUnitUserCollection;
    }

    public Collection<Audit> getAuditCollection() {
        return auditCollection;
    }

    public void setAuditCollection(Collection<Audit> auditCollection) {
        this.auditCollection = auditCollection;
    }

    public Collection<LoginUserRank> getLoginUserRankCollection() {
        return loginUserRankCollection;
    }

    public void setLoginUserRankCollection(Collection<LoginUserRank> loginUserRankCollection) {
        this.loginUserRankCollection = loginUserRankCollection;
    }

    public Collection<LoginRoleUser> getLoginRoleUserCollection() {
        return loginRoleUserCollection;
    }

    public void setLoginRoleUserCollection(Collection<LoginRoleUser> loginRoleUserCollection) {
        this.loginRoleUserCollection = loginRoleUserCollection;
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
        if (!(object instanceof LoginUser)) {
            return false;
        }
        LoginUser other = (LoginUser) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "app.test.model.LoginUser[id=" + id + "]";
    }
    
    public LoginUnit getLoginUnit(){
    	LoginUnit unit = null;
    	
    	if( this.getLoginUnitUserCollection() != null ){
    		for( LoginUnitUser unitUser : this.getLoginUnitUserCollection() ){
    			unit = unitUser.getLoginUnitId();
    			break;
    		}
    	}
    	return unit;
    }

}
