package intest;

import java.io.Serializable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

@Entity
@NamedQueries({
    @NamedQuery(name="MyEntity.query1",
                query="SELECT m FROM MyEntity m WHERE val.a IN (:values)"),
    @NamedQuery(name="MyEntity.query2",
                query="SELECT m FROM MyEntity m WHERE val.a NOT IN (:values)"),
    @NamedQuery(name="MyEntity.query3",
                query="SELECT m FROM MyEntity m WHERE val IN (:values)"),
    @NamedQuery(name="MyEntity.query4",
                query="SELECT m FROM MyEntity m WHERE val NOT IN (:values)")
})
public class MyEntity implements Serializable {

    @Id
    int id;

    @Embedded
    MyRowValue val;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public MyRowValue getVal() { return val; }
    public void setVal(MyRowValue val) { this.val = val; }
}
