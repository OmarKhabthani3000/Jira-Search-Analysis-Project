import java.io.StringReader;

import org.hibernate.tool.hbm2ddl.MultipleLinesSqlCommandExtractor;
import org.junit.Test;

public class MultipleLinesSqlCommandExtractorAndWhiteSpace {
    
    private static final String SQL = "insert into users (name,\n password) values\n ('root', 'secret');";
    
    @Test // ok
    public void sqlCommandExtractor() {
        new MultipleLinesSqlCommandExtractor().extractCommands(new StringReader(SQL));
    }
    
    @Test // error
    public void sqlCommandExtractorWithTrailingSpace() {
        new MultipleLinesSqlCommandExtractor().extractCommands(new StringReader(SQL + " "));
    }
    
    @Test // error
    public void sqlCommandExtractorWithTrailingNewLine() {
        new MultipleLinesSqlCommandExtractor().extractCommands(new StringReader(SQL + "\n"));
    }
}