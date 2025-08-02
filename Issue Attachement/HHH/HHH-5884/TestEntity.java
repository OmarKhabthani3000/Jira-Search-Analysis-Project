package foo;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class TestEntity {

    @Id
    @GeneratedValue (strategy = GenerationType.AUTO)
    private Long id;
    private String field1;
    private String field2;

    public TestEntity() {
    }

    public TestEntity(String field1, String field2) {
        this.field1 = field1;
        this.field2 = field2;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getField1() {
        return field1;
    }

    public void setField1(String field1) {
        this.field1 = field1;
    }

    public String getField2() {
        return field2;
    }

    public void setField2(String field2) {
        this.field2 = field2;
    }

    @Override
    public String toString ()
    {
        return getClass().getName()
               + "["
               + "id=" + ((getId() == null) ? "null" : getId().toString())
               + ", "
               + "field1=" + ((getField1() == null) ? "null" : getField1().toString())
               + ", "
               + "field2=" + ((getField2() == null) ? "null" : getField2().toString())
               + "]\n";
    }
}
