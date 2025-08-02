package com.mediatorsystems.pf.service;

import com.mediatorsystems.pf.domain.Flyer;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import javax.ejb.Remove;
import javax.ejb.Stateful;

/**
 * @author Kenneth Christensen
 *         Copyright (c) 2006, 2007, 2008 Mediator Systems ApS. All rights reserved.
 */
@Stateful
@Name("testService")
@AutoCreate
public class TestBean implements TestLocal {
    @In
    FullTextEntityManager em;

    public void index() {
        Flyer flyer = new Flyer();
        flyer.setLanguage("Danish");
        flyer.setText("Dette er en test!");
        em.persist(flyer);

        flyer = new Flyer();
        flyer.setLanguage("English");
        flyer.setText("This is a test!");
        em.persist(flyer);
    }

    @Remove // Seam needs this one for stateful beans
    public void destroy() {
    }
}
