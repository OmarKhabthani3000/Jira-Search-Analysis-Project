package org.hibernate.metadata.entities4getPropertyValueTest;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;


@Entity
public class Composition {
	@Id private String id;
	@OneToMany(mappedBy="parent") private List<CompositionItem> items = new ArrayList<CompositionItem>();
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public List<CompositionItem> getItems() {
		return items;
	}
	public void setItems(List<CompositionItem> items) {
		this.items = items;
	}
}