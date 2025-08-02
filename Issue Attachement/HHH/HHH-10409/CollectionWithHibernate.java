package org.adithya.hibernate;

import java.io.Serializable;

import org.adithya.modelClasses.Address;
import org.adithya.modelClasses.Student;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.H2Dialect;

public class CollectionWithHibernate 
{
	public static void main(String args[])
	{
		Address address1=new Address();
		Address address2=new Address();
		
		address1.setCity("Missouri");
		address1.setPincode("665511");
		address1.setState("Kansas");
		address1.setStreet("Coser");
		
		address2.setCity("Missouri");
		address2.setPincode("665522");
		address2.setState("Kansas");
		address2.setStreet("chusnut");
		
		Student stud1=new Student();
		stud1.setName("Adithya");
		stud1.setRoll(1814);
		stud1.getAddress().add(address1);
		stud1.getAddress().add(address2);
		
		
		SessionFactory sessionFactory=new Configuration().configure().buildSessionFactory();
		Session session=sessionFactory.openSession();
		session.beginTransaction();
		session.save(stud1);
		session.getTransaction().commit();				
		
	}
	
	
}
