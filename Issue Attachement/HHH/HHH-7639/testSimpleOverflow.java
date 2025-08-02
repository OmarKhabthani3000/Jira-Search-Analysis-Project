/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wakaleo.articles.caching.dao;

import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.Session;


/**
 *
 * @author gjoshi
 */
public class testSimpleOverflow {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        try {
SessionFactory testSession = new Configuration().configure().buildSessionFactory();
            for (int i = 0; i < 10; i++) {

                Session sessionTest =  testSession.openSession();

                Criteria fetchAllRows = sessionTest.createCriteria("Country1");
                fetchAllRows.setCacheable(true);
                List CountryList = fetchAllRows.list();
                System.out.println("TotalRows=" + CountryList.size());

                sessionTest.close();
            }
        } catch (Exception e) {
//            System.out.println("--"+e.getMessage());
            e.printStackTrace();
        }

    }
}
