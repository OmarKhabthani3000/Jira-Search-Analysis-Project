package zzz;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "stuff")
public class Stuff {



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false, insertable = false, updatable = false)
    private int id;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "stuff")
    private Set<Thing> things = new HashSet<>();

    
    public Stuff() {
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Set<Thing> getThings() {
        return things;
    }

    public void setThings(Set<Thing> recipients) {
        this.things = recipients;
    }

}
