package org.hibernate.property;

import java.lang.reflect.Method;

import org.hibernate.HibernateException;
import org.hibernate.PropertyNotFoundException;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.property.Getter;
import org.hibernate.property.PropertyAccessor;
import org.hibernate.property.Setter;

/**
 * Used to declare properties not represented at the pojo level
 * 
 * @author Michael Bartmann
 */
public class NoopAccessor implements PropertyAccessor
{

    public Getter getGetter(Class arg0, String arg1) throws PropertyNotFoundException
    {
        return new NoopGetter();
    }

    public Setter getSetter(Class arg0, String arg1) throws PropertyNotFoundException
    {
        return new NoopSetter();
    }

    /**
     * A Getter which will always return null.
     * It should not be called anyway
     */
    private static class NoopGetter implements Getter
    {

        /**
         * @return always null
         */
        public Object get(Object arg0) throws HibernateException
        {
            return null;
        }

        public Object getForInsert(Object arg0, SessionImplementor arg1) throws HibernateException
        {
            return null;
        }

        /**
         * FIXME: Do we need this? It Could be Void.TYPE
         */
        public Class getReturnType()
        {
            return Integer.TYPE;
        }

        public String getMethodName()
        {
            return null;
        }

        public Method getMethod()
        {
            return null;
        }
        
    }
    
    /**
     * A Setter whichwill just do nothing.
     */
    private static class NoopSetter implements Setter
    {

        public void set(Object arg0, Object arg1, SessionFactoryImplementor arg2) throws HibernateException
        {
            // do not do anything
        }

        public String getMethodName()
        {
            return null;
        }

        public Method getMethod()
        {
            return null;
        }
        
    }
}
