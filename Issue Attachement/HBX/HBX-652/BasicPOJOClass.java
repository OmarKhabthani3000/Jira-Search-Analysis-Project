package org.hibernate.tool.hbm2x.pojo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.cfg.reveng.ReverseEngineeringStrategyUtil;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.MetaAttributable;
import org.hibernate.mapping.MetaAttribute;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.PropertyGeneration;
import org.hibernate.mapping.Selectable;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.Value;
import org.hibernate.tool.hbm2x.Cfg2JavaTool;
import org.hibernate.tool.hbm2x.MetaAttributeConstants;
import org.hibernate.tool.hbm2x.MetaAttributeHelper;
import org.hibernate.util.StringHelper;

/**
 * Abstract implementation of POJOClass. To be extended by ComponentPOJO and EntityPOJO
 * @author max
 * @author <a href="mailto:abhayani@jboss.org">Amit Bhayani</a>
 *
 */
abstract public class BasicPOJOClass implements POJOClass, MetaAttributeConstants {

	protected ImportContext importContext;
	protected MetaAttributable meta;
	protected final Cfg2JavaTool c2j;
	
	public BasicPOJOClass(MetaAttributable ma, Cfg2JavaTool c2j) {
		this.meta = ma;
		this.c2j = c2j;		
		
		if(this.meta==null) {
			throw new IllegalArgumentException("class Argument must be not null");
		}
		if(this.c2j==null) throw new IllegalArgumentException("c2j must be not null");
	}
	
	// called by subclasses
	protected void init() {
		importContext = new ImportContextImpl(getPackageName());
		
		MetaAttribute metaAttribute = meta.getMetaAttribute("extra-import");
		if(metaAttribute!=null) {
			Iterator values = metaAttribute.getValues().iterator();
			while ( values.hasNext() ) {
				String element = (String) values.next();
				importContext.importType(element);				
			}
		}	
	}
	
	protected String getPackageDeclaration(String pkgName) {
		if (pkgName!=null && pkgName.trim().length()!=0 ) {
			return "package " + pkgName + ";";
		} 
		else {        
			return "// default package";
		}
	}

	public String getPackageDeclaration() {
		String pkgName = getPackageName();
		return getPackageDeclaration(pkgName);			
	}

	/** Return package name. Note: Does not handle inner classes */ 
	public String getPackageName() {
		String generatedClass = getMetaAsString(MetaAttributeConstants.GENERATED_CLASS).trim();
		if(StringHelper.isEmpty(generatedClass) ) {
			generatedClass = getMappedClassName();
		}
		if(generatedClass==null) return ""; // will occur for <dynamic-component>
		return StringHelper.qualifier(generatedClass.trim());
	}
	
	public String getShortName() {
		return StringHelper.unqualify(getMappedClassName());
	}
	
	public String getQualifiedDeclarationName() {
		String generatedName = getMetaAsString( MetaAttributeConstants.GENERATED_CLASS );
		if ( generatedName == null || generatedName.trim().length() == 0 ) {
			generatedName = getMappedClassName();
		}

		generatedName = generatedName.replace( '$', '.' ).trim();
		String qualifier = StringHelper.qualifier( getMappedClassName() );
		if ( "".equals( qualifier ) ) {
			return qualifier + "." + generatedName;
		}
		else {
			return generatedName;
		}
	}
	
	/**
	 * @return unqualified classname for this class (can be changed by meta attribute "generated-class")
	 */
	public String getDeclarationName() {
		return StringHelper.unqualify( getQualifiedDeclarationName() );
	}
	
	protected abstract String getMappedClassName();

	public String getMetaAsString(String attribute) {
		MetaAttribute c = meta.getMetaAttribute( attribute );
		return MetaAttributeHelper.getMetaAsString( c );
	}

	public boolean hasMetaAttribute(String attribute) {
		return meta.getMetaAttribute( attribute ) != null;
	}

	public String getMetaAsString(String attribute, String seperator) {
		return MetaAttributeHelper.getMetaAsString( meta.getMetaAttribute( attribute ), seperator );
	}

	public boolean getMetaAsBool(String attribute) {
		return getMetaAsBool( attribute, false );
	}

	public boolean getMetaAsBool(String attribute, boolean defaultValue) {
		return MetaAttributeHelper.getMetaAsBool( meta.getMetaAttribute( attribute ), defaultValue );
	}

	public String getClassJavaDoc(String fallback, int indent) {
		MetaAttribute c = meta.getMetaAttribute( CLASS_DESCRIPTION );
		if ( c == null ) {
			return c2j.toJavaDoc( fallback, indent );
		}
		else {
			return c2j.toJavaDoc( getMetaAsString( CLASS_DESCRIPTION ), indent );
		}
	}
	
	public String getClassModifiers() {
		String classModifiers = null;

		// Get scope (backwards compatibility)
		if ( meta.getMetaAttribute( SCOPE_CLASS ) != null ) {
			classModifiers = getMetaAsString( SCOPE_CLASS ).trim();
		}

		// Get modifiers
		if ( meta.getMetaAttribute( CLASS_MODIFIER ) != null ) {
			classModifiers = getMetaAsString( CLASS_MODIFIER ).trim();
		}
		return classModifiers == null ? "public" : classModifiers;
	}

	public String getDeclarationType() {
		boolean isInterface = isInterface();
		if ( isInterface ) {
			return INTERFACE;
		}
		else {
			return "class";
		}
	}
	
	public boolean isInterface() {
		return getMetaAsBool( INTERFACE );
	}
	
	public String getExtendsDeclaration() {
		String extendz = getExtends();
		if ( extendz == null || extendz.trim().length() == 0 ) {
			return "";
		}
		else {
			return "extends " + extendz;
		}
	}

	public String getImplementsDeclaration() {
		String implementz = getImplements();
		if ( implementz == null || implementz.trim().length() == 0 ) {
			return "";
		}
		else {
			return "implements " + implementz;
		}
	}
	
	public String generateEquals(String thisName, String otherName, boolean useGenerics) {
		Iterator allPropertiesIterator = getEqualsHashCodePropertiesIterator();
		return generateEquals( thisName, otherName, allPropertiesIterator, useGenerics );
	}
	
	public abstract Iterator getAllPropertiesIterator();

	protected String generateEquals(String thisName, String otherName, Iterator allPropertiesIterator, boolean useGenerics) {
		StringBuffer buf = new StringBuffer();
		while ( allPropertiesIterator.hasNext() ) {
			Property property = (Property) allPropertiesIterator.next();
				if ( buf.length() > 0 ) buf.append( "\n && " );
				String javaTypeName = c2j.getJavaTypeName( property, useGenerics, this );
				buf.append(
						internalgenerateEquals(
								javaTypeName, thisName + "." + getGetterSignature( property ) + "()",
								otherName + "." + getGetterSignature( property ) + "()")
				);			
		}

		if ( buf.length() == 0 ) {
			return "false";
		}
		else {
			return buf.toString();
		}
	}

	private boolean usePropertyInEquals(Property property) {
		boolean hasEqualsMetaAttribute = c2j.hasMetaAttribute(property, "use-in-equals");		
		boolean useInEquals = c2j.getMetaAsBool( property, "use-in-equals" );
		
		if(property.isNaturalIdentifier()) {
			if(hasEqualsMetaAttribute && !useInEquals) {
				return false;
			} else {
				return true;
			}
		} 
		
		return useInEquals;
	}

	private boolean useCompareTo(String javaTypeName) {
		// Fix for HBX-400
		if ("java.math.BigDecimal".equals(javaTypeName)) {
			return true;
		} else {
			return false;
		}
	} 


	private String internalgenerateEquals(String typeName, String lh, String rh) {
		if ( c2j.isPrimitive( typeName ) ) {
			return "(" + lh + "==" + rh + ")";
		}
		else {
			if(useCompareTo( typeName )) {
				return "( (" + lh + "==" + rh + ") || ( " + lh + "!=null && " + rh + "!=null && " + lh + ".compareTo(" + rh + ")==0 ) )";
			} else {
				if(typeName.endsWith("[]")) {
					importType("java.util.Arrays");
					return "( (" + lh + "==" + rh + ") || ( " + lh + "!=null && " + rh + "!=null && Arrays.equals(" + lh + ", " + rh + ") ) )";
				} else {
					return "( (" + lh + "==" + rh + ") || ( " + lh + "!=null && " + rh + "!=null && " + lh + ".equals(" + rh + ") ) )";
				}
			}

		}
	}

	public String getExtraClassCode() {
		return getMetaAsString( "class-code", "\n" );
	}
	
	private boolean needsEqualsHashCode(Iterator iter) {
		while ( iter.hasNext() ) {
			Property element = (Property) iter.next();
			if ( usePropertyInEquals( element ) ) {
				return true;
			}
		}
		return false;
	}

	public boolean needsEqualsHashCode() {
		Iterator iter = getAllPropertiesIterator();
		return needsEqualsHashCode( iter );
	}

	public abstract String getExtends();
	
	public abstract String getImplements();

	
	public String importType(String fqcn) {
		return importContext.importType(fqcn);
	}
	
	public String generateImports() {
		return importContext.generateImports();
	}

	public String staticImport(String fqcn, String member) {
		return importContext.staticImport(fqcn, member);
	}
	
	public String generateBasicAnnotation(Property property) {
		StringBuffer annotations = new StringBuffer( "    " );
		if(property.getValue() instanceof SimpleValue) {
			String typeName = ((SimpleValue)property.getValue()).getTypeName();
			if("date".equals(typeName) || "java.sql.Date".equals(typeName)) {
				buildTemporalAnnotation( annotations, "DATE" );
			} else if ("timestamp".equals(typeName) || "java.sql.Timestamp".equals(typeName)) {
				//buildTemporalAnnotation( annotations, "TIMESTAMP" ); ..the default so don't generate
			} else if ("time".equals(typeName) || "java.sql.Time".equals(typeName)) {
				buildTemporalAnnotation(annotations, "TIME");
			} //TODO: calendar etc. ?
		}
			
		return annotations.toString();
	}

	private StringBuffer buildTemporalAnnotation(StringBuffer annotations, String temporalTypeValue) {
		String temporal = importType("javax.persistence.Temporal");
		String temporalType = importType("javax.persistence.TemporalType");
		
		return annotations.append( "@" + temporal +"(" + temporalType + "." + temporalTypeValue + ")");
	}
	
	public String generateAnnColumnAnnotation(Property property) {
		StringBuffer annotations = new StringBuffer( "    " );
		boolean insertable = property.isInsertable();
		boolean updatable = property.isUpdateable();
		if ( property.isComposite() ) {
			annotations.append( "@" + importType("javax.persistence.AttributeOverrides") +"( {" );
			Component component = (Component) property.getValue();
			Iterator subElements = component.getPropertyIterator();
			buildRecursiveAttributeOverride( subElements, null, property, annotations );
			annotations.setLength( annotations.length() - 2 );
			annotations.append( " } )" );
		}
		else {
			if ( property.getColumnSpan() == 1 ) {
				Selectable selectable = (Selectable) property.getColumnIterator().next();
				buildColumnAnnotation( selectable, annotations, insertable, updatable );				
			}
			else {
				Iterator columns = property.getColumnIterator();
				annotations.append("@").append( importType("org.hibernate.annotations.Columns") ).append("( { " );
				while ( columns.hasNext() ) {
					Selectable selectable = (Selectable) columns.next();
	
					if ( selectable.isFormula() ) {
						//TODO formula in multicolumns not supported by annotations
						//annotations.append("/* TODO formula in multicolumns not supported by annotations */");
					}
					else {
						annotations.append( "\n        " );
						buildColumnAnnotation( selectable, annotations, insertable, updatable );
						annotations.append( ", " );
					}
				}
				annotations.setLength( annotations.length() - 2 );
				annotations.append( " } )" );
			}
		}
		return annotations.toString();
	}

	private void buildRecursiveAttributeOverride(Iterator subElements, String path, Property property, StringBuffer annotations) {
		while ( subElements.hasNext() ) {
			Property subProperty = (Property) subElements.next();
			if ( subProperty.isComposite() ) {
				if ( path != null ) {
					path = path + ".";
				}
				else {
					path = "";
				}
				path = path + subProperty.getName();
				Component component = (Component) subProperty.getValue();
				buildRecursiveAttributeOverride( component.getPropertyIterator(), path, subProperty, annotations );
			}
			else {
				Iterator columns = subProperty.getColumnIterator();
				Selectable selectable = (Selectable) columns.next();
				if ( selectable.isFormula() ) {
					//TODO formula in multicolumns not supported by annotations
				}
				else {
					annotations.append( "\n        " ).append("@")
							.append( importType("javax.persistence.AttributeOverride") ).append("(name=\"" );
					if ( path != null ) {
						annotations.append( path ).append( "." );
					}
					annotations.append( subProperty.getName() ).append( "\"" )
							.append( ", column=" );
					buildColumnAnnotation(
							selectable, annotations, subProperty.isInsertable(), subProperty.isUpdateable()
					);
					annotations.append( " ), " );
				}
			}
		}
	}

	private void buildColumnAnnotation(Selectable selectable, StringBuffer annotations, boolean insertable, boolean updatable) {
		if ( selectable.isFormula() ) {
			annotations.append("@").append( importType("org.hibernate.annotations.Formula") )
					.append("(value=\"" ).append( selectable.getText() ).append( "\")" );
		}
		else {
			Column column = (Column) selectable;
			annotations.append( "@" + importType("javax.persistence.Column") + "(name=\"" ).append( column.getName() ).append( "\"" )
					.append( ", unique=" ).append( column.isUnique() )
					.append( ", nullable=" ).append( column.isNullable() )
					.append( ", insertable=" ).append( insertable )
					.append( ", updatable=" ).append( updatable );
			
			if (column.getPrecision() != Column.DEFAULT_PRECISION) {
				annotations.append( ", precision=" ).append( column.getPrecision() );
			}
			if (column.getScale() != Column.DEFAULT_SCALE) {
				annotations.append( ", scale=" ).append( column.getScale() );
			}
			else if (column.getLength() != Column.DEFAULT_LENGTH){
				annotations.append( ", length=" ).append( column.getLength() );
			}
			
					
					
			String sqlType = column.getSqlType();
			if ( StringHelper.isNotEmpty( sqlType ) ) {
				annotations.append( ", columnDefinition=\"" ).append( sqlType ).append( "\"" );
			}
			//TODO support secondary table
			annotations.append( ")" );
		}
	}


	public Iterator getToStringPropertiesIterator() {
		Iterator iter = getAllPropertiesIterator();
		return getToStringPropertiesIterator( iter );
	}

	private Iterator getToStringPropertiesIterator(Iterator iter) {
		List properties = new ArrayList();

		while ( iter.hasNext() ) {
			Property element = (Property) iter.next();
			if ( c2j.getMetaAsBool( element, "use-in-tostring" ) ) {
				properties.add( element );
			}
		}

		return properties.iterator();
	}

	public Iterator getEqualsHashCodePropertiesIterator() {
		Iterator iter = getAllPropertiesIterator();
		return getEqualsHashCodePropertiesIterator(iter);
	}

	private Iterator getEqualsHashCodePropertiesIterator(Iterator iter) {
		List properties = new ArrayList();

		while ( iter.hasNext() ) {
			Property element = (Property) iter.next();
			if ( usePropertyInEquals(element) ) {
				properties.add( element );
			}
		}

		return properties.iterator();
	}

	public boolean needsToString() {
		Iterator iter = getAllPropertiesIterator();
		return needsToString( iter );
	}
	
	private boolean needsToString(Iterator iter) {
		while ( iter.hasNext() ) {
			Property element = (Property) iter.next();
			if ( c2j.getMetaAsBool( element, "use-in-tostring" ) ) {
				return true;
			}
		}
		return false;
	}

	public boolean hasMetaAttribute(MetaAttributable pc, String attribute) {
		return pc.getMetaAttribute( attribute ) != null;
	}

	public boolean getMetaAttribAsBool(MetaAttributable pc, String attribute, boolean defaultValue) {
		return MetaAttributeHelper.getMetaAsBool( pc.getMetaAttribute( attribute ), defaultValue );
	}
	
	public boolean hasFieldJavaDoc(Property property) {
		return property.getMetaAttribute("field-description")!=null;
	}
	
	public String getFieldJavaDoc(Property property, int indent) {
		MetaAttribute c = property.getMetaAttribute( "field-description" );
		if ( c == null ) {
			return c2j.toJavaDoc( "", indent );
		}
		else {
			return c2j.toJavaDoc( c2j.getMetaAsString( property, "field-description" ), indent );
		}
	}
	
	public String getFieldDescription(Property property){
		MetaAttribute c = property.getMetaAttribute( "field-description" );
		if ( c == null ) {
			return "";
		}
		else {
			return c2j.getMetaAsString( property, "field-description" );
		}		
	}

	/**
	 * Method getGetterSignature.
	 *
	 * @return String
	 */
	public String getGetterSignature(Property p) {
		String prefix = c2j.getJavaTypeName( p, false).equals( "boolean" ) ? "is" : "get";
		return prefix + beanCapitalize( p.getName() );
	}

	/**
	 * @param p
	 * @return foo -> Foo, FOo -> FOo
	 */
	public String getPropertyName(Property p) {
		return beanCapitalize( p.getName() );
	}


	// get the "opposite" collectionnae for a property. Currently a "hack" that just uses the same naming algorithm as in reveng, will fail on more general models!
	public String getCollectionNameFor(Property property) {
		String str = getPropertyName(property);
		return ReverseEngineeringStrategyUtil.simplePluralize(str);
	}
	
	
	/**
	 * foo -> Foo
	 * FOo -> FOo
	 */
	static public String beanCapitalize(String fieldname) {
		if ( fieldname == null || fieldname.length() == 0 ) {
			return fieldname;
		}

		if ( fieldname.length() > 1 && Character.isUpperCase( fieldname.charAt( 1 ) ) ) {
			return fieldname;
		}
		char chars[] = fieldname.toCharArray();
		chars[0] = Character.toUpperCase( chars[0] );
		return new String( chars );
	}


	public boolean isComponent(Property property) {
		Value value = property.getValue();
		if ( value != null && value instanceof Component ) {
			return true;
		}
		else {
			return false;
		}
	}

	public String generateHashCode(Property property, String result, String thisName, boolean useGenerics) {
		StringBuffer buf = new StringBuffer();
		if ( c2j.getMetaAsBool( property, "use-in-equals" ) ) {
			String javaTypeName = c2j.getJavaTypeName( property, useGenerics, this );
			boolean isPrimitive = c2j.isPrimitive( javaTypeName );
			if ( !isPrimitive ) {
				if(javaTypeName.endsWith("[]")) {
					buf.append( result )
					.append( " = 37 * " )
					.append( result )
					.append( " + " );
					buf.append( "( " )
					.append( getGetterSignature( property ) )
					.append( "() == null ? 0 : Arrays.hashCode(" )
					.append( thisName )
					.append( "." )
					.append( getGetterSignature( property ) )
					.append( "())" )
					.append( " )" )
					.append(";");
					importType("java.util.Arrays");
				} else {
					buf.append( result )
					.append( " = 37 * " )
					.append( result )
					.append( " + " );
					buf.append( "( " )
					.append( getGetterSignature( property ) )
					.append( "() == null ? 0 : " )
					.append( thisName )
					.append( "." )
					.append( getGetterSignature( property ) )
					.append( "()" )
					.append( ".hashCode()" )
					.append( " )" )
					.append(";");
				}
			}
			else {
				buf.append( result )
				.append( " = 37 * " )
				.append( result )
				.append( " + " );
				String thisValue = thisName + "." + getGetterSignature( property ) + "()";
				if("char".equals(javaTypeName)||"int".equals(javaTypeName)||"short".equals(javaTypeName)||"byte".equals(javaTypeName)) {
					buf.append( thisValue );
				} else if("boolean".equals(javaTypeName)) {
					buf.append("(" + thisValue + "?1:0)");
				} else {
					buf.append( "(int) ");
					buf.append( thisValue );
				}
				buf.append(";");
			}
		}
		return buf.toString();
	}

	public String getFieldModifiers(Property property) {
		return getModifiers( property, "scope-field", "private" );
	}

	public String getPropertyGetModifiers(Property property) {
		return getModifiers( property, "scope-get", "public" );
	}

	public String getPropertySetModifiers(Property property) {
		return getModifiers( property, "scope-set", "public" );
	}

	//TODO defaultModifiers
	private String getModifiers(Property property, String modifiername, String defaultModifiers) {
		MetaAttribute override = property.getMetaAttribute( modifiername );
		if ( override != null ) {
			return MetaAttributeHelper.getMetaAsString( override );
		}
		else {
			return defaultModifiers;
		}
	}

	protected boolean isRequiredInConstructor(Property field) {
		if(hasMetaAttribute(field, "default-value")) {
			return false;
		}
		if(field.getValue()!=null) {			
			if (!field.isOptional() && field.getGeneration().equals(PropertyGeneration.NEVER)) {				
				return true;
			} else if (field.getValue() instanceof Component) {
				Component c = (Component) field.getValue();
				Iterator it = c.getPropertyIterator();
				while ( it.hasNext() ) {
					Property prop = (Property) it.next();
					if(isRequiredInConstructor(prop)) {
						return true;
					}
				}
			}
		}
		
		return false;
	}

	public boolean needsMinimalConstructor() {
		List propClosure = getPropertyClosureForMinimalConstructor();
		if(propClosure.isEmpty()) return false; // minimal=default
		if(propClosure.equals(getPropertyClosureForFullConstructor())) return false; // minimal=full
		
		return true;
	}

	public String getJavaTypeName(Property p, boolean useGenerics) {
		return c2j.getJavaTypeName(p, useGenerics, this);
	}
	
	static Map defaultInitializors = new HashMap();
	static {
		defaultInitializors.put("java.util.List", "java.util.ArrayList");
		defaultInitializors.put("java.util.Map", "java.util.HashMap");
		defaultInitializors.put("java.util.Set", "java.util.HashSet");		
	}
	
	public boolean hasFieldInitializor(Property p, boolean useGenerics) {
		return getFieldInitialization(p, useGenerics)!=null;
	}
	
	public String getFieldInitialization(Property p, boolean useGenerics) {
		String javaTypeName = c2j.getJavaTypeName(p, false);
		if(hasMetaAttribute(p, "default-value")) {
			return MetaAttributeHelper.getMetaAsString( p.getMetaAttribute( "default-value" ) );
		}
		if(javaTypeName==null) {
			return null;
		} else if (p.getValue() instanceof Collection) {
			String initialization = (String) defaultInitializors.get(javaTypeName);
			
			String decl = null;
			
			if(useGenerics) {
				decl = c2j.getGenericCollectionDeclaration((Collection) p.getValue(), true, importContext);
			}
			if(initialization!=null) {			
				return "new " + importType(initialization) + (decl==null?"":decl) + "(0)";
			} else {
				return null;
			}
		} else {
			return null;
		}
	}	
	
}
