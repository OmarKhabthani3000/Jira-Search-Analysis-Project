/*
 * @(#)SchemaBasedMultiTenancyTest.java
 * 
 * Copyright (c) 2012 by XORICON GmbH
 */
package com.xoricon.persistence.bo.multitenancy.test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * Test für Datenbank-Schema-basierte Mandantenfähigkeit.
 * 
 * @author Oriel Maute
 */
public class SchemaBasedMultiTenancyTest extends TestCase {

	// ------------------------ Konstruktor(en) -------------------------------
	// ------------------------------------------------------------------------

	/**
	 * Konstruktor
	 */
	public SchemaBasedMultiTenancyTest() {
		super(SchemaBasedMultiTenancyTest.class.getName());
	}
	
	// ----------------------------- Methoden ---------------------------------
	// ------------------------------------------------------------------------
	
	public void test1() {	
		// Prüfen, ob das Baum richtig angelegt wurde
		EntityManagerFactory lEntityManagerFactory= Persistence.createEntityManagerFactory("orm1");
		EntityManager lManager = lEntityManagerFactory.createEntityManager();
		System.err.println("DONE");
		lManager.close();
	}
	
	
    /**
     * Startet alle Tests dieser Testklasse.
     * @param args[] werden ignoriert
     */
    public static void main(String args[]) {
        TestRunner.run(SchemaBasedMultiTenancyTest.class);
    }

    /**
     * Liefert eine Testsuite die alle Testfälle dieser Klasse enthält
     * @return Testsuite die alle Testfälle dieser Klasse enthält
     */
    public static Test suite() {
        TestSuite lTestSuite = new TestSuite(SchemaBasedMultiTenancyTest.class);
        return lTestSuite;
    }
	
}
