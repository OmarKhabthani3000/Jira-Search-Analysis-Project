//$Id: Table.java,v 1.8 2003/05/14 12:20:23 oneovthafew Exp $
package net.sf.hibernate.mapping;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import net.sf.hibernate.engine.Mapping;
import net.sf.hibernate.id.IdentityGenerator;
import net.sf.hibernate.dialect.Dialect;
import net.sf.hibernate.dialect.HSQLDialect;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.tool.hbm2ddl.JdbcColumnInfo;
import net.sf.hibernate.tool.hbm2ddl.JdbcTableInfo;
import net.sf.hibernate.util.StringHelper;
import net.sf.hibernate.util.Stringable;

import org.apache.commons.collections.SequencedHashMap;

public class Table extends Stringable implements RelationalModel {
	
	private String name;
	private String schema;
	private Map columns = new SequencedHashMap();
	private Value idValue;
	private PrimaryKey primaryKey;
	private Map indexes = new HashMap();
	private Map foreignKeys = new HashMap();
	private Map uniqueKeys = new HashMap();
	private final int uniqueInteger;
	private static int tableCounter=0;
	
	public Table() {
		uniqueInteger = tableCounter++;
	}
	
	public String getQualifiedName() {
		return (schema==null) ? name : schema + StringHelper.DOT + name;
	}
	
	public String getQualifiedName(String defaultQualifier) {
		return ( schema==null ) ? ( (defaultQualifier==null) ? name : defaultQualifier + StringHelper.DOT + name ) : getQualifiedName();
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public Column getColumn(int n) {
		Iterator iter = columns.values().iterator();
		for (int i=0; i<n-1; i++) iter.next();
		return (Column) iter.next();
	}
	public void addColumn(Column column) {
		Column old = (Column) columns.get( column.getName() );
		if ( old==null ) {
			columns.put( column.getName(), column );
			column.uniqueInteger = columns.size();
		}
		else {
			column.uniqueInteger = old.uniqueInteger;
		}
	}
	public int getColumnSpan() {
		return columns.size();
	}
	public Iterator getColumnIterator() {
		return columns.values().iterator();
	}
	public Iterator getIndexIterator() {
		return indexes.values().iterator();
	}
	public Iterator getForeignKeyIterator() {
		return foreignKeys.values().iterator();
	}
	public Iterator getUniqueKeyIterator() {
		return uniqueKeys.values().iterator();
	}
	
	public String sqlAlterString(Dialect dialect,Mapping p,JdbcTableInfo tableInfo) throws HibernateException {
		
		Iterator iter=getColumnIterator();
		StringBuffer buf=new StringBuffer(50);
		while ( iter.hasNext() ) {
			Column col = (Column) iter.next();
			
			JdbcColumnInfo columnInfo=tableInfo.getColumnInfo(col.getName());
			
			if (columnInfo==null) {
				// the column doesnt exist at all.
				if (buf.length()!=0)
				buf.append(StringHelper.COMMA_SPACE);
				buf.append( col.getName() ).append(' ').append( col.getSqlType(dialect,p) );
				if ( col.isUnique() && dialect.supportsUnique() ) {
					buf.append(" unique");
				}
			}
			
		}
		
		if ( buf.length()==0 ) {
			return null;
		}
		else {
			return new StringBuffer("alter table ").append(getQualifiedName()).append(" add ").append(buf).toString();
		}
		
	}
	
	public String sqlCreateString(Dialect dialect, Mapping p) throws HibernateException {
		StringBuffer buf = new StringBuffer("create table ")
			.append( getQualifiedName() )
			.append(" (");
		
		boolean identityColumn = idValue!=null && idValue.createIdentifierGenerator(dialect) instanceof IdentityGenerator;
		
		// Try to find out the name of the primary key to create it as identity if the IdentityGenerator is used
		String pkname = null;
		if (primaryKey != null && identityColumn ) {
			pkname = ( (Column) primaryKey.getColumnIterator().next() ).getName();
		}
		
		Iterator iter = getColumnIterator();
		while ( iter.hasNext() ) {
			Column col = (Column) iter.next();
			
			buf.append( col.getName() );
			
			boolean appendSqlType = true;
			if (identityColumn && col.getName().equals(pkname) && dialect instanceof HSQLDialect) {
				appendSqlType = false;
			}
			
			if (appendSqlType) {
			    buf.append(' ')
			    .append( col.getSqlType(dialect, p) );
			}
			
			if ( identityColumn && col.getName().equals(pkname) ) {
				buf.append(' ')
				.append( dialect.getIdentityColumnString() );
			}
			else {
				if ( col.isNullable() ) {
					buf.append( dialect.getNullColumnString() );
				}
				else {
					buf.append(" not null" );
				}
			}
			
			if ( col.isUnique() ) {
				if ( dialect.supportsUnique() ) {
					buf.append(" unique");
				}
				else {
					UniqueKey uk = getUniqueKey( col.getName() + '_' );
					uk.addColumn(col);
				}
			}
			if ( iter.hasNext() ) buf.append(StringHelper.COMMA_SPACE);
			
		}
		if (primaryKey!=null) {
			if ( dialect instanceof HSQLDialect && identityColumn ) {
				// skip the primary key definition
				// ugly, ugly hack!
			}
			else {
				buf.append(',').append( primaryKey.sqlConstraintString(dialect) );
			}
		}
		
		Iterator ukiter = getUniqueKeyIterator();
		while ( ukiter.hasNext() ) {
			UniqueKey uk = (UniqueKey) ukiter.next();
			buf.append(',').append( uk.sqlConstraintString(dialect) );
		}
		
		buf.append(StringHelper.CLOSE_PAREN);
		
		return buf.toString();
	}
	public String sqlDropString(Dialect dialect) {
		return "drop table " + getQualifiedName() + dialect.getCascadeConstraintsString();
	}
	
	public PrimaryKey getPrimaryKey() {
		return primaryKey;
	}
	
	public void setPrimaryKey(PrimaryKey primaryKey) {
		this.primaryKey = primaryKey;
	}
	
	public Index getIndex(String name) {
		Index index = (Index)indexes.get(name);
		
		if (index == null) {
			index = new Index();
			index.setName(name);
			index.setTable(this);
			indexes.put(name, index);
		}
		
		return index;
	}
	
	public UniqueKey getUniqueKey(String name) {
		
		UniqueKey uk = (UniqueKey) uniqueKeys.get(name);
		
		if (uk == null) {
			uk = new UniqueKey();
			uk.setName(name);
			uk.setTable(this);
			uniqueKeys.put(name, uk);
		}
		return uk;
	}
	
	public ForeignKey createForeignKey(List columns) {
		
		String name = "FK" + uniqueColumnString( columns.iterator() );
		ForeignKey fk = (ForeignKey) foreignKeys.get(name);
		
		if (fk == null) {
			fk = new ForeignKey();
			fk.setName(name);
			fk.setTable(this);
			foreignKeys.put(name, fk);
		}
		Iterator iter = columns.iterator();
		while ( iter.hasNext() ) fk.addColumn( (Column) iter.next() );
		return fk;
	}
	
	public String uniqueColumnString(Iterator iterator) {
		int result = 0;
		while ( iterator.hasNext() ) result += iterator.next().hashCode();
		return ( Integer.toHexString( name.hashCode() ) + Integer.toHexString(result) ).toUpperCase();
	}
	
	
	public String getSchema() {
		return schema;
	}
	
	public void setSchema(String schema) {
		this.schema = schema;
	}
	
	public int getUniqueInteger() {
		return uniqueInteger;
	}
	
	public void setIdentifierValue(Value idValue) {
		this.idValue = idValue;
	}
	
}








