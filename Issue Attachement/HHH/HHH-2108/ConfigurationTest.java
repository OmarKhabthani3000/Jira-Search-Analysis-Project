package org.hibernate.test.configuration;

import java.io.File;
import java.net.URL;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

import org.hibernate.InvalidMappingException;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;

/**
 * @author Courtney Arnold
 * 
 */
public class ConfigurationTest extends TestCase {

	/**
	 * The name and location of the Cacheable configuration file
	 */
	public final String CACHEABLE_FILE = "/org/hibernate/test/configuration"
			+ "/Configuration.hbm.xml";

	/**
	 * Configuration object reference used by the test cases.
	 */
	private Configuration testConfiguration;

	/**
	 * Reference to this classes Class object.
	 */
	private Class currentClass = this.getClass();

	public ConfigurationTest(String name) {

		super(name);
	}

	protected void setUp() throws Exception {

		super.setUp();

		// Remove cached files, If they exist
		URL cachedFile = currentClass.getResource(CACHEABLE_FILE + ".bin");
		if (cachedFile != null) {

			new File(cachedFile.getFile()).delete();
		}
	}

	/**
	 * Tests whether the cacheable file will load and the cached file is created
	 * as a result of loading the cacheable file.
	 * 
	 */
	public void testLoadCacheableFileWithoutCache() {

		// Load CacheableFile, Cached file does not exist and Loading it should
		// auto create the cached file.
		try {
			testConfiguration = new Configuration();
			testConfiguration.setProperty(Environment.CACHE_PROVIDER,
					"org.hibernate.cache.HashtableCacheProvider");
			testConfiguration.addCacheableFile(currentClass.getResource(
					CACHEABLE_FILE).getFile());
		} catch (InvalidMappingException e) {

			fail("Cacheable file was not found!");
		}

		assertNotNull("Cached file was not created!", currentClass
				.getResource(CACHEABLE_FILE + ".bin"));
	}

	/**
	 * Tests whether the cached file for the cacheable file will load.
	 * 
	 * This test depends on the successfull test of
	 * testLoadCacheableFileWithoutCache().
	 */
	public void testLoadCacheableFileWithCache() {

		// This test depends on the testLoadCacheableFileWithoutCache
		// functionality works properly.
		try {

			testLoadCacheableFileWithoutCache();
		} catch (AssertionFailedError e) {

			fail("This test requires that testLoadCacheableFileWithoutCache()"
					+ " does not fail.");
		}

		// Load CacheableFile, Cached file does exist and will load the cached
		// file.
		try {
			testConfiguration = new Configuration();
			testConfiguration.setProperty(Environment.CACHE_PROVIDER,
					"org.hibernate.cache.HashtableCacheProvider");
			testConfiguration.addCacheableFile(currentClass.getResource(
					CACHEABLE_FILE).getFile());
		} catch (InvalidMappingException e) {

			fail("InvalidMappingException should not be thrown!");
		}
	}

	protected void tearDown() throws Exception {

		super.tearDown();

		// Remove cached files
		URL cachedFile = currentClass.getResource(CACHEABLE_FILE + ".bin");
		if (cachedFile != null) {

			new File(cachedFile.getFile()).delete();
		}
	}
}
