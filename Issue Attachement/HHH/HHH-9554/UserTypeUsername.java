package com.yoochoose.services.internal.persister;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;
import org.hibernate.usertype.CompositeUserType;

import com.yoochoose.domain.login.AuthProviderId;
import com.yoochoose.domain.login.Username;
import com.yoochoose.utils.NullUtils;
import com.yoochoose.utils.StringUtils;




public class UserTypeUsername implements CompositeUserType {

	
	@Override
	public Class returnedClass() {
		return Username.class;
	}

	
	@Override
	public boolean equals(Object x, Object y) throws HibernateException {
		return Objects.equals(x, y);
	}

	
	@Override
	public int hashCode(Object x) throws HibernateException {
		return Objects.hashCode(x);
	}
	
	
	@Override
	public boolean isMutable() {
		return false;
	}
	
	
	@Override
	public Object deepCopy(Object value) throws HibernateException {
		return value; // username is immutable
	}


	@Override
	public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner) throws HibernateException, SQLException {
		String provider = StringUtils.tremptyToNull(rs.getString(names[0]));
		String name     = rs.getString(names[1]);
		
		if (StringUtils.isTrempty(name)) {
			return null;
		}
		
		AuthProviderId pid = StringUtils.isTrempty(provider) ? null : new AuthProviderId(provider);
		
		return new Username(pid, name);
	}


	@Override
	public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session) throws HibernateException, SQLException {
		if (value == null) {
			st.setNull(index, StandardBasicTypes.STRING.sqlType());
			st.setNull(index + 1, StandardBasicTypes.STRING.sqlType());
		} else {
			Username oo = (Username)value;
			st.setString(index, StringUtils.nullToEmpty(oo.getProviderId()));
			st.setString(index + 1, oo.getName());
		}
	}


	@Override
	public String[] getPropertyNames() {
		return new String[] {"provider", "name"};
	}


	@Override
	public Type[] getPropertyTypes() {
		return new Type[] {StringType.INSTANCE, StringType.INSTANCE};
	}


	@Override
	public Object getPropertyValue(Object component, int property) throws HibernateException {
		Username oo = (Username)component;
		if (oo == null) {
			return null;
		}
		if (property == 0) {
			return NullUtils.ifnull(oo.getProviderId(), "");
		} else {
			return oo.getName();
		}
	}


	@Override
	public void setPropertyValue(Object component, int property, Object value) throws HibernateException {
		throw new HibernateException("Username ist immutable");
	}


	@Override
	public Serializable disassemble(Object value, SessionImplementor session) throws HibernateException {
		if (value == null) {
			return "";
		} else {
			Username oo = (Username)value;
			return oo.toBindingUsername();	
		}
	}


	@Override
	public Object assemble(Serializable cached, SessionImplementor session, Object owner) throws HibernateException {
		if (cached == null) {
			return "";
		} else {
			return Username.parseBindingUsername(NullUtils.toString(cached));	
		}
	}


	@Override
	public Object replace(Object original, Object target, SessionImplementor session, Object owner) throws HibernateException {
		return original;
	}
	
}