package org.hibernate.criterion;

import org.hibernate.Hibernate;
import org.hibernate.type.Type;

/**
 * @author Sergey Pulyaev
 */
public class RestrictionsExt {

    public static Criterion likeSQLAliased(String propertyName, String value) {
        return new SQLAliasedCriterion(propertyName + " like ?", value + "%", Hibernate.STRING);
    }

    public static Criterion sqlAliased(String sql, Object[] values, Type[] types) {
        return new SQLAliasedCriterion(sql, values, types);
    }


    public static Projection sqlProjection(String sql, String alias) {
        return new SQLAliasedProjection(sql + " as " + alias,
                new String[]{alias}, new Type[]{Hibernate.STRING});
    }

    public static Projection sqlProjectionGroupedString(String sql, String alias) {
        return new SQLAliasedProjection(sql + " as " + alias,
                new String[]{alias}, new Type[]{Hibernate.STRING}, sql);
    }

    @SuppressWarnings("unchecked")
    public static Projection sqlProjectionGrouped(String sql, String alias, Type clazz) {
        return new SQLAliasedProjection(sql + " as " + alias,
                new String[]{alias}, new Type[]{clazz}, sql);
    }

}
