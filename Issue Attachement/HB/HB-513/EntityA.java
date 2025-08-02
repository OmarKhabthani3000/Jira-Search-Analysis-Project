/*
 * Created on 17-Nov-2003
 *
 */
package gatt.example;

import java.util.HashSet;
import java.util.Set;

/**
 * @hibernate.class       table="EntityA"
 *                        proxy = "gatt.example.EntityA"
 * 
 * @author mread
 */
public class EntityA {

    long id;
	protected Set association = new HashSet();
    
    /**
     * @return
     * 
     * @hibernate.id    column="id"
     *                  generator-class="increment"
     *                  unsaved-value="0"
     * 
     */
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }

	/**
	 * 
	 * @hibernate.set                       cascade="all"
	 *                                      lazy="true"
	 *                                      table="Association_To_Self"
	 * @hibernate.collection-key            column="entityA_id"
	 * @hibernate.collection-composite-element class="gatt.example.AssociationForA" 
	 * 
	 * @return
	 */
	public Set getAssociation() {

		return association;

	}
	
	/**
	 * 
	 * @param associationForA
	 */
	public void setAssociation(Set associationForA) {
		this.association = associationForA;
	}

}
