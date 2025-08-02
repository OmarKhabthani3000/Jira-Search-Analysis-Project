package test;

import junit.framework.TestCase;
import net.sf.hibernate.cfg.Configuration;
import net.sf.hibernate.tool.hbm2ddl.SchemaExport;
import net.sf.hibernate.Session;
import net.sf.hibernate.Criteria;
import net.sf.hibernate.LockMode;
import net.sf.hibernate.expression.Expression;

import java.util.List;

/**
 * <p>
 * Denali - A CarShare Reservation System developed by EngineGreen
 * Copyright 2003, EngineGreen.  All Rights Reserved.
 * </p>
 *
 * @version $Id: AccountTest.java,v 1.2 2004/01/05 02:04:13 matt Exp $
 * @author Matt Ho <a href="mailto:matt@enginegreen.com">&lt;matt@enginegreen.com&gt;</a>
 */
public class AccountTest extends TestCase {

    public void testCreateAccount() throws Exception {
        Session session = HibernateSessionFactory.sessionFactory.openSession();
        Account account = new Account();
        account.setName("Matt");
        session.save(account);
        session.flush();
        session.connection().commit();
        session.close();
    }

    public void testSelectForUpdate() throws Exception {
        Session session = HibernateSessionFactory.sessionFactory.openSession();

        Criteria criteria = session.createCriteria(Account.class);
        criteria.setLockMode(LockMode.UPGRADE);
//        criteria.add(Expression.eq("name", "Matt"));
        List accountList = criteria.list();
        assertNotNull(accountList);

        session.flush();
        session.connection().commit();
        session.close();
    }

    /**
     * generate the table for our tests
     * @param args
     */
    public static void main(String[] args) {
        args = new String[]{
            "--properties=src/java/hibernate.properties",
            "--delimiter=;",
            "--format",
            "src/java/test/Account.hbm.xml"
        };
        SchemaExport.main(args);
    }
}
