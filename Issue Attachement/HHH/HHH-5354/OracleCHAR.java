package com.st.wma.datalayer.hibernate.util;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import oracle.jdbc.driver.OracleTypes;

import org.apache.commons.lang.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;

/**
 * This class trim value of Oracle CHAR columns that causes problem of padding
 * with spaces.
 */
public class OracleCHAR implements UserType {
    public OracleCHAR() {
        super();
    }

    public int[] sqlTypes() {
        return new int[]{OracleTypes.FIXED_CHAR};
    }

    public Class<String> returnedClass() {
        return String.class;
    }

    public boolean equals(Object x, Object y) throws HibernateException {
        return (x == y) || (x != null && y != null && (x.equals(y)));
    }

    public Object nullSafeGet(ResultSet rs, String[] names, Object owner)
            throws HibernateException, SQLException {
        String val = rs.getString(names[0]);
        if (null == val) {
            return null;
        }
        else {
            //String trimmed = StringUtils.stripEnd(val, " ");
            String trimmed = StringUtils.trim(val);
            if (trimmed.equals("")) {
                return null;
            }
            else {
                return trimmed;
            }
        }
    }

    public void nullSafeSet(PreparedStatement st, Object value, int index)
            throws HibernateException, SQLException {
        if (value == null) {
            st.setNull(index, OracleTypes.FIXED_CHAR);
        }
        else {
            st.setObject(index, value, OracleTypes.FIXED_CHAR);
        }
    }

    public Object deepCopy(Object value) throws HibernateException {
        if (value == null)
            return null;
        return new String((String) value);
    }

    public boolean isMutable() {
        return false;
    }

    public Object assemble(Serializable arg0, Object arg1)
            throws HibernateException {
        return null;
    }

    public Serializable disassemble(Object arg0) throws HibernateException {
        return null;
    }

    public int hashCode(Object arg0) throws HibernateException {
        return 0;
    }

    public Object replace(Object arg0, Object arg1, Object arg2)
            throws HibernateException {
        return null;
    }
}
