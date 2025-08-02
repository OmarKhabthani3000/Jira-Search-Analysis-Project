package org.hibernate.sample;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

@Entity
public class Event {
    private CompositeId id;

    public Event() {
    }

    public Event(CompositeId id) {
        this.id = id;
    }

    @EmbeddedId
    public CompositeId getId() {
        return id;
    }

    private void setId(CompositeId id) {
        this.id = id;
    }
}
