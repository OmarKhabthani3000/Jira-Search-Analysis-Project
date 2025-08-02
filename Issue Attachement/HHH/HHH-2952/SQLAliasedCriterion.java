package org.hibernate.criterion;

import org.hibernate.Criteria;
import org.hibernate.EntityMode;
import org.hibernate.HibernateException;
import org.hibernate.engine.TypedValue;
import org.hibernate.type.Type;
import org.hibernate.util.StringHelper;

/**
 * A SQL fragment. The string {alias} will be replaced by the
 * alias of the root entity.
  Any other aliases or property names will be searched, if provided in {aliasName} manner
 *
 * @author Sergey Pulyaev
 */
public class SQLAliasedCriterion implements Criterion {

    private static final long serialVersionUID = -4025066009045126609L;

    private final String sql;
    private final TypedValue[] typedValues;


    public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
        return applyAliases(criteria, criteriaQuery);
    }

    private String applyAliases(Criteria criteria, CriteriaQuery criteriaQuery){
        StringBuilder res = new StringBuilder();
        String src = StringHelper.replace(sql, "{alias}", criteriaQuery.getSQLAlias(criteria));
        int i = 0;
        int cnt = src.length();
        while (i < cnt) {
            int l = src.indexOf('{', i);
            if (l == -1) {
                break;
            }

            String before = src.substring(i, l);
            res.append(before);

            int r = src.indexOf('}', l);
            String alias = src.substring(l + 1, r);
            if(alias.length() == 0){
                alias = "this_";
            }
            Criteria cri = null;
            // get criteria using it's path
            cri = ((org.hibernate.loader.criteria.CriteriaQueryTranslator) criteriaQuery).getCriteria(alias);
            if(cri == null){
                // if not found - get criteria using it's alias
                cri = ((org.hibernate.loader.criteria.CriteriaQueryTranslator) criteriaQuery).getAliasedCriteria(alias);
            }
            if (cri != null) {
                String sqlAlias = criteriaQuery.getSQLAlias(cri);
                res.append(sqlAlias);
            } else {
                // try to search criteria or alias in parent criteria (if we are in subcriteria)
                if(((org.hibernate.loader.criteria.CriteriaQueryTranslator) criteriaQuery).getOuterQueryTranslator() != null){
                    CriteriaQuery outerQueryTranslator = ((org.hibernate.loader.criteria.CriteriaQueryTranslator) criteriaQuery).getOuterQueryTranslator();
                    // get criteria using it's path
                    cri = ((org.hibernate.loader.criteria.CriteriaQueryTranslator) outerQueryTranslator).getCriteria(alias);
                    if(cri == null){
                        // if not found - get criteria using it's alias
                        cri = ((org.hibernate.loader.criteria.CriteriaQueryTranslator) outerQueryTranslator).getAliasedCriteria(alias);
                    }
                    if(cri != null){
                        String sqlAlias = ((org.hibernate.loader.criteria.CriteriaQueryTranslator) outerQueryTranslator).getSQLAlias(cri);
                        res.append(sqlAlias);
                    }else{
                        res.append(alias);
                    }
                }else{
                    res.append(alias);
                }
            }
            i = r + 1;
        }
        String after = src.substring(i, cnt);
        res.append(after);

        return res.toString();
    }

    public TypedValue[] getTypedValues(
            @SuppressWarnings("unused")Criteria criteria,
            @SuppressWarnings("unused")CriteriaQuery criteriaQuery)
            throws HibernateException {
        return typedValues;
    }

    @Override
    public String toString() {
        return sql;
    }

    public SQLAliasedCriterion(String sql, Object[] values, Type[] types) {
        this.sql = sql;
        typedValues = new TypedValue[values.length];
        for (int i = 0; i < typedValues.length; i++) {
            typedValues[i] = new TypedValue(types[i], values[i], EntityMode.POJO);
        }
    }

    public SQLAliasedCriterion(String sql, Object value, Type type) {
        this(sql, new Object[]{value}, new Type[]{type});
    }

}

