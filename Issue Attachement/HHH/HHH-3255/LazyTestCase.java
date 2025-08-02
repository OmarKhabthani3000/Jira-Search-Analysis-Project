package gov.utrafe.lab.lazy;

import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import junit.framework.TestCase;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Session;

public class LazyTestCase extends  TestCase{

	
	protected EntityManagerFactory emf = Persistence.createEntityManagerFactory("helloworld");
	
	protected EntityManager em;

	protected EntityTransaction tx;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		this.em = emf.createEntityManager();
		this.tx = em.getTransaction();
		tx.begin();
	}

	@Override
	protected void tearDown() throws Exception {
		// TODO Auto-generated method stub
		super.tearDown();
		this.tx.commit();
		this.em.close();
	}
	
	public void testInsertLazy(){
		Session session = (Session)em.getDelegate();
		Agency agency = new Agency();
		agency.setName("AGENCIA FREGUESIA DO O");
		em.persist(agency);
		
		BankAccount conta = new BankAccount();
		conta.setNumber("0101");
		conta.setAgency(agency);
		em.persist(conta);
	}
	
	public void testFindLazy(){
		Session session = (Session)em.getDelegate();
		Criteria c = session.createCriteria(BankAccount.class);
		//Query c = session.createQuery("from BankAccount");
		List l = c.list();
		for(Iterator it = l.iterator(); it.hasNext();){
			BankAccount bk = (BankAccount)it.next();
			assertFalse(Hibernate.isInitialized(bk.getAgency()));
		}
	}
}
