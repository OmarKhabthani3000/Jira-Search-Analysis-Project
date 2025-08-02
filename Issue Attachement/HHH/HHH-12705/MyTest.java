import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.Before;
import org.junit.Test;

public class MyTest {

    private SessionFactory sf;

    @Before
    public void setup() {
        StandardServiceRegistryBuilder srb = new StandardServiceRegistryBuilder()
                // Add in any settings that are specific to your test. See resources/hibernate.properties for the defaults.
                .applySetting( "hibernate.show_sql", "true" )
                .applySetting( "hibernate.format_sql", "true" )
                .applySetting( "hibernate.hbm2ddl.auto", "update" );

        Metadata metadata = new MetadataSources( srb.build() )
                // Add your entities here.
                .addAnnotatedClass( Lead.class )
                .buildMetadata();

        sf = metadata.buildSessionFactory();
    }

    @Test
    public void myTest() throws Exception {
        Session s = sf.openSession();

        Lead l = new Lead();

        s.saveOrUpdate(l);
    }
}