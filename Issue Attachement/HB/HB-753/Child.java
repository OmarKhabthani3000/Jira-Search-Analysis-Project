/*
 * Created on Feb 26, 2004 at 7:16:18 PM
 * 
 */
package hu.pmmedia.test.hibernate;
import java.io.Serializable;
import java.util.Calendar;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
/**
 * @author f00060
 *  
 */
public class Child implements Serializable {
	/* composite key fields */
	private String instanz;
	private Calendar beginndatum;
	private Calendar erfassungsbeginn;

	/* composite key fields */
	
	/* property */
	private String name;
	/**
	 * @return Returns the beginndatum.
	 */
	public Calendar getBeginndatum() {
		return beginndatum;
	}
	/**
	 * @param beginndatum
	 *            The beginndatum to set.
	 */
	public void setBeginndatum(Calendar beginndatum) {
		this.beginndatum = beginndatum;
	}
	/**
	 * @return Returns the erfassungsbeginn.
	 */
	public Calendar getErfassungsbeginn() {
		return erfassungsbeginn;
	}
	/**
	 * @param erfassungsbeginn
	 *            The erfassungsbeginn to set.
	 */
	public void setErfassungsbeginn(Calendar erfassungsbeginn) {
		this.erfassungsbeginn = erfassungsbeginn;
	}
	/**
	 * @return Returns the instanz.
	 */
	public String getInstanz() {
		return instanz;
	}
	/**
	 * @param instanz
	 *            The instanz to set.
	 */
	public void setInstanz(String instanz) {
		this.instanz = instanz;
	}
	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name
	 *            The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	public boolean equals(final Object arg0) {
		if (!(arg0 instanceof Child)) {
			return false;
		}
		if (this == arg0) {
			return true;
		}
		Child that = (Child) arg0;
		return new EqualsBuilder().append(this.getInstanz(), that.getInstanz())
				.append(this.getBeginndatum(), that.getBeginndatum()).append(
						this.getErfassungsbeginn(), that.getErfassungsbeginn())
				.isEquals();
	}
	/**
	 * general contract
	 * 
	 * @return hashcode
	 */
	public int hashCode() {
		return new HashCodeBuilder().append(this.getInstanz()).append(
				this.getBeginndatum()).append(this.getErfassungsbeginn())
				.hashCode();
	}
}
