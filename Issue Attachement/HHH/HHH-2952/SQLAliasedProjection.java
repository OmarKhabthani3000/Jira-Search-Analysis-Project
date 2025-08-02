/**
 *
 */
package org.hibernate.criterion;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.Projection;
import org.hibernate.type.Type;
import org.hibernate.util.StringHelper;

/**
 * A SQL fragment. The string {alias} will be replaced by the alias of the root entity.
 * ny other aliases or property names will be searched, if provided in {aliasName} manner
  * @author Sergey Pulyaev
*/
@SuppressWarnings("unused")
public class SQLAliasedProjection implements Projection {

    /**
     *
     */
    private static final long serialVersionUID = -7028362199361547260L;
    private final String sql;
    @SuppressWarnings("unused")
    private final String groupBy;
    private final Type[] types;
    private String[]     aliases;
    private String[]     columnAliases;
    private boolean      grouped;

    public String toSqlString(Criteria criteria, int loc, CriteriaQuery criteriaQuery) throws HibernateException {
        return applyAliases(criteria, criteriaQuery, sql);
    }

    /**
     * @param criteria
     * @param criteriaQuery
     * @return
     */
    private String applyAliases(Criteria criteria, CriteriaQuery criteriaQuery, String sql) {
        StringBuilder res = new StringBuilder();
        String src = StringHelper.replace(sql, "{alias}", criteriaQuery.getSQLAlias(criteria));
        int i = 0;
        int cnt = src.length();
        while (i < cnt) {
            int l = src.indexOf('{', i);
            if(l == -1){
                break;
            }

            String before = src.substring(i, l);
            res.append(before);

            int r = src.indexOf('}', l);
            String alias = src.substring(l+1, r);
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

    public String toGroupSqlString(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
        return applyAliases(criteria, criteriaQuery, groupBy);
    }

    @SuppressWarnings("unused")
    public Type[] getTypes(Criteria crit, CriteriaQuery criteriaQuery) throws HibernateException {
        return types;
    }

    @Override
    public String toString() {
        return sql;
    }

    public SQLAliasedProjection(String sql, String[] columnAliases, Type[] types) {
        this(sql, null, columnAliases, types);
    }

    public SQLAliasedProjection(String sql, String[] columnAliases, Type[] types, String grouped) {
        this(sql, grouped, columnAliases, types);
    }

    protected SQLAliasedProjection(String sql, String groupBy, String[] columnAliases, Type[] types) {
        this.sql = sql;
        this.types = types;
        this.aliases = columnAliases;
        this.columnAliases = columnAliases;
        this.grouped = groupBy != null;
        this.groupBy = groupBy;
    }

    public String[] getAliases() {
        return aliases;
    }

    public String[] getColumnAliases(int loc) {
        return columnAliases;
    }

    public boolean isGrouped() {
        return grouped;
    }

    public Type[] getTypes(String alias, Criteria crit, CriteriaQuery criteriaQuery) {
        return null; // unsupported
    }

    public String[] getColumnAliases(String alias, int loc) {
        return null; // unsupported
    }
}
