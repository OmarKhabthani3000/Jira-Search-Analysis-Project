import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;


@Entity(name="b")
public class ModelB {
	
	private int id;
	private String name;
	private ModelA parent;

	@Id
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@ManyToOne
	@JoinColumn(name="id_of_a")
	public ModelA getParent() {
		return parent;
	}

	public void setParent(ModelA parent) {
		this.parent = parent;
	}
	
	@Override
	public String toString() {
		return id + ":" + name;
	}

}
