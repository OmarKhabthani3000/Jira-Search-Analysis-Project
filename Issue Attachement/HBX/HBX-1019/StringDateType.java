package com.mainsys.account.statement.dao.hibernate;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.usertype.ParameterizedType;
import org.hibernate.usertype.UserType;

public class StringDateType implements UserType,ParameterizedType {

   protected Log log = LogFactory.getLog(this.getClass().getName());

   public int[] sqlTypes() {
       return new int[] { Types.VARCHAR };
     }

   private String datePattern = "yyyyMMdd";

   public Class returnedClass() {
      return Date.class;
   }

   public void setParameterValues(Properties arg0) {
      
      String pattern = arg0.getProperty("datePattern");
      if (pattern != null) {
         this.datePattern = pattern;
      }
      
   }

   public Object deepCopy(Object value) throws HibernateException {
        return value;
    }

    public boolean isMutable() {
        return false;
    }

    public Serializable disassemble(Object value) throws HibernateException {
        return (Serializable) value;
    }

    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        return cached;
    }

    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return original;
    }

    public boolean equals(Object x, Object y) throws HibernateException {
       if (x == y)
          return true;
       else if (x != null && x.equals(y))
          return true;
       else
          return false;
    }

    public int hashCode(Object x) throws HibernateException {
        return x.hashCode();
    }

   public Object nullSafeGet(ResultSet arg0, String[] names, Object arg2) throws HibernateException, SQLException {

      SimpleDateFormat formatter = (SimpleDateFormat) SimpleDateFormat.getInstance();
      formatter.applyPattern(datePattern);

      Date result = null;

      String dateAsString = arg0.getString(names[0]);

      if (!arg0.wasNull()) {
          try {
            result = "".equals(dateAsString) ? null : formatter.parse(dateAsString);
         } catch (ParseException e) {
            log.error("Unable to parse date",e);
            throw new HibernateException("Unable to parse date",e);
         }
      }
      
      return result;
   }

   public void nullSafeSet(PreparedStatement arg0, Object arg1, int arg2) throws HibernateException, SQLException {
      log.fatal("This method is not implemented");      
      throw new HibernateException("This method is not implemented");
   }
   
}