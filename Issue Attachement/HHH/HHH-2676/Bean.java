import java.util.Properties;

import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;


public class Bean {
	public Bean(){
		
	}
	
	private String id; 
	
	private long value;

	public long getValue() {
		return value;
	}

	public void setValue(long value) {
		this.value = value;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}

	public static void main(String[] args) {
		
		// Setup config
		Configuration config = new Configuration();
		config.addClass(Bean.class);
		Properties props = new Properties();
		props.setProperty("hibernate.connection.driver_class", "oracle.jdbc.driver.OracleDriver");
		props.setProperty("hibernate.connection.url", "jdbc:oracle:thin:@localhost:1521:XE");
		props.setProperty("hibernate.connection.username", "user");
		props.setProperty("hibernate.connection.password", "pass");
		props.setProperty("hibernate.current_session_context_class", "thread");
		props.setProperty("hibernate.dialect", "org.hibernate.dialect.Oracle9Dialect");
		props.setProperty("hibernate.hbm2ddl.auto", "create");
		config.setProperties(props);
		
		// Open session
		SessionFactory sFactory = config.buildSessionFactory();
		Session session = sFactory.openSession();
		
		// Create query
		Query q = session.createQuery("select b.value from Bean b");
		
		// Set lock mode on query 
		q.setLockMode("b", LockMode.UPGRADE);
		
		// Error occurs here:
		//    Exception in thread "main" java.lang.IllegalArgumentException: could not locate alias to apply lock mode : b
		//		at org.hibernate.loader.hql.QueryLoader.applyLocks(QueryLoader.java:297)
		//		at org.hibernate.loader.Loader.preprocessSQL(Loader.java:201)
		//		at org.hibernate.loader.Loader.prepareQueryStatement(Loader.java:1538)
		//		at org.hibernate.loader.Loader.doQuery(Loader.java:673)
		//		at org.hibernate.loader.Loader.doQueryAndInitializeNonLazyCollections(Loader.java:236)
		//		at org.hibernate.loader.Loader.doList(Loader.java:2220)
		//		at org.hibernate.loader.Loader.listIgnoreQueryCache(Loader.java:2104)
		//		at org.hibernate.loader.Loader.list(Loader.java:2099)
		//		at org.hibernate.loader.hql.QueryLoader.list(QueryLoader.java:378)
		//		at org.hibernate.hql.ast.QueryTranslatorImpl.list(QueryTranslatorImpl.java:338)
		//		at org.hibernate.engine.query.HQLQueryPlan.performList(HQLQueryPlan.java:172)
		//		at org.hibernate.impl.SessionImpl.list(SessionImpl.java:1121)
		//		at org.hibernate.impl.QueryImpl.list(QueryImpl.java:79)
		//		at Bean.main(Bean.java:56)
		q.list();
	}
}
