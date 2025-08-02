package org.hibernate.ejb.packaging;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

import org.junit.BeforeClass;
import org.junit.Test;

import de.soptim.usis.test.core.Configuration;

/**
 * Unit-Tests against a hibernate bug which will be occurred by "jar-in-jars".
 * 
 * @author sith
 * @created 17.04.2012
 * @project USIS
 * @filename JarVisitorFactoryTest.java
 * @package org.hibernate.ejb.packaging
 * @version PMS 7.1.0
 *
 */
public class JarVisitorFactoryTest
{   
  
  @BeforeClass
  public static void setUpBeforeClass() throws Exception
  {
    Configuration.initTestLoggingOnly();
  }

  @Test
  public void testGetJarURLFromURLEntryJarInJarFile() throws IllegalArgumentException, MalformedURLException
  {        
    URL objUrl = JarVisitorFactory.getJarURLFromURLEntry(new URL("rsrc", 
                                                                 " ", 50,
                                                                 "META-INF/persistence.xml", new URLStreamHandler()
                                                                 {                                                                  
                                                                   @Override
                                                                   protected URLConnection openConnection(URL u) throws IOException
                                                                   {
                                                                     return null;
                                                                   }
                                                                 }), "META-INF/persistence.xml"); 
    
    assertNotNull(objUrl);
  }

  
  @Test
  public void testGetJarURLFromURLEntryJarFile() throws IllegalArgumentException, MalformedURLException
  {
    URL objUrl = JarVisitorFactory.getJarURLFromURLEntry(new URL("jar:file:/C:/Users/sith/Desktop/Executables/test2/usis.jar!/META-INF/persistence.xml"), "META-INF/persistence.xml"); 
    
    assertNotNull(objUrl);
  }
}
