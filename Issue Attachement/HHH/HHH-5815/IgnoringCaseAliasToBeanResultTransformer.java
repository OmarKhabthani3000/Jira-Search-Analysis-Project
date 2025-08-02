package org.hibernate.transform;

import java.lang.reflect.Field;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.PropertyNotFoundException;
import org.hibernate.property.ChainedPropertyAccessor;
import org.hibernate.property.PropertyAccessor;
import org.hibernate.property.PropertyAccessorFactory;
import org.hibernate.property.Setter;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.hibernate.transform.ResultTransformer;


public class IgnoringCaseAliasToBeanResultTransformer implements ResultTransformer {

    private static final long serialVersionUID = -3779317531110592988L;

    // IMPL NOTE : due to the delayed population of setters (setters cached
    // for performance), we really cannot properly define equality for
    // this transformer

    @SuppressWarnings("rawtypes")
    private final Class resultClass;
    private final PropertyAccessor propertyAccessor;
    private Setter[] setters;
    private Field[] fields;

    @SuppressWarnings("rawtypes")
    public IgnoringCaseAliasToBeanResultTransformer(final Class resultClass) {
        if (resultClass == null) {
            throw new IllegalArgumentException("resultClass cannot be null");
        }
        this.resultClass = resultClass;
        this.propertyAccessor = new ChainedPropertyAccessor(new PropertyAccessor[] {
                PropertyAccessorFactory.getPropertyAccessor(resultClass, null),
                PropertyAccessorFactory.getPropertyAccessor("field") });
        this.fields = this.resultClass.getDeclaredFields();
    }

    public Object transformTuple(final Object[] tuple, final String[] aliases) {
        Object result;

        try {
            if (this.setters == null) {
                this.setters = new Setter[aliases.length];
                for (int i = 0; i < aliases.length; i++) {
                    String alias = aliases[i];
                    if (alias != null) {
                        Setter setter;
                        try {
                            setter = this.propertyAccessor.getSetter(this.resultClass, alias);
                        } catch (final PropertyNotFoundException e) {
                            for (final Field field : this.fields) {
                                final String fieldName = field.getName();
                                if (fieldName.equalsIgnoreCase(alias)) {
                                    alias = fieldName;
                                    break;
                                }
                            }
                            setter = this.propertyAccessor.getSetter(this.resultClass, alias);
                        }
                        this.setters[i] = setter;
                    }
                }
            }
            result = this.resultClass.newInstance();

            for (int i = 0; i < aliases.length; i++) {
                if (this.setters[i] != null) {
                    this.setters[i].set(result, tuple[i], null);
                }
            }
        } catch (final InstantiationException e) {
            throw new HibernateException("Could not instantiate resultclass: " + this.resultClass.getName(), e);
        } catch (final IllegalAccessException e) {
            throw new HibernateException("Could not instantiate resultclass: " + this.resultClass.getName(), e);
        }

        return result;
    }

    @SuppressWarnings("rawtypes")
    public List transformList(final List collection) {
        return collection;
    }

    @Override
    public int hashCode() {
        int result;
        result = this.resultClass.hashCode();
        result = 31 * result + this.propertyAccessor.hashCode();
        return result;
    }
}