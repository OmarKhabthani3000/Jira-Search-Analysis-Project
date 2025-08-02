package test;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import model.HibernateTestDao;

public class HibernateTest
{
	public static void main(String[] args)
	{
		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:/applicationContext.xml");

		HibernateTestDao dao = applicationContext.getBean(HibernateTestDao.class);

		dao.test();;

		applicationContext.close();
	}
}
