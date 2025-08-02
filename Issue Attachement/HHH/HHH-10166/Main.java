import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

public class Main {
    private static final SessionFactory ourSessionFactory;
    private static final ServiceRegistry serviceRegistry;

    static {
        try {
            Configuration configuration = new Configuration();
            configuration.configure();
            configuration.addAnnotatedClass(TestEntity.class);
            serviceRegistry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
            ourSessionFactory = configuration.buildSessionFactory(serviceRegistry);
        } catch (Throwable ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static Session getSession() throws HibernateException {
        return ourSessionFactory.openSession();
    }

    public static void main(final String[] args) throws Exception {
        final Session session = getSession();
        try {
            for (int i = 0; i < 100; i++) {
                TestEntity entity = new TestEntity(UUID.randomUUID().toString());
                session.save(entity);
                System.out.println(entity.id);
            }
            session.flush();
        } finally {
            session.close();
        }
    }

    @Entity
    @Table(name = "test_entity")
    private static class TestEntity {
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "test_entity_id_seq")
        @SequenceGenerator(name = "test_entity_id_seq", sequenceName = "test_entity_id_seq")
        private Long id;

        @Column(name = "random_string")
        private String randomString;

        public TestEntity() {
        }

        public TestEntity(String s) {
            randomString = s;
        }
    }
}
