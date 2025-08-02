//$Id: Property.java,v 1.14.2.1 2003/09/13 14:25:44 oneovthafew Exp $
package net.sf.hibernate.mapping;

import java.util.Iterator;

import net.sf.hibernate.MappingException;
import net.sf.hibernate.engine.Cascades;
import net.sf.hibernate.type.AbstractComponentType;
import net.sf.hibernate.type.Type;
import net.sf.hibernate.util.Stringable;

public class Property extends Stringable {
	
	private String name;
	private Value value;
	private String cascade;
	private boolean updateable;
	private boolean insertable;
	private String propertyAccessorName;
	private int length;
	
	public Property(Value value) {
		this.value=value;
	}
	
	public Type getType() {
		return value.getType();
	}
	public int getColumnSpan() {
		return value.getColumnSpan();
	}
	public Iterator getColumnIterator() {
		return value.getColumnIterator();
	}
	public String getName() {
		return name;
	}
	public boolean isUpdateable() {
		return updateable && !isFormula();
	}
	
	public boolean isComposite() {
		return value instanceof Component;
	}
	
	public Value getValue() {
		return value;
	}
	
	public Cascades.CascadeStyle getCascadeStyle() throws MappingException {
		Type type = value.getType();
		if ( type.isComponentType() && !type.isObjectType() ) {
			AbstractComponentType actype = (AbstractComponentType) type;
			int length = actype.getSubtypes().length;
			for ( int i=0; i<length; i++ ) {
				if ( actype.cascade(i)!=Cascades.STYLE_NONE ) return Cascades.STYLE_ALL;
			}
			return Cascades.STYLE_NONE;
		}
		else {
			if ( cascade.equals("all") ) {
				return Cascades.STYLE_ALL;
			}
			else if ( cascade.equals("all-delete-orphan") ) {
				return Cascades.STYLE_ALL_GC;
			}
			else if ( cascade.equals("none") ) {
				return Cascades.STYLE_NONE;
			}
			else if ( cascade.equals("save-update") ) {
				return Cascades.STYLE_SAVE_UPDATE;
			}
			else if ( cascade.equals("delete") ) {
				return Cascades.STYLE_ONLY_DELETE;
			}
			else {
				throw new MappingException("Unsupported cascade style: " + cascade);
			}
		}
	}
	
	
	/**
	 * Returns the cascade.
	 * @return String
	 */
	public String getCascade() {
		return cascade;
	}

	/**
	 * Sets the cascade.
	 * @param cascade The cascade to set
	 */
	public void setCascade(String cascade) {
		this.cascade = cascade;
	}

	/**
	 * Sets the mutable.
	 * @param mutable The mutable to set
	 */
	public void setUpdateable(boolean mutable) {
		this.updateable = mutable;
	}

	/**
	 * Sets the name.
	 * @param name The name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Sets the value.
	 * @param value The value to set
	 */
	public void setValue(Value value) {
		this.value = value;
	}

	/**
	 * Returns the insertable.
	 * @return boolean
	 */
	public boolean isInsertable() {
		return insertable && !isFormula();
	}

	/**
	 * Sets the insertable.
	 * @param insertable The insertable to set
	 */
	public void setInsertable(boolean insertable) {
		this.insertable = insertable;
	}
	
	public Formula getFormula() {
		return value.getFormula();
	}
	
	public boolean isFormula() {
		return getFormula()!=null;
	}

	public String getPropertyAccessorName() {
		return propertyAccessorName;
	}

	public void setPropertyAccessorName(String string) {
		propertyAccessorName = string;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int i) {
		length = i;
	}

}







