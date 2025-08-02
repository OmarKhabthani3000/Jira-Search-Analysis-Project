package org.hibernate.bugs;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.hibernate.annotations.AttributeAccessor;

@Entity
public class Foo {
    private Long id;

    private Foo() {
    }

    public Foo(Long id) {
        this.id = id;
    }

    @Id
    @AttributeAccessor("org.hibernate.bugs.MyAttributeAccessor")
    public Long getId() {
        return id;
    }

    private void setId(Long id) {
        this.id = id;
    }
}
