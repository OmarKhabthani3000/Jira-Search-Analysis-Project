
package exemple;

import javax.persistence.AssociationOverride;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Table;

@Entity
@Table(name = "table")
@AssociationOverride(joinTable = @JoinTable(name = "dependency_table", joinColumns = @JoinColumn(name = "id_table"), inverseJoinColumns = @JoinColumn(name = "id_dependency")), name = "dependencies")
public class MyTable extends AbstractTable
{

}
