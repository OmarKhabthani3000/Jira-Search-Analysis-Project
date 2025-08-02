package com.finantix;

import java.util.Arrays;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;
import org.hibernate.classic.Session;
import org.hibernate.tool.hbm2ddl.SchemaExport;

public class ReorderedParameters {

	@Entity
	@Table(name = "Assoc")
	public static class Assoc {

		private Long oid;

		private Topic type;

		private List<Topic> constituents;

		public Assoc() {
			super();
		}

		public Assoc(Topic type, List<Topic> constituents) {
			super();
			this.type = type;
			this.constituents = constituents;
		}

		@ManyToMany
		@IndexColumn(name = "topic_index", nullable = false, base = 0)
		public List<Topic> getConstituents() {
			return constituents;
		}

		@Id
		@GeneratedValue(strategy = GenerationType.AUTO)
		public Long getOid() {
			return oid;
		}

		@ManyToOne(optional = false)
		public Topic getType() {
			return type;
		}

		public void setConstituents(List<Topic> constituents) {
			this.constituents = constituents;
		}

		public void setOid(Long oid) {
			this.oid = oid;
		}

		public void setType(Topic type) {
			this.type = type;
		}

		@Override
		public String toString() {
			return "Assoc[oid=" + oid + ",type=" + type + ",constituents="
					+ constituents + "]";
		}
	}

	@Entity
	@Table(name = "Topic")
	public static class Topic {

		private Long oid;

		private String name;

		public Topic() {
			super();
		}

		public Topic(String name) {
			super();
			this.name = name;
		}

		@Column(name = "name", length = 255)
		public String getName() {
			return name;
		}

		@Id
		@GeneratedValue(strategy = GenerationType.AUTO)
		public Long getOid() {
			return oid;
		}

		public void setName(String name) {
			this.name = name;
		}

		public void setOid(Long oid) {
			this.oid = oid;
		}

		@Override
		public String toString() {
			return "Topic[oid=" + oid + ",name=" + name + "]";
		}
	}

	private static SessionFactory fact;

	private static void createAssociations() {
		Session session = fact.openSession();
		Transaction tx = session.beginTransaction();

		final Topic fooTopic = findTopic(session, "Foo");
		final Topic barTopic = findTopic(session, "Bar");

		final Topic isA = findTopic(session, "is-a");
		final Topic inside = findTopic(session, "inside");

		session.save(new Assoc(isA, Arrays.asList(fooTopic, barTopic)));
		session.save(new Assoc(isA, Arrays.asList(barTopic, fooTopic)));

		session.save(new Assoc(inside, Arrays.asList(fooTopic, barTopic)));
		session.save(new Assoc(inside, Arrays.asList(barTopic, fooTopic)));

		tx.commit();
		session.close();
	}

	private static void createTopic(final String name) {
		Session session = fact.openSession();
		Transaction transaction = session.beginTransaction();

		Topic topic = new Topic(name);
		session.save(topic);

		transaction.commit();
		session.close();
	}

	@SuppressWarnings("unchecked")
	private static List<Assoc> findAssociationsByTypeAndTopicInPosition(
			Session session, Topic type, Topic topic, int position) {

		final Query query = session.createQuery("" + //
				"from com.finantix.ReorderedParameters$Assoc a" //
				+ " where a.type.oid = ?" // 
				+ " and a.constituents[?].oid = ?");

		query.setParameter(0, type.getOid());
		query.setParameter(1, Integer.valueOf(position));
		query.setParameter(2, topic.getOid());

		return (List<Assoc>) query.list();

	}

	private static Topic findTopic(Session session, final String topicName) {
		Query q = session
				.createQuery("from com.finantix.ReorderedParameters$Topic t where t.name = ?");
		q.setParameter(0, topicName);
		return (Topic) q.list().get(0);
	}

	private static void initializeFactory() {
		final Configuration configuration = new AnnotationConfiguration() //
				.addAnnotatedClass(Topic.class) //
				.addAnnotatedClass(Assoc.class) //
				.setProperty("hibernate.dialect",
						"org.hibernate.dialect.H2Dialect") //
				.setProperty("hibernate.connection.driver_class",
						"org.h2.Driver") //
				.setProperty("hibernate.connection.url",
						"jdbc:h2:mem:ReorderedParameters") //
				.setProperty("hibernate.connection.username", "sa") //
				.setProperty("hibernate.connection.password", "") //
				// .setProperty("hibernate.show_sql", "true") //
				// .setProperty("hibernate.format_sql", "true") //
				// .setProperty("hibernate.use_sql_comments", "true") //
				.setProperty("hibernate.hbm2ddl.auto", "create-drop");

		SchemaExport export = new SchemaExport(configuration);
		export.create(false, true);

		fact = configuration //
				.buildSessionFactory();
	}

	private static void listAssociations() {
		Session session = fact.openSession();

		Query query = session
				.createQuery("from com.finantix.ReorderedParameters$Assoc");
		List<?> list = query.list();
		for (Object o : list) {
			System.out.println(o);
		}

		session.close();
	}

	private static void listTopics() {
		Session session = fact.openSession();

		Query query = session
				.createQuery("from com.finantix.ReorderedParameters$Topic");
		List<?> list = query.list();
		for (Object o : list) {
			System.out.println(o);
		}

		session.close();
	}

	public static void main(String[] args) {

		initializeFactory();

		createTopic("is-a");
		createTopic("inside");
		createTopic("Foo");
		createTopic("Bar");

		listTopics();

		createAssociations();

		listAssociations();

		Session session = fact.openSession();
		Transaction tx = session.beginTransaction();

		final Topic isA = findTopic(session, "is-a");
		final Topic inside = findTopic(session, "inside");

		final Topic foo = findTopic(session, "Foo");
		final Topic bar = findTopic(session, "Bar");

		System.out.println();
		System.out.println("----------------------------------------------");
		System.out.println();

		test(session, isA, foo, 0);
		test(session, isA, foo, 1);

		test(session, inside, foo, 0);
		test(session, inside, foo, 1);

		test(session, isA, bar, 0);
		test(session, isA, bar, 1);

		test(session, inside, bar, 0);
		test(session, inside, bar, 1);

		tx.commit();
		session.close();
	}

	private static void test(Session session, final Topic type,
			final Topic topic, final int position) {
		System.out.println("Finding associations with type: " + type + " and "
				+ topic + " in position " + position + "...");
		final List<Assoc> assocs = findAssociationsByTypeAndTopicInPosition(
				session, type, topic, position);

		if (assocs.size() == 1) {

			final Assoc assoc = assocs.get(0);

			System.out.println(assoc);
			System.out.println("Type is " + type + ": "
					+ (assoc.getType() == type));
			System.out.println("Topic in position " + position + " is " + topic
					+ ": " + (assoc.getConstituents().get(position) == topic));

		} else {
			System.out.println("Not exactly one assoc : " + assocs.size()
					+ " assocs.");
		}
		System.out.println();
	}
}
