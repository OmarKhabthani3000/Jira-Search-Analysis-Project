package bug.report.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class SomeItem {
	@Id
	@GeneratedValue(strategy= GenerationType.SEQUENCE)
	private long id;

	private String description;

	@ManyToOne
	private SomeUser user;

	public long getId() {
		return id;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public void setUser(SomeUser user) {
		this.user = user;
	}

	public SomeUser getUser() {
		return user;
	}
}
