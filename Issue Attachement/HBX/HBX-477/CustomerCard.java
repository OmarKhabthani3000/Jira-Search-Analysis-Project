
package example;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratorType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import utilities.InvariantError;

/** Class ...
 */
/**
 * @author Xinhua
 *
 */
@Entity
public class CustomerCard {
	public Integer f_id = 0;
	public String f_color = "";
	public Customer f_owner;
	static private boolean usesAllInstances = false;
	static private List allInstances = new ArrayList();

	/** Default constructor for CustomerCard
	 */
	public CustomerCard() {
		if ( usesAllInstances ) {
			allInstances.add(this);
		}
	}
	

	public CustomerCard(String Color) {
		super();
		this.setColor(Color);
		if ( usesAllInstances ) {
			allInstances.add(this);
		}
	}
	/** Constructor for CustomerCard
	 * 
	 * @param id 
	 * @param Color 
	 */
	public CustomerCard(Integer id, String Color) {
		super();
		this.setId(id);
		this.setColor(Color);
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
	
	/** Implements the getter for feature '+ Color : Integer'
	 */
	public String getColor() {
		return f_color;
	}
	
	/** Implements the setter for feature '+ Color : Integer'
	 * 
	 * @param element 
	 */
	public void setColor(String element) {
		if ( f_color != element ) {
			f_color = element;
		}
	}
	
	/** Implements the setter of association end owner
	 * 
	 * @param element 
	 */
	public void setOwner(Customer element) {
			this.f_owner = element;
	}
	
	/** Implements the getter for owner
	 */
	@ManyToOne(targetEntity=Customer.class, cascade=CascadeType.ALL , fetch = FetchType.EAGER)
	public Customer getOwner() {
		return f_owner;
	}
	
	/** Should NOT be used by clients! Implements the correct setting of the link for + owner : Customer 
						when a single element is added to it.
	 * 
	 * @param element 
	 */
	public void z_internalAddToOwner(Customer element) {
		this.f_owner = element;
	}
	
	/** Should NOT be used by clients! Implements the correct setting of the link for + owner : Customer 
						when a single element is removed to it.
	 * 
	 * @param element 
	 */
	public void z_internalRemoveFromOwner(Customer element) {
		this.f_owner = null;
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
		if ( getOwner() == null ) {
			String message = "Mandatory feature 'owner' in object '";
			message = message + this.getIdString();
			message = message + "' of type '" + this.getClass().getName() + "' has no value.";
			result.add(new InvariantError(this, message));
		}
		return result;
	}
	
	/** Returns the default identifier for CustomerCard
	 */
	@Transient 
	public String getIdString() {
		String result = "";
		result = result + this.getId();
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