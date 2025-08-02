
package example;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratorType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Transient;


/**
 * @author Xinhua
 *
 */
@Entity
public class Customer {
	public Integer f_id = 0;
	public String f_name = "";
	public Set /*(CustomerCard)*/ f_cards = new HashSet( /*CustomerCard*/);
	static private boolean usesAllInstances = false;
	static private List allInstances = new ArrayList();

	/** Default constructor for Customer
	 */
	public Customer() {
		if ( usesAllInstances ) {
			allInstances.add(this);
		}
	}
	
	//should let octopus create this
	public Customer( String name) {
		super();
		this.setName(name);
		if ( usesAllInstances ) {
			allInstances.add(this);
		}
	}

	/** Constructor for Customer
	 * 
	 * @param id 
	 * @param name 
	 */
	public Customer(int id, String name) {
		super();
		this.setId(id);
		this.setName(name);
		if ( usesAllInstances ) {
			allInstances.add(this);
		}
	}

	/** Implements the getter for feature '+ id : Integer'
	 */
	@Id(generate=GeneratorType.IDENTITY)
	public Integer getId() {
		return f_id;
	}
	
	/** Implements the setter for feature '+ id : Integer'
	 * 
	 * @param element 
	 */
	public void setId(Integer element) {
		if ( f_id != element ) {
			f_id = element;
		}
	}
	
	/** Implements the getter for feature '+ name : String'
	 */
	public String getName() {
		return f_name;
	}
	
	/** Implements the setter for feature '+ name : String'
	 * 
	 * @param element 
	 */
	public void setName(String element) {
		if ( f_name != element ) {
			f_name = element;
		}
	}
	
	/** Implements the setter for feature '+ cards : Set(CustomerCard)'
	 * 
	 * @param elements 
	 */
	public void setCards(Set elements) {

			this.f_cards = elements;
	}
	
	/** Implements addition of a single element to feature '+ cards : Set(CustomerCard)'
	 * 
	 * @param element 
	 */
	public void addToCards(CustomerCard element) {
		if ( element == null ) {
			return;
		}
		if ( this.f_cards.contains(element) ) {
			return;
		}
		this.f_cards.add(element);
		if ( element.getOwner() != null ) {
			element.getOwner().z_internalRemoveFromCards(element);
		}
		element.z_internalAddToOwner( (Customer)this );
	}
	
	/** Implements removal of a single element from feature '+ cards : Set(CustomerCard)'
	 * 
	 * @param element 
	 */
	public void removeFromCards(CustomerCard element) {
		if ( element == null ) {
			return;
		}
		this.f_cards.remove(element);
	}
	
	/** Implements the getter for + cards : Set(CustomerCard)
	 */
	@OneToMany(targetEntity=CustomerCard.class, cascade=CascadeType.ALL, 
			mappedBy ="owner" , fetch = FetchType.EAGER)
	public Set getCards() {
		if ( f_cards != null ) {
			return this.f_cards;
		} else {
			return null;
		}
	}
	
	/** This operation should NOT be used by clients. It implements the correct addition of an element in an association.
	 * 
	 * @param element 
	 */
	public void z_internalAddToCards(CustomerCard element) {
		this.f_cards.add(element);
	}
	
	/** This operation should NOT be used by clients. It implements the correct removal of an element in an association.
	 * 
	 * @param element 
	 */
	public void z_internalRemoveFromCards(CustomerCard element) {
		this.f_cards.remove(element);
	}
	
	/** Implements the addition of a number of elements to cards
	 * 
	 * @param newElems 
	 */
	public void addToCards(Collection newElems) {
		Iterator it = newElems.iterator();
		while ( (it.hasNext()) ) {
			Object item = it.next();
			if ( item instanceof CustomerCard ) {
				this.addToCards((CustomerCard)item);
			}
		}
	}
	
	/** Implements the removal of a number of elements from cards
	 * 
	 * @param oldElems 
	 */
	public void removeFromCards(Collection oldElems) {
		Iterator it = oldElems.iterator();
		while ( (it.hasNext()) ) {
			Object item = it.next();
			if ( item instanceof CustomerCard ) {
				this.removeFromCards((CustomerCard)item);
			}
		}
	}
	
	/** Implements the removal of all elements from cards
	 */
	public void removeAllFromCards() {
		Iterator it = new HashSet(getCards()).iterator();
		while ( (it.hasNext()) ) {
			Object item = it.next();
			if ( item instanceof CustomerCard ) {
				this.removeFromCards((CustomerCard)item);
			}
		}
	}
	
	/** Checks all invariants of this object and returns a list of messages about broken invariants
	 */
	public List checkAllInvariants() {
		List /* InvariantError */ result = new ArrayList /* InvariantError */();
		return result;
	}
	
	/** Implements a check on the multiplicities of all attributes and association ends
	 */
	public List checkMultiplicities() {
		List /* InvariantError */ result = new ArrayList /* InvariantError */();
		return result;
	}
	
	/** Returns the default identifier for Customer
	 */
	@Transient
	public String getIdString() {
		String result = "";
		if ( this.getName() != null ) {
			result = result + this.getName();
		}
		return result;
	}
	
	/** Implements the OCL allInstances operation
	 */
	static public List allInstances() {
		if ( !usesAllInstances ) {
			throw new RuntimeException("allInstances is not implemented for this class. Set usesAllInstances to true, if you want allInstances() implemented.");
		}
		return allInstances;
	}

}