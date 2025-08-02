package org.hibernate;

import org.hibernate.dialect.Oracle10gDialect;
import org.hibernate.resource.transaction.spi.TransactionStatus;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;

import java.sql.Connection;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class OracleBadConnectionTest extends BaseCoreFunctionalTestCase {

    @Test
    @RequiresDialect(Oracle10gDialect.class)
    public void test_ORA_03113() throws Exception {
        String simpleSql = "select * from dual";

        openSession();
        Session originalSession = session;
        final Connection[] connections = new Connection[2];
        session.doWork(connection -> connections[0] = connection);

        // initially this statement works
        session.createNativeQuery(simpleSql).getResultList();

        try {
            // create a sql statement that will make Oracle give a ORA-03113 error
            String sql = simpleSql + " where 'foo' in (" +
                    IntStream.range(0, 25)
                            .mapToObj(i -> IntStream.range(0, 999)
                                    .mapToObj(operand -> "'" + UUID.randomUUID().toString() + "'")
                                    .collect(Collectors.joining(", ")) + ")")
                            .collect(Collectors.joining("or 'foo' in ("));


            session.createNativeQuery(sql).getResultList();
        } catch (Exception e) {
            // ignore, oracle connection is hosed at this point
        } finally {
            try {
                Transaction tx = session.getTransaction();
                if (tx != null && TransactionStatus.ACTIVE.equals(tx.getStatus())) {
                    tx.rollback();
                }
            } catch (Exception e) {
                e.printStackTrace();
                // rollback fails
            } finally {
                session.close();
            }
        }

        // open new session
        openSession();
        session.doWork(connection -> connections[1] = connection);

        // should be new session
        Assert.assertNotSame(session, originalSession);

        // should be new connection
        Assert.assertNotSame(connections[0], connections[1]);

        // this statement should work again
        session.createNativeQuery(simpleSql).getResultList();
    }
}
