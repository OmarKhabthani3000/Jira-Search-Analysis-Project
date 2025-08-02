import org.hibernate.dialect.HSQLDialect;

/**
 * Tests the HSQLDialect class for correct handling of schemas for 
 * different versions of HSQLDB.
 * Run the main() method with different versions of HSQLDB in your
 * classpath.
 * <P>
 * CLASSPATH SETUP.  Classpath must contain
 * <OL>
 *  <LI>Patched version of HSQLDialect.  Must be in classpath <I>before</I>
 *      the version in your Hibernate distro
 *      (unless the patched version is already in the distro).
 *      If it's not jarred up, then the classpath element will be the
 *      ancestor directory which contains
 *      <CODE>org/hibernate/dialect/HSQLDialect.class</CODE>.
 *  <LI> Target HSQLDB distro (typically via hsqldb.jar).
 *  <LI> Hibernate distro (typically hibernate3.jar) (unless the first item
 *       above contains your Hibernate distro).
 *  <LI> Commons logging library (like commons-logging-1.0.4.jar).
 * 
 * <HR>
 * Example:
 * <BR>
 * <CODE>java -cp .:/path/to/hibernate3.jar:/path/to/hsqldb.jar:/path/to/commons-logging-1.0.4.jar TestHSQLDialectPatch</CODE>
 * <BR>
 * where your current directory contains TestHSQLDialectPatch.class.
 */
public class TestHSQLDialectPatch {
    static public void main(String[] sa) {
        if (sa.length > 0) {
            System.err.println("This program takes no arguments");
        } else {
            System.out.println(
                    "The goal is that the QuerySequencesString should use the "
                    + "'information_schema'\nschema ONLY for post-1.8 versions "
                    + "of HSQLDB.\n\n"
                    + "QuerySequencesString reported as ("
                    + (new HSQLDialect()).getQuerySequencesString() + ')');
            // DO NOT RUN FOLLOWING LINE UNTIL AFTER we have run desired
            // Hibernate methods.  Reason is, we want to test before 
            // implicitly running any HSQLDB initializers.
            System.out.println("Tested version of HSQLDB:  "
                    + org.hsqldb.Library.getDatabaseProductVersion());
        }
    }
}
