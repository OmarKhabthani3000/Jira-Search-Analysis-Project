/*
 * Copyright 2005 Texas Windstorm Insurance Association All rights reserved.
 *
 * change history:
 *   chris.bono	Jan 4, 2007	- initial creation
 */
package org.twia.persistence.hibernate;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.SimpleProjection;
import org.hibernate.type.Type;
import org.hibernate.util.StringHelper;

/**
 * Projection used for composite identifiers. This is used to
 * get around a bug in the hibernate core IdentifierProjection
 * that did not properly handle composite identifiers. 
 *  
 * @author chris.bono@gmail.com
 * @since  (Jan 4, 2007)
 */
public class CompositeIdProjection extends SimpleProjection {

   private boolean grouped;
   private int numColumns;
   
   protected CompositeIdProjection(boolean grouped) {
      this.grouped = grouped;
   }
   
   protected CompositeIdProjection() {
      this(false);
   }
   
   public String toString() {
      return "id";
   }

   public Type[] getTypes(Criteria criteria, CriteriaQuery criteriaQuery) 
   throws HibernateException {
      return new Type[] { criteriaQuery.getIdentifierType(criteria) };
   }
  
   public String[] getColumnAliases(int arg0)
   {
      /*
       * arg0 is the starting position in the alias list. In our
       * case we need to start the alias at that index and grab
       * up a sql alias for each of our identifier properties.
       */
      String[] aliases = new String[numColumns];
      for (int i = 0; i < numColumns; i++)
      {
         aliases[i] = "y" + arg0 + "_";
         arg0++;
      }
      return aliases;
   }

   public String toSqlString(Criteria criteria, int position, CriteriaQuery criteriaQuery) 
   throws HibernateException {
      StringBuffer buf = new StringBuffer();
      String[] cols = criteriaQuery.getIdentifierColumns(criteria);
      for ( int i=0; i<cols.length; i++ ) {
         buf.append( cols[i] )
            .append(" as y")
            .append(position + i)
            .append('_');
         
         // cbono: added the following to properly handle commas in list
         if (i < cols.length -1)
            buf.append(", ");
         
         numColumns++;
      }
      return buf.toString();
   }

   public boolean isGrouped() {
      return grouped;
   }
   
   public String toGroupSqlString(Criteria criteria, CriteriaQuery criteriaQuery) 
   throws HibernateException {
      if (!grouped) {
         return super.toGroupSqlString(criteria, criteriaQuery);
      }
      else {
         return StringHelper.join( ", ", criteriaQuery.getIdentifierColumns(criteria) );
      }
   }

}
