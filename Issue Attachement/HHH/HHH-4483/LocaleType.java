/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Middleware LLC.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 *
 */
package org.hibernate.type;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.hibernate.EntityMode;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.dialect.Dialect;

/**
 * <tt>locale</tt>: A type that maps an SQL VARCHAR to a Java Locale.
 * @author Gavin King
 * @author schoenborn
 */
public class LocaleType extends ImmutableType implements LiteralType {

    private static final long serialVersionUID = 504220156193967947L;
    
    private static final Pattern LOCALE_PATTERN = Pattern.compile("^([a-z]{2})?(_([A-Z]{2})(_(.+))?)?$");

    @Override
    public Object get(ResultSet rs, String name) throws HibernateException, SQLException {
        return fromStringValue( (String) Hibernate.STRING.get(rs, name) );
    }

    @Override
    public void set(PreparedStatement st, Object value, int index) throws HibernateException, SQLException {
        Hibernate.STRING.set(st, value.toString(), index);
    }

    @Override
    public Object fromStringValue(String value) {
        if (value == null) return null;
        
        final Matcher matcher = LOCALE_PATTERN.matcher(value);
        if (matcher.matches()) {
            final String language = StringUtils.defaultString(matcher.group(1));
            final String country = StringUtils.defaultString(matcher.group(3));
            final String variant = StringUtils.defaultString(matcher.group(5));
            
            return new Locale(language, country, variant);
        } else {
            throw new IllegalStateException(value + " does not match " + LOCALE_PATTERN.pattern());
        }
    }
    
    @Override
    public int compare(Object x, Object y, EntityMode entityMode) {
        return x.toString().compareTo( y.toString() );
    }

    @Override
    public int sqlType() {
        return Hibernate.STRING.sqlType();
    }

    @Override
    public String toString(Object value) throws HibernateException {
        return value.toString();
    }

    @Override
    public Class<?> getReturnedClass() {
        return Locale.class;
    }

    @Override
    public String getName() {
        return "locale";
    }

    @Override
    public String objectToSQLString(Object value, Dialect dialect) throws Exception {
        return ( (LiteralType) Hibernate.STRING ).objectToSQLString( value.toString(), dialect );
    }
    
}