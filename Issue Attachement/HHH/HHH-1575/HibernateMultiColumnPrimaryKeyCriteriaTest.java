package org.wfp.rita.datafacade;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

import junit.framework.TestCase;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.criterion.Restrictions;
import org.hibernate.impl.SessionFactoryImpl;
import org.hibernate.tool.hbm2ddl.SchemaExport;

/**
 * Multi-column primary keys don't work properly with Restrictions.in().
 * The query should be:
 *
 * <pre>
 * select this_.number as number0_0_, this_.street as street0_0_
 * from HibernateMultiColumnPrimaryKeyCriteriaTest$House this_
 * where (this_.number, this_.street)
 * in ((1, 'foobar street'), (10, 'whee street'))
 * </pre>
 *
 * but instead we get:
 * 
 * <pre>
 * select this_.number as number0_0_, this_.street as street0_0_
 * from HibernateMultiColumnPrimaryKeyCriteriaTest$House this_
 * where (this_.number, this_.street)
 * in ((1, 10), ('foobar street', 'whee street'))
 * </pre>
 * 
 * i.e. the parameters array is transposed.
 * 
 * @author chris
 */
public class HibernateMultiColumnPrimaryKeyCriteriaTest extends TestCase
{
    @Entity static class House
    {
        public static class Id implements Serializable
        {
            public String street;
            public Integer number;
            public Id() { }
            public Id(String street, int number)
            {
                this.street = street;
                this.number = number;
            }
            public Integer getNumber() { return number; }
        }
        
        @EmbeddedId public Id id;
        
        public House() { }
        public House(Id id) { this.id = id; }
    }

    public void testHibernateHavingAlias() throws Exception
    {
        AnnotationConfiguration conf = new AnnotationConfiguration();
        conf.addAnnotatedClass(House.class);
        conf.setProperty("hibernate.connection.driver_class",
            "com.mysql.jdbc.Driver");
        conf.setProperty("hibernate.connection.url",
            "jdbc:mysql://localhost/test");
        conf.setProperty("hibernate.connection.username", "root");
        conf.setProperty("hibernate.connection.password", "");
        conf.setProperty("hibernate.dialect",
            "org.hibernate.dialect.MySQLDialect");
        SessionFactory fact = conf.buildSessionFactory();
        
        SchemaExport exporter = new SchemaExport(conf,
            ((SessionFactoryImpl)fact).getSettings());
        exporter.setHaltOnError(true);
        exporter.create(true, true);

        for (Object e : exporter.getExceptions())
        {
            throw (Exception) e;
        }
        
        Session session = fact.openSession();
        
        List<House.Id> ids = new ArrayList<House.Id>();
        ids.add(new House.Id("foobar street", 1));
        ids.add(new House.Id("whee street", 10));
        
        List<House> saved = new ArrayList<House>();
        saved.add(new House(ids.get(0)));
        saved.add(new House(ids.get(1)));
        session.save(saved.get(0));
        session.save(saved.get(1));
        session.flush();
        
        // this one works properly
        List<House> queryResults = session.createCriteria(House.class).add(
            Restrictions.eq("id", ids.get(0))).list();
        assertEquals(1, queryResults.size());
        assertEquals(saved.get(0), queryResults.get(0));

        // and this one
        queryResults = session.createCriteria(House.class).add(
            Restrictions.eq("id", ids.get(1))).list();
        assertEquals(1, queryResults.size());
        assertEquals(saved.get(1), queryResults.get(0));

        // but not this, using Restrictions.in()
        queryResults = session.createCriteria(House.class).add(
            Restrictions.in("id", ids)).list();
        assertEquals(saved.size(), queryResults.size());
    }
    
    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(HibernateMultiColumnPrimaryKeyCriteriaTest.class);
    }
}
