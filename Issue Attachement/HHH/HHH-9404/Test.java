package com;

import com.model.Business;
import com.model.BusinessBank;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.Arrays;

public class Test {
    private static final SessionFactory sessionFactory;

    static {
        try {
            sessionFactory = HibernateUtil.getSessionFactory();
        } catch (Throwable ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static Session getSession() throws HibernateException {
        return sessionFactory.openSession();
    }

    public static void main(final String[] args) throws Exception {
            Business business = new Business();
            business.setBusinessName("Business name");

            BusinessBank businessBank1 = new BusinessBank();
            businessBank1.setBusiness(business);
            businessBank1.setBankName("bank1");

            BusinessBank businessBank2 = new BusinessBank();
            businessBank2.setBusiness(business);
            businessBank2.setBankName("bank2");

            business.setBusinessBanks(Arrays.asList(businessBank1, businessBank2));

            Session session = getSession();
            try {
                session.beginTransaction();
                session.persist(business);
                session.getTransaction().commit();
            } finally {
                session.close();
            }

            businessBank1.setBankName("Bank1 rename");
            businessBank2.setBankName("Bank2 rename");

            session = getSession();
            try {
                session.beginTransaction();
                session.merge(business);
                session.getTransaction().commit();
            } finally {
                session.close();
            }
    }
}