/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.envers.test.integration.jta;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.hibernate.cfg.AvailableSettings;
import org.hibernate.envers.test.BaseEnversJPAFunctionalTestCase;
import org.hibernate.envers.test.Priority;
import org.hibernate.envers.test.entities.IntTestEntity;
import org.hibernate.testing.jta.TestingJtaBootstrap;
import org.hibernate.testing.jta.TestingJtaPlatformImpl;
import org.junit.Assert;
import org.junit.Test;

/**
 * Same as {@link org.hibernate.envers.test.integration.basic.Simple}, but in a JTA environment.
 *
 * @author Adam Warski (adam at warski dot org)
 */
public class JtaTransactionWithSpringReleaseResource extends BaseEnversJPAFunctionalTestCase {
	private Integer id1;

	@Override
	protected Class<?>[] getAnnotatedClasses() {
		return new Class[] {IntTestEntity.class};
	}

	@Override
	protected void addConfigOptions(Map options) {
		TestingJtaBootstrap.prepare( options );
		options.put( AvailableSettings.ALLOW_JTA_TRANSACTION_ACCESS, "true" );
	}

	@Test
	@Priority(10)
	public void initData() throws Exception {
		TestingJtaPlatformImpl.INSTANCE.getTransactionManager().begin();

		EntityManager em;
		IntTestEntity ite;
		try {
			em = getEntityManager();
			ite = new IntTestEntity( 10 );
			em.persist( ite );
			id1 = ite.getId();
			//simulating spring JtaTransactionManager.triggerBeforeCompletion()
			em.close();
		}
		finally {
			TestingJtaPlatformImpl.tryCommit();
		}
	}

	@Test
	public void testRevisionsCounts() throws Exception {
		Assert.assertEquals(
				1, getAuditReader().getRevisions(
				IntTestEntity.class, id1
		).size()
		);
	}

	@Test
	public void testHistoryOfId1() {
		IntTestEntity ver1 = new IntTestEntity( 10, id1 );

		List<Number> revisions = getAuditReader().getRevisions(
				IntTestEntity.class, id1
		);

		Assert.assertEquals(
				ver1, getAuditReader().find(
				IntTestEntity.class, id1, revisions.get( 0 )
		)
		);
	}
}
