/*
 * $Id$
 */
package org.hibernate.test.fetchProxy;

/**
 * 
 * 
 * @author Maarten Winkels
 * @version $Revision$
 */
public class Main {

    private Long id;

    private Proxied one;

    private Proxied two;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Proxied getOne() {
        return one;
    }

    public void setOne(Proxied one) {
        this.one = one;
    }

    public Proxied getTwo() {
        return two;
    }

    public void setTwo(Proxied two) {
        this.two = two;
    }
}