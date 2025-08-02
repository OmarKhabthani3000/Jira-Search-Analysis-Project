package net.codejava.spring;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.OneToMany;

@Entity
public class Lieu extends DescriptionLieu {

    private static final long serialVersionUID = 1L;

    @OneToMany(mappedBy = "lieu")
    private Set<Equipement> equipements;

    @OneToMany(mappedBy = "lieu")
    private Set<Salle> salles;

    public Lieu() {
        super();
    }

    public Set<Equipement> getEquipements() {
        return equipements;
    }

    public void setEquipements(Set<Equipement> equipements) {
        this.equipements = equipements;
    }

    public Set<Salle> getSalles() {
        return salles;
    }

    public void setSalles(Set<Salle> salles) {
        this.salles = salles;
    }
}
