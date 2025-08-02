package cirrus.hibernate.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;

import cirrus.hibernate.Datastore;
import cirrus.hibernate.Hibernate;
import cirrus.hibernate.HibernateException;
import cirrus.hibernate.MappingException;
import cirrus.hibernate.tools.SchemaExport;

/**
 * 
 * An Ant Task to provide a better interface to the SchemaExport than the java command line
 * 
 * supports nested &gt;fileset$lt; tags
 * 
 * Designed to use the Hibernate pre version 2 <tt>cirrus.hibernate.tools.SchemaExport</tt>.
 * 
 * @author <a href="mailto:cameron@datacodex.net">cameronbraid</a>
 * 
 */
public class SchemaExportTask extends Task
{
	/** input mapping files */
	private List fileSets = new ArrayList();

	/** the name of the hibernate properties file to use */
	private File propertiesFile;

	/** the name of the output file for ddl statements */
	private String outputFile;

	/** print sql commands to Standard out */
	private boolean quiet = true;

	/** print out DDL only.. or export if false */
	private boolean text = true;

	/** only drop the tables, don't create them */
	private boolean drop;

	/** pretty print the sql ddl, default is true */
	private boolean formatSQL=true;

	/** the batch delimiter */
	private String delimiter;

	/** add a FileSet to the list of file sets */
	public void addFileset(FileSet set) {
		fileSets.add(set);
	}

	public void setQuiet(boolean quiet)
	{
		this.quiet = quiet;
	}

	public void setDrop(String drop)
	{
		this.drop = Boolean.valueOf(drop).booleanValue();
	}

	public void setText(boolean text)
	{
		this.text = text;
	}

	public void setOutput(String output)
	{
		this.outputFile = output;
	}

	public void setFormat(String format)
	{
		this.formatSQL = Boolean.valueOf(format).booleanValue();
	}
	public void setDelimiter(String delimiter)
	{
		this.delimiter = delimiter;
	}

		/** set the property file to use for configuring the Datastore */
	public void setProperties(File propertiesFile)
	{
		if (!propertiesFile.exists()) {
			throw new BuildException("Properties file: " + propertiesFile + " does not exist.");
		}
		this.propertiesFile = propertiesFile;
	}

	private String[] getFiles() {
	
		List files = new LinkedList();
		for ( Iterator i = fileSets.iterator(); i.hasNext(); ) {
    	
			FileSet fs = (FileSet) i.next();
			DirectoryScanner ds = fs.getDirectoryScanner(project);

			String[] dsFiles = ds.getIncludedFiles();
			for (int j = 0; j < dsFiles.length; j++) {
				File f = new File(dsFiles[j]);
				if ( !f.isFile() ) {
					f = new File( ds.getBasedir(), dsFiles[j] );
				}

				files.add( f.getAbsolutePath() );
			}
		}

		return (String[]) files.toArray( new String[0] );
	}

	private Datastore getDatastore() throws MappingException
	{
		Datastore ds = Hibernate.createDatastore(); 

		// add all of the mapping files to the data store
		String[] files = getFiles();
		for (int i = 0; i < files.length; i++) {
			String filename = files[i];
			System.out.println("Adding mapping " + filename);
			if (filename.endsWith(".jar"))
			{
				ds.storeJar(filename);
			}
			else
			{
				ds.storeFile(filename);
			}
		}

		return ds;
	}


	public void execute() throws BuildException {
		try {
			Datastore ds = getDatastore();
			SchemaExport schemaExport = getSchemaExport(ds);

			if (drop) {
				schemaExport.drop(!quiet, !text, formatSQL, delimiter);
			} 
			else {
				schemaExport.create(!quiet, !text, formatSQL, delimiter);
			}
		} 
		catch (HibernateException e) {
			throw new BuildException("Schema text failed: " + e.getMessage(), e);
		} 
		catch (FileNotFoundException e) {
			throw new BuildException("File not found: " + e.getMessage(), e);
		} 
		catch (IOException e) {
			throw new BuildException("IOException : " + e.getMessage(), e);
		}
	}

	private SchemaExport getSchemaExport(Datastore ds) throws HibernateException, IOException {
		SchemaExport schemaExport = null;
		if (propertiesFile == null) {
			schemaExport = new SchemaExport(ds);
		} 
		else {
			Properties properties = new Properties();
			properties.load( new FileInputStream(propertiesFile) );
			schemaExport = new SchemaExport(ds, properties);
		}
		schemaExport.setOutputFile(outputFile);
		return schemaExport;
	}

}
