package org.hibernate.boot.jaxb.internal.stax;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

public class LocalXmlResourceResolverTest {

	@Test
	public void testLocalHttpResource() {
		final String HTTP_MAPPING =  "http://hibernate.org/dtd/hibernate-mapping-3.0.dtd";
		boolean matches = LocalXmlResourceResolver.ALTERNATE_MAPPING_DTD.matches(HTTP_MAPPING, HTTP_MAPPING);
		Assert.assertTrue(matches);
	}
	
	@Test
	public void testLocalHttpsResource() {
		final String HTTPS_MAPPING =  "https://hibernate.org/dtd/hibernate-mapping-3.0.dtd";
		boolean matches = LocalXmlResourceResolver.ALTERNATE_MAPPING_DTD.matches(HTTPS_MAPPING, HTTPS_MAPPING);
		Assert.assertTrue(matches);
	}
}
