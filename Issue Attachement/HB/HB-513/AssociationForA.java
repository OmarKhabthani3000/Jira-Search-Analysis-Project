/*
 * Created on Nov 12, 2003
 *
 */
package gatt.example;

import java.util.Date;

/**
 * @hibernate.component
 * 
 * @author Gatt
 *
 */
public class AssociationForA {

	protected Date date;
	protected EntityA reverseAssociation;
	
	/**
	 * @hibernate.property
	 * 
	 * @return Returns the date.
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * @param date The date to set.
	 */
	public void setDate(Date date) {
		this.date = date;
	}
	
	/**
	 * @hibernate.many-to-one column="association_id"
	 * 						  not-null="true"
	 *                        cascade="save-update"
	 * 
	 * @return Returns the associated instance.
	 */
	public EntityA getReverseAssociation() {
		return reverseAssociation;
	}
	
	/**
	 * @param attackedBy The attackedBy to set.
	 */
	public void setReverseAssociation(EntityA reverseAssociation) {
		this.reverseAssociation = reverseAssociation;
	}
}
