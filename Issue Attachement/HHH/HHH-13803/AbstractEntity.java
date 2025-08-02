package org.raju.yadav.entity;

import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.util.Objects;

@MappedSuperclass
public abstract class AbstractEntity<T extends Serializable> implements Entity<T> {

    private static final long serialVersionUID = 8704766485307356626L;

    protected transient T id;

    public void setId(T id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        @SuppressWarnings("unchecked")
        AbstractEntity<T> other = (AbstractEntity<T>) obj;
        return Objects.equals(this.id, other.id);
    }

}