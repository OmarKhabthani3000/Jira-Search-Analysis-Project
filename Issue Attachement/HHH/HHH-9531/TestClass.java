import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * Unit test to demonstrate the issue with criteria.scroll() when a using a collection with
 * @OneToMany and FetchType.EAGER)
 *
 */
public class TestClass {
	private static final Properties properties = new Properties();
	private Session session;
	private SessionFactory sessionFactory;
	
	@Before
	public void prepareDB(){
		try {
			properties.load(TestClass.class.getResourceAsStream("db.properties"));
		}
		catch (IOException e) {
			e.printStackTrace();
			fail();
		}
		Configuration configuration = new Configuration().setProperties(properties).addAnnotatedClass(ModelA.class).addAnnotatedClass(ModelB.class);
	    StandardServiceRegistryBuilder ssrb = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties());
	    sessionFactory = configuration.buildSessionFactory(ssrb.build());
	    session = sessionFactory.openSession();
	    
	    String deleteB = "DELETE FROM b";
	    String deleteA = "DELETE FROM a";
	    
	    String insertA = "INSERT INTO a VALUES (1, 'tatooine')";
	    String insertB = "INSERT INTO b VALUES "+
	    		"(1, 'Tusken Raiders', 1),"+
	    		"(2, 'Jawa', 1),"+
	    		"(3, 'Jabba the Hutt', 1)";
	    
	    Transaction t = session.beginTransaction();
	    session.createSQLQuery(deleteB).executeUpdate();
	    session.createSQLQuery(deleteA).executeUpdate();
	    
	    session.createSQLQuery(insertA).executeUpdate();
	    session.createSQLQuery(insertB).executeUpdate();
	    t.commit();
	}

	@After
	public void release(){
		session.close();
	    sessionFactory.close();
	}

	@Test
	public void usingCriteriaList(){
		System.out.println("--- Using Criteria list() ---");
		Criteria criteria = session.createCriteria(ModelA.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		List<ModelA> listOfModelA = criteria.list();
		for(ModelA currModelA : listOfModelA){
	    	System.out.println(currModelA);
	    	assertListOfModelBContent(currModelA.getListofB());
	    }
	}
	
	@Test
	public void usingCriteriaScroll(){
		System.out.println("--- Using Criteria scroll() ---");
		Criteria criteria = session.createCriteria(ModelA.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		ScrollableResults sr = criteria.scroll();
	    while(sr.next()){
	    	ModelA currModelA = (ModelA)sr.get()[0];
	    	System.out.println(currModelA);
	    	List<ModelB> listOfModelB = currModelA.getListofB();
	    	for(ModelB currModelB : listOfModelB){
	    		System.out.println(" " + currModelB);
	    		assertListOfModelBContent(currModelA.getListofB());
	    	}
	    }
	}
	
	@Test
	public void usingQueryScroll(){
		System.out.println("--- Using Query scroll() ---");
	    Query query = session.createQuery(" from ModelA").setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		ScrollableResults sr = query.scroll();
	    while(sr.next()){
	    	ModelA currModelA = (ModelA)sr.get()[0];
	    	System.out.println(currModelA);
	    	List<ModelB> listOfModelB = currModelA.getListofB();
	    	for(ModelB currModelB : listOfModelB){
	    		System.out.println(" " + currModelB);
	    		assertListOfModelBContent(currModelA.getListofB());
	    	}
	    }
	}
	
	private void assertListOfModelBContent(List<ModelB> listOfModelB){
		assertEquals(3, listOfModelB.size());
		List<String> listOfName = new ArrayList<String>(3);
		for(ModelB currModelB : listOfModelB){
			listOfName.add(currModelB.getName());
		}
		assertTrue(listOfName.contains("Tusken Raiders"));
		assertTrue(listOfName.contains("Jawa"));
		assertTrue(listOfName.contains("Jabba the Hutt"));
	}

}
