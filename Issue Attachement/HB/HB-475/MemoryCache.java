package net.sf.hibernate.cache;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import net.sf.hibernate.cache.Cache;
import net.sf.hibernate.cache.CacheException;
import net.sf.hibernate.cache.Timestamper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author David D Phillips
 * 
 * This class implements a maxSize/timeout memory cache.  Its based on the
 * LinkedHashMap provided with Java1.4
 */
public class MemoryCache implements Cache {
  private Log log = LogFactory.getLog(MemoryCache.class);

  private LinkedHashMap cache;
  private int duration;
  private int maxSize;
  private String regionName;

  private long cacheHits = 0;
  private long cacheMisses = 0;
  private long cacheExpired = 0;
  private long cacheOverflow = 0;

  /**
   * A class that wraps the value to provide an expire time
   */
  private class MemoryItem {
    public Object value;
    public long createTime = System.currentTimeMillis();

    public MemoryItem(Object value) {
      this.value = value;
    }

    public boolean hasExpired() {
      if (duration == 0) {
        //if duration is zero, then expiration is disabled
        return false;
      }
      else {
        return System.currentTimeMillis() - createTime > duration * 1000;
      }
    }
  }

  /**
   * A class that makes the Java1.4 LinkedHashMap into a LRU cache
   */
  private class LRULinkedHashMap extends LinkedHashMap {
    public LRULinkedHashMap(int initialCapacity) {
      super(initialCapacity);
    }

    protected boolean removeEldestEntry(Map.Entry eldest) {
      if (maxSize == 0) {
        return false;
      }
      else {
        if (size() > maxSize) {
          cacheOverflow++;
          return true;
        }
        else {
          return false;
        }
      }
    }
  }

  /**
   * Create a new LRU memory cache. 
   * @param maxSize max number of items that can be in the cache
   * @param timeout max number of seconds an item can live in the cache before expiring
   */
  public MemoryCache(String region, int maxSize, int timeout) {
    if (maxSize < 0) {
      throw new IllegalArgumentException("Cannot pass a negative value to maxSize");
    }
    if (timeout < 0) {
      throw new IllegalArgumentException("Cannot pass a negative value to timeout");
    }
    this.cache = new LRULinkedHashMap(maxSize);
    this.regionName = region;
    this.maxSize = maxSize;
    this.duration = timeout;
  }

  /* (non-Javadoc)
   * @see net.sf.hibernate.cache.Cache#get(java.lang.Object)
   */
  public synchronized Object get(Object key) throws CacheException {
    MemoryItem memItem = (MemoryItem)cache.get(key);
    if (memItem == null) {
      cacheMisses++;
      return null;
    }
    else if (memItem.hasExpired()) {
      cacheExpired++;
      cache.remove(key);
      return null;
    }
    else {
      cacheHits++;
      return memItem.value;
    }
  }

  /* (non-Javadoc)
   * @see net.sf.hibernate.cache.Cache#put(java.lang.Object, java.lang.Object)
   */
  public synchronized void put(Object key, Object value) throws CacheException {
    cache.put(key, new MemoryItem(value));
  }

  /* (non-Javadoc)
   * @see net.sf.hibernate.cache.Cache#remove(java.lang.Object)
   */
  public synchronized void remove(Object key) throws CacheException {
    cache.remove(key);
  }

  /* (non-Javadoc)
   * @see net.sf.hibernate.cache.Cache#clear()
   */
  public synchronized void clear() throws CacheException {
    cacheHits = 0;
    cacheMisses = 0;
    cacheExpired = 0;
    cacheOverflow = 0;
    cache.clear();
  }

  /* (non-Javadoc)
   * @see net.sf.hibernate.cache.Cache#destroy()
   */
  public synchronized void destroy() throws CacheException {
    cache.clear();
    cache = null;
  }

  /* (non-Javadoc)
   * @see net.sf.hibernate.cache.Cache#lock(java.lang.Object)
   */
  public void lock(Object key) throws CacheException {
    //Not supported
  }

  /* (non-Javadoc)
   * @see net.sf.hibernate.cache.Cache#unlock(java.lang.Object)
   */
  public void unlock(Object key) throws CacheException {
    //Not supported
  }

  /* (non-Javadoc)
   * @see net.sf.hibernate.cache.Cache#nextTimestamp()
   */
  public long nextTimestamp() {
    return Timestamper.next();
  }

  /* (non-Javadoc)
   * @see net.sf.hibernate.cache.Cache#getTimeout()
   */
  public int getTimeout() {
    return Timestamper.ONE_MS * 60; //Reasonable
  }

  /**
   * Goes through every item in the cache, and removes it if it has
   * expired.  If this method is not called, all the expired item are
   * still reference in the cache.
   */
  public synchronized void removeExpired() {
    for (Iterator it = cache.keySet().iterator(); it.hasNext();) {
      Object key = it.next();
      cache.get(key);
    }
  }
  /**
   * @return
   */
  public long getCacheExpired() {
    return cacheExpired;
  }

  /**
   * @return
   */
  public long getCacheHits() {
    return cacheHits;
  }

  /**
   * @return
   */
  public long getCacheMisses() {
    return cacheMisses;
  }

  /**
   * @return
   */
  public long getCacheOverflow() {
    return cacheOverflow;
  }

  /**
   * @return
   */
  public int getMaxSize() {
    return maxSize;
  }

  /**
   * @return
   */
  public int getDuration() {
    return duration;
  }

  /**
   * @return
   */
  public String getRegionName() {
    return regionName;
  }

  /**
   * @return
   */
  public int getSize() {
    return cache.size();
  }

  /**
   * @return
   */
  public int getHitRate() {
    if (cacheHits + cacheMisses + cacheExpired == 0) {
      return 0;
    }
    else {
      double result = cacheHits;
      result = result / (cacheHits + cacheMisses + cacheExpired) * 100;
      return (int)result;
    }
  }

}
