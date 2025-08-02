package org.hibernate.envers.bugs;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.hibernate.annotations.AttributeAccessor;
import org.hibernate.envers.Audited;

@Entity
@Audited
public class Foo {
    private Long id;
    private String value;

    private Foo() {
    }

    public Foo(Long id) {
        this.id = id;
    }

    @Id
    public Long getId() {
        return id;
    }

    private void setId(Long id) {
        this.id = id;
    }

    @AttributeAccessor("org.hibernate.envers.bugs.MyAttributeAccessor")
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
