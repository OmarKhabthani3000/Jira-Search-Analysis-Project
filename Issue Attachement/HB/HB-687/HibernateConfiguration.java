/*
 * Created on Feb 4, 2004
 *
 * cvs: $Id: HibernateConfiguration.java,v 1.2 2004/02/06 00:32:58 eepstein Exp $
 */
package com.publishworks.hibernate;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.log4j.Logger; 

import net.sf.hibernate.MappingException;
import net.sf.hibernate.cfg.Configuration;

/**
 * InterSight
 * 
 * @author eepstein
 *
 */
public class HibernateConfiguration extends Configuration {

	private static Logger log = Logger.getLogger(HibernateConfiguration.class); 


	/**
	 * Takes mapping files from a ZipInputStream.  
	 * This method complements the existing addJar() method on its superclass.
	 * 
	 * @param zInputStream an open ZipInputStream.
	 * @return this Configuration instance. 
	 * @throws MappingException
	 */
	public Configuration addZipInputStream(ZipInputStream zInputStream) throws MappingException {
		ZipEntry ze = null;
		ZipEntryInputStream zeis = new ZipEntryInputStream(zInputStream);
		
		try {
			while( (ze = zInputStream.getNextEntry()) != null ) {
				if( ze.getName().endsWith(".hbm.xml") ) {
					log.info( "Found mapping documents in Zip stream: " + ze.getName() );
					try {
						// We use the zInputStream itself.
						//  The caveat is whether the reader (SAXReader) makes use
						//  of the available() method for actual byte counts... 
						//  this is something ZipInputStream does not provide.
						addInputStream( zeis );
					}
					catch (MappingException me) {
						throw me;
					}
				}
			}
			zInputStream.close();
		} catch (IOException e) {
			log.error("Could not configure datastore from zip file", e);
			throw new MappingException(e);			
		}

		return this;
	}


	/**
	 * Opens a resource as a Zip (or Jar) archive and uses its contents to add mappings to the configuration.
	 * @param path : the path to a Zip or Jar resource.
	 * @param classLoader
	 * @return this Configuration instance.
	 * @throws MappingException
	 */
	public Configuration addZipResource(String path, ClassLoader classLoader) throws MappingException
	{
		InputStream rsrc = classLoader.getResourceAsStream(path);
		if (rsrc==null) throw new MappingException("Resource: " + path + " not found");
		
		ZipInputStream jRsrc = new ZipInputStream(rsrc);

		try {
			return addZipInputStream(jRsrc);
		} 
		catch (MappingException me) {
			throw new MappingException("Error reading resource: " + path, me);
		}
	}


	/**
	 * Override the deprecated addJar() method.
	 * Ask Hibernate developers to un-deprecate this method.
	 */
	public Configuration addJar(String resource) throws MappingException
	{
		log.info("Mapping resource: " + resource);
		
		return addZipResource(resource, Thread.currentThread().getContextClassLoader());
	}
	
}
