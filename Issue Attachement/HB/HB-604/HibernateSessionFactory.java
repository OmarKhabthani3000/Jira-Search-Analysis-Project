package test;

import net.sf.hibernate.cfg.Configuration;
import net.sf.hibernate.SessionFactory;
import net.sf.hibernate.MappingException;
import org.apache.log4j.Logger;

/**
 * <p>
 * Denali - A CarShare Reservation System developed by EngineGreen
 * Copyright 2003, EngineGreen.  All Rights Reserved.
 * </p>
 *
 * @version $Id: HibernateSessionFactory.java,v 1.1 2004/01/05 01:30:58 matt Exp $
 * @author Matt Ho <a href="mailto:matt@enginegreen.com">&lt;matt@enginegreen.com&gt;</a>
 */
public class HibernateSessionFactory {
    final private static Logger logger = Logger.getLogger(HibernateSessionFactory.class);

    public static SessionFactory sessionFactory;

    static {
        try {
            Configuration ds = new Configuration();
            ds.addClass(Account.class);

            sessionFactory = ds.buildSessionFactory();
        } catch (Exception e) {
            logger.error(e, e);
        }
    }
}
