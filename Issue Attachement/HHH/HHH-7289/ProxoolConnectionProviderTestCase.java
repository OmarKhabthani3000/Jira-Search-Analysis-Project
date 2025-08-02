package org.hibernate.service.jdbc.connections.internal;

import junit.framework.Assert;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.junit.Test;

public class ProxoolConnectionProviderTestCase extends BaseUnitTestCase {

	@Test
	public void testConfigurable() throws Throwable {
		ProxoolConnectionProvider proxool = new ProxoolConnectionProvider();
		boolean flag = proxool instanceof org.hibernate.service.spi.Configurable;
	    Assert.assertEquals(true, flag);
	}

}