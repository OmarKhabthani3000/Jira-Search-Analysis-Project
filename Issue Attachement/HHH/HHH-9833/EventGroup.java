package org.hibernate.sample;

import java.util.Collection;
import java.util.HashSet;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
public class EventGroup {
    private CompositeId id;

    private Collection<Event> events = new HashSet<>();

    public EventGroup() {
    }

    public EventGroup(CompositeId id) {
        this.id = id;
    }

    @EmbeddedId
    public CompositeId getId() {
        return id;
    }

    private void setId(CompositeId id) {
        this.id = id;
    }

    @OneToMany
    @Cascade(CascadeType.ALL)
    @Fetch(FetchMode.SUBSELECT)
    public Collection<Event> getEvents() {
        return events;
    }

    private void setEvents(Collection<Event> events) {
        this.events = events;
    }
}
