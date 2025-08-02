import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

public class JarVisitorFactoryTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		String file = args[0];
		for(int i=0; i<10; i++){
			byte[] arr1 = testOld(file);
			byte arr2[] = testNew(file);
		}

	}

	public static byte[] testNew(String file) throws Exception {

		BufferedInputStream is = null;
		try {
			is = new BufferedInputStream(new FileInputStream(new File(file)));
			long start = System.currentTimeMillis();
			byte arr[] = getBytesFromInputStreamNew(is);
			System.out.println("time: " + (System.currentTimeMillis() - start));
			System.out.println("size: " + arr.length);
			return arr;

		} finally {
			if (is != null)
				is.close();
		}

	}

	public static byte[] testOld(String file) throws Exception {
		BufferedInputStream is = null;
		try {
			is = new BufferedInputStream(new FileInputStream(new File(file)));
			long start = System.currentTimeMillis();
			byte arr[] = getBytesFromInputStream(is);
			System.out.println("old time: "
					+ (System.currentTimeMillis() - start));
			System.out.println("old size: " + arr.length);
			return arr;

		} finally {
			if (is != null)
				is.close();
		}

	}

	public static byte[] getBytesFromInputStream(InputStream inputStream)
			throws IOException {
		int size;

		byte[] entryBytes = new byte[0];
		for (;;) {
			byte[] tmpByte = new byte[4096];
			size = inputStream.read(tmpByte);
			if (size == -1)
				break;
			byte[] current = new byte[entryBytes.length + size];
			System.arraycopy(entryBytes, 0, current, 0, entryBytes.length);
			System.arraycopy(tmpByte, 0, current, entryBytes.length, size);
			entryBytes = current;
		}
		return entryBytes;
	}

	public static byte[] getBytesFromInputStreamNew(InputStream inputStream)
			throws IOException {
		int size;
		List<byte[]> data = new LinkedList<byte[]>();
		int bufferSize = 4096;
		byte[] tmpByte = new byte[bufferSize];
		int offset = 0;
		int total = 0;
		for (;;) {
			size = inputStream.read(tmpByte, offset, bufferSize - offset);
			if (size == -1)
				break;

			offset += size;

			if (offset == tmpByte.length) {
				data.add(tmpByte);
				tmpByte = new byte[bufferSize];
				offset = 0;
				total += tmpByte.length;
			}

		}

		byte[] result = new byte[total + offset];
		int count = 0;
		for (byte[] arr : data) {
			System.arraycopy(arr, 0, result, count * arr.length, arr.length);
			count++;
		}
		System.arraycopy(tmpByte, 0, result, count * tmpByte.length, offset);

		return result;
	}
}
