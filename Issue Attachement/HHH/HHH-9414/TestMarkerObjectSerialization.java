import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.hibernate.engine.spi.PersistenceContext;

public class TestMarkerObjectSerialization {

	public static void main(String[] args) {
		Object noRowDeserialized = null;
		try {
			ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(byteOut);
			out.writeObject(PersistenceContext.NO_ROW);
			out.close();
			byteOut.close();

			ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
			ObjectInputStream in = new ObjectInputStream(byteIn);
			noRowDeserialized = in.readObject();
			in.close();
			byteIn.close();
		} catch (IOException i) {
			i.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		if (PersistenceContext.NO_ROW != noRowDeserialized) {
			throw new RuntimeException("Reference checking is wrong");
		}
	}
}