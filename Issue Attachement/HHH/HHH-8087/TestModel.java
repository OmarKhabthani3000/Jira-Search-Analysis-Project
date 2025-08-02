package models;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import org.hibernate.envers.Audited;

@Audited
@Entity
public class TestModel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
@ManyToMany
@JoinTable(
name="test_assoc",
joinColumns = @JoinColumn( name="id_test"),
inverseJoinColumns = @JoinColumn( name="id_assoc")
)
public List<Assoc> assocs=new ArrayList<Assoc>();

}
