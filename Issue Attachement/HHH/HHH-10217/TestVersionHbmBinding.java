package org.hibernate.test.boot.binding;

import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.jaxb.Origin;
import org.hibernate.boot.jaxb.SourceType;
import org.hibernate.boot.jaxb.internal.FileXmlSource;
import org.hibernate.boot.jaxb.internal.MappingBinder;
import org.hibernate.boot.model.naming.ImplicitNamingStrategyLegacyJpaImpl;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.boot.spi.XmlMappingBinderAccess;
import org.junit.Test;

import java.io.File;
import java.net.URL;

/**
 * @author mwacker
 */
public class TestVersionHbmBinding {

	private StandardServiceRegistry ssr;
	private MappingBinder binder;


	@Test()
	public void testVersionBinding() {
		final MetadataSources metadataSources = new MetadataSources();
		metadataSources.addResource("org/hibernate/test/boot/binding/Mappings.hbm.xml");

		final Metadata metadata = metadataSources.getMetadataBuilder()
				.applyImplicitNamingStrategy(ImplicitNamingStrategyLegacyJpaImpl.INSTANCE)
				.build();
	}


	private Exception couldNotFindHbmXmlFile() {
		throw new IllegalStateException("Could not locate hbm.xml file by resource lookup");
	}
}
