//$Id: DynamicMapOneToOneTest.java 10977 2006-12-12 23:28:04Z steve.ebersole@jboss.com $
package org.hibernate.test.onetoone;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import org.junit.Test;

/**
 * @author Gavin King
 */
public class OneToOneTest
{
    
    @Test
    public void testOneToManyPropertyRef()
    {
        Person person = new Person();
        person.setId(new BigDecimal(1));
        person.setName("hyj");
        
        Address address = new Address();
        address.setId(new BigDecimal(1));
        address.setName("mar");
        Address address2 = new Address();
        address2.setId(new BigDecimal(2));
        address2.setName("sun");
        
        List<Address> list = new ArrayList<Address>(2);
        list.add(address);
        list.add(address2);
        person.setAddress(list);
        
        Session s = openSession();
        s.beginTransaction();
        s.save("Person", person);
        s.getTransaction().commit();
        s.close();
    }
    
    /**
     * TODO 添加方法注释
     * @return
     */
    private Session openSession()
    {
        Configuration configuration = new Configuration();
        configuration.configure("/hibernate.cfg.xml");
        
        org.hibernate.SessionFactory sessionFactory = configuration.buildSessionFactory();
        return sessionFactory.openSession();
    }
    
}
