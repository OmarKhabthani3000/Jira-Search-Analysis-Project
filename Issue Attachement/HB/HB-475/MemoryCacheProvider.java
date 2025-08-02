package net.sf.hibernate.cache;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import net.sf.hibernate.cache.Cache;
import net.sf.hibernate.cache.CacheException;
import net.sf.hibernate.cache.CacheProvider;
import net.sf.hibernate.cache.Timestamper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author David D Phillips
 * 
 * A factory for new {@link Cache} instances. Properties to create each cache is red
 * from the properties file (<code>memoryCache.properties</code>) from the classpath.
 * If the file cannot be found or loaded, an exception is raised.
 */
public class MemoryCacheProvider implements CacheProvider {
  private Log log = LogFactory.getLog(MemoryCacheProvider.class);

  /**
   * A list of all caches that have been created using this CacheProvider
   */
  private List caches = new ArrayList();

  /**
   * The properties file to search the classpath for.
   */
  public static final String PROPERTIES_FILE = "/memoryCache.properties";

  /**
   * Prefix for properties that have specific maxSize/duration per region
   */
  public static final String REGION = "region";

  /**
   * Prefix for regions that do NOT have specific maxSize/duration
   */
  public static final String DEFAULT = "default";

  /** 
   * The timeout (refresh period) property.  This is the time in
   * seconds that an object will ever be returned from the cache.  
   * If it is older, the get method removed this exired item, and returns 
   * null. 
   */
  public static final String DURATION = "duration";

  /** 
   * The max size of each region property. This is the max number
   * of object that will ever exist in this cache.
   */
  public static final String MAXSIZE = "maxSize";

  /**
   * The default duration
   */
  public static final int DURATION_DEFAULT = 300;

  /**
   * The default maxSize
   */
  public static final int MAXSIZE_DEFAULT = 2000;

  /** 
   * @see net.sf.hibernate.cache.CacheProvider#buildCache(java.lang.String, java.util.Properties)
   */
  public synchronized Cache buildCache(String regionName, Properties properties) throws CacheException {
    InputStream is = null;
    try {
      log.debug("About to fetch the propeties file[" + PROPERTIES_FILE + "]");
      is = this.getClass().getResourceAsStream(PROPERTIES_FILE);
      if (is != null) {
        log.debug("Correctly found the propeties file[" + PROPERTIES_FILE + "]");
        Properties props = new Properties();
        props.load(is);

        int durationInt = getDuration(regionName, props);
        int maxSizeInt = getMaxSize(regionName, props);

        MemoryCache cache = new MemoryCache(regionName, maxSizeInt, durationInt);
        caches.add(cache);
        return cache;
      }
      else {
        log.debug("Cant find the propeties file[" + PROPERTIES_FILE + "] Using default duration[" + DURATION_DEFAULT + "] maxSize[" + MAXSIZE_DEFAULT + "]");
        MemoryCache cache = new MemoryCache(regionName, MAXSIZE_DEFAULT, DURATION_DEFAULT);
        caches.add(cache);
        return cache;
      }
    }
    catch (IOException e) {
      throw new CacheException(e);
    }
    finally {
      if (is != null) {
        try {
          is.close();
        }
        catch (IOException e) {
          throw new CacheException(e);
        }
      }
    }
  }

  /**
   * Gets the maxSize from either the region, propdefault or default
   * @param regionName
   * @param props
   * @return
   */
  private int getMaxSize(String regionName, Properties props) {
    String maxSize = props.getProperty(REGION + "." + regionName + "." + MAXSIZE);
    if (maxSize == null) {
      maxSize = props.getProperty(DEFAULT + "." + MAXSIZE);
    }
    if (maxSize != null) {
      log.debug("Read the maxSize[" + maxSize + "] for region[" + regionName + "]");
      return Integer.parseInt(maxSize);
    }
    else {
      log.debug("Using default maxSize[" + MAXSIZE_DEFAULT + "] for region[" + regionName + "]");
      return MAXSIZE_DEFAULT;
    }
  }

  /**
   * Gets the duration from either the region, propdefault or default
   * @param regionName
   * @param props
   * @return
   */
  private int getDuration(String regionName, Properties props) {
    String duration = props.getProperty(REGION + "." + regionName + "." + DURATION);
    if (duration == null) {
      duration = props.getProperty(DEFAULT + "." + DURATION);
    }
    if (duration != null) {
      log.debug("Read the duration[" + duration + "] for region[" + regionName + "]");
      return Integer.parseInt(duration);
    }
    else {
      log.debug("Using default duration[" + DURATION_DEFAULT + "] for region[" + regionName + "]");
      return DURATION_DEFAULT;
    }
  }
  /**
   * The list of all caches
   * @return 
   */
  public synchronized List getCaches() {
    return caches;
  }

  /**
   * Clears all the caches that are associated with this factory
   * @return 
   */
  public synchronized void clearCaches() throws CacheException {
    for (Iterator it = caches.iterator(); it.hasNext();) {
      MemoryCache cache = (MemoryCache)it.next();
      cache.clear();
    }
  }

  /**
   * @see net.sf.hibernate.cache.CacheProvider#nextTimestamp()
   */
  public long nextTimestamp() {
    return Timestamper.next();
  }

}
