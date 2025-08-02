/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * Copyright (c) 2010, Red Hat Inc. or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Inc.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package my.test.own.hibernate_xml_1_N_bidir_ehcache;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.PersistenceContext;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.internal.SessionImpl;
import org.hibernate.stat.Statistics;
/**
 * Illustrates use of Hibernate native APIs.
 *
 * @author Steve Ebersole
 */
public class Main{
	private Configuration configuration;
	private SessionFactory sessionFactory;
	private StandardServiceRegistry serviceRegistry;
	private Session session;
	private Statistics stats;

	public static void main(String[]args){

		new Main();

	}
	public Main(){
	try{
		setUp();
		testBasicUsage();
		tearDown();
	}catch(Exception e){
		e.printStackTrace();
	}
	}

	protected void setUp() throws Exception {
             
        	configuration=new Configuration().configure();
    		serviceRegistry=new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
    		sessionFactory=configuration.buildSessionFactory(serviceRegistry);
    		
    		stats = sessionFactory.getStatistics();
            System.out.println("Stats enabled="+stats.isStatisticsEnabled());
            stats.setStatisticsEnabled(true);
            System.out.println("Stats enabled="+stats.isStatisticsEnabled());
        }


	protected void tearDown() throws Exception {
		if ( sessionFactory != null ) {
			sessionFactory.close();
		}
	}

	public void testBasicUsage() {

		

			session = sessionFactory.openSession();
			org.hibernate.engine.spi.PersistenceContext context=((SessionImpl)session).getPersistenceContext();
			Transaction tx=session.beginTransaction();

		  	printStats(stats);
		  
			Employee employee=new Employee(1l,"Foo1",1.00);
			Employee employee2=new Employee(2l,"Foo2",2.00);
			Address address=new Address(12l,"foostreet", "12 foo", "FooCity12");
			Address address3=new Address(34l,"foostreet", "34 foo", "FooCity34");

			employee.setAddress(address3);
			employee2.setAddress(address3);
			address.getEmployees().add(employee);
			address.getEmployees().add(employee2);
			
			session.save(address);
			session.save(address3);
			session.save(employee);
			session.save(employee2);
			
			
		  	printStats(stats);
		 
			tx.commit();
			printStats(stats);
			session.close();
			
		  	address.setCity("FooCity12B");
		  	employee.setName("Foo1B");
		
			
	        
			session = sessionFactory.openSession();
			tx=session.beginTransaction();
		  	
	        employee= (Employee) session.get(Employee.class, 1l);
	        address=employee.getAddress();
	        String city=address.getCity();
	        employee= (Employee) session.get(Employee.class, 2l);
	        address=employee.getAddress();
	        city=address.getCity();


	        printStats( stats);	        
	        tx.commit();
	        printStats( stats);
	        session.close();

	        
	}
    private static void printStats(Statistics stats) {
        System.out.println("*****");
        System.out.println("entity delete count="+ stats.getEntityDeleteCount());
        System.out.println("entity insert count="+ stats.getEntityInsertCount());
        System.out.println("entity load count="+ stats.getEntityLoadCount());
        System.out.println("entity fetch count="+ stats.getEntityFetchCount());
        System.out.println("entity update count="+ stats.getEntityUpdateCount());
        
        System.out.println("second level miss count="+ stats.getSecondLevelCacheMissCount());
        System.out.println("second level hit count="+ stats.getSecondLevelCacheHitCount());
        System.out.println("second level put count="+ stats.getSecondLevelCachePutCount());
        
    }
}
