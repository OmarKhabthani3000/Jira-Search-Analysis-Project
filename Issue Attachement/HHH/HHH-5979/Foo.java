package foo;

import java.util.Properties;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.sql.DataSource;

import org.dom4j.Element;
import org.hibernate.EntityMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.cfg.Environment;
import org.hibernate.ejb.Ejb3Configuration;
import org.postgresql.ds.PGSimpleDataSource;


public class Foo {
	
	@Entity
	@Table(name="mytable")
	public static class A {
		
		@Id
		@GeneratedValue
		Long id;
		byte[] data;
		
	}
	
	public static void main(String[] args) {
		EntityManagerFactory emf = null;
		try {
			DataSource ds = getDataSource();
			emf = getEntityManagerFactory(ds);
			createTable(emf);
			emf.close();
			
			emf = getEntityManagerFactory(ds);
			EntityManager em = emf.createEntityManager();
	    	Session session = (Session)em.getDelegate();
			Session dom4jSession = session.getSession(EntityMode.DOM4J);
			Query query = dom4jSession.createQuery("from Foo$A");
			for (Object obj: query.list()) {
				Element element = (Element)obj;
//			Iterator<?> iter = query.iterate();
//			while (iter.hasNext()) {
//				Element element = (Element)iter.next();
				System.out.println(element.asXML());
				System.out.println("##############################");
			}		
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if (emf != null) {
				dropTable(emf);
				emf.close();
			}
		}
	}

	static DataSource getDataSource() {
		PGSimpleDataSource ds = new PGSimpleDataSource(); 
		ds.setDatabaseName("test");
		ds.setServerName("localhost");
		ds.setUser("sa");
		ds.setPassword("password");
		return ds;
	}
	
	static EntityManagerFactory getEntityManagerFactory(DataSource ds) {
        Properties properties = new Properties();
        properties.put(Environment.DIALECT, "org.hibernate.dialect.PostgreSQLDialect");
        Ejb3Configuration cfg = new Ejb3Configuration().configure((String)null, properties);
        cfg.addAnnotatedClass(A.class);
        cfg.buildMappings();
        cfg.setDataSource(ds);
        return cfg.buildEntityManagerFactory();
	}
	
	static void createTable(EntityManagerFactory emf) {
		EntityManager em = emf.createEntityManager();
		EntityTransaction tx;
		javax.persistence.Query q;
		
		tx = em.getTransaction();
		tx.begin();
		q= em.createNativeQuery("CREATE TABLE mytable(id INTEGER PRIMARY KEY, data BYTEA)");
		q.executeUpdate();
		q = em.createNativeQuery("INSERT INTO mytable VALUES (1, '\\176\\177\\200\\201\\202')");
		q.executeUpdate();
		q = em.createNativeQuery("INSERT INTO mytable VALUES (2, '\\001\\002\\003')");
		q.executeUpdate();
		q = em.createNativeQuery("INSERT INTO mytable VALUES (3, '123')");
		q.executeUpdate();
		tx.commit();		
		em.close();
	}
	
	static void dropTable(EntityManagerFactory emf) {
		EntityManager em = emf.createEntityManager();
		EntityTransaction tx;
		javax.persistence.Query q;

		tx = em.getTransaction();
		tx.begin();
		q = em.createNativeQuery("DROP TABLE mytable");
		q.executeUpdate();
		tx.commit();
		
		em.close();
	}
	
}
