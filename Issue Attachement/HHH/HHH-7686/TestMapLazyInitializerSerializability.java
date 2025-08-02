package org.hibernate.proxy.map;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import junit.framework.Assert;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.hibernate.proxy.map.MapLazyInitializer;
import org.junit.Test;

public class TestMapLazyInitializerSerializability {
	@Test
	public void testSerialization() {
		MapLazyInitializer mli = new MapLazyInitializer("MyEntity", 42, null);
		MapProxy mapEntityWhichIsNotAndNeverWillBeInitialized = new MapProxy(
				mli);

		byte[] serializedMapEntityData = serialized(mapEntityWhichIsNotAndNeverWillBeInitialized);

		MapProxy deSerializedMapEntityWhichIsNotAndNeverWillBeInitialized = (MapProxy) deserialized(serializedMapEntityData);

		Assert.assertEquals(mapEntityWhichIsNotAndNeverWillBeInitialized
				.getHibernateLazyInitializer().getEntityName(),
				deSerializedMapEntityWhichIsNotAndNeverWillBeInitialized
						.getHibernateLazyInitializer().getEntityName());

		Assert.assertEquals(mapEntityWhichIsNotAndNeverWillBeInitialized
				.getHibernateLazyInitializer().getIdentifier(),
				deSerializedMapEntityWhichIsNotAndNeverWillBeInitialized
						.getHibernateLazyInitializer().getIdentifier());
	}

	private Object deserialized(byte[] aSerializedObjectByteRepresentation) {
		Object result = null;

		InputStream inputStream = new ByteArrayInputStream(
				aSerializedObjectByteRepresentation);

		try {
			ObjectInputStream objectInputStream = new ObjectInputStream(
					inputStream);

			try {
				result = objectInputStream.readObject();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} finally {
				objectInputStream.close();
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return result;
	}

	private byte[] serialized(Object anObject) {
		byte[] result = null;

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		try {
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(
					outputStream);
			try {
				objectOutputStream.writeObject(anObject);
				result = outputStream.toByteArray();
			} finally {
				objectOutputStream.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
	}
}
