import java.math.BigDecimal;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.test.TestCase;


public class MyEntityTest extends TestCase {

	public MyEntityTest(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}
	public void testSave(){
	       MyEntity myEntity = new MyEntity();
	        myEntity.setCost(new BigDecimal("123.12345"));
	        Session session=openSession();
	        session.save(myEntity);
	        session.flush();
	        session.clear();

	        List<MyEntity> results = session.createCriteria(MyEntity.class).list();
	        if (results.size() != 1) {
	            throw new IllegalStateException("Expected 1 result");
	        }
	}

	@Override
	protected String[] getMappings() {
		// TODO Auto-generated method stub
		return new String[]{"MyEntity.hbm.xml"};
	}

}
