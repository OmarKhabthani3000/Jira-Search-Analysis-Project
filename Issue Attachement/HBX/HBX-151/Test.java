package test;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.tool.hbm2ddl.SchemaExport;

public class Test {
	public static void main(String[] args) {
		try {
			//System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.Jdk14Logger");
			//Logger.getLogger("org.hibernate").setLevel(Level.FINEST);
			//Logger.getLogger("org.hibernate.SQL").setLevel(Level.FINEST);
			//Logger.getLogger("org.hibernate.type").setLevel(Level.FINEST);
			
			AnnotationConfiguration ac = new AnnotationConfiguration();
			ac.addAnnotatedClass(One.class);
			ac.addAnnotatedClass(Many.class);
			SchemaExport se = new SchemaExport(ac);
			se.create(true, true);
			SessionFactory sf = ac.buildSessionFactory();
			Session s = sf.openSession();
			Transaction t = s.beginTransaction();
			Many m = new Many();
			One o = new One();
			o.getMany().add(m);
			s.persist(o);
			t.commit();
			s.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}