package com.hes.cc;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.hibernate.boot.MetadataBuilder;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.BootstrapServiceRegistryBuilder;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.cfg.Environment;
import org.hibernate.tool.hbm2ddl.SchemaUpdate;
import org.junit.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import com.google.common.io.Files;

/**
 * @author Anton Shashok
 */


public class Hbm2ddlTest {
    @Test
    public void testName() throws Exception {
        EmbeddedDatabase dataSource = 
                new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .setName("test_db")
                .build();
        
        Map<String, Object> properties = new HashMap<>();
        properties.put(Environment.DATASOURCE, dataSource);
        properties.put(Environment.DIALECT, "org.hibernate.dialect.H2Dialect");
        properties.put(Environment.HBM2DDL_AUTO, "none");
        StandardServiceRegistryBuilder standardServiceRegistryBuilder = new StandardServiceRegistryBuilder(new BootstrapServiceRegistryBuilder().build());
        standardServiceRegistryBuilder.applySettings(properties);
        StandardServiceRegistry serviceRegistry = standardServiceRegistryBuilder.build();
        MetadataSources metadataSources = new MetadataSources(serviceRegistry);
        metadataSources.addAnnotatedClass(TestEntity.class);
        MetadataBuilder metadataBuilder = metadataSources.getMetadataBuilder(serviceRegistry );
        MetadataImplementor metadata = (MetadataImplementor)metadataBuilder.build();
        
        File folder = Files.createTempDir();
        File output = new File(folder, "/update_script.sql");

        SchemaUpdate su = new SchemaUpdate(serviceRegistry, metadata);
        su.setHaltOnError(true);
        su.setOutputFile(output.getAbsolutePath());
        su.setDelimiter(";");
        su.setFormat(true);
        su.execute(true, false);
    }
    
    @Entity
    public static class TestEntity {
        @Id
        private String field;
        
        public String getField() {
            return field;
        }
        
        public void setField(String field) {
            this.field = field;
        }
    }
}
