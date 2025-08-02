package hibernateTesting;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateTest {
	
	public static void main(String[] args) {
		Contact contact = new Contact();

		contact.setfName("another user");
		contact.setlName("another lastname");
		contact.setSex('F');
		
		Phone phone = new Phone();
		phone.setPhoneType("Mobile");
		phone.setPhoneNumber("404-992-3529");
		
		Phone phone2 = new Phone();
		phone2.setPhoneType("Home");
		phone2.setPhoneNumber("470-424-0904");
		
		contact.getListOfPhone().add(phone);
		contact.getListOfPhone().add(phone2);
		
		Address addr = new Address();
		addr.setAddressType("Home");
		addr.setAptNo("1234");
		addr.setFirstLine("Plantation trace drive");
		addr.setCity("Duluth");
		addr.setState("Georgia");
		addr.setCountry("USA");
		addr.setZip("30096");
		
		contact.getListOfAddress().add(addr);
		
		SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
		
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		
		session.save(contact);
		session.getTransaction().commit();
		
		contact = null;
		
		contact = (Contact)session.get(Contact.class, (long)1);
		System.out.println("User with key 1 is :"+contact.getfName());
		
	}

}
