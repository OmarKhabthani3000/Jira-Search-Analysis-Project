package org.hibernate.envers.test.integration.manytoone.bidirectional;

import org.hibernate.Session;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.hibernate.envers.configuration.EnversSettings;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;
import org.hibernate.envers.test.BaseEnversFunctionalTestCase;
import org.hibernate.envers.test.Priority;
import org.hibernate.testing.TestForIssue;
import org.junit.Test;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

@TestForIssue(jiraKey = "HHH-6542")
public class ManyToManyRevisionOnCollectionChangesFalseTest extends BaseEnversFunctionalTestCase {
	private Long id;

	@Override
	protected Class<?>[] getAnnotatedClasses() {
		return new Class<?>[] {
				EntityA.class,
				EntityB.class
		};
	}

	@Override
	protected void addSettings(Map settings) {
		super.addSettings( settings );

		settings.put( EnversSettings.REVISION_ON_COLLECTION_CHANGE, false);
	}

	@Test
	@Priority(10)
	public void initData() {

		Session session = openSession();
		try {

			// Revision 1
			session.getTransaction().begin();

			EntityB b = new EntityB();
			b.setId(1L);
			session.persist(b);

			EntityA a = new EntityA();
			a.setId(2L);
			a.setEntityBs(Stream.of(b).collect(Collectors.toSet()));
			session.persist(a);

			session.getTransaction().commit();

			this.id = a.getId();
		}
		finally {
			session.close();
		}
	}

	@Test
	public void testAuditQuery() {
		AuditQuery query = this.getAuditReader()
				.createQuery()
				.forRevisionsOfEntity(EntityA.class, true, true)
				.add(AuditEntity.id().eq(this.id));

		List results = query.getResultList();

		assertEquals( 1, results.size() );

		// from the log we it can be seen that the relationship table is not audited, if REVISION_ON_COLLECTION_CHANGE
		// is updated to true, then the auditing occurs correctly
		EntityA a = (EntityA)results.get(0);
		assertEquals(1, a.getEntityBs().size());
	}

	@Entity
	@Audited
	public static class EntityA {
		@Id
		private Long id;

		@ManyToMany
		@JoinColumn(nullable = false)
		@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
		private Set<EntityB> entityBs = new HashSet<>();

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public Set<EntityB> getEntityBs() {
			return entityBs;
		}

		public void setEntityBs(Set<EntityB> entityBs) {
			this.entityBs = entityBs;
		}
	}

	@Entity
	@Audited
	public static class EntityB {
		@Id
		private Long id;

		@ManyToMany(mappedBy = "entityBs")
		private Set<EntityA> entityAs = new HashSet<>();;

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}
	}
}
