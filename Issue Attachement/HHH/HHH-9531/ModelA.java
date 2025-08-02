import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity(name="a")
public class ModelA {
	
	private int id;
	private String name;
	private List<ModelB> listofB;
	
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
	
	@OneToMany(mappedBy="parent", fetch=FetchType.EAGER)
	public List<ModelB> getListofB() {
		return listofB;
	}
	public void setListofB(List<ModelB> listofB) {
		this.listofB = listofB;
	}
	
	@Override
	public String toString() {
		return id + ":" + name;
	}

}
