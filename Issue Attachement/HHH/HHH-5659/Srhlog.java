/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.serhmatica.ejb.entidades;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author rettore
 */
@Entity
@Table(name = "SRHLOG", catalog = "", schema = "SRHCREDI")
@NamedQueries({
    @NamedQuery(name = "Srhlog.findAll", query = "SELECT s FROM Srhlog s"),
    @NamedQuery(name = "Srhlog.findByIdlog", query = "SELECT s FROM Srhlog s WHERE s.idlog = :idlog"),
    @NamedQuery(name = "Srhlog.findByIdtplog", query = "SELECT s FROM Srhlog s WHERE s.idtplog = :idtplog"),
    @NamedQuery(name = "Srhlog.findByDthrlog", query = "SELECT s FROM Srhlog s WHERE s.dthrlog = :dthrlog"),
    @NamedQuery(name = "Srhlog.findByNmentidade", query = "SELECT s FROM Srhlog s WHERE s.nmentidade = :nmentidade"),
    @NamedQuery(name = "Srhlog.findByNumip", query = "SELECT s FROM Srhlog s WHERE s.numip = :numip")})
public class Srhlog implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "IDLOG", nullable = false, precision = 22)
    @SequenceGenerator(name = "SRHLOG_SEQ", sequenceName = "SRHLOG_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SRHLOG_SEQ")
    private Long idlog;
    @Basic(optional = false)
    @Column(name = "IDTPLOG", nullable = false, length = 50)
    private String idtplog;
    @Basic(optional = false)
    @Column(name = "DTHRLOG", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dthrlog;
    @Basic(optional = false)
    @Column(name = "NMENTIDADE", nullable = false, length = 150)
    private String nmentidade;
    @Column(name = "NUMIP", length = 20)
    private String numip;
    @Basic(optional = false)
    @Lob
    @Column(name = "DELOG", nullable = false)
    private String delog;
    @JoinColumn(name = "IDUSU", referencedColumnName = "IDUSU")
    @ManyToOne(fetch = FetchType.LAZY)
    private Srhusuario idusu;

    public Srhlog() {
    }

    public Srhlog(Long idlog) {
        this.idlog = idlog;
    }

    public Srhlog(Long idlog, String idtplog, Date dthrlog, String nmentidade, String delog) {
        this.idlog = idlog;
        this.idtplog = idtplog;
        this.dthrlog = dthrlog;
        this.nmentidade = nmentidade;
        this.delog = delog;
    }

    public Long getIdlog() {
        return idlog;
    }

    public void setIdlog(Long idlog) {
        this.idlog = idlog;
    }

    public String getIdtplog() {
        return idtplog;
    }

    public void setIdtplog(String idtplog) {
        this.idtplog = idtplog;
    }

    public Date getDthrlog() {
        return dthrlog;
    }

    public void setDthrlog(Date dthrlog) {
        this.dthrlog = dthrlog;
    }

    public String getNmentidade() {
        return nmentidade;
    }

    public void setNmentidade(String nmentidade) {
        this.nmentidade = nmentidade;
    }

    public String getNumip() {
        return numip;
    }

    public void setNumip(String numip) {
        this.numip = numip;
    }

    public String getDelog() {
        return delog;
    }

    public void setDelog(String delog) {
        this.delog = delog;
    }

    public Srhusuario getIdusu() {
        return idusu;
    }

    public void setIdusu(Srhusuario idusu) {
        this.idusu = idusu;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idlog != null ? idlog.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Srhlog)) {
            return false;
        }
        Srhlog other = (Srhlog) object;
        if ((this.idlog == null && other.idlog != null) || (this.idlog != null && !this.idlog.equals(other.idlog))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "br.com.serhmatica.ejb.entidades.Srhlog[idlog=" + idlog + "]";
    }
}
