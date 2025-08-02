//$Id: OptimisticLockTest.java,v 1.1 2004/08/21 08:43:19 oneovthafew Exp $
package org.hibernate.test.optlock;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.StaleObjectStateException;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.test.TestCase;

/**
 * @author Gavin King
 */
public class OptimisticLockTest extends TestCase {
	//private static Log log = LogFactory.getLog(OptimisticLockTest.class);

	public OptimisticLockTest(String str) {
		super(str);
	}
	
	public void testOptimisticLockDirty() {
		Session s = openSession();
		Transaction t = s.beginTransaction();
		Document doc = new Document();
		doc.setTitle("Hibernate in Action");
		doc.setAuthor("Bauer et al");
		doc.setSummary("Very boring book about persistence");
		doc.setText("blah blah yada yada yada");
		s.save(doc);
		s.flush();
		doc.setSummary("A modern classic");
		s.flush();
		s.delete(doc);
		t.commit();
		s.close();
	}	
	
	public void testLockingClash() {
		Session s1 = openSession();
		Transaction t1 = s1.beginTransaction();
		Document doc1 = new Document();

		// Create a book...
		doc1.setTitle("Ulysses");
		doc1.setAuthor("James Joyce");
		doc1.setSummary( "A drunken ramble" );
		doc1.setText("blah blah yada yada yada");
		s1.save(doc1);
		s1.flush();
		t1.commit();
		s1.close();
		
		// Update it in two separate sessions...
		Session s2 = openSession();
		Transaction t2 = s2.beginTransaction();
		Document doc2 = lookupDoc( "Ulysses", s2 );
		
		Session s3 = openSession();
		Transaction t3 = s3.beginTransaction();
		Document doc3 = lookupDoc( "Ulysses", s3 );
		
		doc2.setSummary("A modern classic");
		s2.save( doc2 );
		s2.flush();
		t2.commit();
		s2.close();
		
		doc3.setSummary( "" );
		try {
			s3.save( doc3 );
			//log.info( "save worked, about to flush the session..." );
			s3.flush();
			//log.info( "...session flush worked." );
			
			// If we get to here without an exception being thrown, fail the 
			// test...
			fail( "A StaleObjectStateException should have been thrown, " +
				  "becuase of the optimistic locking clash with session 2" );	
			
		} catch ( StaleObjectStateException sose ) {
			// Expected behaviour - test test should pass...
		}
		s3.close();
	}

	
	/**
	 * Looks up a Document by title.
	 *  
	 * @param string
	 * @param s2
	 * @return
	 */
	private Document lookupDoc( String title, Session ses ) {
		Query q =
		    ses.createQuery( "select doc from Document as doc " +
		    		         "where doc.title = :title" );
		
		q.setString( "title", title );
		
		return (Document) q.uniqueResult();
	}

	protected String[] getMappings() {
		return new String[] { "optlock/Document.hbm.xml" };
	}

	public static Test suite() {
		return new TestSuite(OptimisticLockTest.class);
	}

}

