package hibernate;

import static com.google.common.base.Preconditions.*;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.google.common.base.Objects;

@Entity
public class SimpleState {

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private final Date created;

    @Column(nullable = false)
    private final Integer state;

    /**
     * Nullable identifier property for Hibernate.
     */
    @Id
    @GeneratedValue
    private Integer id;

    /**
     * No-argument constructor with package visibility is used by Hibernate.
     */
    SimpleState() {
        // Hibernate will set final fields properly.
        created = null;
        state = null;
    }

    public SimpleState(Integer s, Date c) {
        state = checkNotNull(s, "Given Integer must not be null.");
        created = checkNotNull(c, "Given Date must not be null.");
    }

    @Override
    // method is explicitly final - when using Hibernate, it is not recommended to make an entity class final
    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof SimpleState)) {
            return false;
        }
        SimpleState other = (SimpleState) obj;
        // we do not use fields to ensure compatibility with lazy loading
        if (!Objects.equal(getCreated(), other.getCreated())) {
            System.out.println(getCreated().getClass() + " != " + other.getCreated().getClass());
            System.out.println(getCreated() + " != " + other.getCreated());
            System.out.println(getCreated().getTime() + " != " + other.getCreated().getTime());
            return false;
        }
        if (!Objects.equal(getState(), other.getState())) {
            return false;
        }
        return true;
    }

    public Date getCreated() {
        return created;
    }

    public Integer getState() {
        return state;
    }

    @Override
    // method is explicitly final - when using Hibernate, it is not recommended to make an entity class final
    public final int hashCode() {
        // we do not use fields to ensure compatibility with lazy loading
        return Objects.hashCode(getCreated(), getState());
    }

    @Override
    public String toString() {
        // we do not use fields to ensure compatibility with lazy loading
        return String.format("SimpleState [id=%s, state=%s, created=%s]", id, getState(), getCreated());
    }

}
