	@Test
	@TestForIssue(jiraKey = "HHH-8373")
	public void testFunctionInWhere() {
		EntityManager em = getOrCreateEntityManager();
		em.getTransaction().begin();
		Item item = new Item( "mouse", "Micro$oft mouse" );
		em.persist( item );
		em.getTransaction().commit();

		em.getTransaction().begin();
		Item resultItem = em.createQuery( "from Item i where i.name = lower(:name)", Item.class ).setParameter( "name", "MOUSE" ).getSingleResult();
		assertNotNull( resultItem );
		assertEquals( "Micro$oft mouse", resultItem.getDescr() );
		em.getTransaction().commit();

		em.close();
	}
