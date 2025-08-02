import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name="lead", uniqueConstraints = { })
public class Lead implements Serializable {

    private int leadid;

    private Set<Lead> duplicateLeadChildren;
    private Lead duplicateLeadParent;

    public Lead () {

    }

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="leadid", unique=true, nullable=false, insertable=true, updatable=true)
    public int getLeadid() {
        return this.leadid;
    }
    public void setLeadid(int leadid) {
        this.leadid = leadid;
    }

    // More fields

    /**
     * Returns a set of leads that has been marked as duplicates of this lead.
     * @return
     */
    @OneToMany(fetch=FetchType.LAZY)
    @JoinTable(name="leadduplicate"
            , joinColumns={@JoinColumn(name="parentleadid")}
            , inverseJoinColumns={@JoinColumn(name="childleadid")})
    @NotFound(action=NotFoundAction.IGNORE)
    public Set<Lead> getDuplicateLeadChildren(){
        return duplicateLeadChildren;
    }

    public void setDuplicateLeadChildren(Set<Lead> duplicateLeadChildren) {
        this.duplicateLeadChildren = duplicateLeadChildren;
    }

    /**
     * Returns a lead if this lead has been marked as a duplicate.
     *
     * The returned lead is the lead that should be used as the main lead in all future contact with the company.
     * @return
     */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinTable(name="leadduplicate"
            , joinColumns={@JoinColumn(name="childleadid")}
            , inverseJoinColumns={@JoinColumn(name="parentleadid")})
    @NotFound(action=NotFoundAction.IGNORE)
    public Lead getDuplicateLeadParent(){
        return duplicateLeadParent;
    }

    public void setDuplicateLeadParent(Lead duplicateLeadParent) {
        this.duplicateLeadParent = duplicateLeadParent;
    }
}
