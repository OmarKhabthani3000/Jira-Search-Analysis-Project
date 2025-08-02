package test;

import static org.junit.Assert.*;
import org.junit.*;
import java.io.*;
import java.sql.Blob;
import org.hibernate.Hibernate;

public class TestHibernateGetBlob
{
  final private byte[] messageBytes = "Hello World".getBytes();
  private InputStream inputStream;

  @Before
  public void setUp() throws Exception {
    inputStream = new FiveBytesByteArrayInputStream( messageBytes );
  }

  /**
   * tests whether a Blob created via Hibernate.createBlob( inputStream ) has the same length as
   * the number off bytes which it is supposed to store.
   */
  @Test
  public void testHibernateGetBlobLength() throws Exception {
    Blob blob = Hibernate.createBlob( inputStream );
    assertEquals( "if the blob length isn't correct at this point the wrong number of bytes will be persisted",
                  messageBytes.length,(int)blob.length() );
  }
  
  /**
   * tests whether FiveBytesByteArrayInputStream is a valid InputStream class 
   * and if streamToBytes() reads all bytes from an input stream. 
   * It's quite unlikely that both have errors which chancel themselfs out in this test case.
   */
  @Test
  public void testFiveBytesByteArrayInputStreamAndStreamToBytes() throws Exception {
    byte[] bytesFromInputStream = streamToBytes( inputStream );
    
    assertEquals( new String( messageBytes),
                  new String( bytesFromInputStream ) );
  }
  
  /**
   * Reads all bytes from an InputStream and returns them as byte[].
   * @see #testFiveBytesByteArrayInputStreamAndStreamToBytes()
   * @param inputStream stream to read all bytes from
   * @return all bytes off the input stream
   * @throws IOException if something goes wrong while reading the inputstream
   */
  public synchronized byte[] streamToBytes( InputStream inputStream )
  throws IOException
  {
    ByteArrayOutputStream outStream = new ByteArrayOutputStream( 64 );
    
    byte[] buffer = new byte[32];
    int bytesRead;

    while ((bytesRead = inputStream.read(buffer)) > 0) {
      outStream.write(buffer, 0, bytesRead);
    }
    
    byte[] bytes = outStream.toByteArray();
    
    //damit outStream nur einmal allokiert werden muﬂ
    outStream.reset();
    
    return bytes;
  }

  /**
   * version of the ByteArrayInputStream class which has only up to 5 bytes availabe every time
   * @see TestHibernateGetBlob#testFiveBytesByteArrayInputStreamAndStreamToBytes()
   * @author stephan schroeder
   */
  class FiveBytesByteArrayInputStream
  extends ByteArrayInputStream
  {
    public FiveBytesByteArrayInputStream( byte[] bytes )
    {
      super( bytes );
    }
    
    /**
     * The behavior of not returning the number of bytes left in the inputstream is acceptable. 
     * See {@link http://java.sun.com/javase/6/docs/api/java/io/InputStream.html#available}.
     * "Note that while some implementations of InputStream will return the total number of bytes in the stream, 
     * many will not. It is never correct to use the return value of this method to allocate a buffer 
     * intended to hold all data in this stream."
     * I used for example a BlobArrayInputStream, that relied on this. 
     * @return Math.min( super.available(),5 );
     */
    @Override
    public synchronized int available() {
      return Math.min( super.available(),5 );
    }
  }
}
