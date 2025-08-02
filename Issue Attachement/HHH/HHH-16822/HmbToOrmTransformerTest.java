package org.test.tools.hmb;

import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import org.hibernate.boot.jaxb.Origin;
import org.hibernate.boot.jaxb.SourceType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmHibernateMapping;
import org.hibernate.boot.jaxb.hbm.transform.HbmXmlTransformer;
import org.hibernate.boot.jaxb.hbm.transform.UnsupportedFeatureHandling;
import org.hibernate.boot.jaxb.internal.MappingBinder;
import org.hibernate.boot.jaxb.mapping.JaxbEntityMappings;
import org.hibernate.boot.jaxb.spi.Binding;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.StringWriter;

public class HmbToOrmTransformerTest {

    private static final Logger LOG = LoggerFactory.getLogger(HmbToOrmTransformerTest.class);

    @Test
    public void hbmToOrmTransformTest() throws Exception {
        Assert.assertNotNull(transformResource("/hbm/simple.hbm.xml"));
    }

    private String transformResource(String resourceName) {
        MappingBinder.Options options = new MappingBinder.Options() {
            @Override
            public boolean validateMappings() {
                return false;
            }

            @Override
            public boolean transformHbmMappings() {
                return false;
            }
        };

        MappingBinder mappingBinder = new MappingBinder(
                MappingBinder.class.getClassLoader()::getResourceAsStream,
                () -> options, () -> UnsupportedFeatureHandling.IGNORE);

        final Marshaller marshaller;
        try {
            marshaller = mappingBinder.mappingJaxbContext().createMarshaller();
        } catch (JAXBException e) {
            throw new RuntimeException( "Unable to create JAXB Marshaller", e );
        }

        final Origin origin = new Origin( SourceType.RESOURCE, resourceName );

        InputStream hbmXmlIs = HmbToOrmTransformerTest.class.getResourceAsStream(resourceName);
        final Binding<JaxbHbmHibernateMapping> binding = bindMapping( mappingBinder, hbmXmlIs, origin );
        if ( binding == null ) {
            return null;
        }

        final HbmXmlTransformer.Options transformationOptions = () -> UnsupportedFeatureHandling.IGNORE;
        StringWriter sw = new StringWriter();
        try {
            JaxbEntityMappings transformed = HbmXmlTransformer.transform(binding.getRoot(), origin, transformationOptions);
            marshaller.marshal(transformed, sw);
            String transformedXml = sw.toString();
            LOG.info("TRANSFORMED MAPPING: " + transformedXml);
            return transformedXml;
        } catch (JAXBException e) {
            LOG.error("JAXB Error", e);
            return null;
        }
    }

    private Binding<JaxbHbmHibernateMapping> bindMapping(MappingBinder mappingBinder, InputStream hbmXmlInputStream, Origin origin) {
        try {
            return mappingBinder.bind( hbmXmlInputStream, origin );
        } catch (Exception e) {
            return null;
        }
    }
}
