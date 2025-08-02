package pl.comit.orm.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Note {

	private int id;

	private Task task;

	@Id
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}
}
