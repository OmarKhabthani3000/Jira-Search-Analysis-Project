package org.wfp.rita.test.hibernate;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import junit.framework.TestCase;

import org.apache.derby.jdbc.EmbeddedDriver;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.JoinColumnOrFormula;
import org.hibernate.annotations.JoinColumnsOrFormulas;
import org.hibernate.annotations.JoinFormula;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.ExtendedMappings;
import org.hibernate.dialect.DerbyDialect;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.UniqueKey;
import org.hibernate.tool.hbm2ddl.SchemaExport;

/**
 * When a {@link JoinFormula} references a column which has a unique
 * attribute on it, {@link SchemaExport} creates two separate keys that
 * cover the same columns:
 * 
 * <ul>
 * <li>The <code>unique</code> attribute on the end of the column
 * definition;
 * <li>A separate unique key after the column and primary key definitions,
 * from being the target of a {@link ManyToOne} association.
 * </ul>
 * 
 * Derby doesn't like this and fails with an error like this:
 * 
 * <code>
 * create table product (id integer not null,
 * product_idnf char(18) not null unique, primary key (id),
 * unique (product_idnf)): Constraints 'SQL101020115435462' and
 * 'SQL101020115435460' have the same set of columns, which is not allowed.
 * </code>
 * 
 * Proposed fix: in {@link org.hibernate.mapping.Table#getUniqueKeys},
 * when the unique key has a single column, check whether that column is
 * already unique, and if so don't generate the unique key.
 *
 * @author Chris Wilson <chris+rita@aptivate.org>
 */
public class HibernateSchemaExportUniqueColumnAndForeignKeyDerbyFailsTest extends TestCase
{
    @Entity
    @javax.persistence.Table(name="product")
    private static class Product 
    {
        @Id
        public Integer id;

        @javax.persistence.Column(name="product_idnf", length=18, nullable=false, unique=true,
            columnDefinition="char(18)")
    	private String productIdnf;
        
        @ManyToOne
        @ForeignKey(name="none")
        @JoinColumnsOrFormulas({
        	@JoinColumnOrFormula(formula=@JoinFormula(value="SUBSTR(product_idnf, 1, 3)",
        		referencedColumnName="product_idnf"))
        	})
       	@Fetch(FetchMode.JOIN)
        private Product family;
    }

    private void assertConfigure(AnnotationConfiguration conf)
    throws Exception
    {
        conf.addAnnotatedClass(Product.class);
        conf.setProperty("hibernate.connection.driver_class",
        	EmbeddedDriver.class.getName());
	    conf.setProperty("hibernate.connection.url",
	    	"jdbc:derby:/tmp/test.derby;create=true");
	    conf.setProperty("hibernate.connection.username", "");
	    conf.setProperty("hibernate.connection.password", "");
	    conf.setProperty("hibernate.dialect",
	    	DerbyDialect.class.getName());
	    
	    SchemaExport se = new SchemaExport(conf);
	    se.drop(false, true);
	    se.execute(true, true, false, true);
	    
	    for (Object e : se.getExceptions())
	    {
	    	throw (Exception) e;
	    }
    }

    public void testFailing()
    throws Exception
    {
    	assertConfigure(new AnnotationConfiguration());
    }
    
    /**
     * Apart from the following lines in {@link FixedTable#getUniqueKeys()},
     * all the code in this class is plumbing end up using our modified
     * version of getUniqueKeys() instead of the original one.
     * 
     * <pre>
if ( ! uniqueKeys.isEmpty() ) {
...
// new code starts: check for unique columns as well
if (columns.size() == 1)
{
	Column col = (Column) columns.get(0);
	if (col.isUnique())
	{
		skip = true;
		break;
	}
}
// new code ends: check for unique columns as well</pre>
     */
    private static class FixedConfiguration extends AnnotationConfiguration
    {
		private static final long serialVersionUID = 1L;

		private static class FixedTable extends Table
    	{
    		public Iterator getUniqueKeyIterator() {
    			return getUniqueKeys().values().iterator();
    		}
    		
    		private Map getUniqueKeys()
    		{
    			// reconstruct the private map
    			Map uniqueKeys = new HashMap();
    			
    			for (Iterator i = super.getUniqueKeyIterator(); i.hasNext();)
    			{
    				UniqueKey uk = (UniqueKey) i.next();
    				uniqueKeys.put(uk.getName(), uk);
    			}
    			
    			// modified code below
    			// the following line was changed to check even when size() == 1
    			if ( ! uniqueKeys.isEmpty() ) {
    				//deduplicate unique constraints sharing the same columns
    				//this is needed by Hibernate Annotations since it creates automagically
    				// unique constraints for the user
    				Iterator it = uniqueKeys.entrySet().iterator();
    				Map finalUniqueKeys = new HashMap( uniqueKeys.size() );
    				while ( it.hasNext() ) {
    					Map.Entry entry = (Map.Entry) it.next();
    					UniqueKey uk = (UniqueKey) entry.getValue();
    					List columns = uk.getColumns();
    					int size = finalUniqueKeys.size();
    					boolean skip = false;
    					Iterator tempUks = finalUniqueKeys.entrySet().iterator();
    					while ( tempUks.hasNext() ) {
    						final UniqueKey currentUk = (UniqueKey) ( (Map.Entry) tempUks.next() ).getValue();
    						if ( currentUk.getColumns().containsAll( columns ) && columns
    								.containsAll( currentUk.getColumns() ) ) {
    							skip = true;
    							break;
    						}
    					}
    					// new code starts: check for unique columns as well
    					if (columns.size() == 1)
    					{
    						Column col = (Column) columns.get(0);
    						if (col.isUnique())
    						{
    							skip = true;
    							break;
    						}
    					}
    					// new code ends: check for unique columns as well
    					if ( !skip ) finalUniqueKeys.put( entry.getKey(), uk );
    				}
    				return finalUniqueKeys;
    			}
    			else {
    				return uniqueKeys;
    			}
    		}
    	}
    	
    	protected class FixedMappingsImpl extends ExtendedMappingsImpl
    	{
			public Table addTable(
				String schema,
				String catalog,
				String name,
				String subselect,
				boolean isAbstract)
			{
				name = getObjectNameNormalizer().normalizeIdentifierQuoting( name );
				schema = getObjectNameNormalizer().normalizeIdentifierQuoting( schema );
				catalog = getObjectNameNormalizer().normalizeIdentifierQuoting( catalog );
		
				String key = subselect == null ? Table.qualify( catalog, schema, name ) : subselect;
				Table table = ( Table ) tables.get( key );
		
				if ( table == null ) {
					table = new FixedTable();
					table.setAbstract( isAbstract );
					table.setName( name );
					table.setSchema( schema );
					table.setCatalog( catalog );
					table.setSubselect( subselect );
					tables.put( key, table );
				}
				else {
					if ( !isAbstract ) {
						table.setAbstract( false );
					}
				}
		
				return table;
			}
    	}
    	
    	@Override
    	public ExtendedMappings createExtendedMappings() {
    		return new FixedMappingsImpl();
    	}
    }

    public void testPatch()
    throws Exception
    {
    	assertConfigure(new FixedConfiguration());
    }

    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(HibernateSchemaExportUniqueColumnAndForeignKeyDerbyFailsTest.class);
    }
}
