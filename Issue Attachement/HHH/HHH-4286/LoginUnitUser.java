/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package app.test.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

/**
 *
 * @author harrodr
 */
@Entity
@Table(name = "login_unit_user")
@SQLDelete( sql="UPDATE login_unit_user SET _is_active = 0 WHERE id = ?")
@Where(clause="_is_active <> 0")
@NamedQueries({@NamedQuery(name = "LoginUnitUser.findAll", query = "SELECT l FROM LoginUnitUser l"), @NamedQuery(name = "LoginUnitUser.findById", query = "SELECT l FROM LoginUnitUser l WHERE l.id = :id"), @NamedQuery(name = "LoginUnitUser.findByCreated", query = "SELECT l FROM LoginUnitUser l WHERE l.created = :created"), @NamedQuery(name = "LoginUnitUser.findByUpdated", query = "SELECT l FROM LoginUnitUser l WHERE l.updated = :updated")})
public class LoginUnitUser extends DomainObject implements Serializable{
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "id")
    @GeneratedValue(generator="SqLoginUnitUser")
    @SequenceGenerator(name="SqLoginUnitUser",sequenceName="sq_login_unit_user", allocationSize=1)
    private Integer id;
    @Column(name = "_created")
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;
    @Column(name = "_updated")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updated;
    @JoinColumn(name = "login_unit_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private LoginUnit loginUnitId;
    @JoinColumn(name = "login_user_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private LoginUser loginUserId;

    public LoginUnitUser() {
    }

    public LoginUnitUser(Integer id) {
        this.id = id;
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

    public LoginUnit getLoginUnitId() {
        return loginUnitId;
    }

    public void setLoginUnitId(LoginUnit loginUnitId) {
        this.loginUnitId = loginUnitId;
    }

    public LoginUser getLoginUserId() {
        return loginUserId;
    }

    public void setLoginUserId(LoginUser loginUserId) {
        this.loginUserId = loginUserId;
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
        if (!(object instanceof LoginUnitUser)) {
            return false;
        }
        LoginUnitUser other = (LoginUnitUser) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "app.test.model.LoginUnitUser[id=" + id + "]";
    }

}
