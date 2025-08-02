//////////////////////////////////////////////////////////////////////////////////////////////////
//PutGetClobs is an example application adapted from 
//http://publib.boulder.ibm.com/infocenter/systems/scope/i5os/index.jsp?topic=/rzaha/adwrclob.htm
//that shows the following Unicode handling bug:
//
//When a string like "Granpré Molière†; 0123456789" is stored in a CLOB
//column CCSID 1208 (UTF8) and read back, the string is actually stored 
// as "Granpré Molière†; 012345".
//So its truncated and the "6789" part is not stored.
//
//The cause is that this string has THREE diacritical marks in it.
//The é and è are coded in two bytes in UTF8 and the † in three bytes.
//The CLOB truncation occurs when characters are used that are UTF8 coded in
//MORE THAN 1 BYTE. It seems the driver is counting bytes instead of characters 
//when calculating CLOB size to store.
//
//It does not happen with a simple SQL update statement. That works OK.
//Also PreparedStatement.setString works OK.
//Yhe bug occurs when PreparedStatement.setCharacterStream is used.
//
//////////////////////////////////////////////////////////////////////////////////////////////////
import java.sql.*;

	public class PutGetClobs {
	   public static void main(String[] args) 
	   throws SQLException 
	   {
	       // Register the native JDBC driver.
	       try {
	          Class.forName("com.ibm.as400.access.AS400JDBCDriver");
	      } catch (Exception e) {
	          System.exit(1);  // Setup error.
	      }
	          
	      // Establish a Connection and Statement with which to work.
	      Connection c = DriverManager.getConnection("jdbc:as400://fuji/CIVMKTEST;user=makelaar;password=makelaar;transaction isolation=read committed");
	      Statement s = c.createStatement();
	      
	      // Clean up any previous run of this application.
	      try {
	          s.executeUpdate("DROP TABLE CLOBTABLE");
	      } catch (SQLException e) {
	          // Ignore it - assume the table did not exist.
	      }

	      // Create a table with a CLOB column. The default CLOB column
	      // size is 1 MB.
	      s.executeUpdate("CREATE TABLE CLOBTABLE (COL1 CLOB CCSID 1208)");

	      // Create a PreparedStatement object that allow you to put
	      // a new Clob object into the database.
	      PreparedStatement ps = c.prepareStatement("INSERT INTO CLOBTABLE VALUES(?)");
          
	      String demostring = "Granpré Molière†; 0123456789";
	      java.io.StringReader reader = new java.io.StringReader(demostring);
	      ps.setCharacterStream(1, reader, demostring.length());
	      // ps.setString(1, clobValue); //this works OK

	      // Process the statement, inserting the clob into the database.
	      ps.executeUpdate();

	      // Process a query and get the CLOB that was just inserted out of the 
	      // database as a Clob object.
	      ResultSet rs = s.executeQuery("SELECT * FROM CLOBTABLE");
	      rs.next();
	      Clob clob = rs.getClob(1);
	      String txt = clob.getSubString(1,(int)clob.length());
          System.out.println(txt);
          // With the current jt400.jar this will output Granpré Molière†; 012345
	      
	      // Clean up.
	      try {
	          s.executeUpdate("DROP TABLE CLOBTABLE");
	      } catch (SQLException e) {
	          // Ignore it 
	      }
	      c.close(); // Connection close also closes stmt and rs.
	   }
	}
