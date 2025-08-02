package org.hibernate.bugs;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Bar {
    private Long id;

    private Bar() {
    }

    public Bar(Long id) {
        this.id = id;
    }

    @Id
    public Long getId() {
        return id;
    }

    private void setId(Long id) {
        this.id = id;
    }
}
