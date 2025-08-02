package org.raju.yadav.entity;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;
import org.springframework.orm.jpa.vendor.Database;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = { "org.raju" })
@Import(EmbeddedDbConfig.class)
public class JpaTestConfig {

    @Inject
    DataSource dataSource;

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(false);
        vendorAdapter.setDatabase(Database.HSQL);
        vendorAdapter.setDatabasePlatform("org.hibernate.dialect.HSQLDialect");
        vendorAdapter.setShowSql(true);
        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setJpaVendorAdapter(vendorAdapter);
        factory.setPackagesToScan("org.raju.yadav");
        factory.setDataSource(dataSource);
        Properties jpaProperties = new Properties();
        jpaProperties.setProperty("org.hibernate.envers.audit_table_suffix", "_audit");
        jpaProperties.setProperty("org.hibernate.envers.revision_type_field_name", "revision_type");
        jpaProperties.setProperty("org.hibernate.envers.revision_field_name", "revision_info_id");
        jpaProperties.setProperty("org.hibernate.envers.store_data_at_delete", "true");
        jpaProperties.setProperty("org.hibernate.envers.do_not_audit_optimistic_locking_field", "false");
        factory.setJpaProperties(jpaProperties);
        return factory;
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        JpaTransactionManager txManager = new JpaTransactionManager();
        txManager.setEntityManagerFactory(entityManagerFactory().getObject());
        return txManager;
    }

    @Bean
    public EntityManager entityManager(EntityManagerFactory entityManagerFactory) {
        return entityManagerFactory.createEntityManager();
    }

}