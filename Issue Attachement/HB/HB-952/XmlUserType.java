package net.sf.hibernate;

import net.sf.hibernate.engine.SessionFactoryImplementor;

/**
 * Interface to be implemented by a UserType that manages XML
 * marshalling and unmarshalling
 * 
 * @author Stefano Travelli (stefano.travelli@formula.it)
 * @see UserType
 */
public interface XmlUserType {
	/**
	 * Return the XML representation of the value
	 */
	public String toString(Object value, SessionFactoryImplementor factory) throws HibernateException;

	/**
	 * Instantiate a value given the XML string representation
	 */
	public Object fromString(String xml) throws HibernateException;

	/**
	 * Return true if XML representation of the value must be
	 * embedded in a CDATA block
	 */
	public boolean isCDATA();
}
