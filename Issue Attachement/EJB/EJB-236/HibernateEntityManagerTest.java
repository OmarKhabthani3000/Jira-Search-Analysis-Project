package com.arvato.hibernate.entities.test;

import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.hibernate.HibernateException;

import com.arvato.hibernate.EntityManagerFactoryConfiguration;
import com.arvato.hibernate.entities.Tras001;
import com.arvato.hibernate.entities.Tras001Home;
import com.arvato.hibernate.entities.Tras001Id;


public class HibernateEntityManagerTest {
	public static void main(String[] args) {
		HibernateEntityManagerTest.load();
	}

 
	
    public static void load(){
    	

		System.out.println("start");

		EntityTransaction trans = null;
		try {
			EntityManager entityManager = EntityManagerFactoryConfiguration.getCurrentEntityManager();
			trans = entityManager.getTransaction();
			System.err.println("trans status before:" + trans.isActive() );
			trans.begin();
			System.err.println("trans status after:" + trans.isActive() );
			
			Tras001Id productId = new Tras001Id();
			productId.setProdNo("260600");
			productId.setClientKey("0000");
			
			Tras001 product = new Tras001();
			product.setId( productId );
						
			Tras001Home productHome = new Tras001Home();
			Tras001 productResult = productHome.findById( productId );
			
			if( trans.isActive() ) 
				trans.commit(); //TODO: close causes problems
				
			EntityManagerFactoryConfiguration.closeCurrentEntityManager();

		} catch (Exception onfe) {
			onfe.printStackTrace();
			trans.rollback();			
		} finally {
			try {
				EntityManagerFactoryConfiguration.closeCurrentEntityManager();
			} catch (HibernateException e) {
				e.printStackTrace();
			}
		}
		System.out.println("finished");
		
	}

}
