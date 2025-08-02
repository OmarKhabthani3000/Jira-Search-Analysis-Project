package test.hibernate;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;

import net.sf.hibernate.Criteria;
import net.sf.hibernate.Session;
import net.sf.hibernate.SessionFactory;
import net.sf.hibernate.cfg.Configuration;
import net.sf.hibernate.expression.Expression;
import oracle.jdbc.driver.OracleDriver;

/**
 * 
 * 
 */
public class Test
{
  private String _id;
  private TestComponent _testComponent;
  

	public static void main(String[] args) throws Exception
	{
		DriverManager.registerDriver(new OracleDriver());
		Connection con =
			DriverManager.getConnection("jdbc:oracle:oci8:@j817", "j_adm", "j_adm");

		SessionFactory sf = new Configuration().configure().buildSessionFactory();
		Session s = sf.openSession(con);

		ArrayList contentIdList = new ArrayList(1);
		contentIdList.add("e6656575d7c572592316a2d41bbc4f0a68a7140f");

		Criteria c = s.createCriteria(Test.class);
		c.add(Expression.in("id", contentIdList));
		List infoList = c.list();

	}
  
	/**
	 * @return Returns the id.
	 */
	public String getId()
	{
		return _id;
	}

	/**
	 * @param id The id to set.
	 */
	public void setId(String id)
	{
		_id = id;
	}

	/**
	 * @return Returns the testComponent.
	 */
	public TestComponent getTestComponent()
	{
		return _testComponent;
	}

	/**
	 * @param testComponent The testComponent to set.
	 */
	public void setTestComponent(TestComponent testComponent)
	{
		_testComponent = testComponent;
	}

}
