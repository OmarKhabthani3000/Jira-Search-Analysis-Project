package net.codejava.spring;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class Salle extends DescriptionLieu {

    private static final long serialVersionUID = 1L;

    @ManyToOne(optional = false)
    @JoinColumn
    private Lieu lieu;

    @OneToMany(mappedBy = "salle", cascade = CascadeType.ALL)
    private Set<Equipement> equipements;

    public Salle() {
        super();
        equipements = new HashSet<>();
    }

    public Lieu getLieu() {
        return lieu;
    }

    public void setLieu(Lieu lieu) {
        this.lieu = lieu;
    }

    public Set<Equipement> getEquipements() {
        return equipements;
    }

    public void setEquipements(Set<Equipement> equipements) {
        this.equipements = equipements;
    }
}
