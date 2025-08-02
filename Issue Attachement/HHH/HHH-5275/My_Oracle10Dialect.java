import org.hibernate.LockOptions;
import org.hibernate.dialect.Oracle10gDialect;

public class My_Oracle10Dialect extends Oracle10gDialect
{
    public My_Oracle10Dialect()
    {
        super();
    }

    @Override
    public String getForUpdateString(String aliases, LockOptions lockOptions)
    {
        // BRJ: 10.11.2010: Workaround für Problem mit setLockMode() in Hibernate 3.5.6
        return getForUpdateString(aliases);
    }
}