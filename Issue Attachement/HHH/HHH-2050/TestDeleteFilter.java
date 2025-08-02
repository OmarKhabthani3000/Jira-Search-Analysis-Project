import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Environment;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Table;
import javax.persistence.Version;
import java.util.List;

import junit.framework.TestCase;

public class TestDeleteFilter extends TestCase {

    private SessionFactory sessionFactory;
    private Session session;
    private Transaction transaction;

    public TestDeleteFilter() {
        AnnotationConfiguration tHibernateConfiguration = new AnnotationConfiguration();
        tHibernateConfiguration.setProperty(Environment.DRIVER, "com.mysql.jdbc.Driver")
        .setProperty(Environment.URL, "jdbc:mysql://127.0.0.1/test")
        .setProperty(Environment.USER, "test")
        .setProperty(Environment.PASS, "test")
        .setProperty(Environment.DIALECT, "org.hibernate.dialect.MySQLInnoDBDialect")
        .setProperty(Environment.HBM2DDL_AUTO, "auto")
        .setProperty(Environment.SHOW_SQL, "true");
        tHibernateConfiguration.addAnnotatedClass(FilteredContent.class);
        tHibernateConfiguration.addAnnotatedClass(FilteredContainer.class);

        sessionFactory = tHibernateConfiguration.buildSessionFactory();
    }


    protected void setUp() throws Exception {
        session = sessionFactory.openSession();
        transaction = session.beginTransaction();
    }

    protected void tearDown() throws Exception {
        transaction.rollback();
        session.close();
    }

    private void checkQuery(String pQuery, int pResultSize) {
        List tResult = session.createQuery(pQuery).list();
        if (tResult.size() != pResultSize) {
            System.out.println("FAILED: " + pQuery);
        }
    }

    public void testQuery() {
        session.enableFilter("onlyAvailable");

        // prepare a container
        FilteredContainer tFilteredContainer = new FilteredContainer();
        session.save(tFilteredContainer);

        // prepare a content, add it to the container
        FilteredContent tFilteredContent = new FilteredContent();
        tFilteredContent.container = tFilteredContainer;
        session.save(tFilteredContent);

        // expect to return no result if one of the pairs is marked as deleted
        // these two queries should be equivalent
        // i use oid only as select to have leaner sql queries
        String tExplicitQuery = "select fc.oid from FilteredContent fr join fr.container as fc";
        String tImplicitQuery = "select fc.oid from FilteredContent fr, FilteredContainer fc where fc=fr.container";

        // in the explicit query the filter for container is not reflected in the sql:
        //  select testdelete1_.oid from TaFilteredContent testdelete0_ inner join TaFilteredContainer testdelete1_ on testdelete0_.container_oid=testdelete1_.oid where testdelete0_.deleted=0
        // this leads to an inconsitency of the two queries

        checkQuery(tExplicitQuery, 1);
        checkQuery(tImplicitQuery, 1);

        tFilteredContent.deleted = true;
        tFilteredContainer.deleted = false;
        checkQuery(tExplicitQuery, 0);
        checkQuery(tImplicitQuery, 0);

        // here the explicid query fails, but the implicit is ok
        tFilteredContent.deleted = false;
        tFilteredContainer.deleted = true;
        checkQuery(tExplicitQuery, 0);
        checkQuery(tImplicitQuery, 0);

        tFilteredContent.deleted = true;
        tFilteredContainer.deleted = true;
        checkQuery(tExplicitQuery, 0);
        checkQuery(tImplicitQuery, 0);
    }

    @MappedSuperclass
    public static class PersistableObject {

        @Id
        @GeneratedValue
        long oid;

        @Version
        @Column(name = "vers", nullable = false)
        int version;

        boolean deleted;
    }


    @Entity(name = "FilteredContainer")
    @Table(name = "TaFilteredContainer")
    @FilterDef(name = "onlyAvailable")
    @Filter(name = "onlyAvailable", condition = "deleted=0")
    public static class FilteredContainer extends PersistableObject {
    }

    @Entity(name = "FilteredContent")
    @Table(name = "TaFilteredContent")
    @FilterDef(name = "onlyAvailable")
    @Filter(name = "onlyAvailable", condition = "deleted=0")
    public static class FilteredContent extends PersistableObject {

        @ManyToOne(targetEntity = FilteredContainer.class)
        FilteredContainer container;
    }
} 
