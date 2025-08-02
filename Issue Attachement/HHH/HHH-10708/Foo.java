package org.hibernate.test.hhh10708_bytecodeissue;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

@Entity
public class Foo {
	private int id;
	private Set<Bar> bar = new HashSet<>();

	@Id
	@GeneratedValue
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@OneToMany(orphanRemoval = true, mappedBy = Bar.FOO)
	@Cascade(CascadeType.ALL)
	public Set<Bar> getBar() {
		return bar;

	}

	public void setBar(Set<Bar> bar) {
		this.bar = bar;
	}

}
