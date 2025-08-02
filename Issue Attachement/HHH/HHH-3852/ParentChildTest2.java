package org.hibernate.test.legacy;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.Transaction;
import org.hibernate.classic.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.dialect.DB2Dialect;



public class ParentChildTest2 extends LegacyTestCase {

	private Session s;
	public ParentChildTest2(String x) {
		super(x);
	}

	public String[] getMappings() {		
		return new String[] {
			"legacy/ParentChild.hbm.xml",
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
		 	"legacy/Simple.hbm.xml",
		 	"legacy/Container.hbm.xml",
		 	"legacy/Circular.hbm.xml",
		 	"legacy/Stuff.hbm.xml"
		};
		
	}
	public void testArrayHQL() {
		s = openSession();
		Transaction t = s.beginTransaction();
		Baz baz = new Baz();
		s.save(baz);
		Foo foo1 = new Foo();
		s.save(foo1);
		baz.setFooArray( new FooProxy[] { foo1 } );
		
		Query createQuery = s.createQuery("select  from "+ Baz.class.getName() + "  inner join fooArray");		
		List list =createQuery.list();
		
		System.out.println(list.size());
		
		s.close();
		
	}
	public void testArrayCriteria() {
		
		s = openSession();
		Transaction t = s.beginTransaction();
		Baz baz = new Baz();
		s.save(baz);
		Foo foo1 = new Foo();
		s.save(foo1);
		baz.setFooArray( new FooProxy[] { foo1 } );		

		Criteria crit = s.createCriteria(Baz.class).createCriteria("fooArray");						
		List list = crit.list();		
		
		System.out.println(list.size());						
		
		s.close();
	}
	
}
