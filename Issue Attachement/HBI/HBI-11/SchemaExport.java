//$Id: SchemaExport.java,v 1.15 2003/01/17 23:53:29 oneovthafew Exp $
package cirrus.hibernate.tools;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.StringTokenizer;

import cirrus.hibernate.Datastore;
import cirrus.hibernate.Environment;
import cirrus.hibernate.Hibernate;
import cirrus.hibernate.HibernateException;
import cirrus.hibernate.connection.ConnectionProvider;
import cirrus.hibernate.connection.ConnectionProviderFactory;
import cirrus.hibernate.helpers.JDBCExceptionReporter;
import cirrus.hibernate.sql.Dialect;

/**
 * Commandline tool to export table schema for a configured Datastore to the database.
 */

public class SchemaExport  {
	
	private String[] dropSQL;
	private String[] createSQL;
	private Properties connectionProperties;
	private String outputFile = null;
	
	/**
	 * Create a schema exporter for the given Datastore.
	 */
	public SchemaExport(Datastore ds) throws HibernateException {
		connectionProperties = Environment.getProperties();
		Dialect dialect = Dialect.getDialect();
		dropSQL = ds.generateDropSchemaScript(dialect);
		createSQL = ds.generateSchemaCreationScript(dialect);
	}
	
	/**
	 * Create a schema exporter for the given Datastore, with the given
	 * database connection properties.
	 */
	public SchemaExport(Datastore ds, Properties connectionProperties) throws HibernateException {
		this.connectionProperties = connectionProperties;
		Dialect dialect = Dialect.getDialect(connectionProperties);
		dropSQL = ds.generateDropSchemaScript(dialect);
		createSQL = ds.generateSchemaCreationScript(dialect);
	}
	
	/**
	 * Set an output filename. The generated script will be written to this file.
	 */
	public SchemaExport setOutputFile(String filename) {
		outputFile = filename;
		return this;
	}
	
	/**
	 * Run the schema creation script.
	 * @param script write the script to standard out
	 * @param export export the script to the database
	 * @param format format the output
	 * @param delimiter String used as a delimiter
	 * @throws HibernateException
	 */	
	public void create(boolean script, boolean export, boolean format, String delimiter) throws HibernateException {
		execute(script, export, false, format, delimiter);
	}

	/**
	 * Run the schema creation script.
	 * @param script write the script to standard out
	 * @param export export the script to the database
	 * @throws HibernateException
	 */	
	public void create(boolean script, boolean export) throws HibernateException {
		execute(script, export, false, true, null);
	}
	
	/**
	 * Run the drop schema script.
	 * @param script write the script to standard out
	 * @param export export the script to the database
	 * @param format format the output
	 * @param delimiter String used as a delimiter
	 * @throws HibernateException
	 */	
	public void drop(boolean script, boolean export, boolean format, String delimiter) throws HibernateException {
		execute(script, export, true, format, delimiter);
	}

	/**
	 * Run the schema creation script.
	 * @param script write the script to standard out
	 * @param export export the script to the database
	 * @throws HibernateException
	 */	
	public void drop(boolean script, boolean export) throws HibernateException {
		execute(script, export, true, true, null);
	}
	
	private void execute(boolean script, boolean export, boolean justDrop, boolean format, String delimiter) throws HibernateException {
		Connection connection = null;
		FileWriter fileOutput = null;
		ConnectionProvider cp = null;
		Statement stmt = null;
		
		Properties givenProps = (connectionProperties==null) ? Environment.getProperties() : connectionProperties;
		Properties props = new Properties();
		props.putAll( Dialect.getDialect(givenProps).getDefaultProperties() );
		props.putAll(givenProps);
		boolean jdbc2 = false;
		
		try {
			if(outputFile != null) {
				fileOutput = new FileWriter(outputFile);				
			}		
			
			if (export) {
				cp = ConnectionProviderFactory.newConnectionProvider(props);
				connection = cp.getConnection();	
				connection.commit(); 
				connection.setAutoCommit(true);
				stmt = connection.createStatement();
			}
			
			for (int i = 0; i < dropSQL.length; i++) {
				try {
					String formatted = dropSQL[i];
					if (delimiter!=null) formatted+=delimiter;
					if (script) System.out.println(formatted);
					if (outputFile != null) fileOutput.write( formatted + "\n" );
					if (export) {
						if (jdbc2) {
							stmt.addBatch( dropSQL[i] );
						}
						else {
							stmt.executeUpdate( dropSQL[i] );				
						}
					}
				}
				catch(SQLException e) {
					if (!script) System.out.println( dropSQL[i] );
					System.out.println( "Unsuccessful: " + e.getMessage() );
				}
				
			}
			
			if (!justDrop) {
				for(int j = 0; j < createSQL.length; j++) {
					try {
						String formatted = format ? format( createSQL[j] ) : createSQL[j];
						if (delimiter!=null) formatted+=delimiter;
						if (script) System.out.println( formatted );
						if (outputFile != null) fileOutput.write( formatted + "\n" );
						if (export) {
							if (jdbc2) {
								stmt.addBatch( createSQL[j] );
							}
							else {
								stmt.executeUpdate( createSQL[j] );
							}
						}
					}
					catch (SQLException e) {
						if (!script) System.out.println( createSQL[j] );
						System.out.println( "Unsuccessful: " + e.getMessage() );
					}
				}
			}
			
			if (jdbc2) stmt.executeBatch();
			
		}
		
		catch(Exception e) {
			e.printStackTrace();
			throw new HibernateException( e.getMessage() );
		}
		
		finally {
			try {
				if (stmt!=null) stmt.close();
				if (connection!=null) {
					JDBCExceptionReporter.logWarnings( connection.getWarnings() );
					connection.clearWarnings();
					cp.closeConnection(connection);
				}
			}
			catch(SQLException e) {
				System.err.println( "Could not close connection: " + e.getMessage() );
			}
			if(fileOutput != null) {
				try {
					fileOutput.close();
				}
				catch(IOException ioe) {
					System.err.println( "Error closing output file " + outputFile +  ": " + ioe.getMessage() );
				}
			}
		}
	}
	
	/**
	 * Format an SQL statement using simple rules:
	 *  a) Insert newline after each comma;
	 *  b) Indent three spaces after each inserted newline;
	 * If the statement contains single/double quotes return unchanged,
	 * it is too complex and could be broken by simple formatting.
	 */
	private static String format(String sql) {
		
		if ( sql.indexOf("\"") > 0 || sql.indexOf("'") > 0) {
		    return sql;
		}
		
		String formatted;
		
		if ( sql.toLowerCase().startsWith("create table") ) {
			
			StringBuffer result = new StringBuffer(60);
			StringTokenizer tokens = new StringTokenizer( sql, "(,)", true );
			
			int depth = 0;
			
			while ( tokens.hasMoreTokens() ) {
				String tok = tokens.nextToken();
				if ( ")".equals(tok) ) {
					depth--;
					if (depth==0) result.append("\n");
				}
				result.append(tok);
				if ( ",".equals(tok) && depth==1 ) result.append("\n  ");
				if ( "(".equals(tok) ) {
					depth++;
					if (depth==1) result.append("\n   ");
				}
			}
			
			formatted = result.toString();
			
		}
		else {
			formatted = sql;
		}
				
	    return formatted;
	}
	
	public static void main(String[] args) {
		try {
			Datastore ds = Hibernate.createDatastore(); 
			
			boolean script = true;
			boolean drop = false;
			boolean export = true;
			String outputFile = null;
			String propFile = null;
			boolean formatSQL = false;
			String delimiter = null;
			
			for ( int i=0; i<args.length; i++ )  {
				if( args[i].startsWith("--") ) {
					if( args[i].equals("--quiet") ) {
						script = false;
					}
					else if( args[i].equals("--drop") ) {
						drop = true;
					}
					else if( args[i].equals("--text") ) {
						export = false;
					}
					else if( args[i].startsWith("--output=") ) {
						outputFile = args[i].substring(9);
					}
					else if( args[i].startsWith("--properties=") ) {
						propFile = args[i].substring(13);
					}
					else if( args[i].equals("--format") ) {
						formatSQL = true;
					}
					else if ( args[i].startsWith("--delimiter=") ) {
						delimiter = args[i].substring(12);
					}
				}
				else {
					String filename = args[i];
					if ( filename.endsWith( ".jar" ) ) {
						ds.storeJar(filename);
					}
					else {
						ds.storeFile(filename);
					}
				}
					
			}
			if(propFile!=null) {			
				Properties props = new Properties();
				props.load( new FileInputStream(propFile) );
				new SchemaExport(ds, props).setOutputFile(outputFile).execute(script, export, drop, formatSQL, delimiter);
			}
			else {
				new SchemaExport(ds).setOutputFile(outputFile).execute(script, export, drop, formatSQL, delimiter);
			}
		}
		catch(Exception e) {
			System.err.println( "Error creating schema " + e.getMessage() );
			e.printStackTrace();
		}
	}
}

