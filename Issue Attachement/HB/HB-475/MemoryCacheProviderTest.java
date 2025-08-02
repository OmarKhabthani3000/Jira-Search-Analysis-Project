package test.net.sf.hibernate.cache;

import java.util.Properties;

import junit.framework.TestCase;

import org.apache.log4j.BasicConfigurator;

import net.sf.hibernate.cache.MemoryCache;
import net.sf.hibernate.cache.MemoryCacheProvider;

/**
 * @author David D. Phillips
 */
public class MemoryCacheProviderTest extends TestCase {

  static {
    BasicConfigurator.configure();
  }

  public void testProperties() throws Exception {
    MemoryCacheProvider provider = new MemoryCacheProvider();
    MemoryCache cache = (MemoryCache)provider.buildCache("region", new Properties());
    assertEquals(10, cache.getDuration());
    assertEquals(50, cache.getMaxSize());
    assertEquals("region", cache.getRegionName());
  }

  public void testRegionSpecific() throws Exception {
    MemoryCacheProvider provider = new MemoryCacheProvider();
    MemoryCache cache = (MemoryCache)provider.buildCache("region", new Properties());
    MemoryCache cache1 = (MemoryCache)provider.buildCache("region1", new Properties());
    MemoryCache cache2 = (MemoryCache)provider.buildCache("region2", new Properties());
    MemoryCache cache3 = (MemoryCache)provider.buildCache("region3", new Properties());
    MemoryCache cache4 = (MemoryCache)provider.buildCache("region4", new Properties());
    assertEquals(10, cache.getDuration());
    assertEquals(50, cache.getMaxSize());
    assertEquals(1, cache1.getDuration());
    assertEquals(1, cache1.getMaxSize());
    assertEquals(2, cache2.getDuration());
    assertEquals(2, cache2.getMaxSize());
    assertEquals(10, cache3.getDuration());
    assertEquals(3, cache3.getMaxSize());
    assertEquals(4, cache4.getDuration());
    assertEquals(50, cache4.getMaxSize());
  }

  public void testGetAllCaches() throws Exception {
    MemoryCacheProvider provider = new MemoryCacheProvider();
    MemoryCache cache1 = (MemoryCache)provider.buildCache("region1", new Properties());
    MemoryCache cache2 = (MemoryCache)provider.buildCache("region2", new Properties());
    MemoryCache cache3 = (MemoryCache)provider.buildCache("region3", new Properties());
    assertEquals(3, provider.getCaches().size());
  }

  public void testClearAllCaches() throws Exception {
    MemoryCacheProvider provider = new MemoryCacheProvider();
    MemoryCache cache1 = (MemoryCache)provider.buildCache("region1", new Properties());
    MemoryCache cache2 = (MemoryCache)provider.buildCache("region2", new Properties());
    MemoryCache cache3 = (MemoryCache)provider.buildCache("region3", new Properties());

    cache1.put(new Integer(0), new Integer(0));
    cache2.put(new Integer(0), new Integer(0));

    provider.clearCaches();

    assertEquals(0, cache1.getSize());
    assertEquals(0, cache2.getSize());
    assertEquals(0, cache3.getSize());
  }

  public void testDisableMaxSize() throws Exception {
    MemoryCache cache = new MemoryCache("region", 0, 0);
    assertEquals(0, cache.getMaxSize());
    assertEquals(0, cache.getDuration());
    for (int i = 0; i < MemoryCacheProvider.MAXSIZE_DEFAULT + 100; i++) {
      cache.put(new Integer(i), new Integer(i));
    }
    assertEquals(0, cache.getCacheOverflow());
    assertEquals(MemoryCacheProvider.MAXSIZE_DEFAULT + 100, cache.getSize());
  }

  public void testInvalidMaxSize() throws Exception {
    try {
      MemoryCache cache = new MemoryCache("region", -1, 0);
      fail();
    }
    catch (Exception e) {
    }
  }

  public void testDisableDuration() throws Exception {
    MemoryCache cache = new MemoryCache("region", 0, 0);
    assertEquals(0, cache.getMaxSize());
    assertEquals(0, cache.getDuration());
    for (int i = 0; i < 10; i++) {
      cache.put(new Integer(i), new Integer(i));
    }
    assertEquals(0, cache.getCacheOverflow());
    assertEquals(10, cache.getSize());

    Thread.sleep(cache.getDuration() + 1000);

    assertEquals(0, cache.getCacheOverflow());
    assertEquals(10, cache.getSize());
  }

  public void testInvalidDuration() throws Exception {
    try {
      MemoryCache cache = new MemoryCache("region", 0, -1);
      fail();
    }
    catch (Exception e) {
    }
  }

  public void testMaxSize() throws Exception {
    MemoryCacheProvider provider = new MemoryCacheProvider();
    MemoryCache cache = new MemoryCache("region", 10, 0);
    assertEquals(10, cache.getMaxSize());
    for (int i = 0; i < cache.getMaxSize() + 10; i++) {
      cache.put(new Integer(i), new Integer(i));
    }
    assertEquals(cache.getMaxSize(), cache.getSize());
    assertEquals(10, cache.getMaxSize());
  }

  public void testDuration() throws Exception {
    MemoryCache cache = new MemoryCache("region", 0, 1);
    assertEquals(0, cache.getMaxSize());
    assertEquals(1, cache.getDuration());
    for (int i = 0; i < 10; i++) {
      cache.put(new Integer(i), new Integer(i));
    }
    assertEquals(0, cache.getCacheExpired());
    assertEquals(10, cache.getSize());

    for (int i = 0; i < 10; i++) {
      Object o = cache.get(new Integer(i));
      assertNotNull(o);
    }

    Thread.sleep(cache.getDuration() * 1000 + 1000);

    assertEquals(0, cache.getCacheExpired());
    assertEquals(10, cache.getSize());

    for (int i = 0; i < 10; i++) {
      Object o = cache.get(new Integer(i));
      assertNull(o);
    }

    assertEquals(10, cache.getCacheExpired());
    assertEquals(0, cache.getSize());
  }

  public void testHitsMisses() throws Exception {
    MemoryCache cache = new MemoryCache("region", 0, 0);
    for (int i = 0; i < 10; i++) {
      cache.put(new Integer(i), new Integer(i));
    }
    assertEquals(0, cache.getCacheHits());
    assertEquals(0, cache.getCacheMisses());
    assertEquals(0, cache.getCacheExpired());
    assertEquals(10, cache.getSize());

    assertNotNull(cache.get(new Integer(0)));
    assertNotNull(cache.get(new Integer(1)));
    assertEquals(2, cache.getCacheHits());
    assertEquals(0, cache.getCacheMisses());

    assertNull(cache.get(new Integer(15)));
    assertNull(cache.get(new Integer(20)));
    assertNull(cache.get(new Integer(25)));
    assertEquals(2, cache.getCacheHits());
    assertEquals(3, cache.getCacheMisses());
  }

  public void testClear() throws Exception {
    MemoryCache cache = new MemoryCache("region", 0, 0);
    for (int i = 0; i < 10; i++) {
      cache.put(new Integer(i), new Integer(i));
    }
    assertEquals(0, cache.getCacheHits());
    assertEquals(0, cache.getCacheMisses());
    assertEquals(0, cache.getCacheExpired());
    assertEquals(10, cache.getSize());

    assertNotNull(cache.get(new Integer(0)));
    assertNull(cache.get(new Integer(15)));
    assertEquals(1, cache.getCacheHits());
    assertEquals(1, cache.getCacheMisses());

    cache.clear();

    assertEquals(0, cache.getCacheHits());
    assertEquals(0, cache.getCacheMisses());
    assertEquals(0, cache.getSize());
  }

  public void testHitRate() throws Exception {
    MemoryCache cache = new MemoryCache("region", 0, 0);
    for (int i = 0; i < 10; i++) {
      cache.put(new Integer(i), new Integer(i));
    }
    assertEquals(0, cache.getCacheHits());
    assertEquals(0, cache.getCacheMisses());
    assertEquals(0, cache.getCacheExpired());
    assertEquals(10, cache.getSize());
    assertEquals(0, cache.getHitRate());
    
    cache.get(new Integer(0));
    assertEquals(100, cache.getHitRate());
    assertEquals(1, cache.getCacheHits());

    cache.get(new Integer(20));
    assertEquals(50, cache.getHitRate());
    assertEquals(1, cache.getCacheHits());
    assertEquals(1, cache.getCacheMisses());

    cache.get(new Integer(20));
    assertEquals(33, cache.getHitRate());
    assertEquals(1, cache.getCacheHits());
    assertEquals(2, cache.getCacheMisses());
  }
}