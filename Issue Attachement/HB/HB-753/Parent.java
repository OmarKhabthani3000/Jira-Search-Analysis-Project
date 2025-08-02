/*
 * Created on Feb 26, 2004 at 7:16:12 PM
 * 
 */
package hu.pmmedia.test.hibernate;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Set;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
/**
 * @author f00060
 *  
 */
public class Parent implements Serializable {
	/* composite key fields */
	private String instanz;
	private Calendar beginndatum;
	/* composite key fields */
	/* property */
	private String vorname;
	/* one2many */
	private Set children;
	/**
	 * @return Returns the aktion.
	 */
	public String getVorname() {
		return vorname;
	}
	/**
	 * @param aktion
	 *            The aktion to set.
	 */
	public void setVorname(String aktion) {
		this.vorname = aktion;
	}
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
	 * @return Returns the children.
	 */
	public Set getChildren() {
		return children;
	}
	/**
	 * @param children
	 *            The children to set.
	 */
	public void setChildren(Set children) {
		this.children = children;
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
	public boolean equals(final Object arg0) {
		if (!(arg0 instanceof Parent)) {
			return false;
		}
		if (this == arg0) {
			return true;
		}
		Parent that = (Parent) arg0;
		return new EqualsBuilder().append(this.getInstanz(), that.getInstanz())
				.append(this.getBeginndatum(), that.getBeginndatum())
				.isEquals();
	}
	/**
	 * general contract
	 * 
	 * @return hashcode
	 */
	public int hashCode() {
		return new HashCodeBuilder().append(this.getInstanz()).append(
				this.getBeginndatum()).hashCode();
	}
}
