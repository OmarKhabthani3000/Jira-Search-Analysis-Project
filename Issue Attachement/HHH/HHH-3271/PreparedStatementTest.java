/**
* This is an example of usage of the prepared statement. It may not work properly
*/
import java.sql.PreparedStatement;
import java.sql.SQLException;

import junit.framework.TestCase;

public class PreparedStatementTest extends TestCase {
	
	Account account;
	String acctId = "PSReuse1";
	String updateAcct = "update acct_dtls a set a.counter = a.counter+1 where a.acct_no=?";

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}
	
	/**
	 * this should pass as we have not closed the prepared statement.
	 */
	public void testPreparedStatementReuse() {
		PreparedStatement ps;
		Session session = DataAccessManager.getManager().fetchCurrentSession();
		try {
			Transaction trx = session.beginTransaction();
			ps = session.connection().prepareStatement(updateAcct);

			//first batch
			ps.setString(1, acctId);
			ps.addBatch();
			ps.setString(1, acctId);
			ps.addBatch();
			ps.executeBatch();
			trx.commit();
	
			// this is written to reopen the PS which should not be the case.
			trx = session.beginTransaction();

			//second batch should run since ps not closed
			ps.setString(1, acctId);
			ps.addBatch();
			ps.setString(1, acctId);
			ps.addBatch();
			ps.executeBatch();
			trx.commit();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}
	}
	
	/**
	 * this should fail
	 */
	public void testPreparedStatementReuseFail() {
		PreparedStatement ps;
		Session internalSession = DataAccessManager.getManager().fetchCurrentSession();
		try {
			ps = internalSession.connection().prepareStatement(updateAcct);

			//first batch
			ps.setString(1, acctId);
			ps.addBatch();
			ps.setString(1, acctId);
			ps.addBatch();
			ps.executeBatch();
			ps.close();

			//second batch should run since ps not closed
			ps.setString(1, acctId);
			ps.addBatch();
			ps.setString(1, acctId);
			ps.addBatch();
			ps.executeBatch();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

}