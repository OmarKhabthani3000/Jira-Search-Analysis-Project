//$Id$
package org.hibernate.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import net.sf.hibernate.LockMode;
import junit.framework.TestCase;

/**
 * @author Andrej Golovnin
 * @version $Revision$
 */
public class LockModeSerializationTest extends TestCase {
	
	public void testNONE() throws Exception {
		LockMode mode = serialize_deserialize(LockMode.NONE);
		assertTrue(LockMode.NONE == mode);
	}
	
	public void testREAD() throws Exception {
		LockMode mode = serialize_deserialize(LockMode.READ);
		assertTrue(LockMode.READ == mode);
	}
	
	public void testUPGRADE() throws Exception {
		LockMode mode = serialize_deserialize(LockMode.UPGRADE);
		assertTrue(LockMode.UPGRADE == mode);
	}
	
	public void testUPGRADE_NOWAIT() throws Exception {
		LockMode mode = serialize_deserialize(LockMode.UPGRADE_NOWAIT);
		assertTrue(LockMode.UPGRADE_NOWAIT == mode);
	}
	
	public void testWRITE() throws Exception {
		LockMode mode = serialize_deserialize(LockMode.WRITE);
		assertTrue(LockMode.WRITE == mode);
	}
	
	private LockMode serialize_deserialize(LockMode mode) throws Exception {
        ByteArrayOutputStream bout = new ByteArrayOutputStream(512);
        ObjectOutputStream out = new ObjectOutputStream(bout);
        out.writeObject(mode);
        out.close();
        ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
        ObjectInputStream in = new ObjectInputStream(bin);
        LockMode m = (LockMode) in.readObject();
        in.close();
		return m;
	}
}
