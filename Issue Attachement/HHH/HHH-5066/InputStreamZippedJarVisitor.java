//$Id: InputStreamZippedJarVisitor.java 14672 2008-05-17 12:50:57Z epbernard $
package org.hibernate.ejb.packaging;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.zip.ZipEntry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Work on a JAR that can only be accessed through a inputstream
 * This is less efficient than the {@link FileZippedJarVisitor}
 *
 * @author Emmanuel Bernard
 */
public class InputStreamZippedJarVisitor extends AbstractJarVisitor {
	private final Logger log = LoggerFactory.getLogger( InputStreamZippedJarVisitor.class );
	private String entry;

	public InputStreamZippedJarVisitor(URL url, Filter[] filters, String entry) {
		super( url, filters );
		this.entry = entry;
	}

	public InputStreamZippedJarVisitor(String fileName, Filter[] filters) {
		super( fileName, filters );
	}

        /*jis = null;

        String url = jarUrl.toString();
        if( url.startsWith("http:") ) {
            try {
                // IOException("no entry name specified")
                URL urlJarCheck = new URL("jar:" + url + "!/");
                JarURLConnection jarConnection = (JarURLConnection)urlJarCheck.openConnection();
                JarFile jar = jarConnection.getJarFile();
                //jis = (JarInputStream) jar.getInputStream( new JarEntry("/") );
                jis = new JarInputStream( jarConnection.getInputStream() );
                //jis = (JarInputStream) jar.getInputStream( jarConnection.getJarEntry() );
                //InputStream is = new
                //jis = new JarInputStream( new FileInputStream(jar.) );
                InputStream is = Thread.currentThread().getContextClassLoader().
                        getResourceAsStream(urlJarCheck);
                jis = new JarInputStream(is);
            }
            catch( Exception ex ) {
                jis = null;
            }
        }

        jis = new JarInputStream( jarUrl.openStream() );*/

	protected void doProcessElements() throws IOException {

            JarEntriesEnumerator jarEnum;
            try {
                jarEnum = new JarEntriesEnumerator(jarUrl);
            } catch (Exception ze) {
                //really should catch IOException but Eclipse is buggy and raise NPE...
                log.warn("Unable to find file (ignored): " + jarUrl, ze);
                return;
            }
            if (entry != null && entry.length() == 1) {
                entry = null; //no entry
            }
            if (entry != null && entry.startsWith("/")) {
                entry = entry.substring(1); //remove '/' header
            }
            JarEntry jarEntry;
            //while ( ( jarEntry = jis.getNextJarEntry() ) != null ) {
            while ( jarEnum.hasMoreElements() ) {

                jarEntry = jarEnum.nextElement();

                String name = jarEntry.getName();
                if (entry != null && !name.startsWith(entry)) {
                    continue; //filter it out
                }
                if (!jarEntry.isDirectory()) {
                    if (name.equals(entry)) {
                        //exact match, might be a nested jar entry (ie from jar:file:..../foo.ear!/bar.jar)
			/*
                         * This algorithm assumes that the zipped file is only the URL root (including entry), not just any random entry
                         */
                        JarInputStream subJis = null;
                        try {
                            JarInputStream jis = new JarInputStream( jarEnum.getCurrentInputStream() );
                            subJis = new JarInputStream(jis);
                            ZipEntry subZipEntry = subJis.getNextEntry();
                            while (subZipEntry != null) {
                                if (!subZipEntry.isDirectory()) {
                                    //FIXME copy sucks
                                    //byte[] entryBytes = JarVisitorFactory.getBytesFromInputStream(jis);
                                    byte[] entryBytes = JarVisitorFactory.getBytesFromInputStream( subJis );
                                    String subname = subZipEntry.getName();
                                    if (subname.startsWith("/")) {
                                        subname = subname.substring(1);
                                    }
                                    addElement(
                                            subname,
                                            new ByteArrayInputStream(entryBytes),
                                            new ByteArrayInputStream(entryBytes));
                                }
                                subZipEntry = subJis.getNextJarEntry();
                            }
                        } finally {
                            if (subJis != null) {
                                subJis.close();
                            }
                        }
                    } else {
                        //byte[] entryBytes = JarVisitorFactory.getBytesFromInputStream(jis);
                        byte[] entryBytes = JarVisitorFactory.getBytesFromInputStream( jarEnum.getCurrentInputStream() );
                        //build relative name
                        if (entry != null) {
                            name = name.substring(entry.length());
                        }
                        if (name.startsWith("/")) {
                            name = name.substring(1);
                        }
                        //this is bad cause we actually read everything instead of walking it lazily
                        addElement(
                                name,
                                new ByteArrayInputStream(entryBytes),
                                new ByteArrayInputStream(entryBytes));
                        jarEnum.closeCurrentStream();
                    }
                }
            }

            jarEnum.closeStreams();
            
            //jis.close();
	}
        
//    protected void doProcessElements() throws IOException {
//
//        JarInputStream jis;
//        try {
//            jis = new JarInputStream(jarUrl.openStream());
//        } catch (Exception ze) {
//            //really should catch IOException but Eclipse is buggy and raise NPE...
//            log.warn("Unable to find file (ignored): " + jarUrl, ze);
//            return;
//        }
//        if (entry != null && entry.length() == 1) {
//            entry = null; //no entry
//        }
//        if (entry != null && entry.startsWith("/")) {
//            entry = entry.substring(1); //remove '/' header
//        }
//        JarEntry jarEntry;
//        while ((jarEntry = jis.getNextJarEntry()) != null) {
//            String name = jarEntry.getName();
//            if (entry != null && !name.startsWith(entry)) {
//                continue; //filter it out
//            }
//            if (!jarEntry.isDirectory()) {
//                if (name.equals(entry)) {
//                    //exact match, might be a nested jar entry (ie from jar:file:..../foo.ear!/bar.jar)
//					/*
//                     * This algorithm assumes that the zipped file is only the URL root (including entry), not just any random entry
//                     */
//                    JarInputStream subJis = null;
//                    try {
//                        subJis = new JarInputStream(jis);
//                        ZipEntry subZipEntry = jis.getNextEntry();
//                        while (subZipEntry != null) {
//                            if (!subZipEntry.isDirectory()) {
//                                //FIXME copy sucks
//                                byte[] entryBytes = JarVisitorFactory.getBytesFromInputStream(jis);
//                                String subname = subZipEntry.getName();
//                                if (subname.startsWith("/")) {
//                                    subname = subname.substring(1);
//                                }
//                                addElement(
//                                        subname,
//                                        new ByteArrayInputStream(entryBytes),
//                                        new ByteArrayInputStream(entryBytes));
//                            }
//                            subZipEntry = jis.getNextJarEntry();
//                        }
//                    } finally {
//                        if (subJis != null) {
//                            subJis.close();
//                        }
//                    }
//                } else {
//                    byte[] entryBytes = JarVisitorFactory.getBytesFromInputStream(jis);
//                    //build relative name
//                    if (entry != null) {
//                        name = name.substring(entry.length());
//                    }
//                    if (name.startsWith("/")) {
//                        name = name.substring(1);
//                    }
//                    //this is bad cause we actually read everything instead of walking it lazily
//                    addElement(
//                            name,
//                            new ByteArrayInputStream(entryBytes),
//                            new ByteArrayInputStream(entryBytes));
//                }
//            }
//        }
//        jis.close();
//    }
//
    /** Jar entries to avoid java web start download problems. */
    private static class JarEntriesEnumerator {

        /** Input stream if its a local jar. null otherwise. */
        private JarInputStream jis;

        private JarFile jar;

        /** Enumeration for java web start jar file. null if we are not on java web start. */
        private Enumeration<JarEntry> jarFiles;

        /** Next JAR entry to return, if jis != null. */
        private JarEntry nextEntry;

        /** Last entry returned. */
        private JarEntry currentEntry;

        /** input stream for the last entry returned. */
        private InputStream currentStream;

        public JarEntriesEnumerator(URL jarUrl) throws IOException {

            String url = jarUrl.toString();
            if (url.startsWith("http:")) {
                // Check if we are on java web start:
                try {
                    URL urlJarCheck = new URL("jar:" + url + "!/");
                    JarURLConnection jarConnection = (JarURLConnection) urlJarCheck.openConnection();
                    jar = jarConnection.getJarFile();
                    jarFiles = jar.entries();
                } catch (Exception ex) {
                    jarFiles = null;
                }
            }

            if (jarFiles == null)
                jis = new JarInputStream(jarUrl.openStream());

        }

        public boolean hasMoreElements() throws IOException {

            if (jarFiles != null)
                return jarFiles.hasMoreElements();
            else {
                nextEntry = jis.getNextJarEntry();
                return nextEntry != null;
            }

        }

        public JarEntry nextElement() throws IOException {

            if (jarFiles != null) {
                currentEntry = jarFiles.nextElement();
                currentStream =jar.getInputStream(currentEntry);
            }
            else
                currentEntry = nextEntry;

            return currentEntry;
        }

        public InputStream getCurrentInputStream() throws IOException {
            if( jarFiles != null )
                return currentStream;
            else
                return jis;
        }

        public void closeCurrentStream() throws IOException {
            if( jarFiles != null && currentStream != null )
                currentStream.close();
        }

        public void closeStreams() throws IOException {
            if( jarFiles == null )
                jis.close();
        }
    }
}
