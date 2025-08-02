package org.hibernate.test.cascade;
import static org.junit.Assert.assertEquals;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.jdbc.Work;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Test;

/**
 * Show how collection refresh fails
 */
public class RefreshCascadeCollectionsTest extends BaseCoreFunctionalTestCase {
	@Override
	public String[] getMappings() {
		return new String[] {
				"cascade/Child.hbm.xml",
				"cascade/DeleteOrphanChild.hbm.xml",
				"cascade/Parent.hbm.xml"
		};
	}

	@Test
	public void testRefreshDeletedChild() throws Throwable {
		Session session = openSession();
		Transaction txn = session.beginTransaction();
		Parent parent = new Parent();
		DeleteOrphanChild child = new DeleteOrphanChild();
		child.setParent( parent );
		parent.setDeleteOrphanChildren( Collections.singleton( child ) );
		session.save( parent );

		session.flush();
		
		// behind the session's back, delete the child
		deleteOrphanChild((SessionImplementor) session, child);
		
		// Now refresh the parent, and see if the collection is refreshed
		// Unfortunately this will throw an EntityNotFoundException and also mark transaction for rollback
		session.refresh(parent);
		
		// We expect the collection to be in sync with the underlying database
		assertEquals( 0, parent.getDeleteOrphanChildren().size() );
		
		txn.rollback();
		session.close();
	}

	@Test
	public void testRefreshAddedChild() throws Throwable {
		Session session = openSession();
		Transaction txn = session.beginTransaction();
		Parent parent = new Parent();
		DeleteOrphanChild child = new DeleteOrphanChild();
		child.setParent( parent );
		parent.setDeleteOrphanChildren( Collections.singleton( child ) );
		session.save( parent );

		session.flush();
		
		// behind the session's back, add a child
		addOrphanChild((SessionImplementor) session, parent);
		
		// Now refresh the parent, and see if the collection is refreshed
		// This actually sees the added child
		session.refresh(parent);
		
		// We expect the collection to be in sync with the underlying database
		assertEquals( 2, parent.getDeleteOrphanChildren().size() );

		txn.rollback();
		session.close();
	}

	@Test
	public void testRefreshDeletedAddedChild() throws Throwable {
		Session session = openSession();
		Transaction txn = session.beginTransaction();
		Parent parent = new Parent();
		DeleteOrphanChild child = new DeleteOrphanChild();
		child.setParent( parent );
		parent.setDeleteOrphanChildren( Collections.singleton( child ) );
		session.save( parent );

		session.flush();
		
		// behind the session's back, delete the child
		deleteOrphanChild((SessionImplementor) session, child);
		// behind the session's back, add a child
		addOrphanChild((SessionImplementor) session, parent);
		
		// Now refresh the parent, and see if the collection is refreshed
		// Unfortunately this will throw an EntityNotFoundException and also mark transaction for rollback
		session.refresh(parent);
		
		// We expect the collection to be in sync with the underlying database
		assertEquals( 1, parent.getDeleteOrphanChildren().size() );
		
		txn.rollback();
		session.close();
	}

	private void deleteOrphanChild(final SessionImplementor session, DeleteOrphanChild child) throws Throwable {
		((Session)session).doWork(
				new Work() {
					@Override
					public void execute(Connection connection) throws SQLException {
						PreparedStatement stmnt = null;
						try {
							stmnt = session.getJdbcCoordinator().getStatementPreparer().prepareStatement( "DELETE FROM DeleteOrphanChild WHERE id=?");
							stmnt.setLong(1, child.getId());
							session.getJdbcCoordinator().getResultSetReturn().executeUpdate( stmnt );
						}
						finally {
							if ( stmnt != null ) {
								try {
									session.getJdbcCoordinator().getResourceRegistry().release( stmnt );
								}
								catch( Throwable ignore ) {
								}
							}
						}
					}
				}
		);
	}

	private void addOrphanChild(final SessionImplementor session, Parent parent) throws Throwable {
		((Session)session).doWork(
				new Work() {
					@Override
					public void execute(Connection connection) throws SQLException {
						PreparedStatement stmnt = null;
						try {
							stmnt = session.getJdbcCoordinator().getStatementPreparer().prepareStatement( "INSERT INTO DeleteOrphanChild(parent) VALUES(?)");
							stmnt.setLong(1, parent.getId());
							session.getJdbcCoordinator().getResultSetReturn().executeUpdate( stmnt );
						}
						finally {
							if ( stmnt != null ) {
								try {
									session.getJdbcCoordinator().getResourceRegistry().release( stmnt );
								}
								catch( Throwable ignore ) {
								}
							}
						}
					}
				}
		);
	}
}
