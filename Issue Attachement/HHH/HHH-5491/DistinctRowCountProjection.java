package org.hibernate.criterion;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.type.Type;

/**
 * Extended version of {@link RowCountProjection} that allows to include the
 * 'distinct' keyword to the count.
 *
 * @see RowCountProjection
 */
public class DistinctRowCountProjection extends SimpleProjection {
    private static final long serialVersionUID = -5082542035205061741L;

    private boolean distinct;

    protected DistinctRowCountProjection() {
    }

    @Override
    public Type[] getTypes(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
        return new Type[] { Hibernate.INTEGER };
    }

    public DistinctRowCountProjection setDistinct() {
        this.distinct = true;
        return this;
    }

    @Override
    public String toSqlString(Criteria criteria, int position, CriteriaQuery criteriaQuery) throws HibernateException {
        return new StringBuffer().append(this.countExpression() + " as y").append(position).append('_')
                .toString();
    }

    @Override
    public String toString() {
        return this.countExpression();
    }

    private String countExpression() {
        return this.distinct ? "count(distinct(*))" : "count(*)";
    }

}
