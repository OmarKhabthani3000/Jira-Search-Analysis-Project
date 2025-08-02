public class JPACacheTest extends TestCase
{

	@Before
	public void setUp() throws Exception
	{
		PersistenceManager.closeEntityManager();
		CommonTest.createDomain();
	}

	@After
	public void tearDown() throws Exception
	{
		CommonTest.deleteDomain();
	}

	public void testAll()
	{
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = DriverManager
					.getConnection("jdbc:mysql://sqlserver:1007/drift?user=someId&password=somePassword");
			conn.setAutoCommit(false);

			PreparedStatement ps = null;

			ps = conn
					.prepareStatement("update domain set name='new name' where description=?");
			ps.setString(1, DomainDAOTest.DESCRIPTION);
			ps.executeUpdate();
			conn.commit();

			Domain d = new DomainDAO().findByDescription(
					DomainDAOTest.DESCRIPTION).get(0);

			assertNotNull(d);
			System.out.println(d.getName());
			assertEquals(d.getName(), "new name");

		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}

	}

}