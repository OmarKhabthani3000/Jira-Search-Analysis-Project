package org.hibernate.cache;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.ibm.websphere.cache.DistributedMap;

public class WebSphereCache implements Cache {
	
	private final DistributedMap cache;
	private final String regionName;

	public WebSphereCache(DistributedMap cache, String regionName) {
		this.cache = cache;
		this.regionName = regionName;
	}
	
	public void clear() throws CacheException {
		cache.clear();
	}

	public void destroy() throws CacheException {
		// nothing to destroy;
	}

	public Object get(Object key) throws CacheException {
		return cache.get(asWebSphereKey(key));
	}

	public long getElementCountInMemory() {
		return cache.size(false);
	}

	public long getElementCountOnDisk() {
		return cache.size(true) - cache.size(false);
	}

	public String getRegionName() {
		return regionName;
	}

	public long getSizeInMemory() {
		// Not supported
		return -1;
	}

	public int getTimeout() {
		// TODO: see if we can figure this out from WebSphere's metadata
		// for the cache. Don't know how to obtain that, though...
		return Timestamper.ONE_MS * 60000; //ie. 60 seconds
	}

	public void lock(Object key) throws CacheException {
		// not needed / supported by WebSphere
	}

	public long nextTimestamp() {
		return Timestamper.next();
	}

	public void put(Object key, Object value) throws CacheException {
		cache.put(asWebSphereKey(key), value);
	}

	public Object read(Object key) throws CacheException {
		return get(key);
	}

	public void remove(Object key) throws CacheException {
		cache.invalidate(asWebSphereKey(key));
	}

	public Map toMap() {
		Set keySet = cache.keySet(true);
		Map map = new HashMap(keySet.size());
		for (Iterator iter = keySet.iterator(); iter.hasNext();) {
			Object key = iter.next();
			map.put(key, cache.get(key));
		}
		return map;
	}

	public void unlock(Object key) throws CacheException {
		// not needed / supported by WebSphere
	}

	public void update(Object key, Object value) throws CacheException {
		put(key, value);
	}
	
	/**
	 * Ensures the key used for the WebSphere cache is unique, 
	 * whether we use separate cache instances for each cached entity or not.
	 * Note that WebSphere's DistributedMap only supports Strings as keys,
	 * although the method signatures do not indicate this
	 * @param suppliedKey the key used by Hibernate
	 * @return a unique stringified key, 
	 * 		    composed of the cache's region name and the Hibernate cache name 
	 */
	private String asWebSphereKey(Object suppliedKey) {
		return this.regionName + '.' + suppliedKey;
	}

}
