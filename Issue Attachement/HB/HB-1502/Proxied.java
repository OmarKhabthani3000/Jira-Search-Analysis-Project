/*
 * $Id$
 */
package org.hibernate.test.fetchProxy;

/**
 * Class to be proxied.
 * 
 * @author Maarten Winkels
 * @version $Revision$
 */
public class Proxied {
    
    private Long id;
    
    private String name;
    
    private Spec spec;

    protected Proxied() {}

    public Proxied(String name) {
        this.name = name;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Spec getSpec() {
        return spec;
    }
    
    public void setSpec(Spec spec) {
        this.spec = spec;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
}
