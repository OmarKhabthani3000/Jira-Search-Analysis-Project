package org.foo;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.foo.model.AuctionItem;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hsqldb.Server;

public class BulkDeleteTestCase extends TestCase {
	private static Log log = LogFactory.getLog(BulkDeleteTestCase.class);
	protected Configuration conf;
	protected SessionFactory sessionFactory;
	private static Server server;
	private static String SERVER_PROPS = "database.0=mem:test";
    protected static final String CONF_PATH = "org/foo/model/Test.cfg.xml";
	
    protected void setUp() {
        server = new Server();
        server.putPropertiesFromString(SERVER_PROPS);
        server.start();
		conf = new Configuration().configure(CONF_PATH);
		sessionFactory = conf.buildSessionFactory();
    	SchemaExport schemaExport = new SchemaExport(conf);
    	schemaExport.create(true, true);
    }
    
	protected void tearDown() throws Exception {
		sessionFactory.close();
		sessionFactory = null;
		server.stop();
		server = null;
		super.tearDown();
	}

	public void testBulkDelete(){
		Session session = sessionFactory.openSession();
		Transaction t = session.beginTransaction();
		
		String hql = " DELETE FROM " + AuctionItem.class.getName() + " WHERE seller.id = 1 ";
		Query queryWithSeller = session.createQuery(hql);
		String queryWithSellerString = queryWithSeller.getQueryString();
		
		hql = " DELETE FROM " + AuctionItem.class.getName() + " WHERE id = 1 ";
		Query queryWithOutSeller = session.createQuery(hql);
		String queryWithOutSellerString = queryWithOutSeller.getQueryString();
		
		// these show up the same in the logs.
		log.info(" look @ this ");
		queryWithSeller.executeUpdate();
		queryWithOutSeller.executeUpdate();
		log.info(" did you see that ? ");
		
		t.commit();
		// and this will pass ... 
		assertFalse(queryWithOutSellerString.equals(queryWithSellerString));
		session.close();
	}
}
