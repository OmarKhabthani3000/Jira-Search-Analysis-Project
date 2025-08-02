package org.hibernate.test.legacy;

import org.hibernate.classic.Session;

import junit.framework.Test;
import junit.textui.TestRunner;

import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.junit.functional.FunctionalTestClassTestSuite;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class IngresAmbiguousColumnTest extends LegacyTestCase {
	private static final Logger log = LoggerFactory.getLogger( IngresAmbiguousColumnTest.class );

	public IngresAmbiguousColumnTest(String arg) {
		super(arg);
	}

	public String[] getMappings() {
		return new String[] {
			"legacy/FooBar.hbm.xml",
			"legacy/Baz.hbm.xml",
			"legacy/Glarch.hbm.xml",
			"legacy/Fee.hbm.xml",
			"legacy/Qux.hbm.xml",
			"legacy/Fum.hbm.xml",
			"legacy/Fumm.hbm.xml",
			"legacy/Fo.hbm.xml",
			"legacy/One.hbm.xml",
			"legacy/Many.hbm.xml",
			"legacy/Immutable.hbm.xml",
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
		return new FunctionalTestClassTestSuite( IngresAmbiguousColumnTest.class );
	}

	public static void main(String[] args) throws Exception {
		TestRunner.run( suite() );
	}

	public void testCreate() throws Exception {
		Session s = openSession();
		s.beginTransaction();
		Foo foo = new Foo();
		s.save(foo);
		s.getTransaction().commit();
		s.close();

		s = openSession();
		s.beginTransaction();
		Foo foo2 = new Foo();
		s.load( foo2, foo.getKey() );
		assertTrue( "create", foo.equalsFoo(foo2) );
		s.delete(foo2);
		s.getTransaction().commit();
		s.close();
	}
}
