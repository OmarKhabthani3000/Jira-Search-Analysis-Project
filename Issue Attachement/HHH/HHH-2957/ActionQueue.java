// $Id: ActionQueue.java 11403 2007-04-11 14:25:13Z steve.ebersole@jboss.com $
package org.hibernate.engine;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.AssertionFailure;
import org.hibernate.HibernateException;
import org.hibernate.action.BulkOperationCleanupAction;
import org.hibernate.action.CollectionRecreateAction;
import org.hibernate.action.CollectionRemoveAction;
import org.hibernate.action.CollectionUpdateAction;
import org.hibernate.action.EntityDeleteAction;
import org.hibernate.action.EntityIdentityInsertAction;
import org.hibernate.action.EntityInsertAction;
import org.hibernate.action.EntityUpdateAction;
import org.hibernate.action.Executable;
import org.hibernate.cache.CacheException;

/**
 * Responsible for maintaining the queue of actions related to events.
 * </p>
 * The ActionQueue holds the DML operations queued as part of a session's
 * transactional-write-behind semantics.  DML operations are queued here
 * until a flush forces them to be executed against the database.
 *
 * @author Steve Ebersole
 */
public class ActionQueue {

	private static final Log log = LogFactory.getLog( ActionQueue.class );

	private SessionImplementor session;

	// Object insertions, updates, and deletions have list semantics because
	// they must happen in the right order so as to respect referential
	// integrity
	private List insertions;
	private List deletions;
	private List updates;
	// Actually the semantics of the next three are really "Bag"
	// Note that, unlike objects, collection insertions, updates,
	// deletions are not really remembered between flushes. We
	// just re-use the same Lists for convenience.
	private List collectionCreations;
	private List collectionUpdates;
	private List collectionRemovals;

	private List executions;

	/**
	 * Constructs an action queue bound to the given session.
	 *
	 * @param session The session "owning" this queue.
	 */
	public ActionQueue(SessionImplementor session) {
		this.session = session;
		init();
	}

	private void init() {
		// these should be linked lists (as opposed to ArrayLists) since we don't
		// know how many entries will be added to each list.
		// As long as an Iterator is used to iterate over the lists, the performance differences
		// between a LinkedList and an ArrayList are negligible.
		insertions = new LinkedList();
		deletions = new LinkedList();
		updates = new LinkedList();

		collectionCreations = new LinkedList();
		collectionRemovals = new LinkedList();
		collectionUpdates = new LinkedList();

		executions = new LinkedList();
	}

	public void clear() {
		updates.clear();
		insertions.clear();
		deletions.clear();

		collectionCreations.clear();
		collectionRemovals.clear();
		collectionUpdates.clear();
	}

	public void addAction(EntityInsertAction action) {
		insertions.add( action );
	}

	public void addAction(EntityDeleteAction action) {
		deletions.add( action );
	}

	public void addAction(EntityUpdateAction action) {
		updates.add( action );
	}

	public void addAction(CollectionRecreateAction action) {
		collectionCreations.add( action );
	}

	public void addAction(CollectionRemoveAction action) {
		collectionRemovals.add( action );
	}

	public void addAction(CollectionUpdateAction action) {
		collectionUpdates.add( action );
	}

	public void addAction(EntityIdentityInsertAction insert) {
		insertions.add( insert );
	}

	public void addAction(BulkOperationCleanupAction cleanupAction) {
		// Add these directly to the executions queue
		executions.add( cleanupAction );
	}

	/**
	 * Perform all currently queued entity-insertion actions.
	 *
	 * @throws HibernateException error executing queued insertion actions.
	 */
	public void executeInserts() throws HibernateException {
		executeActions( insertions );
	}

	/**
	 * Perform all currently queued actions.
	 *
	 * @throws HibernateException error executing queued actions.
	 */
	public void executeActions() throws HibernateException {
		executeActions( insertions );
		executeActions( updates );
		executeActions( collectionRemovals );
		executeActions( collectionUpdates );
		executeActions( collectionCreations );
		executeActions( deletions );
	}

	/**
	 * Prepares the internal action queues for execution.
	 *
	 * @throws HibernateException error preparing actions.
	 */
	public void prepareActions() throws HibernateException {
		prepareActions( collectionRemovals );
		prepareActions( collectionUpdates );
		prepareActions( collectionCreations );
	}

	/**
	 * Performs cleanup of any held cache softlocks.
	 *
	 * @param success Was the transaction successful.
	 */
	public void afterTransactionCompletion(boolean success) {
		final boolean invalidateQueryCache = session.getFactory().getSettings().isQueryCacheEnabled();
		for ( Iterator execItr = executions.iterator(); execItr.hasNext(); ) {
			try {
				Executable exec = ( Executable ) execItr.next();
				try {
					exec.afterTransactionCompletion( success );
				}
				finally {
					if ( invalidateQueryCache ) {
						session.getFactory().getUpdateTimestampsCache().invalidate( exec.getPropertySpaces() );
					}
				}
			}
			catch (CacheException ce) {
				log.error( "could not release a cache lock", ce );
				// continue loop
			}
			catch (Exception e) {
				throw new AssertionFailure( "Exception releasing cache locks", e );
			}
		}
		executions.clear();
	}

	/**
	 * Check whether the given tables/query-spaces are to be executed against
	 * given the currently queued actions.
	 *
	 * @param tables The table/query-spaces to check.
	 * @return True if we contain pending actions against any of the given
	 * tables; false otherwise.
	 */
	public boolean areTablesToBeUpdated(Set tables) {
		return areTablesToUpdated( updates, tables ) ||
		       areTablesToUpdated( insertions, tables ) ||
		       areTablesToUpdated( deletions, tables ) ||
		       areTablesToUpdated( collectionUpdates, tables ) ||
		       areTablesToUpdated( collectionCreations, tables ) ||
		       areTablesToUpdated( collectionRemovals, tables );
	}

	/**
	 * Check whether any insertion or deletion actions are currently queued.
	 *
	 * @return True if insertions or deletions are currently queued; false otherwise.
	 */
	public boolean areInsertionsOrDeletionsQueued() {
		return ( insertions.size() > 0 || deletions.size() > 0 );
	}

	private static boolean areTablesToUpdated(List executables, Set tablespaces) {
		for ( Iterator execItr = executables.iterator(); execItr.hasNext(); ) {
			Serializable[] spaces = ( (Executable) execItr.next() ).getPropertySpaces();
			for ( int i = 0; i < spaces.length; i++ ) {
				if ( tablespaces.contains( spaces[i] ) ) {
					if ( log.isDebugEnabled() ) log.debug( "changes must be flushed to space: " + spaces[i] );
					return true;
				}
			}
		}
		return false;
	}

	private void executeActions(List list) throws HibernateException {
		for ( Iterator execItr = list.iterator(); execItr.hasNext(); ) {
			execute( (Executable) execItr.next() );
		}
		list.clear();
		session.getBatcher().executeBatch();
	}

	public void execute(Executable executable) {
		final boolean lockQueryCache = session.getFactory().getSettings().isQueryCacheEnabled();
		if ( executable.hasAfterTransactionCompletion() || lockQueryCache ) {
			executions.add( executable );
		}
		if (lockQueryCache) {
			session.getFactory()
				.getUpdateTimestampsCache()
				.preinvalidate( executable.getPropertySpaces() );
		}
		executable.execute();
	}

	private void prepareActions(List queue) throws HibernateException {
		for ( Iterator execItr = queue.iterator(); execItr.hasNext(); ) {
			Executable executable = ( Executable ) execItr.next();
			executable.beforeExecutions();
		}
	}

	/**
	 * Returns a string representation of the object.
	 *
	 * @return a string representation of the object.
	 */
	public String toString() {
		return new StringBuffer()
				.append("ActionQueue[insertions=").append(insertions)
				.append(" updates=").append(updates)
		        .append(" deletions=").append(deletions)
				.append(" collectionCreations=").append(collectionCreations)
				.append(" collectionRemovals=").append(collectionRemovals)
				.append(" collectionUpdates=").append(collectionUpdates)
		        .append("]")
				.toString();
	}

	public int numberOfCollectionRemovals() {
		return collectionRemovals.size();
	}

	public int numberOfCollectionUpdates() {
		return collectionUpdates.size();
	}

	public int numberOfCollectionCreations() {
		return collectionCreations.size();
	}

	public int numberOfDeletions() {
		return deletions.size();
	}

	public int numberOfUpdates() {
		return updates.size();
	}

	public int numberOfInsertions() {
		return insertions.size();
	}

	public void sortCollectionActions() {
		if ( session.getFactory().getSettings().isOrderUpdatesEnabled() ) {
			//sort the updates by fk
			java.util.Collections.sort( collectionCreations );
			java.util.Collections.sort( collectionUpdates );
			java.util.Collections.sort( collectionRemovals );
		}
	}

	public void sortActions() {
		if ( session.getFactory().getSettings().isOrderUpdatesEnabled() ) {
			//sort the updates by pk
			java.util.Collections.sort( updates );
		}
		if ( session.getFactory().getSettings().isOrderInsertsEnabled() ) {
			sortInsertActions();
		}
	}

	/**
	 * Order the {@link #insertions} queue such that we group inserts
	 * against the same entity together (without violating constraints).  The
	 * original order is generated by cascade order, which in turn is based on
	 * the directionality of foreign-keys.  So even though we will be changing
	 * the ordering here, we need to make absolutely certain that we do not
	 * circumvent this FK ordering to the extent of causing constraint
	 * violations
	 */
	public void sortInsertActions() {
		NewInsertActionSorter sorter = new NewInsertActionSorter(insertions);
		sorter.sort();
	}

	public ArrayList cloneDeletions() {
		return new ArrayList(deletions);
	}

	public void clearFromFlushNeededCheck(int previousCollectionRemovalSize) {
		collectionCreations.clear();
		collectionUpdates.clear();
		updates.clear();
		// collection deletions are a special case since update() can add
		// deletions of collections not loaded by the session.
		for ( int i = collectionRemovals.size()-1; i >= previousCollectionRemovalSize; i-- ) {
			collectionRemovals.remove(i);
		}
	}

	public boolean hasAnyQueuedActions() {
		return updates.size() > 0 ||
		       insertions.size() > 0 ||
		       deletions.size() > 0 ||
		       collectionUpdates.size() > 0 ||
		       collectionRemovals.size() > 0 ||
		       collectionCreations.size() > 0;
	}

	/**
	 * Used by the owning session to explicitly control serialization of the
	 * action queue
	 *
	 * @param oos The stream to which the action queue should get written
	 * @throws IOException
	 */
	public void serialize(ObjectOutputStream oos) throws IOException {
		log.trace( "serializing action-queue" );

		int queueSize = insertions.size();
		log.trace( "starting serialization of [" + queueSize + "] insertions entries" );
		oos.writeInt( queueSize );
		for ( Iterator insertionItr = insertions.iterator(); insertionItr.hasNext(); ) {
			oos.writeObject( insertionItr );
		}

		queueSize = deletions.size();
		log.trace( "starting serialization of [" + queueSize + "] deletions entries" );
		oos.writeInt( queueSize );
		for ( Iterator deletionItr = deletions.iterator(); deletionItr.hasNext(); ) {
			oos.writeObject( deletionItr.next() );
		}

		queueSize = updates.size();
		log.trace( "starting serialization of [" + queueSize + "] updates entries" );
		oos.writeInt( queueSize );
		for ( Iterator updateItr = updates.iterator(); updateItr.hasNext(); ) {
			oos.writeObject( updateItr.next() );
		}

		queueSize = collectionUpdates.size();
		log.trace( "starting serialization of [" + queueSize + "] collectionUpdates entries" );
		oos.writeInt( queueSize );
		for ( Iterator collectionUpdateItr = collectionUpdates.iterator(); collectionUpdateItr.hasNext(); ) {
			oos.writeObject( collectionUpdateItr.next() );
		}

		queueSize = collectionRemovals.size();
		log.trace( "starting serialization of [" + queueSize + "] collectionRemovals entries" );
		oos.writeInt( queueSize );
		for ( Iterator collectionRemovalItr = collectionRemovals.iterator(); collectionRemovalItr.hasNext(); ) {
			oos.writeObject( collectionRemovalItr.next() );
		}

		queueSize = collectionCreations.size();
		log.trace( "starting serialization of [" + queueSize + "] collectionCreations entries" );
		oos.writeInt( queueSize );
		for ( Iterator collectionCreationItr = collectionCreations.iterator(); collectionCreationItr.hasNext(); ) {
			oos.writeObject( collectionCreationItr.next() );
		}
	}

	/**
	 * Used by the owning session to explicitly control deserialization of the
	 * action queue
	 *
	 * @param ois The stream from which to read the action queue
	 * @throws IOException
	 */
	public static ActionQueue deserialize(
			ObjectInputStream ois,
	        SessionImplementor session) throws IOException, ClassNotFoundException {
		log.trace( "deserializing action-queue" );
		ActionQueue rtn = new ActionQueue( session );

		int queueSize = ois.readInt();
		log.trace( "starting deserialization of [" + queueSize + "] insertions entries" );
		rtn.insertions = new ArrayList( queueSize );
		for ( int i = 0; i < queueSize; i++ ) {
			rtn.insertions.add( ois.readObject() );
		}

		queueSize = ois.readInt();
		log.trace( "starting deserialization of [" + queueSize + "] deletions entries" );
		rtn.deletions = new ArrayList( queueSize );
		for ( int i = 0; i < queueSize; i++ ) {
			rtn.deletions.add( ois.readObject() );
		}

		queueSize = ois.readInt();
		log.trace( "starting deserialization of [" + queueSize + "] updates entries" );
		rtn.updates = new ArrayList( queueSize );
		for ( int i = 0; i < queueSize; i++ ) {
			rtn.updates.add( ois.readObject() );
		}

		queueSize = ois.readInt();
		log.trace( "starting deserialization of [" + queueSize + "] collectionUpdates entries" );
		rtn.collectionUpdates = new ArrayList( queueSize );
		for ( int i = 0; i < queueSize; i++ ) {
			rtn.collectionUpdates.add( ois.readObject() );
		}

		queueSize = ois.readInt();
		log.trace( "starting deserialization of [" + queueSize + "] collectionRemovals entries" );
		rtn.collectionRemovals = new ArrayList( queueSize );
		for ( int i = 0; i < queueSize; i++ ) {
			rtn.collectionRemovals.add( ois.readObject() );
		}

		queueSize = ois.readInt();
		log.trace( "starting deserialization of [" + queueSize + "] collectionCreations entries" );
		rtn.collectionCreations = new ArrayList( queueSize );
		for ( int i = 0; i < queueSize; i++ ) {
			rtn.collectionCreations.add( ois.readObject() );
		}
		return rtn;
	}
}
