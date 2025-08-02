package org.hibernate.test.transaction;

import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.hibernate.cfg.AvailableSettings;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;

import org.junit.Test;

/**
 * Trying to define multiple transactions on same {@link EntityManager}
 * using JTA but not enabling JPA compliance
 */
public class JtaReusingEntityTransactionTest extends BaseEntityManagerFunctionalTestCase {

	@Override
	protected void addConfigOptions(Map options) {
		options.put( AvailableSettings.JPA_TRANSACTION_TYPE, "JTA" );
	}

	@Test
	public void testSave() {
		EntityManager em = createEntityManager();
		EntityTransaction transaction = null;
		try {
			transaction = em.getTransaction();
			transaction.begin();
			transaction.commit();
			transaction.begin();
			transaction.commit();
		}
		finally {
			if ( transaction != null && transaction.isActive() ) {
				transaction.rollback();
			}
			em.close();
		}
	}

}
