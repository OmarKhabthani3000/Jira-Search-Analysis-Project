package bug;

import javax.persistence.*;

/**
 * @author Mohd Adnan (adnan@spmsoftware.com)
 */
@Entity
@Access(AccessType.FIELD)
@DiscriminatorValue("1")
public class AbstractChild1 extends ParentHierarchy {

    @Column(name = "VALUE1")
        String value;
}
