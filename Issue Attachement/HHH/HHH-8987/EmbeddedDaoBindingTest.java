import org.testng.AssertJUnit;
import org.testng.annotations.Test;

public class EmbeddedDaoBindingTest extends BaseIntegrationTestCase
{
	@Test
	public void testEmbeddedPKWithDetachedObjects() throws Exception
	{
		final Dao2 dao2 = new Dao2();
		final Dao3 dao3 = new Dao3();

		final Dao1 dao1 = new Dao1(dao2,  dao3);

		// persist these to the DB in a transaction...
		transactor.runWithTransaction(new TransactorService.UnitOfWork<Exception>()
		{
			public void run() throws Exception
			{
				// allocate primary keys & persist to database...
				DbUtil.primary.getSession().save(dao2);
				DbUtil.primary.getSession().save(dao3);

				dao2.setText("blah");

				// save the composite PK object
				DbUtil.primary.getSession().save(dao1);
			}
		});

		// check we allocated primary keys...
		AssertJUnit.assertTrue(dao2.getId() > 0);
		AssertJUnit.assertTrue(dao3.getId() > 0);

		// Note that dao2 and dao3 are detached at this point... so, the following statement has no effect on the persisted data
		dao3.setText("roger that");

		// in a new transaction, load dao2 / dao3 and check that the persisted state is as expected...
		transactor.runWithTransaction(new TransactorService.UnitOfWork<Exception>()
		{
			public void run() throws Exception
			{
				// fetch new copy of Dao2/3 from DB...
				Dao2 newCopyOfDao2 = (Dao2) DbUtil.primary.getSession().get(Dao2.class, dao2.getId());
				Dao3 newCopyOfDao3 = (Dao3) DbUtil.primary.getSession().get(Dao3.class, dao3.getId());

				AssertJUnit.assertNotNull(newCopyOfDao2.getText());
				// text should be null, as the set (above) was on a detached object, and should not have been persisted...
				AssertJUnit.assertNull(newCopyOfDao3.getText());
			}
		});

		// in a new transaction, first load Dao1 (composite PK via detached dao2/ dao3),
		// then load dao2 / dao3 and check that the persisted state is as expected
		transactor.runWithTransaction(new TransactorService.UnitOfWork<Exception>()
		{
			public void run() throws Exception
			{
				// fetch new copies of Dao1 from DB...
				Dao1 newCopyOfDao1 = (Dao1) DbUtil.primary.getSession().get(Dao1.class, new Dao1PK(dao2, dao3));

				// in Hibernate 4.3.1, the above statement has the side effect of binding the detached objects dao2/3 to
				// the current transaction...  this is done without a merge or anything.... which means that subsequent
				// fetches by primary key on those objects will return the WRONG (previously detached) objects.
				//
				// this was not the case in Hibernate 4.2.8 - and results in the assert statement below failing...
				// as text was set non-null on the detached object, but never persisted.

				// fetch new copy of Dao2/3 from DB...
				Dao2 newCopyOfDao2 = (Dao2) DbUtil.primary.getSession().get(Dao2.class, dao2.getId());
				Dao3 newCopyOfDao3 = (Dao3) DbUtil.primary.getSession().get(Dao3.class, dao3.getId());

				AssertJUnit.assertNotNull(newCopyOfDao2.getText());
				// text should be null, as the set (above) was on a detached object, and should not have been persisted...
				AssertJUnit.assertNull(newCopyOfDao3.getText());
				// FAILS in Hibernate 4.3.1 ...
			}
		});
	}

}
