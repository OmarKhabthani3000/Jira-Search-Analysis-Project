package test;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class QuotingBug {

	private Integer id = 0;
	private String name;
	private String description;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	public Integer getId() {
		return this.id;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	@Column(length = 256)
	public String getName() {
		return this.name;

	}

	public void setName(final String name) {
		this.name = name;
	}

	@Column(length = 256)
	public String getDescription() {
		return this.description;

	}

	public void setDescription(final String description) {
		this.description = description;
	}
}
