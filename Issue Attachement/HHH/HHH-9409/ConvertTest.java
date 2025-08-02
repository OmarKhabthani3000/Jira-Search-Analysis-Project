import java.util.HashSet;
import java.util.Set;

import javax.persistence.AttributeConverter;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Converter;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.Before;
import org.junit.Test;

public class ConvertTest {

    private SessionFactory sessionFactory;
    
    @Before
    public void setup() {
        Configuration cfg = new Configuration()
            .addAnnotatedClass(Customer.class)
            .addAnnotatedClass(ColorType.class)
            .setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect")
            .setProperty("hibernate.connection.driver_clas", "com.mysql.jdbc.Driver")
            .setProperty("hibernate.connection.url", "jdbc:mysql://localhost/test_converter")
            .setProperty("hibernate.connection.username", "root")
            .setProperty("hibernate.connection.password", "pass")
            .setProperty("hibernate.hbm2ddl.auto", "create");
        cfg.addAttributeConverter(ColorTypeConverter.class);
        sessionFactory = cfg.buildSessionFactory();
    }
    
    @Test
    public void test() {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();

            Customer customer = new Customer(1);
            customer.colors.add(ColorType.BLUE);
            session.save(customer);

            session.getTransaction().commit();
        } catch (RuntimeException e) {
            if (session != null) {
                session.getTransaction().rollback();
            }
            throw e;
        }
    }
    
    
    @Entity(name = "Customer")
    @Table(name = "CUST")
    public static class Customer {
        @Id
        private Integer id;
        @ElementCollection(fetch = FetchType.EAGER)
        @CollectionTable(name = "cust_color", joinColumns = @JoinColumn(name = "cust_fk", nullable = false), uniqueConstraints = @UniqueConstraint(columnNames = {
                "cust_fk", "color" }))
        @Column(name = "color", nullable = false)
        @Convert(converter=ColorTypeConverter.class)
        private Set<ColorType> colors = new HashSet<ColorType>();

        public Customer() {
        }

        public Customer(Integer id) {
            this.id = id;
        }
    }

    // an enum-like class (converters are technically not allowed to apply to
    // enums)
    public static class ColorType {
        public static ColorType BLUE = new ColorType("blue");
        public static ColorType RED = new ColorType("red");
        public static ColorType YELLOW = new ColorType("yellow");
        private final String color;

        public ColorType(String color) {
            this.color = color;
        }

        public String toExternalForm() {
            return color;
        }

        public static ColorType fromExternalForm(String color) {
            if (BLUE.color.equals(color)) {
                return BLUE;
            } else if (RED.color.equals(color)) {
                return RED;
            } else if (YELLOW.color.equals(color)) {
                return YELLOW;
            } else {
                throw new RuntimeException("Unknown color : " + color);
            }
        }
    }

    @Converter(autoApply = false)
    public static class ColorTypeConverter implements
            AttributeConverter<ColorType, String> {
        @Override
        public String convertToDatabaseColumn(ColorType attribute) {
            return attribute == null ? null : attribute.toExternalForm();
        }

        @Override
        public ColorType convertToEntityAttribute(String dbData) {
            return ColorType.fromExternalForm(dbData);
        }
    }
}
