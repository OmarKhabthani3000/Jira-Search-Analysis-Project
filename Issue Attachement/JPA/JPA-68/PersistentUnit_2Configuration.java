package com.airfrance.ndatabase.spring.configuration;

import java.util.Arrays;

import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;

import org.dozer.DozerBeanMapper;
import org.hibernate.cfg.Environment;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.persistenceunit.DefaultPersistenceUnitManager;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * A base configuration bootstrapping the application context.
 * 
 * @author DINB - TA
 */
@Configuration
@Import({ 
			DefaultProfileConfiguration.class, 
			SecurityConfiguration.class })
@ComponentScan(
	    basePackages = {
	    		 "com.airfrance.ndatabase.entity.pu2", 
	    		 "com.airfrance.ndatabase.service.pu2", 
	    		 "com.airfrance.ndatabase.repository.pu2"
	    		},
	    excludeFilters = @Filter(type = FilterType.ANNOTATION, value = Configuration.class))
@EnableTransactionManagement
@EnableJpaRepositories(
		transactionManagerRef 	= "2_transactionManager", 
		basePackages 			= "com.airfrance.ndatabase.repository.pu2", 
		entityManagerFactoryRef = "2_entityManagerFactory"
		)

public class PersistentUnit_2Configuration {
	
	/**
	 * The datasource provided by other configurations.
	 */
	@Inject
	@Named("2_datasource")
	private DataSource dataSource;

	/**
	 * The dialect provided by other configurations.
	 */
	@Inject
	@Named("jpaDialect")
	private String jpaDialect;

	/**
	 * The flag generateDdl provided by other configurations.
	 */
	@Inject
	@Named("generateDdl")
	private Boolean generateDdl; 

	/**
	 * The flag showSql provided by other configurations.
	 */
	@Inject
	@Named("showSql")
	private Boolean showSql;

	/**
	 * Gets the bean mapper.
	 * @return the bean mapper.
	 */
	@Bean
	public DozerBeanMapper dozerBeanMapper() {
		return new DozerBeanMapper(Arrays.asList("dozer-bean-mappings.xml"));
	}

	/**
	 * Builds the persistence unit manager.
	 * @param dataSource the datasource.
	 * @return the persistence unit manager.
	 */
	@Bean(name="2_persitenceUnitManager")
	public DefaultPersistenceUnitManager persistenceUnitManager() {
		DefaultPersistenceUnitManager defaultPersistenceUnitManager = new DefaultPersistenceUnitManager();
		defaultPersistenceUnitManager.setPersistenceXmlLocation("classpath*:/META-INF/persistence.xml");
		defaultPersistenceUnitManager.setDefaultDataSource(dataSource);
		defaultPersistenceUnitManager.setDefaultPersistenceUnitName("2_persistenceUnit");
		return defaultPersistenceUnitManager;
	}
	
	/**
	 * Builds the persistence unit manager.
	 * @param dataSource the datasource.
	 * @return the persistence unit manager.
	 */
	@Bean
	public HibernateJpaVendorAdapter jpaAdapter() {
		HibernateJpaVendorAdapter jpaAdapter = new HibernateJpaVendorAdapter();
		jpaAdapter.setDatabasePlatform(jpaDialect);
		jpaAdapter.setGenerateDdl(generateDdl);
		jpaAdapter.setShowSql(showSql);
		return jpaAdapter;
	}
	
	/**
	 * Builds the persistence unit manager.
	 * @param dataSource the datasource.
	 * @return the persistence unit manager.
	 */
	@Bean(name="2_entityManagerFactory")
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
		localContainerEntityManagerFactoryBean.setDataSource(dataSource);
		localContainerEntityManagerFactoryBean.setPersistenceUnitManager(persistenceUnitManager());
		localContainerEntityManagerFactoryBean.setJpaVendorAdapter(jpaAdapter());
		localContainerEntityManagerFactoryBean.setPersistenceProviderClass(HibernatePersistenceProvider.class);
		if(generateDdl) {
			localContainerEntityManagerFactoryBean.getJpaPropertyMap().put(Environment.HBM2DDL_AUTO, "create");
		}
		return localContainerEntityManagerFactoryBean;
	}
	
	/**
	 * Builds the persistence unit manager.
	 * @param dataSource the datasource.
	 * @return the persistence unit manager.
	 */
	@Bean(name="2_transactionManager")
	public PlatformTransactionManager transactionManager() {
		return new JpaTransactionManager(entityManagerFactory().getObject());
	}
}
