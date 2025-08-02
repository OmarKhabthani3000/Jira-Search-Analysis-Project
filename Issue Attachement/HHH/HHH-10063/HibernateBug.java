package com.liberologico.cloudesire.cmw.test.unit;

import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.PostgreSQL9Dialect;
import org.hibernate.tool.hbm2ddl.SchemaUpdate;
import org.hibernate.tool.hbm2ddl.SchemaValidator;
import org.junit.Test;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

public class HibernateBug
{
    @Entity
    public static class TestEntity {
        @Id
        @GeneratedValue (strategy = GenerationType.AUTO)
        private int id;
    }

    @Test
    public void test() {
        Configuration configuration = new Configuration().addAnnotatedClass(TestEntity.class);
        configuration.setProperty( AvailableSettings.GLOBALLY_QUOTED_IDENTIFIERS, Boolean.TRUE.toString());
        configuration.setProperty( AvailableSettings.DIALECT, PostgreSQL9Dialect.class.getName() );
        configuration.setProperty(AvailableSettings.URL, "jdbc:postgresql://localhost/test");
        configuration.setProperty(AvailableSettings.USER, "postgres");
        configuration.setProperty(AvailableSettings.PASS, "admin");
        new SchemaUpdate(configuration).execute(false, true);
        new SchemaValidator(configuration).validate();
    }
}
