package pl.comit.orm.model;

import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;

@Entity
public class Task {

	private int id;

	private Map<Integer, Note> notes;

	@Id
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@OneToMany(cascade = CascadeType.PERSIST, mappedBy = "task", orphanRemoval = true)
	@MapKey
	public Map<Integer, Note> getNotes() {
		return notes;
	}

	public void setNotes(Map<Integer, Note> notes) {
		this.notes = notes;
	}
}
