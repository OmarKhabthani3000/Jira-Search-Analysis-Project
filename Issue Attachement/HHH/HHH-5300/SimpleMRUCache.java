/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Middleware LLC.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 *
 */
package org.hibernate.util;

import java.io.IOException;
import java.io.Serializable;

import org.apache.commons.collections.map.LRUMap;
import org.hibernate.cfg.Environment;

/**
 * Cache following a "Most Recently Used" (MRU) algorithm for maintaining a
 * bounded in-memory size; the "Least Recently Used" (LRU) entry is the first
 * available for removal from the cache.
 * <p/>
 * This implementation uses a bounded MRU Map to limit the in-memory size of the
 * cache. Thus the size of this cache never grows beyond the stated size.
 * <p/>
 * <strong>Note:</strong> This class is serializable, however all entries are
 * discarded on serialization.
 * 
 * @see Environment#QUERY_PLAN_CACHE_MAX_STRONG_REFERENCES
 * 
 * @author Steve Ebersole
 * @author Manuel Dominguez Sarmiento
 */
@SuppressWarnings("serial")
public class SimpleMRUCache implements Serializable {

	// Constants

	/**
	 * The default strong reference count.
	 */
	public static final int DEFAULT_STRONG_REF_COUNT = 128;

	// Fields

	private final int strongRefCount;

	private transient LRUMap cache;

	// Constructors

	/**
	 * Constructs a cache with the default settings.
	 * 
	 * @see #DEFAULT_STRONG_REF_COUNT
	 */
	public SimpleMRUCache() {
		this(DEFAULT_STRONG_REF_COUNT);
	}

	/**
	 * Constructs a cache with the specified settings.
	 * 
	 * @param strongRefCount
	 *            the strong reference count.
	 * @throws IllegalArgumentException
	 *             if the strong reference count is less than one.
	 */
	public SimpleMRUCache(int strongRefCount) {
		this.strongRefCount = strongRefCount;
		init();
	}

	// Cache methods

	/**
	 * Gets an object from the cache.
	 * 
	 * @param key
	 *            the cache key.
	 * @return the stored value, or <code>null</code> if no entry exists.
	 */
	public synchronized Object get(Object key) {
		return cache.get(key);
	}

	/**
	 * Puts a value in the cache.
	 * 
	 * @param key
	 *            the key.
	 * @param value
	 *            the value.
	 * @return the previous value stored in the cache, if any.
	 */
	public synchronized Object put(Object key, Object value) {
		return cache.put(key, value);
	}

	/**
	 * Gets the strong reference cache size.
	 * 
	 * @return the strong reference cache size.
	 */
	public synchronized int size() {
		return cache.size();
	}

	/**
	 * Clears the cache.
	 */
	public synchronized void clear() {
		cache.clear();
	}

	// Private helper methods

	private void init() {
		this.cache = new LRUMap(strongRefCount);
	}

	private void readObject(java.io.ObjectInputStream in) throws IOException,
			ClassNotFoundException {

		in.defaultReadObject();
		init();
	}
}
