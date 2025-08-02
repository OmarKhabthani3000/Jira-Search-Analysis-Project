package org.hibernate.test.session;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.service.ServiceRegistryBuilder;
import org.hibernate.service.internal.StandardServiceRegistryImpl;
import org.hibernate.testing.env.ConnectionProviderBuilder;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author alexeym
 */
public class TemporaryStatelessSessionTest extends BaseUnitTestCase {

    private StandardServiceRegistryImpl serviceRegistry = null;

    @Before
    public void setUp() {
        serviceRegistry = (StandardServiceRegistryImpl) new ServiceRegistryBuilder()
                .applySettings(ConnectionProviderBuilder.getConnectionProviderProperties())
                .buildServiceRegistry();
    }

    @After
    public void tearDown() {
        serviceRegistry.destroy();
    }

    @Test
    public void testSharedTransactionContextSessionClosing() {

        SessionFactory sessionFactory = buildSessionFactory(Country.class);

        Session session = sessionFactory.openSession();
        session.getTransaction().begin();

        StatelessSession temporary = sessionFactory.openStatelessSession(((SessionImplementor) session).connection());

        List countries = temporary.createCriteria(Country.class).list();

        Assert.assertTrue(countries.isEmpty());
        Assert.assertTrue(session.isOpen());

        //temporary.close();  // not closing temporary, as it should be handled by original session

        session.getTransaction().commit();
        session.close();                     //  HibernateException: proxy handle is no longer valid

        Assert.assertFalse(session.isOpen());

        sessionFactory.close();
    }

    private SessionFactory buildSessionFactory(Class... entities) {
        Configuration config = new Configuration();
        config.setProperty(Environment.HBM2DDL_AUTO, "create-drop");
        for(Class entity : entities) {
            config.addAnnotatedClass(entity);
        }
        return config.buildSessionFactory(serviceRegistry);
    }

    @Entity
    @Table(name = "tCountries")
    public static class Country {

        @Id
        @Column(name = "id")
        private Long id;

        @Column(name = "name")
        private String name;

        public Long getId() {
            return id;
        }

        public void setId(final Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(final String name) {
            this.name = name;
        }
    }

}
