package de.juplo.plugins.hibernate4;

import java.util.List;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.tool.schema.internal.SchemaCreatorImpl;
import org.junit.Test;

import de.juplo.plugins.hibernate4.Employee;

/**
 * @author Emmanuel Bernard
 */
public class ImplicitCompositeJoinTest{

    private static final Logger LOGGER = Logger.getLogger(ImplicitCompositeJoinTest.class);

    @Test
    public void testImplicitCompositeJoin() throws Exception {
        String expectedSql = "create table Employee " +
        		"(address varchar(255) not null" +
        		", age varchar(20) not null" +
        		", birthday varchar(255) not null" +
        		", name varchar(20) not null" +
        		", phone varchar(20) not null" +
        		", manager_address varchar(255)" +
        		", manager_age varchar(20)" +
        		", manager_birthday varchar(255)" +
        		", manager_name varchar(20)" +
        		", manager_phone varchar(20)" +
        		", primary key (address, age, birthday, name, phone))";
        StandardServiceRegistry ssr = new StandardServiceRegistryBuilder().applySetting(
                AvailableSettings.DIALECT,
                "org.hibernate.dialect.MySQLDialect").build();
        try {
            Metadata metadata = new MetadataSources(ssr)
                    .addAnnotatedClass(Employee.class)
                    .buildMetadata();

            boolean passed = false;

            List<String> commands = new SchemaCreatorImpl().generateCreationCommands(
                    metadata,
                    false);
            for (String command : commands) {
                LOGGER.info(command);
                if (expectedSql.equals(command)) {
                    passed = true;
                }
            }
            Assert
                    .assertTrue(
                            "Expected create table command for Employee entity not found",
                            passed);
        }
        finally {
            StandardServiceRegistryBuilder.destroy(ssr);
        }
    }
}
