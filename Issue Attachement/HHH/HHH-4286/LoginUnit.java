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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
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
@Table(name = "login_unit")
@SQLDelete( sql="UPDATE login_unit SET _is_active = 0 WHERE id = ?")
@Where(clause="_is_active <> 0")
@NamedQueries({@NamedQuery(name = "LoginUnit.findAll", query = "SELECT l FROM LoginUnit l"), @NamedQuery(name = "LoginUnit.findByUnitName", query = "SELECT l FROM LoginUnit l WHERE l.unitName = :unitName"), @NamedQuery(name = "LoginUnit.findById", query = "SELECT l FROM LoginUnit l WHERE l.id = :id"), @NamedQuery(name = "LoginUnit.findByCreated", query = "SELECT l FROM LoginUnit l WHERE l.created = :created"), @NamedQuery(name = "LoginUnit.findByUpdated", query = "SELECT l FROM LoginUnit l WHERE l.updated = :updated")})
public class LoginUnit extends DomainObject implements Serializable{
    private static final long serialVersionUID = 1L;
    @Column(name = "unit_name")
    private String unitName;
    @Id
    @Basic(optional = false)
    @Column(name = "id")
    @GeneratedValue(generator="SqLoginUnit")
    @SequenceGenerator(name="SqLoginUnit",sequenceName="sq_login_unit", allocationSize=1)
    private Integer id;
    @Column(name = "_created")
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;
    @Column(name = "_updated")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updated;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "loginUnitId")
    private Collection<LoginUnitUser> loginUnitUserCollection;
    @OneToMany(mappedBy = "loginUnitId")
    private Collection<Audit> auditCollection;
    //Cascade needed for hibernate to persist properly
    @JoinColumn(name = "contact_information_id", referencedColumnName = "id")
    @ManyToOne(cascade=CascadeType.ALL)
    private ContactInformation contactInformationId;

    public LoginUnit() {
    }

    public LoginUnit(Integer id) {
        this.id = id;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
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

    public ContactInformation getContactInformationId() {
        return contactInformationId;
    }

    public void setContactInformationId(ContactInformation contactInformationId) {
        this.contactInformationId = contactInformationId;
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
        if (!(object instanceof LoginUnit)) {
            return false;
        }
        LoginUnit other = (LoginUnit) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "app.test.model.LoginUnit[id=" + id + "]";
    }

}
