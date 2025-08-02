package fr.bdf.ceph.services.history;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PersistenceContext;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.sql.DataSource;

import lombok.Data;

import org.dbunit.ext.h2.H2DataTypeFactory;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.Audited;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.hibernate4.HibernateExceptionTranslator;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.support.PersistenceAnnotationBeanPostProcessor;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.github.springtestdbunit.bean.DatabaseConfigBean;
import com.github.springtestdbunit.bean.DatabaseDataSourceConnectionFactoryBean;
import com.google.common.collect.Maps;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class Hibernate4_3_11IT {

    @Configuration
    public static class ContextConfig {
        @Bean
        public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
            LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
            entityManagerFactoryBean.setDataSource(dataSource());
            entityManagerFactoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
            entityManagerFactoryBean.setPersistenceProvider(new HibernatePersistenceProvider());
            entityManagerFactoryBean.setPackagesToScan("fr.bdf.ceph");
            entityManagerFactoryBean.setJpaProperties(jpaProperties());
            return entityManagerFactoryBean;
        }

        @Bean
        public Properties jpaProperties() {
            Properties jpaProperties = new Properties();
            jpaProperties.put("hibernate.dialect", "org.hibernate.dialect.Oracle10gDialect");
            jpaProperties.put("hibernate.hbm2ddl.auto", "create");
            jpaProperties.put("hibernate.show_sql", "false");
            jpaProperties.put("hibernate.connection.release_mode", "after_transaction");
            return jpaProperties;
        }

        @Bean
        public DataSource dataSource() {
            EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
            builder.setType(EmbeddedDatabaseType.H2);
            return builder.build();
        }

        @Bean
        public DatabaseConfigBean dbUnitDatabaseConfig() {
            DatabaseConfigBean configuration = new DatabaseConfigBean();
            configuration.setDatatypeFactory(new H2DataTypeFactory());
            return configuration;
        }

        @Bean
        public DatabaseDataSourceConnectionFactoryBean dbUnitDatabaseConnection() {
            DatabaseDataSourceConnectionFactoryBean connectionFactory = new DatabaseDataSourceConnectionFactoryBean();
            connectionFactory.setDatabaseConfig(dbUnitDatabaseConfig());
            connectionFactory.setDataSource(dataSource());
            return connectionFactory;
        }

        @Bean
        public HibernateExceptionTranslator hibernateExceptionTranslator() {
            return new HibernateExceptionTranslator();
        }

        @Bean
        public PersistenceExceptionTranslationPostProcessor persistenceExceptionTranslationPostProcessor() {
            return new PersistenceExceptionTranslationPostProcessor();
        }

        @Bean
        public PersistenceAnnotationBeanPostProcessor persistenceAnnotationBeanPostProcessor() {
            return new PersistenceAnnotationBeanPostProcessor();
        }

        @Bean
        @Primary
        public JpaTransactionManager transactionManager() {
            JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
            jpaTransactionManager.setEntityManagerFactory(entityManagerFactory().getObject());
            return jpaTransactionManager;
        }

        @Bean
        public TransactionTemplate transactionTemplate() {
            TransactionTemplate transactionTemplate = new TransactionTemplate();
            transactionTemplate.setTransactionManager(transactionManager());
            transactionTemplate.setTimeout(60);
            return transactionTemplate;
        }

        @Bean
        public JdbcTemplate jdbcTemplate() {
            return new JdbcTemplate(dataSource());
        }
    }

    @Entity(name = "PARENT")
    @Data
    @Audited
    public static class ComplexParentEntity {
        @Id
        private Long id;

        @OneToOne
        private ChildEntity child;

        @OneToMany
        private List<ChildDiscrepancy> discrepancyList;

        @Temporal(TemporalType.TIME)
        private Date issueDate;

    }

    @Entity(name = "CHILD")
    @Data
    @Audited
    public static class ChildEntity {
        @Id
        private Long id;
        private String property;
    }

    @Entity(name = "CHILDDISCREPANCY")
    @Data
    @Audited
    public static class ChildDiscrepancy {
        @Id
        private Long id;
    }

    @Autowired
    private TransactionTemplate transactionTemplate;
    @PersistenceContext
    private EntityManager em;

    @Test
    @Transactional
    public void should_create_only_one_revision() {
        transactionTemplate.setPropagationBehavior(Propagation.REQUIRES_NEW.value());

        final Map<String, ComplexParentEntity> parents = transactionTemplate
                .execute(new TransactionCallback<Map<String, ComplexParentEntity>>() {
                    @Override
                    public Map<String, ComplexParentEntity> doInTransaction(TransactionStatus status) {
                        Map<String, ComplexParentEntity> parents = Maps.newHashMap();

                        final ComplexParentEntity parent = new ComplexParentEntity();
                        parent.setId(1L);
                        parent.setIssueDate(new Date());

                        final ChildEntity child = new ChildEntity();
                        child.setId(1L);
                        child.setProperty("value1");
                        parent.setChild(child);

                        em.persist(child);
                        em.persist(parent);
                        parents.put("ISIN1", parent);

                        return parents;
                    }
                });

        ComplexParentEntity parent = em.merge(parents.get("ISIN1"));
        em.refresh(parent);

        @SuppressWarnings("unchecked")
        List<Object[]> revisionsAfterInsert = AuditReaderFactory
                .get(em)
                .createQuery()
                .forRevisionsOfEntity(ComplexParentEntity.class, false, false)
                .getResultList();
        assertThat(revisionsAfterInsert).hasSize(1);

        final Map<String, ComplexParentEntity> updatedParents = transactionTemplate
                .execute(new TransactionCallback<Map<String, ComplexParentEntity>>() {
                    @Override
                    public Map<String, ComplexParentEntity> doInTransaction(TransactionStatus status) {
                        Map<String, ComplexParentEntity> updatedParents = Maps.newHashMap();
                        ComplexParentEntity parent = em.merge(parents.get("ISIN1"));

                        ChildEntity child = parent.getChild();
                        child.setProperty("value2");
                        child = em.merge(child);
                        parent.setChild(child);
                        updatedParents.put("ISIN1", parent);

                        return updatedParents;
                    }
                });

        @SuppressWarnings("unchecked")
        List<Object[]> revisionsAfterChildUpdate = AuditReaderFactory
                .get(em)
                .createQuery()
                .forRevisionsOfEntity(ComplexParentEntity.class, false, false)
                .getResultList();
        assertThat(revisionsAfterChildUpdate).hasSize(1);

    }
}
