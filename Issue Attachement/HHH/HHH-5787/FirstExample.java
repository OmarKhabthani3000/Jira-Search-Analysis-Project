
package roseindia.tutorial.hibernate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;
import annotationbug.SimpleAnnotation;

/**
 * @author Deepak Kumar
 *
 * http://www.roseindia.net
 * Hibernate example to inset data into Contact table
 */
public class FirstExample {
	public static void main(String[] args) {
		Session session = null;

		try{
			// This step will read hibernate.cfg.xml and prepare hibernate for use
			SessionFactory sessionFactory = new AnnotationConfiguration().configure().buildSessionFactory();
			 session =sessionFactory.openSession();
			
				
				SimpleAnnotation simpleAnnotation = new SimpleAnnotation();
				simpleAnnotation.setId(6);
				simpleAnnotation.setId1("avalue");
				simpleAnnotation.setId2(7);
				simpleAnnotation.setName("myname");
				session.save(simpleAnnotation);
				System.out.println("Done saving simple annotation");
				// Actual contact insertion will happen at this step
	            session.flush();
	            
	            //do query
	            
	            session.createQuery("select SimpleAnnotation_Alias from SimpleAnnotation SimpleAnnotation_Alias");
	            session.close();
	            System.out.println("done!");
		}catch(Exception e){
		    e.printStackTrace();
			System.out.println(e.getMessage());
		}finally{
			

			}
		
	}
}
