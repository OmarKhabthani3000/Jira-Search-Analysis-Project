package com.dcubedev.config;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;

import org.hibernate.cfg.Environment;
import org.hibernate.ogm.cfg.OgmProperties;
import org.hibernate.ogm.jpa.HibernateOgmPersistence;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@ComponentScan("com.dcubedev.neo4j")
@EnableJpaRepositories("com.dcubedev.neo4j")
public class JpaNeo4jPersistenceContext {

	private Properties hibProperties() {
		Properties properties = new Properties();
		properties.put( Environment.HBM2DDL_AUTO, "none" );
		properties.put( OgmProperties.DATASTORE_PROVIDER, "neo4j_http");
		properties.put( OgmProperties.HOST, "localhost:7474" );
		properties.put( OgmProperties.USERNAME, "neo4j" );
		properties.put( OgmProperties.PASSWORD, "demo2017" );
		//properties.put( Neo4jProperties.DATABASE_PATH, "C:/neo4j/database/tutorial" );
		return properties;
	}
	
	@Bean(name="entityManagerFactoryJpaNeo4jLocal")
	public LocalContainerEntityManagerFactoryBean entityManagerFactoryJpaNeo4jLocal() {
		String persistenceUnitName = "60Capital-neo4j-ejbPU";

		LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
		factory.setJpaVendorAdapter( jpaVendorAdapter() );
		factory.setPackagesToScan( "com.dcubedev.neo4j" );
		factory.setPersistenceUnitName( persistenceUnitName );
		factory.setJpaProperties( hibProperties() );
		factory.setPersistenceProviderClass( HibernateOgmPersistence.class );
		return factory;
	}
	
	@Bean(name="entityManagerFactoryJpaNeo4j")
	//@Bean
	public EntityManagerFactory entityManagerFactoryJpaNeo4j() {
		return entityManagerFactoryJpaNeo4jLocal().getObject();
	}
	
	@Bean(name="jpaVendorAdapterJpaNeo4j")
	public JpaVendorAdapter jpaVendorAdapter() {
		HibernateJpaVendorAdapter bean = new HibernateJpaVendorAdapter();
		bean.setGenerateDdl( true );
		return bean;
	}
	
	/*@Bean(name="transactionManagerJpaNeo4j")
	public JpaTransactionManager transactionManager(EntityManagerFactory emf) {
		return new JpaTransactionManager( emf );
	}*/

	@Bean(name="transactionManagerJpaNeo4j")
	public PlatformTransactionManager transactionManager() {

		JpaTransactionManager txManager = new JpaTransactionManager();
		//txManager.setEntityManagerFactory(entityManagerFactoryJpaNeo4j().getObject());
		txManager.setEntityManagerFactory(entityManagerFactoryJpaNeo4j());
		return txManager;
	}
	
}
