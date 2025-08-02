//$Id: CompositeUserTypeTest.java,v 1.5 2005/11/29 20:04:17 steveebersole Exp $
package org.hibernate.test.cut;

import java.awt.PageAttributes.OriginType;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.Properties;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.dialect.HSQLDialect;
import org.hibernate.dialect.Oracle9Dialect;
import org.hibernate.test.TestCase;

/**
 * @author Gavin King
 */
public class CompositeUserTypeTest extends TestCase {
	
	public CompositeUserTypeTest(String str) {
		super(str);
		Properties cfg = System.getProperties();
		cfg.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLInnoDBDialect");
		cfg.setProperty("hibernate.connection.driver_class", "com.mysql.jdbc.Driver");
		cfg.setProperty("hibernate.connection.url", "jdbc:mysql://localhost/h3test");
		cfg.setProperty("hibernate.connection.username", "root");
		cfg.setProperty("hibernate.connection.password", "a");
		cfg.setProperty("hibernate.show_sql", "true");
		
	}
	
	public void testCompositeUserType() {
		Session s = openSession();
		org.hibernate.Transaction t = s.beginTransaction();
		
		Transaction tran = new Transaction();
		tran.setDescription("a small transaction");
		MonetoryAmount oneHalfUSD = new MonetoryAmount( new BigDecimal(1.5), Currency.getInstance("USD") );
		tran.setValue( oneHalfUSD );
		s.persist(tran);
		
		Query q = s.createQuery("from Transaction tran where tran.value = :value");
		q.setParameter("value", new MonetoryAmount( new BigDecimal(1.5), Currency.getInstance("USD") ), Hibernate.custom(MonetoryAmountUserType.class) );
		q.list();
		
		List result = s.createQuery("from Transaction tran where tran.value.amount > 1.0 and tran.value.currency = 'USD'").list();
		assertEquals( result.size(), 1 );
		tran.getValue().setCurrency( Currency.getInstance("AUD") );
		result = s.createQuery("from Transaction tran where tran.value.amount > 1.0 and tran.value.currency = 'AUD'").list();
		assertEquals( result.size(), 1 );
		
		if ( !(getDialect() instanceof HSQLDialect) && ! (getDialect() instanceof Oracle9Dialect) ) {
		
			result = s.createQuery("from Transaction txn where txn.value = (1.5, 'AUD')").list();
			assertEquals( result.size(), 1 );
			result = s.createQuery("from Transaction where value = (1.5, 'AUD')").list();
			assertEquals( result.size(), 1 );
			
		}
		
		s.delete(tran);
		t.commit();
		s.close();
	}

	
	protected String[] getMappings() {
		return new String[] { "cut/types.hbm.xml", "cut/Transaction.hbm.xml" };
	}

	public static Test suite() {
		return new TestSuite(CompositeUserTypeTest.class);
	}

}

