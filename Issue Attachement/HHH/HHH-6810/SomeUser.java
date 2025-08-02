package bug.report.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class SomeUser {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private long id;

	private String name;

	@OneToMany(mappedBy = "user")
	private Set<SomeItem> items = new HashSet<SomeItem>();

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void addItem(SomeItem item) {
		item.setUser(this);
		items.add(item);
	}

	public Set<SomeItem> getItems() {
		return items;
	}
}
