/**
 *
 */
package org.hibernate.dialect;

/**
 * In DB2 for z/OS "order of" is not supported inside over(). For example in:
 * select * from ( select inner2_.*, rownumber() over(order by order of inner2_)
 * as rownumber_ from ( ... A work around for this issue could be changing the
 * above SQL to: select * from ( select inner2_.*, rownumber() over(order by
 * inner2_.SORT_COLUMN_ALIAS_HERE) as rownumber_ from ( ... or for cases when
 * the subquery isn't ordered: select * from ( select inner2_.*, rownumber()
 * over() as rownumber_ from ( ...
 *
 * Which works for DB2 Linux/Windows/Unix and for z/OS.
 *
 * See bug: https://hibernate.atlassian.net/browse/HHH-7001
 *
 * @author rubensa
 *
 */
public class DB2zOSDialect extends DB2Dialect {

    /*
     * (non-Javadoc)
     * 
     * @see org.hibernate.dialect.DB2Dialect#getLimitString(java.lang.String,
     * int, int)
     */
    @Override
    public String getLimitString(String sql, int offset, int limit) {
        return super.getLimitString(sql, offset, limit).replace(
                "order by order of inner2_", getExpandedOrder(sql));
    }

    protected String getExpandedOrder(String sql) {
        StringBuffer order = new StringBuffer();
        int orderByPos = sql.lastIndexOf("order by");
        if (orderByPos > -1) {
            order.append("order by ");
            String orderBy = sql.substring(orderByPos + 8).trim();
            String[] orderParams = null;
            if (orderBy.contains(",")) {
                orderParams = orderBy.split(",");
            } else {
                orderParams = new String[] { orderBy };
            }
            for (int i = 0; i < orderParams.length; i++) {
                String orderParameter = orderParams[i].trim();
                String columnName = orderParameter;
                int spacePos = orderParameter.indexOf(' ');
                if (spacePos != -1) {
                    columnName = orderParameter.substring(0, spacePos);
                }
                String columnAlias = getColumnAlias(sql, columnName);
                if (order.length() > 9) {
                    order.append(", ");
                }
                order.append("inner2_.")
                        .append(columnAlias)
                        .append(orderParameter.substring(spacePos,
                                orderParameter.length()));
            }
        }
        return order.toString();
    }

    protected String getColumnAlias(String sql, String columnName) {
        String columnAlias = "";
        int aliasPos = sql.indexOf(columnName + " as ");
        if (aliasPos != -1) {
            for (int j = aliasPos + columnName.length() + 4; j < sql.length(); j++) {
                char ch = sql.charAt(j);
                if (ch == ',' || ch == ' ') {
                    break;
                }
                columnAlias += ch;
            }
        } else {
            columnAlias = columnName;
        }
        return columnAlias;
    }
}
