package org.wfp.rita.datafacade;

import java.util.Set;

import junit.framework.TestCase;

import org.hibernate.MappingException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;

/**
 * Hibernate produces error messages like:
 * 
 * org.hibernate.MappingException: property-ref [projectSite] not found
 *   on entity [org.wfp.rita.dao.ProjectSite]
 * at org.hibernate.mapping.PersistentClass.getReferencedProperty
 *   (PersistentClass.java:362)
 * at org.hibernate.mapping.ManyToOne.createPropertyRefConstraints
 *   (ManyToOne.java:70)
 * at org.hibernate.cfg.HbmBinder$ManyToOneSecondPass.doSecondPass
 *   (HbmBinder.java:2733)
 * 
 * which don't identify the source of the errant property reference.
 *
 * @author chris
 */
public class HibernatePropertyRefNotFoundErrorMessageTest extends TestCase
{
    private static class House
    {
        public Integer id;
        public Person owner;
    }

    String houseXml = "<?xml version='1.0' encoding='utf-8'?>\n" +
    "<!DOCTYPE hibernate-mapping PUBLIC " + 
    "'-//Hibernate/Hibernate Mapping DTD 3.0//EN' " +
    "'http://hibernate.sourceforge.net/hibernate-mapping-3.0.dtd'>\n" +
    "<hibernate-mapping>\n" +
    "<class name='org.wfp.rita.datafacade.HibernatePropertyRefNotFoundErrorMessageTest$House' " +
    "table='house'>\n" +
    "<id name='id' type='java.lang.Integer' access='field'>\n" +
    "<column name='id' />\n" +
    "<generator class='increment' />\n" +
    "</id>\n" +
    "<many-to-one name='owner' class='org.wfp.rita.datafacade.HibernatePropertyRefNotFoundErrorMessageTest$Person' " +
    "fetch='select' access='field' property-ref='ssn'>\n" +
    "<column name='owner_id' />\n" +
    "</many-to-one>\n" +
    "</class>\n" +
    "</hibernate-mapping>";

    private static class Person
    {
        public Integer id;
        public Set<House> houses;
    }

    String personXml = "<?xml version='1.0' encoding='utf-8'?>\n" +
        "<!DOCTYPE hibernate-mapping PUBLIC " + 
        "'-//Hibernate/Hibernate Mapping DTD 3.0//EN' " +
        "'http://hibernate.sourceforge.net/hibernate-mapping-3.0.dtd'>\n" +
        "<hibernate-mapping>\n" +
        "<class name='org.wfp.rita.datafacade.HibernatePropertyRefNotFoundErrorMessageTest$Person' " +
        "table='person'>\n" +
        "<id name='id' type='java.lang.Integer' access='field'>\n" +
        "<column name='id' />\n" +
        "<generator class='increment' />\n" +
        "</id>\n" +
        "<set name='houses' inverse='true' access='field'>\n" +
        "<key><column name='owner' not-null='true' /></key>\n" +
        "<one-to-many class='org.wfp.rita.datafacade.HibernatePropertyRefNotFoundErrorMessageTest$House' />\n" +
        "</set>\n" +
        "</class>\n" +
        "</hibernate-mapping>";

    public void testHibernateHavingAlias() throws Exception
    {
        AnnotationConfiguration conf = new AnnotationConfiguration();
        conf.addXML(houseXml);
        conf.addXML(personXml);
        conf.setProperty("hibernate.connection.driver_class",
            "com.mysql.jdbc.Driver");
        conf.setProperty("hibernate.connection.url",
            "jdbc:mysql://localhost/test");
        conf.setProperty("hibernate.connection.username", "root");
        conf.setProperty("hibernate.connection.password", "");
        conf.setProperty("hibernate.dialect",
            "org.hibernate.dialect.MySQLDialect");
        
        MappingException expected = null;
        
        try
        {
            SessionFactory fact = conf.buildSessionFactory();
        }
        catch (MappingException e)
        {
            expected = e;
        }
        
        assertNotNull(expected);
        assertEquals("Association property [owner] of " +
            "[org.wfp.rita.datafacade." +
            "HibernatePropertyRefNotFoundErrorMessageTest$House] " +
            "references unknown property [ssn] of " +
            "[org.wfp.rita.datafacade." +
            "HibernatePropertyRefNotFoundErrorMessageTest$Person]",
            expected.getMessage());
    }
    
    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(HibernatePropertyRefNotFoundErrorMessageTest.class);
    }
}
