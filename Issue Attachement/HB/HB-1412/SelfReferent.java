package org.hibernate.test;

import java.util.HashSet;
import java.util.Set;

/**
 * Persistent class for <code>OuterJoinProxyTest</code>.
 * 
 * @author Maarten Winkels
 */
public class SelfReferent {
    
    private Long id;
    
    private String name;
    
    private SelfReferent lazy;
    
    private SelfReferent eager;
    
    private Set group;
    
    /**
     * Getter for the "id" property.
     * 
     * @return Returns the id.
     */
    public Long getId() {
        return id;
    }
    
    /**
     * Setter for the "id" property.
     * 
     * @param id The id to set.
     */
    private void setId(Long id) {
        this.id = id;
    }
    
    /**
     * Getter for the "other" property.
     * 
     * @return Returns the other.
     */
    public SelfReferent getEager() {
        return eager;
    }
    
    /**
     * Setter for the "other" property.
     * 
     * @param other The other to set.
     */
    public void setEager(SelfReferent other) {
        this.eager = other;
    }
    
    /**
     * Getter for the "name" property.
     * 
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }
    
    /**
     * Setter for the "name" property.
     * 
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Getter for the "lazy" property.
     * 
     * @return Returns the lazy.
     */
    public SelfReferent getLazy() {
        return lazy;
    }
    
    /**
     * Setter for the "lazy" property.
     * 
     * @param lazy The lazy to set.
     */
    public void setLazy(SelfReferent lazy) {
        this.lazy = lazy;
    }
    
    /**
     * Adds an item to the group.
     * 
     * @param item
     */
    public void add (SelfReferent item) {
        if (group == null) {
            group = new HashSet();
        }
        group.add(item);
    }
    
    /**
     * Getter for the "group" property.
     * 
     * @return Returns the group.
     */
    public Set getGroup() {
        return group;
    }
    
    /**
     * Setter for the "group" property.
     * 
     * @param group The group to set.
     */
    private void setGroup(Set group) {
        this.group = group;
    }
}
