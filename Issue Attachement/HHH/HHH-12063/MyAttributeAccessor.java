package org.hibernate.envers.bugs;

import org.hibernate.property.access.internal.PropertyAccessStrategyBasicImpl;
import org.hibernate.property.access.spi.PropertyAccess;

public class MyAttributeAccessor extends PropertyAccessStrategyBasicImpl {
    static boolean invoked;

    @Override
    public PropertyAccess buildPropertyAccess(Class containerJavaType, String propertyName) {
        invoked = true;
        return super.buildPropertyAccess(containerJavaType, propertyName);
    }
}
