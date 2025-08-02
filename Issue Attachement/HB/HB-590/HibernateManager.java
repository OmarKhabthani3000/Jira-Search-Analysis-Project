package com.i21.autopay.framework.persist;



import org.apache.log4j.Logger;

import com.i21.autopay.framework.*;

import com.i21.autopay.framework.Initializeable;
import net.sf.hibernate.cfg.*;
import net.sf.hibernate.*;

/**
 * Class HibernateManager.java Created on Dec 13, 2003
 * @author Niraj Juneja (nzjuneja@kanbay.com)
 * 
 */
public class HibernateManager implements Initializeable {

    private static Logger log = Framework.logger(HibernateManager.class);	
	private static HibernateManager Self = new HibernateManager();
	private static SessionFactory sessionFactory = null;	

	private static final ThreadLocal session = new ThreadLocal();

	public static HibernateManager Instance() {
		return Self;
	}

	private HibernateManager() {
		super();
	}
	
	/**
	 * @see com.i21.autopay.framework.Initializeable#init(KEY)
	 */
	public void init(Framework.KEY key) throws Exception {
		log.info("Trying to init the Hibernate Manager");
		sessionFactory = new Configuration().configure().buildSessionFactory();
		log.info("Hibernate Manager initialised....");
	}

	/**
	 * @see com.i21.autopay.framework.Initializeable#start(KEY)
	 */
	public void start(Framework.KEY key) throws Exception {
	}

	/**
	 * @see com.i21.autopay.framework.Initializeable#stop(KEY)
	 */
	public void stop(Framework.KEY key) throws Exception {
	}
	
	
	public static Session currentSession() throws ApplicationException {
		
		Session s = (Session) session.get();
		// open a new Session , if this Thread has not yet
		if(s == null){
			try{
			s = sessionFactory.openSession();
			session.set(s);	
			}catch(HibernateException ex){
			  log.error("Could not get current Session for Hibernate " , ex);	
			  throw new ApplicationException();	
			}
		}		
		return s;
	}

}
