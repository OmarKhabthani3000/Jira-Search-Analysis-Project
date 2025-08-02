package com.rdriskill.hibernate.dialect;

import java.util.Locale;

import org.hibernate.dialect.Oracle10gDialect;
import org.hibernate.dialect.pagination.AbstractLimitHandler;
import org.hibernate.dialect.pagination.LimitHandler;
import org.hibernate.dialect.pagination.LimitHelper;
import org.hibernate.engine.spi.RowSelection;

/**
 * @author adriskil
 */
public class Oracle10gDialectCustom extends Oracle10gDialect {

  @Override
  public String getLimitString(String sql, boolean hasOffset) {
    sql = sql.trim();
    String forUpdateClause = null;
    boolean isForUpdate = false;
    final int forUpdateIndex = sql.toLowerCase(Locale.ROOT).lastIndexOf("for update");
    if (forUpdateIndex > -1) {
      // save 'for update ...' and then remove it
      forUpdateClause = sql.substring(forUpdateIndex);
      sql = sql.substring(0, forUpdateIndex - 1);
      isForUpdate = true;
    }

    final StringBuilder pagingSelect = new StringBuilder(sql.length() + 100);
    if (hasOffset) {
      pagingSelect.append("select * from ( select row_.*, rownum rownum_ from ( ");
    } else {
      pagingSelect.append("select * from ( ");
    }
    pagingSelect.append(sql);
    if (hasOffset) {
      // pagingSelect.append( " ) row_ where rownum <= ?) where rownum_ > ?" );
      pagingSelect.append(" ) row_ ) where rownum_ <= ? and rownum_ > ?"); // Changed
    } else {
      pagingSelect.append(" ) where rownum <= ?");
    }

    if (isForUpdate) {
      pagingSelect.append(" ");
      pagingSelect.append(forUpdateClause);
    }

    return pagingSelect.toString();
  }

  @Override
  public LimitHandler getLimitHandler() {
    return new AbstractLimitHandler() {
      @Override
      public String processSql(String sql, RowSelection selection) {
        final boolean hasOffset = LimitHelper.hasFirstRow(selection);
        sql = sql.trim();
        String forUpdateClause = null;
        boolean isForUpdate = false;
        final int forUpdateIndex = sql.toLowerCase(Locale.ROOT).lastIndexOf("for update");
        if (forUpdateIndex > -1) {
          // save 'for update ...' and then remove it
          forUpdateClause = sql.substring(forUpdateIndex);
          sql = sql.substring(0, forUpdateIndex - 1);
          isForUpdate = true;
        }

        final StringBuilder pagingSelect = new StringBuilder(sql.length() + 100);
        if (hasOffset) {
          pagingSelect.append("select * from ( select row_.*, rownum rownum_ from ( ");
        } else {
          pagingSelect.append("select * from ( ");
        }
        pagingSelect.append(sql);
        if (hasOffset) {
          // pagingSelect.append(" ) row_ where rownum <= ?) where rownum_ > ?");
          pagingSelect.append(" ) row_ ) where rownum_ <= ? and rownum_ > ?"); // Changed
        } else {
          pagingSelect.append(" ) where rownum <= ?");
        }

        if (isForUpdate) {
          pagingSelect.append(" ");
          pagingSelect.append(forUpdateClause);
        }

        return pagingSelect.toString();
      }

      @Override
      public boolean supportsLimit() {
        return true;
      }

      @Override
      public boolean bindLimitParametersInReverseOrder() {
        return true;
      }

      @Override
      public boolean useMaxForLimit() {
        return true;
      }
    };
  }

}
