import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.RevisionType;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.criteria.AuditCriterion;
import org.hibernate.envers.query.criteria.AuditDisjunction;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class TestMaximizeInDisjunction {
	
	private EntityManagerFactory entityManagerFactory;

	@Before
	public void setUp() throws Exception {
		entityManagerFactory = Persistence.createEntityManagerFactory("HBTestPU");
	}

	@After
	public void tearDown() throws Exception {
		entityManagerFactory.close();
	}

	@Test
	public void test() {
		List<Integer> ids = new ArrayList<Integer>();
		
		// Create some dummy instances to emulate traffic.
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();
		for (int i=0; i<10; i++) {
			TestEntity te = new TestEntity("test"+i, "dummy"+i);
			entityManager.persist(te);
			ids.add(te.getId());
		}
		entityManager.getTransaction().commit();
		entityManager.close();
		
		// Edit some of them just to have one more revision.
		entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();
		List<Integer> editedIds = ids.subList(0, 3);
		for (Integer id : editedIds) {
			TestEntity te = entityManager.find(TestEntity.class, id);
			te.setFoo(te.getFoo()+"xxx");
			te.setBar(te.getBar()+"xxx");
		}
		entityManager.getTransaction().commit();
		entityManager.close();
		
		// Delete half of them to get some DEL revisions.
		entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();
		List<Integer> deletedIds = ids.subList(0, 5);
		for (Integer id : deletedIds) {
			entityManager.remove(entityManager.getReference(TestEntity.class, id));
		}
		entityManager.getTransaction().commit();
		entityManager.close();
		
		// Now we have the test dataset ready. Time to trigger the issue.
		entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();
		// Try fetching the last non-delete revision for each of the deleted entities listed in deletedIds
		AuditDisjunction disjunction = AuditEntity.disjunction();
		for (Integer id : deletedIds) {
			AuditCriterion crit = AuditEntity.revisionNumber().maximize()
					.add(AuditEntity.id().eq(id))
					.add(AuditEntity.revisionType().ne(RevisionType.DEL));
			disjunction.add(crit);
			// Workaround: using this line instead works correctly:
			//disjunction.add(AuditEntity.conjunction().add(crit));
		}
		List<?> beforeDeletionRevisions = AuditReaderFactory.get(entityManager).createQuery()
				.forRevisionsOfEntity(TestEntity.class, true, false)
				.add(disjunction)
				.getResultList();
		for (Object o : beforeDeletionRevisions) {
			TestEntity te = (TestEntity)o;
			assertTrue("Entity "+te.getId()+" returned, but it wasn't queried for.", deletedIds.contains(te.getId()));
		}
		for (Object o : beforeDeletionRevisions) {
			TestEntity te = (TestEntity)o;
			if (editedIds.contains(te.getId())) {
				assertTrue("Entity "+te.getId()+" wasn't returned at the expected revision.", te.getFoo().endsWith("xxx") && te.getBar().endsWith("xxx"));
			} else {
				assertTrue("Entity "+te.getId()+" wasn't returned at the expected revision.", !te.getFoo().endsWith("xxx") && !te.getBar().endsWith("xxx"));
			}
		}
		entityManager.getTransaction().commit();
		entityManager.close();
	}

}
