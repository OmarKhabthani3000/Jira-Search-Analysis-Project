import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

import org.jboss.util.file.ArchiveBrowser;

public class SeparatorTest {

    public static void main(final String[] args) {
        try {
            URL jar = new URL("file:///");
            Iterator it = ArchiveBrowser.getBrowser(jar, new ArchiveBrowser.Filter() {
                public boolean accept(final String filename) {
                    System.out.println(filename);
                    return false;
                }
            });
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
