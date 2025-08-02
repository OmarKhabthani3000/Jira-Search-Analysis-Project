import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.hibernate.EntityMode;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmHibernateMapping;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmRootEntityType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmTuplizerType;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.tuple.entity.DynamicMapEntityTuplizer;
import org.junit.Test;

public final class JaxbHbmTuplizerTypeMarshallingTest {

	@Test
	public void testNullEntityType() {
		try {
			generateXml(false);
		} catch (Throwable t) {
			t.printStackTrace();
			throw new AssertionError("Marshalling to xml failed");
		}
	}

	@Test
	public void testEntityTypeUnmarshalling() throws JAXBException {
		InputStream is = generateXml(true);
		try {
			createSessionFactory(is);
		} catch (Throwable t) {
			t.printStackTrace();
			throw new AssertionError("Marshalling to xml failed");
		}
	}

	private void createSessionFactory(InputStream is) throws JAXBException {
		Configuration config = new Configuration();
		config.addInputStream(is).buildSessionFactory();
	}

	private InputStream generateXml(boolean includeEntityMode)
			throws JAXBException {
		JaxbHbmHibernateMapping hm = new JaxbHbmHibernateMapping();
		JaxbHbmRootEntityType clazz = new JaxbHbmRootEntityType();
		clazz.setEntityName("MyEntity");
		clazz.setTable("MyTable");
		JaxbHbmTuplizerType tuplizer = new JaxbHbmTuplizerType();
		tuplizer.setClazz(DynamicMapEntityTuplizer.class.getCanonicalName());
		if (includeEntityMode)
			tuplizer.setEntityMode(EntityMode.MAP);
		clazz.getTuplizer().add(tuplizer);
		hm.getClazz().add(clazz);
		JAXBContext jaxbContext = JAXBContext
				.newInstance(JaxbHbmHibernateMapping.class);

		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		jaxbMarshaller.marshal(hm, System.out);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		jaxbMarshaller.marshal(hm, bos);
		byte[] byteArray = bos.toByteArray();
		return new ByteArrayInputStream(byteArray);
	}

}
