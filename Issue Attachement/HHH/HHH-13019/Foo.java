package org.hibernate.bugs;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;

import org.hibernate.annotations.AttributeAccessor;

@Entity
public class Foo {
    private Long id;
    private Bar _bar;

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

    @OneToOne
    @PrimaryKeyJoinColumn
    @AttributeAccessor("org.hibernate.bugs.MyAttributeAccessor")
	public Bar getBar() {
		return _bar;
	}

	public void setBar(Bar _bar) {
		this._bar = _bar;
	}
}
