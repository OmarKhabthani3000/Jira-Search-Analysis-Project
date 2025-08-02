//$Id: Configuration.java,v 1.26 2003/06/17 09:28:38 oneovthafew Exp $
package net.sf.hibernate.cfg;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.StringReader;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Attribute;
import org.dom4j.Element;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import net.sf.hibernate.util.ArrayHelper;
import net.sf.hibernate.util.ReflectHelper;
import net.sf.hibernate.util.StringHelper;
import net.sf.hibernate.util.XMLHelper;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Interceptor;
import net.sf.hibernate.MappingException;
import net.sf.hibernate.SessionFactory;
import net.sf.hibernate.tool.hbm2ddl.DatabaseMetadata;
import net.sf.hibernate.tool.hbm2ddl.TableMetadata;
import net.sf.hibernate.type.Type;
import net.sf.hibernate.id.IdentifierGenerator;
import net.sf.hibernate.id.PersistentIdentifierGenerator;
import net.sf.hibernate.impl.SessionFactoryImpl;
import net.sf.hibernate.mapping.Collection;
import net.sf.hibernate.mapping.ForeignKey;
import net.sf.hibernate.mapping.Index;
import net.sf.hibernate.mapping.PersistentClass;
import net.sf.hibernate.mapping.RootClass;
import net.sf.hibernate.mapping.Table;
import net.sf.hibernate.cache.Cache;
import net.sf.hibernate.cache.CacheConcurrencyStrategy;
import net.sf.hibernate.cache.CacheException;
import net.sf.hibernate.cache.JCSCache;
import net.sf.hibernate.cache.NonstrictReadWriteCache;
import net.sf.hibernate.cache.ReadOnlyCache;
import net.sf.hibernate.cache.ReadWriteCache;
import net.sf.hibernate.dialect.Dialect;
import net.sf.hibernate.engine.Mapping;

/**
 * An instance of <tt>Configuration</tt> allows the application
 * to specify properties and mapping documents to be used when
 * creating a <tt>SessionFactory</tt>. Usually an application will create
 * a single <tt>Configuration</tt>, build a single instance of 
 * <tt>SessionFactory</tt> and then instantiate <tt>Session</tt>s in 
 * threads servicing client requests. The <tt>Configuration</tt> is meant 
 * only as an initialization-time object. <tt>SessionFactory</tt>s are
 * immutable and do not retain any association back to the
 * <tt>Configuration</tt>.<br>
 * <br>
 * A new <tt>Configuration</tt> will use the properties specified in
 * <tt>hibernate.properties</tt> by default.
 * 
 * @see net.sf.hibernate.SessionFactory
 * @author Gavin King
 */
public class Configuration {
	
	private HashMap classes = new HashMap();
	private HashMap imports = new HashMap();
	private HashMap collections = new HashMap();
	private HashMap tables = new HashMap();
	private HashMap namedQueries = new HashMap();
	private ArrayList secondPasses = new ArrayList();
	private Interceptor interceptor = EMPTY_INTERCEPTOR;
	private Properties properties = Environment.getProperties();
	
	private static Log log = LogFactory.getLog(Configuration.class);
	
	protected void reset() {
		classes = new HashMap();
		collections = new HashMap();
		tables = new HashMap();
		namedQueries = new HashMap();
		secondPasses = new ArrayList();
		interceptor = EMPTY_INTERCEPTOR;
		properties = Environment.getProperties();
	}
	
	private Mapping mapping = new Mapping() {
		/**
		 * Returns the identifier type of a mapped class
		 */
		public Type getIdentifierType(Class persistentClass) throws MappingException {
			return ( (PersistentClass) classes.get(persistentClass) ).getIdentifier().getType();
		}
	};
	
	public Configuration() {
		reset();
	}
	
	/**
	 * Iterate the class mappings
	 */
	public Iterator getClassMappings() {
		return classes.values().iterator();
	}
	
	/**
	 * Iterate the collection mappings
	 */
	public Iterator getCollectionMappings() {
		return collections.values().iterator();
	}
	
	/**
	 * Iterate the table mappings
	 */
	private Iterator getTableMappings() {
		return tables.values().iterator();
	}
	
	/**
	 * Get the mapping for a particular class
	 */
	public PersistentClass getClassMapping(Class persistentClass) {
		return (PersistentClass) classes.get(persistentClass);
	}
	
	/**
	 * Get the mapping for a particular collection role
	 * @param role a collection role
	 * @return Collection
	 */
	public Collection getCollectionMapping(String role) {
		return (Collection) collections.get(role);
	}
	
	/**
	 * Read mappings from a particular XML file
	 * @param xmlFile a path to a file
	 */
	public Configuration addFile(String xmlFile) throws MappingException {
		log.info("Mapping file: " + xmlFile);
		try {
			add( XMLHelper.createSAXReader(xmlFile).read( new File(xmlFile) ) );
		}
		catch (Exception e) {
			log.error("Could not configure datastore from file: " + xmlFile, e);
			throw new MappingException(e);
		}
		return this;
	}
	
	/**
	 * Read mappings from a particular XML file
	 * @param xmlFile a path to a file
	 */
	public Configuration addFile(File xmlFile) throws MappingException {
		log.info( "Mapping file: " + xmlFile.getPath() );
		try {
			addInputStream( new FileInputStream(xmlFile) );
		}
		catch (Exception e) {
			log.error("Could not configure datastore from file: " + xmlFile.getPath(), e);
			throw new MappingException(e);
		}
		return this;
	}
	
	/**
	 * Read mappings from a <tt>String</tt>
	 * @param xml an XML string
	 */
	public Configuration addXML(String xml) throws MappingException {
		if ( log.isDebugEnabled() ) log.debug("Mapping XML:\n" + xml);
		try {
			add( XMLHelper.createSAXReader("XML String").read( new StringReader(xml) ) );
		}
		catch (Exception e) {
			log.error("Could not configure datastore from XML", e);
			throw new MappingException(e);
		}
		return this;
	}
	
	/**
	 * Read mappings from a <tt>URL</tt>
	 * @param url
	 */
	public Configuration addURL(URL url) throws MappingException {
		if ( log.isDebugEnabled() ) log.debug("Mapping URL:\n" + url);
		try {
			addInputStream( url.openStream() );
		}
		catch (Exception e) {
			log.error("Could not configure datastore from URL", e);
			throw new MappingException(e);
		}
		return this;
	}
	
	/**
	 * Read mappings from a DOM <tt>Document</tt>
	 * @param doc a DOM document
	 */
	public Configuration addDocument(Document doc) throws MappingException {
		if ( log.isDebugEnabled() ) log.debug("Mapping XML:\n" + doc);
		try {
			add( XMLHelper.createDOMReader().read(doc) );
		}
		catch (Exception e) {
			log.error("Could not configure datastore from XML document", e);
			throw new MappingException(e);
		}
		return this;
	}
	
	protected void add(org.dom4j.Document doc) throws Exception {
		try {
			Binder.bindRoot( doc, createMappings() );
		}
		catch (MappingException me) {
			log.error("Could not compile the mapping document", me);
			throw me;
		}
	}
	
	/**
	 * Create a new <tt>Mappings</tt> to add class and collection
	 * mappings to.
	 */
	public Mappings createMappings() {
		return new Mappings(classes, collections, tables, namedQueries, imports, secondPasses);
	}
	
	/**
	 * Read mappings from an <tt>InputStream</tt>
	 * @param xmlInputStream an <tt>InputStream</tt> containing XML
	 */
	public Configuration addInputStream(InputStream xmlInputStream) throws MappingException {
		try {
			add( XMLHelper.createSAXReader("XML InputStream").read( new InputSource(xmlInputStream) ) );
			return this;
		}
		catch (MappingException me) {
			throw me;
		}
		catch (Exception e) {
			log.error("Could not configure datastore from input stream", e);
			throw new MappingException(e);
		}
	}

	/**
	 * Read mappings from an <tt>InputStream</tt>
	 * @param xmlInputStream an <tt>InputStream</tt> containing XML
	 */
	public Configuration addInputStream(InputStream xmlInputStream, ClassLoader src) throws MappingException {
		try {
			add( XMLHelper.createSAXReader("XML InputStream", src).read( new InputSource(xmlInputStream) ) );
			return this;
		}
		catch (MappingException me) {
			throw me;
		}
		catch (Exception e) {
			log.error("Could not configure datastore from input stream", e);
			throw new MappingException(e);
		}
	}
	
	/**
	 * Read mappings from an application resource
	 * @param path a resource
	 * @param classLoader a <tt>ClassLoader</tt> to use
	 */
	public Configuration addResource(String path, ClassLoader classLoader) throws MappingException {
		log.info("Mapping resource: " + path);
		InputStream rsrc = classLoader.getResourceAsStream(path);
		if (rsrc==null) throw new MappingException("Resource: " + path + " not found");
		return addInputStream(rsrc, classLoader);
	}
	
	/**
	 * Read a mapping from an application resource, using a convention.
	 * The class <tt>foo.bar.Foo</tt> is mapped by the file <tt>foo/bar/Foo.hbm.xml</tt>.
	 * @param persistentClass the mapped class
	 */
	public Configuration addClass(Class persistentClass) throws MappingException {
		String fileName = persistentClass.getName().replace(StringHelper.DOT,'/') + ".hbm.xml";
		log.info("Mapping resource: " + fileName);
		ClassLoader cl = persistentClass.getClassLoader() ;
		InputStream rsrc = cl.getResourceAsStream(fileName);
		if (rsrc==null) throw new MappingException("Resource: " + fileName + " not found");
		return addInputStream(rsrc, cl);
	}
	
	/**
	 * Read all mappings from a jar file
	 * @param resource an application resource (a jar file)
	 */
	public Configuration addJar(String resource) throws MappingException {
		
		log.info("Searching for mapping documents in jar: " + resource);
		
		final JarFile jarFile;
		final ClassLoader cl ;
		try {
			cl = Thread.currentThread().getContextClassLoader() ;
			jarFile = new JarFile(
				cl.getResource(resource).getFile()
			);
		}
		catch (IOException ioe) {
			log.error("Could not configure datastore from jar", ioe);
			throw new MappingException(ioe);
		}
		
		if (jarFile==null) throw new MappingException("Resource: " + resource + " not found");
		
		Enumeration enum = jarFile.entries();
		while( enum.hasMoreElements() ) {
			
			ZipEntry z = (ZipEntry) enum.nextElement();
			
			if( z.getName().endsWith(".hbm.xml") ) {
				log.info( "Found mapping documents in jar: " + z.getName() );
				try {
					addInputStream( jarFile.getInputStream(z), cl );
				}
				catch (MappingException me) {
					throw me;
				}
				catch (Exception e) {
					log.error("Could not configure datastore from jar", e);
					throw new MappingException(e);
				}
			}
		}
		
		return this;
		
	}

	private Iterator iterateGenerators(Dialect dialect) throws MappingException {
		HashMap generators = new HashMap();
		Iterator iter = classes.values().iterator();
		while ( iter.hasNext() ) {
			IdentifierGenerator ig = ( (PersistentClass) iter.next() ).getIdentifier().createIdentifierGenerator(dialect);
			if ( ig instanceof PersistentIdentifierGenerator ) generators.put( 
				( (PersistentIdentifierGenerator) ig ).generatorKey(), ig 
			);
		}
		return generators.values().iterator();
	}
	
	/**
	 * Generate DDL for dropping tables
	 * @see net.sf.hibernate.tool.hbm2ddl.SchemaExport
	 */
	public String[] generateDropSchemaScript(Dialect dialect) throws HibernateException {
		
		secondPassCompile();
		
		ArrayList script = new ArrayList(50);
		
		if ( dialect.dropConstraints() ) {
			Iterator iter = getTableMappings();
			while ( iter.hasNext() )  {
				Table table = (Table)iter.next();
				Iterator subIter = table.getForeignKeyIterator();
				while ( subIter.hasNext() ) {
					ForeignKey fk = (ForeignKey)subIter.next();
					script.add(fk.sqlDropString(dialect));
				}
			}
		}
		
		
		Iterator iter = getTableMappings();
		while ( iter.hasNext() )  {
			Table table = (Table) iter.next();
			script.add( table.sqlDropString(dialect) );
		}
		
		iter = iterateGenerators(dialect);
		while ( iter.hasNext() ) {
			String dropString = ( (PersistentIdentifierGenerator) iter.next() ).sqlDropString(dialect);
			if (dropString!=null) script.add(dropString);
		}
		
		return ArrayHelper.toStringArray(script);
	}
	
	/**
	 * Generate DDL for creating tables
	 * @see net.sf.hibernate.tool.hbm2ddl.SchemaExport
	 */
	public String[] generateSchemaCreationScript(Dialect dialect) throws HibernateException {
		secondPassCompile();
		
		ArrayList script = new ArrayList(50);
		
		Iterator iter = getTableMappings();
		while ( iter.hasNext() ) {
			Table table = (Table) iter.next();
			script.add( table.sqlCreateString(dialect, mapping) );
		}
		
		iter = getTableMappings();
		while ( iter.hasNext() ) {
			Table table = (Table) iter.next();
			Iterator subIter;
			
			if ( dialect.hasAlterTable() ) {
				subIter = table.getForeignKeyIterator();
				while ( subIter.hasNext() ) {
					ForeignKey fk = (ForeignKey) subIter.next();
					script.add( fk.sqlCreateString(dialect, mapping) );
				}
			}
			
			subIter = table.getIndexIterator();
			while ( subIter.hasNext() ) {
				Index index = (Index) subIter.next();
				script.add( index.sqlCreateString(dialect, mapping) );
			}
		}
		
		iter = iterateGenerators(dialect);
		while ( iter.hasNext() ) {
			String[] lines = ( (PersistentIdentifierGenerator) iter.next() ).sqlCreateStrings(dialect);
			for ( int i=0; i<lines.length; i++ ) script.add( lines[i] );
		}
		
		return ArrayHelper.toStringArray(script);
	}
	
	/**
	 * Generate DDL for altering tables
	 * @see net.sf.hibernate.tool.hbm2ddl.SchemaUpdate
	 */
	public String[] generateSchemaUpdateScript(Dialect dialect, DatabaseMetadata databaseMetadata) throws HibernateException {
		secondPassCompile();
		
		ArrayList script = new ArrayList(50);
		
		Iterator iter = getTableMappings();
		while ( iter.hasNext() ) {
			Table table = (Table) iter.next();
			TableMetadata tableInfo = databaseMetadata.getTableMetadata( table.getName() );
			if (tableInfo==null) {
				script.add( table.sqlCreateString(dialect, mapping) );
			}
			else {
				Iterator subiter = table.sqlAlterStrings(dialect, mapping, tableInfo);
				while ( subiter.hasNext() ) script.add( subiter.next() );
			}
		}
		
		iter = getTableMappings();
		while ( iter.hasNext() ) {
			
			Table table = (Table) iter.next();
			TableMetadata tableInfo = databaseMetadata.getTableMetadata( table.getName() );
			Iterator subIter;
			
			if ( dialect.hasAlterTable() ) {
				subIter = table.getForeignKeyIterator();
				while ( subIter.hasNext() ) {
					ForeignKey fk = (ForeignKey) subIter.next();
					if ( tableInfo==null || tableInfo.getForeignKeyMetadata( fk.getName() ) == null ) {
						script.add( fk.sqlCreateString(dialect, mapping) );
					}
				}
			}
			
			subIter = table.getIndexIterator();
			while ( subIter.hasNext() ) {
				Index index = (Index) subIter.next();
				if ( tableInfo==null || tableInfo.getIndexMetadata( index.getName() ) == null ) {
					script.add( index.sqlCreateString(dialect, mapping) );
				}
			}
		}
		
		iter = iterateGenerators(dialect);
		while ( iter.hasNext() ) {
			PersistentIdentifierGenerator generator = (PersistentIdentifierGenerator) iter.next();
			Object key = generator.generatorKey();
			if ( !databaseMetadata.isSequence(key) && !databaseMetadata.isTable(key) ) {
				String[] lines = generator.sqlCreateStrings(dialect);
				for (int i = 0; i < lines.length; i++) script.add( lines[i] );
			}
		}
		
		return ArrayHelper.toStringArray(script);
	}
	
	// This method may be called many times!!
	private void secondPassCompile() throws MappingException {
		
		Iterator iter = secondPasses.iterator();
		while ( iter.hasNext() ) {
			Binder.SecondPass sp = (Binder.SecondPass) iter.next();
			sp.doSecondPass(classes);
			iter.remove();
		}
		
		//TODO: Somehow add the newly created foreign keys to the internal collection
		
		iter = getTableMappings();
		while ( iter.hasNext() ) {
			Table table = (Table) iter.next();
			Iterator subIter = table.getForeignKeyIterator();
			while ( subIter.hasNext() ) {
				
				ForeignKey fk = (ForeignKey) subIter.next();
				if ( fk.getReferencedTable() == null ) {
					PersistentClass referencedClass = (PersistentClass) classes.get( fk.getReferencedClass() );
					if (referencedClass == null) throw new MappingException(
						"An association refers to an unmapped class: " + 
						fk.getReferencedClass().getName()
					);
					fk.setReferencedTable( referencedClass.getTable() );
				}
			}
		}
	}
	
	/**
	 * Get the named queries
	 */
	public Map getNamedQueries() {
		return namedQueries;
	}
	
	private static final Interceptor EMPTY_INTERCEPTOR = new EmptyInterceptor();
	
	static final class EmptyInterceptor implements Interceptor, Serializable {
		/**
		 * @see net.sf.hibernate.Interceptor#onDelete(Object, Serializable id, Object[], String[], Type[])
		 */
		public void onDelete(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
		}
		
		/**
		 * @see net.sf.hibernate.Interceptor#onFlushDirty(Object, Object[], Object[], String[], Type[])
		 */
		public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) {
			return false;
		}
		
		/**
		 * @see net.sf.hibernate.Interceptor#onLoad(Object, Object[], String[], Type[])
		 */
		public boolean onLoad(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
			return false;
		}
		
		/**
		 * @see net.sf.hibernate.Interceptor#onSave(Object, Object[], String[], Type[])
		 */
		public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
			return false;
		}
		
		/**
		 * @see net.sf.hibernate.Interceptor#onPostFlush(Object, Serializable, Object[], String[], Type[])
		 */
		public void onPostFlush(Object entity, Serializable id, Object[] currentState, String[] propertyNames, Type[] types) {
		}
		
		/**
		 * @see net.sf.hibernate.Interceptor#postFlush(Iterator)
		 */
		public void postFlush(Iterator entities) {
		}
		
		/**
		 * @see net.sf.hibernate.Interceptor#preFlush(Iterator)
		 */
		public void preFlush(Iterator entities) {
		}
		
		/**
		 * @see net.sf.hibernate.Interceptor#isUnsaved(java.lang.Object)
		 */
		public Boolean isUnsaved(Object entity) {
			return null;
		}

		/**
		 * @see net.sf.hibernate.Interceptor#instantiate(java.lang.Class, java.io.Serializable)
		 */
		public Object instantiate(Class clazz, Serializable id) {
			return null;
		}

		/**
		 * @see net.sf.hibernate.Interceptor#findDirty(java.lang.Object, java.io.Serializable, java.lang.Object, java.lang.Object, java.lang.String, net.sf.hibernate.type.Type)
		 */
		public int[] findDirty(
			Object entity,
			Serializable id,
			Object[] currentState,
			Object[] previousState,
			String[] propertyNames,
			Type[] types) {
			return null;
		}

	}
	
	/**
	 * Instantiate a new <tt>SessionFactory</tt>, using the properties and
	 * mappings in this configuration. The <tt>SessionFactory</tt> will be
	 * immutable, so changes made to the <tt>Configuration</tt> after
	 * building the <tt>SessionFactory</tt> will not affect it.
	 * 
	 * @see net.sf.hibernate.SessionFactory
	 * @return a new factory for <tt>Session</tt>s
	 */
	public SessionFactory buildSessionFactory() throws HibernateException {
		secondPassCompile();
		Environment.verifyProperties(properties);
		Properties copy = new Properties();
		copy.putAll(properties);
		return new SessionFactoryImpl(this, copy, interceptor);
	}
	
	/**
	 * Return the configured <tt>Interceptor</tt>
	 */
	public Interceptor getInterceptor() {
		return interceptor;
	}
	
	/**
	 * Get all properties
	 */
	public Properties getProperties() {
		return properties;
	}

	/**
	 * Configure an <tt>Interceptor</tt>
	 */
	public Configuration setInterceptor(Interceptor interceptor) {
		this.interceptor = interceptor;
		return this;
	}

	/**
	 * Specify a completely new set of properties
	 */
	public Configuration setProperties(Properties properties) {
		this.properties = properties;
		return this;
	}
	
	/**
	 * Set the given properties
	 */
	public Configuration addProperties(Properties properties) {
		this.properties.putAll(properties);
		return this;
	}
	
	/**
	 * Set a property
	 */
	public Configuration setProperty(String propertyName, String value) {
		properties.setProperty(propertyName, value);
		return this;
	}

	/**
	 * Get a property
	 */
	public String getProperty(String propertyName) {
		return properties.getProperty(propertyName);
	}

	private void addProperties(Element parent) {
		Iterator iter = parent.elementIterator("property");
		while ( iter.hasNext() ) {
			Element node = (Element) iter.next();
			String name = node.attributeValue("name");
			String value = node.getText();
			log.debug(name + "=" + value);
			properties.setProperty(name, value);
			if ( !name.startsWith("hibernate") ) properties.setProperty("hibernate." + name, value);
		}
		Environment.verifyProperties(properties);
	}
	
	/**
	 * Get the configuration file as an <tt>InputStream</tt>. Might be overridden
	 * by subclasses to allow the configuration to be located by some arbitrary
	 * mechanism.
	 */
	protected InputStream getConfigurationInputStream(String resource) throws HibernateException {
		
		log.info("Configuration resource: " + resource);
		
		InputStream stream = Environment.class.getResourceAsStream(resource);
		if (stream==null) {
			log.warn(resource + " not found");
			throw new HibernateException(resource + " not found");
		}
		return stream;
		
	}
	
	/**
	 * Use the mappings and properties specified in an application
	 * resource named <tt>hibernate.cfg.xml</tt>.
	 */
	public Configuration configure() throws HibernateException {
		configure("/hibernate.cfg.xml");
		return this;
	}
	
	/**
	 * Use the mappings and properties specified in the given application
	 * resource. The format of the resource is defined in 
	 * <tt>hibernate-configuration-2.0.dtd</tt>.
	 * 
	 * The resource is found via <tt>getConfigurationInputStream(resource)</tt>.
	 */
	public Configuration configure(String resource) throws HibernateException {
		InputStream stream = getConfigurationInputStream(resource);
		return configure(stream, resource);		
	}
	
	/**
	 * Use the mappings and properties specified in the given document.
	 * The format of the document is defined in 
	 * <tt>hibernate-configuration-2.0.dtd</tt>.
	 * 
	 * @param url URL from which you wish to load the configuration
	 * @return A configuration configured via the file
	 * @throws HibernateException
	 */
	public Configuration configure(URL url) throws HibernateException {
		try {
			return configure( url.openStream(), url.toString() );
		}
		catch (IOException ioe) {
			throw new HibernateException("could not configure from URL: " + url, ioe);
		}
	}
	
	/**
	 * Use the mappings and properties specified in the given application
	 * file. The format of the file is defined in 
	 * <tt>hibernate-configuration-2.0.dtd</tt>.
	 * 
	 * @param configFile <tt>File</tt> from which you wish to load the configuration 
	 * @return A configuration configured via the file
	 * @throws HibernateException
	 */
	public Configuration configure(File configFile) throws HibernateException {
		try {
			return configure( new FileInputStream(configFile), configFile.toString() );
		}
		catch (FileNotFoundException fnfe) {
			throw new HibernateException("could not find file: " + configFile, fnfe);
		}
	}
	
	/**
	 * Use the mappings and properties specified in the given application
	 * resource. The format of the resource is defined in 
	 * <tt>hibernate-configuration-2.0.dtd</tt>.
	 *
	 * @param stream Inputstream to be read from
	 * @param resourceName The name to use in warning/error messages 
	 * @return A configuration configured via the stream
	 * @throws HibernateException
	 */
	protected Configuration configure(InputStream stream, String resourceName) throws HibernateException {

		org.dom4j.Document doc;
		try {
			doc = XMLHelper.createSAXReader(resourceName).read( new InputSource(stream) );
		}
		catch (Exception e) {
			log.error("problem parsing configuration" + resourceName, e);
			throw new HibernateException("problem parsing configuration" + resourceName, e);
		}
		
		return configure(doc);
		
	}
	
	/**
	 * Use the mappings and properties specified in the given XML document.
	 * The format of the file is defined in 
	 * <tt>hibernate-configuration-2.0.dtd</tt>.
	 * 
	 * @param document an XML document from which you wish to load the configuration 
	 * @return A configuration configured via the <tt>Document</tt>
	 * @throws HibernateException
	 * @throws FileNotFoundException if there is problem in accessing the file. 
	 */
	public Configuration configure(Document document) throws HibernateException {

		org.dom4j.Document doc;
		try {
			doc = XMLHelper.createDOMReader().read(document);
		}
		catch (Exception e) {
			log.error("problem parsing document", e);
			throw new HibernateException("problem parsing document", e);
		}
		
		return configure(doc);
	}
	
	protected Configuration configure(org.dom4j.Document doc) throws HibernateException {
		
		Element sfNode = doc.getRootElement().element("session-factory");
		String name = sfNode.attributeValue("name");
		if (name!=null) properties.setProperty(Environment.SESSION_FACTORY_NAME, name);
		addProperties(sfNode);
		
		Iterator elements = sfNode.elementIterator();
		while ( elements.hasNext() ) {
			Element mapElement = (Element) elements.next();
			String elemname = mapElement.getName();
			if ( "mapping".equals(elemname) ) {
				Attribute rsrc = mapElement.attribute("resource");
				Attribute file = mapElement.attribute("file");
				Attribute jar = mapElement.attribute("jar");
				if (rsrc!=null) {
					log.debug(name + "<-" + rsrc);
					try {
						addResource( rsrc.getValue(), Thread.currentThread().getContextClassLoader() );
					}
					catch (MappingException me) {
						addResource( rsrc.getValue(), Environment.class.getClassLoader() );
					}
				}
				else if ( jar!=null ) {
					log.debug(name + "<-" + jar);
					addJar( jar.getValue() );
				}
				else {
					if (file==null) throw new MappingException("<mapping> element in configuration specifies no attributes");
					log.debug(name + "<-" + file);
					addFile( file.getValue() );
				}
			}
			else if ( "jcs-class-cache".equals(elemname) ) {
				String className = mapElement.attributeValue("class");
				final Class clazz;
				try {
					clazz = ReflectHelper.classForName(className);
				}
				catch (ClassNotFoundException cnfe) {
					throw new MappingException("Could not find class: " + className, cnfe);
				}
				RootClass pc;
				try {
					pc = (RootClass) getClassMapping(clazz);
				}
				catch (ClassCastException cce) {
					throw new MappingException("You may only specify a cache for root <class> mappings");
				}
				Attribute regionNode = mapElement.attribute("region");
				String region = className;
				if (regionNode!=null) region = regionNode.getValue(); 
				pc.setCache( createJCSCache( mapElement.attributeValue("usage"), region, pc ) );
			}
			else if ( "jcs-collection-cache".equals(elemname) ) {
				String role = mapElement.attributeValue("collection");
				Collection c = (Collection) getCollectionMapping(role);
				Attribute regionNode = mapElement.attribute("region");
				String region = role;
				if (regionNode!=null) region = regionNode.getValue(); 
				c.setCache( createJCSCache( mapElement.attributeValue("usage"), region, c.getOwner() ) );
			}
		}
		
		log.info("Configured SessionFactory: " + name);
		log.debug("properties: " + properties);
		
		return this;
		
	}

	static CacheConcurrencyStrategy createJCSCache(String usage, String name, PersistentClass owner) throws MappingException {
		
		log.info("creating JCS cache region: " + name + ", usage: " + usage);
		
		final Cache jcs;
		try {
			jcs = new JCSCache();
		}
		catch (NoClassDefFoundError ncf) {
			log.warn( "Could not instantiate cache - probably the JCS jar is missing", ncf );
			// continue with no cache
			return null;
		}
		try {
			jcs.setRegion(name);
		}
		catch (CacheException ce) {
			throw new MappingException("Could not instantiate JCS",ce);
		}
		if ( usage.equals("read-only") ) {
			if ( owner.isMutable() ) log.warn( "read-only cache configured for mutable: " + name );
			return new ReadOnlyCache(jcs);
		}
		else if ( usage.equals("read-write") ) {
			return new ReadWriteCache(jcs);
		}
		else if ( usage.equals("nonstrict-read-write") ) {
			return new NonstrictReadWriteCache(jcs);
		}
		else {
			throw new MappingException("jcs-cache usage attribute should be read-write or read-only");
		}
	}
	
	/**
	 * Get the query language imports
	 * 
	 * @return Map
	 */
	public Map getImports() {
		return imports;
	}

}






