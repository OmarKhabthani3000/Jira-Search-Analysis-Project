package tests;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.classic.Session;

/*
 * Created on: 08-02-06
 * 
 * Important notice: This software is the sole property of Accovia Inc. and
 * cannot be distributed and/or copied without the written permission of Accovia Inc.
 * 
 * Copyright (c) 2008, Accovia Inc., All rights reserved.
 */

/**
 * DOCUMENTME
 *
 * @author [METTRE VOTRE NOM ICI]
 */
public class BugIdClassIds {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		AnnotationConfiguration config = new AnnotationConfiguration();
		
		SessionFactory sessionFact = config.configure().buildSessionFactory();
		
		Session session = sessionFact.getCurrentSession();
		
		Transaction tx = session.beginTransaction();

		System.out.println(" --- countrySubdivisionWithEmbeddedIdPdo -------------------------------");
		
		CountrySubdivisionPdoWithEmbeddedId.PrimaryKey embeddedIdPk = new CountrySubdivisionPdoWithEmbeddedId.PrimaryKey();
		
		embeddedIdPk.setCode("QC");
		embeddedIdPk.setCountryIsoCode("CA");
		
		CountrySubdivisionPdoWithEmbeddedId countrySubdivisionWithEmbeddedId = (CountrySubdivisionPdoWithEmbeddedId) session.load(CountrySubdivisionPdoWithEmbeddedId.class, embeddedIdPk);

		System.out.println(countrySubdivisionWithEmbeddedId.getPk().getCode()); // This DOES NOT generates lazy loading while as expected
		System.out.println(countrySubdivisionWithEmbeddedId.getPk().getCountryIsoCode());

		System.out.println(" --- countrySubdivisionWithIdClass -------------------------------------");
		
		CountrySubdivisionPdoWithIdClass.PrimaryKey idClassPk = new CountrySubdivisionPdoWithIdClass.PrimaryKey();
		
		idClassPk.setCode("QC");
		idClassPk.setCountryIsoCode("CA");
		
		CountrySubdivisionPdoWithIdClass countrySubdivisionWithIdClass = (CountrySubdivisionPdoWithIdClass) session.load(CountrySubdivisionPdoWithIdClass.class, idClassPk);

		System.out.println(countrySubdivisionWithIdClass.getCode()); // This DOES generates lazy loading while IT SHOULD NOT
		System.out.println(countrySubdivisionWithIdClass.getCountryIsoCode());
		
		tx.commit(); // This will close the session
		
		// session.close();
		
	}
		
}
