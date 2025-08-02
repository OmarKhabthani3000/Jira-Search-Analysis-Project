package pkg;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/** @author Hibernate CodeGenerator */
abstract public class AutoA implements Serializable {

    /** identifier field */
    private Long id;

    /** nullable persistent field */
    private byte[] cryptedPassword;

    /** full constructor */
    public AutoA(byte[] cryptedPassword) {
        this.cryptedPassword = cryptedPassword;
    }

    /** default constructor */
    public AutoA() {
    }

    public java.lang.Long getId() {
        return this.id;
    }

    public void setId(java.lang.Long id) {
        this.id = id;
    }

    public byte[] getCryptedPassword() {
        return this.cryptedPassword;
    }

    public void setCryptedPassword(byte[] cryptedPassword) {
        this.cryptedPassword = cryptedPassword;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("id", getId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( !(other instanceof AutoA) ) return false;
        AutoA castOther = (AutoA) other;
        return new EqualsBuilder()
            .append(this.getId(), castOther.getId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getId())
            .toHashCode();
    }

}
