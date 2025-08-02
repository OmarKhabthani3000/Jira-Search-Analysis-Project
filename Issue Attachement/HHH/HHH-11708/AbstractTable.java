
package exemple;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;

@MappedSuperclass
public abstract class AbstractTable
{
    @Id
    @Column(name = "id")
    private int id;

    @NotNull
    @ManyToMany(fetch = FetchType.LAZY)
    private List<Dependency> dependencies;

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public List<Dependency> getDependencies()
    {
        return dependencies;
    }

    public void setDependencies(List<Dependency> dependencies)
    {
        this.dependencies = dependencies;
    }

}
