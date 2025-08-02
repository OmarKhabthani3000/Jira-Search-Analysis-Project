package model;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.MapKeyClass;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.UniqueConstraint;

@Entity
public class Person {
    @Id @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    @OneToMany( mappedBy="owner", fetch=FetchType.EAGER, cascade=CascadeType.ALL)
    @MapKeyColumn(name="phone_type", table="Phone")
    private Map<String, Phone> phones;

    public Map<String, Phone> getPhones() {
        return phones;
    }
    public void setStudents(Map<String, Phone>phones ) {
        this.phones = phones;
    }

}
