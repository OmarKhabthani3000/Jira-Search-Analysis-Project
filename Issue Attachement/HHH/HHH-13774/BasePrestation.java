/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.csys.parametrage.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;

/**
 *
 * @author s
 */
@Entity
@Table(name = "Prestation")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "visible")
@Audited
@AuditTable("Prestation_AUD")
public class BasePrestation implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "Code", nullable = false)
    private Integer code;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "Code_Saisie", nullable = false, length = 10)
    private String codeSaisie;
    @Basic(optional = false)
    @NotNull
    @Column(name = "Autoris_Modifier_Prix", nullable = false)
    private boolean autorisModifierPrix;
    @Basic(optional = false)
    @NotNull
    @Column(name = "Code_Beneficiere", nullable = false)
    private int codeBeneficiere;
    @Basic(optional = false)
    @NotNull
    @Column(name = "Compte_Rendu", nullable = false)
    private boolean compteRendu;
    @Basic(optional = false)
    @NotNull
    @Column(name = "Demande_Obligatoire", nullable = false)
    private boolean demandeObligatoire;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 200)
    @Column(name = "Designation_Ar", nullable = false, length = 200)
    private String designationAr;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 200)
    @Column(name = "Designation_En", nullable = false, length = 200)
    private String designationEn;
    @Size(max = 200)
    @Column(name = "Designation_Fr", length = 200)
    private String designationFr;
    @Basic(optional = false)
    @NotNull
    @Column(name = "Etage", nullable = false)
    private boolean etage;
    @Basic(optional = false)
    @NotNull
    @Column(name = "Facturation", nullable = false)
    private boolean facturation;
    @Basic(optional = false)
    @NotNull
    @Column(name = "Sous_Traitance", nullable = false)
    private boolean sousTraitance;
    @Basic(optional = false)
    @NotNull
    @Column(name = "Actif", nullable = false)
    private boolean actif;
    @CreatedDate
    @Column(name = "Date_Create")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreate;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @CreatedBy
    @Column(name = "User_Create", nullable = false, length = 20)
    private String userCreate;
    @JoinColumn(name = "Code_Famille_Facturation", referencedColumnName = "Code", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private FamilleFacturation codeFamilleFacturation;
    @JoinColumn(name = "Code_Sous_Famille", referencedColumnName = "Code", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private SousFamillePrestation codeSousFamille;
    @JoinColumn(name = "TVA", referencedColumnName = "Code")
    @ManyToOne(fetch = FetchType.LAZY)
    private Tva tva;

    @JsonManagedReference
    @OneToMany(mappedBy = "codePrestation", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<DetailsPriceList> detailsPriceList;

    @JsonManagedReference
    @OneToMany(mappedBy = "prestation", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<DetailsListeCouverture> detailsListeCouvertureList;

    @JsonManagedReference
    @OneToMany(mappedBy = "prestation", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<DetailsPrestationParTypeIntervenant> detailsPrestationParTypeIntervenant;

    @Basic(optional = false)
    @NotNull
    @Column(name = "Cout_Revient", nullable = false, precision = 18, scale = 3)
    private BigDecimal coutRevient;

    @JoinColumn(name = "code_nature_centre", referencedColumnName = "code", nullable = true)
    @ManyToOne(fetch = FetchType.LAZY)
    private NatureCentre natureCentre;

    @Basic(optional = false)
    @NotNull
    @Column(name = "Is_Degree", nullable = false)
    private boolean isDegree;

    @OneToMany(mappedBy = "prestation", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<DetailsPanierPrestation> detailsPanierPrestation;

    @JoinColumn(name = "Code_Modele_Panier", referencedColumnName = "code", nullable = true)
    @ManyToOne(fetch = FetchType.LAZY)
    private ModelePanier codeModele;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "prestation", orphanRemoval = true)
    private Collection<DetailsCostingPrestation> detailsCostingPrestationCollection;

    @Column(name = "FacturationPanier")
    private Boolean facturationPanier;
    @Column(name = "ModifPanierQteCategorie")
    private Boolean modifPanierQteCategorie;
    @Column(name = "PrixFixePanier", precision = 18, scale = 3)
    private BigDecimal prixFixePanier;

    @JoinColumn(name = "code_cost_centre", referencedColumnName = "Code")
    @ManyToOne(fetch = FetchType.LAZY)
    private CostProfitCentre costProfitCentre;

    @Column(name = "duree_realisation_minute")
    private Integer dureeRealisationMinute;

    @OneToMany(mappedBy = "prestation", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<PrestationParSpecialiteMedecin> prestationParSpecialiteMedecin;

    @Basic(optional = false)
    @NotNull
    @Column(name = "Visible", nullable = false, insertable = false, updatable = true)
    private boolean visible;

    @Basic(optional = false)
    @NotNull
    @Column(name = "all_Type_Medecin", nullable = false)
    private Boolean allTypeMedecin;

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public BasePrestation() {
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getCodeSaisie() {
        return codeSaisie;
    }

    public void setCodeSaisie(String codeSaisie) {
        this.codeSaisie = codeSaisie;
    }

    public boolean getAutorisModifierPrix() {
        return autorisModifierPrix;
    }

    public void setAutorisModifierPrix(boolean autorisModifierPrix) {
        this.autorisModifierPrix = autorisModifierPrix;
    }

    public int getCodeBeneficiere() {
        return codeBeneficiere;
    }

    public void setCodeBeneficiere(int codeBeneficiere) {
        this.codeBeneficiere = codeBeneficiere;
    }

    public boolean getCompteRendu() {
        return compteRendu;
    }

    public void setCompteRendu(boolean compteRendu) {
        this.compteRendu = compteRendu;
    }

    public boolean getDemandeObligatoire() {
        return demandeObligatoire;
    }

    public void setDemandeObligatoire(boolean demandeObligatoire) {
        this.demandeObligatoire = demandeObligatoire;
    }

    public String getDesignationAr() {
        return designationAr;
    }

    public void setDesignationAr(String designationAr) {
        this.designationAr = designationAr;
    }

    public String getDesignationEn() {
        return designationEn;
    }

    public void setDesignationEn(String designationEn) {
        this.designationEn = designationEn;
    }

    public String getDesignationFr() {
        return designationFr;
    }

    public void setDesignationFr(String designationFr) {
        this.designationFr = designationFr;
    }

    public boolean getEtage() {
        return etage;
    }

    public void setEtage(boolean etage) {
        this.etage = etage;
    }

    public boolean getFacturation() {
        return facturation;
    }

    public void setFacturation(boolean facturation) {
        this.facturation = facturation;
    }

    public boolean getSousTraitance() {
        return sousTraitance;
    }

    public void setSousTraitance(boolean sousTraitance) {
        this.sousTraitance = sousTraitance;
    }

    public boolean getActif() {
        return actif;
    }

    public void setActif(boolean actif) {
        this.actif = actif;
    }

    public Date getDateCreate() {
        return dateCreate;
    }

    public void setDateCreate(Date dateCreate) {
        this.dateCreate = dateCreate;
    }

    public String getUserCreate() {
        return userCreate;
    }

    public void setUserCreate(String userCreate) {
        this.userCreate = userCreate;
    }

    public FamilleFacturation getCodeFamilleFacturation() {
        return codeFamilleFacturation;
    }

    public void setCodeFamilleFacturation(FamilleFacturation codeFamilleFacturation) {
        this.codeFamilleFacturation = codeFamilleFacturation;
    }

    public SousFamillePrestation getCodeSousFamille() {
        return codeSousFamille;
    }

    public void setCodeSousFamille(SousFamillePrestation codeSousFamille) {
        this.codeSousFamille = codeSousFamille;
    }

    public Tva getTva() {
        return tva;
    }

    public void setTva(Tva tva) {
        this.tva = tva;
    }

    public List<DetailsPriceList> getDetailsPriceList() {
        return detailsPriceList;
    }

    public void setDetailsPriceList(List<DetailsPriceList> detailsPriceList) {
        this.detailsPriceList = detailsPriceList;
    }

    public List<DetailsPrestationParTypeIntervenant> getDetailsPrestationParTypeIntervenant() {
        return detailsPrestationParTypeIntervenant;
    }

    public void setDetailsPrestationParTypeIntervenant(List<DetailsPrestationParTypeIntervenant> detailsPrestationParTypeIntervenant) {
        this.detailsPrestationParTypeIntervenant = detailsPrestationParTypeIntervenant;
    }

    public BigDecimal getCoutRevient() {
        return coutRevient;
    }

    public void setCoutRevient(BigDecimal coutRevient) {
        this.coutRevient = coutRevient;
    }

    public List<DetailsListeCouverture> getDetailsListeCouvertureList() {
        return detailsListeCouvertureList;
    }

    public void setDetailsListeCouvertureList(List<DetailsListeCouverture> detailsListeCouvertureList) {
        this.detailsListeCouvertureList = detailsListeCouvertureList;
    }

    public NatureCentre getNatureCentre() {
        return natureCentre;
    }

    public void setNatureCentre(NatureCentre natureCentre) {
        this.natureCentre = natureCentre;
    }

    public boolean isIsDegree() {
        return isDegree;
    }

    public void setIsDegree(boolean isDegree) {
        this.isDegree = isDegree;
    }

    public List<DetailsPanierPrestation> getDetailsPanierPrestation() {
        return detailsPanierPrestation;
    }

    public void setDetailsPanierPrestation(List<DetailsPanierPrestation> detailsPanierPrestation) {
        this.detailsPanierPrestation = detailsPanierPrestation;
    }

    public ModelePanier getCodeModele() {
        return codeModele;
    }

    public void setCodeModele(ModelePanier codeModele) {
        this.codeModele = codeModele;
    }

    public Collection<DetailsCostingPrestation> getDetailsCostingPrestationCollection() {
        return detailsCostingPrestationCollection;
    }

    public void setDetailsCostingPrestationCollection(Collection<DetailsCostingPrestation> detailsCostingPrestationCollection) {
        this.detailsCostingPrestationCollection = detailsCostingPrestationCollection;
    }

    public Boolean getFacturationPanier() {
        return facturationPanier;
    }

    public void setFacturationPanier(Boolean facturationPanier) {
        this.facturationPanier = facturationPanier;
    }

    public Boolean getModifPanierQteCategorie() {
        return modifPanierQteCategorie;
    }

    public void setModifPanierQteCategorie(Boolean modifPanierQteCategorie) {
        this.modifPanierQteCategorie = modifPanierQteCategorie;
    }

    public BigDecimal getPrixFixePanier() {
        return prixFixePanier;
    }

    public void setPrixFixePanier(BigDecimal prixFixePanier) {
        this.prixFixePanier = prixFixePanier;
    }

    public CostProfitCentre getCostProfitCentre() {
        return costProfitCentre;
    }

    public void setCostProfitCentre(CostProfitCentre costProfitCentre) {
        this.costProfitCentre = costProfitCentre;
    }

    public Integer getDureeRealisationMinute() {
        return dureeRealisationMinute;
    }

    public void setDureeRealisationMinute(Integer dureeRealisationMinute) {
        this.dureeRealisationMinute = dureeRealisationMinute;
    }

    public List<PrestationParSpecialiteMedecin> getPrestationParSpecialiteMedecin() {
        return prestationParSpecialiteMedecin;
    }

    public void setPrestationParSpecialiteMedecin(List<PrestationParSpecialiteMedecin> prestationParSpecialiteMedecin) {
        this.prestationParSpecialiteMedecin = prestationParSpecialiteMedecin;
    }

    public Boolean getAllTypeMedecin() {
        return allTypeMedecin;
    }

    public void setAllTypeMedecin(Boolean allTypeMedecin) {
        this.allTypeMedecin = allTypeMedecin;
    }
   
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (code != null ? code.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof BasePrestation)) {
            return false;
        }
        BasePrestation other = (BasePrestation) object;
        if ((this.code == null && other.code != null) || (this.code != null && !this.code.equals(other.code))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.csys.parametrage.domain.Prestation[ code=" + code + " ]";
    }

}
