import java.sql.SQLException;
import org.hibernate.JDBCException;
import org.hibernate.PessimisticLockException;
import org.hibernate.dialect.PostgreSQL82Dialect;
import org.hibernate.exception.LockAcquisitionException;
import org.hibernate.exception.internal.SQLStateConversionDelegate;
import org.hibernate.exception.spi.SQLExceptionConversionDelegate;
import org.hibernate.internal.util.JdbcExceptionHelper;

/**
 * A stupid-simple PostgreSQL dialect that will properly map locking issues back to Hibernate exceptions.
 */
public class Postgres91Dialect extends PostgreSQL82Dialect {

    @Override
    public SQLExceptionConversionDelegate buildSQLExceptionConversionDelegate() {
        SQLExceptionConversionDelegate delegate = super.buildSQLExceptionConversionDelegate();
        if (delegate == null) {
            delegate = new SQLStateConversionDelegate(this) {

                @Override
                public JDBCException convert(SQLException sqlException, String message, String sql) {
                    JDBCException exception = super.convert(sqlException, message, sql);

                    if (exception == null) {
                        String sqlState = JdbcExceptionHelper.extractSqlState(sqlException);
                        
                        if ("40P01".equals(sqlState)) { // DEADLOCK
                            exception = new LockAcquisitionException(message, sqlException, sql);
                        }
                        
                        if ("55P03".equals(sqlState)) { // LOCK_NOT_AVAILABLE
                            
                            exception = new PessimisticLockException(message, sqlException, sql);
                        }
                    }

                    return exception;
                }
            };
        }
        return delegate;
    }
}

