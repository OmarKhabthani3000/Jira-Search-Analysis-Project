package pl.comit.orm.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class FileContent {

	private int id;

	@Id
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
