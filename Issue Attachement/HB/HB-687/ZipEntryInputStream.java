/*
 * Created on Feb 4, 2004
 *
 * cvs: $Id: ZipEntryInputStream.java,v 1.1 2004/02/05 02:14:32 eepstein Exp $
 */
package com.publishworks.hibernate;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipInputStream;

/**
 * InterSight
 * 
 * @author eepstein
 *
 */
public class ZipEntryInputStream extends InputStream {

	private ZipInputStream zipIS;

	/**
	 * Wraps a ZipInputStream.  
	 * Allows a single entry to be read from the wrapped stream 
	 * without closing that stream when the read is complete.
	 */
	public ZipEntryInputStream(ZipInputStream zIS) {
		super();		
		zipIS = zIS;
	}

	/**
	 * Gets the wrapped ZipInputStream.
	 * @return The ZipInputStream wrapped by this instance.
	 */
	public ZipInputStream getWrappedStream(){
		return zipIS;
	}


	/**
	 * Invokes read() on the wrapped ZipInputStream.
	 * 
	 * @see java.io.InputStream#read()
	 */
	public int read() throws IOException {
		return zipIS.read();
	}


	/**
	 * Invokes the corresponding method on the wrapped ZipInputStream.
	 */
	public int read(byte b[]) throws IOException {
		return zipIS.read(b);
	}


	/**
	 * Invokes the corresponding method on the wrapped ZipInputStream.
	 */
	public int read(byte b[], int off, int len) throws IOException {
		return zipIS.read(b, off, len);	
	}


	/**
	 * Invokes the corresponding method on the wrapped ZipInputStream.
	 */
	public long skip(long n) throws IOException {
		return zipIS.skip(n);
	}


	/**
	 * Invokes the corresponding method on the wrapped ZipInputStream.
	 */
	public int available() throws IOException {
		return zipIS.available();
	}


	/**
	 * Invokes closeEntry() on the wrapped ZipInputStream.  
	 */
	public void close() throws IOException {
		zipIS.closeEntry();
	}


	/**
	 * Invokes the corresponding method on the wrapped ZipInputStream.
	 */
	public synchronized void mark(int readlimit) 
	{
		zipIS.mark(readlimit);
	}


	/**
	 * Invokes the corresponding method on the wrapped ZipInputStream.
	 */
	public synchronized void reset() throws IOException {
		zipIS.reset();
	}


	/**
	 * Invokes the corresponding method on the wrapped ZipInputStream.
	 */
	public boolean markSupported() {
		return zipIS.markSupported();
	}

}
