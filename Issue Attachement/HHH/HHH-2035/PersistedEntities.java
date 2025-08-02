package org.hibernate.stat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.engine.TwoPhaseLoad;
import org.hibernate.persister.entity.EntityPersister;

/**
 * Holds all persisted entities.
 * 
 *
 */
public class PersistedEntities {
	private static final Log log = LogFactory.getLog(PersistedEntities.class);

	private final static Map map = new HashMap();
	

	public final static void register(final EntityPersister persister) {
		final String entityName = persister.getClassMetadata().getEntityName();
		
		if(map.get(entityName) == null) {
			log.info("Registering PersistedEntity " + entityName);
			map.put(entityName, entityName);
		}
	}

	/**
	 * Log used persisters (entity names) on INFO level.
	 *
	 */
	public final static void logUsedPersisters() {
		log.info("Used persisters: ");

		for (final Iterator it = map.keySet()
				.iterator(); it.hasNext();) {
			final String entityName = (String) it.next();
			log.info("  " + entityName);
		}
	}

	public final static String usedPersistersToXml(final String indent)
			throws IOException {

		final StringBuffer sb = new StringBuffer("");

		// e.g. <mapping class="mypackage.MyClass"/>
		for (final Iterator it = map.keySet()
				.iterator(); it.hasNext();) {
			final String entityName = (String) it.next();
			log.info("Persisted Entity: " + entityName);

			sb.append(indent);
			sb.append("<mapping class=\"");
			sb.append(entityName);
			sb.append("\"/>");
			sb.append("\n");
		}

		final String xml = sb.toString();
		log.info("Mappings: " + xml);
		return xml;
	}

	/**
	 * Convenience method for @link #writeUsedPersisters(File, File, String) 
	 * using an indentation of 4 spaces.
	 * 
	 * @param templateFile
	 * @param outputFile
	 * @throws IOException
	 */
	public final static void writeUsedPersisters(final File templateFile,
			final File outputFile) throws IOException {
		writeUsedPersisters(templateFile, outputFile, "    ");
	}

	/**
	 * Create a hibernate configuration file with mappings for all used
	 * persisters (all persisted entities). A template file is given 
	 * to provide the needed XML to complete the hibernate cfg file.
	 * 
	 * <p/>
	 * 
	 * Each line in the template file which equals "$ClassMappings$" 
	 * (trimmed) will be replaced with the actual class mappings.
	 * 
	 * <p/>
	 * 
	 * Only the class mappings will be added to the XML document, not the
	 * column mappings.
	 * 
	 * @param templateFile
	 * @param outputFile
	 * @param indent
	 * @throws IOException
	 */
	public final static void writeUsedPersisters(final File templateFile,
			final File outputFile, final String indent) throws IOException {
		FileOutputStream fos = null;
		PrintWriter pw = null;

		FileInputStream fis = null;
		InputStreamReader isr = null;
		BufferedReader br = null;

		try {
			// template
			fis = new FileInputStream(templateFile);
			isr = new InputStreamReader(fis);
			br = new BufferedReader(isr);

			// output
			fos = new FileOutputStream(outputFile);
			pw = new PrintWriter(fos);

			String line;
			while ((line = br.readLine()) != null) {
				if (line.trim().equals("$ClassMappings$")) {
					pw.println(usedPersistersToXml(indent));
				} else {
					pw.println(line);
				}
			}

		} finally {
			if (br != null) {
				br.close();
			}

			if (isr != null) {
				isr.close();
			}

			if (fis != null) {
				fis.close();
			}

			if (pw != null) {
				pw.close();
			}

			if (fos != null) {
				fos.close();
			}
		}
	}
}
