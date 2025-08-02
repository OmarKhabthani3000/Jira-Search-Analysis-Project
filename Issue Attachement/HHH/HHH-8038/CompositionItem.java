package org.hibernate.metadata.entities4getPropertyValueTest;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class CompositionItem {
	@Id private Integer id;
	@ManyToOne private Composition parent;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Composition getParent() {
		return parent;
	}
	public void setParent(Composition parent) {
		this.parent = parent;
	}
}