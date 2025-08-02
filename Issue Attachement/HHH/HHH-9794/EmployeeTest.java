package test.hibernate.bug1;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.junit.Before;
import org.junit.Test;

public class EmployeeTest {

	private static final String Query_Employees_All = "from Employee";
	private static SessionFactory sessionFactory;
	private static ServiceRegistry serviceRegistry;
	private static Configuration config;
	private static Session session;

	private static final String STRVALUE = "Employee Number 1";
	private static final String COMMA = ",";

	@Before
	public void setUp() throws Exception {
		String strRecord = null;

		System.out.println("Connect to sql database");
		config = new Configuration().configure("hibernate.cfg.xml");
		config.addAnnotatedClass(Employee.class);

		serviceRegistry = new StandardServiceRegistryBuilder().
				applySettings(config.getProperties()).build();
		sessionFactory = config.buildSessionFactory(serviceRegistry);
		session = sessionFactory.getCurrentSession();

		// create the table
		new SchemaExport(config).create(true, true);

		System.out.println("Create one record in the sql database");
		// create one Record
		session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();

		Employee employee = new Employee();
		employee.setEmployeeName(STRVALUE);
		session.save(employee);

		tx.commit();

		strRecord = "Record ID: " + employee.getEmployeeID()
				+ " --> field value is: " + employee.getEmployeeName();

		System.out.println("One Database record created in SQL database:");
		System.out.println(strRecord);

	}

	@Test
	public void testUpdateRecord() throws Exception {

		// update the record
		System.out.println("Update a record in the sql database.");
		Query query;
		List list;

		session = sessionFactory.openSession();

		// here is the bug ->
		// the word "Employee" in the comment string will be
		// replaced with "test.hibernate.bug1.Employee"
		// if a comma is preceding the word "Employee" is
		// replaced with the employee class name
		String Query_Update_EmployeeName_ByID = "UPDATE Employee employee set employee.employeeName = '"
				+ COMMA + STRVALUE + "'" + " WHERE employeeID = '1'";

		// update Employee employee set employee.empID='Test'
		Transaction tx = session.beginTransaction();
		query = session.createQuery(Query_Update_EmployeeName_ByID);
		query.executeUpdate();

		tx.commit();

		if (session.isOpen()) {
			session.close();
		}

		// READ Records() after update;
		System.out
				.println("Read the records in sql database after the update");

		session = sessionFactory.openSession();

		query = session.createQuery(Query_Employees_All);
		list = query.list();

		for (int j = 0; j < list.size(); j++) {
			Employee employee = (Employee) list.get(j);

			System.out.println("Record ID: " + employee.getEmployeeID()
					+ " --> field value should be: " + COMMA + STRVALUE);
			System.out.println("Record ID: " + employee.getEmployeeID()
					+ " --> but field value is: " + employee.getEmployeeName());

			assertEquals(COMMA + STRVALUE, employee.getEmployeeName());
		}

	}
}
