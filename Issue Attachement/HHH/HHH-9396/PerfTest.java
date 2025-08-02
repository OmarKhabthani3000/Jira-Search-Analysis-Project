
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class PerfTest {

	@Autowired
    protected DataSource dataSource;
    @PersistenceUnit
    protected EntityManagerFactory entityManagerFactory;
    @PersistenceContext
    protected EntityManager entityManager;
    @Autowired
    protected PlatformTransactionManager transactionManager;

	private static Long id = 36223L;

	@Test
    @org.databene.contiperf.PerfTest(invocations = 20000, threads = 8, rampUp = 1000)
    public void test1() {
        new TransactionTemplate(transactionManager).execute(new TransactionCallback<Object>() {
            @Override
            public Object doInTransaction(TransactionStatus status) {

                UserLoginHistoryEntry entry = createLoginHistoryEntry();
                entityManager.persist(entry);
                entityManager.flush();
                return null;
            }
        });

    }

    @Test
    @org.databene.contiperf.PerfTest(invocations = 20000, threads = 8, rampUp = 1000)
    public void test2() {
        new TransactionTemplate(transactionManager).execute(new TransactionCallback<Object>() {
            @Override
            public Object doInTransaction(TransactionStatus status) {
             UserLoginHistoryEntry entry = entityManager.find(UserLoginHistoryEntry.class, id++);
                entry.setLogoutReason("log out");
                entry.setLogoutDate(DateTime.now());
                entityManager.merge(entry);
                entityManager.flush();
                return null;
            }
        });

    }
	
}