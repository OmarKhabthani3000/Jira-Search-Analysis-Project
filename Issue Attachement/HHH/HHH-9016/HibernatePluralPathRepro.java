/*
 *  
 * Copyright (c) .Grant Jennings. All rights reserved.  
 * Licensed under the ##LICENSENAME##. See LICENSE file in the project root for full license information.  
*/
package hibernate.repro;


import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.Bindable;
import javax.persistence.metamodel.PluralAttribute;
import org.junit.After;

import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author grantjennings
 */
public class HibernatePluralPathRepro  {
	EntityManagerFactory emf = Persistence.createEntityManagerFactory("default");
	
	
	EntityManager em;
	
	@Before
	public void createEntityManager() {
		em = emf.createEntityManager();
		em.getTransaction().begin();
	}
	
	@After
	public void rollback() {
		em.getTransaction().rollback();
	}
	
	@Test
	public void hibernatePluralPathTest() {
		CriteriaBuilder build = em.getCriteriaBuilder();
		CriteriaQuery<OneToManyInstance> critQ = build.createQuery(OneToManyInstance.class);
		Root<OneToManyInstance> resultRoot = critQ.from(OneToManyInstance.class);
		Path pluralPath = resultRoot.get("many");
		Bindable shouldBePluralAttribute = pluralPath.getModel();
		assertNotNull(shouldBePluralAttribute);
		
		assertTrue(shouldBePluralAttribute instanceof PluralAttribute);
	}
}
