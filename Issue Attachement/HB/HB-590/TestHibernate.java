package test.workarea;
import net.sf.hibernate.Session;

import com.i21.autopay.core.model.BankInformation;
import com.i21.autopay.framework.AbstractTestCase;
import com.i21.autopay.framework.persist.HibernateManager;
import com.i21.autopay.framework.persist.MapperLookup;
/**
 * Class TestHibernate.java Created on Dec 27, 2003
 * @author Niraj Juneja (nzjuneja@kanbay.com)
 * 
 */
public class TestHibernate extends AbstractTestCase {
	/**
	 * Constructor for TestHibernate.
	 * @param arg0
	 */
	public TestHibernate(String arg0) {
		super(arg0);
	}
	/**
	 * @see AbstractTestCase#setUp()
	 */
	protected void setUp() throws Exception {
		
	}
	/**
	 * @see AbstractTestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		
	}

	public void testBankInformationInHibernate() throws Exception {
		
		BankInformation binfo ;
	}
	
	public void testHibernate() {
	try{	
	 MapperLookup.Instance().init(super.key());	
	 HibernateManager.Instance().init(super.key());	
	 Session session = HibernateManager.Instance().currentSession();
	}catch(Throwable ex)
	{
	 ex.printStackTrace();	
	}
	}
}
