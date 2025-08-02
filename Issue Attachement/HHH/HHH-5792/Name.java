package foo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Name implements Serializable {

    private static final long serialVersionUID = -4531151817039187822L;

    @Column(name="FIRST_NAME")
    public String             firstName;

    @Column(name="LAST_NAME")
    public String             lastName;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------
    public Name() {

    }
}