package test;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Loader;

@Entity
@Table(name = "TEST")
@NamedQueries( {
    @NamedQuery(name = "AnEntity.findAll", query = "select e from AnEntity e"),
    @NamedQuery(name = "AnEntity.count", query = "select count(e) from AnEntity e"),
    @NamedQuery(name = "AnEntity.LoaderFind", query = "select id, test from AnEntity e where id = :id")
})
@Loader(namedQuery = "AnEntity.LoaderFind")
public class AnEntity implements Serializable {

    public AnEntity() {}

    public AnEntity(String id, String test) {
        this.id = id;
        this.test = test;
    }

    @Id
    @Column(name = "ID", nullable = false)
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    private String id;

    @Column(name = "TEST")
    private String test;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }

}
