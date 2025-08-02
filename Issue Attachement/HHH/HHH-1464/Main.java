//$Id: Main.java,v 1.6 2005/07/04 03:18:34 oneovthafew Exp $
package org.hibernate.auction;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.hibernate.FetchMode;
import org.hibernate.FlushMode;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.MatchMode;


/**
 * Demonstrate a bug in Query.getReturnAliases
 * just drop in place of Main.java in the
 * examples in the hibernate distribution
 *
 */
public class Main {

	private SessionFactory factory;


	/**
	 * Demonstrates HQL with runtime fetch strategy
	 */
	public void viewAllAuctionsSlow() throws Exception {
		System.out.println("Viewing all auction item objects");

		Session s = factory.openSession();
		Transaction tx=null;
		try {
			s.setFlushMode(FlushMode.NEVER); //entirely optional!!
			tx = s.beginTransaction();

			Query q = s.createQuery(
				"from AuctionItem item "
				+ "left join fetch item.bids bid left join fetch bid.bidder "
				+ "order by item.ends desc"
				);
			q.getReturnAliases();
			tx.commit();
		}
		catch (Exception e) {
			if (tx!=null) tx.rollback();
			throw e;
		}
		finally {
			s.close();
		}
	}


	/**
	 * Demonstrates transitive persistence
	 */
	public void createTestAuctions() throws Exception {
		System.out.println("Setting up some test data");

		Session s = factory.openSession();
		Transaction tx = s.beginTransaction();

		User seller = new User();
		seller.setUserName("xam");
		seller.setName( new Name("Max", new Character('R'), "Andersen") );
		seller.setEmail("max@hibernate.org");
		seller.setPassword("******");
		seller.setAuctions( new ArrayList() );
		s.save(seller);
		User bidder1 = new User();
		bidder1.setUserName("1E1");
		bidder1.setName( new Name( "Gavin", new Character('A'), "King") );
		bidder1.setEmail("gavin@hibernate.org");
		bidder1.setPassword("******");
		bidder1.setBids( new ArrayList() );
		s.save(bidder1);
		User bidder2 = new User();
		bidder2.setUserName("steve");
		bidder2.setName( new Name("Steve", null, "Ebersole") );
		bidder2.setEmail("steve@hibernate.org");
		bidder2.setPassword("******");
		bidder2.setBids( new ArrayList() );
		s.save(bidder2);

		for ( int i=0; i<3; i++ ) {
			AuctionItem item = new AuctionItem();
			item.setShortDescription("Auction " + i);
			item.setDescription("the auction item number " + i);
			item.setEnds( new Date() );
			item.setBids( new ArrayList() );
			item.setSeller(seller);
			item.setCondition(i*3 + 2);
			for ( int j=0; j<i; j++ ) {

				Bid bid = new Bid();
				bid.setBidder(bidder1);
				bid.setAmount(j);
				bid.setDatetime( new Date() );
				bid.setItem(item);
				item.getBids().add(bid);
				bidder1.getBids().add(bid);

				Bid bid2 = new Bid();
				bid2.setBidder(bidder2);
				bid2.setAmount( j + 0.5f);
				bid2.setDatetime( new Date() );
				bid2.setItem(item);
				item.getBids().add(bid2);
				bidder2.getBids().add(bid2);
			}
			seller.getAuctions().add(item);
			mainItem = item;
		}
		mainBidder = bidder2;
		mainSeller = seller;

		BuyNow buyNow = new BuyNow();
		buyNow.setAmount(1.2f);
		buyNow.setDatetime( new Date() );
		buyNow.setBidder(mainBidder);
		buyNow.setItem(mainItem);
		mainBidder.getBids().add(buyNow);
		mainItem.getBids().add(buyNow);

		tx.commit();
		s.close();
	}

	static AuctionItem mainItem;
	static User mainBidder;
	static User mainSeller;

	public static void main(String[] args) throws Exception {

		final Main test = new Main();

		Configuration cfg = new Configuration()
			.addClass(AuctionItem.class)
			.addClass(Bid.class)
			.addClass(User.class)
			.setProperty(Environment.HBM2DDL_AUTO, "create");
		//cfg.setProperty("hibernate.show_sql", "true");

		test.factory = cfg.buildSessionFactory();

		test.createTestAuctions();
		test.viewAllAuctionsSlow();

		test.factory.close();

	}
}
