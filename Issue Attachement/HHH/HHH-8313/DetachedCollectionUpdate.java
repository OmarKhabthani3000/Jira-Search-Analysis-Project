package net.gmc.planner;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistryBuilder;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DetachedCollectionUpdate {
	
	private static final Logger logger = LoggerFactory.getLogger(DetachedCollectionUpdate.class);
	
	private PARENT pOrig;
	private PARENT p;

	private PARENT2 p2Orig;
	private PARENT2 p2;
	
	private SessionFactory factory;
	
	@Before
	public void setUp() {
		Configuration cfg = new Configuration()
		.setProperty("hibernate.dialect", "org.hibernate.dialect.SQLServer2008Dialect")
		.setProperty("hibernate.connection.driver_class", "net.sourceforge.jtds.jdbc.Driver")
		.setProperty("hibernate.connection.url", "jdbc:jtds:sqlserver://t218:1433/planner_neubauer")
		.setProperty("hibernate.connection.username", "neubauer")
		.setProperty("hibernate.connection.password", "heslo")
		.addAnnotatedClass(PARENT.class)
		.addAnnotatedClass(PARENT2.class)
		.addAnnotatedClass(CHILD.class)
		;
		
		//factory = cfg.buildSessionFactory(new ServiceRegistryBuilder().applySettings(cfg.getProperties()).buildServiceRegistry());
		factory = cfg.buildSessionFactory();

		SchemaExport schemaExport = new SchemaExport(cfg);
		schemaExport.create(true, true);
	}
	

	@Test
	public void testManyToMany_parentIdAsGetter() throws Exception {
		
		logger.info("(1) save parent with 1 child in collection");
		doInTx(
			new Operation() {
			@Override public void run(Session sess) {
				pOrig = new PARENT();
				pOrig.id = 1l;
				pOrig.value = "Hello";
				CHILD ch = new CHILD();
				ch.id=1l;
				pOrig.childs.add(ch);
				sess.save(pOrig);
			}});
		
		logger.info("(2) get");
		doInTx(new Operation() {
			@Override public void run(Session sess) {
				p = (PARENT) sess.get(PARENT.class, pOrig.id);
				Assert.assertEquals(1, p.childs.size());
			}});
		
		logger.info("(3) update - should preserve the collection");
		doInTx(new Operation() {
			@Override public void run(Session sess) {
				p.value = "world";
				sess.update(p);
			}});
		
		logger.info("(4) get - collection should be the same");
		doInTx(new Operation() {
			@Override public void run(Session sess) {
				PARENT pAfter = (PARENT) sess.get(PARENT.class, pOrig.id);
				Assert.assertEquals(1, pAfter.childs.size());
			}});
	}

	@Test
	public void testManyToMany_parentIdAsField() throws Exception {
		
		logger.info("(1) save");
		doInTx(
			new Operation() {
			@Override public void run(Session sess) {
				p2Orig = new PARENT2();
				p2Orig.id = 1l;
				p2Orig.value = "Hello";
				CHILD ch = new CHILD();
				ch.id=1l;
				p2Orig.childs.add(ch);
				sess.save(p2Orig);
			}});
		
		logger.info("(2) get");
		doInTx(new Operation() {
			@Override public void run(Session sess) {
				p2 = (PARENT2) sess.get(PARENT2.class, p2Orig.id);
				Assert.assertEquals(1, p2.childs.size());
			}});
		
		logger.info("(3) update");
		doInTx(new Operation() {
			@Override public void run(Session sess) {
				p2.value = "world";
				sess.update(p2);
			}});
		
		logger.info("(4) get");
		doInTx(new Operation() {
			@Override public void run(Session sess) {
				PARENT2 p2After = (PARENT2) sess.get(PARENT2.class, p2Orig.id);
				Assert.assertEquals(1, p2After.childs.size());
			}});
	}
	
	
	@Entity
	@Table(name="X_PARENT")
	public static class PARENT  {
		
		private Long id;
		
		@Id
		@GeneratedValue()
		public Long getId() {return id;}
		public void setId(Long id) {this.id = id;}
		
		@Column(name="VAL") String value;
		
		@ManyToMany(fetch=FetchType.EAGER, cascade=CascadeType.ALL)
		@JoinTable(
				name="X_PARENT_X_CHILD",
				joinColumns={
						@JoinColumn(name="PARENT_ID")},
				inverseJoinColumns=@JoinColumn(name="CHILD_ID"))
		List<CHILD> childs = new ArrayList<CHILD>();
	}

	
	@Entity
	@Table(name="X_PARENT2")
	public static class PARENT2  {
		
		@Id
		@GeneratedValue()
		Long id;

		@Column(name="VAL") String value;
		
		@ManyToMany(fetch=FetchType.EAGER, cascade=CascadeType.ALL)
		@JoinTable(
				name="X_PARENT2_X_CHILD",
				joinColumns={
						@JoinColumn(name="PARENT2_ID")},
				inverseJoinColumns=@JoinColumn(name="CHILD_ID"))
		List<CHILD> childs = new ArrayList<CHILD>();
	}
	
	
	@Entity
	@Table(name="X_CHILD")
	public static class CHILD {
		@Id Long id;
	}
	
	private interface Operation {
		public void run(Session sess);
	}
	
	private void doInTx(Operation operation) throws Exception {
		Session sess = factory.openSession();
		Transaction tx = sess.beginTransaction();
		try {
			operation.run(sess);
			tx.commit();
		} catch(Exception e) {
			tx.rollback();
			throw e;
		} finally {
			sess.close();
		}
	}
	
}
