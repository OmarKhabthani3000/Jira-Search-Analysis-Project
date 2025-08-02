/*
 * Copyright (C) Siemens SA - 2005
 * The reproduction, transmission or use of this document or its contents is not
 * permitted without express written authorization. All rights, including rights
 * created by patent grant or registration of a utility model or design, are
 * reserved. Modifications made to this document are restricted to authorized
 * personnel only. Technical specifications and features are binding only when
 * specifically and expressly agreed upon in a written contract.
 *
 * IC R&D WON - LISBON
 */

package org.hibernate.test;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;

public class InterfaceProxyTest extends TestCase {
	
	public InterfaceProxyTest(String desc) {
		super(desc);
	}
	
	protected String[] getMappings() {
		return new String[] { "InterfaceProxy.hbm.xml" };
	}
	
	public void testPolymorphic() throws HibernateException {
		// session 1
		Session s1 = openSession();
		
		Bar bar = new Bar();
		bar.setString("bar");
		Long barId = (Long) s1.save(bar);
		
		Bleep bleep = new Bleep();
		bleep.setString("bleep");
		Long bleepId = (Long) s1.save(bleep);
		
		s1.close();
		
		// session 2
		Session s2 = openSession();
		
		BleepProxy bleepProxy = (BleepProxy) s2.load(Abstract.class, bleepId);
		assertEquals(bleep.getString(), bleepProxy.getString());
		
		BarProxy barProxy = (BarProxy) s2.load(Abstract.class, barId);
		assertEquals(bar.getString(), barProxy.getString());
		
		s2.close();
	}
}
