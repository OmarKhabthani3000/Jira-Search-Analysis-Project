package bug;

import javax.persistence.*;

/**
 * @author Mohd Adnan (adnan@spmsoftware.com)
 */
@Entity
@Access(AccessType.FIELD)
@DiscriminatorValue("2")
public class AbstractChild2 extends ParentHierarchy {

    @Column(name = "VALUE2")
        String value;
}
