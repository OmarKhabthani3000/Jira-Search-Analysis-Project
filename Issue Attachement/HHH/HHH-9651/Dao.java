package pl.comit.orm.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import pl.comit.orm.model.Note;
import pl.comit.orm.model.Task;

@Repository
public class Dao {

	@PersistenceContext
	private EntityManager entityManager;

	@Transactional
	public void assureCreatedTaskAndNote(int taskId, int noteId) {
		Task task = entityManager.find(Task.class, taskId);
		if (task == null) {
			task = new Task();
			task.setId(taskId);
			entityManager.persist(task);
		}

		Note note = entityManager.find(Note.class, noteId); 
		if (note == null) {
			note = new Note();
			note.setId(noteId);
			note.setTask(task);
			entityManager.persist(note);
		}
	}

	@Transactional
	public void removeNote(int ordId, int noteId) {
		Task ord = entityManager.find(Task.class, ordId);
		entityManager.remove(ord.getNotes().get(noteId));
	}

	public Note find(int noteId) {
		return entityManager.find(Note.class, noteId);
	}
}
