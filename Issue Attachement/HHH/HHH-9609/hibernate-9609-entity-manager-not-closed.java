package org.hibernate.test.connections;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;

import javax.persistence.EntityManager;

import org.hibernate.Session;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.engine.spi.SessionFactoryImplementor;


public class TestEntityManager {

  private void closeJdbcConnection(EntityManager em) {
      Session session = (Session) em.getDelegate();
      SessionFactoryImplementor sfi = (SessionFactoryImplementor) session.getSessionFactory();
      ConnectionProvider cp = sfi.getConnectionProvider();
      Connection connection = cp.getConnection();
  }

  /**
   * EntityManager should get in the state close, if EntityManager.close() is called, even if the JDBC connection is already closed.
   */
  public void testCloseEntityManagerAfterConnectionClose() {
    MyEntity entity = new MyEntity();
    EntityManager em = entityManagerFactory.createEntityManager();
    try {
	em.getTransaction().begin();
	closeJdbcConnection(em);//simulate a connection loss.
	em.merge(entity);
	em.getTransaction().close();
    } catch (Exception e) {
	try {
	    if (em.getTransaction().isActive()) {
		em.getTransaction().rollback();
	    }
	} catch (Exception excRollback) {
	    logger.error(excRollback);
	} finally  {
	    try {
		em.close();
	    } catch (Exception excClose) {
		logger.error(excClose);
	    }
	    logger.error("EntityManager isOpen: " + em.isOpen());
	    assertFalse("EntityManager is not in the closed state, after close()", em.isOpen());
	}
    }
  }

}