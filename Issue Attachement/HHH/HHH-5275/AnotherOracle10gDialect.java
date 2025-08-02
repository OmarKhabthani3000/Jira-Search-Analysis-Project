import java.util.Iterator;
import java.util.Map;

import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.sql.ForUpdateFragment;

/**
 * 'Patches' {@link org.hibernate.dialect.Oracle10gDialect} to apply correct locking for {@link org.hibernate.Criteria} queries.
 * The dialect class is referenced in the 'hibernate.*.properties' files.<br/>
 * Since Hibernate 3.5 the {@link org.hibernate.LockMode} set to a {@link org.hibernate.Criteria} object is ignored when
 * generating the SELECT-statement. This is fixed by overriding the {@link #applyLocksToSql(String, LockOptions, Map)} method.<br/>
 */
public class Oracle10gDialect extends org.hibernate.dialect.Oracle10gDialect {

    /**
     * {@inheritDoc}
     */
    @Override
    public String applyLocksToSql(String sql, LockOptions aliasedLockOptions, Map keyColumnNames) {
        if (aliasedLockOptions != null && aliasedLockOptions.getAliasLockCount() > 0) {
            Iterator<Map.Entry<String, LockMode>> iter = aliasedLockOptions.getAliasLockIterator();
            while (iter.hasNext()) {
                Map.Entry<String, LockMode> entry = iter.next();
                if (keyColumnNames.containsKey(entry.getKey())) {
                    aliasedLockOptions.setLockMode(entry.getValue());
                    break;
                }
            }
        }

        return sql + new ForUpdateFragment(this, aliasedLockOptions, keyColumnNames).toFragmentString();
    }
}