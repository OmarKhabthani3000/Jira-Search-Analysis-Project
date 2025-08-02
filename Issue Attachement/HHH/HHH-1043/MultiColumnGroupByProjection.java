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
package org.wfp.rita.db;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.dom4j.Node;
import org.hibernate.Criteria;
import org.hibernate.EntityMode;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Projection;
import org.hibernate.engine.Mapping;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.loader.criteria.CriteriaQueryTranslator;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.persister.entity.PropertyMapping;
import org.hibernate.type.ComponentType;
import org.hibernate.type.ForeignKeyDirection;
import org.hibernate.type.Type;

/**
 * A property value, or grouped property value
 * @author Gavin King
 */
public class MultiColumnGroupByProjection implements Projection
{
    private String m_PropertyName;
    private Criterion m_HavingCriterion;
    private List<String> m_Aliases;

    public MultiColumnGroupByProjection(String prop,
        Criterion havingCriterion)
    {
        m_PropertyName = prop;
        m_HavingCriterion = havingCriterion;
    }
    
    public MultiColumnGroupByProjection(String prop)
    {
        this(prop, null);
    }

    public String getPropertyName()
    {
        return m_PropertyName;
    }
    
    public String toString()
    {
        return "MultiColumnGroupByProjection(" + m_PropertyName + ")";
    }

    public Type[] getTypes(Criteria criteria, CriteriaQuery criteriaQuery) 
    throws HibernateException
    {
        Type t = criteriaQuery.getType(criteria, m_PropertyName);
        
        if (t instanceof ComponentType)
        {
            final ComponentType ct = (ComponentType) t;
            t = new Type(){
                public boolean isAssociationType() { return false; }
                public boolean isCollectionType() { return false; }
                public boolean isComponentType() { return true; }
                public boolean isEntityType() { return false; }
                public boolean isAnyType() { return false; }                
                public boolean isXMLElement() { return false; }
                public int[] sqlTypes(Mapping mapping) throws MappingException
                {
                    return ct.sqlTypes(mapping);
                }
                public int getColumnSpan(Mapping mapping) throws MappingException
                {
                    return ct.getColumnSpan(mapping);
                }
                public Class getReturnedClass()
                {
                    return ct.getReturnedClass();
                }
                public boolean isSame(Object x, Object y, EntityMode entityMode)
                throws HibernateException
                {
                    return ct.isSame(x, y, entityMode);
                }
                public boolean isEqual(Object x, Object y, EntityMode entityMode)
                throws HibernateException
                {
                    return ct.isEqual(x, y, entityMode);
                }
                public boolean isEqual(Object x, Object y, EntityMode entityMode,
                    SessionFactoryImplementor factory) 
                throws HibernateException
                {
                    return ct.isEqual(x, y, entityMode, factory);
                }
                public int getHashCode(Object x, EntityMode entityMode)
                throws HibernateException
                {
                    return ct.getHashCode(x, entityMode);
                }
                public int getHashCode(Object x, EntityMode entityMode,
                    SessionFactoryImplementor factory) 
                throws HibernateException
                {
                    return ct.getHashCode(x, entityMode, factory);
                }
                public int compare(Object x, Object y, EntityMode entityMode)
                {
                    return ct.compare(x, y, entityMode);
                }
                public boolean isDirty(Object old, Object current,
                    SessionImplementor session)
                throws HibernateException
                {
                    return ct.isDirty(old, current, session);
                }
                public boolean isDirty(Object old, Object current,
                    boolean[] checkable, SessionImplementor session)
                throws HibernateException
                {
                    return ct.isDirty(old, current, checkable, session);
                }
                public boolean isModified(Object oldHydratedState,
                    Object currentState, boolean[] checkable,
                    SessionImplementor session)
                throws HibernateException
                {
                    return ct.isModified(oldHydratedState, currentState,
                        checkable, session);
                }
                public Object nullSafeGet(ResultSet rs, String[] names,
                    SessionImplementor session, Object owner)
                throws HibernateException, SQLException
                {
                    return ct.nullSafeGet(rs, names, session, owner);
                }
                /** 
                 * this one is overridden!
                 */
                public Object nullSafeGet(ResultSet rs, String name,
                    SessionImplementor session, Object owner)
                throws HibernateException, SQLException
                {
                    // ignore the single column name and use our own
                    return ct.nullSafeGet(rs, m_Aliases.toArray(new String [0]),
                        session, owner);
                }
                public void nullSafeSet(PreparedStatement st, Object value,
                    int index, boolean[] settable, SessionImplementor session)
                throws HibernateException, SQLException
                {
                    ct.nullSafeSet(st, value, index, settable, session);
                }
                public void nullSafeSet(PreparedStatement st, Object value,
                    int index, SessionImplementor session)
                throws HibernateException, SQLException
                {
                    ct.nullSafeSet(st, value, index, session);
                }
                public void setToXMLNode(Node node, Object value,
                    SessionFactoryImplementor factory)
                throws HibernateException
                {
                    ct.setToXMLNode(node, value, factory);
                }
                public String toLoggableString(Object value,
                    SessionFactoryImplementor factory)
                throws HibernateException
                {
                    return ct.toLoggableString(value, factory);
                }
                public Object fromXMLNode(Node xml, Mapping factory)
                throws HibernateException
                {
                    return ct.fromXMLNode(xml, factory);
                }
                public String getName()
                {
                    return ct.getName();
                }
                public Object deepCopy(Object value, EntityMode entityMode,
                    SessionFactoryImplementor factory) 
                throws HibernateException
                {
                    return ct.deepCopy(value, entityMode, factory);
                }
                public boolean isMutable()
                {
                    return ct.isMutable();
                }
                public Serializable disassemble(Object value,
                    SessionImplementor session, Object owner)
                throws HibernateException
                {
                    return ct.disassemble(value, session, owner);
                }
                public Object assemble(Serializable cached,
                    SessionImplementor session, Object owner)
                throws HibernateException
                {
                    return ct.assemble(cached, session, owner);
                }
                public void beforeAssemble(Serializable cached,
                    SessionImplementor session)
                {
                    ct.beforeAssemble(cached, session);
                }
                public Object hydrate(ResultSet rs, String[] names,
                    SessionImplementor session, Object owner)
                throws HibernateException, SQLException
                {
                    return ct.hydrate(rs, names, session, owner);
                }
                public Object resolve(Object value, SessionImplementor session, 
                    Object owner)
                throws HibernateException
                {
                    return ct.resolve(value, session, owner);
                }
                public Object semiResolve(Object value,
                    SessionImplementor session, Object owner)
                throws HibernateException
                {
                    return ct.semiResolve(value, session, owner);
                }
                public Type getSemiResolvedType(SessionFactoryImplementor factory)
                {
                    return ct.getSemiResolvedType(factory);
                }
                public Object replace(Object original, Object target, 
                        SessionImplementor session, Object owner, Map copyCache)
                throws HibernateException
                {
                    return ct.replace(original, target, session, owner,
                        copyCache);
                }
                public Object replace(Object original, Object target, 
                        SessionImplementor session, Object owner, 
                        Map copyCache, ForeignKeyDirection foreignKeyDirection)
                throws HibernateException
                {
                    return ct.replace(original, target, session, owner,
                        copyCache, foreignKeyDirection);
                }
                public boolean[] toColumnNullness(Object value, Mapping mapping)
                {
                    return ct.toColumnNullness(value, mapping);
                }
            };
        }
        
        return new Type [] {t};
    }

    private List<String> getColumns(Criteria criteria,
        CriteriaQuery criteriaQuery)
    {
        String entityName = criteriaQuery.getEntityName(criteria);
        SessionFactoryImplementor sfi = criteriaQuery.getFactory();
        EntityPersister ep = sfi.getEntityPersister(entityName);
        PropertyMapping pm = (PropertyMapping) ep;
        String alias = CriteriaQueryTranslator.ROOT_SQL_ALIAS;
        String [] colArray = pm.toColumns(alias, m_PropertyName);
        return Arrays.asList(colArray);        
    }

    private StringBuffer getColumnListSql(Criteria criteria,
        CriteriaQuery criteriaQuery, boolean withAliases, int position) 
    throws HibernateException
    {
        List<String> columns = getColumns(criteria, criteriaQuery);
        
        if (withAliases)
        {
            m_Aliases = new ArrayList<String>(columns.size());
        }
        
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < columns.size(); i++)
        {
            sb.append(columns.get(i));
            
            if (withAliases)
            {
                String alias = "column_" + i;
                sb.append(" as ").append(alias);
                m_Aliases.add(alias);
            }
            
            if (i < columns.size() - 1)
            {
                sb.append(", ");
            }
        }

        return sb;
    }

    /**
     * This has the pretty nasty side-effect of initializing
     * m_Aliases, and must therefore be called before 
     * @link MultiColumnGroupByProjection#getColumnAliases(int)
     */
    public String toSqlString(Criteria criteria, int position,
        CriteriaQuery criteriaQuery) 
    throws HibernateException
    {
        return getColumnListSql(criteria, criteriaQuery, true,
            position).toString();
    }
    
    /**
     * m_Aliases must be initialized first by calling
     * @link MultiColumnGroupByProjection#toSqlString(Criteria, int, CriteriaQuery)
     */
    public String[] getColumnAliases(int loc)
    {
        return m_Aliases.toArray(new String [0]);
    }

    public boolean isGrouped()
    {
        return m_HavingCriterion != null;
    }
    
	public String toGroupSqlString(Criteria criteria,
        CriteriaQuery criteriaQuery) 
	throws HibernateException
    {
        if (m_HavingCriterion == null)
        {
            throw new UnsupportedOperationException("This is not a " +
                    "grouping projection, why did you call toGroupSqlString()?");
        }
        
        return getColumnListSql(criteria, criteriaQuery, false, 0).toString() +
            " HAVING " + m_HavingCriterion.toSqlString(criteria, criteriaQuery);
	}
    
    /**
     * @todo this is only a stub, but @link SimpleProjection does this too
     */
    public Type[] getTypes(String alias, Criteria criteria,
        CriteriaQuery criteriaQuery) 
    throws HibernateException
    {
        return null;
    }
    
    /**
     * @todo this is only a stub, but @link SimpleProjection does this too
     */
    public String[] getColumnAliases(String alias, int loc)
    {
        return null;
    }
    
    /**
     * @todo this is only a stub, but @link SimpleProjection does this too
     */
    public String[] getAliases()
    {
        return new String[1];
    }
}
