package org.hibernate.test;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public final class LeakingServletContextListener implements ServletContextListener {
	private boolean isShutthingDown = false;
	private EntityManagerFactory entityManagerFactory = null;
	
	@Override
	public void contextInitialized(ServletContextEvent event) {
		getEntityManagerFactory();
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		shutdownEntityManagerFactory();
	}

	public void shutdownEntityManagerFactory() {
		isShutthingDown = true;
		synchronized (LeakingServletContextListener.class) {
			if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
				try {
					entityManagerFactory.close();
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
			entityManagerFactory = null;
		}
	}
	
	private EntityManagerFactory getEntityManagerFactory() {
		if (isShutthingDown) {
			throw new RuntimeException("Application is shutdown can't create a new EntityManagerFactory !");
		}
		if (entityManagerFactory == null || !entityManagerFactory.isOpen()){
			synchronized (LeakingServletContextListener.class) {
				if (entityManagerFactory == null || !entityManagerFactory.isOpen()){
					entityManagerFactory = Persistence.createEntityManagerFactory("test");
				}
			}
		}		
		return entityManagerFactory;
	}
}