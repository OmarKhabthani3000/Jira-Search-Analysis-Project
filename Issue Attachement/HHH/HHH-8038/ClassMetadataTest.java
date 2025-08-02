package org.hibernate.metadata;

import static junit.framework.Assert.assertEquals;
import static org.hibernate.proxy.HibernateProxyHelper.getClassWithoutInitializingProxy;
import static org.junit.Assert.assertFalse;

import java.beans.PropertyVetoException;
import java.util.Collection;
import java.util.Properties;

import javax.sql.DataSource;

import org.hibernate.Hibernate;
import org.hibernate.SessionFactory;
import org.hibernate.dialect.HSQLDialect;
import org.hibernate.metadata.entities4getPropertyValueTest.Composition;
import org.hibernate.metadata.entities4getPropertyValueTest.CompositionItem;
import org.hsqldb.jdbc.JDBCDriver;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import com.mchange.v2.c3p0.ComboPooledDataSource;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=ClassMetadataTest.SpringConfig.class)
public class ClassMetadataTest {

	@Configuration
	@EnableTransactionManagement
	static class SpringConfig {
		@Autowired private DataSource dataSource;
		@Autowired private LocalSessionFactoryBean sessionFactoryBean;
		
		@Bean(name="transactionManager") PlatformTransactionManager getTxManager() {
			HibernateTransactionManager txManager = new HibernateTransactionManager();
			txManager.setSessionFactory(sessionFactoryBean.getObject());
			return txManager;
		}
		
		@Bean LocalSessionFactoryBean getSessionFactory() {
			LocalSessionFactoryBean sfFactory = new LocalSessionFactoryBean();
			sfFactory.setDataSource(dataSource);
			sfFactory.setPackagesToScan(Composition.class.getPackage().getName());
			
			Properties props = new Properties();
			props.setProperty("hibernate.show_sql", "true");
			props.setProperty("hibernate.hbm2ddl.auto", "create-drop");
			props.setProperty("hibernate.dialect", HSQLDialect.class.getName());
			sfFactory.setHibernateProperties(props);
			
			return sfFactory;
		}
		
		@Bean DataSource getDataSource() throws PropertyVetoException {
			ComboPooledDataSource ds = new ComboPooledDataSource();
			ds.setDriverClass(JDBCDriver.class.getName());
			ds.setUser("sa");
			ds.setPassword("");
			ds.setJdbcUrl("jdbc:hsqldb:mem:hibernateTests");
			return ds;
		}
	}
	
	@Autowired private SessionFactory sessionFactory;
	
	private void saveUpdate(Object o) {
		sessionFactory.getCurrentSession().saveOrUpdate(o);
	}
	
	private boolean isInSession(Object o) {
		return sessionFactory.getCurrentSession().contains(o);
	}
	
	@Test
	@Transactional
	public void testGetPropertyValue() {
		String compositionId = "whatever";
		Composition parent = new Composition();
		parent.setId(compositionId);
		saveUpdate(parent);
		
		int compositionSize = 10;
		for (int i = 0; i < compositionSize; i++) {
			CompositionItem item = new CompositionItem();
			item.setId(i);
			item.setParent(parent);
			saveUpdate(item);
		}
		
		sessionFactory.getCurrentSession().flush();
		sessionFactory.getCurrentSession().clear();
		
		assertFalse("Object still in session.", isInSession(parent));
		
		Composition loadedParent = (Composition) sessionFactory.getCurrentSession().load(Composition.class, compositionId);
		ClassMetadata metadata = sessionFactory.getClassMetadata(getClassWithoutInitializingProxy(loadedParent));
		Hibernate.initialize(loadedParent);
		
		@SuppressWarnings("unchecked")
		Collection<CompositionItem> loadedList = (Collection<CompositionItem>) metadata.getPropertyValue(loadedParent, "items");
		Hibernate.initialize(loadedList);
		
		assertEquals("Wrong number of items.",compositionSize, loadedList.size());
	}
}
