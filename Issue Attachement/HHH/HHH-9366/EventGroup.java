package org.hibernate.sample;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class EventGroup {
    private Long id;

    private Collection<Event> events = new ArrayList<>();

    public EventGroup() {
    }

    @Id
    @GeneratedValue
    public Long getId() {
        return id;
    }

    private void setId(Long id) {
        this.id = id;
    }

    @OneToMany
    public Collection<Event> getEvents() {
        return events;
    }

    private void setEvents(Collection<Event> events) {
        this.events = events;
    }
}
