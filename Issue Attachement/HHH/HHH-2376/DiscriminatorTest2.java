package org.hibernate.test.discriminator;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.test.TestCase;

public class DiscriminatorTest2 extends TestCase {

  public DiscriminatorTest2(String str) {
    super(str);
  }

  public void testQueryWithoutPackage() {
    Session s = openSession();
    Transaction t = s.beginTransaction();
    Set people = setup(s);

    assertEquals( s.createQuery("from Person p where p.class = Person").list().size(), 1 );

    cleanup(s, people);
    t.commit();
    s.close();
  }

  public void testQueryWithPackage() {
    Session s = openSession();
    Transaction t = s.beginTransaction();
    Set people = setup(s);

    assertEquals( s.createQuery("from Person p where p.class = org.hibernate.test.discriminator.Person").list().size(), 1 );

    cleanup(s, people);
    t.commit();
    s.close();
  }
  
  private void cleanup(Session s, Set people) {
    for (Iterator iter = people.iterator(); iter.hasNext();) {
      Person p = (Person) iter.next();
      s.delete(p);
    }    
        
	assertTrue( s.createQuery("from Person").list().isEmpty() );
  }

  private Set setup(Session s) {
    Employee mark = new Employee();
    mark.setName("Mark");
    mark.setTitle("internal sales");
    mark.setSex('M');
    mark.setAddress("buckhead");
    mark.setZip("30305");
    mark.setCountry("USA");

    Customer joe = new Customer();
    joe.setName("Joe");
    joe.setAddress("San Francisco");
    joe.setZip("XXXXX");
    joe.setCountry("USA");
    joe.setComments("Very demanding");
    joe.setSex('M');
    joe.setSalesperson(mark);

    Person yomomma = new Person();
    yomomma.setName("mum");
    yomomma.setSex('F');

    s.save(yomomma);
    s.save(mark);
    s.save(joe);
    
    Set people = new HashSet();
    people.add(yomomma);
    people.add(mark);
    people.add(joe);
    
    return people;
  }

  protected String[] getMappings() {
    return new String[] { "discriminator/Person.hbm.xml" };
  }

  public static Test suite() {
    return new TestSuite(DiscriminatorTest2.class);
  }

}

