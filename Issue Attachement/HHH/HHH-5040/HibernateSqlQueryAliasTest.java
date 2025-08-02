package org.wfp.rita.test.hibernate;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

import org.hibernate.Hibernate;
import org.hibernate.QueryException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.engine.QueryParameters;
import org.hibernate.impl.SQLQueryImpl;
import org.hibernate.loader.custom.CustomLoader;
import org.hibernate.loader.custom.CustomLoader.ScalarResultColumnProcessor;
import org.wfp.rita.test.base.HibernateTestBase;

/**
 * Hibernate assumes that the column name returned by
 * {@link ResultSetMetaData#getColumnName(int)} is the name it
 * can use to extract the data from the ResultSet. However, if the
 * {@link SQLQuery} uses column aliases, then this will fail here:
<pre>
org.hibernate.exception.SQLGrammarException: could not execute query
    at org.hibernate.exception.SQLStateConverter.convert(SQLStateConverter.java:90)
    at org.hibernate.exception.JDBCExceptionHelper.convert(JDBCExceptionHelper.java:66)
    at org.hibernate.loader.Loader.doList(Loader.java:2235)
    at org.hibernate.loader.Loader.listIgnoreQueryCache(Loader.java:2129)
    at org.hibernate.loader.Loader.list(Loader.java:2124)
    at org.hibernate.loader.custom.CustomLoader.list(CustomLoader.java:312)
    at org.hibernate.impl.SessionImpl.listCustomQuery(SessionImpl.java:1723)
    at org.hibernate.impl.AbstractSessionImpl.list(AbstractSessionImpl.java:165)
    at org.hibernate.impl.SQLQueryImpl.list(SQLQueryImpl.java:175)
    at org.wfp.rita.test.hibernate.HibernateSqlQueryAliasTest.testFailingWithAliases(HibernateSqlQueryAliasTest.java:45)
    at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
    at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)
    at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)
    at java.lang.reflect.Method.invoke(Method.java:597)
    at org.wfp.rita.test.base.HibernateTestBase.runTestMethod(HibernateTestBase.java:204)
    at org.wfp.rita.test.base.HibernateTestBase.runTest(HibernateTestBase.java:117)
    at junit.framework.TestCase.runBare(TestCase.java:130)
    at junit.framework.TestResult$1.protect(TestResult.java:106)
    at junit.framework.TestResult.runProtected(TestResult.java:124)
    at junit.framework.TestResult.run(TestResult.java:109)
    at junit.framework.TestCase.run(TestCase.java:120)
    at junit.framework.TestSuite.runTest(TestSuite.java:230)
    at junit.framework.TestSuite.run(TestSuite.java:225)
    at org.eclipse.jdt.internal.junit.runner.junit3.JUnit3TestReference.run(JUnit3TestReference.java:130)
    at org.eclipse.jdt.internal.junit.runner.TestExecution.run(TestExecution.java:38)
    at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.runTests(RemoteTestRunner.java:467)
    at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.runTests(RemoteTestRunner.java:683)
    at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.run(RemoteTestRunner.java:390)
    at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.main(RemoteTestRunner.java:197)
Caused by: java.sql.SQLException: Column 'id' not found.
    at com.mysql.jdbc.SQLError.createSQLException(SQLError.java:1075)
    at com.mysql.jdbc.SQLError.createSQLException(SQLError.java:989)
    at com.mysql.jdbc.SQLError.createSQLException(SQLError.java:984)
    at com.mysql.jdbc.SQLError.createSQLException(SQLError.java:929)
    at com.mysql.jdbc.ResultSetImpl.findColumn(ResultSetImpl.java:1145)
    at com.mysql.jdbc.ResultSetImpl.getInt(ResultSetImpl.java:2814)
    at org.hibernate.type.IntegerType.get(IntegerType.java:51)
    at org.hibernate.type.NullableType.nullSafeGet(NullableType.java:184)
    at org.hibernate.type.NullableType.nullSafeGet(NullableType.java:210)
    at org.hibernate.loader.custom.CustomLoader$ScalarResultColumnProcessor.extract(CustomLoader.java:497)
    at org.hibernate.loader.custom.CustomLoader$ResultRowProcessor.buildResultRow(CustomLoader.java:443)
    at org.hibernate.loader.custom.CustomLoader.getResultColumnOrRow(CustomLoader.java:340)
    at org.hibernate.loader.Loader.getRowFromResultSet(Loader.java:629)
    at org.hibernate.loader.Loader.doQuery(Loader.java:724)
    at org.hibernate.loader.Loader.doQueryAndInitializeNonLazyCollections(Loader.java:259)
    at org.hibernate.loader.Loader.doList(Loader.java:2232)
    ... 26 more
</pre>
 * 
 * <p>Hibernate appears to support auto-discovery of result set columns,
 * notwithstanding bug 
 * <a href="http://opensource.atlassian.com/projects/hibernate/browse/HHH-436">HHH-436</a>
 * which was closed as fixed. No {@link QueryException} is thrown if the
 * returns of the query have not been defined.
 * 
 * <p>Instead, {@link SQLQueryImpl#verifyParameters} sets
 * {@link SQLQueryImpl#autodiscovertypes}, which ends up in
 * {@link QueryParameters#autodiscovertypes}, which
 * {@link CustomLoader#doQuery} passes to {@link CustomLoader#getResultSet},
 * which then calls {@link CustomLoader#autoDiscoverTypes}, which calls
 * {@link ScalarResultColumnProcessor#performDiscovery}.
 * 
 * <p>performDiscovery() calls {@link ResultSetMetaData#getColumnName}
 * to retrieve each column name from the result set, which ends up in
 * the {@link CustomLoader.ResultRowProcessor}, where
 * {@link ScalarResultColumnProcessor#extract} tries to use it
 * to extract the column data from the result set. Because the name doesn't
 * match the actual alias used in the result set, this fails with the
 * exception given above.
 * 
 * <p>The <a href="http://jcp.org/aboutJava/communityprocess/final/jsr221/index.html">JDBC 4.0 Specification</a>
 * does not specify whether <code>ResultSetMetaData.getColumnName()</code>
 * should return the name of the underlying column, or the name of the
 * alias. It seems bizarre to me that it returns a value that cannot
 * be passed to {@link ResultSet#getObject(String)}. However, both
 * MySQL and H2 take the position that we should call
 * {@link ResultSetMetaData#getColumnLabel} instead to get the name that
 * can be used on the <code>ResultSet</code>:
 * 
 * <ul>
 * <li><a href="http://bugs.mysql.com/bug.php?id=21379">MySQL bug 21379</a>
 * <li><a href="http://bugs.mysql.com/bug.php?id=21596">MySQL bug 21596</a>
 * <li><a href="http://www.mail-archive.com/h2-database@googlegroups.com/msg00876.html">H2 mailing list discussion</a>
 * </ul>
 * 
 * The fix would appear to be modifying
 * {@link ScalarResultColumnProcessor#performDiscovery}
 * (or {@link CustomLoader.Metadata#getColumnName}) so that it calls
 * getColumnLabel() instead of getColumnName(). The workaround is to
 * explicitly specify column aliases with {@link SQLQuery#addScalar}.
 * 
 * @author Chris Wilson <chris+rita@aptivate.org>
 */
public class HibernateSqlQueryAliasTest extends HibernateTestBase
{
    protected Class[] getMappings()
    {
        return new Class[]{};
    }
    
    public void setUp() throws Exception
    {
        super.setUp();
        
        Session session = openSession();
        Connection conn = session.connection();
        
        Statement stmt = conn.createStatement();
        stmt.execute("DROP TABLE IF EXISTS foo");
        stmt.execute("CREATE TABLE foo (id INT4)");
        stmt.execute("INSERT INTO foo VALUES (10)");
        
        session.close();
    }
    
    public void testSuccessfulWithoutAliases()
    {
        Session session = openSession();
        SQLQuery query = session.createSQLQuery("SELECT id FROM foo");
        query.list();
        session.close();
    }
    
    public void testFailingWithAliases()
    {
        Session session = openSession();
        SQLQuery query = session.createSQLQuery("SELECT id AS i FROM foo");
        query.list();
        session.close();
    }
    
    public void testWorkaroundWithAliases()
    {
        Session session = openSession();
        SQLQuery query = session.createSQLQuery("SELECT id AS i FROM foo");
        query.addScalar("i", Hibernate.INTEGER);
        query.list();
        session.close();
    }
}
