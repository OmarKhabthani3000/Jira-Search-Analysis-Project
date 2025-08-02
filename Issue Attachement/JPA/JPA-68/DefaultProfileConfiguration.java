package com.airfrance.ndatabase.spring.configuration;

import java.text.MessageFormat;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.sql.DataSource;

import org.h2.Driver;
import org.hibernate.dialect.H2Dialect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

/**
 * A configuration for 'Default' profile.
 * 
 * @author DINB - TA
 */
@Configuration
@Profile(value = "default")
public class DefaultProfileConfiguration {
	
	/**
	 * The logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(DefaultProfileConfiguration.class); 

	 /**
     * Creates a resource bundle.
     */
    protected static ResourceBundle ndatabaseResourceBundle = PropertyResourceBundle.getBundle("ndatabase-config");


	/**
	 * Builds a JNDI datasource.
	 * @return the datasource.
	 */
	@Bean(name="1_datasource", destroyMethod="")
	public DataSource dataSource1() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName(Driver.class.getName());
		String h2Url = MessageFormat.format(" jdbc:h2:file:C:/data/ndatabase1;MODE=Oracle", System.getProperty("java.io.tmpdir"));
		logger.info("Using H2 as datasource1 with URL : {}", h2Url);
		dataSource.setUrl(h2Url);
		dataSource.setUsername("sa");
		dataSource.setPassword("");
		return dataSource;
	}
	
	/**
	 * Builds a JNDI datasource.
	 * @return the datasource.
	 */
	@Bean(name="2_datasource", destroyMethod="")
	public DataSource dataSource2() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName(Driver.class.getName());
		String h2Url = MessageFormat.format("jdbc:h2:file:C:/data/ndatabase2;MODE=Oracle", System.getProperty("java.io.tmpdir"));
		logger.info("Using H2 as datasource2 with URL : {}", h2Url);
		dataSource.setUrl(h2Url);
		dataSource.setUsername("sa");
		dataSource.setPassword("");
		return dataSource;
		
	}


	/**
	 * Returns the JPA dialect to use.
	 * @return the JPA dialect to use.
	 */
	@Bean
	public String jpaDialect() {
		return H2Dialect.class.getName();
	}

	/**
	 * Returns the Generate DDL flag.
	 * @return the Generate DDL flag.
	 */
	@Bean
	public Boolean generateDdl() {
		return Boolean.TRUE;
	}

	/**
	 * Returns the Show SQL flag.
	 * @return the Show SQL flag.
	 */
	@Bean
	public Boolean showSql() {
		return Boolean.TRUE;
	}
	
	/**
	 * Returns the Generate DDL flag.
	 * @return the Generate DDL flag.
	 */
	@Bean
	public Boolean runLiquibase() {
		return Boolean.FALSE;
	}
}
