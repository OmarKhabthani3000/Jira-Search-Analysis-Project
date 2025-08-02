package org.wfp.rita.datafacade;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import junit.framework.TestCase;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.impl.SessionFactoryImpl;
import org.hibernate.tool.hbm2ddl.SchemaExport;

@Entity class House
{
    @Id public int id;
    public String name;
    @OneToMany(mappedBy="house")
    public Set<Cat> cats;
}

@Entity class Cat
{
    @Id public int id;
    @ManyToOne
    public House house;
    public String name;
    public int weight;
    public String sex;
}

public class HibernateHavingAliasTest extends TestCase
{
    public void testHibernateHavingAlias() throws Exception
    {
        AnnotationConfiguration conf = new AnnotationConfiguration();
        conf.addAnnotatedClass(House.class);
        conf.addAnnotatedClass(Cat.class);
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
        session.createQuery("SELECT house.id, " +
                "       SUM(CASE cat.sex WHEN 'M' " +
                "           THEN 1 ELSE 0 END) " +
                "           AS num_male, " +
                "       SUM(CASE cat.sex WHEN 'F' " +
                "           THEN 1 ELSE 0 END) " +
                "           AS num_female " +
                "FROM   House AS house " +
                "JOIN   house.cats AS cat " +
                "GROUP BY house.id " +
                "HAVING num_male <> num_female").list();
    }
    
    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(HibernateHavingAliasTest.class);
    }
}
