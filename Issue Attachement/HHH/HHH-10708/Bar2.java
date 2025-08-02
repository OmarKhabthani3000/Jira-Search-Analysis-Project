package org.hibernate.test.hhh10708_bytecodeissue_wo_orphanremoval;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

@Entity
public class Bar2 {
	private int id;
	private Set<Foo2> foos = new HashSet<>();

	@Id
	@GeneratedValue
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@ManyToMany(fetch = FetchType.LAZY)
	public Set<Foo2> getFoos() {
		return foos;
	}

	public void setFoos(Set<Foo2> foos) {
		this.foos = foos;
	}
}
