package hibernate.test;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/** @author Hibernate CodeGenerator */
public class B implements Serializable {

    /** identifier field */
    private hibernate.test.APK comp_id;

    /** nullable persistent field */
    private Integer G;

    /** full constructor */
    public B(hibernate.test.APK comp_id, Integer G) {
        this.comp_id = comp_id;
        this.G = G;
    }

    /** default constructor */
    public B() {
    }

    /** minimal constructor */
    public B(hibernate.test.APK comp_id) {
        this.comp_id = comp_id;
    }

    public hibernate.test.APK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(hibernate.test.APK comp_id) {
        this.comp_id = comp_id;
    }

    public Integer getG() {
        return this.G;
    }

    public void setG(Integer G) {
        this.G = G;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( !(other instanceof B) ) return false;
        B castOther = (B) other;
        return new EqualsBuilder()
            .append(this.getComp_id(), castOther.getComp_id())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getComp_id())
            .toHashCode();
    }

}
