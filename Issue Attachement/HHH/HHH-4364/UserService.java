/**
 *
 */
package edu.upmc.ccweb.dosimetry.hibtest;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;


public class UserService {

	private SessionFactory sessionFactory;
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}


	@SuppressWarnings("unchecked")
	public List<User> findByLoginName(String loginName)  {
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		Query query = session.getNamedQuery("User.findByLoginName");
		query.setParameter("loginName", loginName);
		return query.list();
	}


	@SuppressWarnings("unchecked")
	public List<User> findByFirstName(String firstName)  {
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		Query query = session.getNamedQuery("User.findByFirstName");
		query.setParameter("firstName", firstName);
		return query.list();
	}
}
