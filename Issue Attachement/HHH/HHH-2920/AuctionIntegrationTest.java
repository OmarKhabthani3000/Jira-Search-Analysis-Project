package auction;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import junit.framework.TestCase;

public class AuctionIntegrationTest extends TestCase {
	private SessionFactory sessionFactory;
	private Serializable bidId;
	private Session session;
	private Transaction transaction;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		sessionFactory = new Configuration().configure().buildSessionFactory();
		session = sessionFactory.openSession();
		transaction = session.beginTransaction();
		
		
		Bid bid = new CashBid(new BigDecimal(100.0d), "euro");
		Item item = new Item("pc");
		
		bidId = session.save(bid);
		item.addBid(bid);
		session.save(item);
		
		transaction.commit();
		session.close();	
	}
	
	@SuppressWarnings("unchecked")
	public void testThatFieldsDefinedInCashBidAreNotNull() throws Exception {
		session = sessionFactory.openSession();
		transaction = session.beginTransaction();
		
		//If the following line is uncommented, the test passes.
		//session.load(CashBid.class, bidId);
		
		Query query = session.createQuery("from Item");
		List<Item> list = query.list();
		
		assertEquals(1, list.size());
		
		Set<Bid> bids = list.get(0).getBids();
		
		assertEquals(1, bids.size());
		
		Bid bid = bids.iterator().next();
		
		assertSame(CashBid.class, bid.getClass());
		
		CashBid cashBid = (CashBid) bid;
		
		assertNotNull(cashBid.getCurrency());
		
		transaction.commit();
		session.close();
		
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		sessionFactory.close();
	}
}
