package com.vocado.services.hibernate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.collection.PersistentCollection;
import org.hibernate.ejb.event.EJB3FlushEventListener;
import org.hibernate.engine.CollectionEntry;
import org.hibernate.engine.PersistenceContext;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.event.FlushEvent;
import org.jboss.tm.TransactionLocal;

import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * This special implementation of the FlushEventListener is used to workaround the HH2763 bug
 * by allowing users to lazy load collections during flush.
 *
 * The way the flush listener works is that before performing the postFlush, the listener
 * removes all non processed entries that have been loaded during the flush operation, and
 * put them in a queue to be processed at the end of the normal flush.
 *
 * Like this the listener can automatically flush again as needed the lazy loaded collection
 * entries and process them safely, meaning that if you load an lazy collection entry, and if
 * you modify it later on with an event, the entry should be processed in a second phase.
 *
 * The listener will flush() as many time as required to flush all lazily loaded entries.
 *
 * @author benoit@vocado.net
 * @since Jun 24, 2010
 */
public class HibernateWorkaroundHH2763 extends EJB3FlushEventListener {
	private static final long serialVersionUID = 5374361490442663245L;
	private static final Log log = LogFactory.getLog(HibernateWorkaroundHH2763.class);

	// Contains the Map<PersistentCollection, CollectionEntry> of all elements that
	// couldn't be processed by the queue.
	private static final TransactionLocal flushAgainQueue = new TransactionLocal() {
		@Override
		protected Object initialValue() {
			return new IdentityHashMap<PersistentCollection, CollectionEntry>();
		}
	};
	
	// Indicate how many flushes the workaround can try
	private static final int MAX_FLUSH = 100;


	public void onFlush(FlushEvent event) throws HibernateException {
		log.info("Start flushing session!");
		final Map<PersistentCollection, CollectionEntry> queue = getFlushAgainQueue();
		final PersistenceContext persistenceContext = event.getSession().getPersistenceContext();
		int counter = 0;
		do {
			// When flushing, let's start by adding the entries form the previous
			// flush to the current flush
			final Map entries = persistenceContext.getCollectionEntries();
			entries.putAll(queue);

			// then clear the queue
			queue.clear();
			
			// and then do the flush.
			super.onFlush(event);
			
			// and do this until the queue is empty
		} while (!queue.isEmpty() && counter++ < MAX_FLUSH);
		log.info("Flushing session done!");
	}


	@Override
	protected void postFlush(SessionImplementor session) throws HibernateException {
		// Remove the list of CollectionEntry that can't be processed by the session
		// and keep them for a later flush operation.
		final PersistenceContext persistenceContext = session.getPersistenceContext();
		final Map entries = persistenceContext.getCollectionEntries();
		final Map<PersistentCollection, CollectionEntry> queue = getFlushAgainQueue();
		
		final Iterator iter = entries.entrySet().iterator();
		while ( iter.hasNext() ) {
			Map.Entry me = (Map.Entry) iter.next();
			CollectionEntry collectionEntry = (CollectionEntry) me.getValue();
			// If the collection entry isn't ignored and has to be processed but
			// wasn't processed at that stage, then it means that the collectionEntry
			// needs to be processed in another phase
			if (!collectionEntry.isIgnore() && !collectionEntry.isProcessed()) {
				// Start removing this entry from the actual queue
				// iter.remove(); //does not work, since the entrySet is not backed by the set
				PersistentCollection persistentCollection = (PersistentCollection) me.getKey();
				entries.remove(persistentCollection);

				// and let's queue it for the next phase
				queue.put(persistentCollection, collectionEntry);
				log.warn("Un processed collection entry found during flush, queue it to next phase " + collectionEntry);
			}
		}

		super.postFlush(session);
	}

	@SuppressWarnings({"unchecked"})
	private Map<PersistentCollection, CollectionEntry> getFlushAgainQueue() {
		return (Map<PersistentCollection, CollectionEntry>) flushAgainQueue.get();
	}
}
