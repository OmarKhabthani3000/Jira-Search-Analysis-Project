package org.hibernate.test.dom4j;

import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.hibernate.EntityMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Example;
import org.hibernate.test.TestCase;

import com.ips.dto.RolDto;
import com.ips.dto.UsuarioDto;

/**
 * @author Paco Hernández
 */
public class Dom4jManyToOneTest extends TestCase {

	public Dom4jManyToOneTest(String str) {
		super( str );
	}

	public void testDom4jManyToOne() throws Exception {

		Session s = openSession();
		Transaction t = s.beginTransaction();

		CarType carType = new CarType();
		carType.setTypeName("Type 1");
		s.save(carType);

		Car car1 = new Car();
		car1.setCarType(carType);
		car1.setModel("Model 1");
		s.save(car1);
		
		Car car2 = new Car();
		car2.setCarType(carType);
		car2.setModel("Model 2");
		s.save(car2);
		
		t.commit();
		s.close();

		s = openSession();
		Session dom4jSession = s.getSession( EntityMode.DOM4J );
		t = s.beginTransaction();

		Query q = dom4jSession.createQuery( "from Car" );
		List list = q.list();

		String[] expectedResults = new String[] {
				"<car id=\"1\"><model>Model 1</model><carType><id id=\"1\"><typeName>Type 1</typeName></id></carType></car>",
				"<car id=\"2\"><model>Model 2</model><carType><id id=\"1\"><typeName>Type 1</typeName></id></carType></car>"
		};
		
		String[] results = new String[2];
		
		for (int i = 0; i < list.size(); i++) {
			Element element = (Element) list.get(i);

			//System.out.println(element.asXML());
			assertTrue(element.asXML().equals(expectedResults[i]));
		}
		
		t.commit();
		s.close();
	}

	protected String[] getMappings() {
		return new String[] { "dom4j/Car.hbm.xml" };
	}

	public static Test suite() {
		return new TestSuite( Dom4jManyToOneTest.class );
	}
}
