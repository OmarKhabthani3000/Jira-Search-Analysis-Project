package org.hibernate.tool.hbm2x.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Set;
import java.util.HashSet;

import org.hibernate.id.MultipleHiLoPerTableGenerator;
import org.hibernate.id.PersistentIdentifierGenerator;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.Formula;
import org.hibernate.mapping.KeyValue;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.Selectable;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.Subclass;
import org.hibernate.mapping.ToOne;
import org.hibernate.mapping.UniqueKey;
import org.hibernate.mapping.Value;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.OneToMany;
import org.hibernate.mapping.ManyToOne;
import org.hibernate.mapping.Table;
import org.hibernate.tool.hbm2x.Cfg2JavaTool;
import org.hibernate.util.JoinedIterator;
import org.hibernate.util.StringHelper;
import org.hibernate.cfg.Configuration;

public class EntityPOJOClass extends BasicPOJOClass {

	private PersistentClass clazz;

	public EntityPOJOClass(PersistentClass clazz, Cfg2JavaTool cfg) {
		super(clazz, cfg);
		this.clazz = clazz;
		init();
	}

	protected String getMappedClassName() {
		return clazz.getClassName();
	}

	/**
	 * @return whatever the class (or interface) extends (null if it does not extend anything)
	 */
	public String getExtends() {
		String extendz = "";

		if ( isInterface() ) {
			if ( clazz.getSuperclass() != null ) {
				extendz = clazz.getSuperclass().getClassName();
			}
			if ( clazz.getMetaAttribute( EXTENDS ) != null ) {
				if ( !"".equals( extendz ) ) {
					extendz += ",";
				}
				extendz += getMetaAsString( EXTENDS, "," );
			}
		}
		else if ( clazz.getSuperclass() != null ) {
			if ( c2j.getPOJOClass(clazz.getSuperclass()).isInterface() ) {
				// class cannot extend it's superclass because the superclass is marked as an interface
			}
			else {
				extendz = clazz.getSuperclass().getClassName();
			}
		}
		else if ( clazz.getMetaAttribute( EXTENDS ) != null ) {
			extendz = getMetaAsString( EXTENDS, "," );
		}

		return "".equals( extendz ) ? null : extendz;
	}


	public String getImplements() {
		List interfaces = new ArrayList();

		//			implement proxy, but NOT if the proxy is the class it self!
		if ( clazz.getProxyInterfaceName() != null && ( !clazz.getProxyInterfaceName().equals( clazz.getClassName() ) ) ) {
			interfaces.add( clazz.getProxyInterfaceName() );
		}

		if ( !isInterface() ) {
			if ( clazz.getSuperclass() != null && c2j.getPOJOClass(clazz.getSuperclass()).isInterface() ) {
				interfaces.add( clazz.getSuperclass().getClassName() );
			}
			if ( clazz.getMetaAttribute( IMPLEMENTS ) != null ) {
				interfaces.addAll( clazz.getMetaAttribute( IMPLEMENTS ).getValues() );
			}
			interfaces.add( Serializable.class.getName() ); // TODO: is this "nice" ? shouldn't it be a user choice ?
		}
		else {
			// interfaces can't implement suff
		}


		if ( interfaces.size() > 0 ) {
			StringBuffer sbuf = new StringBuffer();
			for ( Iterator iter = interfaces.iterator(); iter.hasNext() ; ) {
				//sbuf.append(JavaTool.shortenType(iter.next().toString(), pc.getImports() ) );
				sbuf.append( iter.next() );
				if ( iter.hasNext() ) sbuf.append( "," );
			}
			return sbuf.toString();
		}
		else {
			return null;
		}
	}

	public Iterator getAllPropertiesIterator() {
		return getAllPropertiesIterator(clazz);
	}

	public Iterator getAllPropertiesIterator(PersistentClass pc) {
		List properties = new ArrayList();
		List iterators = new ArrayList();
		if ( pc.getSuperclass() == null ) {
			// only include identifier for the root class.
			if ( pc.hasIdentifierProperty() ) {
				properties.add( pc.getIdentifierProperty() );
			}
			else if ( pc.hasEmbeddedIdentifier() ) {
				Component embeddedComponent = (Component) pc.getIdentifier();
				iterators.add( embeddedComponent.getPropertyIterator() );
			}
			/*if(clazz.isVersioned() ) { // version is already in property set
				properties.add(clazz.getVersion() );
			}*/
		}

		iterators.add( properties.iterator() );
		iterators.add( pc.getPropertyIterator() );
		Iterator[] it = (Iterator[]) iterators.toArray( new Iterator[iterators.size()] );
		return new SkipBackRefPropertyIterator( new JoinedIterator( it ) );
	}

	public boolean isComponent() {
		return false;
	}


	public boolean hasIdentifierProperty() {
		return clazz.hasIdentifierProperty();
	}
	
	public Property getIdentifierProperty() {
		return clazz.getIdentifierProperty();
	}

	public boolean needsAnnTableUniqueConstraints() {
		return ( !( clazz instanceof Subclass ) && clazz.getTable().getUniqueKeyIterator().hasNext() );
	}

	public String generateAnnTableUniqueConstraint() {
		if ( ! ( clazz instanceof Subclass ) ) {
			Table table = clazz.getTable();
			return generateAnnTableUniqueConstraint( table );
		}
		return "";
	}

	private String generateAnnTableUniqueConstraint(Table table) {
		StringBuffer constraints = new StringBuffer();
		Iterator uniqueKeys = table.getUniqueKeyIterator();
			
		while ( uniqueKeys.hasNext() ) {
			UniqueKey key = (UniqueKey) uniqueKeys.next();
			if (table.hasPrimaryKey() && table.getPrimaryKey().getColumns().equals(key.getColumns())) {
				continue;
			}
			
			String constraint = importType("javax.persistence.UniqueConstraint");
			constraints.append( "@" + constraint + "( columnNames = { " );
			
			Iterator columns = key.getColumnIterator();
			while ( columns.hasNext() ) {
				constraints.append( "\"" + ( (Column) columns.next() ).getName() + "\"" )
						.append( ", " );
			}
			constraints.setLength( constraints.length() - 2 );
			constraints.append( " } ), " );
		}
		if ( constraints.length() != 0 ) {
			constraints.setLength( constraints.length() - 2 );
		}
		return constraints.toString();
	}

	public String generateAnnIdGenerator() {
		KeyValue identifier = clazz.getIdentifier();
		String strategy = null;
		Properties properties = null;
		StringBuffer wholeString = new StringBuffer( "    " );
		if ( identifier instanceof Component ) {

			wholeString.append( "@" + importType("javax.persistence.EmbeddedId") );
		}
		else if ( identifier instanceof SimpleValue ) {
			SimpleValue simpleValue = (SimpleValue) identifier;
			strategy = simpleValue.getIdentifierGeneratorStrategy();
			properties = simpleValue.getIdentifierGeneratorProperties();
			StringBuffer id = new StringBuffer().append("@").append( importType("javax.persistence.Id") );
			
			boolean isGenericGenerator = false; //TODO: how to handle generic now??
			if ( !"assigned".equals( strategy ) ) { 
				id.append(" @").append( importType("javax.persistence.GeneratedValue") );
				if ( !"native".equals( strategy ) ) {
					id.append('(');
					if ( "identity".equals( strategy ) ) {
						id.append("strategy=");
						id.append( staticImport("javax.persistence.GenerationType", "IDENTITY" ) );
					}
					else if ( "sequence".equals( strategy ) ) {
						id.append("strategy=");
						id.append( staticImport("javax.persistence.GenerationType", "SEQUENCE") )
								.append( ", generator=\"generator\"" );
						buildAnnSequenceGenerator( wholeString, properties );
					}
					else if ( MultipleHiLoPerTableGenerator.class.getName().equals( strategy ) ) {
						id.append("strategy=");
						id.append( staticImport("javax.persistence.GenerationType", "TABLE") )
								.append( ", generator=\"generator\"" );
						buildAnnTableGenerator( wholeString, properties );
					}
					else {
						isGenericGenerator = true;
						id.append( "generator=\"generator\"" );

					}
					id.append(')');
				}
			}
			if ( isGenericGenerator ) {
				wholeString.append( "@" + importType("org.hibernate.annotations.GenericGenerator"))
						.append( "(name=\"generator\", strategy=\"" )
						.append( strategy )
						.append( "\", " );
				wholeString.append( "parameters = {  " );
				if ( properties != null ) {
					Enumeration propNames = properties.propertyNames();
					while ( propNames.hasMoreElements() ) {
						String propertyName = (String) propNames.nextElement();
						wholeString.append( "@" + importType("org.hibernate.annotations.Parameter"))
								.append( "(name=\"" )
								.append( propertyName )
								.append( "\", " )
								.append( "value=\"" )
								.append( properties.getProperty( propertyName ) )
								.append( "\"), " );
					}
				}
				wholeString.setLength( wholeString.length() - 2 );
				wholeString.append( " } )\n" );
			}
			wholeString.append( id );
		}
		return wholeString.toString();
	}

	private void buildAnnSequenceGenerator(StringBuffer wholeString, Properties properties) {
		wholeString.append( "@" + importType("javax.persistence.SequenceGenerator") + "(name=\"generator\", sequenceName=\"" )
				.append( properties.getProperty( org.hibernate.id.SequenceGenerator.SEQUENCE, "" ) )
				.append( "\")" );
		//TODO HA does not support initialValue and allocationSize
		wholeString.append( "\n    " );
	}

	private void buildAnnTableGenerator(StringBuffer wholeString, Properties properties) {
		wholeString.append( "@" + importType("javax.persistence.TableGenerator") + "(name=\"generator\", table=\"" )
				.append( properties.getProperty( "generatorTableName", "hibernate_sequences" ) )
				.append("\"");
		if ( ! isPropertyDefault( PersistentIdentifierGenerator.CATALOG, properties ) ) {
			wholeString.append(", catalog=\"")
				.append( properties.getProperty( PersistentIdentifierGenerator.CATALOG, "") );
		}
		if ( ! isPropertyDefault( PersistentIdentifierGenerator.SCHEMA, properties ) ) {
			wholeString.append(", schema=\"")
				.append( properties.getProperty( PersistentIdentifierGenerator.SCHEMA, "") );
		}
		if (! isPropertyDefault( MultipleHiLoPerTableGenerator.PK_VALUE_NAME, properties ) ) {
			wholeString.append(", pkColumnValue=\"")
					.append(properties.getProperty( MultipleHiLoPerTableGenerator.PK_VALUE_NAME, "" ) ).append("\"");
		}
		if ( ! isPropertyDefault( MultipleHiLoPerTableGenerator.MAX_LO, properties, "50" ) ) {
			wholeString.append(", allocationSize=")
					.append(properties.getProperty( MultipleHiLoPerTableGenerator.MAX_LO, "50" ) );
		}
		if (! isPropertyDefault( MultipleHiLoPerTableGenerator.PK_COLUMN_NAME, properties ) ) {
			wholeString.append(", pkColumnName=\"")
					.append( properties.getProperty( MultipleHiLoPerTableGenerator.PK_COLUMN_NAME, "" ) )
					.append( "\"");
		}
		if (! isPropertyDefault( MultipleHiLoPerTableGenerator.VALUE_COLUMN_NAME, properties ) ) {
			wholeString.append( ", valueColumnName=\"")
					.append( properties.getProperty( MultipleHiLoPerTableGenerator.VALUE_COLUMN_NAME, "" ) )
					.append("\"");
		}
		wholeString.append( ")\n    " );
	}

	private boolean isPropertyDefault(String property, Properties properties) {
		return StringHelper.isEmpty( properties.getProperty( property ) );
	}

	private boolean isPropertyDefault(String property, Properties properties, String defaultValue) {
		String propertyValue = properties.getProperty( property );
		return propertyValue != null && propertyValue.equals( defaultValue );
	}

	public String generateJoinColumnsAnnotation(Property property) {
		boolean insertable = property.isInsertable();
		boolean updatable = property.isUpdateable();
		Value value = property.getValue();
		int span;
		Iterator columnIterator;
		if (value != null && value instanceof Collection) {
			Collection collection = (Collection) value;
			span = collection.getKey().getColumnSpan();
			columnIterator = collection.getKey().getColumnIterator();
		}
		else {
			span = property.getColumnSpan();
			columnIterator = property.getColumnIterator();
		}

		StringBuffer annotations = new StringBuffer( "    " );
		if ( span == 1 ) {
				Selectable selectable = (Selectable) columnIterator.next();
				buildJoinColumnAnnotation( selectable, annotations, insertable, updatable );
		}
		else {
			Iterator columns = columnIterator;
			annotations.append("@").append( importType("javax.persistence.JoinColumns") ).append("( { " );
			buildArrayOfJoinColumnAnnotation( columns, annotations, insertable, updatable );
			annotations.append( " } )" );
		}
		return annotations.toString();
	}

	private void buildArrayOfJoinColumnAnnotation(
			Iterator columns, StringBuffer annotations, boolean insertable,
			boolean updatable
	) {
		while ( columns.hasNext() ) {
			Selectable selectable = (Selectable) columns.next();

			if ( selectable.isFormula() ) {
				//TODO formula in multicolumns not supported by annotations
				//annotations.append("/* TODO formula in multicolumns not supported by annotations */");
			}
			else {
				annotations.append( "\n        " );
				buildJoinColumnAnnotation( selectable, annotations, insertable, updatable );
				annotations.append( ", " );
			}
		}
		annotations.setLength( annotations.length() - 2 );
	}

	private void buildJoinColumnAnnotation(
			Selectable selectable, StringBuffer annotations, boolean insertable, boolean updatable
	) {
		if ( selectable.isFormula() ) {
			//TODO not supported by HA
		}
		else {
			Column column = (Column) selectable;
			annotations.append("@").append( importType("javax.persistence.JoinColumn") )
					.append("(name=\"" ).append( column.getName() ).append( "\"" )
					//TODO handle referenced column name, this is a hard one
					//.append(", referencedColumnName=")
					.append( ", unique=" ).append( column.isUnique() )
					.append( ", nullable=" ).append( column.isNullable() )
					.append( ", insertable=" ).append( insertable )
					.append( ", updatable=" ).append( updatable );
			String sqlType = column.getSqlType();
			if ( StringHelper.isNotEmpty( sqlType ) ) {
				annotations.append( ", columnDefinition=\"" ).append( sqlType ).append( "\"" );
			}
			//TODO support secondary table
			annotations.append( ")" );
		}
	}

	public String getCascadeType(Property property) {
		StringTokenizer st =  new StringTokenizer( property.getCascade(), ", ", false );
		String cascadeType = null;
		StringBuffer cascade = new StringBuffer();
		while ( st.hasMoreElements() ) {
			String element = ( (String) st.nextElement() ).toLowerCase();
			if ( "persist".equals( element ) ) {
				if (cascadeType == null) cascadeType = importType( "javax.persistence.CascadeType");
				cascade.append( cascadeType ).append(".PERSIST").append(", ");
			}
			else if ( "merge".equals( element ) ) {
				if (cascadeType == null) cascadeType = importType( "javax.persistence.CascadeType");
				cascade.append( cascadeType ).append(".MERGE").append(", ");
			}
			else if ( "delete".equals( element ) ) {
				if (cascadeType == null) cascadeType = importType( "javax.persistence.CascadeType");
				cascade.append( cascadeType ).append(".REMOVE").append(", ");
			}
			else if ( "refresh".equals( element ) ) {
				if (cascadeType == null) cascadeType = importType( "javax.persistence.CascadeType");
				cascade.append( cascadeType ).append(".REFRESH").append(", ");
			}
			else if ( "all".equals( element ) ) {
				if (cascadeType == null) cascadeType = importType( "javax.persistence.CascadeType");
				cascade.append( cascadeType ).append(".ALL").append(", ");
			}
		}
		if ( cascade.length() >= 2 ) {
			cascade.setLength( cascade.length() - 2 );
		}
		return cascade.toString();
	}

	public String getHibernateCascadeTypeAnnotation(Property property) {
		StringTokenizer st =  new StringTokenizer( property.getCascade(), ", ", false );
		String cascadeType = null;
		StringBuffer cascade = new StringBuffer();
		while ( st.hasMoreElements() ) {
			String element = ( (String) st.nextElement() ).toLowerCase();
			if ( "all-delete-orphan".equals( element ) ) {
				if (cascadeType == null) cascadeType = importType( "org.hibernate.annotations.CascadeType");
				cascade.append( cascadeType ).append(".ALL").append(", ")
						.append( cascadeType ).append(".DELETE_ORPHAN").append(", ");
			}
			else if ( "delete-orphan".equals( element ) ) {
				if (cascadeType == null) cascadeType = importType( "org.hibernate.annotations.CascadeType");
				cascade.append( cascadeType ).append(".DELETE_ORPHAN").append(", ");
			}
			else if ( "save-update".equals( element ) ) {
				if (cascadeType == null) cascadeType = importType( "org.hibernate.annotations.CascadeType");
				cascade.append( cascadeType ).append(".SAVE_UPDATE").append(", ");
			}
			else if ( "replicate".equals( element ) ) {
				if (cascadeType == null) cascadeType = importType( "org.hibernate.annotations.CascadeType");
				cascade.append( cascadeType ).append(".REPLICATE").append(", ");
			}
			else if ( "lock".equals( element ) ) {
				if (cascadeType == null) cascadeType = importType( "org.hibernate.annotations.CascadeType");
				cascade.append( cascadeType ).append(".LOCK").append(", ");
			}
			else if ( "evict".equals( element ) ) {
				if (cascadeType == null) cascadeType = importType( "org.hibernate.annotations.CascadeType");
				cascade.append( cascadeType ).append(".EVICT").append(", ");
			}
		}
		if ( cascade.length() >= 2 ) {
			String hibernateCascade = importType("org.hibernate.annotations.Cascade");
			cascade.insert(0, "@" + hibernateCascade + "( {");
			cascade.setLength( cascade.length() - 2 );
			cascade.append("} )");
		}
		return cascade.toString();
	}

	public String getFetchType(Property property) {
		Value value = property.getValue();
		String fetchType = importType( "javax.persistence.FetchType");
		boolean lazy = false;
		if ( value instanceof ToOne ) {
			lazy = ( (ToOne) value ).isLazy();
		}
		else if ( value instanceof Collection ) {
			lazy = ( (Collection) value ).isLazy();
		}
		else {
			//we're not collection neither *toone so we are looking for property fetching
			lazy = property.isLazy();
		}
		if ( lazy ) {
			return fetchType + "." + "LAZY";
		}
		else {
			return fetchType + "." + "EAGER";
		}
	}


	public Object getDecoratedObject() {
		return clazz;
	}

	public String generateCollectionAnnotation(Property property, Configuration cfg) {
		StringBuffer annotation = new StringBuffer();
		Value value = property.getValue();
		if ( value != null && value instanceof Collection) {
			Collection collection = (Collection) value;
			if ( collection.isOneToMany() ) {
				String mappedBy = null;
				annotation.append("    @").append( importType( "javax.persistence.OneToMany") )
						.append( "(cascade={").append(getCascadeType( property ) ).append("}")
						.append(", fetch=").append( getFetchType( property ) );
				if ( collection.isInverse() ) {
					annotation.append(", mappedBy=\"");
					Iterator joinColumnsIt = collection.getKey().getColumnIterator();
					Set joinColumns = new HashSet();
					while ( joinColumnsIt.hasNext() ) {
						joinColumns.add( joinColumnsIt.next() );
					}
					OneToMany oneToMany = (OneToMany) collection.getElement();
					PersistentClass pc = cfg.getClassMapping( oneToMany.getReferencedEntityName() );
					Iterator properties = pc.getPropertyClosureIterator();
					//TODO we should check the table too
					boolean isOtherSide = false;
					mappedBy = "unresolved";
					while ( ! isOtherSide && properties.hasNext() ) {
						Property manyProperty = (Property) properties.next();
						Value manyValue = manyProperty.getValue();
						if ( manyValue != null && manyValue instanceof ManyToOne ) {
							if ( joinColumns.size() == manyValue.getColumnSpan() ) {
								isOtherSide = true;
								Iterator it = manyValue.getColumnIterator();
								while ( it.hasNext() ) {
									Object column = it.next();
									if (! joinColumns.contains( column ) ) {
										isOtherSide = false;
										break;
									}
								}
								if (isOtherSide) {
									mappedBy = manyProperty.getName();
								}
							}

						}
					}
					annotation.append( mappedBy ).append("\"");
				}
				annotation.append(")");
				if (mappedBy == null) annotation.append("\n").append( generateJoinColumnsAnnotation(property) );
			}
			else {
				//TODO do the @OneToMany @JoinTable
				//TODO composite element
				String mappedBy = null;
				annotation.append("    @").append( importType( "javax.persistence.ManyToMany") )
						.append( "(cascade={").append(getCascadeType( property ) ).append("}")
						.append(", fetch=").append( getFetchType( property ) );
				if ( collection.isInverse() ) {
					annotation.append(", mappedBy=\"");
					Iterator joinColumnsIt = collection.getKey().getColumnIterator();
					Set joinColumns = new HashSet();
					while ( joinColumnsIt.hasNext() ) {
						joinColumns.add( joinColumnsIt.next() );
					}
					ManyToOne manyToOne = (ManyToOne) collection.getElement();
					PersistentClass pc = cfg.getClassMapping( manyToOne.getReferencedEntityName() );
					Iterator properties = pc.getPropertyClosureIterator();
					//TODO we should check the table too
					boolean isOtherSide = false;
					mappedBy = "unresolved";
					while ( ! isOtherSide && properties.hasNext() ) {
						Property collectionProperty = (Property) properties.next();
						Value collectionValue = collectionProperty.getValue();
						if ( collectionValue != null && collectionValue instanceof Collection ) {
							Collection realCollectionValue = (Collection) collectionValue;
							if ( ! realCollectionValue.isOneToMany() ) {
								if ( joinColumns.size() == realCollectionValue.getElement().getColumnSpan() ) {
									isOtherSide = true;
									Iterator it = realCollectionValue.getElement().getColumnIterator();
									while ( it.hasNext() ) {
										Object column = it.next();
										if (! joinColumns.contains( column ) ) {
											isOtherSide = false;
											break;
										}
									}
									if (isOtherSide) {
										mappedBy = collectionProperty.getName();
									}
								}
							}
						}
					}
					annotation.append( mappedBy ).append("\"");
				}
				annotation.append(")");
				if (mappedBy == null) {
					annotation.append("\n    @");
					annotation.append( importType( "javax.persistence.JoinTable") ).append( "(name=\"" );
					Table table = collection.getCollectionTable();
					annotation.append( table.getName() );
					annotation.append( "\"" );
					if ( StringHelper.isNotEmpty( table.getSchema() ) ) {
						annotation.append(", schema=\"").append( table.getSchema() ).append("\"");
					}
					if ( StringHelper.isNotEmpty( table.getCatalog() ) ) {
						annotation.append(", catalog=\"").append( table.getCatalog() ).append("\"");
					}
					String uniqueConstraint = generateAnnTableUniqueConstraint(table);
					if ( uniqueConstraint.length() > 0 ) {
						annotation.append(", uniqueConstraints={").append(uniqueConstraint).append("}");
					}
					annotation.append( ", joinColumns = { ");
					buildArrayOfJoinColumnAnnotation(
							collection.getKey().getColumnIterator(),
							annotation,
							property.isInsertable(),
							property.isUpdateable()
					);
					annotation.append( " }");
					annotation.append( ", inverseJoinColumns = { ");
					buildArrayOfJoinColumnAnnotation(
							collection.getElement().getColumnIterator(),
							annotation,
							property.isInsertable(),
							property.isUpdateable()
					);
					annotation.append( " }");
					annotation.append(")");
				}

			}
			String hibernateCascade = getHibernateCascadeTypeAnnotation( property );
			if (hibernateCascade.length() > 0) annotation.append("\n    ").append(hibernateCascade);
		}
		return annotation.toString();
	}
	
	public boolean isSubclass() {
		return clazz.getSuperclass()!=null; 
	}
	
	public List getPropertyClosureForFullConstructor() {
		return getPropertyClosureForFullConstructor(clazz);
	}
	
	protected List getPropertyClosureForFullConstructor(PersistentClass pc) {
		List l = new ArrayList(getPropertyClosureForSuperclassFullConstructor( pc ));
		l.addAll(getPropertiesForFullConstructor( pc ));
		return l;
	}

	public List getPropertiesForFullConstructor() {
		return getPropertiesForFullConstructor(clazz);
	}
	
	protected List getPropertiesForFullConstructor(PersistentClass pc) {
		List result = new ArrayList();
		
		for ( Iterator myFields = getAllPropertiesIterator(pc); myFields.hasNext() ; ) {
			Property field = (Property) myFields.next();
			// TODO: if(!field.isGenerated() ) ) {
			if(field.equals(pc.getIdentifierProperty()) && !isAssignedIdentifier(pc, field)) {
				continue; // dont add non assigned identifiers
			} else if(field.equals(pc.getVersion())) {
				continue; // version prop
			} else if(field.isBackRef()) {
				continue;
			} else if(isFormula(field)) {
				continue;
			} else if(isToBeGenerated(field)) {
				continue;
			} else {
				result.add( field );	
			}
		}
		
		return result;
	}

	private boolean isFormula(Property field) {
		Value value = field.getValue();
		boolean foundFormula = false; 
		
		if(value!=null && value.getColumnSpan()>0) {
			Iterator columnIterator = value.getColumnIterator();
			while ( columnIterator.hasNext() ) {
				Selectable element = (Selectable) columnIterator.next();
				if(!(element instanceof Formula)) {
					return false;
				} else {
					foundFormula = true;
				}
			}
		} else {
			return false;
		}
		return foundFormula;
	}

	private boolean isToBeGenerated(Property field){
		return !"false".equals(field.getMetaAttribute(GEN_PROPERTY));		
	}
	
	public List getPropertyClosureForSuperclassFullConstructor() {
		return getPropertyClosureForSuperclassFullConstructor(clazz);
	}
	
	public List getPropertyClosureForSuperclassFullConstructor(PersistentClass pc) {
		List result = new ArrayList();
		if ( pc.getSuperclass() != null ) {
			// The correct sequence is vital here, as the subclass should be
			// able to invoke the fullconstructor based on the sequence returned
			// by this method!
			result.addAll( getPropertyClosureForSuperclassFullConstructor( pc.getSuperclass() ) );
			result.addAll( getPropertiesForFullConstructor( pc.getSuperclass() ) );
		}

		return result;
	}
	
	
	public List getPropertyClosureForMinimalConstructor() {
		return getPropertyClosureForMinimalConstructor(clazz);
	}
	
	protected List getPropertyClosureForMinimalConstructor(PersistentClass pc) {
		List l = new ArrayList(getPropertyClosureForSuperclassMinConstructor( pc ));
		l.addAll(getPropertiesForMinimalConstructor( pc ));
		return l;
	}

	public List getPropertiesForMinimalConstructor() {
		return getPropertiesForMinimalConstructor(clazz);
	}
	
	protected List getPropertiesForMinimalConstructor(PersistentClass pc) {
		List result = new ArrayList();
		
		for ( Iterator myFields = getAllPropertiesIterator(pc); myFields.hasNext() ; ) {
			Property property = (Property) myFields.next();
			if(property.equals(pc.getIdentifierProperty())) {
				if(isAssignedIdentifier(pc, property)) {
					result.add(property);
				} else {
					continue; 
				}				
			} else if (property.equals(pc.getVersion())) {
				continue; // the version property should not be in the result.
			} else if( isRequiredInConstructor(property) ) {
				result.add(property);
			}			
		}
		
		return result;
	}

	protected boolean isAssignedIdentifier(PersistentClass pc, Property property) {
		if(property.equals(pc.getIdentifierProperty())) {
			if(property.getValue().isSimpleValue()) {
				SimpleValue sv = (SimpleValue) property.getValue();
				if("assigned".equals(sv.getIdentifierGeneratorStrategy())) {
					return true;
				}
			}								
		}
		return false;
	}

	public List getPropertyClosureForSuperclassMinimalConstructor() {
		return getPropertyClosureForSuperclassMinConstructor(clazz);
	}
	
	protected List getPropertyClosureForSuperclassMinConstructor(PersistentClass pc) {
		List result = new ArrayList();
		if ( pc.getSuperclass() != null ) {
			// The correct sequence is vital here, as the subclass should be
			// able to invoke the fullconstructor based on the sequence returned
			// by this method!
			result.addAll( getPropertyClosureForSuperclassMinConstructor( pc.getSuperclass() ) );
			result.addAll( getPropertiesForMinimalConstructor( pc.getSuperclass() ) );
		}

		return result;
	}
	
	public POJOClass getSuperClass(){
		return new EntityPOJOClass(clazz.getSuperclass(),c2j);
	}
	
	public String toString() {
		return getClass().getName() + "(" + (clazz==null?"<none>":clazz.getEntityName()) + ")";
	}
	
	public boolean hasVersionProperty() {
		return clazz.isVersioned() && clazz instanceof RootClass;
	}
}
