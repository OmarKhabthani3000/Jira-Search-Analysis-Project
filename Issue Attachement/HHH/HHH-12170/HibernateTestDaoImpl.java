package model;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Repository
public class HibernateTestDaoImpl implements HibernateTestDao
{
	@Autowired
  private SessionFactory sessionFactory;

	public HibernateTestDaoImpl()
	{
		System.out.println("DAO created");
	}

	@Transactional(readOnly = false)
	@Override
	public void test()
	{
		Person person = new Person();
		person.setName("Joe");
		person.setAge(10);

		Long id = (Long)sessionFactory.getCurrentSession().save(person);

		sessionFactory.getCurrentSession().flush();

		person = sessionFactory.getCurrentSession().get(Person.class, id);

		sessionFactory.getCurrentSession().clear();

		person.setAge(11);

		sessionFactory.getCurrentSession().merge(person);
		sessionFactory.getCurrentSession().flush();

//		throw new RuntimeException();
	}

}
