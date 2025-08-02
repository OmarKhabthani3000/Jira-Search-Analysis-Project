package org.hibernate.test.joinpropertyref;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Transaction;
import org.hibernate.classic.Session;
import org.hibernate.test.TestCase;

public abstract class AbstractJoinTest extends TestCase {

	public AbstractJoinTest(String name) {
		super(name);
	}

	public final void testSave () throws SQLException {
		Person person = newPerson("King", "Address");
		save(person);
		
		Session s = openSession();
		Connection c = s.connection();
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		try {
			statement = c.prepareStatement("select count(*) from address where address_id = ?");
			setJoinedTableKeyProperty(person, statement);
			resultSet = statement.executeQuery();
			assertTrue(resultSet.next());
		} finally {
			if (resultSet != null)
				try {
					resultSet.close();
				} catch (SQLException e1) {
				}
			try {
				statement.close();
			} catch (SQLException e) {
			}
			s.close();
		}
		
		delete(person);
	}

	protected abstract void setJoinedTableKeyProperty(Person person, PreparedStatement statement) throws SQLException;

	private void delete(Person person) {
		Session s = openSession();
		Transaction t = s.beginTransaction();
		s.delete(person);
		s.flush();
		t.commit();
		s.close();
	}
	
	public final void testGet () {
		Serializable id = save(newPerson("p1","a1"));
		Session s = openSession();
		Person loaded = (Person) s.get(Person.class, id);
		s.close();

		delete(loaded);
	}
	
	public final void testFindByHQL () {
		save(newPerson("p1","a1"));
		save(newPerson("p2","a2"));
		save(newPerson("p3","b1"));
		
		Session s = openSession();
		assertEquals(1,s.createQuery("from Person where address = ?").setString(0,"a1").list().size());
		assertEquals(2,s.createQuery("from Person where address like ?").setString(0,"a%").list().size());
		List all = s.createQuery("from Person").list();
		assertEquals(3,all.size());
		
		for (Iterator i = all.iterator(); i.hasNext();) {
			delete((Person) i.next());
		}
	}
	
	public final void testBatchDelete () {
		save(newPerson("p1","a1"));
		save(newPerson("p2","a2"));
		save(newPerson("p3","b1"));
		
		Session s = openSession();
		Transaction t = s.beginTransaction();
		assertEquals(3,s.createQuery("delete from Person").executeUpdate());
		t.commit();
		s.close();
	}
	
	public final void testBatchUpdate () {
		save(newPerson("p1","a1"));
		save(newPerson("p2","a2"));
		save(newPerson("p3","b1"));
		
		Session s = openSession();
		Transaction t = s.beginTransaction();
		assertEquals(3,s.createQuery("update Person p set address = 'home'").executeUpdate());
		t.commit();
		s.close();
	}

	private Serializable save(Person person) {
		Session s = openSession();
		Transaction t = s.beginTransaction();
		Serializable id = s.save(person);
		t.commit();
		s.close();
		return id;
	}

	private Person newPerson(String name, String address) {
		Person person = new Person();
		person.setName(name);
		person.setSex('m');
		person.setCountry("Country");
		person.setAddress(address);
		person.setZip("zip");
		return person;
	}

}
