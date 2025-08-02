package foo;

import java.io.Serializable;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Client implements Serializable {

    private static final long serialVersionUID = 7767765055716312630L;

    @Id
    public int                id;

    @Embedded
    public Name               name;
}