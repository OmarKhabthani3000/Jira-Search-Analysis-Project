package com.kodak.intersystem.data.color;

import java.util.List;

import org.apache.log4j.PropertyConfigurator;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class ColorRepository
{
	static SessionFactory sessionFactory;    

    static void createAndStoreLibrary()
    {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        
        Color color = new Color("Yellow", "donor");
        Library library = new Library("Spot", "whatever");
        
        color.addLibrary(library);
        library.addColor(color);
        
        session.save(color);
        session.save(library);

        session.getTransaction().commit();
    }
    
    static void list()
    {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();

        List<Library> libraries = session.createQuery("from Library as Library").list();
        
        for (Library library : libraries)
        {
        	System.out.println("library-name = " + library.getName());
        	System.out.println("library-attributes = " + library.getAttributes());
        	for (Color color : library.getColors())
        	{
        		System.out.println("\tcolor-name = " + color.getName());
        		System.out.println("\tcolor-attributes = " + color.getAttributes());
        	}
        }
        
        session.getTransaction().commit();
    }

	public static void main(String[] args)
	{
		PropertyConfigurator.configure("Logging.properties");
		
        try {
            // Create the SessionFactory from hibernate.cfg.xml
        	
        	Configuration configuration = new Configuration();
        	
        	configuration.registerTypeOverride(
        	        /*
        	         * Create a new PostgresUUIDType instance in order to register it as the default mapping for java.util.UUID.
        	         *
        	         * The other option is to manually specify to use this type for all UUID fields.
        	         * 
        	         * @see http://opensource.atlassian.com/projects/hibernate/browse/HHH-3579
        	         */
        	        new org.hibernate.type.PostgresUUIDType() {
        	            protected boolean registerUnderJavaType() {
        	                return true;
        	            }
        	        }
        	);
        	
        	sessionFactory = configuration.configure().buildSessionFactory();

        	//createAndStoreLibrary();
        	list();
    		sessionFactory.close();
        }
        catch (Throwable ex) {
            // Make sure you log the exception, as it might be swallowed
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
	}

}
