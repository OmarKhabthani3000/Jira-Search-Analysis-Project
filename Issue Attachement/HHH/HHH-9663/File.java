package pl.comit.orm.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class File {

	private int id;

	private FileContent content;

	@Id
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@OneToOne(fetch = FetchType.LAZY, orphanRemoval = true)
	public FileContent getContent() {
		return content;
	}

	public void setContent(FileContent content) {
		this.content = content;
	}
}
