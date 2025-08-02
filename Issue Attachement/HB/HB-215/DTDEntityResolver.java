//$Id: DTDEntityResolver.java,v 1.4 2003/02/23 13:47:18 oneovthafew Exp $
//Contributed by Markus Meissner
package net.sf.hibernate.util;

import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

public class DTDEntityResolver implements EntityResolver {
	
	Log log = LogFactory.getLog(DTDEntityResolver.class);
	
	private final ClassLoader loader ;

	public DTDEntityResolver(ClassLoader loader) {
		super() ;
		this.loader = loader ;
	}

	private static final String URL = "http://hibernate.sourceforge.net/";
	private static final String FILE = "file:///" ;

	public InputSource resolveEntity (String publicId, String systemId) {
		if ( systemId!=null) {
			if (systemId.startsWith(URL) ) {
				log.debug("trying to locate " + systemId + " in classpath under net/sf/hibernate/");
				// Search for DTD
				ClassLoader classLoader = this.getClass().getClassLoader();
				InputStream dtdStream = classLoader.getResourceAsStream( "net/sf/hibernate/" + systemId.substring( URL.length() ) );
				if (dtdStream==null) {
					log.debug(systemId + "not found in classpath");
					return null;
				}
				else {
					log.debug("found " + systemId + " in classpath");
					InputSource source = new InputSource(dtdStream);
					source.setPublicId(publicId);
					source.setSystemId(systemId);
					return source;
				}
			}
			else if (systemId.startsWith(FILE))
			{
				InputStream childStream = loader.getResourceAsStream(systemId.substring(FILE.length())) ;
				if (childStream == null)
					return null ;
				else {
					log.debug("found " + systemId + " from classLoader");
					InputSource source = new InputSource(childStream);
					source.setPublicId(publicId);
					source.setSystemId(systemId);
					return source;
				}
			} else
				return null ;
		} else {
			// use the default behaviour
			return null;
		}
	}
	
}







