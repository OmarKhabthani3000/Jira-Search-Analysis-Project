//$Id: Order.java,v 1.2.2.2 2003/08/12 12:51:00 oneovthafew Exp $
package net.sf.hibernate.expression;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.engine.SessionFactoryImplementor;

/**
 * Represents an order imposed upon a <tt>Criteria</tt> result set
 * @author Gavin King
 */
public class Order implements Serializable {

  /**
   * List of the orders, in order. Entries in pairs, first property name
   * then direction, boolean for ascending.
   */
  private List orderList = new ArrayList();

	/**
	 * Constructor for Order.
	 */
	protected Order(String propertyName, boolean ascending) {
    orderList.add(propertyName);
    orderList.add(Boolean.valueOf(ascending));
	}
	
	/**
	 * Render the SQL fragment
	 * 
	 * @param sessionFactory
	 * @param persistentClass
	 * @param alias
	 * @return String
	 * @throws HibernateException
	 */
	public String toSqlString(SessionFactoryImplementor sessionFactory, Class persistentClass, String alias) throws HibernateException {
    StringBuffer ret = new StringBuffer();
    for (Iterator i = orderList.iterator(); i.hasNext();) {
      String propertyName = (String) i.next();
      boolean ascending = ((Boolean)i.next()).booleanValue();
      
      String[] columns = AbstractCriterion.getColumns(sessionFactory, persistentClass, propertyName, alias, EMPTY_MAP);
      if (columns.length!=1) throw new HibernateException("Cannot order by multi-column property: " + propertyName);
      ret.append(", " + columns[0] + ( ascending ? " asc" : " desc" ));
    }
    return ret.substring(2);
	}
	
	/**
	 * Ascending order
	 * 
	 * @param propertyName
	 * @return Order
	 */
	public static Order asc(String propertyName) {
		return new Order(propertyName, true);
	}
	
	/**
	 * Descending order
	 * 
	 * @param propertyName
	 * @return Order
	 */
	public static Order desc(String propertyName) {
		return new Order(propertyName, false);
	}

  /**
   * Append an ordering onto this order.
   * 
   * @param Order to append.
   * @return this order, returned for convenience.
   */
  public Order addOrder(Order add) {
    orderList.addAll(add.orderList);
    return this;
  }
  
  /**
   * Add an asc order to this order.
   * 
   * @param propertyName
   * @return this order, returned for convenience.
   */
  public Order addAsc(String propertyName) {
    orderList.add(propertyName);
    orderList.add(Boolean.TRUE);
    return this;
  }
  
  /**
   * Add an desc order to this order.
   * 
   * @param propertyName
   * @return this order, returned for convenience.
   */
  public Order addDesc(String propertyName) {
    orderList.add(propertyName);
    orderList.add(Boolean.FALSE);
    return this;
  }
  
  /**
   * Stringify this order
   */
  public String toString() {
    StringBuffer ret = new StringBuffer();
    for (Iterator i = orderList.iterator(); i.hasNext();) {
      String propertyName = (String) i.next();
      boolean ascending = ((Boolean)i.next()).booleanValue();
      
      ret.append(", " + propertyName + ( ascending ? " asc" : " desc" ));
    }
    return ret.substring(2);
  }
  
	private static final Map EMPTY_MAP = new HashMap();

}
