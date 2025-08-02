/**
 * 
 */
package org.hibernate.envers.test;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.MappingException;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.envers.test.integration.entityNames.oneToManyNotAudited.Car;
import org.hibernate.envers.test.integration.entityNames.oneToManyNotAudited.Person;
import org.hibernate.envers.tools.Tools;
import org.hibernate.proxy.HibernateProxy;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author Sven
 *
 */
public class ToolsTest extends AbstractSessionTest {

	private long id_car1;
	private long id_car2;
	
	private long id_pers1;
	
	protected void initMappings() throws MappingException, URISyntaxException {
		URL url = Thread.currentThread().getContextClassLoader().getResource("mappings/entityNames/oneToManyNotAudited/mappings.hbm.xml");
        config.addFile(new File(url.toURI()));
	}
	
	
    @BeforeClass(dependsOnMethods = "init")
    public void initData() {
    	
    	newSessionFactory();

        Person pers1 = new Person("Hernan", 28);
        Person pers2 = new Person("Leandro", 29);
        Person pers3 = new Person("Barba", 32);
        Person pers4 = new Person("Camomo", 15);

        List<Person > owners = new ArrayList<Person>();
        owners.add(pers1);
        owners.add(pers2);
        Car car1 = new Car(5, owners);

        //REV 1 
        getSession().getTransaction().begin();
        getSession().persist(car1);
        getSession().getTransaction().commit();
        id_pers1 = pers1.getId();
        id_car1 = car1.getId();

        owners = new ArrayList<Person>();
        owners.add(pers2);
        owners.add(pers4);
        Car car2 = new Car(27, owners);
        //REV 2
        getSession().getTransaction().begin();
        Person person1 = (Person)getSession().get("Personaje", id_pers1);
        person1.setName("Hernan David");
        person1.setAge(40);
        getSession().persist(car1);
        getSession().persist(car2);
        getSession().getTransaction().commit();
        id_car2 = car2.getId();

    }
    
    @Test
    public void testGetTargetFromProxy() {
    	
    	Car proxy = (Car) getSession().load(Car.class, id_car1);
    	
    	Car notProxy = (Car) Tools.getTargetFromProxy((SessionFactoryImplementor) getSession().getSessionFactory(), (HibernateProxy) proxy);
    	
    	proxy.getOwners().size();
    }
}


	
