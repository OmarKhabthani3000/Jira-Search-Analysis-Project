/**
 * 
 */
package org.wfp.rita.db;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.Criterion;
import org.hibernate.engine.TypedValue;

public class FunctionExpression implements Criterion
{
    private final String m_PropertyName;
    private final String m_Function;

    public FunctionExpression(String function, String propertyName)
    {
        this.m_PropertyName = propertyName;
        this.m_Function = function;
    }

    public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery)
    throws HibernateException
    {
        String[] columns = criteriaQuery.getColumnsUsingProjection(criteria,
            m_PropertyName);
        
        StringBuffer fragment = new StringBuffer();
        fragment.append(m_Function).append('(');
        
        for (int i = 0; i < columns.length; i++)
        {
            fragment.append(columns[i]);
            if (i < columns.length - 1)
            {
                fragment.append(", ");
            }
        }
        
        fragment.append(')');
        return fragment.toString();
    }

    public static final TypedValue [] NO_TYPED_VALUES = new TypedValue [0];
    
    public TypedValue[] getTypedValues(Criteria criteria,
        CriteriaQuery criteriaQuery)
    throws HibernateException
    {
        return NO_TYPED_VALUES;
    }
}