/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package com.example;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Field;
import java.util.Iterator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.action.internal.EntityInsertAction;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.engine.spi.ActionQueue;
import org.hibernate.engine.spi.ExecutableList;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.persister.entity.EntityPersister;
import org.junit.Before;
import org.junit.Test;

public class MyTest {

	private SessionFactory sf;

	@Before
	public void setup() {
		StandardServiceRegistryBuilder srb = new StandardServiceRegistryBuilder()
				.applySetting("hibernate.show_sql", "true").applySetting("hibernate.hbm2ddl.auto", "create-drop");
		Metadata metadata = new MetadataSources(srb.build()).addAnnotatedClass(Article.class).buildMetadata();
		sf = metadata.buildSessionFactory();
	}

	@Test
	public void test() throws Exception {
		try (Session session = sf.openSession()) {
			Transaction tx = session.beginTransaction();
			Article article = new Article();
			session.save(article); // need retrieve generated ID for construct path later
			article.setPath("/article/" + article.getId());

			hack(session, article); // save an update if id is not identity column

			tx.commit();
		}

		try (Session session = sf.openSession()) {
			Article article = session.get(Article.class, 1L);
			assertEquals("/article/1", article.getPath());
		}
	}

	private void hack(Session session, Article article) {
		try {
			ActionQueue queue = ((SessionImplementor) session).getActionQueue();
			Field field = ActionQueue.class.getDeclaredField("insertions");
			field.setAccessible(true);
			@SuppressWarnings("unchecked")
			ExecutableList<EntityInsertAction> insertions = (ExecutableList<EntityInsertAction>) field.get(queue);
			if (insertions != null) {
				Iterator<EntityInsertAction> it = insertions.iterator();
				while (it.hasNext()) {
					EntityInsertAction action = it.next();
					if (action.getInstance() == article) {
						Object[] state = action.getState();
						EntityPersister ep = action.getPersister();
						String[] propertyNames = ep.getPropertyNames();
						for (int i = 0; i < propertyNames.length; i++) {
							if (propertyNames[i].equals("path")) {
								state[i] = article.getPath();
							}
						}
						break;
					}
				}
			}
		}
		catch (Exception ignore) {
			// It's safe even hacking failed
		}
	}

	@Entity
	@Table(name = "article")
	public static class Article {

		@Id
		@GeneratedValue(strategy = GenerationType.AUTO)
		private Long id;

		private String path;

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String getPath() {
			return path;
		}

		public void setPath(String path) {
			this.path = path;
		}

	}

}
