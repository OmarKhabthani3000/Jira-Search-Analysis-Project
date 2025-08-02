package bug;

import javax.persistence.*;

/**
 * @author Mohd Adnan (adnan@spmsoftware.com)
 */
@Entity
@Table(name = "A_ABS_PARENT")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Access(AccessType.FIELD)
@DiscriminatorColumn(name = "DISC")
public class ParentHierarchy {

    @Id
       @GeneratedValue
       @Column(name = "ID")
       Integer id;
}
