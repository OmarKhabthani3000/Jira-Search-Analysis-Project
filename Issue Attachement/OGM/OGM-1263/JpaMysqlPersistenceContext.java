package com.dcubedev.config;

import java.util.Properties;

import javax.annotation.Resource;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

//@Configuration
//@ComponentScan("com.dcubedev.mysql")
//@EnableJpaRepositories("com.dcubedev.mysql")
public class JpaMysqlPersistenceContext {
	private static final String PROPERTY_NAME_DATABASE_DRIVER = "db.driver";
    private static final String PROPERTY_NAME_DATABASE_PASSWORD = "db.password";
    private static final String PROPERTY_NAME_DATABASE_URL = "db.url";
    private static final String PROPERTY_NAME_DATABASE_USERNAME = "db.username";
 
    private static final String PROPERTY_NAME_HIBERNATE_DIALECT = "hibernate.dialect";
    private static final String PROPERTY_NAME_HIBERNATE_SHOW_SQL = "hibernate.show_sql";
    private static final String PROPERTY_NAME_ENTITYMANAGER_PACKAGES_TO_SCAN = "entitymanager.packages.to.scan";
    
    @Resource
    private Environment env;
    
	private Properties hibProperties() {
        Properties properties = new Properties();
        properties.put(PROPERTY_NAME_HIBERNATE_DIALECT, "org.hibernate.dialect.MySQL57InnoDBDialect");
        properties.put(PROPERTY_NAME_HIBERNATE_SHOW_SQL, "false");
        //properties.put(PROPERTY_NAME_HIBERNATE_DIALECT, env.getRequiredProperty(PROPERTY_NAME_HIBERNATE_DIALECT));
        //properties.put(PROPERTY_NAME_HIBERNATE_SHOW_SQL, env.getRequiredProperty(PROPERTY_NAME_HIBERNATE_SHOW_SQL));
        return properties;
    }
    
	@Bean(name="dataSourceJpaMysql")
	public DataSource dataSource() {
    	System.out.println("JpaMysqlPersistenceContext::dataSource() Environment env: " + env);
    	// capital60
    	// matalan
    	// mysql:mysql-connector-java:jar
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
 
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/capital60");
        dataSource.setUsername("stephen");
        dataSource.setPassword("demo2016");
 
        return dataSource;
    }
    
	@Bean(name="entityManagerFactoryJpaMysqlLocal")
	public LocalContainerEntityManagerFactoryBean entityManagerFactoryJpaMysqlLocal() {
		//String persistenceXmlLocation = "C:/workspace/dcube-groups/dcube-groups/src/main/resources/persistence.xml";
		//String persistenceXmlLocation = "persistence.xml";
		String persistenceUnitName = "60Capital-ejbPU";
		//System.out.println("JpaMysqlPersistenceContext::entityManagerFactoryLocalJpaMysql() ... " );
		//System.out.println("JpaMysqlPersistenceContext::entityManagerFactoryLocalJpaMysql() Environment env: " + env);

		HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		//System.out.println("JpaMysqlPersistenceContext::entityManagerFactoryLocalJpaMysql() vendorAdapter: " + vendorAdapter);
		vendorAdapter.setGenerateDdl(true);

		LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
		//System.out.println("JpaMysqlPersistenceContext::entityManagerFactoryLocalJpaMysql() LocalContainerEntityManagerFactoryBean factory: " + factory);
		factory.setJpaVendorAdapter(vendorAdapter);
		factory.setPackagesToScan("com.dcubedev.mysql");
		factory.setDataSource(dataSource());
		//factory.setPersistenceXmlLocation(persistenceXmlLocation);
		factory.setPersistenceUnitName(persistenceUnitName);
		
		factory.setJpaProperties(hibProperties());
		
		//System.out.println("JpaMysqlPersistenceContext::entityManagerFactoryLocalJpaMysql() factory.getObject(): " + factory.getObject());
		return factory;
	}

	@Bean(name="entityManagerFactory")
	public EntityManagerFactory entityManagerFactory() {
		return entityManagerFactoryJpaMysqlLocal().getObject();
	}
	
	@Bean(name="jpaVendorAdapterJpaMysql")
	public JpaVendorAdapter jpaVendorAdapter() {
		HibernateJpaVendorAdapter bean = new HibernateJpaVendorAdapter();
		bean.setGenerateDdl( true );
		return bean;
	}

	/*@Bean(name="transactionManagerJpaMysql")
	public JpaTransactionManager transactionManager(EntityManagerFactory emf) {
		return new JpaTransactionManager( emf );
	}*/

	@Bean(name="transactionManagerJpaMysql")
	public PlatformTransactionManager transactionManager() {

		JpaTransactionManager txManager = new JpaTransactionManager();
		//txManager.setEntityManagerFactory(entityManagerFactory().getObject());
		txManager.setEntityManagerFactory(entityManagerFactory());
		return txManager;
	}
	
	
	//=====================================================================
	
	/*private Properties hibProperties() {
        Properties properties = new Properties();
        //properties.put(PROPERTY_NAME_HIBERNATE_DIALECT, "org.hibernate.dialect.MySQL57InnoDBDialect");
        properties.put(PROPERTY_NAME_HIBERNATE_DIALECT, env.getRequiredProperty(PROPERTY_NAME_HIBERNATE_DIALECT));
        properties.put(PROPERTY_NAME_HIBERNATE_SHOW_SQL, env.getRequiredProperty(PROPERTY_NAME_HIBERNATE_SHOW_SQL));
        return properties;
    }
	
	@Bean(name="dataSourceJpaMysql")
    public DataSource dataSource() {
    	System.out.println("JpaMysqlPersistenceContext::dataSource() Environment env: " + env);
    	System.out.println("JpaMysqlPersistenceContext::dataSource() env.getRequiredProperty(PROPERTY_NAME_DATABASE_DRIVER): " + env.getRequiredProperty(PROPERTY_NAME_DATABASE_DRIVER));
    	// capital60
    	// matalan
    	// mysql:mysql-connector-java:jar
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
 
        dataSource.setDriverClassName(env.getRequiredProperty(PROPERTY_NAME_DATABASE_DRIVER));
        dataSource.setUrl(env.getRequiredProperty(PROPERTY_NAME_DATABASE_URL));
        dataSource.setUsername(env.getRequiredProperty(PROPERTY_NAME_DATABASE_USERNAME));
        dataSource.setPassword(env.getRequiredProperty(PROPERTY_NAME_DATABASE_PASSWORD));
 
        return dataSource;
    }*/
}
