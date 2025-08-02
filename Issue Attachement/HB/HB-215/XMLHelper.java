//$Id: XMLHelper.java,v 1.6 2003/06/15 12:45:08 oneovthafew Exp $
package net.sf.hibernate.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.io.DOMReader;
import org.dom4j.io.SAXReader;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

public final class XMLHelper {
	
	public static SAXReader createSAXReader(String file, ClassLoader cl) {
		SAXReader reader = new SAXReader();
		reader.setEntityResolver(new DTDEntityResolver(cl));
		reader.setErrorHandler( new ErrorLogger(file) );
		reader.setMergeAdjacentText(true);
		reader.setValidation(true);
		return reader;
	}
	
	public static SAXReader createSAXReader(String file) {
		SAXReader reader = new SAXReader();
		reader.setEntityResolver(DTD_RESOLVER);
		reader.setErrorHandler( new ErrorLogger(file) );
		reader.setMergeAdjacentText(true);
		reader.setValidation(true);
		return reader;
	}
	

	public static DOMReader createDOMReader() {
		DOMReader reader = new DOMReader();
		return reader;
	}
	
	private static final Log log = LogFactory.getLog(XMLHelper.class);
	private static final EntityResolver DTD_RESOLVER = new DTDEntityResolver(XMLHelper.class.getClassLoader());
	
	public static class ErrorLogger implements ErrorHandler {
		private String file;
		ErrorLogger(String file) {
			this.file=file;	
		}
		public void error(SAXParseException error) {
			log.error( "Error parsing XML: " + file + '(' + error.getLineNumber() + ')', error );
		}
		public void fatalError(SAXParseException error) {
			error(error);
		}
		public void warning(SAXParseException error) {
			log.warn( "Warning parsing XML: " + file + '(' + error.getLineNumber() + ')', error );
		}
	};
	
	private XMLHelper() { //cannot be instantiated
	}
	
}






