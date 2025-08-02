package net.codejava.spring;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.SequenceGenerator;
import javax.persistence.Version;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class DescriptionLieu implements Serializable {

    private static final long serialVersionUID = 1L;

    protected DescriptionLieu() {
        super();
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "lieu_id_sequence")
    @SequenceGenerator(name = "lieu_id_sequence", sequenceName = "lieu_id_sequence", allocationSize = 1)
    private Long id;

    @Column(nullable = false)
    private Date dateCreaEnr;

    @Version
    private Date dateModifEnr;

    @Column(nullable = false)
    private Boolean temoinEnService;

    @Column(nullable = false, length = 320)
    private String codeUserToUpdate;

    @Column(length = 320)
    private String codeUserToCreate;

    public Date getDateCreaEnr() {
        return this.dateCreaEnr;
    }

    public void setDateCreaEnr(Date dateCreaEnr) {
        this.dateCreaEnr = dateCreaEnr;
    }

    public Date getDateModifEnr() {
        return this.dateModifEnr;
    }

    public void setDateModifEnr(Date dateModifEnr) {
        this.dateModifEnr = dateModifEnr;
    }

    public Boolean getTemoinEnService() {
        return this.temoinEnService;
    }

    public void setTemoinEnService(Boolean temoinEnService) {
        this.temoinEnService = temoinEnService;
    }

    public String getCodeUserToUpdate() {
        return this.codeUserToUpdate;
    }

    public void setCodeUserToUpdate(String codUserToUpdate) {
        this.codeUserToUpdate = codUserToUpdate;
    }

    public String getCodeUserToCreate() {
        return this.codeUserToCreate;
    }

    public void setCodeUserToCreate(String codUserToCreate) {
        this.codeUserToCreate = codUserToCreate;
    }
}
