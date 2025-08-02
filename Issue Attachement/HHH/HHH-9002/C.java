package sk.c.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

@Entity
public class C {
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Id
	Long id;
	
	@NotNull
	String name;	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "C [id=" + id + ", name=" + name + "]";
	}

}
