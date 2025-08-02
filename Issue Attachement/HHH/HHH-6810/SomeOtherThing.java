package bug.report.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class SomeOtherThing {
	@Id
	@GeneratedValue(strategy= GenerationType.SEQUENCE)
	private long id;

	private String name;
	
	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
