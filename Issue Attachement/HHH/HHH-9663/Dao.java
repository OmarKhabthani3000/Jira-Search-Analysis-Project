package pl.comit.orm.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import pl.comit.orm.model.File;
import pl.comit.orm.model.FileContent;

@Repository
public class Dao {

	@PersistenceContext
	private EntityManager entityManager;

	@Transactional
	public void assureCreatedTaskAndNote(int fileId, int contentId) {
		FileContent content = entityManager.find(FileContent.class, contentId);
		if (content == null) {
			content = new FileContent();
			content.setId(contentId);
			entityManager.persist(content);
		}

		File file = entityManager.find(File.class, fileId);
		if (file == null) {
			file = new File();
			file.setId(fileId);
			entityManager.persist(file);
		}
		file.setContent(content);
	}

	@Transactional
	public void removeContent(int fileId) {
		File file = entityManager.find(File.class, fileId);
		file.setContent(null);
	}

	public FileContent find(int contentId) {
		return entityManager.find(FileContent.class, contentId);
	}
}
