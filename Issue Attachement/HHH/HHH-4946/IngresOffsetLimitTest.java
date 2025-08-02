package org.hibernate.test.legacy;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.SortedSet;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.TreeSet;

import junit.framework.Test;
import junit.textui.TestRunner;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.FlushMode;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.LazyInitializationException;
import org.hibernate.LockMode;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Query;
import org.hibernate.QueryException;
import org.hibernate.ScrollableResults;
import org.hibernate.Transaction;
import org.hibernate.LockOptions;
import org.hibernate.cfg.Environment;
import org.hibernate.classic.Session;
import org.hibernate.connection.ConnectionProvider;
import org.hibernate.connection.DriverManagerConnectionProvider;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.dialect.DB2Dialect;
import org.hibernate.dialect.DerbyDialect;
import org.hibernate.dialect.HSQLDialect;
import org.hibernate.dialect.IngresDialect;
import org.hibernate.dialect.Ingres9Dialect;
import org.hibernate.dialect.InterbaseDialect;
import org.hibernate.dialect.MckoiDialect;
import org.hibernate.dialect.MySQLDialect;
import org.hibernate.dialect.Oracle8iDialect;
import org.hibernate.dialect.PointbaseDialect;
import org.hibernate.dialect.PostgreSQLDialect;
import org.hibernate.dialect.SAPDBDialect;
import org.hibernate.dialect.Sybase11Dialect;
import org.hibernate.dialect.SybaseASE15Dialect;
import org.hibernate.dialect.SybaseDialect;
import org.hibernate.dialect.TimesTenDialect;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.junit.functional.FunctionalTestClassTestSuite;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.type.Type;
import org.hibernate.util.JoinedIterator;
import org.hibernate.util.SerializationHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class IngresOffsetLimitTest extends LegacyTestCase {
	private static final Logger log = LoggerFactory.getLogger( IngresOffsetLimitTest.class );

	public IngresOffsetLimitTest(String arg) {
		super(arg);
	}

	public String[] getMappings() {
		return new String[] {
			"legacy/FooBar.hbm.xml",
			"legacy/Baz.hbm.xml",
			"legacy/Qux.hbm.xml",
			"legacy/Glarch.hbm.xml",
			"legacy/Fum.hbm.xml",
			"legacy/Fumm.hbm.xml",
			"legacy/Fo.hbm.xml",
			"legacy/One.hbm.xml",
			"legacy/Many.hbm.xml",
			"legacy/Immutable.hbm.xml",
			"legacy/Fee.hbm.xml",
			"legacy/Vetoer.hbm.xml",
			"legacy/Holder.hbm.xml",
			"legacy/Location.hbm.xml",
			"legacy/Stuff.hbm.xml",
			"legacy/Container.hbm.xml",
			"legacy/Simple.hbm.xml",
			"legacy/XY.hbm.xml"
		};
	}

	public static Test suite() {
		return new FunctionalTestClassTestSuite( IngresOffsetLimitTest.class );
	}

	public static void main(String[] args) throws Exception {
		TestRunner.run( suite() );
	}

	public void testLimit() throws Exception {
		Session s = openSession();
		Transaction txn = s.beginTransaction();
		for ( int i=0; i<10; i++ ) s.save( new Foo() );
		Iterator iter = s.createQuery("from Foo foo")
			.setMaxResults(4)
			.setFirstResult(2)
			.iterate();
		int count=0;
		while ( iter.hasNext() ) {
			iter.next();
			count++;
		}
		assertTrue(count==4);
		iter = s.createQuery("select distinct foo from Foo foo")
			.setMaxResults(2)
			.setFirstResult(2)
			.list()
			.iterator();
		count=0;
		while ( iter.hasNext() ) {
			iter.next();
			count++;
		}
		assertTrue(count==2);
		iter = s.createQuery("select distinct foo from Foo foo")
		.setMaxResults(3)
		.list()
		.iterator();
		count=0;
		while ( iter.hasNext() ) {
			iter.next();
			count++;
		}
		assertTrue(count==3);
		assertEquals( 10, doDelete( s, "from Foo foo" ) );
		txn.commit();
		s.close();
	}
}
