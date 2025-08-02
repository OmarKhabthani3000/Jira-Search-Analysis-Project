package com.docent.utility;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;

/** Hibernate UserType definitions like date, time and timestamp,
 * but using the UTC TimeZone (not the default TimeZone).
 *
 * @author Copyright (c) 2003 by Docent, Inc. All Rights Reserved.
 */
public abstract class HibernateUTC implements net.sf.hibernate.UserType {

    public static final int[] SQL_TYPES = {Types.DATE, Types.TIME, Types.TIMESTAMP};

    /** Get the type codes (from <code>java.sql.Types</code>) of columns mapped by this type. */
    public int[] sqlTypes()
    {
        return SQL_TYPES;
    }

    /** Compare the persistent state of two objects returned by <code>nullSafeGet</code>. */
    public boolean equals(Object x, Object y)
    {
        return (x == null) ? (y == null) : x.equals(y);
    }

    /** Are instances of this type mutable? */
    public boolean isMutable()
    {
        return true;
    }

    /** Get the class of objects returned by <code>nullSafeGet</code>. */
    public Class returnedClass()
    {
        return objectClass;
    }

    /** The class of objects returned by <code>nullSafeGet</code>.
     * Currently, returned objects are derived from this class, not exactly this class.
     */
    protected Class objectClass = Date.class;

    /** Like a Hibernate date, but using the UTC TimeZone (not the default TimeZone). */
    public static class DateType extends HibernateUTC {

        public Object deepCopy(Object value)
        {
            return (value == null) ? null : new java.sql.Date(((Date)value).getTime());
        }

        public Object nullSafeGet(ResultSet rs, String[] names, Object owner)
            throws SQLException
        {
            return rs.getDate(names[0], UTCCalendar.getCalendar());
        }

        public void nullSafeSet(PreparedStatement st, Object value, int index)
            throws SQLException
        {
            if ( ! (value instanceof java.sql.Date)) value = deepCopy(value);
            st.setDate(index, (java.sql.Date)value, UTCCalendar.getCalendar());
        }

    }

    /** Like a Hibernate time, but using the UTC TimeZone (not the default TimeZone). */
    public static class TimeType extends HibernateUTC {

        public Object deepCopy(Object value)
        {
            return (value == null) ? null : new java.sql.Time(((Date)value).getTime());
        }

        public Object nullSafeGet(ResultSet rs, String[] names, Object owner)
            throws SQLException
        {
            return rs.getTime(names[0], UTCCalendar.getCalendar());
        }

        public void nullSafeSet(PreparedStatement st, Object value, int index)
            throws SQLException
        {
            if ( ! (value instanceof java.sql.Time)) value = deepCopy(value);
            st.setTime(index, (java.sql.Time)value, UTCCalendar.getCalendar());
        }

    }

    /** Like a Hibernate timestamp, but using the UTC TimeZone (not the default TimeZone). */
    public static class TimestampType extends HibernateUTC {

        public Object deepCopy(Object value)
        {
            return (value == null) ? null : new java.sql.Timestamp(((Date)value).getTime());
        }

        public Object nullSafeGet(ResultSet rs, String[] names, Object owner)
            throws SQLException
        {
            return rs.getTimestamp(names[0], UTCCalendar.getCalendar());
        }

        public void nullSafeSet(PreparedStatement st, Object value, int index)
            throws SQLException
        {
            if ( ! (value instanceof java.sql.Timestamp)) value = deepCopy(value);
            st.setTimestamp(index, (java.sql.Timestamp)value, UTCCalendar.getCalendar());
        }

    }

}
