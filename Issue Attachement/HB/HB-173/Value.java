//$Id: Value.java,v 1.13.2.3 2003/09/27 10:38:16 oneovthafew Exp $
package net.sf.hibernate.mapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.MappingException;
import net.sf.hibernate.dialect.Dialect;
import net.sf.hibernate.id.IdentifierGenerator;
import net.sf.hibernate.id.IdentifierGeneratorFactory;
import net.sf.hibernate.loader.OuterJoinLoader;
import net.sf.hibernate.type.Type;
import net.sf.hibernate.util.ReflectHelper;
import net.sf.hibernate.util.Stringable;

/**
 * A value represents a simple thing that maps down to
 * a table column or columns. Higher level things like
 * classes, properties and collections add semantics
 * to instances of this class.
 */
public class Value extends Stringable {
	//TODO: Value should be an interface
	
	private final ArrayList columns = new ArrayList();
	private Type type;
	private Properties identifierGeneratorProperties;
	private String identifierGeneratorStrategy = "assigned";
	private String nullValue;
	private Table table;
	private Formula formula;
	private String foreignKeyName;
	private boolean unique;
	private Map metaValues = new HashMap();
	
	public void addColumn(Column column) {
		if ( !columns.contains(column) ) columns.add(column);
	}
	public int getColumnSpan() {
		return columns.size();
	}
	public Iterator getColumnIterator() {
		return columns.iterator();
	}
	public java.util.List getConstraintColumns() {
		return columns;
	}
	
	public Type getType() {
		return type;
	}
	public void setType(Type type) {
		this.type = type;
		Iterator iter = getColumnIterator();
		int count=0;
		while ( iter.hasNext() ) {
			Column col = (Column) iter.next();
			col.setType(type);
			col.setTypeIndex(count++);
		}
	}
	public void setTable(Table table) {
		this.table = table;
	}
	
	public Value(Table table) {
		this.table = table;
	}
	
	public void createForeignKey() {
	}
	
	
	public void createForeignKeyOfClass(Class persistentClass) {
		table.createForeignKey( getForeignKeyName(), getConstraintColumns(), persistentClass );
	}
	
	private IdentifierGenerator uniqueIdentifierGenerator;
	
	public IdentifierGenerator createIdentifierGenerator(Dialect dialect) throws MappingException {
		if (uniqueIdentifierGenerator==null) {
			uniqueIdentifierGenerator = IdentifierGeneratorFactory.create(
				identifierGeneratorStrategy, type, identifierGeneratorProperties, dialect
			);
		}
		return uniqueIdentifierGenerator;
	}
	
	public void setTypeByReflection(Class propertyClass, String propertyName) throws MappingException {
		try {
			if (type==null) {
				type = ReflectHelper.reflectedPropertyType(propertyClass, propertyName);
				Iterator iter = getColumnIterator();
				int count=0;
				while ( iter.hasNext() ) {
					Column col = (Column) iter.next();
					col.setType(type);
					col.setTypeIndex(count++);
				}
			}
		}
		catch (HibernateException he) {
			throw new MappingException( "Problem trying to set property type by reflection", he );
		}
	}
	
	public int getOuterJoinFetchSetting() { 
		return OuterJoinLoader.LAZY; 
	}
	
	public Properties getIdentifierGeneratorProperties() {
		return identifierGeneratorProperties;
	}
	
	public boolean isComposite() {
		return false;
	}
	
	public String getNullValue() {
		return nullValue;
	}
	
	public Table getTable() {
		return table;
	}

	/**
	 * Returns the identifierGeneratorStrategy.
	 * @return String
	 */
	public String getIdentifierGeneratorStrategy() {
		return identifierGeneratorStrategy;
	}

	/**
	 * Sets the identifierGeneratorProperties.
	 * @param identifierGeneratorProperties The identifierGeneratorProperties to set
	 */
	public void setIdentifierGeneratorProperties(Properties identifierGeneratorProperties) {
		this.identifierGeneratorProperties = identifierGeneratorProperties;
	}

	/**
	 * Sets the identifierGeneratorStrategy.
	 * @param identifierGeneratorStrategy The identifierGeneratorStrategy to set
	 */
	public void setIdentifierGeneratorStrategy(String identifierGeneratorStrategy) {
		this.identifierGeneratorStrategy = identifierGeneratorStrategy;
	}

	/**
	 * Sets the nullValue.
	 * @param nullValue The nullValue to set
	 */
	public void setNullValue(String nullValue) {
		this.nullValue = nullValue;
	}
	
	public boolean isAny() {
		return false;
	}
	
	public void setFormula(Formula formula) {
		this.formula = formula;
	}
	public Formula getFormula() {
		return formula;
	}

	public String getForeignKeyName() {
		return foreignKeyName;
	}

	public void setForeignKeyName(String string) {
		foreignKeyName = string;
	}
	
	public boolean isUnique() {
		return unique;
	}
	
	public void setUnique(boolean unique) {
		this.unique = unique;
	}

	public Map getMetaValues() {
		return metaValues;
	}

}






