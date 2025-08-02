/*
 * Created on 2004-11-23
 *
 */
package org.hibernate.cfg;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.FetchMode;
import org.hibernate.MappingException;
import org.hibernate.cfg.reveng.DatabaseCollector;
import org.hibernate.cfg.reveng.JDBCReader;
import org.hibernate.cfg.reveng.JDBCToHibernateTypeHelper;
import org.hibernate.cfg.reveng.MappingsDatabaseCollector;
import org.hibernate.cfg.reveng.ReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.TableIdentifier;
import org.hibernate.cfg.reveng.dialect.JDBCMetaDataDialect;
import org.hibernate.cfg.reveng.dialect.MetaDataDialect;
import org.hibernate.connection.ConnectionProvider;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.Mapping;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.DependantValue;
import org.hibernate.mapping.ForeignKey;
import org.hibernate.mapping.KeyValue;
import org.hibernate.mapping.ManyToOne;
import org.hibernate.mapping.MetaAttribute;
import org.hibernate.mapping.OneToMany;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.Value;
import org.hibernate.type.Type;
import org.hibernate.type.TypeFactory;
import org.hibernate.util.JoinedIterator;
import org.hibernate.util.ReflectHelper;
import org.hibernate.util.StringHelper;


/**
 * @author max
 *
 */
public class JDBCBinder {

	private static final boolean MANYTOONEFK_INC_PROP = true; //should  be configurable somehow
	
	private Settings settings;
	private ConnectionProvider connectionProvider;
	private static final Log log = LogFactory.getLog(JDBCBinder.class);
	private Connection connection;
	
	private final Mappings mappings;
	
	private final JDBCMetaDataConfiguration cfg;
	private ReverseEngineeringStrategy revengStrategy;

	/**
	 * @param mappings
	 * @param configuration
	 */
	public JDBCBinder(JDBCMetaDataConfiguration cfg, Settings settings, Mappings mappings, ReverseEngineeringStrategy revengStrategy) {
		this.cfg = cfg;
		this.settings = settings;
		this.mappings = mappings;
		this.revengStrategy = revengStrategy;
	}

	/**
	 * 
	 */
	public void readFromDatabase(String catalog, String schema, Mapping mapping) {
		
		this.connectionProvider = settings.getConnectionProvider();
				
		try {
			
			Map oneToManyCandidates = readDatabaseSchema(catalog, schema); 
			
			createPersistentClasses(oneToManyCandidates, mapping); //move this to a different step!
		} 
		catch (SQLException e) {
			throw settings.getSQLExceptionConverter().convert(e, "Reading from database", null);
		} 
		finally	{
				if(connectionProvider!=null) connectionProvider.close();
		}
		
	}

	/**
	 * Read JDBC Metadata from the database. Does not create any classes or other ORM releated structures.
	 * 
	 * @param catalog
	 * @param schema
	 * @return
	 * @throws SQLException
	 */
	public Map readDatabaseSchema(String catalog, String schema) throws SQLException {
	  	 // use default from settings if nothing else specified.
	     catalog = catalog!=null ? catalog : settings.getDefaultCatalogName();
	     schema = schema!=null ? schema : settings.getDefaultSchemaName();
	     
	     JDBCReader reader = JDBCReaderFactory.newJDBCReader(cfg.getProperties(),settings,revengStrategy);
	     DatabaseCollector dbs = new MappingsDatabaseCollector(mappings); 
	     dbs = reader.readDatabaseSchema(dbs, catalog, schema);
	     return dbs.getOneToManyCandidates();
	}


	
	/**
	 * @param manyToOneCandidates
	 * @param mappings2
	 */
	private void createPersistentClasses(Map manyToOneCandidates, Mapping mapping) {
		
		for (Iterator iter = mappings.iterateTables(); iter.hasNext();) {
			Table table = (Table) iter.next();
			if(table.getColumnSpan()==0) {
				log.warn("Cannot create persistent class for " + table + " as no columns were found.");
				continue;
			}
			// TODO: this naively just create an entity per table
			// should have an opt-out option to mark some as helper tables, subclasses etc.
			/*if(table.getPrimaryKey()==null || table.getPrimaryKey().getColumnSpan()==0) {
			    log.warn("Cannot create persistent class for " + table + " as no primary key was found.");
                continue;
                // TODO: just create one big embedded composite id instead.
            }*/
			RootClass rc = new RootClass();
			rc.setEntityName( revengStrategy.tableToClassName( TableIdentifier.create(table) ) );
			
			
			rc.setClassName( revengStrategy.tableToClassName( TableIdentifier.create(table) ) );				
			rc.setProxyInterfaceName( rc.getEntityName() ); // TODO: configurable ?
			rc.setLazy(true);
			
			rc.setDiscriminatorValue( rc.getEntityName() );
			rc.setTable(table);
			mappings.addClass(rc);
			mappings.addImport( rc.getEntityName(), rc.getEntityName() );
			
			Set processed = new HashSet();
            
			bindPrimaryKeyToProperties(table, rc, processed, mapping);
			bindOutgoingForeignKeys(table, rc, processed);
			bindColumnsToProperties(table, rc, processed, mapping);
			List incomingForeignKeys = (List) manyToOneCandidates.get( rc.getEntityName() );
			bindIncomingForeignKeys(rc, processed, incomingForeignKeys, mapping);
		}
		
	}
	
	private void bindIncomingForeignKeys(PersistentClass rc, Set processed, List foreignKeys, Mapping mapping) {
		if(foreignKeys!=null) {
			for (Iterator iter = foreignKeys.iterator(); iter.hasNext();) {
				ForeignKey fk = (ForeignKey) iter.next();
				
				Iterator fkIterator = fk.getTable().getForeignKeyIterator();				
				Property property = bindOneToMany(rc, fk, processed, mapping);
				rc.addProperty(property);
			}
		}
	}

    /**
     * @param table
     * @param fk
     * @param columnsToBind 
     * @param processedColumns
     * @param rc
     * @param propName 
     */
    private Property bindManyToOne(String propertyName, Table table, ForeignKey fk, Set processedColumns) {
        ManyToOne value = new ManyToOne(table);
        value.setReferencedEntityName( fk.getReferencedEntityName() );
		Iterator columns = fk.getColumnIterator();
        while ( columns.hasNext() ) {       
			Column fkcolumn = (Column) columns.next();
            checkColumn(fkcolumn);
            value.addColumn(fkcolumn);
            processedColumns.add(fkcolumn);
		}
        value.setFetchMode(FetchMode.SELECT);
        
        return makeProperty(propertyName, value, true, true, value.getFetchMode()!=FetchMode.JOIN, null, null);
     }

	/**
	 * @param rc
	 * @param processed
	 * @param table
	 * @param object
	 */
	private Property bindOneToMany(PersistentClass rc, ForeignKey foreignKey, Set processed, Mapping mapping) {
		
		Table collectionTable = foreignKey.getTable();

		Collection collection = new org.hibernate.mapping.Set(rc); // MASTER TODO: allow overriding collection type
        
		collection.setCollectionTable(collectionTable); // CHILD+
		
		boolean b = isUniqueReference(foreignKey);
		
		String collectionRole = revengStrategy.foreignKeyToCollectionName(
				foreignKey.getName(),				
				TableIdentifier.create( foreignKey.getTable() ),	
				foreignKey.getColumns(), TableIdentifier.create( foreignKey.getReferencedTable() ), foreignKey.getReferencedColumns(), 
				b
			);
        collectionRole = makeUnique(rc,collectionRole);
        
		String fullRolePath = StringHelper.qualify(rc.getEntityName(), collectionRole);
        if (mappings.getCollection(fullRolePath)!=null) {
            log.debug(fullRolePath + " found twice!");
        }
        collection.setRole(fullRolePath);  // Master.setOfChildren+ 
		collection.setInverse(true); // TODO: allow overriding this
        collection.setLazy(true); // TODO: configurable
        collection.setFetchMode(FetchMode.SELECT);
        
		OneToMany oneToMany = new OneToMany( collection.getOwner() );
		oneToMany.setReferencedEntityName( revengStrategy.tableToClassName( TableIdentifier.create( foreignKey.getTable() ) ) ); // Child
		
        mappings.addSecondPass( new JDBCCollectionSecondPass(mappings, collection) );
		collection.setElement(oneToMany);

		// bind keyvalue
		KeyValue referencedKeyValue;
		String propRef = collection.getReferencedPropertyName();
		if (propRef==null) {
			referencedKeyValue = collection.getOwner().getIdentifier();
		}
		else {
			referencedKeyValue = (KeyValue) collection.getOwner()
				.getProperty(propRef)
				.getValue();
		}

		SimpleValue keyValue = new DependantValue( collectionTable, referencedKeyValue );
		//key.setCascadeDeleteEnabled( "cascade".equals( subnode.attributeValue("on-delete") ) );
		Iterator columnIterator = foreignKey.getColumnIterator();        
		while ( columnIterator.hasNext() ) {
			Column fkcolumn = (Column) columnIterator.next();
			if(fkcolumn.getSqlTypeCode()!=null) { // TODO: user defined foreign ref columns does not have a type set.
				guessAndAlignType(collectionTable, fkcolumn, mapping); // needed to ensure foreign key columns has same type as the "property" column.
			}
			keyValue.addColumn( fkcolumn );
		}
		
		collection.setKey(keyValue);
		
		mappings.addCollection(collection);
		
		return makeProperty(collectionRole, collection, true, true, true, "all", null);
		
	}

	/** return true if this foreignkey is the only reference from this table to the same foreign table */
    private boolean isUniqueReference(ForeignKey foreignKey) {
		
    	Iterator foreignKeyIterator = foreignKey.getTable().getForeignKeyIterator();
    	while ( foreignKeyIterator.hasNext() ) {
			ForeignKey element = (ForeignKey) foreignKeyIterator.next();
			if(element!=foreignKey && element.getReferencedTable().equals(foreignKey.getReferencedTable())) {
				return false;
			}
		}
		return true;
	}

	private void bindPrimaryKeyToProperties(Table table, RootClass rc, Set processed, Mapping mapping) {
		SimpleValue id = null;
		String idPropertyname = null;
		
		List keyColumns = null;
		if (table.getPrimaryKey()!=null) {
			keyColumns = table.getPrimaryKey().getColumns();
		} 
		else {
			log.debug("No primary key found for " + table + ", using all properties as the identifier.");
			keyColumns = new ArrayList();
			Iterator iter = table.getColumnIterator();
			while (iter.hasNext() ) {
				Column col = (Column) iter.next();
				keyColumns.add(col);
			}
		}

		final TableIdentifier tableIdentifier = TableIdentifier.create(table);
		if (keyColumns.size()>1) {
			id = handleCompositeKey(rc, processed, keyColumns, mapping);
			idPropertyname = "id";
		} 
		else {
			Column pkc = (Column) keyColumns.get(0);
			checkColumn(pkc);
			
			id = bindColumnToSimpleValue(table, pkc, mapping);

			idPropertyname = revengStrategy.columnToPropertyName(tableIdentifier, pkc.getName() );
			processed.add(pkc);
		} 
		id.setIdentifierGeneratorStrategy(revengStrategy.getTableIdentifierStrategyName(tableIdentifier));
		id.setIdentifierGeneratorProperties(revengStrategy.getTableIdentifierProperties(tableIdentifier));
		if("assigned".equals(id.getIdentifierGeneratorStrategy())) {
			id.setNullValue("undefined");
		}
		
		Property property = makeProperty(makeUnique(rc,idPropertyname), id, true, true, false, null, null);
		rc.setIdentifierProperty(property);            
		rc.setIdentifier(id);
			
	}

	/**
	 * @param table
	 * @param rc
	 * @param primaryKey
	 */
	private void bindOutgoingForeignKeys(Table table, RootClass rc, Set processedColumns) {
		
		// Iterate the outgoing foreign keys and create many-to-one's 
		for(Iterator iterator = table.getForeignKeyIterator(); iterator.hasNext();) {
			ForeignKey foreignKey = (ForeignKey) iterator.next();
			
			boolean mutable = true;
            if ( contains( foreignKey.getColumnIterator(), processedColumns ) ) {
				if ( !cfg.preferBasicCompositeIds() ) continue; //it's in the pk, so skip this one
				mutable = false;	
            }
            
            boolean isUnique = isUniqueReference(foreignKey);
            String propertyName = revengStrategy.foreignKeyToEntityName(
					foreignKey.getName(), 
					TableIdentifier.create(foreignKey.getTable() ), 
					foreignKey.getColumns(), 
					TableIdentifier.create(foreignKey.getReferencedTable() ),
					foreignKey.getReferencedColumns(), 
					isUnique
				);
			
            Property property = bindManyToOne(
					makeUnique(rc, propertyName), 
					table, 
					foreignKey,
					processedColumns
				);
            property.setUpdateable(mutable);
            property.setInsertable(mutable);
			
            rc.addProperty(property);
        
		}
	}
		
	/**
	 * @param table
	 * @param rc
	 * @param primaryKey
	 */
	private void bindColumnsToProperties(Table table, RootClass rc, Set processedColumns, Mapping mapping) {

		//we'll need the PK columns again. 		
		List pkeyColumns = null;
		if (table.getPrimaryKey()!=null) {
			pkeyColumns = table.getPrimaryKey().getColumns();
		}
		
		//if we have no PKs, then in bindPrimaryKeyToProperties every column was included in a composite key,
		//so there is no reason to even iterate over the columns to add properties, since they have all been processed already
		if (pkeyColumns==null || pkeyColumns.size()==0) return;
				
		
		for (Iterator iterator = table.getColumnIterator(); iterator.hasNext();) {
			Column column = (Column) iterator.next();
			if ( !processedColumns.contains(column) || (MANYTOONEFK_INC_PROP && !pkeyColumns.contains(column))) {
				checkColumn(column);
				
				String propertyName = revengStrategy.columnToPropertyName(TableIdentifier.create(table), column.getName() );
				
				Property property = bindBasicProperty( 
						makeUnique(rc,propertyName), 
						table, 
						column, 
						processedColumns,
						mapping
					);
				
				rc.addProperty(property);
			}			
		}
	}
	
	private Property bindBasicProperty(String propertyName, Table table, Column column, Set processedColumns, Mapping mapping) {

		SimpleValue value = bindColumnToSimpleValue( table, column, mapping );
		
		if (MANYTOONEFK_INC_PROP && processedColumns.contains(column)) {			
			return makeProperty(propertyName, value, false, false, false, null, null);
		}
		
		return makeProperty(propertyName, value, true, true, false, null, null);
	}

	private SimpleValue bindColumnToSimpleValue(Table table, Column column, Mapping mapping) {
		SimpleValue value = new SimpleValue(table);                
		value.addColumn(column);
		value.setTypeName(guessAndAlignType(table, column, mapping));
		return value;
	}

    /**
     * @param columnIterator
     * @param processedColumns
     * @return
     */
    private boolean contains(Iterator columnIterator, Set processedColumns) {
        while (columnIterator.hasNext() ) {
            Column element = (Column) columnIterator.next();
            if(processedColumns.contains(element) ) {
                return true;
            }
        }
        return false;
    }

	private void checkColumn(Column column) {
		if(column.getValue()!=null) {
			//throw new JDBCBinderException("Binding column twice should not happen. " + column);
		}
	}

	/**
	 * @param pkc
	 * @return
	 */
	private String guessAndAlignType(Table table, Column pkc, Mapping mapping) {
		// TODO: this method mutates the column if the types does not match...not good. 
		// maybe we should copy the column instead before calling this method.
		Integer sqlTypeCode = pkc.getSqlTypeCode();
		if(sqlTypeCode==null) {
			throw new JDBCBinderException("Could not find sqltype for " + pkc);
		}
		
		String preferredHibernateType = revengStrategy.columnToHibernateTypeName(
				TableIdentifier.create(table), 
				pkc.getName(), 
				sqlTypeCode.intValue(),
				pkc.getLength(), pkc.getPrecision(), pkc.getScale()
		);
		
		Type wantedType = TypeFactory.heuristicType(preferredHibernateType);
		
		if(wantedType!=null) {
			int[] wantedSqlTypes = wantedType.sqlTypes(mapping);
			
			if(wantedSqlTypes.length>1) {
				throw new JDBCBinderException("The type " + preferredHibernateType + " found on column " + pkc + " on " + table + " spans multiple columns. Only single column types allowed for single columns."); 
			}
			
			int wantedSqlType = wantedSqlTypes[0];
			if(wantedSqlType!=sqlTypeCode.intValue() ) {			
				log.debug("Sql type mismatch for Column " + pkc + " between DB and wanted hibernate type. Sql type set to " + sqlTypeCode.intValue() + " instead of " + wantedSqlType );
				pkc.setSqlTypeCode(new Integer(wantedSqlType));
			}
		} 
		else {
			log.debug("No Hibernate type found for " + preferredHibernateType + ". Most likely cause is a missing UserType class.");
		}
		
		
		
		if(preferredHibernateType==null) {
			throw new JDBCBinderException("Could not find javatype for " + sqlTypeCode + "(" + JDBCToHibernateTypeHelper.getJDBCTypeName(sqlTypeCode.intValue() ) + ")");
		}
        
		return preferredHibernateType;
	}

	/**
     * Basically create an [classname]Id.class and add  properties for it.
	 * @param rc
	 * @param compositeKeyColumns 
	 * @param processed
	 * @return
	 */
	private SimpleValue handleCompositeKey(RootClass rc, Set processedColumns, List keyColumns, Mapping mapping) {
		Component pkc = new Component(rc);
        pkc.setMetaAttributes(Collections.EMPTY_MAP);
        pkc.setEmbedded(false);
        pkc.setComponentClassName(revengStrategy.classNameToCompositeIdName(rc.getClassName()));
		Table table = rc.getTable();		
        List list = null;        
		if (cfg.preferBasicCompositeIds() ) {
            list = new ArrayList(keyColumns);
        } 
		else {
            list = findForeignKeys(table.getForeignKeyIterator(), keyColumns);
        }
        for (Iterator iter = list.iterator(); iter.hasNext();) {
            Object element = iter.next();
			Property property;
            if (element instanceof Column) {
                Column column = (Column) element;
                if ( processedColumns.contains(column) ) {
                    throw new JDBCBinderException("Binding column twice for primary key should not happen: " + column); 
                } 
				else {
                    checkColumn(column);
                    
                    String propertyName = revengStrategy.columnToPropertyName( TableIdentifier.create(table), column.getName() );
					property = bindBasicProperty( makeUnique(pkc, propertyName), table, column, processedColumns, mapping);
                    
                    processedColumns.add(column);
                }
            } 
			else if (element instanceof ForeignKeyForColumns) {
                ForeignKeyForColumns fkfc = (ForeignKeyForColumns) element;
                ForeignKey foreignKey = fkfc.key;
                String propertyName = revengStrategy.foreignKeyToEntityName(
						foreignKey.getName(), 
						TableIdentifier.create(foreignKey.getTable() ),
						foreignKey.getColumns(), TableIdentifier.create(foreignKey.getReferencedTable() ), foreignKey.getReferencedColumns(), true
					);
                property = bindManyToOne( makeUnique(pkc, propertyName), table, foreignKey, processedColumns);
                processedColumns.addAll(fkfc.columns);
            }
			else {
				throw new JDBCBinderException("unknown thing");
			}
			
            markAsUseInEquals(property);
            pkc.addProperty(property);
			
		}

		return pkc;
	}

    /**
     * @param property
     */
    private void markAsUseInEquals(Property property) {
        Map m = new HashMap();
        MetaAttribute ma = new MetaAttribute("use-in-equals");
        ma.addValue("true");
        m.put(ma.getName(),ma);
        property.setMetaAttributes(m);
    }

    /**
     * @param foreignKeyIterator
     * @param columns
     * @return 
     */
    private List findForeignKeys(Iterator foreignKeyIterator, List pkColumns) {
    	
    	List tempList = new ArrayList();
    	while(foreignKeyIterator.hasNext()) {
    		tempList.add(foreignKeyIterator.next());
    	}
    	
//    	Collections.reverse(tempList); 
    	
    	List result = new ArrayList();
    	Column myPkColumns[] = (Column[]) pkColumns.toArray(new Column[pkColumns.size()]);
    	
    	for (int i = 0; i < myPkColumns.length; i++) {
			
    		boolean foundKey = false;
    		foreignKeyIterator = tempList.iterator();
    		while(foreignKeyIterator.hasNext()) {
    			ForeignKey key = (ForeignKey) foreignKeyIterator.next();
    			List matchingColumns = columnMatches(myPkColumns, i, key);
    			if(matchingColumns!=null) {
    				result.add(new ForeignKeyForColumns(key, matchingColumns));
    				i+=matchingColumns.size()-1;
    				foreignKeyIterator.remove();
    				foundKey=true;
    				break;
    			} 
    		}
    		if(!foundKey) {
    			result.add(myPkColumns[i]);				
    		}
    		
		}
    	
    	return result;
    }

    private List columnMatches(Column[] myPkColumns, int offset, ForeignKey key) {
		
    	if(key.getColumnSpan()>(myPkColumns.length-offset)) {
    		return null; // not enough columns in the key
    	}
    	
    	List columns = new ArrayList();
    	for (int j = 0; j < key.getColumnSpan(); j++) {
			Column column = myPkColumns[j+offset];
			if(!column.equals(key.getColumn(j))) {
				return null;
			} else {
				columns.add(column);
			}
		}
		return columns.isEmpty()?null:columns;
	}

	static class ForeignKeyForColumns {
        
        protected final List columns;
        protected final ForeignKey key;

        public ForeignKeyForColumns(ForeignKey key, List columns) {
            this.key = key;
            this.columns = columns;
        }
    }

    private static Property makeProperty(String propertyName, Value value, boolean insertable, boolean updatable, boolean lazy, String cascade, String propertyAccessorName) {
		log.debug("Building property " + propertyName);
        Property prop = new Property();
		prop.setName(propertyName);
		prop.setValue(value);
		prop.setInsertable(insertable);
		prop.setUpdateable(updatable);
		prop.setLazy(lazy);		
		prop.setCascade(cascade==null?"none":cascade);
		prop.setPropertyAccessorName(propertyAccessorName==null?"property":propertyAccessorName);
		prop.setMetaAttributes(Collections.EMPTY_MAP);
		log.debug("Cascading " + propertyName + " with " + cascade);
		return prop;
	}
    

    /**
     * @param pkc
     * @param string
     * @return
     */
    private String makeUnique(Component clazz, String propertyName) {
        return makeUnique(clazz.getPropertyIterator(), propertyName);
    }

    private String makeUnique(PersistentClass clazz, String propertyName) {
        List list = new ArrayList();
                
        if( clazz.hasIdentifierProperty() ) {
            list.add( clazz.getIdentifierProperty() );
        }
        
        if( clazz.isVersioned() ) {
            list.add( clazz.getVersion() );
        }
        
        JoinedIterator iterator = new JoinedIterator( list.iterator(),clazz.getPropertyClosureIterator() );
        return makeUnique(iterator, propertyName);
    }
    /**
     * @param clazz
     * @param propertyName
     * @return
     */
    private static String makeUnique(Iterator props, String originalPropertyName) {
        int cnt = 0;
        String propertyName = originalPropertyName;
        Set uniqueNames = new HashSet();
        
        while ( props.hasNext() ) {
            Property element = (Property) props.next();
            uniqueNames.add( element.getName() );
        }
        
        while( uniqueNames.contains(propertyName) ) { 
            cnt++;
            propertyName = originalPropertyName + "_" + cnt;
        }
        
        return propertyName;                                
    }

    public static void bindCollectionSecondPass(
            Collection collection,
            java.util.Map persistentClasses,
            Mappings mappings,
            java.util.Map inheritedMetas) throws MappingException {

        if(collection.isOneToMany() ) {
            OneToMany oneToMany = (OneToMany) collection.getElement();
            PersistentClass persistentClass = mappings.getClass(oneToMany.getReferencedEntityName() ); 
            
            if (persistentClass==null) throw new MappingException(
                    "Association references unmapped class: " + oneToMany.getReferencedEntityName()
                );
            
            oneToMany.setAssociatedClass(persistentClass); // Child            
        }
        
    }

    static class JDBCCollectionSecondPass extends CollectionSecondPass {

        /**
         * @param mappings
         * @param coll
         */
        JDBCCollectionSecondPass(Mappings mappings, Collection coll) {
            super(mappings, coll);
        }

        /* (non-Javadoc)
         * @see org.hibernate.cfg.HbmBinder.SecondPass#secondPass(java.util.Map, java.util.Map)
         */
        public void secondPass(Map persistentClasses, Map inheritedMetas) throws MappingException {
            JDBCBinder.bindCollectionSecondPass(collection, persistentClasses, mappings, inheritedMetas);            
        }        
    }
}

